package io.github.maaf72.smartthings.infra.security;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import com.fasterxml.jackson.core.type.TypeReference;

import io.github.maaf72.smartthings.config.Config;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
  private static SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(Config.APP_JWT_SECRET_KEY);

    return Keys.hmacShaKeyFor(keyBytes);
  }
  
  public static Claims parseJWTToken(String token) {
    return Jwts
      .parser()
      .verifyWith(getSignInKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }
  
  public static String generateJWTToken(Object obj) {
    Map<String, Object> claims = CustomObjectMapper.getObjectMapper().convertValue(obj, new TypeReference<>() {});

    return generateJWTToken(claims);
  }
  
  public static String generateJWTToken(Map<String, ?> claims) {
    return Jwts
      .builder()
      .claims(claims)
      .issuer(Config.APP_NAME)
      .issuedAt(new Date())
      .expiration(new Date(System.currentTimeMillis() + Config.APP_JWT_TOKEN_DURATION))
      .signWith(getSignInKey(), Jwts.SIG.HS256)
      .compact();
  }
}
