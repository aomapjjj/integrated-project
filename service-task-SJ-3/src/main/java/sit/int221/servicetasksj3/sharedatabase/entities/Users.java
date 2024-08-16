package sit.int221.servicetasksj3.sharedatabase.entities;

import jakarta.persistence.*;
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

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;


}
