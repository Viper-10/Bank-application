package com.bankapplication.Bank_application_Jaxrs.cryptography;

public interface CryptographyI {
    String encrypt(String originalMessage);
    String decrypt(String originalMessage);
}
