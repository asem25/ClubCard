package ru.semavin.ClubCard.util;

public class ClubMemberEmailAlreadyUsed extends RuntimeException{
    public ClubMemberEmailAlreadyUsed(String message) {
        super(message);
    }
}
