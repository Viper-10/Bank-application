package com.bankapplication.Bank_application_Jaxrs.cryptography;

public class AddOneCryptography implements CryptographyI {
    @Override
    public String encrypt(String originalMessage) {
        StringBuilder encryptedMessage = new StringBuilder("");

        for(int i = 0;i < originalMessage.length(); ++i){
            char c = originalMessage.charAt(i);
            if(Character.isLowerCase(c))
                encryptedMessage.append((char)((c-'a'+1) % 26 + 'a'));
            else if(Character.isUpperCase(c))
                encryptedMessage.append((char)((c-'A'+1)% 26+'A'));
            else{
                encryptedMessage.append((char)((c-'0'+1) % 26+'0'));
            }
        }

        return encryptedMessage.toString();
    }

    @Override
    public String decrypt(String originalMessage) {
        StringBuilder decryptedMessage = new StringBuilder("");

        for(int i = 0;i < originalMessage.length(); ++i){
            char c = originalMessage.charAt(i);
            if(Character.isLowerCase(c))
                decryptedMessage.append((char)((c-'a'-1+26) % 26 + 'a'));
            else if(Character.isUpperCase(c))
                decryptedMessage.append((char)((c-'A'-1+26)% 26+'A'));
            else{
                decryptedMessage.append((char)((c-'0'-1+26) % 26+'0'));
            }
        }

        return decryptedMessage.toString();
    }
}
