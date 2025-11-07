package com.automobilesystem.automobile.Exceptions;

public class UserIdNotFoundException extends  RuntimeException{

    public UserIdNotFoundException(){
        super("UserID not found ");

    }
    public UserIdNotFoundException(String message) {
        super(message);

    }
}
