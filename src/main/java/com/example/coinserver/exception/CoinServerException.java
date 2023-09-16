package com.example.coinserver.exception;

import lombok.Getter;

@Getter
public class CoinServerException extends RuntimeException {

    private final int statusCode;

    public CoinServerException(ExceptionBasis basis) {
        super(basis.name());
        this.statusCode = basis.getStatusCode();
    }
}
