package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.PaymentRequest;
import group8.EVBatterySwapStation_BackEnd.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequest request) {
        String paymentUrl = service.createPayment(request);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestParam Map<String, String> params) {
        try {
            boolean isSuccess = service.verifyPayment(params);
            if (isSuccess) {
                return ResponseEntity.ok("Payment success!");
            } else {
                return ResponseEntity.ok("Payment Failed!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment verification failed: " + e.getMessage());
        }
    }
}
