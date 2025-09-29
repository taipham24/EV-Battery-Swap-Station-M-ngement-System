package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.response.StationInfoResponse;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.repository.BatteryRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StationRepository;
import group8.EVBatterySwapStation_BackEnd.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StationImpl implements StationService {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private BatteryRepository batteryRepository;

    @Override
    public List<StationInfoResponse> findNearestStations(double lat, double lon, double radiusKm) {
        List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .filter(s -> distance(lat, lon, s.getLatitude(), s.getLongitude()) <= radiusKm)
                .map(s -> {
                    long available = batteryRepository.findByStationAndStatus(s, BatteryStatus.FULL).size();
                    return new StationInfoResponse(
                            s.getStationId(),
                            s.getName(),
                            s.getAddress(),
                            s.getLatitude(),
                            s.getLongitude(),
                            s.getCapacity(),
                            s.getStatus(),
                            available
                    );
                })
                .sorted(Comparator.comparingDouble(s -> distance(lat, lon, s.getLatitude(), s.getLongitude())))
                .toList();
    }
    @Override
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // convert to kilometers
    }

}
