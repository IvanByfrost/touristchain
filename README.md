# 🌍 TouristChain

**TouristChain** es una plataforma digital integral que revoluciona la industria turística global, utilizando **Blockchain** e **Inteligencia Artificial (IA)** para establecer un ecosistema de viajes seguro, transparente y eficiente.

Eliminamos la falta de confianza, el fraude y los altos costos por intermediación, conectando directamente a turistas, proveedores de servicios y autoridades gubernamentales en un único entorno descentralizado.

## ✅ La Solución: TouristChain

**TouristChain** es la respuesta a estos desafíos, ofreciendo una modernización completa del ecosistema turístico a través de dos pilares tecnológicos:

| Tecnología | Rol en TouristChain | Beneficio Clave |
| :--- | :--- | :--- |
| **Blockchain (Cadena de Bloques)** | Registro inmutable de transacciones, servicios y contratos inteligentes. | **Transparencia y Seguridad.** Garantiza transacciones seguras, elimina el fraude en reservas y permite la trazabilidad completa de cada servicio. |
| **Inteligencia Artificial (IA)** | Motor de personalización, optimización de rutas y automatización de procesos. | **Eficiencia y Personalización.** Mejora las recomendaciones de viaje, optimiza la gestión de reservas para proveedores y personaliza la experiencia del usuario. |

### Beneficios Clave del Ecosistema

* **Conexión Directa:** Comunicación sin fricciones entre turistas y proveedores, **reduciendo drásticamente los costos** de intermediación.
* **Herramientas Regulatorias:** Provisión de *dashboards* y *APIs* a autoridades para el **monitoreo en tiempo real** del cumplimiento normativo y la prevención de fraudes.
* **Confianza Garantizada:** Un sistema de reputación basado en la *blockchain* que asegura la confiabilidad de cada proveedor y servicio.

## 🛠️ 4. Tecnologías Utilizadas

TouristChain se construye sobre una robusta pila tecnológica enfocada en la seguridad del lado del servidor y la simplicidad del lado del cliente.

### Pilares Descentralizados (Blockchain)

| Componente | Descripción |
| :--- | :--- |
| **Plataforma Blockchain:** | [Define aquí la plataforma de Blockchain, ej.: **Polygon** o **Ethereum**] |
| **Lenguaje de Contratos:** | [Define aquí el lenguaje, ej.: **Solidity**] |
| **Conectividad:** | [Define aquí la librería de interacción Web3, ej.: **Web3.js** o **Ethers.js**] |

### Backend y Servicios de IA

El servidor principal de la aplicación y la lógica de negocio se gestionan con tecnología Java robusta:

| Componente | Descripción |
| :--- | :--- |
| **Lenguaje Principal:** | **Java** |
| **Framework Web (API):** | **Spring Boot** (Gestión de Microservicios, Seguridad y APIs RESTful) |
| **Lenguaje IA/Scripting:** | **JavaScript** (Posiblemente para lógica secundaria o utilidades en el servidor/servicios sin Spring) |
| **Base de Datos:** | **MySQL** |

### Frontend (Interfaz de Usuario)

La interfaz de usuario se mantiene ligera y accesible:

| Componente | Descripción |
| :--- | :--- |
| **Markup:** | **HTML5** (Estructura de la aplicación) |
| **Estilos:** | **CSS3** (Diseño y presentación) |
| **Interactividad:** | JavaScript nativo (Para manejar la lógica del cliente y la interacción con la API) |

## ⚙️ 5. Instalación y Puesta en Marcha (Desarrollo Local)

Sigue estos pasos para obtener una copia operativa del proyecto en tu máquina local con fines de desarrollo y prueba.

### Prerrequisitos

* **Java Development Kit (JDK)** (Versión 17 o superior)
* **Apache Maven** o **Gradle** (Para gestión de dependencias y compilación de Spring Boot)
* **Node.js** (Para herramientas de Blockchain/Web3)
* Un IDE compatible con Java (como IntelliJ IDEA o VS Code con extensiones de Java)

### 1. Clonar el Repositorio

```bash
git clone [https://github.com/IvanByfrost/touristchain](https://github.com/IvanByfrost/touristchain)
cd TouristChain

# Navega al directorio de contratos (ej. 'blockchain/')
cd ../blockchain/
npm install
npm run deploy-local # Comando de ejemplo para desplegar

## ▶️ 6. Uso Básico

Esta sección debe describir cómo interactuar con tu plataforma.

```
## ▶️ 6. Uso Básico de TouristChain

La plataforma TouristChain está diseñada para tres actores principales, cada uno con un conjunto de funcionalidades específicas:

### A. Para Turistas (Usuarios Finales)

* **Búsqueda Personalizada:** Utiliza la IA para recibir recomendaciones de destinos y servicios basadas en preferencias y historial.
* **Reservas Seguras:** Realiza reservas de alojamiento y actividades mediante Contratos Inteligentes (Smart Contracts) que aseguran el cumplimiento de las condiciones.
* **Verificación de Identidad:** Usa el sistema descentralizado para verificar la identidad del proveedor antes de realizar cualquier pago.

### B. Para Proveedores (Empresas y Servicios)

* **Gestión Descentralizada:** Utiliza el panel de control para listar servicios, gestionar la disponibilidad y configurar Smart Contracts para pagos.
* **Reputación Inmutable:** Acumula un historial de servicio transparente y verificable en la Blockchain, mejorando su credibilidad.
* **Pagos Instantáneos:** Recibe pagos directamente sin demoras excesivas de intermediarios.

### C. Para Autoridades Gubernamentales

* **Monitoreo en Tiempo Real:** Acceso a *APIs* y *dashboards* para supervisar el flujo de transacciones y la actividad del sector en la Blockchain.
* **Cumplimiento Normativo:** Herramientas para verificar que los proveedores cumplan con las regulaciones locales mediante la información transparente registrada.

## 🏗️ 7. Módulos Funcionales de TouristChain: Arquitectura Temática 🏙️

TouristChain opera a través de una arquitectura funcional dividida en tres pilares, cada uno con nombres temáticos de ciudades globales que reflejan su propósito central.

### 1. 🛍️ Pilares de Mercado y Experiencia de Viaje (Marketplace & UX)

Este grupo se enfoca en la interacción directa entre el turista y la oferta de servicios.

| Módulo (Nombre de Ciudad) | Función Central |
| :--- | :--- |
| **Kyoto** 🇯🇵 | **Motor de Oferta Global:** Gestión, curación y exposición de todo el catálogo de servicios turísticos. |
| **Roma** 🇮🇹 | **Acuerdo de Reserva Smart:** Inicia y gestiona el ciclo de vida del *Smart Contract* de reserva, basándose en la ley del acuerdo. |
| **Tokio** 🇯🇵 | **Inventario Dinámico:** Administración de la disponibilidad y las reglas de precios en tiempo real con alta eficiencia. |

### 2. 🛡️ Pilares de Confianza y Datos (Blockchain & Trust)

Este grupo proporciona la base de seguridad, inmutabilidad y personalización del ecosistema.

| Módulo (Nombre de Ciudad) | Función Central |
| :--- | :--- |
| **Ginebra** 🇨🇭 | **Identidad Propietaria:** Gestión de la identidad descentralizada (*DID*) y soberana de todos los actores de la plataforma. |
| **Nueva York** 🇺🇸 | **Caja de Seguridad Escrow:** Fideicomiso digital que maneja la retención y liberación segura de los pagos (*Escrow*). |
| **Atenas** 🇬🇷 | **Historial de Reputación:** Registro inmutable en la *Blockchain* de las valoraciones, construyendo el legado de servicio de los proveedores. |
| **San Francisco** 💻 | **Asistente Cognitivo de Viajes:** Ejecuta la lógica de IA para generar recomendaciones y personalización de la experiencia. |

### 3. ⚖️ Pilares Regulatorios y de Cumplimiento

Este grupo está diseñado para proporcionar transparencia y herramientas de supervisión a las autoridades gubernamentales.

| Módulo (Nombre de Ciudad) | Función Central |
| :--- | :--- |
| **Londres** 🇬🇧 | **Monitor Transaccional:** Proporciona visibilidad en tiempo real sobre el flujo de Contratos Inteligentes y actividades en la red. |
| **Berlín** 🇩🇪 | **Detector de Riesgos Predictivo:** Aplica modelos de IA para analizar el ecosistema e identificar patrones de fraude o actividad anómala. |
| **Bruselas** 🇧🇪 | **Generador de Cumplimiento:** Herramientas para la generación de informes y auditorías que aseguren la adhesión a las normativas legales. |

## 📜 8. Registro de Cambios (Changelog) - TouristChain

Este registro detalla el progreso y los hitos clave de cada versión. 

### v1.1.0 (2026-04-01) 🔒 Seguridad Financiera y Monitoreo

Hito crucial que activa la protección de los fondos y la transparencia regulatoria en la red de prueba.

* ✅ **Protección de Fondos:** Módulo `Nueva York` (Caja de Seguridad Escrow) completamente funcional, asegurando los pagos mediante *Smart Contracts*.
* ✅ **Supervisión Activa:** Módulo `Londres` (Monitor Transaccional) lanzado, proporcionando la primera interfaz para el monitoreo regulatorio *on-chain*.
* ✅ **Frontend Mejorado:** Sistema de filtrado y búsqueda avanzado implementado para una mejor experiencia de usuario.
* 🚧 **IA en Preparación:** El motor del Módulo `San Francisco` inicia la recolección de datos y el entrenamiento de sus modelos de recomendación.
* ❌ **Módulos Planificados:** `Atenas` y `Tokio` serán el foco de la v1.3.0.

---

### v0.0.0 (2025-10-25) ⚙️ Base de la Arquitectura

Lanzamiento inicial del *framework* y la arquitectura modular que soporta el proyecto.

* ✅ **Arquitectura Estable:** Base del *backend* en Spring Boot (Java) y *frontend* en HTML/CSS/JS.
* ✅ **Identidad Inicial:** Módulo `Ginebra` (Identidad Propietaria) implementado para el registro inicial de usuarios.
* ✅ **Servicios Esenciales:** Autenticación de usuarios y gestión básica de perfiles.
* ✅ **Entorno Configurado:** Base de datos MySQL inicializada y lista para el desarrollo *off-chain*.