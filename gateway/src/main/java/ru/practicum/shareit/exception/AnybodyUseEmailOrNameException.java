package ru.practicum.shareit.exception;

public class AnybodyUseEmailOrNameException extends RuntimeException {
    public AnybodyUseEmailOrNameException(String message) {
        super(message);
    }
}
