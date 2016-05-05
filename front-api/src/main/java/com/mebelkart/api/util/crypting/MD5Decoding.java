package com.mebelkart.api.util.crypting;

/**
 * @author Tinku
 *
 */
public class MD5Decoding {
	/**
	 * hexadecimal
	 */
	private static final char[] hexadecimal = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * This method decryptes binaryData to String 
	 * @param binaryData encrypted byte data
	 * @return decrypted String
	 */
	public String decrypt(byte[] binaryData) {

		if (binaryData.length != 16)
			return null;

		char[] buffer = new char[32];

		for (int i = 0; i < 16; i++) {
			int low = (int) (binaryData[i] & 0x0f);
			int high = (int) ((binaryData[i] & 0xf0) >> 4);
			buffer[i * 2] = hexadecimal[high];
			buffer[i * 2 + 1] = hexadecimal[low];
		}

		return new String(buffer);

	}

}
