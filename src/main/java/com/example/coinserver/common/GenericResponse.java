package com.example.coinserver.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenericResponse<T> {

    private int statusCode;

    private String message;

    private T data;

    public GenericResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public GenericResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public GenericResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public GenericResponse(int statusCode, T data) {
        this.statusCode = statusCode;
        this.data = data;
    }
}
