<?php
namespace V01\Touristchain\Core;

class Router
{
    private $routes = [];

    public function get(string $path, callable|array $handler)
    {
        $this->routes['GET'][$path] = $handler;
    }

    public function dispatch(string $uri, string $method)
    {
        $path = parse_url($uri, PHP_URL_PATH);

        if (isset($this->routes[$method][$path])) {
            $handler = $this->routes[$method][$path];

            if (is_array($handler)) {
                [$class, $action] = $handler;
                $controller = new $class();
                return $controller->$action();
            }

            return call_user_func($handler);
        }

        http_response_code(404);
        echo "404 Not Found";
    }
}
