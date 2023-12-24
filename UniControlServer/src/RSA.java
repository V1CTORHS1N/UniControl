import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RSA {
    private static final Map<Integer, String> keyMap = new HashMap<Integer, String>();
    private static PrivateKey privateKey;

    public static void genKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = new String(Base64.getEncoder().encode(publicKey.getEncoded()));
        keyMap.put(0, publicKeyString);
    }

    public static String decrypt(String message) throws Exception {
        byte[] inputByte = Base64.getDecoder().decode(message.getBytes(StandardCharsets.UTF_8));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        String out = new String(cipher.doFinal(inputByte));
        return out;
    }

    public static String getPublicKey() {
        return keyMap.get(0);
    }
}
