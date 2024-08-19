package sit.int221.servicetasksj3.sharedatabase.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequestUser {
    @Column(name = "username", nullable = false, length = 50)
    @Size(min = 1, max = 50, message = "Username length must be between 1 and 50 characters.")
    @NotBlank(message = "Username must not be empty.")
    private String userName;
    @Column(name = "password", nullable = false, length = 14)
    @Size(min = 1, max = 14, message = "Password length must be between 1 and 14 characters.")
    @NotBlank(message = "Password must not be empty.")
    private String password;
}
