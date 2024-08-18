package sit.int221.servicetasksj3.sharedatabase.services;


import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;
import sit.int221.servicetasksj3.sharedatabase.entities.Users;
import sit.int221.servicetasksj3.sharedatabase.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    //Get all users
    @Transactional
    public List<Users> getAllUser(){
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users users = userRepository.findByUsername(userName);
        if(users == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, userName+ " does not exist !!");
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        GrantedAuthority grantedAuthority = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return users.getRole().toString();
            }
        };
        roles.add(grantedAuthority);
        return new AuthUser(users.getUsername(), users.getPassword(), roles,
                users.getOid(), users.getEmail(), users.getRole());
    }

    @Transactional
    public boolean Authentication(String username, String password) {
        Users users = userRepository.findByUsername(username);
        if (users == null){
            return false;
        } else {
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 32);
            char[] passwordArray = password.toCharArray();
            return argon2.verify(users.getPassword(), passwordArray);
        }
    }


}
