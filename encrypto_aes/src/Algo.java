import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import de.flexiprovider.api.keys.SecretKeySpec;
import de.flexiprovider.core.FlexiCoreProvider;

public class Algo {

	public static SecretKey secKey;

	public static void main(String[] args) throws Exception {

		Security.addProvider(new FlexiCoreProvider());

		Cipher cipher = Cipher.getInstance("AES128_CBC", "FlexiCore");
		KeyGenerator keyGen = KeyGenerator.getInstance("AES", "FlexiCore");
		SecretKey secKey = keyGen.generateKey();

		// Encrypt

		cipher.init(Cipher.ENCRYPT_MODE, secKey);

		String cleartextFile = "cleartext.txt";
		String ciphertextFile = "ciphertextSymm.txt";

		FileInputStream fis = new FileInputStream(cleartextFile);
		FileOutputStream fos = new FileOutputStream(ciphertextFile);
		CipherOutputStream cos = new CipherOutputStream(fos, cipher);

		byte[] block = new byte[8];
		int i;
		while ((i = fis.read(block)) != -1) {
			cos.write(block, 0, i);
		}
		cos.close();

		System.out.println(secKey);
		byte[] data = secKey.getEncoded();
		SecretKey key2 = new SecretKeySpec(data, 0, data.length, "AES");
		System.out.println(key2);

		// Decrypt
		Cipher cipher1 = Cipher.getInstance("AES128_CBC", "FlexiCore");

		String cleartextAgainFile = "cleartextAgainSymm.txt";

		cipher1.init(Cipher.DECRYPT_MODE, secKey);

		fis = new FileInputStream(ciphertextFile);
		CipherInputStream cis = new CipherInputStream(fis, cipher1);
		fos = new FileOutputStream(cleartextAgainFile);

		while ((i = cis.read(block)) != -1) {
			fos.write(block, 0, i);
		}
		fos.close();

	}

}
