# 🇨🇭 GINEBRA: Módulo de Identidad Propietaria

**Ginebra** es el pilar de seguridad y privacidad de TouristChain. Este módulo implementa la lógica de **Identidad Descentralizada (DID)**, permitiendo a los turistas y proveedores registrar una identidad verificada y soberana, **sin depender de una autoridad centralizada**.

Su función principal es establecer la confianza en el ecosistema, asegurando que cada actor sea quien dice ser antes de interactuar con los *Smart Contracts*.

---

## 🛡️ Propósito

El módulo `Ginebra` resuelve el problema de la **verificación de identidad centralizada** y la **propiedad de datos**.

* **Identificación Segura:** Asigna una clave única y verificable a cada usuario (Proveedor, Turista, Autoridad).
* **Privacidad:** La información sensible se almacena de forma *off-chain* o cifrada, mientras que la prueba de la identidad reside *on-chain*.
* **Interoperabilidad:** Establece un estándar de identidad que puede ser utilizado por otros módulos, como `Atenas` (Reputación) y `Roma` (Contratación).

---

## 🛠️ Stack y Asignación de Tareas

El módulo `Ginebra` utiliza una combinación del Backend de Spring Boot para la lógica de negocio y la Blockchain para el registro de la prueba de identidad.

| Componente Técnico | Rol en Ginebra | Tecnologías |
| :--- | :--- | :--- |
| **Backend** | **Gestión de la Solicitud:** Maneja la API de registro de perfiles y la lógica de verificación *off-chain* (e.g., KYC simplificado). | Spring Boot (Java) |
| **Blockchain** | **Registro del DID:** Despliegue del *Smart Contract* que registra la clave pública (DID) de la entidad una vez verificada. | [Definir Plataforma] / [Definir Lenguaje] |
| **Database** | Almacenamiento seguro de datos de perfil no sensibles e indicadores de verificación. | MySQL |

---

## 🔗 Funcionalidades Clave (Endpoints de Ejemplo)

El módulo expone las siguientes funcionalidades principales:

### APIs de Spring Boot (Manejadas por el Backend de Ginebra)

| Endpoint | Método | Descripción |
| :--- | :--- | :--- |
| `/api/ginebra/register` | `POST` | Inicia el proceso de registro y verificación del nuevo usuario (Turista/Proveedor). |
| `/api/ginebra/profile/{id}` | `GET` | Recupera el perfil *off-chain* del usuario. |
| `/api/ginebra/verify` | `POST` | Endpoint interno para la verificación de credenciales de otros módulos. |

### Smart Contract (Identidad)

| Función | Descripción |
| :--- | :--- |
| `registerDID(address userAddress)` | Registra la clave pública del usuario como un DID en el registro inmutable. |
| `getRole(address userAddress)` | Consulta si la dirección es un **Proveedor** o **Turista** registrado y verificado. |

---

## 🚧 Desarrollo y Estado Actual

* **Estado en v1.0.0:** Implementado el registro inicial de usuarios (`/api/ginebra/register`) y la lógica de autenticación básica.
* **Próximo Hito (v1.4.0):** Implementación de la función de revocación de identidad (si un proveedor viola términos de servicio).