package ru.practicum.shareit.exceptions;

public class AnybodyUseEmailOrNameException extends RuntimeException {
    public AnybodyUseEmailOrNameException(String message) {
        super(message);
    }
}
