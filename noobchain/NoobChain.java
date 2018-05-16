package noobchain;

import com.google.gson.GsonBuilder;
import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

/**
 * noobchain.NoobChain
 *
 * https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
 */
public class NoobChain {

  public static ArrayList<Block> blockchain = new ArrayList<Block>();
  // list of all unspent transactions
  public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
  public static int difficulty = 5;
  public static float minimumTransaction = 0.1f;
  public static Transaction genesisTrasaction;
  public static Wallet walletA;
  public static Wallet walletB;

  public static void main(String[] args) {
    // Set Bouncy Castle as security provider
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

    // New Wallets
    walletA = new Wallet();
    walletB = new Wallet();
    Wallet coinBase = new Wallet();

    // Create a genesis transaction which send 100 noobcoins to WalletA
    genesisTrasaction = new Transaction(
      coinBase.publicKey, walletA.publicKey, 100f, null);
    genesisTrasaction.generateSignature(coinBase.priavteKey);
    genesisTrasaction.transactionId = "0"; // manually set transactionId
    genesisTrasaction.outputs.add(new TransactionOutput(
      genesisTrasaction.recipient, genesisTrasaction.value, genesisTrasaction.transactionId
    ));
    // its important to store our first transaction in the UTXOs list
    UTXOs.put(genesisTrasaction.outputs.get(0).id, genesisTrasaction.outputs.get(0));

    System.out.println("Creating and miningn genesis block.");
    Block genesis = new Block("0");
    genesis.addTransaction(genesisTrasaction);
    addBlock(genesis);

    // testing
    Block block1 = new Block(genesis.hash);
    System.out.println("\nWalletA balance=" + walletA.getBalance());
    System.out.println("Attempt to transfer funds 40 to walletB");
    block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
    addBlock(block1);
    System.out.println("\nWallertA balance=" + walletA.getBalance());
    System.out.println("WallertB balance=" + walletB.getBalance());

    Block block2 = new Block(block1.hash);
    System.out.println("Attempt to send more funds (1000) than it has.");
    block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
    addBlock(block2);
    System.out.println("\nWallertA balance=" + walletA.getBalance());
    System.out.println("WallertB balance=" + walletB.getBalance());

    Block block3 = new Block(block2.hash);
    System.out.println("Attempt to send 20 funds from WalletB to WalletA.");
    block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
    System.out.println("\nWallertA balance=" + walletA.getBalance());
    System.out.println("WallertB balance=" + walletB.getBalance());

    isChainValid();
  }

  public static Boolean isChainValid() {
    Block currentBlock;
    Block previousBlock;

    int len = blockchain.size();
    String hashTarget = new String(new char[difficulty]).replace('\0', '0');
    HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
    tempUTXOs.put(genesisTrasaction.outputs.get(0).id, genesisTrasaction.outputs.get(0));

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
      
      TransactionOutput tempOutput;
      int cbLen = currentBlock.transactions.size();

      for (int j = 0; j < cbLen; j++) {
        Transaction currentTransaction = currentBlock.transactions.get(j);

        if (!currentTransaction.verifySignature()) {
          System.out.println("Signature on transaction (" + j + ") is Invalid.");
          return false;
        }

        if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
          System.out.println("Inputs and Outputs are not equal on transaction (" + j + ")");
          return false;
        }

        for (TransactionInput input : currentTransaction.inputs) {
          tempOutput = tempUTXOs.get(input.transactionOutputId);

          if (tempOutput == null) {
            System.out.println("Reference input on transaction (" + j + ") is missing.");
            return false;
          }

          if (input.UTXO.value != tempOutput.value) {
            System.out.println("Reference input transaction (" + j + ") is invalid.");
            return false;
          }
          UTXOs.remove(input.transactionOutputId);
        }

        for (TransactionOutput output : currentTransaction.outputs) {
          tempUTXOs.put(output.id, output);
        }

        if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
          System.out.println("Transaction (" + j + ") output recipient is not who it should be.");
          return false;
        }

        if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
          System.out.println("Transaction (" + j + ") output 'change' is not sender.");
          return false;
        }
      }
    }

    System.out.println("Blockchain is valid.");
    return true;
  }

  public static void addBlock(Block newBlock) {
    newBlock.mineBlock(difficulty);
    blockchain.add(newBlock);
  }

}