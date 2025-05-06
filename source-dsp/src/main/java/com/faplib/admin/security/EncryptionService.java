//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.faplib.admin.security;

import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class EncryptionService {
    private static final String ALG = "DES";
    private Cipher encryptionCipher;
    private Cipher decryptionCipher;

    public EncryptionService() throws GeneralSecurityException {
        this.init("smartlib.util.crypto".substring(0, 8).getBytes());
    }

    private void init(byte[] password) throws GeneralSecurityException {
        DESKeySpec desKeySpec = new DESKeySpec(password);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance("DES");
        SecretKey pbeKey = keyFac.generateSecret(desKeySpec);
        this.encryptionCipher = Cipher.getInstance("DES");
        this.decryptionCipher = Cipher.getInstance("DES");
        this.encryptionCipher.init(1, pbeKey);
        this.decryptionCipher.init(2, pbeKey);
    }

    public String encrypt(String strPassword) throws IllegalBlockSizeException, BadPaddingException {
        byte[] res = this.encryptionCipher.doFinal(strPassword.getBytes());
        /*BASE64Encoder encoder = new BASE64Encoder();
        String base64 = encoder.encode(res);*/
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(res);
    }

    public String decrypt(String strEncryptPassword) throws Exception {
        /*BASE64Decoder encoder = new BASE64Decoder();
        byte[] inputBytes = encoder.decodeBuffer(strEncryptPassword);*/
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(strEncryptPassword);

        byte[] retVal;
        try {
            retVal = this.decryptionCipher.doFinal(decodedBytes);
        } catch (BadPaddingException var6) {
            throw new Exception();
        }

        return new String(retVal);
    }
}
