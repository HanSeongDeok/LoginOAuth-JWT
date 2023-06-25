package com.example.OAuth.demo2.provider.secrestkey;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 암호화 복호화에 사용되는 비밀키를 가져오는 함수
 */
@FunctionalInterface
public interface ISecretKey {
    // Default 값 제공
    byte[] SECRET_KEY = Base64.getDecoder().decode("JWTSECRETTOKENDECODERKEYTESTTESTLONGSTRINGRANDOMSTRING");
    String algorithm = "HmacSHA256";
    SecretKeySpec createSecretKey();
}
