package ru.semavin.ClubCard.util;

public class ClubMemberNotFoundException extends RuntimeException{
    public ClubMemberNotFoundException(String message) {
        super(message);
    }
}
