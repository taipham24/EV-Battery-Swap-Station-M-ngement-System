package group8.EVBatterySwapStation_BackEnd.enums;

import lombok.Getter;


@Getter
public enum Priority {
    LOW(72),
    NORMAL(48),
    HIGH(24),
    URGENT(12);

    private final int slaHours;

    Priority(int slaHours) {
        this.slaHours = slaHours;
    }

}
