<?php
namespace V01\Touristchain\Controllers;

use V01\Touristchain\Models\User;
use V01\Touristchain\Controllers\BaseController;

/**
 * Controlador de autenticación
 */

class AuthController extends BaseController
{
    public function login()
    {
        header('Content-Type: application/json');

        // Sanitizar entrada
        $email = filter_input(INPUT_POST, 'email', FILTER_VALIDATE_EMAIL);
        $password = $_POST['password'] ?? null;

        if (!$email || !$password) {
            echo json_encode([
                'success' => false,
                'message' => 'Campos inválidos'
            ]);
            return;
        }

        // Buscar usuario en DB
        $user = User::findByEmail($email);
        if (!$user) {
            echo json_encode([
                'success' => false,
                'message' => 'Usuario no encontrado'
            ]);
            return;
        }

        // Verificar contraseña (usa password_hash en registros)
        if (!password_verify($password, $user['password'])) {
            echo json_encode([
                'success' => false,
                'message' => 'Contraseña incorrecta'
            ]);
            return;
        }

        // Si todo bien: iniciar sesión / generar token
        $_SESSION['user_id'] = $user['id'];

        echo json_encode([
            'success' => true,
            'message' => 'Login correcto',
            'user' => [
                'id' => $user['id'],
                'email' => $user['email'],
                'rol' => $user['rol'] ?? 'turista'
            ]
        ]);
    }

public function register()
{
    header('Content-Type: application/json; charset=utf-8');

    try {
        error_log('POST register: ' . json_encode($_POST, JSON_UNESCAPED_UNICODE));

        $emailRaw = $_POST['email'] ?? '';
        $email    = filter_var(trim($emailRaw), FILTER_VALIDATE_EMAIL);
        $pass1    = $_POST['password'] ?? '';
        $pass2    = $_POST['password_confirm'] ?? '';
        $roleRaw  = $_POST['role'] ?? null;

        // Mapeo tolerante ES/EN
        $role = null;
        if ($roleRaw !== null) {
            $norm = mb_strtolower(trim($roleRaw));
            $map  = [
                'turista'=>'tourist', 'socio'=>'partner', 'administrador'=>'admin',
                'tourist'=>'tourist','partner'=>'partner','admin'=>'admin'
            ];
            $role = $map[$norm] ?? null;
        }

        if (!$email)                  { http_response_code(422); echo json_encode(['success'=>false,'message'=>'Email inválido']); return; }
        if (strlen($pass1) < 6)      { http_response_code(422); echo json_encode(['success'=>false,'message'=>'Contraseña mínima 6']); return; }
        if ($pass1 !== $pass2)       { http_response_code(422); echo json_encode(['success'=>false,'message'=>'Las contraseñas no coinciden']); return; }
        if (User::existsByEmail($email)) {
            http_response_code(409); echo json_encode(['success'=>false,'message'=>'El correo ya está registrado']); return;
        }

        // Inserta solo lo necesario
        $id = User::createMinimal($email, $pass1, $role);

        http_response_code(201);
        echo json_encode(['success'=>true,'message'=>'Usuario creado','user_id'=>$id]);
    } catch (\PDOException $e) {
        // Dev útil en pruebas; luego deja solo error_log
        http_response_code(500);
        echo json_encode(['success'=>false,'message'=>'Error BD','dev'=>$e->getMessage()]);
        error_log('Register PDO: '.$e->getMessage());
    } catch (\Throwable $e) {
        http_response_code(500);
        echo json_encode(['success'=>false,'message'=>'Error interno','dev'=>$e->getMessage()]);
        error_log('Register fatal: '.$e->getMessage().' @'.$e->getFile().':'.$e->getLine());
    }
}


}
