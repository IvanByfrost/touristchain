package com.touristchain.rome.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touristchain.common.NotFoundException;
import com.touristchain.geneva.security.UserPrincipal;
import com.touristchain.rome.payment.PaymentDtos.PaymentRequest;
import com.touristchain.rome.payment.PaymentDtos.PaymentStats;
import com.touristchain.rome.payment.PaymentDtos.PaymentStatus;
import com.touristchain.rome.payment.PaymentDtos.PaymentView;
import com.touristchain.rome.payment.PaymentDtos.QrPaymentRequest;
import com.touristchain.rome.payment.PaymentDtos.QrPaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public List<PaymentView> list(@AuthenticationPrincipal UserPrincipal principal) {
        return paymentService.list(principal.getUserId(), isAdmin(principal));
    }

    @PostMapping
    public ResponseEntity<PaymentView> pay(@AuthenticationPrincipal UserPrincipal principal,
                                           @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.pay(principal.getUserId(), request.method(), request.description()));
    }

    @PostMapping("/qr")
    public ResponseEntity<QrPaymentResponse> createQr(@AuthenticationPrincipal UserPrincipal principal,
                                                      @Valid @RequestBody(required = false) QrPaymentRequest request)
            throws JsonProcessingException {
        PaymentView payment = paymentService.createQr(principal.getUserId(), request == null ? null : request.description());
        String confirmUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/payments/qr/{token}/confirm")
                .buildAndExpand(payment.qrToken())
                .toUriString();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("touristchain", true);
        payload.put("paymentId", payment.id());
        payload.put("reference", payment.reference());
        payload.put("amount", payment.amount());
        payload.put("merchant", "TouristChain");
        payload.put("url", confirmUrl);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new QrPaymentResponse(payment, objectMapper.writeValueAsString(payload), confirmUrl));
    }

    @GetMapping("/{paymentId}/status")
    public PaymentStatus status(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID paymentId) {
        return paymentService.status(principal.getUserId(), isAdmin(principal), paymentId);
    }

    // ========== Custodia (escrow): solo administradores (ver SecurityConfig) ==========

    @GetMapping("/escrow")
    public List<PaymentView> held() {
        return paymentService.held();
    }

    @PatchMapping("/{paymentId}/release")
    public PaymentView release(@PathVariable UUID paymentId) {
        return paymentService.release(paymentId);
    }

    @PatchMapping("/{paymentId}/refund")
    public PaymentView refund(@PathVariable UUID paymentId) {
        return paymentService.refund(paymentId);
    }

    @GetMapping("/stats")
    public PaymentStats stats() {
        return paymentService.stats();
    }

    /** Rechaza los pagos QR abandonados (pendientes hace más de {@code hours} horas). */
    @PostMapping("/expired/reject")
    public List<PaymentView> rejectExpired(@RequestParam(defaultValue = "24") int hours) {
        return paymentService.rejectExpired(hours);
    }

    /** Pública: es la URL que lleva el QR y simula la pasarela de pago. Responde una página HTML. */
    @GetMapping(value = "/qr/{token}/confirm", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> confirmQr(@PathVariable String token) {
        try {
            PaymentEntity payment = paymentService.confirmQr(token);
            if (!PaymentEntity.COMPLETED.equals(payment.getStatus())) {
                return ResponseEntity.ok(page("Pago no disponible",
                        "Este pago fue reemplazado por otro y ya no se puede completar.", false));
            }
            return ResponseEntity.ok(page("¡Pago exitoso!",
                    "Se confirmó el pago " + payment.getReference() + " por $" + payment.getAmount().stripTrailingZeros().toPlainString() + ".",
                    true));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(page("Pago no encontrado",
                    "El código QR escaneado no corresponde a ninguna transacción válida.", false));
        }
    }

    private static String page(String title, String message, boolean success) {
        String color = success ? "#28a745" : "#dc3545";
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
                <style>body{font-family:system-ui,sans-serif;text-align:center;padding:3rem 1rem;background:#f8f9fa;}</style>
                </head>
                <body><h1 style="color:%s;">%s</h1><p>%s</p><p>Ya puedes volver a TouristChain.</p></body>
                </html>
                """.formatted(title, color, title, message);
    }

    private static boolean isAdmin(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
