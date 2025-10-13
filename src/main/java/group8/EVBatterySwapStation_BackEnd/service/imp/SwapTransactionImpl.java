package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.SwapTransactionRequest;
import group8.EVBatterySwapStation_BackEnd.entity.Booking;
import group8.EVBatterySwapStation_BackEnd.entity.SwapTransaction;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.BookingRepository;
import group8.EVBatterySwapStation_BackEnd.repository.SwapTransactionRepository;
import group8.EVBatterySwapStation_BackEnd.service.SwapTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SwapTransactionImpl implements SwapTransactionService {
    @Autowired
    private final SwapTransactionRepository repository;
    @Autowired
    private BookingRepository bookingRepository;

//    @Override
//    public SwapTransaction createSwapTransaction(SwapTransactionRequest request) {
//        Booking booking= bookingRepository.findById(request.getBookingId())
//                .orElseThrow(()-> new AppException(ErrorCode.BOOKING_NOT_EXISTED));
//
//        return repository.save(request);
//    }
}
