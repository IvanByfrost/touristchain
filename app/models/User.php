<?php
namespace App\Models;

require(__DIR__ . '/../../../config.php');
class User extends BaseModel
{
    public static function all() {
        $stmt = $pdo->query('SELECT * FROM users');
        return $stmt->fetchAll();
    }

    public static function find(int $id) {
        $stmt = $pdo->prepare('SELECT * FROM users WHERE id = ?');
        $stmt->execute([$id]);
        return $stmt->fetch();
    }

    public static function create(array $data) {
        $sql = 'INSERT INTO users (name, email, password) VALUES (?, ?, ?)';
        $stmt = $pdo->prepare($sql);
        return $stmt->execute([
            $data['name'],
            $data['email'],
            $data['password']
        ]);
    }

    public static function update(int $id, array $data) {
        $sql = 'UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?';
        $stmt = $pdo->prepare($sql);
        return $stmt->execute([
            $data['name'],
            $data['email'],
            $data['password'],
            $id
        ]);
    }

    public static function delete(int $id) {
        $sql = 'DELETE FROM users WHERE id = ?';
        $stmt = $pdo->prepare($sql);
        return $stmt->execute([$id]);
    }
}
