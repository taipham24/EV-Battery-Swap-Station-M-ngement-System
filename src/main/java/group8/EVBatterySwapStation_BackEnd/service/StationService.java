package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.response.StationInfoResponse;

import java.util.List;

public interface StationService {
    List<StationInfoResponse> findNearestStations(double lat, double lon, double radiusKm);

    double distance(double lat1, double lon1, double lat2, double lon2);
}
