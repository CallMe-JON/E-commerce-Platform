package com.example.ecp_backend.util;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtSecretGenerator {
    public static void main(String[] args) {
        // Generate a cryptographically secure key
        Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String base64Encoded = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("Generated JWT Secret (Base64 Encoded): " + base64Encoded);
    }
}