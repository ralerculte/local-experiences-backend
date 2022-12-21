package com.cultegroup.localexperience.security;

import com.cultegroup.localexperience.exceptions.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret.access}")
    private String secretAccess;

    @Value("${jwt.secret.refresh}")
    private String secretRefresh;

    @Value("${jwt.header}")
    private String header;

    private Key keyAccess;
    private Key keyRefresh;

    public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
         keyAccess = Keys.hmacShaKeyFor(secretAccess.getBytes());
         keyRefresh = Keys.hmacShaKeyFor(secretRefresh.getBytes());
    }

    public String createAccessToken(String username, Long id) {
        Date accessExpiration = Date.from(
                LocalDateTime.now()
                        .plusMinutes(5)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        return createToken(username, id, accessExpiration, keyAccess);
    }

    public String createRefreshToken(String username, Long id) {
        Date accessExpiration = Date.from(
                LocalDateTime.now()
                        .plusDays(30)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        return createToken(username, id, accessExpiration, keyRefresh);
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, keyAccess);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, keyRefresh);
    }

    public Authentication getAuthentication(String token) {
        SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(getUsername(token, keyAccess));
        return new UsernamePasswordAuthenticationToken(user, "", null);
    }

    public String getUsernameByRefreshToken(String token) {
        return getUsername(token, keyRefresh);
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(header);
    }

    private String createToken(String username, Long id, Date accessExpiration, Key key) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(username, id);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(accessExpiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean validateToken(String token, Key key) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("Jwt token is expired or invalid", HttpStatus.UNAUTHORIZED);
        }
    }

    private String getUsername(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
