package com.touristchain.rome.payment;

import com.touristchain.common.NotFoundException;
import com.touristchain.geneva.users.UserEntity;
import com.touristchain.geneva.users.UserRepository;
import com.touristchain.rome.booking.BookingEntity;
import com.touristchain.rome.booking.BookingService;
import com.touristchain.rome.payment.PaymentDtos.PaymentStats;
import com.touristchain.rome.payment.PaymentDtos.PaymentStatus;
import com.touristchain.rome.payment.PaymentDtos.Summary;
import com.touristchain.rome.payment.PaymentDtos.PaymentView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Pagos simulados: tarjeta, Nequi y PSE se aprueban al instante; QR queda pendiente hasta que
 * se abre su URL de confirmación (que hace de pasarela). El importe siempre es la suma de las
 * reservas pendientes del usuario, calculada aquí y no en el navegador.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PaymentView> list(UUID userId, boolean isAdmin) {
        return toViews(isAdmin
                ? paymentRepository.findAllByOrderByCreatedAtAsc()
                : paymentRepository.findByUserIdOrderByCreatedAtAsc(userId));
    }

    private List<PaymentView> toViews(List<PaymentEntity> payments) {
        Set<UUID> userIds = payments.stream().map(PaymentEntity::getUserId).collect(Collectors.toSet());
        Map<UUID, UserEntity> users = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getUserId, Function.identity()));
        return payments.stream()
                .map(payment -> PaymentView.from(payment, displayName(users.get(payment.getUserId())), false))
                .toList();
    }

    /** Tarjeta, Nequi o PSE: aprobación inmediata y reservas pagadas. */
    @Transactional
    public PaymentView pay(UUID userId, String method, String description) {
        List<BookingEntity> bookings = requirePendingBookings(userId);
        PaymentEntity payment = newPayment(userId, method, bookings, description, "Reserva TouristChain");
        LocalDateTime now = LocalDateTime.now();
        payment.complete(now);
        payment = paymentRepository.save(payment);
        bookingService.confirmPayment(bookings, payment.getPaymentId(), now);
        return PaymentView.from(payment, userName(userId), false);
    }

    /** QR: el pago queda pendiente y las reservas asociadas hasta que se confirme. */
    @Transactional
    public PaymentView createQr(UUID userId, String description) {
        List<BookingEntity> bookings = requirePendingBookings(userId);
        PaymentEntity payment = newPayment(userId, "qr", bookings, description, "Pago con QR - TouristChain");
        payment.setQrToken(HexFormat.of().formatHex(randomBytes(24)));
        payment = paymentRepository.save(payment);
        bookingService.linkToPayment(bookings, payment.getPaymentId());
        return PaymentView.from(payment, userName(userId), true);
    }

    /** Lo que hace la "pasarela" al escanear el QR. Devuelve el pago, ya completado. */
    @Transactional
    public PaymentEntity confirmQr(String token) {
        PaymentEntity payment = paymentRepository.findByQrToken(token)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado"));
        if (PaymentEntity.PENDING.equals(payment.getStatus())) {
            LocalDateTime now = LocalDateTime.now();
            payment.complete(now);
            bookingService.confirmPayment(payment.getPaymentId(), now);
        }
        return payment;
    }

    @Transactional(readOnly = true)
    public PaymentStatus status(UUID userId, boolean isAdmin, UUID paymentId) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado"));
        if (!isAdmin && !payment.getUserId().equals(userId)) {
            throw new SecurityException("Acceso denegado");
        }
        return new PaymentStatus(payment.getPaymentId(), payment.getStatus(), payment.getReference(),
                payment.getEscrowStatus());
    }

    // ========== Custodia (escrow) y mantenimiento: solo administradores ==========

    /** Pagos completados cuyo dinero sigue retenido, el más antiguo primero. */
    @Transactional(readOnly = true)
    public List<PaymentView> held() {
        return toViews(paymentRepository.findByEscrowStatusOrderByPaidAtAsc(PaymentEntity.ESCROW_HELD));
    }

    /** Entrega al socio el dinero retenido. */
    @Transactional
    public PaymentView release(UUID paymentId) {
        PaymentEntity payment = find(paymentId);
        payment.release(LocalDateTime.now());
        return toViews(List.of(payment)).get(0);
    }

    /** Devuelve el dinero retenido al turista y cancela sus reservas (los viajes recuperan los cupos). */
    @Transactional
    public PaymentView refund(UUID paymentId) {
        PaymentEntity payment = find(paymentId);
        payment.refund(LocalDateTime.now());
        bookingService.cancelForRefund(paymentId);
        return toViews(List.of(payment)).get(0);
    }

    @Transactional(readOnly = true)
    public PaymentStats stats() {
        Map<String, Summary> byStatus = summaries(paymentRepository.summarizeByStatus());
        Map<String, Summary> byEscrow = summaries(paymentRepository.summarizeByEscrowStatus());
        long total = byStatus.values().stream().mapToLong(Summary::count).sum();
        return new PaymentStats(total, byStatus, byEscrow);
    }

    /** Rechaza los QR que siguen pendientes después de {@code hours} horas; sus reservas siguen pendientes. */
    @Transactional
    public List<PaymentView> rejectExpired(int hours) {
        if (hours < 1) {
            throw new IllegalArgumentException("Las horas deben ser al menos 1");
        }
        List<PaymentEntity> expired = paymentRepository.findByStatusAndCreatedAtBefore(
                PaymentEntity.PENDING, LocalDateTime.now().minusHours(hours));
        expired.forEach(payment -> payment.setStatus(PaymentEntity.REJECTED));
        return toViews(expired);
    }

    private PaymentEntity find(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado"));
    }

    private static Map<String, Summary> summaries(List<Object[]> rows) {
        Map<String, Summary> result = new TreeMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], new Summary((Long) row[1], ((BigDecimal) row[2]).doubleValue()));
        }
        return result;
    }

    private List<BookingEntity> requirePendingBookings(UUID userId) {
        List<BookingEntity> bookings = bookingService.pendingOf(userId);
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("No tienes reservas pendientes de pago");
        }
        // Un QR abandonado no debe poder cobrar después unas reservas que se pagan ahora por otra vía
        paymentRepository.findByUserIdAndStatus(userId, PaymentEntity.PENDING)
                .forEach(abandoned -> abandoned.setStatus(PaymentEntity.REJECTED));
        return bookings;
    }

    private static PaymentEntity newPayment(UUID userId, String method, List<BookingEntity> bookings,
                                            String description, String defaultDescription) {
        PaymentEntity payment = new PaymentEntity();
        payment.setUserId(userId);
        payment.setMethod(method);
        payment.setAmount(bookings.stream().map(BookingEntity::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        payment.setDescription(description == null || description.isBlank() ? defaultDescription : description.trim());
        payment.setReference("TC-" + Long.toString(System.currentTimeMillis(), 36).toUpperCase(Locale.ROOT)
                + HexFormat.of().withUpperCase().formatHex(randomBytes(2)));
        return payment;
    }

    private String userName(UUID userId) {
        return displayName(userRepository.findById(userId).orElse(null));
    }

    private static String displayName(UserEntity user) {
        if (user == null) {
            return "Usuario";
        }
        return user.getLastName() == null ? user.getFirstName() : user.getFirstName() + " " + user.getLastName();
    }

    private static byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        RANDOM.nextBytes(bytes);
        return bytes;
    }
}
