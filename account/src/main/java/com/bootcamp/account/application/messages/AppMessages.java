package com.bootcamp.account.application.messages;

public final class AppMessages {

    private AppMessages() {}

    public static final String ACCOUNT_CREATED = "Account created successfully";
    public static final String ACCOUNT_RETRIEVED = "Accounts retrieved successfully";
    public static final String ACCOUNT_FOUND = "Account found successfully";
    public static final String ACCOUNT_UPDATED = "Account updated successfully";
    public static final String ACCOUNT_DELETED = "Account deleted successfully";

    public static final String ACCOUNT_NOT_FOUND = "Account not found";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds";
    public static final String UNKNOWN_CUSTOMER_TYPE = "Unknown customer type";

    public static final String PERSONAL_SAVINGS_LIMIT = "A personal customer can only have one savings account";
    public static final String PERSONAL_CURRENT_LIMIT = "A personal customer can only have one current account";
    public static final String BUSINESS_SAVINGS_FORBIDDEN = "A business customer cannot have savings accounts";
    public static final String BUSINESS_FIXED_TERM_FORBIDDEN = "A business customer cannot have fixed term accounts";

    public static final String VIP_ONLY_PERSONAL = "VIP applies only to PERSONAL customers";
    public static final String VIP_SAVINGS_REQUIRED = "VIP account requires SAVINGS type";
    public static final String VIP_NEEDS_CREDIT_CARD = "VIP customer requires an active credit card";

    public static final String PYME_ONLY_BUSINESS = "PYME applies only to BUSINESS customers";
    public static final String PYME_CURRENT_REQUIRED = "PYME account requires CURRENT type";
    public static final String PYME_NEEDS_CREDIT_CARD = "PYME customer requires an active credit card";

    public static final String MOVEMENT_APPLIED = "Balance updated";
}