package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.InspectReturnRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.SwapPaymentRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.BatteryInspectionDTO;
import group8.EVBatterySwapStation_BackEnd.DTO.response.PaymentDTO;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SwapTransactionDTO;
import group8.EVBatterySwapStation_BackEnd.entity.*;
import group8.EVBatterySwapStation_BackEnd.enums.*;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.*;
import group8.EVBatterySwapStation_BackEnd.service.SwapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SwapServiceImpl implements SwapService {

    private final BookingRepository bookingRepository;
    private final SwapTransactionRepository swapTransactionRepository;
    private final PaymentRepository paymentRepository;
    private final BatteryInspectionRepository batteryInspectionRepository;
    private final BatteryLedgerRepository batteryLedgerRepository;
    private final BatteryRepository batteryRepository;

    @Transactional
    @Override
    public SwapTransactionDTO confirm(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_INVALID));
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(ErrorCode.BOOKING_INVALID);
        }
        Battery reserved = booking.getReservedBattery();
        if (reserved == null || reserved.getStatus() != BatteryStatus.AVAILABLE && reserved.getStatus() != BatteryStatus.RESERVED && reserved.getStatus() != BatteryStatus.FULL) {
            throw new AppException(ErrorCode.BATTERY_NOT_AVAILABLE);
        }

        // Create swap
        SwapTransaction swap = SwapTransaction.builder()
                .driver(booking.getDriver())
                .station(booking.getStation())
                .reservedBattery(reserved)
                .status(SwapStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .confirmedAt(LocalDateTime.now())
                .build();
        swap = swapTransactionRepository.save(swap);

        // Ledger reserve
        logLedger(reserved, booking.getStation(), BatteryLedgerAction.RESERVE, reserved.getStatus(), BatteryStatus.RESERVED, swap.getSwapId());

        // Update battery to RESERVED to lock
        reserved.setStatus(BatteryStatus.RESERVED);
        batteryRepository.save(reserved);

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return toDTO(swap);
    }

    @Transactional
    @Override
    public PaymentDTO pay(Long swapId, SwapPaymentRequest request) {
        SwapTransaction swap = swapTransactionRepository.findById(swapId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));
        if (swap.getStatus() != SwapStatus.CONFIRMED && swap.getStatus() != SwapStatus.PAID) {
            throw new AppException(ErrorCode.INVALID_TRANSITION);
        }
        paymentRepository.findFirstBySwapAndStatus(swap, PaymentStatus.SUCCESS)
                .ifPresent(p -> {throw new AppException(ErrorCode.ALREADY_PAID);});

        if (request.getAmountVnd() == null || request.getAmountVnd() < 0) {
            throw new AppException(ErrorCode.AMOUNT_MISMATCH);
        }

        Payment payment = Payment.builder()
                .swap(swap)
                .method(request.getMethod())
                .amountVnd(request.getAmountVnd())
                .status(PaymentStatus.SUCCESS)
                .paidAt(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);

        // Update swap
        swap.setAmountVnd(request.getAmountVnd());
        swap.setPaidAt(payment.getPaidAt());
        swap.setStatus(SwapStatus.PAID);
        swapTransactionRepository.save(swap);

        // Dispense reserved battery to IN_USE
        Battery reserved = swap.getReservedBattery();
        BatteryStatus prev = reserved.getStatus();
        reserved.setStatus(BatteryStatus.IN_USE);
        batteryRepository.save(reserved);
        logLedger(reserved, swap.getStation(), BatteryLedgerAction.DISPENSE, prev, BatteryStatus.IN_USE, swap.getSwapId());

        return PaymentDTO.builder()
                .paymentId(payment.getPaymentId())
                .swapId(swap.getSwapId())
                .method(payment.getMethod())
                .amountVnd(payment.getAmountVnd())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .build();
    }

    @Transactional
    @Override
    public BatteryInspectionDTO inspectReturn(Long swapId, InspectReturnRequest request) {
        SwapTransaction swap = swapTransactionRepository.findById(swapId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));
        if (swap.getStatus() != SwapStatus.PAID && swap.getStatus() != SwapStatus.INSPECTED) {
            throw new AppException(ErrorCode.INVALID_TRANSITION);
        }
        Battery returned = batteryRepository.findById(request.getBatteryId())
                .orElseThrow(() -> new AppException(ErrorCode.BATTERY_NOT_FOUND));

        // Save inspection
        BatteryInspection inspection = BatteryInspection.builder()
                .swap(swap)
                .battery(returned)
                .condition(request.getCondition())
                .socPercent(request.getSocPercent())
                .notes(request.getNotes())
                .inspectedAt(LocalDateTime.now())
                .build();
        inspection = batteryInspectionRepository.save(inspection);

        // Update returned battery status and ledger
        BatteryStatus newStatus = (request.getCondition() == InspectionCondition.DAMAGED)
                ? BatteryStatus.QUARANTINED
                : BatteryStatus.AVAILABLE;
        BatteryStatus prev = returned.getStatus();
        returned.setStatus(newStatus);
        batteryRepository.save(returned);
        logLedger(returned, swap.getStation(),
                request.getCondition() == InspectionCondition.DAMAGED ? BatteryLedgerAction.QUARANTINE : BatteryLedgerAction.RETURN,
                prev, newStatus, swap.getSwapId());

        swap.setReturnedBattery(returned);
        swap.setInspectedAt(inspection.getInspectedAt());
        swap.setStatus(SwapStatus.INSPECTED);

        // Complete if paid and inspected
        if (Objects.equals(swap.getStatus(), SwapStatus.INSPECTED) && swap.getPaidAt() != null) {
            swap.setStatus(SwapStatus.COMPLETED);
            swap.setCompletedAt(LocalDateTime.now());
        }
        swapTransactionRepository.save(swap);

        return BatteryInspectionDTO.builder()
                .inspectionId(inspection.getInspectionId())
                .swapId(swap.getSwapId())
                .batteryId(returned.getBatteryId())
                .condition(inspection.getCondition())
                .socPercent(inspection.getSocPercent())
                .notes(inspection.getNotes())
                .inspectedAt(inspection.getInspectedAt())
                .build();
    }

    @Override
    public Page<SwapTransactionDTO> list(Long driverId, Long stationId, String status, Pageable pageable) {
        if (driverId != null) {
            return swapTransactionRepository.findByDriver_DriverId(driverId, pageable).map(this::toDTO);
        }
        if (stationId != null) {
            return swapTransactionRepository.findByStation_StationId(stationId, pageable).map(this::toDTO);
        }
        if (status != null) {
            return swapTransactionRepository.findByStatus(SwapStatus.valueOf(status), pageable).map(this::toDTO);
        }
        return swapTransactionRepository.findAll(pageable).map(this::toDTO);
    }

    private void logLedger(Battery battery, Station station, BatteryLedgerAction action,
                           BatteryStatus prev, BatteryStatus next, Long refSwapId) {
        BatteryLedger entry = BatteryLedger.builder()
                .battery(battery)
                .station(station)
                .action(action)
                .prevStatus(prev)
                .newStatus(next)
                .refSwapId(refSwapId)
                .createdAt(LocalDateTime.now())
                .build();
        batteryLedgerRepository.save(entry);
    }

    private SwapTransactionDTO toDTO(SwapTransaction s) {
        return SwapTransactionDTO.builder()
                .swapId(s.getSwapId())
                .driverId(s.getDriver().getDriverId())
                .stationId(s.getStation().getStationId())
                .reservedBatteryId(s.getReservedBattery() != null ? s.getReservedBattery().getBatteryId() : null)
                .returnedBatteryId(s.getReturnedBattery() != null ? s.getReturnedBattery().getBatteryId() : null)
                .status(s.getStatus())
                .amountVnd(s.getAmountVnd())
                .createdAt(s.getCreatedAt())
                .confirmedAt(s.getConfirmedAt())
                .paidAt(s.getPaidAt())
                .inspectedAt(s.getInspectedAt())
                .completedAt(s.getCompletedAt())
                .notes(s.getNotes())
                .build();
    }
}



