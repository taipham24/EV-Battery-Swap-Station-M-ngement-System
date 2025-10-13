package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.StationRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StationDetailResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StationInfoResponse;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StationService {
    Station createStation(StationRequest request);

    Station updateStation(Long id, StationRequest request);

    StationDetailResponse getStationDetail(Long id);

    Page<Station> getAllStations(Pageable pageable);

    List<Station> searchStations(String keyword);

    List<StationInfoResponse> findNearestStations(double lat, double lon, double radiusKm);

    double distance(double lat1, double lon1, double lat2, double lon2);

    void deleteStation(Long id);
}
