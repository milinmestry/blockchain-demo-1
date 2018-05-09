import java.util.ArrayList;
import com.google.gson.GsonBuilder;

/**
 * NoobChain
 *
 * @see https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
 */
public class NoobChain {

  public static ArrayList<Block> blockchain = new ArrayList<Block>();

  public static void main(String[] args) {
    // Add blocks to blockchain ArrayList
    blockchain.add(new Block("Im First Block in the chain.", "0"));
    blockchain.add(new Block(
      "Im Second Block in the chain.", blockchain.get(blockchain.size()-1).hash));
    blockchain.add(new Block(
      "Im Third Block in the chain.", blockchain.get(blockchain.size()-1).hash));

    String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
    System.out.println("Json Blocks\n " + blockchainJson);

  }

}