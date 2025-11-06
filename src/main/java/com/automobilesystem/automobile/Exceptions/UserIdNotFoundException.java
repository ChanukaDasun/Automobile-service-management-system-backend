package com.automobilesystem.automobile.Exceptions;

public class UserIdNotFoundException extends  Exception{

    public UserIdNotFoundException(){
        super("UserID not found ");

    }
    public UserIdNotFoundException(String message) {
        super(message);

    }
}
