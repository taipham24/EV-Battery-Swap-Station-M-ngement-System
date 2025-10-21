package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.entity.Booking;
import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;
import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;

public interface EmailService {
    void sendBookingConfirmation(String to, Booking booking);

    void sendBookingRejected(String to, Booking booking);


    void sendRenewalSuccessEmail(String to, DriverSubscription sub);

    void sendEscalationNotice(SupportTicket ticket);
}
