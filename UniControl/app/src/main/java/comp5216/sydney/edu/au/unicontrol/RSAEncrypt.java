package comp5216.sydney.edu.au.unicontrol;

import android.os.Build;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSAEncrypt {
    private static byte[] pubKey;

    public static void setPubKey(String pubKey) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            RSAEncrypt.pubKey = Base64.getDecoder().decode(pubKey);
        }
    }

    public static String encrypt(String message) throws Exception {
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(RSAEncrypt.pubKey));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String out = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            out = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));
        }
        return out;
    }
}
