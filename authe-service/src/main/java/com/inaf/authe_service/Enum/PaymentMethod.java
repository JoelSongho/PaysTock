package com.inaf.authe_service.Enum;

public enum PaymentMethod {
    CASH("Espèces"),
    CREDIT_CARD("Carte de crédit"),
    DEBIT_CARD("Carte de débit"),
    BANK_TRANSFER("Virement bancaire"),
    MOBILE_PAYMENT("Paiement mobile"),
    CHECK("Chèque");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }


}