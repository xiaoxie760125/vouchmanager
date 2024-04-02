package com.sqds.springjwtstudy.jwtutil.modal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    @Value("${jwt.secret}")
    private  String secret;
    @Value("${jwt.expiration}")
    private int expiration;
    public  String genetateToken(String username) throws Exception {
        Map<String ,Object> claims=new HashMap<>();
        claims.put("username",username);
        return  creatToken(username,claims);

    }

    private String creatToken(String username, Map<String, Object> claims)  throws  Exception{

      Map<String,Object> headers=new HashMap<>();
      headers.put("typ","JWT");
      headers.put("alg","HS256");
      var token= Jwts.builder()
              .setHeader(headers)
              .setClaims(claims)
              .setSubject(username)
              .setExpiration(new Date(System.currentTimeMillis()+expiration))
              .signWith(getsignKey(), SignatureAlgorithm.HS256)
              .compact();
        return  token;

    }
    public  String exractUsername(String token) throws Exception {
        return  getTokenBody(token,Claims::getSubject);
    }
    private String getSubject(Claims claims)
    {
        return claims.get("username",String.class);
    }
    private  Date getExpiration(Claims claims)
    {
        return claims.getExpiration();
    }
    public <T> T getTokenBody(String token, Function<Claims, T> claimsResolver) throws Exception {
    final  Claims claims=extractToken(token);
    return  claimsResolver.apply(claims);
    }


    public Claims extractToken(String token) throws Exception {
        return  Jwts.parserBuilder()
                .setSigningKey(getsignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public  String UsernameFromToken(String token) throws Exception {
        return  Jwts.parserBuilder()
                .setSigningKey(getsignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public  Boolean isTokenExpired(String token) throws Exception {
        Date expiration=Jwts.parserBuilder()
                .setSigningKey(getsignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return  expiration.before(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }
    public  Boolean validateToken(String token, UserDetails userDetails) throws Exception {
        String usernameFromToken=UsernameFromToken(token);
        return userDetails.getUsername().equals(usernameFromToken);
    }
    private Key getsignKey()
    {
        byte[]  byteKey= Decoders.BASE64.decode(secret);
        return  Keys.hmacShaKeyFor(byteKey);
    }



}
