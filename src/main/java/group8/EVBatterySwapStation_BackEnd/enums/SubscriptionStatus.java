package group8.EVBatterySwapStation_BackEnd.enums;

public enum SubscriptionStatus {
    PENDING_PAYMENT,  // tạo trước khi thanh toán
    ACTIVE,           // thanh toán thành công
    CANCELLED,        // người dùng hủy / hệ thống hủy sau thời gian
    EXPIRED
}
