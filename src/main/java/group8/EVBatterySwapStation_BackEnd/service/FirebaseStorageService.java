package group8.EVBatterySwapStation_BackEnd.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FirebaseStorageService {

    String uploadFile(MultipartFile file, String fileName) throws IOException;
}
