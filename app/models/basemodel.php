<?php
declare(strict_types=1);

namespace V01\Touristchain\Models;

use PDO;
use InvalidArgumentException;
use RuntimeException;

/**
 * BaseModel (MVC clásico, ActiveRecord simple)
 *
 * Requisitos por subclase:
 *  - static string  $table       = 'tabla';
 *  - static string  $primaryKey  = 'id';
 *  - static array   $fillable    = ['col1', 'col2', ...]; // columnas asignables
 *  - static bool    $timestamps  = true;  // si quieres manejar created_at/updated_at
 *  - static bool    $softDeletes = false; // si quieres manejar deleted_at
 *
 * Conexión:
 *  - Llamar una vez: BaseModel::useConnection($pdo);
 *    (o cada subclase: User::useConnection($pdo);)
 */
abstract class BaseModel
{
    /** @var PDO|null Conexión compartida a nivel de clase base */
    protected static ?PDO $pdo = null;

    /** Configurables por subclase */
    protected static string $table;
    protected static string $primaryKey = 'id';
    protected static array  $fillable = [];
    protected static bool   $timestamps = true;
    protected static bool   $softDeletes = false;

    // =========================
    // Conexión
    // =========================
    public static function useConnection(PDO $pdo): void
    {
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
        self::$pdo = $pdo;
    }

    protected static function conn(): PDO
    {
        if (!self::$pdo) {
            throw new RuntimeException('BaseModel: no hay conexión PDO. Llama BaseModel::useConnection($pdo) antes de usar.');
        }
        return self::$pdo;
    }

    // =========================
    // Lectura
    // =========================
    public static function all(?string $orderBy = null, string $dir = 'DESC'): array
    {
        $table = static::table();
        $sql = "SELECT * FROM {$table}";
        if (static::$softDeletes) {
            $sql .= " WHERE deleted_at IS NULL";
        }
        if ($orderBy) {
            $sql .= " ORDER BY {$orderBy} " . (strtoupper($dir) === 'ASC' ? 'ASC' : 'DESC');
        }
        return static::conn()->query($sql)->fetchAll();
    }

    public static function find(int $id): ?array
    {
        $table = static::table();
        $pk    = static::pk();
        $sql   = "SELECT * FROM {$table} WHERE {$pk} = :id";
        if (static::$softDeletes) {
            $sql .= " AND deleted_at IS NULL";
        }
        $st = static::conn()->prepare($sql);
        $st->execute([':id' => $id]);
        $row = $st->fetch();
        return $row ?: null;
    }

    /**
     * where: condiciones simples con AND.
     * $conds = ['campo1' => 'valor', 'campo2' => 10]
     */
    public static function where(array $conds = [], ?string $orderBy = null, ?int $limit = null, ?int $offset = null): array
    {
        $table = static::table();
        $sql   = "SELECT * FROM {$table}";
        $params = [];

        $where = [];
        foreach ($conds as $k => $v) {
            $ph = ':' . preg_replace('/\W+/', '_', $k);
            $where[] = "{$k} = {$ph}";
            $params[$ph] = $v;
        }
        if (static::$softDeletes) {
            $where[] = "deleted_at IS NULL";
        }
        if ($where) $sql .= " WHERE " . implode(' AND ', $where);

        if ($orderBy) $sql .= " ORDER BY {$orderBy}";
        if ($limit !== null)  $sql .= " LIMIT " . (int)$limit;
        if ($offset !== null) $sql .= " OFFSET " . (int)$offset;

        $st = static::conn()->prepare($sql);
        $st->execute($params);
        return $st->fetchAll();
    }

    // =========================
    // Escritura
    // =========================
    public static function create(array $data): int
    {
        $table = static::table();
        $clean = static::onlyFillable($data);

        if (static::$timestamps) {
            $now = date('Y-m-d H:i:s');
            $clean['created_at'] = $clean['created_at'] ?? $now;
            $clean['updated_at'] = $clean['updated_at'] ?? $now;
        }

        if (empty($clean)) {
            throw new InvalidArgumentException(static::class . '::create() sin datos válidos (fillable).');
        }

        [$cols, $placeholders, $params] = static::buildInsert($clean);
        $sql = "INSERT INTO {$table} ({$cols}) VALUES ({$placeholders})";
        $st  = static::conn()->prepare($sql);
        $st->execute($params);
        return (int) static::conn()->lastInsertId();
    }

    public static function update(int $id, array $data): bool
    {
        $table = static::table();
        $pk    = static::pk();
        $clean = static::onlyFillable($data);

        if (static::$timestamps) {
            $clean['updated_at'] = date('Y-m-d H:i:s');
        }
        if (empty($clean)) {
            throw new InvalidArgumentException(static::class . '::update() sin datos válidos (fillable).');
        }

        [$sets, $params] = static::buildUpdate($clean);
        $params[":__pk"] = $id;

        $sql = "UPDATE {$table} SET {$sets} WHERE {$pk} = :__pk";
        return static::conn()->prepare($sql)->execute($params);
    }

    public static function delete(int $id): bool
    {
        $table = static::table();
        $pk    = static::pk();

        if (static::$softDeletes) {
            $sql = "UPDATE {$table} SET deleted_at = :dt WHERE {$pk} = :id";
            return static::conn()->prepare($sql)->execute([
                ':dt' => date('Y-m-d H:i:s'),
                ':id' => $id,
            ]);
        }

        $sql = "DELETE FROM {$table} WHERE {$pk} = :id";
        return static::conn()->prepare($sql)->execute([':id' => $id]);
    }

    // =========================
    // Helpers
    // =========================
    protected static function table(): string
    {
        if (!isset(static::$table) || static::$table === '') {
            throw new RuntimeException(static::class . ' no define $table.');
        }
        return static::$table;
    }

    protected static function pk(): string
    {
        return static::$primaryKey ?: 'id';
    }

    protected static function onlyFillable(array $data): array
    {
        if (empty(static::$fillable)) return [];

        $out = [];
        foreach (static::$fillable as $col) {
            if (array_key_exists($col, $data)) {
                $out[$col] = $data[$col];
            }
        }
        return $out;
    }

    protected static function buildInsert(array $clean): array
    {
        $cols = [];
        $phs  = [];
        $params = [];
        foreach ($clean as $k => $v) {
            $cols[] = $k;
            $p = ':' . $k;
            $phs[] = $p;
            $params[$p] = $v;
        }
        return [implode(',', $cols), implode(',', $phs), $params];
    }

    protected static function buildUpdate(array $clean): array
    {
        $sets = [];
        $params = [];
        foreach ($clean as $k => $v) {
            $p = ':' . $k;
            $sets[] = "{$k} = {$p}";
            $params[$p] = $v;
        }
        return [implode(', ', $sets), $params];
    }

    // =========================
    // Paginación simple (opcional)
    // =========================
    public static function paginate(int $page = 1, int $perPage = 10, ?array $conds = null, string $orderBy = 'id DESC'): array
    {
        $page = max(1, $page);
        $offset = ($page - 1) * $perPage;

        $data = $conds ? static::where($conds, $orderBy, $perPage, $offset)
                       : static::where([],   $orderBy, $perPage, $offset);

        // Conteo simple (sin WHERE complejo)
        $table = static::table();
        $sqlCount = "SELECT COUNT(*) AS c FROM {$table}";
        if (static::$softDeletes) $sqlCount .= " WHERE deleted_at IS NULL";
        $total = (int) static::conn()->query($sqlCount)->fetchColumn();

        return [
            'data' => $data,
            'meta' => [
                'page'      => $page,
                'perPage'   => $perPage,
                'total'     => $total,
                'totalPages'=> (int) ceil($total / $perPage),
            ]
        ];
    }
}
