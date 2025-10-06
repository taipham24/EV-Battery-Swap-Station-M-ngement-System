package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.BatteryRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StationRepository;
import group8.EVBatterySwapStation_BackEnd.service.imp.BatteryServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BatteryServiceImpTest {

    private BatteryRepository batteryRepository;
    private StationRepository stationRepository;
    private BatteryServiceImp service;

    @BeforeEach
    void setup() {
        batteryRepository = Mockito.mock(BatteryRepository.class);
        stationRepository = Mockito.mock(StationRepository.class);
        service = new BatteryServiceImp(batteryRepository, stationRepository);
    }

    @Test
    @DisplayName("listBatteries filters and pagination")
    void testListBatteriesFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Battery b1 = Battery.builder().batteryId(1L).model("M1").capacityWh(1500).status(BatteryStatus.CHARGING).build();
        Page<Battery> page = new PageImpl<>(List.of(b1), pageable, 1);
        when(batteryRepository.findAll(Mockito.<Specification<Battery>>any(), eq(pageable))).thenReturn(page);

        Page<Battery> result = service.listBatteries(1L, BatteryStatus.CHARGING, "M", 1000, 2000, pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getModel()).isEqualTo("M1");
    }

    @Test
    @DisplayName("listBatteries invalid capacity range -> INVALID_QUERY")
    void testListBatteriesInvalidRange() {
        Pageable pageable = PageRequest.of(0, 10);
        assertThatThrownBy(() -> service.listBatteries(null, null, null, 3000, 1000, pageable))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INVALID_QUERY.getMessage());
    }

    @Test
    @DisplayName("summary by status/model/capacity buckets")
    void testSummary() {
        Battery b1 = Battery.builder().batteryId(1L).model("A").capacityWh(1500).status(BatteryStatus.CHARGING).build();
        Battery b2 = Battery.builder().batteryId(2L).model("A").capacityWh(2500).status(BatteryStatus.AVAILABLE).build();
        Battery b3 = Battery.builder().batteryId(3L).model("B").capacityWh(6000).status(BatteryStatus.AVAILABLE).build();
        when(batteryRepository.findAll(Mockito.<Specification<Battery>>any())).thenReturn(List.of(b1, b2, b3));

        List<int[]> buckets = List.of(new int[]{0,2000}, new int[]{2001,5000}, new int[]{5001,10000});
        Map<String, Object> res = service.summary(null, buckets);
        assertThat((List<?>) res.get("byStatus")).isNotEmpty();
        assertThat((List<?>) res.get("byModel")).isNotEmpty();
        assertThat((List<?>) res.get("byCapacityBucket")).hasSize(3);
    }

    @Test
    @DisplayName("updateStatus valid transition")
    void testUpdateStatusValid() {
        Battery b = Battery.builder().batteryId(1L).status(BatteryStatus.AVAILABLE).build();
        when(batteryRepository.findById(1L)).thenReturn(Optional.of(b));
        when(batteryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Battery updated = service.updateStatus(1L, BatteryStatus.RESERVED, null, false);
        assertThat(updated.getStatus()).isEqualTo(BatteryStatus.RESERVED);

        ArgumentCaptor<Battery> captor = ArgumentCaptor.forClass(Battery.class);
        verify(batteryRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(BatteryStatus.RESERVED);
    }

    @Test
    @DisplayName("updateStatus invalid transition without override -> INVALID_TRANSITION")
    void testUpdateStatusInvalidTransition() {
        Battery b = Battery.builder().batteryId(1L).status(BatteryStatus.DAMAGED).build();
        when(batteryRepository.findById(1L)).thenReturn(Optional.of(b));

        assertThatThrownBy(() -> service.updateStatus(1L, BatteryStatus.RESERVED, null, false))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INVALID_TRANSITION.getMessage());
    }

    @Test
    @DisplayName("updateStatus invalid transition with admin override -> allowed")
    void testUpdateStatusInvalidWithOverride() {
        Battery b = Battery.builder().batteryId(1L).status(BatteryStatus.DAMAGED).build();
        when(batteryRepository.findById(1L)).thenReturn(Optional.of(b));
        when(batteryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Battery updated = service.updateStatus(1L, BatteryStatus.MAINTENANCE, "admin override", true);
        assertThat(updated.getStatus()).isEqualTo(BatteryStatus.MAINTENANCE);
    }
}


