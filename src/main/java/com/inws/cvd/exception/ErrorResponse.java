package com.inws.cvd.exception;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {

    private List<String> errors;

    public ErrorResponse(String error) {
        this.errors = List.of(error);
    }

}
