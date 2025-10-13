package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.StaffAssignmentRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.StaffFilterRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StaffDetailResponse;
import group8.EVBatterySwapStation_BackEnd.entity.*;
import group8.EVBatterySwapStation_BackEnd.enums.Role;
import group8.EVBatterySwapStation_BackEnd.enums.StationStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.RoleRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StaffProfileRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffManagementServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private StaffProfileRepository staffProfileRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private StaffManagementServiceImpl staffManagementService;

    private Driver testDriver;
    private Station testStation;
    private StaffProfile testStaffProfile;
    private RoleDetail staffRole;
    private StaffAssignmentRequest assignmentRequest;
    private StaffFilterRequest filterRequest;

    @BeforeEach
    void setUp() {
        // Setup test driver
        testDriver = new Driver();
        testDriver.setDriverId(1L);
        testDriver.setUserName("staffuser");
        testDriver.setEmail("staff@example.com");
        testDriver.setFullName("Staff User");
        testDriver.setStatus(true);
        testDriver.setDeleted(false);

        // Setup staff role
        staffRole = new RoleDetail();
        staffRole.setUserRoleId(2L);
        staffRole.setRoleType("STAFF");

        testDriver.setRoles(Set.of(staffRole));

        // Setup test station
        testStation = new Station();
        testStation.setStationId(1L);
        testStation.setName("Test Station");
        testStation.setAddress("Test Address");
        testStation.setStatus(StationStatus.ACTIVE);
        testStation.setCapacity(50);

        // Setup test staff profile
        testStaffProfile = new StaffProfile();
        testStaffProfile.setStaffId(1L);
        testStaffProfile.setDriver(testDriver);
        testStaffProfile.setStation(testStation);
        testStaffProfile.setWorkShift("Morning");
        testStaffProfile.setActive(true);
        testStaffProfile.setAssignedDate(LocalDateTime.now());
        testStaffProfile.setNotes("Test staff member");

        // Setup assignment request
        assignmentRequest = StaffAssignmentRequest.builder()
                .driverId(1L)
                .stationId(1L)
                .workShift("Morning")
                .notes("Test assignment")
                .active(true)
                .build();

        // Setup filter request
        filterRequest = StaffFilterRequest.builder()
                .stationId(1L)
                .active(true)
                .workShift("Morning")
                .keyword("staff")
                .page(0)
                .size(10)
                .sortBy("assignedDate")
                .sortDirection("desc")
                .build();
    }

    @Test
    void assignStaffToStation_ValidRequest_ShouldCreateStaffProfile() {
        // Given
        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(testDriver));
        when(stationRepository.findById(1L))
                .thenReturn(Optional.of(testStation));
        when(staffProfileRepository.findByDriver_DriverId(1L))
                .thenReturn(Optional.empty());
        when(staffProfileRepository.save(any(StaffProfile.class)))
                .thenReturn(testStaffProfile);

        // When
        StaffDetailResponse result = staffManagementService.assignStaffToStation(assignmentRequest);

        // Then
        assertNotNull(result);
        assertEquals("staffuser", result.getUserName());
        assertEquals("Staff User", result.getFullName());
        assertEquals("Test Station", result.getStationName());
        assertEquals("Morning", result.getWorkShift());
        assertTrue(result.isActive());
        verify(staffProfileRepository).save(any(StaffProfile.class));
    }

    @Test
    void assignStaffToStation_DriverNotFound_ShouldThrowException() {
        // Given
        when(driverRepository.findById(999L))
                .thenReturn(Optional.empty());

        assignmentRequest.setDriverId(999L);

        // When & Then
        AppException exception = assertThrows(AppException.class, 
                () -> staffManagementService.assignStaffToStation(assignmentRequest));
        assertEquals(ErrorCode.DRIVER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void assignStaffToStation_StationNotFound_ShouldThrowException() {
        // Given
        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(testDriver));
        when(stationRepository.findById(999L))
                .thenReturn(Optional.empty());

        assignmentRequest.setStationId(999L);

        // When & Then
        AppException exception = assertThrows(AppException.class, 
                () -> staffManagementService.assignStaffToStation(assignmentRequest));
        assertEquals(ErrorCode.STATION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void assignStaffToStation_DriverNotStaffRole_ShouldThrowException() {
        // Given
        RoleDetail driverRole = new RoleDetail();
        driverRole.setRoleType("DRIVER");
        testDriver.setRoles(Set.of(driverRole));

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(testDriver));

        // When & Then
        AppException exception = assertThrows(AppException.class, 
                () -> staffManagementService.assignStaffToStation(assignmentRequest));
        assertEquals(ErrorCode.INVALID_ROLE, exception.getErrorCode());
    }

    @Test
    void assignStaffToStation_StaffAlreadyAssigned_ShouldThrowException() {
        // Given
        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(testDriver));
        when(stationRepository.findById(1L))
                .thenReturn(Optional.of(testStation));
        when(staffProfileRepository.findByDriver_DriverId(1L))
                .thenReturn(Optional.of(testStaffProfile));

        // When & Then
        AppException exception = assertThrows(AppException.class, 
                () -> staffManagementService.assignStaffToStation(assignmentRequest));
        assertEquals(ErrorCode.STAFF_ALREADY_ASSIGNED, exception.getErrorCode());
    }

    @Test
    void updateStaffAssignment_ValidRequest_ShouldUpdateStaffProfile() {
        // Given
        when(staffProfileRepository.findById(1L))
                .thenReturn(Optional.of(testStaffProfile));
        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(testDriver));
        when(stationRepository.findById(1L))
                .thenReturn(Optional.of(testStation));
        when(staffProfileRepository.save(any(StaffProfile.class)))
                .thenReturn(testStaffProfile);

        // When
        StaffDetailResponse result = staffManagementService.updateStaffAssignment(1L, assignmentRequest);

        // Then
        assertNotNull(result);
        assertEquals("staffuser", result.getUserName());
        assertEquals("Morning", result.getWorkShift());
        verify(staffProfileRepository).save(any(StaffProfile.class));
    }

    @Test
    void updateStaffAssignment_StaffNotFound_ShouldThrowException() {
        // Given
        when(staffProfileRepository.findById(999L))
                .thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, 
                () -> staffManagementService.updateStaffAssignment(999L, assignmentRequest));
        assertEquals(ErrorCode.STAFF_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void removeStaffFromStation_ValidId_ShouldDeactivateStaff() {
        // Given
        when(staffProfileRepository.findById(1L))
                .thenReturn(Optional.of(testStaffProfile));
        when(staffProfileRepository.save(any(StaffProfile.class)))
                .thenReturn(testStaffProfile);

        // When
        staffManagementService.removeStaffFromStation(1L);

        // Then
        verify(staffProfileRepository).save(argThat(staff -> !staff.isActive()));
    }

    @Test
    void removeStaffFromStation_StaffNotFound_ShouldThrowException() {
        // Given
        when(staffProfileRepository.findById(999L))
                .thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, 
                () -> staffManagementService.removeStaffFromStation(999L));
        assertEquals(ErrorCode.STAFF_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getAllStaff_WithFilters_ShouldReturnPaginatedResults() {
        // Given
        List<StaffProfile> staffProfiles = Arrays.asList(testStaffProfile);
        Page<StaffProfile> staffPage = new PageImpl<>(staffProfiles);

        when(staffProfileRepository.findWithFilters(eq(1L), eq(true), eq("Morning"), any(Pageable.class)))
                .thenReturn(staffPage);

        // When
        Page<StaffDetailResponse> result = staffManagementService.getAllStaff(filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("staffuser", result.getContent().get(0).getUserName());
        assertEquals("Test Station", result.getContent().get(0).getStationName());
        verify(staffProfileRepository).findWithFilters(eq(1L), eq(true), eq("Morning"), any(Pageable.class));
    }

    @Test
    void getAllStaff_WithKeywordFilter_ShouldSearchByKeyword() {
        // Given
        filterRequest.setStationId(null);
        filterRequest.setActive(null);
        filterRequest.setWorkShift(null);

        List<StaffProfile> staffProfiles = Arrays.asList(testStaffProfile);
        Page<StaffProfile> staffPage = new PageImpl<>(staffProfiles);

        when(staffProfileRepository.findWithFilters(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(staffPage);

        // When
        Page<StaffDetailResponse> result = staffManagementService.getAllStaff(filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(staffProfileRepository).findWithFilters(isNull(), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    void getStaffDetail_ValidId_ShouldReturnStaffDetails() {
        // Given
        when(staffProfileRepository.findById(1L))
                .thenReturn(Optional.of(testStaffProfile));

        // When
        StaffDetailResponse result = staffManagementService.getStaffDetail(1L);

        // Then
        assertNotNull(result);
        assertEquals("staffuser", result.getUserName());
        assertEquals("Staff User", result.getFullName());
        assertEquals("staff@example.com", result.getEmail());
        assertEquals("Test Station", result.getStationName());
        assertEquals("Morning", result.getWorkShift());
        assertTrue(result.isActive());
    }

    @Test
    void getStaffDetail_InvalidId_ShouldThrowException() {
        // Given
        when(staffProfileRepository.findById(999L))
                .thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, 
                () -> staffManagementService.getStaffDetail(999L));
        assertEquals(ErrorCode.STAFF_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getStationStaff_ValidStationId_ShouldReturnActiveStaff() {
        // Given
        List<StaffProfile> staffProfiles = Arrays.asList(testStaffProfile);
        when(staffProfileRepository.findByStation_StationIdAndActiveTrue(1L))
                .thenReturn(staffProfiles);

        // When
        List<StaffDetailResponse> result = staffManagementService.getStationStaff(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("staffuser", result.get(0).getUserName());
        assertEquals("Test Station", result.get(0).getStationName());
        verify(staffProfileRepository).findByStation_StationIdAndActiveTrue(1L);
    }

    @Test
    void getStaffStatistics_ShouldReturnStatistics() {
        // Given
        List<StaffProfile> allStaff = Arrays.asList(testStaffProfile);
        when(staffProfileRepository.findAll())
                .thenReturn(allStaff);
        when(staffProfileRepository.countActiveStaffByStation(1L))
                .thenReturn(1L);

        // When
        Map<String, Object> result = staffManagementService.getStaffStatistics();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("totalStaff"));
        assertTrue(result.containsKey("activeStaff"));
        assertTrue(result.containsKey("staffByStation"));
        assertEquals(1L, result.get("totalStaff"));
        assertEquals(1L, result.get("activeStaff"));
    }

    @Test
    void assignStaffToStation_WithDifferentStation_ShouldUpdateStation() {
        // Given
        Station newStation = new Station();
        newStation.setStationId(2L);
        newStation.setName("New Station");
        newStation.setStatus(StationStatus.ACTIVE);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(testDriver));
        when(stationRepository.findById(2L))
                .thenReturn(Optional.of(newStation));
        when(staffProfileRepository.findByDriver_DriverId(1L))
                .thenReturn(Optional.empty());
        when(staffProfileRepository.save(any(StaffProfile.class)))
                .thenReturn(testStaffProfile);

        assignmentRequest.setStationId(2L);

        // When
        StaffDetailResponse result = staffManagementService.assignStaffToStation(assignmentRequest);

        // Then
        assertNotNull(result);
        verify(staffProfileRepository).save(any(StaffProfile.class));
    }

    @Test
    void getAllStaff_WithNullFilters_ShouldReturnAllStaff() {
        // Given
        filterRequest.setStationId(null);
        filterRequest.setActive(null);
        filterRequest.setWorkShift(null);
        filterRequest.setKeyword(null);

        List<StaffProfile> staffProfiles = Arrays.asList(testStaffProfile);
        Page<StaffProfile> staffPage = new PageImpl<>(staffProfiles);

        when(staffProfileRepository.findWithFilters(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(staffPage);

        // When
        Page<StaffDetailResponse> result = staffManagementService.getAllStaff(filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(staffProfileRepository).findWithFilters(isNull(), isNull(), isNull(), any(Pageable.class));
    }
}
