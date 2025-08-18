<?php
// Punto de entrada TouristChain

// 1. Definir raíz
define('ROOT', dirname(__DIR__));

// 2. Autoload si existe (lo haremos luego con Composer)
// require ROOT . '/vendor/autoload.php';

// 3. Incluir Router (todavía MVP mínimo)
require ROOT . '/App/Core/Router.php';

// 4. Instanciar Router
$router = new Router();

// 5. Registrar rutas
$router->get('/', [\App\Controllers\HomeController::class, 'index']);

// 6. Despachar la petición actual
$router->dispatch($_SERVER['REQUEST_URI'], $_SERVER['REQUEST_METHOD']);
