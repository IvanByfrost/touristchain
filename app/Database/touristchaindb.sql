-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 18-08-2025 a las 22:35:40
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `touristchaindb`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `administrator`
--

CREATE TABLE `administrator` (
  `Administrator_id` int(11) NOT NULL,
  `MainUser_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `administrator`
--

INSERT INTO `administrator` (`Administrator_id`, `MainUser_id`) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `airport`
--

CREATE TABLE `airport` (
  `Airport_id` int(11) NOT NULL,
  `Place_id` int(11) DEFAULT NULL,
  `Package_id` int(11) DEFAULT NULL,
  `AirportName` varchar(60) DEFAULT NULL,
  `IATACode` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `airport`
--

INSERT INTO `airport` (`Airport_id`, `Place_id`, `Package_id`, `AirportName`, `IATACode`) VALUES
(1, 1, 1, 'El Dorado', 'BOG'),
(2, 2, 2, 'José María Córdova', 'MDE'),
(3, 3, 3, 'Alfonso Bonilla Aragón', 'CLO'),
(4, 4, 4, 'Rafael Núñez', 'CTG'),
(5, 5, 5, 'Simón Bolívar', 'SMR'),
(6, 6, 6, 'Palonegro', 'BGA'),
(7, 7, 7, 'Matecaña', 'PEI'),
(8, 8, 8, 'La Nubia', 'MZL');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `area`
--

CREATE TABLE `area` (
  `Area_id` int(11) NOT NULL,
  `Country_id` int(11) DEFAULT NULL,
  `AreaName` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `area`
--

INSERT INTO `area` (`Area_id`, `Country_id`, `AreaName`) VALUES
(1, 1, 'Cundinamarca'),
(2, 2, 'Antioquia'),
(3, 3, 'Valle del Cauca'),
(4, 4, 'Bolívar'),
(5, 5, 'Magdalena'),
(6, 6, 'Santander'),
(7, 7, 'Risaralda'),
(8, 8, 'Caldas');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `chat`
--

CREATE TABLE `chat` (
  `Chat_id` int(11) NOT NULL,
  `Administrator_id` int(11) DEFAULT NULL,
  `Tourist_id` int(11) DEFAULT NULL,
  `Partner_id` int(11) DEFAULT NULL,
  `Description` varchar(150) DEFAULT NULL,
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `chat`
--

INSERT INTO `chat` (`Chat_id`, `Administrator_id`, `Tourist_id`, `Partner_id`, `Description`, `startTime`, `endTime`) VALUES
(1, 1, 1, 1, 'Consulta sobre viaje', '2025-01-01 09:00:00', '2025-01-01 09:30:00'),
(2, 2, 2, 2, 'Reserva de hotel', '2025-02-01 10:00:00', '2025-02-01 10:30:00'),
(3, 3, 3, 3, 'Información de paquete', '2025-03-01 11:00:00', '2025-03-01 11:30:00'),
(4, 4, 4, 4, 'Soporte de pago', '2025-04-01 12:00:00', '2025-04-01 12:30:00'),
(5, 5, 5, 5, 'Consulta de itinerario', '2025-05-01 13:00:00', '2025-05-01 13:30:00'),
(6, 6, 6, 6, 'Problema con reserva', '2025-06-01 14:00:00', '2025-06-01 14:30:00'),
(7, 7, 7, 7, 'Reembolso solicitado', '2025-07-01 15:00:00', '2025-07-01 15:30:00'),
(8, 8, 8, 8, 'Consulta general', '2025-08-01 16:00:00', '2025-08-01 16:30:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `company`
--

CREATE TABLE `company` (
  `Company_id` int(11) NOT NULL,
  `Partner_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `company`
--

INSERT INTO `company` (`Company_id`, `Partner_id`) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `country`
--

CREATE TABLE `country` (
  `Country_id` int(11) NOT NULL,
  `Trip_id` int(11) DEFAULT NULL,
  `CountryName` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `country`
--

INSERT INTO `country` (`Country_id`, `Trip_id`, `CountryName`) VALUES
(1, 1, 'Colombia'),
(2, 2, 'Colombia'),
(3, 3, 'Colombia'),
(4, 4, 'Colombia'),
(5, 5, 'Colombia'),
(6, 6, 'Colombia'),
(7, 7, 'Colombia'),
(8, 8, 'Colombia');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `hotel`
--

CREATE TABLE `hotel` (
  `Hotel_id` int(11) NOT NULL,
  `HotelName` varchar(60) DEFAULT NULL,
  `PlaceHotel` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `hotel`
--

INSERT INTO `hotel` (`Hotel_id`, `HotelName`, `PlaceHotel`) VALUES
(1, 'Hotel Bogotá Real', 'Bogotá'),
(2, 'Hotel Medellín Plaza', 'Medellín'),
(3, 'Hotel Cartagena Beach', 'Cartagena'),
(4, 'Hotel Cali Central', 'Cali'),
(5, 'Hotel Santa Marta Mar', 'Santa Marta'),
(6, 'Hotel Bucaramanga Suite', 'Bucaramanga'),
(7, 'Hotel Pereira Park', 'Pereira'),
(8, 'Hotel Manizales Nevado', 'Manizales');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mainuser`
--

CREATE TABLE `mainuser` (
  `MainUser_id` int(11) NOT NULL,
  `CredentialType` varchar(2) DEFAULT NULL,
  `CredentialNumber` int(11) DEFAULT NULL,
  `UserName` varchar(60) DEFAULT NULL,
  `UserLastname` varchar(60) DEFAULT NULL,
  `UserEmail` varchar(60) DEFAULT NULL,
  `UserPhone` varchar(20) DEFAULT NULL,
  `UserAddress` varchar(100) DEFAULT NULL,
  `UserCountry` varchar(50) DEFAULT NULL,
  `UserCity` varchar(50) DEFAULT NULL,
  `UserBirthDate` date DEFAULT NULL,
  `UserPassword` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `mainuser`
--

INSERT INTO `mainuser` (`MainUser_id`, `CredentialType`, `CredentialNumber`, `UserName`, `UserLastname`, `UserEmail`, `UserPhone`, `UserAddress`, `UserCountry`, `UserCity`, `UserBirthDate`, `UserPassword`) VALUES
(1, 'CC', 10000001, 'Juan', 'Pérez', 'juan.perez@email.com', '3001112233', 'Cra 1 #10-20', 'Colombia', 'Bogotá', '1990-01-01', 'pass123'),
(2, 'CC', 10000002, 'Ana', 'Gómez', 'ana.gomez@email.com', '3002223344', 'Cra 2 #20-30', 'Colombia', 'Medellín', '1992-02-02', 'pass234'),
(3, 'CC', 10000003, 'Luis', 'Martínez', 'luis.martinez@email.com', '3003334455', 'Cra 3 #30-40', 'Colombia', 'Cali', '1988-03-03', 'pass345'),
(4, 'CC', 10000004, 'Sofía', 'Rodríguez', 'sofia.rodriguez@email.com', '3004445566', 'Cra 4 #40-50', 'Colombia', 'Barranquilla', '1995-04-04', 'pass456'),
(5, 'CC', 10000005, 'Carlos', 'López', 'carlos.lopez@email.com', '3005556677', 'Cra 5 #50-60', 'Colombia', 'Cartagena', '1991-05-05', 'pass567'),
(6, 'CC', 10000006, 'María', 'Torres', 'maria.torres@email.com', '3006667788', 'Cra 6 #60-70', 'Colombia', 'Santa Marta', '1993-06-06', 'pass678'),
(7, 'CC', 10000007, 'Pedro', 'Ramírez', 'pedro.ramirez@email.com', '3007778899', 'Cra 7 #70-80', 'Colombia', 'Bucaramanga', '1989-07-07', 'pass789'),
(8, 'CC', 10000008, 'Laura', 'Jiménez', 'laura.jimenez@email.com', '3008889900', 'Cra 8 #80-90', 'Colombia', 'Pereira', '1994-08-08', 'pass890');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `package`
--

CREATE TABLE `package` (
  `Package_id` int(11) NOT NULL,
  `Partner_id` int(11) DEFAULT NULL,
  `PackagePrice` decimal(10,2) DEFAULT NULL,
  `OriginTrip` int(11) DEFAULT NULL,
  `DestinationTrip` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `package`
--

INSERT INTO `package` (`Package_id`, `Partner_id`, `PackagePrice`, `OriginTrip`, `DestinationTrip`) VALUES
(1, 1, 1200.00, 1, 'Bogotá'),
(2, 2, 1500.00, 2, 'Medellín'),
(3, 3, 900.00, 3, 'Cali'),
(4, 4, 2000.00, 4, 'Cartagena'),
(5, 5, 1100.00, 5, 'Santa Marta'),
(6, 6, 1800.00, 6, 'Bucaramanga'),
(7, 7, 950.00, 7, 'Pereira'),
(8, 8, 2100.00, 8, 'Manizales');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `partner`
--

CREATE TABLE `partner` (
  `Partner_id` int(11) NOT NULL,
  `MainUser_id` int(11) DEFAULT NULL,
  `Country_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `partner`
--

INSERT INTO `partner` (`Partner_id`, `MainUser_id`, `Country_id`) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 3),
(4, 4, 4),
(5, 5, 5),
(6, 6, 6),
(7, 7, 7),
(8, 8, 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `payment`
--

CREATE TABLE `payment` (
  `Payment_id` int(11) NOT NULL,
  `PaymentMethod_id` int(11) DEFAULT NULL,
  `Amount` decimal(10,2) DEFAULT NULL,
  `Id_comprobante` varchar(50) DEFAULT NULL,
  `Id_transaccion` int(11) DEFAULT NULL,
  `Estado_pago` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `payment`
--

INSERT INTO `payment` (`Payment_id`, `PaymentMethod_id`, `Amount`, `Id_comprobante`, `Id_transaccion`, `Estado_pago`) VALUES
(1, 1, 1200.00, 'CMP001', 1, 'Pagado'),
(2, 2, 1500.00, 'CMP002', 2, 'Pagado'),
(3, 3, 900.00, 'CMP003', 3, 'Pendiente'),
(4, 4, 2000.00, 'CMP004', 4, 'Pagado'),
(5, 5, 1100.00, 'CMP005', 5, 'Pagado'),
(6, 6, 1800.00, 'CMP006', 6, 'Pendiente'),
(7, 7, 950.00, 'CMP007', 7, 'Pagado'),
(8, 8, 2100.00, 'CMP008', 8, 'Pagado');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `paymentmethod`
--

CREATE TABLE `paymentmethod` (
  `PaymentMethod_id` int(11) NOT NULL,
  `PaymentType_id` int(11) DEFAULT NULL,
  `Tourist_id` int(11) DEFAULT NULL,
  `PMethodName` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `paymentmethod`
--

INSERT INTO `paymentmethod` (`PaymentMethod_id`, `PaymentType_id`, `Tourist_id`, `PMethodName`) VALUES
(1, 1, 1, 'Visa'),
(2, 2, 2, 'Mastercard'),
(3, 3, 3, 'Efectivo'),
(4, 4, 4, 'Transferencia Bancolombia'),
(5, 5, 5, 'Nequi'),
(6, 6, 6, 'Daviplata'),
(7, 7, 7, 'Paypal'),
(8, 8, 8, 'PSE');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `paymenttype`
--

CREATE TABLE `paymenttype` (
  `PaymentType_id` int(11) NOT NULL,
  `PaymentName` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `paymenttype`
--

INSERT INTO `paymenttype` (`PaymentType_id`, `PaymentName`) VALUES
(1, 'Tarjeta de crédito'),
(2, 'Tarjeta débito'),
(3, 'Efectivo'),
(4, 'Transferencia'),
(5, 'Nequi'),
(6, 'Daviplata'),
(7, 'Paypal'),
(8, 'PSE');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `place`
--

CREATE TABLE `place` (
  `Place_id` int(11) NOT NULL,
  `Country_id` int(11) DEFAULT NULL,
  `PlaceName` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `place`
--

INSERT INTO `place` (`Place_id`, `Country_id`, `PlaceName`) VALUES
(1, 1, 'Bogotá'),
(2, 2, 'Medellín'),
(3, 3, 'Cali'),
(4, 4, 'Cartagena'),
(5, 5, 'Santa Marta'),
(6, 6, 'Bucaramanga'),
(7, 7, 'Pereira'),
(8, 8, 'Manizales');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `profit`
--

CREATE TABLE `profit` (
  `Profit_id` int(11) NOT NULL,
  `Amount` decimal(10,2) DEFAULT NULL,
  `ProfitPercentage` decimal(5,2) DEFAULT NULL,
  `BasePrice` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `profit`
--

INSERT INTO `profit` (`Profit_id`, `Amount`, `ProfitPercentage`, `BasePrice`) VALUES
(1, 1200.00, 10.00, 1080.00),
(2, 1500.00, 12.50, 1333.33),
(3, 900.00, 8.00, 833.33),
(4, 2000.00, 15.00, 1739.13),
(5, 1100.00, 9.50, 1004.57),
(6, 1800.00, 13.00, 1592.92),
(7, 950.00, 7.00, 887.85),
(8, 2100.00, 16.00, 1810.34);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `refund`
--

CREATE TABLE `refund` (
  `Refund_id` int(11) NOT NULL,
  `Transaction_id` int(11) DEFAULT NULL,
  `Tourist_id` int(11) DEFAULT NULL,
  `RefundDate` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `refund`
--

INSERT INTO `refund` (`Refund_id`, `Transaction_id`, `Tourist_id`, `RefundDate`) VALUES
(1, 1, 1, '2025-01-10'),
(2, 2, 2, '2025-02-15'),
(3, 3, 3, '2025-03-20'),
(4, 4, 4, '2025-04-25'),
(5, 5, 5, '2025-05-30'),
(6, 6, 6, '2025-06-05'),
(7, 7, 7, '2025-07-10'),
(8, 8, 8, '2025-08-15');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `report`
--

CREATE TABLE `report` (
  `Report_id` int(11) NOT NULL,
  `MainUser_id` int(11) DEFAULT NULL,
  `Transaction_id` int(11) DEFAULT NULL,
  `SalesReport` varchar(50) DEFAULT NULL,
  `ReportDate` date DEFAULT NULL,
  `FinancialReport` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `report`
--

INSERT INTO `report` (`Report_id`, `MainUser_id`, `Transaction_id`, `SalesReport`, `ReportDate`, `FinancialReport`) VALUES
(1, 1, 1, 'Ventas Enero', '2025-01-31', 'Finanzas Enero'),
(2, 2, 2, 'Ventas Febrero', '2025-02-28', 'Finanzas Febrero'),
(3, 3, 3, 'Ventas Marzo', '2025-03-31', 'Finanzas Marzo'),
(4, 4, 4, 'Ventas Abril', '2025-04-30', 'Finanzas Abril'),
(5, 5, 5, 'Ventas Mayo', '2025-05-31', 'Finanzas Mayo'),
(6, 6, 6, 'Ventas Junio', '2025-06-30', 'Finanzas Junio'),
(7, 7, 7, 'Ventas Julio', '2025-07-31', 'Finanzas Julio'),
(8, 8, 8, 'Ventas Agosto', '2025-08-31', 'Finanzas Agosto');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ticketsupport`
--

CREATE TABLE `ticketsupport` (
  `Ticket_id` int(11) NOT NULL,
  `Administrator_id` int(11) DEFAULT NULL,
  `Tourist_id` int(11) DEFAULT NULL,
  `Partner_id` int(11) DEFAULT NULL,
  `Status` tinyint(1) DEFAULT NULL,
  `Description` varchar(150) DEFAULT NULL,
  `StartTime` date DEFAULT NULL,
  `EndTime` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `ticketsupport`
--

INSERT INTO `ticketsupport` (`Ticket_id`, `Administrator_id`, `Tourist_id`, `Partner_id`, `Status`, `Description`, `StartTime`, `EndTime`) VALUES
(1, 1, 1, 1, 1, 'Problema con pago', '2025-01-02', '2025-01-03'),
(2, 2, 2, 2, 0, 'Consulta de reserva', '2025-02-02', '2025-02-03'),
(3, 3, 3, 3, 1, 'Reembolso solicitado', '2025-03-02', '2025-03-03'),
(4, 4, 4, 4, 0, 'Cambio de fecha', '2025-04-02', '2025-04-03'),
(5, 5, 5, 5, 1, 'Problema con hotel', '2025-05-02', '2025-05-03'),
(6, 6, 6, 6, 0, 'Consulta de itinerario', '2025-06-02', '2025-06-03'),
(7, 7, 7, 7, 1, 'Soporte técnico', '2025-07-02', '2025-07-03'),
(8, 8, 8, 8, 0, 'Consulta general', '2025-08-02', '2025-08-03');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tourist`
--

CREATE TABLE `tourist` (
  `Tourist_id` int(11) NOT NULL,
  `MainUser_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tourist`
--

INSERT INTO `tourist` (`Tourist_id`, `MainUser_id`) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `transaction`
--

CREATE TABLE `transaction` (
  `Id_transaccion` int(11) NOT NULL,
  `Payment_id` int(11) DEFAULT NULL,
  `Fecha_transaccion` date DEFAULT NULL,
  `Tipo_transaccion` varchar(20) DEFAULT NULL,
  `Metodo_pago` int(11) DEFAULT NULL,
  `Factura_emitida` int(11) DEFAULT NULL,
  `Estado_transaccion` varchar(20) DEFAULT NULL,
  `MainUser_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `transaction`
--

INSERT INTO `transaction` (`Id_transaccion`, `Payment_id`, `Fecha_transaccion`, `Tipo_transaccion`, `Metodo_pago`, `Factura_emitida`, `Estado_transaccion`, `MainUser_id`) VALUES
(1, 1, '2025-01-10', 'Compra', 1, 1, 'Completada', 1),
(2, 2, '2025-02-15', 'Compra', 2, 2, 'Completada', 2),
(3, 3, '2025-03-20', 'Compra', 3, 3, 'Pendiente', 3),
(4, 4, '2025-04-25', 'Compra', 4, 4, 'Completada', 4),
(5, 5, '2025-05-30', 'Compra', 5, 5, 'Completada', 5),
(6, 6, '2025-06-05', 'Compra', 6, 6, 'Pendiente', 6),
(7, 7, '2025-07-10', 'Compra', 7, 7, 'Completada', 7),
(8, 8, '2025-08-15', 'Compra', 8, 8, 'Completada', 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `trip`
--

CREATE TABLE `trip` (
  `Trip_id` int(11) NOT NULL,
  `Package_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `trip`
--

INSERT INTO `trip` (`Trip_id`, `Package_id`) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `administrator`
--
ALTER TABLE `administrator`
  ADD PRIMARY KEY (`Administrator_id`),
  ADD KEY `MainUser_id` (`MainUser_id`);

--
-- Indices de la tabla `airport`
--
ALTER TABLE `airport`
  ADD PRIMARY KEY (`Airport_id`),
  ADD KEY `Place_id` (`Place_id`),
  ADD KEY `Package_id` (`Package_id`);

--
-- Indices de la tabla `area`
--
ALTER TABLE `area`
  ADD PRIMARY KEY (`Area_id`),
  ADD KEY `Country_id` (`Country_id`);

--
-- Indices de la tabla `chat`
--
ALTER TABLE `chat`
  ADD PRIMARY KEY (`Chat_id`),
  ADD KEY `Administrator_id` (`Administrator_id`),
  ADD KEY `Tourist_id` (`Tourist_id`),
  ADD KEY `Partner_id` (`Partner_id`);

--
-- Indices de la tabla `company`
--
ALTER TABLE `company`
  ADD PRIMARY KEY (`Company_id`),
  ADD KEY `Partner_id` (`Partner_id`);

--
-- Indices de la tabla `country`
--
ALTER TABLE `country`
  ADD PRIMARY KEY (`Country_id`);

--
-- Indices de la tabla `hotel`
--
ALTER TABLE `hotel`
  ADD PRIMARY KEY (`Hotel_id`);

--
-- Indices de la tabla `mainuser`
--
ALTER TABLE `mainuser`
  ADD PRIMARY KEY (`MainUser_id`);

--
-- Indices de la tabla `package`
--
ALTER TABLE `package`
  ADD PRIMARY KEY (`Package_id`),
  ADD KEY `Partner_id` (`Partner_id`);

--
-- Indices de la tabla `partner`
--
ALTER TABLE `partner`
  ADD PRIMARY KEY (`Partner_id`),
  ADD KEY `MainUser_id` (`MainUser_id`),
  ADD KEY `Country_id` (`Country_id`);

--
-- Indices de la tabla `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`Payment_id`),
  ADD KEY `PaymentMethod_id` (`PaymentMethod_id`);

--
-- Indices de la tabla `paymentmethod`
--
ALTER TABLE `paymentmethod`
  ADD PRIMARY KEY (`PaymentMethod_id`),
  ADD KEY `PaymentType_id` (`PaymentType_id`),
  ADD KEY `Tourist_id` (`Tourist_id`);

--
-- Indices de la tabla `paymenttype`
--
ALTER TABLE `paymenttype`
  ADD PRIMARY KEY (`PaymentType_id`);

--
-- Indices de la tabla `place`
--
ALTER TABLE `place`
  ADD PRIMARY KEY (`Place_id`),
  ADD KEY `Country_id` (`Country_id`);

--
-- Indices de la tabla `profit`
--
ALTER TABLE `profit`
  ADD PRIMARY KEY (`Profit_id`);

--
-- Indices de la tabla `refund`
--
ALTER TABLE `refund`
  ADD PRIMARY KEY (`Refund_id`),
  ADD KEY `Transaction_id` (`Transaction_id`),
  ADD KEY `Tourist_id` (`Tourist_id`);

--
-- Indices de la tabla `report`
--
ALTER TABLE `report`
  ADD PRIMARY KEY (`Report_id`),
  ADD KEY `MainUser_id` (`MainUser_id`),
  ADD KEY `Transaction_id` (`Transaction_id`);

--
-- Indices de la tabla `ticketsupport`
--
ALTER TABLE `ticketsupport`
  ADD PRIMARY KEY (`Ticket_id`),
  ADD KEY `Administrator_id` (`Administrator_id`),
  ADD KEY `Tourist_id` (`Tourist_id`),
  ADD KEY `Partner_id` (`Partner_id`);

--
-- Indices de la tabla `tourist`
--
ALTER TABLE `tourist`
  ADD PRIMARY KEY (`Tourist_id`),
  ADD KEY `MainUser_id` (`MainUser_id`);

--
-- Indices de la tabla `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`Id_transaccion`),
  ADD KEY `Payment_id` (`Payment_id`),
  ADD KEY `MainUser_id` (`MainUser_id`);

--
-- Indices de la tabla `trip`
--
ALTER TABLE `trip`
  ADD PRIMARY KEY (`Trip_id`),
  ADD KEY `Package_id` (`Package_id`);

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `administrator`
--
ALTER TABLE `administrator`
  ADD CONSTRAINT `administrator_ibfk_1` FOREIGN KEY (`MainUser_id`) REFERENCES `mainuser` (`MainUser_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `airport`
--
ALTER TABLE `airport`
  ADD CONSTRAINT `airport_ibfk_1` FOREIGN KEY (`Place_id`) REFERENCES `place` (`Place_id`),
  ADD CONSTRAINT `airport_ibfk_2` FOREIGN KEY (`Package_id`) REFERENCES `package` (`Package_id`);

--
-- Filtros para la tabla `area`
--
ALTER TABLE `area`
  ADD CONSTRAINT `area_ibfk_1` FOREIGN KEY (`Country_id`) REFERENCES `country` (`Country_id`);

--
-- Filtros para la tabla `chat`
--
ALTER TABLE `chat`
  ADD CONSTRAINT `chat_ibfk_1` FOREIGN KEY (`Administrator_id`) REFERENCES `administrator` (`Administrator_id`),
  ADD CONSTRAINT `chat_ibfk_2` FOREIGN KEY (`Tourist_id`) REFERENCES `tourist` (`Tourist_id`),
  ADD CONSTRAINT `chat_ibfk_3` FOREIGN KEY (`Partner_id`) REFERENCES `partner` (`Partner_id`);

--
-- Filtros para la tabla `company`
--
ALTER TABLE `company`
  ADD CONSTRAINT `company_ibfk_1` FOREIGN KEY (`Partner_id`) REFERENCES `partner` (`Partner_id`);

--
-- Filtros para la tabla `package`
--
ALTER TABLE `package`
  ADD CONSTRAINT `package_ibfk_1` FOREIGN KEY (`Partner_id`) REFERENCES `partner` (`Partner_id`);

--
-- Filtros para la tabla `partner`
--
ALTER TABLE `partner`
  ADD CONSTRAINT `partner_ibfk_1` FOREIGN KEY (`MainUser_id`) REFERENCES `mainuser` (`MainUser_id`),
  ADD CONSTRAINT `partner_ibfk_2` FOREIGN KEY (`Country_id`) REFERENCES `country` (`Country_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`PaymentMethod_id`) REFERENCES `paymentmethod` (`PaymentMethod_id`);

--
-- Filtros para la tabla `paymentmethod`
--
ALTER TABLE `paymentmethod`
  ADD CONSTRAINT `paymentmethod_ibfk_1` FOREIGN KEY (`PaymentType_id`) REFERENCES `paymenttype` (`PaymentType_id`),
  ADD CONSTRAINT `paymentmethod_ibfk_2` FOREIGN KEY (`Tourist_id`) REFERENCES `tourist` (`Tourist_id`);

--
-- Filtros para la tabla `place`
--
ALTER TABLE `place`
  ADD CONSTRAINT `place_ibfk_1` FOREIGN KEY (`Country_id`) REFERENCES `country` (`Country_id`);

--
-- Filtros para la tabla `refund`
--
ALTER TABLE `refund`
  ADD CONSTRAINT `refund_ibfk_1` FOREIGN KEY (`Transaction_id`) REFERENCES `transaction` (`Id_transaccion`),
  ADD CONSTRAINT `refund_ibfk_2` FOREIGN KEY (`Tourist_id`) REFERENCES `tourist` (`Tourist_id`);

--
-- Filtros para la tabla `report`
--
ALTER TABLE `report`
  ADD CONSTRAINT `report_ibfk_1` FOREIGN KEY (`MainUser_id`) REFERENCES `mainuser` (`MainUser_id`),
  ADD CONSTRAINT `report_ibfk_2` FOREIGN KEY (`Transaction_id`) REFERENCES `transaction` (`Id_transaccion`);

--
-- Filtros para la tabla `ticketsupport`
--
ALTER TABLE `ticketsupport`
  ADD CONSTRAINT `ticketsupport_ibfk_1` FOREIGN KEY (`Administrator_id`) REFERENCES `administrator` (`Administrator_id`),
  ADD CONSTRAINT `ticketsupport_ibfk_2` FOREIGN KEY (`Tourist_id`) REFERENCES `tourist` (`Tourist_id`),
  ADD CONSTRAINT `ticketsupport_ibfk_3` FOREIGN KEY (`Partner_id`) REFERENCES `partner` (`Partner_id`);

--
-- Filtros para la tabla `tourist`
--
ALTER TABLE `tourist`
  ADD CONSTRAINT `tourist_ibfk_1` FOREIGN KEY (`MainUser_id`) REFERENCES `mainuser` (`MainUser_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`Payment_id`) REFERENCES `payment` (`Payment_id`),
  ADD CONSTRAINT `transaction_ibfk_2` FOREIGN KEY (`MainUser_id`) REFERENCES `mainuser` (`MainUser_id`);

--
-- Filtros para la tabla `trip`
--
ALTER TABLE `trip`
  ADD CONSTRAINT `trip_ibfk_1` FOREIGN KEY (`Package_id`) REFERENCES `package` (`Package_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
