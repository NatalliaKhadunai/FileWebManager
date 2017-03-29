package com.epam.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

public class FileDuplicateException extends IOException {
    public FileDuplicateException() {
        super();
    }
    public FileDuplicateException(String message) {
        super(message);
    }
}
