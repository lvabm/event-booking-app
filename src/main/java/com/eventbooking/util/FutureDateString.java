package com.eventbooking.util;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureDateStringValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureDateString {
    String regexp();
    String message () default "Invalid format date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
