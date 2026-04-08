package com.bootcamp.movement.application.messages;

public final class AppMessages {

    private AppMessages() {}

    public static final String DEPOSIT_REGISTERED = "Deposit registered successfully";
    public static final String WITHDRAWAL_REGISTERED = "Withdrawal registered successfully";
    public static final String CREDIT_PAYMENT_REGISTERED = "Credit payment registered successfully";
    public static final String CREDIT_CONSUMPTION_REGISTERED = "Credit consumption registered successfully";

    public static final String MOVEMENTS_RETRIEVED = "Movements retrieved successfully";
    public static final String LAST_10_MOVEMENTS_RETRIEVED = "Last 10 movements retrieved successfully";
    public static final String GENERAL_REPORT_GENERATED = "General report generated successfully";
    public static final String TRANSFER_PROCESSED = "Transfer processed successfully";

    public static final String INVALID_DEPOSIT_AMOUNT = "Invalid deposit amount";
    public static final String INVALID_WITHDRAWAL_AMOUNT = "Invalid withdrawal amount";
    public static final String INVALID_PAYMENT_AMOUNT = "Payment amount must be greater than 0";
    public static final String INVALID_CONSUMPTION_AMOUNT = "Consumption amount must be greater than 0";

    public static final String AMOUNT_TOO_LOW_AFTER_COMMISSION = "Amount is too low after commission";
    public static final String INSUFFICIENT_BALANCE = "Insufficient balance";
    public static final String INSUFFICIENT_BALANCE_WITH_COMMISSION = "Insufficient funds (with commission)";
    public static final String INSUFFICIENT_CREDIT_LIMIT = "Insufficient credit card limit";

    public static final String ONLY_CREDIT_CARD_ALLOWED = "Only credit cards allow consumption";

    public static final String TRANSFER_AMOUNT_INVALID = "Transfer amount must be greater than 0";
}