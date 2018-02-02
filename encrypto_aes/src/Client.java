import java.net.*;
import java.io.*;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import de.flexiprovider.core.FlexiCoreProvider;

import java.util.Arrays;
import java.util.Base64;

public class Client {

	static SecretKey secKey;
	// initialize socket and input output streams
	private Socket socket = null;
	//private DataInputStream input = null;
	private DataOutputStream out = null;
	FileInputStream fis;
	File myFile;
	BufferedInputStream bis = null;
	OutputStream os = null;

	public Client(String address, int port) {

		// establish a connection
		try {
			socket = new Socket(address, port);
			System.out.println("Connected");

			// takes input from terminal
			//input = new DataInputStream(System.in);

			// sends output to the socket
			out = new DataOutputStream(socket.getOutputStream());

			ObjectOutputStream ob = new ObjectOutputStream(
					socket.getOutputStream());
			ob.writeObject(secKey);

			// String encodedKey =
			// Base64.getEncoder().encodeToString(secKey.getEncoded());
			// out.writeUTF(encodedKey);

			myFile = new File("ciphertextSymm.txt");
			byte[] mybytearray = new byte[(int) myFile.length()];
			fis = new FileInputStream(myFile);
			//bis = new BufferedInputStream(fis);
			//bis.read(mybytearray, 0, mybytearray.length);
			os = socket.getOutputStream();
			System.out.println("Sending ");
			int count;
			while((count = fis.read(mybytearray)) != -1)
			{
			
			os.write(mybytearray, 0, mybytearray.length);
			}
			os.flush();
			System.out.println("Done.");

		} catch (UnknownHostException u) {
			System.out.println(u);
		} catch (IOException i) {
			System.out.println(i);
		}

		// close the connection
		try {
			//input.close();
			out.close();
			socket.close();
			bis.close();
			os.close();
		} catch (Exception x) {
			System.out.println(x);
		}
	}

	public static void main(String args[]) throws Exception {
		Security.addProvider(new FlexiCoreProvider());
		Cipher cipher = Cipher.getInstance("AES128_CBC", "FlexiCore");
		KeyGenerator keyGen = KeyGenerator.getInstance("AES", "FlexiCore");
		secKey = keyGen.generateKey();

		System.out.println(secKey);//my secret key
		cipher.init(Cipher.ENCRYPT_MODE, secKey);
		String cleartextFile = "cleartext.txt";
		String ciphertextFile = "ciphertextSymm.txt";

		FileInputStream fis = new FileInputStream(cleartextFile);
		FileOutputStream fos = new FileOutputStream(ciphertextFile);
		CipherOutputStream cos = new CipherOutputStream(fos, cipher);

		System.out.println(fis.available());
		
		byte[] block = new byte[8];
		int i;
		while ((i = fis.read(block)) != -1) {
			cos.write(block, 0, i);
		}
		cos.close();

		Client client = new Client("127.0.0.1", 5003);
	}
}
