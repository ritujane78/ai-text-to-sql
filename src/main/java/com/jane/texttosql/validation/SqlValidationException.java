package com.jane.texttosql.validation;

public class SqlValidationException extends RuntimeException {
    public SqlValidationException(String message) {
        super(message);
    }
}
