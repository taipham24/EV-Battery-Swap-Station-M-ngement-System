package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.BatteryRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StationRepository;
import group8.EVBatterySwapStation_BackEnd.service.BatteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatteryImpl implements BatteryService {
    @Autowired
    private final StationRepository stationRepository;
    @Autowired
    private BatteryRepository batteryRepository;

    @Override
    public Battery addBatteryToStation(Long stationId, BatteryStatus status) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        if (station.getCapacity() <= station.getBatteries().size()) {
            throw new AppException(ErrorCode.STATION_FULL);
        }
        Battery battery = Battery.builder()
                .serialNumber("BAT-" + System.currentTimeMillis()) // Giả sử serial number là timestamp
                .status(status)
                .station(station)
                .build();
        station.getBatteries().add(battery);
        return batteryRepository.save(battery);
    }
}
