package sit.int221.servicetasksj3.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sit.int221.servicetasksj3.exceptions.UnauthorizedException;
import sit.int221.servicetasksj3.services.AuthenticationService;
import sit.int221.servicetasksj3.sharedatabase.dtos.JwtRequestUser;
import sit.int221.servicetasksj3.sharedatabase.dtos.JwtResponseTokenDTO;
import sit.int221.servicetasksj3.sharedatabase.services.JwtTokenUtil;
import sit.int221.servicetasksj3.sharedatabase.services.JwtUserDetailsService;

@RestController
@CrossOrigin(origins = { "http://localhost:5173/", "http://ip23sj3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th" } )

public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseTokenDTO> login(@Valid @RequestBody JwtRequestUser jwtRequestUser) {
        String token = authenticationService.login(jwtRequestUser);
        return ResponseEntity.ok(new JwtResponseTokenDTO(token));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Object> validateToken(@RequestHeader("Authorization") String requestTokenHeader) {
        Claims claims = null;
        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken); }
            catch (IllegalArgumentException e) {
                throw new UnauthorizedException("Unable to get JWT Token"); }
            catch (ExpiredJwtException e) {
                throw new UnauthorizedException("JWT Token has expired");
            }
        } else {
            throw new UnauthorizedException("JWT Token does not begin with Bearer String");
        }
        return ResponseEntity.ok(claims);
    }
}
