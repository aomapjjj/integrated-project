package sit.int221.servicetasksj3.sharedatabase.services;

import io.viascom.nanoid.NanoId;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import sit.int221.servicetasksj3.entities.Board;
import sit.int221.servicetasksj3.entities.MicrosoftDetail;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.exceptions.UnauthorizedException;
import sit.int221.servicetasksj3.repositories.BoardRepository;
import sit.int221.servicetasksj3.repositories.MicrosoftDetailRepository;
import sit.int221.servicetasksj3.services.BoardService;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;
import sit.int221.servicetasksj3.sharedatabase.entities.MicrosoftUser;
import sit.int221.servicetasksj3.sharedatabase.entities.Role;
import sit.int221.servicetasksj3.sharedatabase.entities.Users;
import sit.int221.servicetasksj3.sharedatabase.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BoardService boardService;
    @Autowired
    private MicrosoftDetailRepository microsoftDetailRepository;




    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users users = userRepository.findByUsername(userName);
        if(users == null) {
            throw new UnauthorizedException(userName + " does not exist !!");
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
                users.getName(), users.getOid(), users.getEmail(), users.getRole());
    }

    public AuthUser loadUserByOid(String userName) throws UsernameNotFoundException {
        Users users = userRepository.findById(userName).orElse(null);

        if (users != null) {
            List<GrantedAuthority> roles = new ArrayList<>();
            GrantedAuthority grantedAuthority = new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return users.getRole().toString();
                }
            };
            roles.add(grantedAuthority);

            return new AuthUser(
                    users.getUsername(),
                    users.getPassword(),
                    roles,
                    users.getName(),
                    users.getOid(),
                    users.getEmail(),
                    users.getRole()
            );
        } else {
            return null;
        }
    }
    public AuthUser getUserDetailsMS(MicrosoftUser microsoftUser) {
        AuthUser userItbkk_Shared = loadUserByOid(microsoftUser.getOid());
        MicrosoftDetail microsoftDetail = microsoftDetailRepository.findById(microsoftUser.getOid())
                .orElse(new MicrosoftDetail());

        microsoftDetail.setOid(microsoftUser.getOid());
        microsoftDetail.setEmail(microsoftUser.getEmail());
        microsoftDetail.setDisplayName(microsoftUser.getDisplayName());
        microsoftDetail.setLastLogin(LocalDateTime.now());

        microsoftDetailRepository.save(microsoftDetail);

        return userItbkk_Shared != null ? userItbkk_Shared : new AuthUser(
                microsoftUser.getDisplayName(),
                microsoftUser.getDisplayName(),
                new ArrayList<>(),
                microsoftUser.getDisplayName(),
                microsoftUser.getOid(),
                microsoftUser.getEmail(),
                Role.STUDENT
        );
    }



    @Transactional
    public boolean authenticateUser(String username, String password) {
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
