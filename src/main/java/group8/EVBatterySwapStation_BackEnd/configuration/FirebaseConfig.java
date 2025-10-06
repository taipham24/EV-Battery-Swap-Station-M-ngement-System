package group8.EVBatterySwapStation_BackEnd.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@ConditionalOnProperty(value = "firebase.enabled", havingValue = "true", matchIfMissing = false)
public class FirebaseConfig {
    @Value("${path.serviceAccountKey}")
    private String serviceAccountKeyPath;

    @PostConstruct
    public FirebaseApp init() throws IOException {
        InputStream serviceAccount = new ClassPathResource(serviceAccountKeyPath).getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);

    }
}
