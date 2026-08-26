# 🇯🇵 KYOTO: Módulo de Motor de Oferta Global

**Kyoto** es el módulo central del *marketplace* de TouristChain. Su responsabilidad principal es gestionar el **Catálogo de Servicios Turísticos** (alojamiento, tours, actividades, etc.), asegurando que la oferta sea presentada, curada y accesible para la búsqueda global.

Funciona como la capa de acceso a los datos de los servicios antes de que se inicie un acuerdo de reserva con el módulo `Roma`.

---

## 🛍️ Propósito

El módulo `Kyoto` garantiza que los turistas encuentren **información precisa y curada** y que los proveedores tengan una plataforma efectiva para exponer sus productos.

* **Gestión del Catálogo:** Permite a los proveedores crear, actualizar y eliminar sus ofertas (*CRUD de servicios*).
* **Búsqueda Eficiente:** Proporciona *endpoints* de alta velocidad para buscar y filtrar servicios por destino, categoría, precio, y disponibilidad.
* **Integración:** Sirve como fuente de datos para el módulo `San Francisco` (IA) para mejorar las recomendaciones.

---

## 🛠️ Stack y Asignación de Tareas

El módulo `Kyoto` reside principalmente en el Backend de Spring Boot para manejar grandes volúmenes de consultas y lógica de filtrado.

| Componente Técnico | Rol en Kyoto | Tecnologías |
| :--- | :--- | :--- |
| **Backend** | **Lógica de Catálogo:** Maneja los controladores para el *CRUD* de servicios, la indexación y el motor de búsqueda. | Spring Boot (Java) |
| **Database** | **Almacenamiento de Servicios:** Almacena la descripción, imágenes, categorías, y metadatos de los servicios. | MySQL |
| **Integración** | Se comunica con `Ginebra` para verificar la identidad del proveedor antes de permitir la publicación de un servicio. | APIs de Spring Boot |

---

## 🔗 Funcionalidades Clave (Endpoints de Ejemplo)

El módulo expone las siguientes funcionalidades principales para la interacción del mercado:

### APIs de Spring Boot (Manejadas por el Backend de Kyoto)

| Endpoint | Método | Descripción |
| :--- | :--- | :--- |
| `/api/kyoto/services` | `POST` | Permite a un proveedor verificado publicar un nuevo servicio. |
| `/api/kyoto/services/search` | `GET` | **Motor de Búsqueda:** Busca servicios turísticos con filtros avanzados (destino, fechas, categoría). |
| `/api/kyoto/services/{id}` | `GET` | Recupera los detalles completos de un servicio específico. |
| `/api/kyoto/services/{id}` | `PUT/DELETE` | Permite al proveedor editar o eliminar su servicio listado. |

---

## 🚧 Desarrollo y Estado Actual

* **Estado en v1.1.0:** El *CRUD* de servicios está completamente implementado y es funcional.
* **Próximo Hito (v1.3.0):** Integración completa de datos en tiempo real con el módulo `Tokio` (Inventario Dinámico) para reflejar la disponibilidad precisa en la búsqueda.