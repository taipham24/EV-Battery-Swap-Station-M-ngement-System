package group8.EVBatterySwapStation_BackEnd.service.imp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import group8.EVBatterySwapStation_BackEnd.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class FirebaseStorageImpl implements FirebaseStorageService {
    @Value("${path.serviceAccountKey}")
    private String serviceAccountKeyPath;

    @Override
    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        InputStream serviceAccount = new ClassPathResource(serviceAccountKeyPath).getInputStream();
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()
                .getService();

        String bucketName = FirebaseApp.getInstance().getOptions().getStorageBucket();
        String objectName = fileName + file.getOriginalFilename();
        String encodedObjectName = URLEncoder.encode(objectName, StandardCharsets.UTF_8.toString());
        System.out.println("Uploading to bucket: " + bucketName);
        System.out.println("File path: " + objectName);
        System.out.println(fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(file.getContentType())
                .build();
        storage.create(blobInfo, file.getBytes());
        String imageUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media", bucketName, encodedObjectName);
        System.out.println("Generated image URL: " + imageUrl);

        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media", bucketName, encodedObjectName);
    }
}
