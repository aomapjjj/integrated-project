package sit.int221.servicetasksj3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.exceptions.InternalServerErrorException;
import sit.int221.servicetasksj3.exceptions.UnauthorizedException;
import sit.int221.servicetasksj3.repositories.BoardRepository;
import sit.int221.servicetasksj3.repositories.LimitRepository;
import sit.int221.servicetasksj3.repositories.StatusRepository;
import sit.int221.servicetasksj3.sharedatabase.dtos.JwtRequestUser;
import sit.int221.servicetasksj3.sharedatabase.dtos.JwtResponseTokenDTO;
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
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private LimitRepository limitRepository;

    public JwtResponseTokenDTO login(JwtRequestUser user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));

        if (!authentication.isAuthenticated()) {
            throw new UnauthorizedException("Username or Password is incorrect.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtTokenUtil.generateToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
        // ดึง oid จาก UserDetails ที่กำหนดไว้ใน AuthUser
        String oid = ((AuthUser) userDetails).getOid();
        try {
            if (boardRepository.findByOwnerId(oid).isEmpty()){ // ตรวจสอบว่าผู้ใช้มีบอร์ดหรือไม่

            }
        } catch (Exception e) {
            throw new InternalServerErrorException("Cannot create user: " + e.getMessage());
        }
        return new JwtResponseTokenDTO(accessToken, refreshToken);
    }
}


