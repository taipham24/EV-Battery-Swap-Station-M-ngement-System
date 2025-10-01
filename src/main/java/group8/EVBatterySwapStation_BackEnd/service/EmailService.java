package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.entity.Booking;

public interface EmailService {
    void sendBookingConfirmation(String to, Booking booking);
}
