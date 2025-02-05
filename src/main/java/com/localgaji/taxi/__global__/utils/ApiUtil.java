package com.localgaji.taxi.__global__.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ApiUtil {
    public static <T> Response<T> success(T responseBody) {
        return new Response<>(responseBody, null);
    }

    public static Response<String> fail(int internalCode) {
        return new Response<>(null, new Error(internalCode));
    }

    @AllArgsConstructor @Getter
    public static class Response<T> {
        T response;
        Error error;
    }

    @AllArgsConstructor @Getter
    public static class Error {
        int errorCode;
    }
}
