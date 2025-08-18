<?php
declare(strict_types=1);

namespace V01\Touristchain\Models;

use V01\Touristchain\Models\BaseModel; // ← ajusta si tu BaseModel está en otro namespace

/**
 * User (ActiveRecord simple sobre BaseModel)
 *
 * Importante:
 * - En tu bootstrap debes llamar una vez: BaseModel::useConnection($pdo)
 * - Este modelo hace hash de password al crear/actualizar.
 */
final class User extends BaseModel
{
    /** Nombre de la tabla */
    protected static string $table = 'users';

    /** Clave primaria */
    protected static string $primaryKey = 'id';

    /** Columnas permitidas para asignación masiva */
    protected static array $fillable = ['name', 'email', 'password'];

    /**
     * Activa esto SOLO si tu tabla tiene created_at / updated_at.
     * Cambia a true si existen esas columnas en DB.
     */
    protected static bool $timestamps = false;

    /** Soft deletes (requiere deleted_at). Déjalo en false si no existe. */
    protected static bool $softDeletes = false;

    /**
     * Crea usuario con hash de contraseña (si viene en $data).
     */
    public static function create(array $data): int
    {
        if (isset($data['password']) && $data['password'] !== '') {
            $data['password'] = password_hash((string)$data['password'], PASSWORD_DEFAULT);
        }
        return parent::create($data);
    }

    /**
     * Actualiza usuario, re-hasheando password si fue provista.
     * Si password viene vacío, no se toca.
     */
    public static function update(int $id, array $data): bool
    {
        if (array_key_exists('password', $data)) {
            if ($data['password'] === '' || $data['password'] === null) {
                unset($data['password']); // no actualizar password
            } else {
                $data['password'] = password_hash((string)$data['password'], PASSWORD_DEFAULT);
            }
        }
        return parent::update($id, $data);
    }

    // find/all/delete se heredan de BaseModel (no necesitas redefinirlos).
}
