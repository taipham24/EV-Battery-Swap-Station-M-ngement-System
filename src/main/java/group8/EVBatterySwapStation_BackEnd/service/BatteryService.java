package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BatteryService {
    Battery addBatteryToStation(Long stationId, BatteryStatus status);

    List<Battery> getBatteriesByStation(Long stationId, BatteryStatus status);

    Page<Battery> listBatteries(Long stationId,
                                BatteryStatus status,
                                String model,
                                Integer capacityMin,
                                Integer capacityMax,
                                Pageable pageable);

    Map<String, Object> summary(Long stationId, List<int[]> capacityBuckets);

    Battery updateStatus(Long id, BatteryStatus newStatus, String reason, boolean adminOverride);
}
