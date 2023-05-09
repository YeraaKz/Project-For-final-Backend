package com.example.demo.exceptions;

import java.util.function.Supplier;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message){
        super(message);
    }

}
