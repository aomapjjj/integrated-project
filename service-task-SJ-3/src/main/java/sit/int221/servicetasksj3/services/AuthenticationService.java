package sit.int221.servicetasksj3.services;

import io.viascom.nanoid.NanoId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.entities.Board;
import sit.int221.servicetasksj3.exceptions.InternalServerErrorException;
import sit.int221.servicetasksj3.exceptions.UnauthorizedException;
import sit.int221.servicetasksj3.repositories.BoardRepository;
import sit.int221.servicetasksj3.sharedatabase.dtos.JwtRequestUser;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;
import sit.int221.servicetasksj3.sharedatabase.services.JwtTokenUtil;
import sit.int221.servicetasksj3.sharedatabase.services.JwtUserDetailsService;

@Service
public class AuthenticationService {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private BoardRepository boardRepository;

    public String login(JwtRequestUser user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));

        if (!authentication.isAuthenticated()) {
            throw new UnauthorizedException("Username or Password is incorrect.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);
        String oid = ((AuthUser) userDetails).getOid();

        try {
            if (boardRepository.findByOwnerId(oid).isEmpty()) {
                Board newBoard = new Board();
                newBoard.setId(NanoId.generate(10));  // สร้าง id โดยใช้ NanoId
                newBoard.setOwnerId(oid);
                newBoard.setName("Default Board Name"); // ตั้งชื่อบอร์ดค่าเริ่มต้น (สามารถเปลี่ยนได้ในภายหลัง)
                boardRepository.save(newBoard);
            }
        } catch (Exception e) {
            throw new InternalServerErrorException("Cannot create user: " + e.getMessage());
        }
        return token;
    }
}

