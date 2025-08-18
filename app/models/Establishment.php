<?php
declare(strict_types=1);

namespace V01\Touristchain\Models;

use V01\Touristchain\Models\BaseModel;

/**
 * Establishment (ActiveRecord simple sobre BaseModel)
 *
 * Requiere que en tu bootstrap llames una vez:
 *   BaseModel::useConnection($pdo);
 */
final class Establishment extends BaseModel
{
    /** Nombre de la tabla (ajusta si usas plural 'establishments') */
    protected static string $table = 'establishments';

    /** Clave primaria */
    protected static string $primaryKey = 'id';

    /** Columnas permitidas para create/update */
    protected static array $fillable = ['name', 'address'];

    /** Activa si tu tabla tiene created_at/updated_at */
    protected static bool $timestamps = false;

    /** Activa si tu tabla tiene deleted_at (soft delete) */
    protected static bool $softDeletes = false;

    // No es necesario redefinir all/find/create/update/delete:
    // se heredan de BaseModel y usan PDO preparado correctamente.
}
