package controllers;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHashing {
	private static final int SALT_LENGTH = 16; //16 bytes = 128 bits
	private static final int ITERATIONS = 1000;
	private static final int KEYLENGTH = 128;

	public static String hash(String password, String salt) {
		try {
			PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes("UTF-8"), ITERATIONS, KEYLENGTH);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte hash[] = factory.generateSecret(keySpec).getEncoded();
			return toHex(hash);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String generateSalt() {
		try {
			SecureRandom secureRandom;
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
			byte[] salt = new byte[SALT_LENGTH];
			secureRandom.nextBytes(salt);
			return toHex(salt);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String toHex(byte[] array) {
		BigInteger bigInteger = new BigInteger(1, array);
		return bigInteger.toString(16);
	}
	
}
