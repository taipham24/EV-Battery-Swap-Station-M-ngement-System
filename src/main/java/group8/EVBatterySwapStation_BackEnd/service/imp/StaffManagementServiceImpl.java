package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.StaffAssignmentRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.StaffFilterRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StaffDetailResponse;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.RoleDetail;
import group8.EVBatterySwapStation_BackEnd.entity.StaffProfile;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import group8.EVBatterySwapStation_BackEnd.enums.Role;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.RoleRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StaffProfileRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StationRepository;
import group8.EVBatterySwapStation_BackEnd.service.StaffManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffManagementServiceImpl implements StaffManagementService {

    private final StaffProfileRepository staffProfileRepository;
    private final DriverRepository driverRepository;
    private final StationRepository stationRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public StaffDetailResponse assignStaffToStation(StaffAssignmentRequest request) {
        log.info("Assigning staff {} to station {}", request.getDriverId(), request.getStationId());
        
        // Validate driver exists and has STAFF role
        Driver driver = driverRepository.findById(request.getDriverId())
            .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));
        
        boolean hasStaffRole = driver.getRoles().stream()
            .anyMatch(role -> Role.STAFF.name().equals(role.getRoleType()));
        
        if (!hasStaffRole) {
            throw new AppException(ErrorCode.INVALID_ROLE);
        }
        
        // Validate station exists
        Station station = stationRepository.findById(request.getStationId())
            .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_FOUND));
        
        // Check if staff is already assigned to a station
        Optional<StaffProfile> existingAssignment = staffProfileRepository.findByDriver_DriverId(request.getDriverId());
        if (existingAssignment.isPresent()) {
            throw new AppException(ErrorCode.STAFF_ALREADY_ASSIGNED);
        }
        
        // Create new staff profile
        StaffProfile staffProfile = StaffProfile.builder()
            .driver(driver)
            .station(station)
            .workShift(request.getWorkShift())
            .assignedDate(LocalDateTime.now())
            .active(true)
            .notes(request.getNotes())
            .build();
        
        StaffProfile savedProfile = staffProfileRepository.save(staffProfile);
        
        log.info("Staff {} assigned to station {} successfully", request.getDriverId(), request.getStationId());
        return mapToStaffDetailResponse(savedProfile);
    }

    @Override
    @Transactional
    public StaffDetailResponse updateStaffAssignment(Long staffId, StaffAssignmentRequest request) {
        log.info("Updating staff assignment {} with data: {}", staffId, request);
        
        StaffProfile staffProfile = staffProfileRepository.findById(staffId)
            .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        
        // Validate station if changed
        if (!staffProfile.getStation().getStationId().equals(request.getStationId())) {
            Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_FOUND));
            staffProfile.setStation(station);
        }
        
        staffProfile.setWorkShift(request.getWorkShift());
        staffProfile.setNotes(request.getNotes());
        
        StaffProfile savedProfile = staffProfileRepository.save(staffProfile);
        
        log.info("Staff assignment {} updated successfully", staffId);
        return mapToStaffDetailResponse(savedProfile);
    }

    @Override
    @Transactional
    public void removeStaffFromStation(Long staffId) {
        log.info("Removing staff {} from station", staffId);
        
        StaffProfile staffProfile = staffProfileRepository.findById(staffId)
            .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        
        staffProfile.setActive(false);
        staffProfileRepository.save(staffProfile);
        
        log.info("Staff {} removed from station successfully", staffId);
    }

    @Override
    public Page<StaffDetailResponse> getAllStaff(StaffFilterRequest filter) {
        log.info("Getting staff with filter: {}", filter);
        
        // Create pageable
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortDirection()) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), 
            Sort.by(direction, filter.getSortBy()));
        
        Page<StaffProfile> staffProfiles = staffProfileRepository.findWithFilters(
            filter.getStationId(),
            filter.getActive(),
            filter.getWorkShift(),
            pageable
        );
        
        return staffProfiles.map(this::mapToStaffDetailResponse);
    }

    @Override
    public StaffDetailResponse getStaffDetail(Long staffId) {
        log.info("Getting staff detail for ID: {}", staffId);
        
        StaffProfile staffProfile = staffProfileRepository.findById(staffId)
            .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        
        return mapToStaffDetailResponse(staffProfile);
    }

    @Override
    public List<StaffDetailResponse> getStationStaff(Long stationId) {
        log.info("Getting staff for station: {}", stationId);
        
        List<StaffProfile> staffProfiles = staffProfileRepository.findByStation_StationIdAndActiveTrue(stationId);
        
        return staffProfiles.stream()
            .map(this::mapToStaffDetailResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getStaffStatistics() {
        log.info("Getting staff statistics");
        
        List<StaffProfile> allStaff = staffProfileRepository.findAll();
        
        long totalStaff = allStaff.size();
        long activeStaff = allStaff.stream()
            .mapToLong(staff -> staff.isActive() ? 1 : 0)
            .sum();
        
        // Count by station
        Map<Long, Long> staffByStation = allStaff.stream()
            .filter(StaffProfile::isActive)
            .collect(Collectors.groupingBy(
                staff -> staff.getStation().getStationId(),
                Collectors.counting()
            ));
        
        // Count by work shift
        Map<String, Long> staffByShift = allStaff.stream()
            .filter(StaffProfile::isActive)
            .collect(Collectors.groupingBy(
                StaffProfile::getWorkShift,
                Collectors.counting()
            ));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStaff", totalStaff);
        stats.put("activeStaff", activeStaff);
        stats.put("staffByStation", staffByStation);
        stats.put("staffByShift", staffByShift);
        
        return stats;
    }

    private StaffDetailResponse mapToStaffDetailResponse(StaffProfile staffProfile) {
        Driver driver = staffProfile.getDriver();
        
        return StaffDetailResponse.builder()
            .staffId(staffProfile.getStaffId())
            .driverId(driver.getDriverId())
            .userName(driver.getUserName())
            .fullName(driver.getFullName())
            .email(driver.getEmail())
            .stationId(staffProfile.getStation().getStationId())
            .stationName(staffProfile.getStation().getName())
            .workShift(staffProfile.getWorkShift())
            .assignedDate(staffProfile.getAssignedDate())
            .active(staffProfile.isActive())
            .notes(staffProfile.getNotes())
            .build();
    }
}
