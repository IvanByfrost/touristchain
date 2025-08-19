<?php
namespace V01\Touristchain\Core;

class Router
{
    /** @var array<string,array<int,array{path:string,regex:string,handler:mixed}>> */
    private array $routes = [];

    public function get(string $path, callable|array $handler): void   { $this->add('GET', $path, $handler); }
    public function post(string $path, callable|array $handler): void  { $this->add('POST', $path, $handler); }
    public function put(string $path, callable|array $handler): void   { $this->add('PUT', $path, $handler); }
    public function patch(string $path, callable|array $handler): void { $this->add('PATCH', $path, $handler); }
    public function delete(string $path, callable|array $handler): void{ $this->add('DELETE', $path, $handler); }
    public function any(string $path, callable|array $handler): void   {
        foreach (['GET','POST','PUT','PATCH','DELETE','OPTIONS'] as $m) $this->add($m, $path, $handler);
    }

    private function add(string $method, string $path, callable|array $handler): void
    {
        // Normaliza ruta declarada
        $norm = $this->normalizePath($path);

        // Compila {param} -> (?P<param>[^/]+)
        $regex = '#^' . preg_replace('#\{([a-zA-Z_][\w-]*)\}#', '(?P<$1>[^/]+)', $norm) . '$#';

        $this->routes[$method][] = [
            'path'    => $norm,
            'regex'   => $regex,
            'handler' => $handler,
        ];
    }

    public function dispatch(string $uri, string $method)
    {
        $path   = $this->normalizePath(parse_url($uri, PHP_URL_PATH) ?? '/');
        $method = strtoupper($method);

        // HEAD -> trata como GET si no hay HEAD explícito
        if ($method === 'HEAD' && empty($this->routes['HEAD'])) {
            $method = 'GET';
        }

        $allowed = $this->allowedMethodsForPath($path);

        if (!isset($this->routes[$method])) {
            if ($allowed) return $this->methodNotAllowed($allowed);
            return $this->notFound();
        }

        foreach ($this->routes[$method] as $route) {
            if (preg_match($route['regex'], $path, $m)) {
                $params = array_filter($m, fn($k) => !is_int($k), ARRAY_FILTER_USE_KEY);
                return $this->invoke($route['handler'], $params);
            }
        }

        // Si hay otras rutas con el mismo path pero diferente método -> 405
        if ($allowed) return $this->methodNotAllowed($allowed);

        return $this->notFound();
    }

    private function invoke(callable|array $handler, array $params = [])
    {
        try {
            if (is_array($handler)) {
                [$class, $action] = $handler;
                $controller = new $class();
                // soporte a parameters por nombre
                return $controller->{$action}(...$params);
            }
            return $handler(...array_values($params));
        } catch (\Throwable $e) {
            http_response_code(500);
            echo "500 Internal Server Error";
            // En dev puedes loguear: error_log($e);
            return null;
        }
    }

    private function normalizePath(string $p): string
    {
        $p = '/' . ltrim($p, '/');
        // elimina slash final excepto en raíz
        return rtrim($p, '/') ?: '/';
    }

    /** @return string[] */
    private function allowedMethodsForPath(string $path): array
    {
        $methods = [];
        foreach ($this->routes as $method => $items) {
            foreach ($items as $r) {
                if ($r['path'] === $path || preg_match($r['regex'], $path)) {
                    $methods[] = $method;
                }
            }
        }
        return array_values(array_unique($methods));
    }

    private function notFound(): void
    {
        http_response_code(404);
        echo "404 Not Found";
    }

    /** @param string[] $allowed */
    private function methodNotAllowed(array $allowed): void
    {
        http_response_code(405);
        header('Allow: ' . implode(', ', $allowed));
        echo "405 Method Not Allowed";
    }
}
