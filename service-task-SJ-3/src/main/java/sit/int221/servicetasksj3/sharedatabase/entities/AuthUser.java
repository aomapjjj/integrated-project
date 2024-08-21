package sit.int221.servicetasksj3.sharedatabase.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class AuthUser extends User implements Serializable {
    private String name;
    private String oid;
    private String email;
    private Role role;
    public AuthUser() {
        super("anonymous", "", new ArrayList<GrantedAuthority>()); }
    public AuthUser(String userName, String password) {
        super(userName, password, new ArrayList<GrantedAuthority>());
    }
//    public AuthUser(String userName, String password, Collection<? extends GrantedAuthority> authorities) {
//        super(userName, password, authorities);
//    }
    public AuthUser(String userName, String password, Collection<? extends GrantedAuthority> authorities,
                    String name, String oid, String email, Role role) {
        super(userName, password, authorities);
        this.name = name;
        this.oid = oid;
        this.email = email;
        this.role = role;
    }
}
