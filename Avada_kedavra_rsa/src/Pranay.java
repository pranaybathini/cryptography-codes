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
import de.flexiprovider.core.FlexiCoreProvider;
import codec.Base64;

public class Pranay {
	public final static int FILE_SIZE = 6022386; 
	 int bytesRead;
	 int current = 0;
	 
	private Socket          socketReceive   = null;
    private ServerSocket    server   = null;
    private Socket          socketSend   = null;
    
    private DataInputStream in       =  null;
    private DataOutputStream out     = null;
   
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    
     static  PublicKey pubKey;
    
    public Pranay(String address,int port1)
    {
        // starts server and waits for a connection
        try
        {
        	socketSend = new Socket(address, port1); //To send
        	String publicK = Base64.encode(pubKey.getEncoded());
        	out    = new DataOutputStream(socketSend.getOutputStream());
        	out.writeUTF(publicK);
            
        
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
}
    
    public Pranay(int port1)
    {
        // starts server and waits for a connection
        try
        {
        	
            server = new ServerSocket(port1); // To Receive
            System.out.println("Server started");
            System.out.println("Waiting for a client ...");
            socketReceive = server.accept();
            System.out.println("Client accepted");
            
            
            InputStream inn = socketReceive.getInputStream();
            OutputStream outt = new FileOutputStream("cipher.txt");
            byte[] bytes = new byte[32];
            int count;
            while ((count = inn.read(bytes)) > 0) {
                outt.write(bytes, 0, count);
            }

            outt.close();
            inn.close();
           
           
            System.out.println("File Received" );
            
        
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
}
    
    
    
    public static void main(String args[]) throws Exception
    {
    	
    	Security.addProvider(new FlexiCoreProvider());
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "FlexiCore");
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		kpg.initialize(1024);
		KeyPair keyPair = kpg.generateKeyPair();
		PrivateKey privKey = keyPair.getPrivate();
		pubKey = keyPair.getPublic();
		System.out.println(pubKey);
		
		
		Pranay sub = new Pranay("127.0.0.1",5008);
		Pranay self = new Pranay(5009);
		
		String cleartextAgainFile = "cleartextAgainRSA.txt";
		String ciphertextFile = "cipher.txt";
		cipher.init(Cipher.DECRYPT_MODE, privKey);

		FileInputStream fis = new FileInputStream(ciphertextFile);
		CipherInputStream cis = new CipherInputStream(fis, cipher);
		FileOutputStream fos = new FileOutputStream(cleartextAgainFile);
		byte[] block = new byte[32];
		int i;
		while ((i = cis.read(block)) != -1) {
		    fos.write(block, 0, i);
		}
		fos.close();
		fis.close();
		cis.close();
	    
		System.out.print("End");
		
    }
    
}
