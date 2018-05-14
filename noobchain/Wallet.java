package noobchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {
  public PrivateKey priavteKey;
  public PublicKey publicKey;

  public Wallet() {
    generateKeyPair();
  }

  public void generateKeyPair() {
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
      SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
      ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

      // Initialize the key generator and generate the key pair
      keyGen.initialize(ecSpec, secureRandom);
      KeyPair keyPair = keyGen.generateKeyPair();

      // Set the public and private keys
      priavteKey = keyPair.getPrivate();
      publicKey = keyPair.getPublic();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
