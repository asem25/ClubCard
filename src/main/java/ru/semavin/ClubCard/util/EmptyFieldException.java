package ru.semavin.ClubCard.util;

public class EmptyFieldException extends RuntimeException{
    public EmptyFieldException(String message) {
        super(message);
    }
}
