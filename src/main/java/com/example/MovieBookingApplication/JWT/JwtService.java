package com.example.MovieBookingApplication.JWT;

import com.example.MovieBookingApplication.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public String extractUserName(String jwtToken) {

        return extractClaim(jwtToken, Claims::getSubject);
        
    }

    private <T> T extractClaim(String jwtToken, Function<Claims,T> claimsResolver) {

        final Claims claims = extractAllClaims(jwtToken);

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {

         return Jwts
                 .parser()
                 .verifyWith(getSignInKey())
                 .build()
                 .parseSignedClaims(jwtToken)
                 .getPayload();
    }

   public PublicKey getSignInKey() {
        return (PublicKey) Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(UserDetails userDetails) {

        return generateToken(new HashMap<>(),userDetails);
    }

    public String generateToken(Map<String, Objects> extractClaims, UserDetails userDetails){

        return Jwts
                .builder()
                .claims(extractClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }


    public boolean isTokenValid(String jwtToken, User userDetails) {

        final String username = extractUserName(jwtToken);

        return (userDetails.getUsername().equals(username) && !isTokenExpired(jwtToken));

    }

    private boolean isTokenExpired(String jwtToken) {

        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken,Claims::getExpiration);
    }
}
