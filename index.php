<?php
// index.php — TouristChain Front Controller (fuera de public)
declare(strict_types=1);

define('ROOT', __DIR__);

// 1) Autoload: Composer instalado en /app/vendor
require 'vendor/autoload.php';

// 2) Namespaces que usaremos
use V01\Touristchain\Core\Router;
use V01\Touristchain\Controllers\HomeController;

// 3) Instanciar Router
$router = new Router();

// 4) Rutas mínimas
$router->get('/', [HomeController::class, 'index']);

// 5) Despachar
$router->dispatch($_SERVER['REQUEST_URI'], $_SERVER['REQUEST_METHOD']);
