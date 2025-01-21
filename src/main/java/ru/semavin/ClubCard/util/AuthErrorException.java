package ru.semavin.ClubCard.util;

public class AuthErrorException extends RuntimeException{
    public AuthErrorException(String message) {
        super(message);
    }
}
