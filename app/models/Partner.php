<?php
namespace App\Models;

class Partner extends BaseModel
{
    public static function create(array $data) {
        require(__DIR__ . '/../../../config.php');
        $sql = 'INSERT INTO partner (name, type, description, location) VALUES (?, ?, ?, ?)';
        $stmt = $pdo->prepare($sql);
        return $stmt->execute([
            $data['name'],
            $data['type'],
            $data['description'],
            $data['location']
        ]);
    }

    public static function all() {
        require(__DIR__ . '/../../../config.php');
        $stmt = $pdo->query('SELECT * FROM partner');
        return $stmt->fetchAll();
    }

    public static function find(int $id) {
        require(__DIR__ . '/../../../config.php');
        $stmt = $pdo->prepare('SELECT * FROM partner WHERE id = ?');
        $stmt->execute([$id]);
        return $stmt->fetch();
    }

    public static function update(int $id, array $data) {
        require(__DIR__ . '/../../../config.php');
        $sql = 'UPDATE partner SET name = ?, type = ?, description = ?, location = ? WHERE id = ?';
        $stmt = $pdo->prepare($sql);
        return $stmt->execute([
            $data['name'],
            $data['type'],
            $data['description'],
            $data['location'],
            $id
        ]);
    }

    public static function delete(int $id) {
        require(__DIR__ . '/../../../config.php');
        $sql = 'DELETE FROM partner WHERE id = ?';
        $stmt = $pdo->prepare($sql);
        return $stmt->execute([$id]);
    }
}
