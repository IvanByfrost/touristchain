<?php
namespace App\Controllers;

class HomeController
{
    public function index()
    {
        $data = [
            'title' => 'TouristChain | Home',
            'message' => 'Bienvenido a TouristChain'
        ];

        // Pasamos las variables a la vista
        extract($data);

        // Render mínimo con layout
        require __DIR__ . '/../Views/layout.php';
    }
}
