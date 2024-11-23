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
import io.jsonwebtoken.impl.DefaultClaims;
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

        return getClaimFromToken(token, Claims::getSubject);
    }

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
        System.out.println("Utill " + userDetails);
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    public Boolean isMicrosoftToken(String token) throws JsonProcessingException {
        try {
            NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withIssuerLocation("https://login.microsoftonline.com/79845616-9df0-43e0-8842-e300feb2642a/v2.0").build();
            Jwt jwt = nimbusJwtDecoder.decode(token);
            return true;
        } catch (JwtException | IllegalArgumentException |ExpiredJwtException e) {
            return false;
        }
    }

    public MicrosoftUser getDetailMicrosoftFromToken(String token) {
        Claims claims = getClaimsFromMicrosoftToken(token);
        if (claims == null) {
            return null;
        }
        return new MicrosoftUser(
                claims.get("oid", String.class),
                claims.get("preferred_username", String.class),
                claims.get("name", String.class)
        );
    }

    public Claims getClaimsFromMicrosoftToken(String token) {
        try {
            NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withIssuerLocation("https://login.microsoftonline.com/79845616-9df0-43e0-8842-e300feb2642a/v2.0").build();
            Jwt jwt = nimbusJwtDecoder.decode(token);
            return new DefaultClaims(jwt.getClaims());
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }


}
