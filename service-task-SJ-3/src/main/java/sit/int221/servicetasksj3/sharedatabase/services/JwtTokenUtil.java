package sit.int221.servicetasksj3.sharedatabase.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import sit.int221.servicetasksj3.sharedatabase.entities.MicrosoftUser;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("#{${jwt.max-token-interval-minutes}*60*1000}")
    private long JWT_TOKEN_VALIDITY;
    @Value("#{${jwt.refresh-token-validity}*60*60*1000}")
    private long JWT_REFRESH_TOKEN_VALIDITY;
    @Value("${spring.security.oauth2.client.provider.azure.issuer-uri}")
    private String azureIssuerUri;
    private final String graphApiUrl = "https://graph.microsoft.com/v1.0/me";

    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject); }
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    public Claims getAllClaimsFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claims;
    }
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    public String generateToken(UserDetails userDetails) {
        AuthUser authUser = (AuthUser) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", authUser.getName());
        claims.put("oid", authUser.getOid());
        claims.put("email", authUser.getEmail());
        claims.put("role", authUser.getRole().name());
        return doGenerateToken(claims, userDetails.getUsername());
    }
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("https://intproj23.sit.kmutt.ac.th/sj3/")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(signatureAlgorithm, SECRET_KEY)
                .compact();
    }
    public String generateRefreshToken(UserDetails userDetails) {
        AuthUser authUser = (AuthUser) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", authUser.getName());
        claims.put("oid", authUser.getOid());
        claims.put("email", authUser.getEmail());
        claims.put("role", authUser.getRole().name());
        return doGenerateRefreshToken(claims, userDetails.getUsername());
    }
    private String doGenerateRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("https://intproj23.sit.kmutt.ac.th/sj3/")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY))
                .signWith(signatureAlgorithm, SECRET_KEY)
                .compact();
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean isMicrosoftToken(String token) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    graphApiUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );


            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Token is valid. User info: " + response.getBody());
                return true;
            }
        } catch (HttpClientErrorException ex) {

            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                System.out.println("Invalid token: " + ex.getMessage());
            } else {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        }
        return false;
    }


    public MicrosoftUser extractMicrosoftUserFromToken(String token) {
        Claims claims = getClaimsFromMicrosoftToken(token);
        if (claims == null) {
            System.out.println("Claims is null");
            return null;
        }

        return new MicrosoftUser(
                claims.get("id", String.class),
                claims.get("userPrincipalName", String.class),
                claims.get("displayName", String.class)
        );
    }


    public Claims getClaimsFromMicrosoftToken(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://graph.microsoft.com/v1.0/me",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );


            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> userInfo = response.getBody();
                Claims claims = Jwts.claims();
                claims.putAll(userInfo);
                return claims;
            } else {
                System.out.println("Failed to fetch user info. Status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                System.out.println("Invalid token: " + ex.getMessage());
            } else {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        return null;
    }


    // ตรวจสอบว่า token ที่ส่งมาเป็น refresh token หรือไม่
//    public Boolean isRefreshToken(String token) {
//        Claims claims = getAllClaimsFromToken(token);
//        Date expiration = claims.getExpiration();
//        return expiration.getTime() - new Date().getTime() <= JWT_REFRESH_TOKEN_VALIDITY;
//    }
}
