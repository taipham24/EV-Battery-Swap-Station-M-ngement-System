package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.BatteryRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StationRepository;
import group8.EVBatterySwapStation_BackEnd.service.BatteryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BatteryServiceImp implements BatteryService {

    private final BatteryRepository batteryRepository;
    private final StationRepository stationRepository;

    @Override
    public Battery addBatteryToStation(Long stationId, BatteryStatus status) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_EXISTED));
        Battery battery = Battery.builder()
                .station(station)
                .status(status)
                .serialNumber(UUID.randomUUID().toString())
                .capacityWh(1000)
                .model("UNKNOWN")
                .build();
        return batteryRepository.save(battery);
    }

    @Override
    public List<Battery> getBatteriesByStation(Long stationId, BatteryStatus status) {
        if (status == null) return batteryRepository.findByStation_StationId(stationId);
        return batteryRepository.findByStation_StationIdAndStatus(stationId, status);
    }

    @Override
    public Page<Battery> listBatteries(Long stationId,
                                       BatteryStatus status,
                                       String model,
                                       Integer capacityMin,
                                       Integer capacityMax,
                                       Pageable pageable) {
        if (capacityMin != null && capacityMax != null && capacityMin > capacityMax) {
            throw new AppException(ErrorCode.INVALID_QUERY);
        }
        Specification<Battery> spec = Specification.allOf();
        if (stationId != null) spec = spec.and((root, q, cb) -> cb.equal(root.get("station").get("stationId"), stationId));
        if (status != null) spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        if (model != null && !model.isBlank()) spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase() + "%"));
        if (capacityMin != null) spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("capacityWh"), capacityMin));
        if (capacityMax != null) spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("capacityWh"), capacityMax));
        return batteryRepository.findAll(spec, pageable);
    }

    @Override
    public Map<String, Object> summary(Long stationId, List<int[]> capacityBuckets) {
        Specification<Battery> base = Specification.allOf();
        if (stationId != null) base = base.and((root, q, cb) -> cb.equal(root.get("station").get("stationId"), stationId));
        List<Battery> batteries = batteryRepository.findAll(base);
        Map<BatteryStatus, Long> byStatus = batteries.stream().collect(java.util.stream.Collectors.groupingBy(Battery::getStatus, java.util.stream.Collectors.counting()));
        Map<String, Long> byModel = batteries.stream().collect(java.util.stream.Collectors.groupingBy(Battery::getModel, java.util.stream.Collectors.counting()));
        List<Map<String, Object>> byCapacityBucket = new ArrayList<>();
        for (int[] bucket : capacityBuckets) {
            int start = bucket[0], end = bucket[1];
            long count = batteries.stream().filter(b -> b.getCapacityWh() >= start && b.getCapacityWh() <= end).count();
            Map<String, Object> row = new HashMap<>();
            row.put("range", start + "-" + end);
            row.put("count", count);
            byCapacityBucket.add(row);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("byStatus", byStatus.entrySet().stream().map(e -> Map.of("status", e.getKey(), "count", e.getValue())).toList());
        result.put("byModel", byModel.entrySet().stream().map(e -> Map.of("model", e.getKey(), "count", e.getValue())).toList());
        result.put("byCapacityBucket", byCapacityBucket);
        return result;
    }

    @Transactional
    @Override
    public Battery updateStatus(Long id, BatteryStatus newStatus, String reason, boolean adminOverride) {
        Battery battery = batteryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BATTERY_NOT_EXISTED));
        BatteryStatus current = battery.getStatus();
        if (!isAllowedTransition(current, newStatus)) {
            if (!(adminOverride)) throw new AppException(ErrorCode.INVALID_TRANSITION);
            // admin override allowed, proceed; in real system we would audit reason
        }
        // BR-002: MAINTENANCE cannot be reserved or assigned; enforced in booking flows
        battery.setStatus(newStatus);
        return batteryRepository.save(battery);
    }

    private boolean isAllowedTransition(BatteryStatus from, BatteryStatus to) {
        if (from == null || to == null) return false;
        EnumMap<BatteryStatus, Set<BatteryStatus>> matrix = new EnumMap<>(BatteryStatus.class);
        matrix.put(BatteryStatus.FULL, Set.of(BatteryStatus.RESERVED, BatteryStatus.CHARGING, BatteryStatus.AVAILABLE, BatteryStatus.MAINTENANCE, BatteryStatus.DAMAGED));
        matrix.put(BatteryStatus.AVAILABLE, Set.of(BatteryStatus.RESERVED, BatteryStatus.CHARGING, BatteryStatus.MAINTENANCE, BatteryStatus.DAMAGED));
        matrix.put(BatteryStatus.CHARGING, Set.of(BatteryStatus.FULL, BatteryStatus.AVAILABLE, BatteryStatus.MAINTENANCE, BatteryStatus.DAMAGED));
        matrix.put(BatteryStatus.RESERVED, Set.of(BatteryStatus.AVAILABLE, BatteryStatus.FULL, BatteryStatus.DAMAGED));
        matrix.put(BatteryStatus.MAINTENANCE, Set.of(BatteryStatus.AVAILABLE, BatteryStatus.DAMAGED));
        matrix.put(BatteryStatus.DAMAGED, Set.of(BatteryStatus.MAINTENANCE));
        matrix.put(BatteryStatus.FULLY_CHARGED, Set.of(BatteryStatus.AVAILABLE, BatteryStatus.RESERVED, BatteryStatus.MAINTENANCE, BatteryStatus.DAMAGED));
        return matrix.getOrDefault(from, Collections.emptySet()).contains(to);
    }
}


