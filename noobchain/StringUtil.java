package noobchain;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

/**
 * noobchain.StringUtil
 */
public class StringUtil {

  public static final String SHA256 = "SHA-256";
  public static final String UTF8 = "UTF-8";
  public static final String ECDSA = "ECDSA";
  public static final String BC = "BC";

  public static String applySha256(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance(SHA256);
      byte[] hash = digest.digest(input.getBytes(UTF8));
      StringBuffer hexString = new StringBuffer();
      int hashLength = hash.length;

      for (int i = 0; i < hashLength; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);

        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Applies ECDSA Signature and returns the result
   *
   * @param PrivateKey privateKey
   * @param String input
   * @return array byte
   */
  public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
    Signature dsa;
    byte[] output = new byte[0];

    try{
      dsa = Signature.getInstance(ECDSA, BC);
      dsa.initSign(privateKey);
      byte[] strByte = input.getBytes();
      dsa.update(strByte);
      byte[] realSig = dsa.sign();
      output = realSig;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return output;
  }

  /**
   * Verify ECDSA Signature
   *
   * @param PublicKey publicKey
   * @param String data
   * @param byte signature
   * @return boolean
   */
  public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
    try {
      Signature ecdsaVerify = Signature.getInstance(ECDSA, BC);
      ecdsaVerify.initVerify(publicKey);
      ecdsaVerify.update(data.getBytes());
      return ecdsaVerify.verify(signature);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Return encoded string value from key bytes
   * @param key
   * @return String
   */
  public static String getStringFromKey(Key key) {
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  public static String getMerkleRoot(ArrayList<Transaction> transactions) {
    int count = transactions.size();
    ArrayList<String> previousTreeLayer = new ArrayList<String>();

    for (Transaction transaction : transactions) {
      previousTreeLayer.add(transaction.transactionId);
    }
    int ptlCount = previousTreeLayer.size();
    ArrayList<String> treeLayer = previousTreeLayer;

    while (count > 1) {
      treeLayer = new ArrayList<String>();
      for (int i = 0; i < ptlCount; i++) {
        treeLayer.add(applySha256(
          previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
      }
      count = treeLayer.size();
      previousTreeLayer = treeLayer;
    }
    String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    return merkleRoot;
  }
}