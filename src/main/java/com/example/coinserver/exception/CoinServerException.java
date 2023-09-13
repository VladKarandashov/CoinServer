package com.example.coinserver.exception;

import lombok.Getter;

@Getter
public class CoinServerException extends RuntimeException {

    private final int statusCode;

    public CoinServerException() {
        this.statusCode = 1000;
    }

    public CoinServerException(int statusCode) {
        this.statusCode = statusCode;
    }

    public CoinServerException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public CoinServerException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public CoinServerException(int statusCode, Throwable cause) {
        super(cause);
        this.statusCode = statusCode;
    }
}
