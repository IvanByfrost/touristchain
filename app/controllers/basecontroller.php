<?php
/**
 * BaseController.php
 */

namespace App\Controllers;

class BaseController
{
    /** @var string Nombre del layout base (sin extensión) */
    protected string $layout = 'main';

    /** @var string Ruta absoluta a la carpeta de vistas */
    protected string $viewsPath;

    /** @var array Variables compartidas con TODAS las vistas */
    protected array $shared = [];

    public function __construct(?string $viewsPath = null)
    {
        // Si este archivo está en app/Controllers, subimos a app/Views
        $this->viewsPath = $viewsPath ?? dirname(__DIR__) . DIRECTORY_SEPARATOR . 'Views';

        // Asegura sesión para flash
        if (session_status() !== PHP_SESSION_ACTIVE) {
            @session_start();
        }

        // Variables compartidas por defecto (puedes añadir más)
        $this->share([
            'appName' => $_ENV['APP_NAME'] ?? 'Byfrost',
        ]);
    }

    /**
     * Renderiza una vista dentro del layout actual.
     * @param string $view Ruta relativa de la vista (sin .php), ej: "users/index"
     * @param array  $data Datos específicos de la vista
     * @param null|string $layout Layout a usar (null = usa $this->layout; '' = sin layout)
     */
    protected function render(string $view, array $data = [], ?string $layout = null): void
    {
        $layoutToUse = $layout === null ? $this->layout : $layout;

        // Variables disponibles en la vista
        $vars = array_merge($this->shared, $data);
        extract($vars, EXTR_OVERWRITE);

        $viewFile = $this->resolveView($view);
        if (!is_file($viewFile)) {
            $this->renderMissingView($viewFile);
            return;
        }

        // Si no hay layout, incluimos solo la vista
        if ($layoutToUse === '') {
            include $viewFile;
            return;
        }

        // Con layout: header + vista + footer
        $header = $this->resolveView("layouts/{$layoutToUse}_header");
        $footer = $this->resolveView("layouts/{$layoutToUse}_footer");

        // Header opcional
        if (is_file($header)) include $header;

        include $viewFile;

        // Footer opcional
        if (is_file($footer)) include $footer;
    }

    /**
     * Renderiza solo un fragmento/partial sin layout.
     * @param string $view
     * @param array $data
     */
    protected function renderPartial(string $view, array $data = []): void
    {
        $vars = array_merge($this->shared, $data);
        extract($vars, EXTR_OVERWRITE);

        $viewFile = $this->resolveView($view);
        if (!is_file($viewFile)) {
            $this->renderMissingView($viewFile);
            return;
        }
        include $viewFile;
    }

    /**
     * Cambia el layout actual (sin extensión). Usa '' para desactivar layout.
     */
    protected function setLayout(string $layout): void
    {
        $this->layout = $layout;
    }

    /**
     * Variables compartidas con todas las vistas.
     */
    protected function share(array $vars): void
    {
        $this->shared = array_merge($this->shared, $vars);
    }

    /**
     * Redirección simple.
     */
    protected function redirect(string $url, int $status = 302): void
    {
        if (!headers_sent()) {
            http_response_code($status);
            header("Location: {$url}");
        } else {
            // Fallback si headers ya enviados
            echo "<script>window.location.href=" . json_encode($url) . ";</script>";
        }
        exit;
    }

    /**
     * Redirige a la URL previa si existe, o a un fallback.
     */
    protected function back(string $fallback = '/'): void
    {
        $target = $_SERVER['HTTP_REFERER'] ?? $fallback;
        $this->redirect($target);
    }

    /**
     * Helpers de request triviales (MVC básico).
     */
    protected function isGet(): bool   { return ($_SERVER['REQUEST_METHOD'] ?? 'GET') === 'GET'; }
    protected function isPost(): bool  { return ($_SERVER['REQUEST_METHOD'] ?? 'GET') === 'POST'; }
    protected function isPut(): bool   { return ($_SERVER['REQUEST_METHOD'] ?? 'GET') === 'PUT'; }
    protected function isDelete(): bool{ return ($_SERVER['REQUEST_METHOD'] ?? 'GET') === 'DELETE'; }

    protected function input(string $key, $default = null)
    {
        if ($this->isPost() && array_key_exists($key, $_POST)) return $_POST[$key];
        if (array_key_exists($key, $_GET)) return $_GET[$key];
        return $default;
    }

    /**
     * Flash messages (sesión).
     */
    protected function setFlash(string $key, $value): void
    {
        $_SESSION['_flash'][$key] = $value;
    }

    protected function getFlash(string $key, $default = null)
    {
        if (!isset($_SESSION['_flash'][$key])) return $default;
        $val = $_SESSION['_flash'][$key];
        unset($_SESSION['_flash'][$key]); // one-time read
        return $val;
    }

    /**
     * Resolución segura de rutas de vistas.
     */
    private function resolveView(string $view): string
    {
        // Normaliza separadores y evita path traversal básico
        $clean = str_replace(['..', '\\'], ['', '/'], $view);
        return rtrim($this->viewsPath, DIRECTORY_SEPARATOR) . DIRECTORY_SEPARATOR . $clean . '.php';
    }

    private function renderMissingView(string $viewFile): void
    {
        http_response_code(500);
        echo "<h1>Vista no encontrada</h1>";
        echo "<p>No se encontró el archivo de vista: <code>{$viewFile}</code></p>";
    }

    // ============================
    // CRUD Skel (para controladores hijos)
    // ============================
    /**
     * Lista recursos.
     * Convención: render('recurso/index', ['items' => $items])
     */
    public function index(): void
    {
        $this->render('shared/empty'); // Los hijos deben sobrescribir
    }

    /**
     * Muestra un recurso por id.
     */
    public function show($id): void
    {
        $this->render('shared/empty');
    }

    /**
     * Crea un recurso (GET muestra formulario, POST procesa).
     */
    public function create(): void
    {
        if ($this->isPost()) {
            // Los hijos implementan guardado y redirección
            $this->setFlash('error', 'No implementado.');
            $this->back();
            return;
        }
        $this->render('shared/empty_form');
    }

    /**
     * Actualiza un recurso (GET muestra formulario, POST procesa).
     */
    public function update($id): void
    {
        if ($this->isPost()) {
            $this->setFlash('error', 'No implementado.');
            $this->back();
            return;
        }
        $this->render('shared/empty_form');
    }

    /**
     * Elimina un recurso.
     */
    public function delete($id): void
    {
        $this->setFlash('error', 'No implementado.');
        $this->back();
    }
}