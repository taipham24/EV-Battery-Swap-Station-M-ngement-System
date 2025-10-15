package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.PaymentRequest;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;
import group8.EVBatterySwapStation_BackEnd.entity.Payment;
import group8.EVBatterySwapStation_BackEnd.enums.PaymentMethod;
import group8.EVBatterySwapStation_BackEnd.enums.PaymentStatus;
import group8.EVBatterySwapStation_BackEnd.enums.SubscriptionStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.DriverSubscriptionRepository;
import group8.EVBatterySwapStation_BackEnd.repository.PaymentRepository;
import group8.EVBatterySwapStation_BackEnd.service.PaymentService;

import group8.EVBatterySwapStation_BackEnd.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentImpl implements PaymentService {
    @Autowired
    private final DriverRepository driverRepository;
    @Autowired
    private DriverSubscriptionRepository driverSubscriptionRepository;

    @Value("${vnpay.vnp_TmnCode}")
    private String vnp_TmnCode;
    @Value("${vnpay.vnp_HashSecret}")
    private String vnp_HashSecret;
    @Value("${vnpay.vnp_Url}")
    private String vnp_Url;
    @Value("${vnpay.vnp_ReturnUrl}")
    private String vnp_ReturnUrl;

    private final Map<String, String> paymentTokens = new HashMap<>();

    @Autowired
    private final PaymentRepository paymentRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public String createPayment(PaymentRequest request) {
        try {
            Long driverId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
            Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
            DriverSubscription subscription = driverSubscriptionRepository.findById(request.getSubscriptionId())
                    .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

            if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
                throw new AppException(ErrorCode.ALREADY_PAID);
            }
            long planPrice = subscription.getPlan().getPrice();
            if (!Objects.equals(planPrice, request.getAmountVnd())) {
                throw new AppException(ErrorCode.INVALID_AMOUNT);
            }
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(request.getAmountVnd().intValue() * 100));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_BankCode", "NCB");
            vnp_Params.put("vnp_TxnRef", String.valueOf(request.getSubscriptionId()));
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_Params.get("vnp_TxnRef"));
            vnp_Params.put("vnp_OrderType", "Subscription");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", request.getIpAddr());

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            String vnp_CreateDate = LocalDateTime.now(zoneId).format(formatter);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = LocalDateTime.now(zoneId).plusMinutes(15).format(formatter);
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            paymentTokens.put(vnp_Params.get("vnp_TxnRef"), driver.getUserName());
            return vnp_Url + "?" + queryUrl;
        } catch (Exception e) {
            log.error("Error occurred during payment creation: " + e.getMessage(), e);
            throw new AppException(ErrorCode.PAYMENT_ERROR);
        }
    }

    @Override
    public boolean verifyPayment(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash").toUpperCase();
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        try {
            String hashString = hmacSHA512(vnp_HashSecret, hashData.toString());
            if (!hashString.equals(vnp_SecureHash)) {
                log.error("Secure hash mismatch. Expected: {}, Actual: {}", hashString, vnp_SecureHash);
                return false;
            }
        } catch (Exception e) {
            log.error("Error while verifying payment hash", e);
            return false;
        }
        String responseCode = params.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            Driver driver = securityUtils.getCurrentUser();
            Long subscriptionId = Long.valueOf(params.get("vnp_TxnRef"));
            DriverSubscription driverSubscription = driverSubscriptionRepository.findById(subscriptionId)
                    .orElseThrow(() -> new RuntimeException("Subscription not found with ID: " + subscriptionId));
            long amount = Long.parseLong(params.get("vnp_Amount")) / 100;

            Payment payment = new Payment();
            payment.setMethod(PaymentMethod.CREDIT_CARD);
            payment.setAmountVnd(amount);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setSubscription(driverSubscription);
            payment.setSwap(null); // Not linked to a swap transaction

            if(payment.getMethod() == PaymentMethod.CASH) {
                payment.setCashier(driver.getStaffProfile());
            }

            driverSubscription.setStatus(SubscriptionStatus.ACTIVE);
            driverSubscription.setActive(true);
            driverSubscription.setStartDate(LocalDateTime.now());
            driverSubscription.setEndDate(LocalDateTime.now().plusDays(driverSubscription.getPlan().getDurationDays()));
            driverSubscription.setPayment(payment);
            paymentRepository.save(payment);
            return true;
        } else {
            log.info("Response code is not 00. Actual response: {}", responseCode);
        }
        return false;
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        sha512_HMAC.init(secret_key);
        return bytesToHex(sha512_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
