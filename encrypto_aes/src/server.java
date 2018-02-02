import java.net.*;
import java.io.*;
import java.security.Security;

import javax.crypto.*;

import de.flexiprovider.api.keys.SecretKeySpec;
import de.flexiprovider.core.FlexiCoreProvider;
import de.flexiprovider.core.rijndael.RijndaelKeyFactory;

import java.util.Base64;

public class server {
	public final static int FILE_SIZE = 6022386;
	private Socket socket = null;
	private ServerSocket server = null;
	private DataInputStream in = null;
	int bytesRead;
	int current = 0;
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	static SecretKey secKey = null;

	public server(int port) {
		// starts server and waits for a connection
		try {
			server = new ServerSocket(port);
			System.out.println("Server started");

			System.out.println("Waiting for a client ...");

			socket = server.accept();
			System.out.println("Client accepted");

			ObjectInputStream ob = new ObjectInputStream(
					socket.getInputStream());
			secKey = (SecretKey) ob.readObject();

			// takes input from the client socket
			// in = new DataInputStream(new
			// BufferedInputStream(socket.getInputStream()));
			// String line = in.readUTF();

			// byte[] decodedKey = Base64.getDecoder().decode(line);
			// secKey = new SecretKeySpec(decodedKey, "AES");

			byte[] mybytearray = new byte[8];
			InputStream is = socket.getInputStream();
			fos = new FileOutputStream("cipher.txt");
			//bos = new BufferedOutputStream(fos);
			//bytesRead = is.read(mybytearray, 0, mybytearray.length);
			//current = bytesRead;
			//do {
			//	bytesRead = is.read(mybytearray, current,(mybytearray.length - current));
			//	if (bytesRead >= 0)
			//		current += bytesRead;
			//} while (bytesRead > -1);
			//bos.write(mybytearray, 0, current);
			//bos.flush();
			while((current = is.read(mybytearray)) != -1)
			{
				fos.write(mybytearray,0,current);
			}
			System.out.println("File Received");

			// close connection
			socket.close();
			in.close();
			fos.close();
			bos.close();
		} catch (Exception i) {
			System.out.println(i);
		}
	}

	public static void main(String args[]) throws Exception {
		Security.addProvider(new FlexiCoreProvider());
		Cipher cipher = Cipher.getInstance("AES128_CBC", "FlexiCore");
		server ser = new server(5003);

		byte[] block = new byte[8];
		int i;

		String ciphertextFile = "cipher.txt";
		String cleartextAgainFile = "cleartextAgainSymm.txt";

		System.out.println(secKey);
		cipher.init(Cipher.DECRYPT_MODE, secKey);

		FileInputStream fis = new FileInputStream(ciphertextFile);
		CipherInputStream cis = new CipherInputStream(fis, cipher);
		FileOutputStream fos = new FileOutputStream(cleartextAgainFile);

		while ((i = cis.read(block)) != -1) {
			fos.write(block, 0, i);
		}
		fos.close();

	}
}
