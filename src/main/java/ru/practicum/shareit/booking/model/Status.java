package ru.practicum.shareit.booking.model;

public enum Status {
    WAITING("WAITING"), APPROVED("APPROVED"), REJECTED("REJECTED"), CANCELED("CANCELED");

    private final String statusName;

    Status(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

}
