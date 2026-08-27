# Prompt: Migración de TouristChain a Arquitectura Hexagonal (Ports & Adapters)

## Contexto del proyecto

TouristChain es una plataforma de turismo (Blockchain + IA) construida como
microservicios Java 21 / Spring Boot 3.3.5, con MySQL como base de datos y un
frontend PHP/JS separado (no lo toques). El backend está organizado en
`backend/<modulo>`, con nombres temáticos de ciudades. Cada módulo es (o debería
ser) un proyecto Maven independiente:

- **ginebra** — Identidad: usuarios, proveedores, autenticación.
- **kyoto** — Catálogo: destinos y servicios turísticos.
- **roma** — Reservas (booking) vía smart contract.
- **athenas** — Reputación: reseñas y ratings.
- **newyork** — Pagos en escrow.

Todos siguen hoy el mismo patrón de capas "tradicional" de Spring:
`Controller (@RestController) → Service (@Service, @Transactional) → Repository
(Spring Data JPA)`, con las entidades `@Entity` usadas directamente como modelo
de dominio y, en varios casos, devueltas tal cual desde los servicios sin pasar
por un DTO.

## Objetivo

Migrar **todos los módulos backend** a una arquitectura hexagonal (ports &
adapters), módulo por módulo, preservando el comportamiento funcional y los
contratos REST existentes (mismos endpoints, mismos paths, mismos campos JSON
de entrada/salida). No es una reescritura funcional: es una reorganización de
capas y responsabilidades.

## Estructura de paquetes objetivo (aplicar en cada módulo)

```
com.touristchain.backend.<modulo>
├── domain
│   ├── model           # Objetos de dominio puros (sin @Entity, sin @Service, sin imports de Spring/JPA)
│   ├── port
│   │   ├── in           # Interfaces de casos de uso (p.ej. CreateBookingUseCase)
│   │   └── out           # Interfaces que el dominio necesita del exterior (p.ej. BookingRepositoryPort, BlockchainPort)
│   └── exception        # Excepciones de dominio específicas (no RuntimeException genérica)
├── application
│   └── service          # Implementan los casos de uso (puertos "in"), orquestan el dominio, dependen solo de puertos "out"
└── infrastructure
    ├── adapter
    │   ├── in
    │   │   └── web        # @RestController, DTOs de request/response, mappers domain↔DTO, @ControllerAdvice
    │   └── out
    │       ├── persistence  # @Entity JPA, Spring Data repositories, adapter que implementa el puerto "out" mapeando entidad↔dominio
    │       └── blockchain    # Adapters que implementan los puertos de blockchain (stub por ahora si no hay integración real)
    └── config             # Configuración Spring (seguridad, beans, etc.)
```

Reglas de dependencia (regla de la arquitectura hexagonal, no negociable):

- `domain` no importa nada de Spring, JPA, Jackson ni de `infrastructure`. Es Java puro.
- `application` depende de `domain` (implementa los puertos "in", usa los puertos "out" como interfaces). No conoce JPA ni HTTP.
- `infrastructure` depende de `domain` y `application`, nunca al revés.
- Los controllers **nunca** llaman directo a un repositorio ni manipulan entidades JPA; llaman a un caso de uso (puerto "in").

## Trabajo por módulo (basado en el estado real del código)

### Ginebra (`backend/ginebra`)
- `AuthService` y `UserService`/`ProviderService` actuales pasan a `application.service`, implementando casos de uso como `LoginUseCase`, `RegisterUserUseCase`, `GetUserUseCase`, etc.
- `User`, `Provider` (hoy `@Entity` en `models/entities`) se separan en: modelo de dominio puro (`domain/model`) + entidad JPA (`infrastructure/adapter/out/persistence`), con un mapper entre ambos.
- `UserRepository`/`ProviderRepository` (Spring Data) se convierten en el adapter de salida; define el puerto `UserRepositoryPort`/`ProviderRepositoryPort` en `domain/port/out`.
- **Bug a resolver como parte de la migración**: `BlockchainAuthService` es una clase vacía (sin cuerpo, sin métodos). Decide y documenta: o se materializa como un puerto real `BlockchainVerificationPort` con un adapter stub, o se elimina si no tiene uso real — no la dejes vacía.
- El login genera un token con `"TOKEN-" + id + "-" + timestamp"` a pesar de que `application.properties` ya trae `app.jwt.secret` / `app.jwt.expiration` configurados. Aprovecha la migración para introducir un puerto `TokenPort` (o similar) y una implementación JWT real detrás de él — no es obligatorio implementar JWT completo si no es el foco, pero el puerto debe quedar bien definido para poder hacerlo después.
- El endpoint `/stats` de `UserController` devuelve una clase anónima `new Object(){...}`. Reemplázalo por un DTO/record explícito en `adapter/in/web`.

### Kyoto (`backend/kyoto`)
- `DestinationService`/`DestinationServiceImpl` y sus métodos (`findAll`, `search`, `create`, `activate`, etc.) se dividen en casos de uso concretos en `application.service`, cada uno implementando una interfaz de `domain/port/in`.
- `Destination` (entidad) se separa en modelo de dominio + entidad JPA, igual que en Ginebra.
- Los DTO existentes (`DestinationRequest`, `DestinationResponse`, records) ya están razonablemente bien aislados — consérvalos como los objetos de la capa `adapter/in/web`, pero asegúrate de que el mapeo dominio↔DTO viva en esa capa, no en el "service".
- El comentario en el código (`"Aquí podrías validar providerId con Ginebra"`) es una pista de que este módulo necesita comunicarse con Ginebra: modélalo como un puerto de salida (`ProviderValidationPort`) con un adapter (HTTP client o stub) en vez de dejarlo como TODO suelto.
- Hay métodos duplicados/parcialmente redundantes (dos variantes de `findWithFilters`, una de ellas con comentario `// Temporal`). Al mover la lógica a casos de uso, consolida esto en un único caso de uso de búsqueda con criterios, sin duplicar código.

### Roma (`backend/roma`)
- `BookingServices` mezcla validación de negocio (fechas, huéspedes, precio, disponibilidad), transición de estados (`PENDING`→`CONFIRMED`→`COMPLETED`/`CANCELLED`) y persistencia en una sola clase `@Service`. Divide en casos de uso: `CreateBookingUseCase`, `ConfirmBookingUseCase`, `CancelBookingUseCase`, `CompleteBookingUseCase`, consultas de lectura, etc.
- El estado de la reserva se maneja hoy como `String` (`"PENDING"`, `"CONFIRMED"`, ...). Al mover esto al dominio, conviértelo en un enum de dominio (`BookingStatus`) — mejora natural y de bajo riesgo al reestructurar.
- `BookingModel` se devuelve directamente desde el servicio (sin DTO de por medio). En la nueva capa `adapter/in/web`, introduce `BookingRequest`/`BookingResponse` como en Kyoto.
- El módulo referencia `contractAddress`/`transactionHash` (confirmación vía smart contract). Modélalo con un puerto de salida (`SmartContractPort` o similar), aunque hoy no haya integración real, para dejar el punto de extensión claro.

### Athenas (`backend/athenas`)
- **Bug a corregir obligatoriamente durante la migración**: `ReviewServices.java` declara `package main.java.com.touristchain.backend.athenas.services;` — el paquete real según la ruta del archivo debería ser `com.touristchain.backend.athenas.services`. Verifica si esto compila hoy tal cual (probablemente el proyecto tiene un problema de estructura de fuente) y corrígelo al mover el código a los nuevos paquetes.
- `ReviewServices` expone directamente las entidades `Review`, `ReputationScore`, `DestinationRating` (de `ReviewModels`) en vez de DTOs — introduce el modelo de dominio y DTOs de respuesta.
- La lógica de recálculo de reputación (`updateProviderReputation`, `updateDestinationRating`, `updateProviderResponseRate`) es lógica de dominio pura (cálculo de promedios/contadores) — debe vivir en el dominio (p.ej. como método de un agregado `ReputationScore` o un domain service), no en la capa de aplicación mezclada con llamadas a repositorio.
- No tiene `README.md` (a diferencia de ginebra/kyoto/roma) — agrégalo documentando la nueva estructura hexagonal como parte del PR de este módulo.

### New York (`backend/newyork`)
- **Prerrequisito antes de migrar**: este módulo no tiene `pom.xml`, así que hoy no es un proyecto Maven compilable de forma independiente. Créalo alineado con los otros módulos (Spring Boot 3.3.5, Java 21, mismas dependencias base: web, data-jpa, validation) antes de aplicar la reestructuración hexagonal. Sin esto, el módulo no compila y no se puede validar el resto del trabajo.
- `PaymentServices` mezcla validación, máquina de estados del escrow (`PENDING → DEPOSITED → RELEASED/REFUNDED/CANCELLED`) y persistencia — mismo tratamiento que Roma: casos de uso separados, estado como enum de dominio, DTOs en la capa web.
- Introduce un puerto de salida para las verificaciones de duplicado de transacción on-chain (`existsByTransactionHash`) si tiene sentido moverlo detrás de un `BlockchainPort` en vez de ser una simple consulta de repositorio.

## Cosas que NO debes hacer / fuera de alcance

- No cambies los paths ni el contrato JSON de los endpoints REST existentes (los controllers deben seguir respondiendo igual desde fuera).
- No toques `public/` (frontend PHP/JS) ni `database/touristchaindb.sql`.
- No implementes integración real con Web3/blockchain si no existe ya — solo deja los puertos bien definidos con un adapter stub/mock donde corresponda.
- No arregles el tema de Flyway (dependencia faltante, carpetas `db/migrations` inexistentes) como parte de este trabajo — es un problema aparte, ya identificado, que se resolverá en otro momento. Si te bloquea para compilar/levantar el módulo, repórtalo, no lo soluciones por tu cuenta.
- No agregues frameworks o librerías nuevas (mappers automáticos tipo MapStruct, etc.) salvo que sea estrictamente necesario — mappers manuales simples entre dominio/entidad/DTO son preferibles en este punto del proyecto.

## Orden de trabajo sugerido

1. Ginebra (es la base de identidad que los demás módulos asumen).
2. Kyoto.
3. Roma.
4. Athenas.
5. New York (empezando por crear su `pom.xml`).

Trabaja **un módulo completo por vez**, con su propio commit/PR, dejando cada
módulo compilando y con sus tests pasando antes de pasar al siguiente.

## Criterios de aceptación por módulo

- El módulo compila (`mvn clean verify` o equivalente) sin errores.
- Los `domain/model` y `application/service` no tienen ninguna dependencia de `org.springframework.*` ni `jakarta.persistence.*`.
- Cada caso de uso relevante tiene al menos un test unitario que instancie el `application.service` directamente (sin contexto de Spring) usando un fake/mock del puerto de salida.
- Los endpoints REST documentados (o inferidos del `@RequestMapping` actual) responden igual que antes — si existen tests de integración previos, deben seguir pasando; si no existen, agrega al menos un test de integración mínimo por controller.
- README del módulo actualizado (o creado, en el caso de Athenas) explicando la nueva estructura de carpetas.
