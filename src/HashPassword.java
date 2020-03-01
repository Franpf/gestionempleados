import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class HashPassword {

	private static SecureRandom random = new SecureRandom();
	
	public static byte [] passwordHash(String password, byte [] salt) {
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 0xffff, 1024);
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			return factory.generateSecret(spec).getEncoded();
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static byte [] getSalt() {
		byte [] salt = new byte[48];
		random.nextBytes(salt);
		return salt;
	}
	
	public static String hashHex(String password) {
		byte [] salt = getSalt();
		byte [] hash = passwordHash(password, salt);
		String hashHex = String.format("%256s", new BigInteger(1, hash).toString(16)).replace(' ', '0');
		String saltHex = String.format("%96s", new BigInteger(1, salt).toString(16)).replace(' ', '0');
		return hashHex.substring(0, 21) + saltHex + hashHex.substring(21);
		/* retorna cadena de longitud 256 (hashHex.length) + 96 (saltHex.length) = 352 */
	}
	
	public static String hashBase64(String password) {
		byte [] salt = getSalt();
		byte [] hash = Arrays.copyOf(passwordHash(password, salt), 129);
		String hashBase64 = Base64.getEncoder().encodeToString(hash);
		String saltBase64 = Base64.getEncoder().encodeToString(salt);
		return hashBase64.substring(0, 21) + saltBase64 + hashBase64.substring(21); 
		/* retorna cadena de longitud 172 (hashBase64.length) + 64 (saltBase64.length) = 236 */ 
	}
	
	public static boolean checkHex(String password, String savedHash) {
		byte [] salt = new BigInteger(savedHash.substring(21, 117), 16).toByteArray();
		String hash1 = savedHash.substring(0, 21) + savedHash.substring(117);
		String hash2 = String.format("%256s", new BigInteger(1, passwordHash(password, salt)).toString(16)).replace(' ', '0');;
		return hash1.equals(hash2);
	}
	
	public static boolean checkBase64(String password, String savedHash) {
		byte [] salt = Base64.getDecoder().decode(savedHash.substring(21, 85));
		String hash1 = savedHash.substring(0, 21) + savedHash.substring(85);
		String hash2 = Base64.getEncoder().encodeToString(Arrays.copyOf(passwordHash(password, salt), 129));
		return hash1.equals(hash2);
	}
	
}
