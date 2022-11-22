package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING("WAITING"), APPROVED("APPROVED"),
    REJECTED("REJECTED"), CANCELED("CANCELED");

    private final String statusName;

    BookingStatus(String statusName) {
        this.statusName = statusName;
    }


}
