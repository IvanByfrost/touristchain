<?php
namespace App\Models;

class Establishment extends BaseModel
{
    public static function all() {
        // Implementar lógica para obtener todos los registros
        return [];
    }
    public static function find(int $id) {
        // Implementar lógica para encontrar por id
        return null;
    }
    public static function create(array $data) {
        // Implementar lógica para crear un registro
        return true;
    }
    public static function update(int $id, array $data) {
        // Implementar lógica para actualizar un registro
        return true;
    }
    public static function delete(int $id) {
        // Implementar lógica para borrar un registro
        return true;
    }
}
