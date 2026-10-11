package com.touristchain.common;

/** El recurso pedido no existe; se responde con 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
