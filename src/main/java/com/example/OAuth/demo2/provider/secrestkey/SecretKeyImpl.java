package com.example.OAuth.demo2.provider.secrestkey;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;

@Component
@NoArgsConstructor
public class SecretKeyImpl {
   private ISecretKey secretKey() {
        return ()-> new SecretKeySpec(ISecretKey.SECRET_KEY, ISecretKey.algorithm);
    }
    public SecretKeySpec getSecretKey(){
       return secretKey().createSecretKey();
    }
   /* public static SecretKeySpec getKey() {
       return new SecretKeySpec(ISecretKey.SECRET_KEY, ISecretKey.algorithm);
    }*/
}
