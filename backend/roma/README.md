# 🇮🇹 ROMA: Módulo de Acuerdo de Reserva Smart

**Roma** actúa como el notario digital de TouristChain. Este módulo es el encargado de **crear y gestionar el Contrato Inteligente de Reserva** entre el Turista y el Proveedor, asegurando que los términos del acuerdo (precio, fechas, servicio) queden registrados de forma inmutable.

Su principal función es ser el **precursor del pago**, iniciando el proceso de *Escrow* que será administrado por el módulo `Nueva York`.

---

## 🏛️ Propósito

El módulo `Roma` es fundamental para la seguridad contractual y la automatización de la contratación.

* **Creación de Contrato:** Genera dinámicamente el Smart Contract con las condiciones específicas de la reserva.
* **Trazabilidad:** Proporciona un registro inmutable del acuerdo en la Blockchain.
* **Interacción Directa:** Coordina las peticiones de reserva entre el Frontend y los Contratos Inteligentes.

---

## 🛠️ Stack y Asignación de Tareas

El módulo `Roma` es un híbrido que requiere lógica de negocio en el Backend de Spring Boot y la interacción directa con la capa Blockchain.

| Componente Técnico | Rol en Roma | Tecnologías |
| :--- | :--- | :--- |
| **Backend** | **Orquestación y Pre-cálculos:** Recibe la solicitud del cliente, verifica la disponibilidad (con `Tokio`), y prepara los datos para la transacción *on-chain*. | Spring Boot (Java) |
| **Blockchain** | **Contrato `AcuerdoDeReserva`:** El Smart Contract que codifica las reglas de la reserva (fechas, precio) y hace el enlace al módulo `Nueva York` (Escrow). | [Definir Plataforma] / [Definir Lenguaje] |
| **Integración** | Clase o librería de Java (`Web3j` o similar) utilizada por Spring Boot para interactuar con el Contrato Inteligente. | Java (Web3j) |

---

## 🔗 Funcionalidades Clave (Workflow de Reserva)

El flujo se inicia con una petición al Backend, que luego interactúa con la Blockchain:

### APIs de Spring Boot (Manejadas por el Backend de Roma)

| Endpoint | Método | Descripción |
| :--- | :--- | :--- |
| `/api/roma/contract/init` | `POST` | Recibe los detalles finales de la reserva y **despliega/inicia** el nuevo Smart Contract. |
| `/api/roma/contract/{id}` | `GET` | Consulta el estado actual del Contrato Inteligente de reserva (`Pendiente`, `Confirmado`, `Cancelado`). |
| `/api/roma/action/cancel` | `POST` | Inicia la lógica de cancelación y las reglas de reembolso definidas en el contrato. |

### Smart Contract (`AcuerdoDeReserva`)

| Función | Descripción |
| :--- | :--- |
| `createAgreement(address provider, uint price, uint startDate)` | Función que establece las condiciones del acuerdo y llama al módulo `Nueva York` para depositar fondos. |
| `cancelAgreement(address caller)` | Ejecuta las reglas de penalización o reembolso si se cancela la reserva. |

---

## 🚧 Desarrollo y Estado Actual

* **Estado en v1.1.0:** El Smart Contract está desplegado en Testnet. La API `/api/roma/contract/init` es funcional y logra crear el acuerdo básico.
* **Próximo Hito (v1.2.0):** Integración completa con el módulo `Nueva York` para la transferencia de fondos al *Escrow* inmediatamente después de la creación del acuerdo.