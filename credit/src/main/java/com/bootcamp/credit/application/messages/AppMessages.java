package com.bootcamp.credit.application.messages;

public final class AppMessages {

    private AppMessages() {}

    public static final String CREDIT_CREATED = "Credit created successfully";
    public static final String CREDIT_FOUND = "Credit found successfully";
    public static final String CREDITS_RETRIEVED = "Credits retrieved successfully";
    public static final String CREDITS_BY_CUSTOMER_RETRIEVED = "Credits by customer retrieved successfully";
    public static final String CREDIT_UPDATED = "Credit updated successfully";
    public static final String CREDIT_DELETED = "Credit deleted successfully";

    public static final String CREDIT_NOT_FOUND = "Credit not found";
    public static final String UNSUPPORTED_CREDIT_TYPE = "Unsupported credit type";
    public static final String PERSONAL_CREDIT_ONLY_PERSONAL = "Only personal customers can have personal credits";
    public static final String PERSONAL_CREDIT_EXISTS = "Customer already has a personal credit";
    public static final String BUSINESS_CREDIT_ONLY_BUSINESS = "Only business customers can have business credits";
    public static final String CREDIT_TYPE_CANNOT_CHANGE = "Cannot change credit type";

    public static final String CREDIT_CARD_QUERY = "Credit card existence checked";
}