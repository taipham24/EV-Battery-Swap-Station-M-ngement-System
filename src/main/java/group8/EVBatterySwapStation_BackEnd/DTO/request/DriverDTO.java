package group8.EVBatterySwapStation_BackEnd.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {
    @NotBlank(message = "Username is required")
    private String userName;

    @Size(min = 8, message = "INVALID_PASSWORD")
    private String password;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Full name is required")
    private String fullName;
    private boolean status;
}
