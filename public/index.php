<?php
declare(strict_types=1);

require_once __DIR__ . '/../config.php';
require_once __DIR__ . '/../vendor/autoload.php';


use V01\Touristchain\Models\BaseModel;

$dsn = 'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME . ';charset=' . DB_CHARSET;

try {
    $pdo = new PDO($dsn, DB_USER, DB_PASS, [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    ]);

    // Si más adelante actualizas a MySQL que soporte utf8mb4:
    // $pdo->exec("SET NAMES utf8mb4");
} catch (PDOException $e) {
    http_response_code(500);
    die('DB connection failed: ' . $e->getMessage());
}

// Inyecta en tu BaseModel (requisito de tu arquitectura)
BaseModel::useConnection($pdo);

try {
    $pdo->query('SELECT 1');
} catch (Throwable $e) {
    error_log('Ping DB error: '.$e->getMessage());
}

use V01\Touristchain\Core\Router;
use V01\Touristchain\Controllers\HomeController;
use V01\Touristchain\Controllers\AuthController;

$router = new Router();

$router->post('/api/register', [AuthController::class, 'register']);
$router->post('/api/login',    [AuthController::class, 'login']);

$router->get('/api/register', function () {
  header('Content-Type: application/json; charset=utf-8');
  http_response_code(405);
  echo json_encode(['success'=>false,'message'=>'Usa POST en /api/register']);
});

$router->get('/', [HomeController::class, 'index']);

$router->dispatch($_SERVER['REQUEST_URI'], $_SERVER['REQUEST_METHOD']);
