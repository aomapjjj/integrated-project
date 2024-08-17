package sit.int221.servicetasksj3.sharedatabase.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @Column(name = "username" , nullable = false, length = 50)
    @Size(min = 1 , max = 50)
    @NotEmpty
    private String userName;
    @Column(name = "password" , nullable = false, length = 14)
    @Size(min = 1 , max = 14)
    @NotEmpty
    private String password;
}
