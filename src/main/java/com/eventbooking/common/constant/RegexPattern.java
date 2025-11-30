package com.eventbooking.common.constant;

public final class RegexPattern {
    public static final String ISO_DATE =  "^\\d{4}-\\d{2}-\\d{2}(?:[Tt ]\\d{2}:\\d{2}(?::\\d{2})?(?:Z|[+-]\\d{2}:\\d{2})?)?$";
    public static final String CARD_NUMBER = "\\d{16}";
    public static final String CVV = "\\d{3,4}";
    public static final String CARD_EXPIRER = "(0[1-9]|1[0-2])/(\\d{2})";

    private RegexPattern() {}
}

