package sit.int221.servicetasksj3.sharedatabase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "itbkk_shared", catalog = "")
public class Users {
    
    @Id
    @Column(name = "oid")
    private String oid;

    @Column(name = "name")
    private String name;

    @NotBlank
    @NotEmpty
    @Column(name = "username" , nullable = false, length = 50)
    private String username;

    @Column(name = "email")
    private String email;

    @NotBlank
    @NotEmpty
    @Column(name = "password", nullable = false, length = 50)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;


}
