package ru.practicum.shareit.valid;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, BookItemRequestDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookItemRequestDto bookItemRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookItemRequestDto.getStart();
        LocalDateTime end = bookItemRequestDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        if (start.isBefore(LocalDateTime.now()) || start.isEqual(LocalDateTime.now())) return false;
        if (end.isBefore(LocalDateTime.now())) return false;
        return start.isBefore(end);
    }
}
