package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.PaymentRequest;
import group8.EVBatterySwapStation_BackEnd.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;

    @PostMapping("/create")
    public ResponseEntity<String>createPayment(@RequestBody PaymentRequest request){
        String paymentUrl = service.createPayment(request);
        return ResponseEntity.ok(paymentUrl);
    }
}
