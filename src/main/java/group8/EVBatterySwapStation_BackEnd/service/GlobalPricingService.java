package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.entity.GlobalPricing;

public interface GlobalPricingService {
    double getPricePerSwap();

    GlobalPricing updatePrice(double newPrice);
}
