import java.net.*;
import java.io.*;
import java.util.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;


import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.security.KeyFactory;
import de.flexiprovider.core.FlexiCoreProvider;
import de.flexiprovider.pki.X509EncodedKeySpec;
import codec.Base64;

public class Alice {
	
	//Initializations
	private Socket          socketReceive   = null;
    private ServerSocket    server   = null;
    private Socket          socketSend   = null;
    
    private DataInputStream in       =  null;
    private DataOutputStream out     = null;
    
  
    FileInputStream fis;
    BufferedInputStream bis = null;
    OutputStream os = null;
    
   static PublicKey pub;
    
   
    //Receiving  public key
    public Alice(int port2){
    	try{
    			server = new ServerSocket(port2);
    			System.out.println("Waiting");
    			socketReceive = server.accept();
    			
    			 in = new DataInputStream(new BufferedInputStream(socketReceive.getInputStream()));
    	         String line = in.readUTF();
    	         byte[] bytes = Base64.decode(line);
    	         X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
    	         KeyFactory kf = KeyFactory.getInstance("RSA","FlexiCore");
    	         pub = kf.generatePublic(ks);
    	         //System.out.println(ks);
    	         System.out.println(pub);
    	         
    	         socketReceive.close();
    	         in.close();
    	         
    	         
    	}
    	 catch(Exception i)
         {
             System.out.println(i);
         }
    }
    
    //sending encrypted message using public key
    public Alice(String address,int port1){
    	try{
    		
    		socketSend = new Socket(address, port1);
    		System.out.println("Connected");
    		
    		
    		  File myFile = new File ("ciphertextRSA.txt");
	         byte [] mybytearray  = new byte [(int)myFile.length()];
	         InputStream in = new FileInputStream(myFile);
	         OutputStream out = socketSend.getOutputStream();
	         int count;
	         while ((count = in.read(mybytearray)) > 0) {
	             out.write(mybytearray, 0, count);
	         }

	         out.close();
	         in.close();
	         socketSend.close();
            System.out.println("Done."); 
    	      
    	         
    	}
    	 catch(Exception i)
         {
             System.out.println(i);
         }
    }
    public static void main(String args[]) throws Exception
    {
    	Security.addProvider(new FlexiCoreProvider());
    	Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    	Alice  al = new Alice(5008);
    	
    	cipher.init(Cipher.ENCRYPT_MODE, pub);
    	
    	String cleartextFile = "cleartext.txt";
		String ciphertextFile = "ciphertextRSA.txt";

		FileInputStream fis = new FileInputStream(cleartextFile);
		FileOutputStream fos = new FileOutputStream(ciphertextFile);
		CipherOutputStream cos = new CipherOutputStream(fos, cipher);

		byte[] block = new byte[32];
		int i;
		while ((i = fis.read(block)) != -1) {
		    cos.write(block, 0, i);
		}
		cos.close();
		
		Alice a = new Alice("127.0.0.1",5009);
    }
}
