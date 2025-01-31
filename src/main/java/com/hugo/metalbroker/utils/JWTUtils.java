package com.hugo.metalbroker.utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.hugo.metalbroker.exceptions.TokenNotFoundException;
import com.hugo.metalbroker.repository.UserRepo;
import com.hugo.metalbroker.repository.WalletRepo;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class JWTUtils {
    private final Key secretKey;
    private final WalletRepo walletRepo;
    private final UserRepo userRepo;

    public JWTUtils(WalletRepo walletRepo, UserRepo userRepo) {
        this.walletRepo = walletRepo;
        secretKey = new SecretKeySpec(Dotenv.load().get("SECRET_KEY").getBytes(), "HmacSHA256");
        this.userRepo = userRepo;
    }

    public String generateJWTToken(String username, HttpServletResponse response) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("wallet_id", walletRepo.getWalletIdByUsername(username));
        claims.put("token_version", userRepo.getTokenVersion(username));
        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuer("Sujith")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .signWith(secretKey)
                .compact();
        Cookie cookie = new Cookie("TOKEN", token);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(10 * 60);
        response.addCookie(cookie);
        return token;
    }

    public String generateRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        Cookie getCookie = null;

        for (Cookie cookie : cookies) {
            if ("TOKEN".equals(cookie.getName())) {
                getCookie = cookie;
                break;
            }
        }

        if (getCookie == null) {
            throw new TokenNotFoundException("");
        }

        String token = getCookie.getValue();

        Claims claims = decodeJWTToken(token);
        String username = claims.getSubject();
        Integer tokenVersion = (Integer) claims.get("token_version");

        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("wallet_id", walletRepo.getWalletIdByUsername(username));
        newClaims.put("token_version", (tokenVersion + 1));
        String refreshToken = Jwts.builder()
                .claims(newClaims)
                .subject(username)
                .issuer("Sujith")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .signWith(secretKey)
                .compact();

        Cookie refreshCookie = new Cookie("TOKEN", refreshToken);
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(5 * 60);

        response.addCookie(refreshCookie);
        userRepo.updateTokenVersion(username);

        return refreshToken;
    }

    public Claims decodeJWTToken(String jwt) {
        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public boolean validate(String token, String username) {
        Claims claims = decodeJWTToken(token);
        return claims.getExpiration().after(new Date(System.currentTimeMillis())) && username.equals(claims.getSubject());
    }

    public String getUsername(Cookie[] cookies) {
        String username = "";
        for (Cookie cookie : cookies) {
            if ("TOKEN".equals(cookie.getName())) {
                username = this.decodeJWTToken(cookie.getValue()).getSubject();
                break;
            }
        }
        return username;
    }

    public String getWalletID(Cookie[] cookies) {
        String walletId = "";
        for (Cookie cookie : cookies) {
            if ("TOKEN".equals(cookie.getName())) {
                walletId = (String) this.decodeJWTToken(cookie.getValue()).get("wallet_id");
                break;
            }
        }
        return walletId;
    }

    public int getTokenVersion(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Cookie getCookie = null;

        for (Cookie cookie : cookies) {
            if ("TOKEN".equals(cookie.getName())) {
                getCookie = cookie;
                break;
            }
        }

        return (int) decodeJWTToken(getCookie.getValue()).get("token_version");
    }
}
