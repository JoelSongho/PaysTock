package com.inaf.authe_service.Enum;

public enum SaleStatus {
    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    SHIPPED("Expédiée"),
    DELIVERED("Livrée"),
    CANCELLED("Annulée"),
    REFUNDED("Remboursée");

    private final String displayName;

    SaleStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
