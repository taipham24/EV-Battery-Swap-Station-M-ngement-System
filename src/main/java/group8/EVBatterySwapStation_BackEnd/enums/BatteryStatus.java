package group8.EVBatterySwapStation_BackEnd.enums;

public enum BatteryStatus {
    // Legacy statuses used in existing flows
    FULL,
    EMPTY,
    CHARGING,
    RESERVED,

    // Extended statuses for inventory management
    FULLY_CHARGED,
    AVAILABLE,
    IN_USE,
    MAINTENANCE,
    DAMAGED,
    QUARANTINED
}
