import java.util.ArrayList;
import com.google.gson.GsonBuilder;

/**
 * NoobChain
 *
 * @see https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
 */
public class NoobChain {

  public static ArrayList<Block> blockchain = new ArrayList<Block>();
  public static int difficulty = 5;

  public static void main(String[] args) {
    // Add blocks to blockchain ArrayList
    blockchain.add(new Block("Im First Block in the chain.", "0"));
    System.out.println("Trying to mine Block 1.");
    blockchain.get(0).mineBlock(difficulty);

    blockchain.add(new Block(
      "Im Second Block in the chain.", blockchain.get(blockchain.size()-1).hash));
    System.out.println("Trying to mine Block 2.");
    blockchain.get(1).mineBlock(difficulty);

    blockchain.add(new Block(
      "Im Third Block in the chain.", blockchain.get(blockchain.size()-1).hash));
    System.out.println("Trying to mine Block 3.");
    blockchain.get(2).mineBlock(difficulty);

    System.out.println("\nIs Blockchain valid? " + isChainValid());

    String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
    System.out.println("Json Blocks\n " + blockchainJson);

  }

  public static Boolean isChainValid() {
    Block currentBlock;
    Block previousBlock;

    int len = blockchain.size();
    String hashTarget = new String(new char[difficulty]).replace('\0', '0');

    for (int i = 1; i < len; i++) {
      currentBlock = blockchain.get(i);
      previousBlock = blockchain.get(i - 1);

      // Compared registered hash and calculated hash
      if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
        System.out.println("Current hashes not equal.");
        return false;
      }

      // Compare previous hash and registered previous hash
      if (!previousBlock.hash.equals(currentBlock.previousHash)) {
        System.out.println("Previous hashes not equal.");
        return false;
      }

      // Check hash is solve
      if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
        System.out.println("This block hasn't been mined.");
        return false;
      }
    }
    return true;
  }

}