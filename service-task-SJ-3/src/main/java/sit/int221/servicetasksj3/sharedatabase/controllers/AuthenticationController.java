package sit.int221.servicetasksj3.sharedatabase.controllers;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.servicetasksj3.sharedatabase.dtos.JwtRequestUser;
import sit.int221.servicetasksj3.sharedatabase.dtos.JwtResponseTokenDTO;
import sit.int221.servicetasksj3.sharedatabase.entities.Users;
import sit.int221.servicetasksj3.sharedatabase.services.JwtTokenUtil;
import sit.int221.servicetasksj3.sharedatabase.services.JwtUserDetailsService;

import java.util.List;

@RestController
//@RequestMapping("")
@CrossOrigin(origins = { "http://localhost:5173/", "http://ip23sj3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th" } )

public class AuthenticationController {
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public List<Users> getAllUsers(){
        return jwtUserDetailsService.getAllUser();
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid  @RequestBody JwtRequestUser user) {
//        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getUserName());
//        String token = jwtTokenUtil.generateToken(userDetails);
//        return ResponseEntity.ok(token);
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
//        Authentication authentication = authenticationManager.authenticate(authenticationToken);
//        if (! authentication.isAuthenticated()){
//            throw new UsernameNotFoundException("Invalid user or password");
//        }
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String token = jwtTokenUtil.generateToken(userDetails);
//        return ResponseEntity.ok(token);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponseTokenDTO(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
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
                System.out.println("Unable to get JWT Token"); }
            catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
                    "JWT Token does not begin with Bearer String"); }
        return ResponseEntity.ok(claims);
    }
}
