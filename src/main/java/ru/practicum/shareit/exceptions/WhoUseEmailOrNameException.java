package ru.practicum.shareit.exceptions;

public class WhoUseEmailOrNameException extends RuntimeException{
    public WhoUseEmailOrNameException(String message) {
        super(message);
    }

}
