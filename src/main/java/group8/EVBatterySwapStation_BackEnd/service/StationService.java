package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.StationRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StationDetailResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StationInfoResponse;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StationService {

    Station createStation(StationRequest request, MultipartFile image) throws IOException;

    @Transactional
    Station updateStation(Long id, StationRequest request, MultipartFile image) throws IOException;

    StationDetailResponse getStationDetail(Long id);

    Page<Station> getAllStations(Pageable pageable);

    List<Station> searchStations(String keyword);

    List<StationInfoResponse> findNearestStations(double lat, double lon, double radiusKm);

    double distance(double lat1, double lon1, double lat2, double lon2);

    void deleteStation(Long id);
}
