<?php

//define('BASE_URL', 'http://localhost:8080/touristchain/public/');

$host   = $_SERVER['HTTP_HOST'];
$scheme = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off') ? 'https' : 'http';
define('BASE_URL', $scheme . '://' . $host . '/');


// Configuración de conexión a la base de datos
define('DB_HOST','localhost');
define('DB_NAME','touristchain_db');
define('DB_USER','root');
define('DB_PASS','');
define('DB_CHARSET','utf8mb4');

$dsn = 'mysql:host='.DB_HOST.';dbname='.DB_NAME.';charset='.DB_CHARSET;
$pdo = new PDO($dsn, DB_USER, DB_PASS, [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
]);