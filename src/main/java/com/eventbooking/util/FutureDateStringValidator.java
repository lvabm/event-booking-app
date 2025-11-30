package com.eventbooking.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class FutureDateStringValidator implements ConstraintValidator<FutureDateString, String> {

    private Pattern pattern;

    @Override
    public void initialize(FutureDateString constraintAnnotation) {
        this.pattern = Pattern.compile(constraintAnnotation.regexp());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        String combinedMessage = "";
        LocalDateTime dateTime = null;

        // Kiểm tra regex
        if (!pattern.matcher(value).matches()) {
            combinedMessage = "Required a ISO pattern";
        } else {
            try {
                if (value.contains("T") || value.contains("t") || value.contains(" ")) {
                    // parse ngày + giờ
                    dateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
                } else {
                    // chỉ ngày
                    dateTime = LocalDate.parse(value, DateTimeFormatter.ISO_DATE)
                            .atStartOfDay();
                }
            } catch (DateTimeParseException e) {
                combinedMessage = "Required a ISO pattern";
            }
        }

        // Kiểm tra future
        if (dateTime != null && !dateTime.isAfter(LocalDateTime.now())) {
            combinedMessage = "Date & Time must be in the future";
        }

        if (!combinedMessage.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(combinedMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
