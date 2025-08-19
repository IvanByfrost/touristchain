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
    protected static string $table = 'mainuser';

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
    public static function create(array $data): ?int
    {
        $pdo = static::conn(); // Use the connection from BaseModel

        // Hashear contraseña si no viene hasheada
        if (!empty($data['password'])) {
            $data['password'] = password_hash((string)$data['password'], PASSWORD_DEFAULT);
        }

        // Mapea a columnas de la tabla (lo que no llegue queda NULL)
        $payload = [
            'CredentialType'   => $data['credential_type']   ?? null,
            'CredentialNumber' => isset($data['credential_number']) ? (int)$data['credential_number'] : null,
            'UserName'         => $data['first_name']        ?? null,
            'UserLastname'     => $data['last_name']         ?? null,
            'UserEmail'        => $data['email']             ?? null,
            'UserPhone'        => $data['phone']             ?? null,
            'UserAddress'      => $data['address']           ?? null,
            'UserCountry'      => $data['country']           ?? null,
            'UserCity'         => $data['city']              ?? null,
            'UserBirthDate'    => $data['birthdate']         ?? null, // 'YYYY-MM-DD'
            'UserPassword'     => $data['password']          ?? null,
            // Solo si agregaste la columna:
            'UserRole'         => $data['role']              ?? null,
        ];

        // Construir INSERT solo con columnas existentes (quita UserRole si no la agregaste)
        $sql = 'INSERT INTO mainuser
            (CredentialType, CredentialNumber, UserName, UserLastname, UserEmail, UserPhone, UserAddress, UserCountry, UserCity, UserBirthDate, UserPassword'.(array_key_exists('UserRole', $payload)?', UserRole':'').')
            VALUES
            (:CredentialType, :CredentialNumber, :UserName, :UserLastname, :UserEmail, :UserPhone, :UserAddress, :UserCountry, :UserCity, :UserBirthDate, :UserPassword'.(array_key_exists('UserRole', $payload)?', :UserRole':'').')';

        $st = $pdo->prepare($sql);
        $ok = $st->execute(array_filter($payload, fn($k) => $k !== 'UserRole' || array_key_exists('UserRole',$payload), ARRAY_FILTER_USE_KEY));

        if (!$ok) return null;

        // Si tu PK es AUTO_INCREMENT:
        $id = (int)$pdo->lastInsertId();
        return $id > 0 ? $id : null;
    }

        public static function createMinimal($email, $password, $role = 'tourist')
    {
        $pdo = static::conn(); // Use the connection from BaseModel
        $hash = password_hash($password, PASSWORD_DEFAULT);

        $stmt = $pdo->prepare("INSERT INTO mainuser (UserEmail, UserPassword, UserRole) VALUES (?, ?, ?)");
        $stmt->execute([$email, $hash, $role]);
        return $pdo->lastInsertId();
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

    public static function findByEmail(string $email): ?array
    {
        $pdo = static::conn(); // Use the connection from BaseModel
        $stmt = $pdo->prepare('SELECT * FROM mainuser WHERE email = :email LIMIT 1');
        $stmt->execute(['email' => $email]);
        $user = $stmt->fetch(\PDO::FETCH_ASSOC);

        return $user ?: null;
    }

        public static function existsByEmail($email)
    {
        // Assuming you have a PDO connection method getConnection()
        $db = static::conn();
        $stmt = $db->prepare('SELECT COUNT(*) FROM mainuser WHERE UserEmail = :email');
        $stmt->execute(['email' => $email]);
        return $stmt->fetchColumn() > 0;
    }

}
