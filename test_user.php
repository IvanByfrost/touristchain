<?php
use App\Models\User;

require_once __DIR__ . '/app/models/User.php';

function print_result($label, $result) {
    echo "<h3>$label</h3>";
    echo '<pre>';
    print_r($result);
    echo '</pre>';
}

// 1. Obtener todos los usuarios
print_result('Todos los usuarios', User::all());

// 2. Crear usuario de ejemplo
echo "<h3>Crear usuario de ejemplo</h3>";
$newUser = [
    'name' => 'Ejemplo',
    'email' => 'ejemplo@dominio.com',
    'password' => password_hash('123456', PASSWORD_DEFAULT)
];
User::create($newUser);

// 3. Buscar el último usuario insertado
require __DIR__ . '/config.php';
$lastId = $pdo->lastInsertId();
print_result('Usuario insertado', User::find($lastId));

// 4. Actualizar al usuario generado
$upUser = [
    'name' => 'Ejemplo Modificado',
    'email' => 'ejemplo2@dominio.com',
    'password' => password_hash('abcdef', PASSWORD_DEFAULT)
];
User::update($lastId, $upUser);
print_result('Usuario actualizado', User::find($lastId));

// 5. Eliminar usuario
User::delete($lastId);
print_result('Usuario tras borrado', User::find($lastId));
