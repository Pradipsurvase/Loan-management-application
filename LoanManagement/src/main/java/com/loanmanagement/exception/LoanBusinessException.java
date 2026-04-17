package com.loanmanagement.exception;

public class LoanBusinessException extends RuntimeException{
    public LoanBusinessException(String message){
        super(message);
    }
}
