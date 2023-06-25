package com.example.OAuth.demo2.provider.jwt;

import javax.crypto.spec.SecretKeySpec;

/**
 * Jwt 을 생성하는 함수
 */
@FunctionalInterface
public interface IJwtToken {
    String createJwtToken ();
}