# 🇺🇸 NUEVA YORK: Módulo de Caja de Seguridad Escrow

**Nueva York** es el **centro financiero descentralizado** de TouristChain. Su función es garantizar la seguridad económica de todas las reservas, actuando como un **fideicomiso digital (Escrow)** que retiene los fondos del turista hasta que el proveedor haya entregado el servicio exitosamente.

Este módulo elimina la necesidad de intermediarios bancarios, asegurando que los pagos se realicen de manera automática, transparente y sin riesgo de fraude.

---

## 🔒 Propósito

El módulo `Nueva York` es el encargado de la liquidación de pagos y la gestión de disputas básicas a través de reglas inmutables.

* **Retención de Fondos:** Asegura que los fondos sean bloqueados inmediatamente después de que el módulo `Roma` crea el acuerdo.
* **Liquidación Segura:** Libera automáticamente los fondos al proveedor o los devuelve al turista según el *status* final del servicio.
* **Transparencia Financiera:** Todas las transacciones de depósito y liberación quedan registradas en la Blockchain.

---

## 🛠️ Stack y Asignación de Tareas

**Nueva York** es predominantemente un módulo de **Blockchain**, ya que la lógica de retención y liberación debe ser inmutable y estar fuera del control del servidor central.

| Componente Técnico | Rol en Nueva York | Tecnologías |
| :--- | :--- | :--- |
| **Blockchain** | **Contrato `EscrowSystem`:** El Smart Contract principal que contiene el saldo de los depósitos y ejecuta la lógica de liberación/reembolso. | [Definir Plataforma] / [Definir Lenguaje] |
| **Backend** | **Monitoreo y Notificación:** El servicio de Spring Boot monitorea los eventos del Smart Contract (depósitos, liberaciones) para actualizar el estado *off-chain* y notificar a los usuarios. | Spring Boot (Java) / Web3j |
| **Integración** | Interactúa con `Roma` para recibir la instrucción de depósito y con `Atenas` para verificar la reputación en caso de disputa. | APIs de Contratos Inteligentes |

---

## 🔗 Funcionalidades Clave (Smart Contract)

El módulo opera a través de las funciones públicas del Smart Contract:

### Smart Contract (`EscrowSystem`)

| Función | Descripción |
| :--- | :--- |
| `deposit(address agreementAddress, uint amount)` | **Fase de Depósito:** Recibe los fondos del turista y los bloquea, vinculados al Contrato de `Roma` (`agreementAddress`). |
| `releaseToProvider(address agreementAddress)` | **Fase de Liberación:** Desbloquea los fondos y los transfiere al proveedor, solo después de la confirmación del servicio. |
| `refundToTourist(address agreementAddress)` | **Fase de Reembolso:** Desbloquea los fondos y los devuelve al turista, si la reserva es cancelada bajo términos válidos. |
| `checkBalance(address agreementAddress)` | Consulta el saldo de fondos actualmente retenidos en el *Escrow* para una reserva específica. |

---

## 🚧 Desarrollo y Estado Actual

* **Estado en v1.2.0:** El Contrato `EscrowSystem` está desplegado y las funciones de `deposit` y `refundToTourist` son totalmente operativas.
* **Próximo Hito (v1.3.0):** Optimización del gas de las transacciones y automatización del *listener* de eventos en el Backend para reflejar el estado del Escrow en tiempo real.