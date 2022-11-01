package ru.practicum.shareit.exceptions;

public class AnybodyUseEmailException extends RuntimeException {
    public AnybodyUseEmailException(String message) {
        super(message);
    }
}
