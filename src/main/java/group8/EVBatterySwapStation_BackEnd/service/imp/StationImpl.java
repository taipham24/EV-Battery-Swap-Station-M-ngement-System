package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.StationRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StationDetailResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StationInfoResponse;
import group8.EVBatterySwapStation_BackEnd.entity.*;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.enums.SwapStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.*;
import group8.EVBatterySwapStation_BackEnd.service.FirebaseStorageService;
import group8.EVBatterySwapStation_BackEnd.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationImpl implements StationService {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private BatteryRepository batteryRepository;
    @Autowired
    private StaffProfileRepository staffProfileRepository;
    @Autowired
    private SwapTransactionRepository swapTransactionRepository;
    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Override
    public Station createStation(StationRequest request, MultipartFile image) throws IOException {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = firebaseStorageService.uploadFile(image);
        }
        Station station = Station.builder()
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .capacity(request.getCapacity())
                .status(request.getStatus())
                .build();
        return stationRepository.save(station);
    }

    @Override
    @Transactional
    public Station updateStation(Long id, StationRequest request,MultipartFile image) throws IOException {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = firebaseStorageService.uploadFile(image);
        }
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_EXISTED));

        station.setName(request.getName());
        station.setAddress(request.getAddress());
        station.setLatitude(request.getLatitude());
        station.setLongitude(request.getLongitude());
        station.setCapacity(request.getCapacity());
        station.setStatus(request.getStatus());

        return stationRepository.save(station);
    }

    @Override
    public StationDetailResponse getStationDetail(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_EXISTED));

        // Get battery inventory breakdown
        List<Battery> batteries = batteryRepository.findByStation_StationId(id);
        Map<BatteryStatus, Long> batteryInventory = batteries.stream()
                .collect(Collectors.groupingBy(Battery::getStatus, Collectors.counting()));

        long totalBatteries = batteries.size();
        long availableBatteries = batteryInventory.getOrDefault(BatteryStatus.AVAILABLE, 0L) +
                batteryInventory.getOrDefault(BatteryStatus.FULL, 0L);

        // Get assigned staff
        List<StaffProfile> staffProfiles = staffProfileRepository.findByStation_StationId(id);
        List<StationDetailResponse.StaffInfo> assignedStaff = staffProfiles.stream()
                .map(staff -> StationDetailResponse.StaffInfo.builder()
                        .staffId(staff.getStaffId())
                        .staffName(staff.getDriver().getFullName())
                        .workShift(staff.getWorkShift())
                        .build())
                .collect(Collectors.toList());

        // Get recent swap activity
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekAgo = today.minusDays(7);
        LocalDateTime monthAgo = today.minusDays(30);

        long swapsToday = swapTransactionRepository.countByStationAndCreatedAtAfter(station, today);
        long swapsThisWeek = swapTransactionRepository.countByStationAndCreatedAtAfter(station, weekAgo);
        long swapsThisMonth = swapTransactionRepository.countByStationAndCreatedAtAfter(station, monthAgo);

        StationDetailResponse.SwapActivitySummary recentActivity = StationDetailResponse.SwapActivitySummary.builder()
                .swapsToday(swapsToday)
                .swapsThisWeek(swapsThisWeek)
                .swapsThisMonth(swapsThisMonth)
                .averageSwapTimeMinutes(0.0) // TODO: Calculate from completed swaps
                .build();

        return StationDetailResponse.builder()
                .stationId(station.getStationId())
                .name(station.getName())
                .address(station.getAddress())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .capacity(station.getCapacity())
                .status(station.getStatus())
                .imageUrl(station.getImageUrl())
                .batteryInventory(batteryInventory)
                .totalBatteries(totalBatteries)
                .availableBatteries(availableBatteries)
                .assignedStaff(assignedStaff)
                .recentActivity(recentActivity)
                .build();
    }

    @Override
    public Page<Station> getAllStations(Pageable pageable) {
        return stationRepository.findAll(pageable);
    }

    @Override
    public List<Station> searchStations(String keyword) {
        List<Station> stations = stationRepository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(keyword, keyword);
        return stations.stream()
                .sorted(Comparator.comparing(Station::getName))
                .toList();
    }

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
                            s.getImageUrl(),
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

    @Override
    @Transactional
    public void deleteStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_EXISTED));

        // Check if station has batteries
        long batteryCount = batteryRepository.countByStation_StationId(id);
        if (batteryCount > 0) {
            throw new AppException(ErrorCode.CANNOT_DELETE_STATION_WITH_BATTERIES);
        }

        // Check if station has active swaps
        long activeSwapCount = swapTransactionRepository.countByStationAndStatusIn(
                station, Arrays.asList(SwapStatus.CONFIRMED, SwapStatus.PAID, SwapStatus.INSPECTED));
        if (activeSwapCount > 0) {
            throw new AppException(ErrorCode.CANNOT_DELETE_STATION_WITH_ACTIVE_SWAPS);
        }

        stationRepository.deleteById(id);
    }
}
