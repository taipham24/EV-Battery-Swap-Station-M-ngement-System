package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.entity.GlobalPricing;
import group8.EVBatterySwapStation_BackEnd.repository.GlobalPricingRepository;
import group8.EVBatterySwapStation_BackEnd.service.GlobalPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GlobalPricingImpl implements GlobalPricingService {
    @Autowired
    private final GlobalPricingRepository repository;

    @Override
    public double getPricePerSwap() {
        return repository.findTopByOrderByIdDesc()
                .map(GlobalPricing::getPricePerSwap)
                .orElse(100000.0); // default price if not set
    }

    @Override
    public GlobalPricing updatePrice(double newPrice) {
        GlobalPricing pricing = repository.findTopByOrderByIdDesc()
                .orElse(new GlobalPricing());
        pricing.setPricePerSwap(newPrice);
        return repository.save(pricing);
    }
}
