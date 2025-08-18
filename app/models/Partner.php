<?php
declare(strict_types=1);

namespace V01\Touristchain\Models;

use V01\Touristchain\Models\BaseModel; // Ajusta este use si tu BaseModel vive en otro namespace

/**
 * Partner (ActiveRecord simple sobre BaseModel)
 *
 * Requiere que, en el bootstrap de tu app, hayas llamado:
 *   BaseModel::useConnection($pdo);
 */
final class Partner extends BaseModel
{
    /** Nombre de la tabla (ajusta a 'partners' si tu esquema es plural). */
    protected static string $table = 'partner';

    /** Clave primaria. */
    protected static string $primaryKey = 'id';

    /** Columnas asignables en create/update. */
    protected static array $fillable = ['name', 'type', 'description', 'location'];

    /** Desactiva timestamps si tu tabla NO tiene created_at/updated_at. */
    protected static bool $timestamps = false;

    /** Soft deletes desactivado (no hay deleted_at). */
    protected static bool $softDeletes = false;

    // Nota: No necesitas redefinir create/all/find/update/delete:
    // vienen heredados de BaseModel y usan PDO preparado correctamente.
}
