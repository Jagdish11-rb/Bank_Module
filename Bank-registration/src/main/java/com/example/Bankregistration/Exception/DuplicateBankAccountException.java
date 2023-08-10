package com.example.Bankregistration.Exception;

public class DuplicateBankAccountException extends RuntimeException {
    public DuplicateBankAccountException(String message){
        super(message);
    }
}
