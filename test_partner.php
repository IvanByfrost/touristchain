<?php
use App\Models\Partner;

require_once __DIR__ . '/app/models/Partner.php';

// Socio 1: Restaurante Italiano
Partner::create([
    'name' => 'Ristorante Bella Italia',
    'type' => 'Restaurante',
    'description' => 'Restaurante italiano tradicional con auténtica pasta y pizza.',
    'location' => 'Cali, Colombia'
]);

// Socio 2: Hotel 3 estrellas
Partner::create([
    'name' => 'Hotel Central Cali',
    'type' => 'Hotel 3 Estrellas',
    'description' => 'Hotel confortable y céntrico en Cali, ideal para turistas y familias.',
    'location' => 'Cali, Colombia'
]);

// Socio 3: Empresa de transporte
Partner::create([
    'name' => 'TransCali Express',
    'type' => 'Transporte',
    'description' => 'Empresa de transporte exclusiva para recorridos turísticos y traslados.',
    'location' => 'Cali, Colombia'
]);

echo "Socios registrados exitosamente en la base de datos.";
