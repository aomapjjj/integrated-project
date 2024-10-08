package sit.int221.servicetasksj3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.exceptions.UnauthorizedException;
import sit.int221.servicetasksj3.repositories.BoardRepository;
import sit.int221.servicetasksj3.repositories.LimitRepository;
import sit.int221.servicetasksj3.repositories.StatusRepository;
import sit.int221.servicetasksj3.sharedatabase.dtos.JwtRequestUser;
import sit.int221.servicetasksj3.sharedatabase.dtos.JwtResponseTokenDTO;
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

        return new JwtResponseTokenDTO(accessToken, refreshToken);
    }

    public JwtResponseTokenDTO refreshAccessToken(String requestTokenHeader) {
        String refreshToken = extractToken(requestTokenHeader); // แยก refresh token ออกจาก header
        // ตรวจสอบ refresh token
        if (jwtTokenUtil.isTokenExpired(refreshToken)) {
            throw new UnauthorizedException("Refresh token has expired");
        }
        // ดึง username จาก refresh token
        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

        // สร้าง access token ใหม่
        String newAccessToken = jwtTokenUtil.generateToken(userDetails);
        return new JwtResponseTokenDTO(newAccessToken, null); // คืนเฉพาะ access token
    }
    // Method สำหรับแยก token ออกจาก header
    private String extractToken(String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            return tokenHeader.substring(7);
        } else {
            throw new UnauthorizedException("Token does not begin with Bearer String");
        }
    }
}


