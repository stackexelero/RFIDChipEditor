package com.stackexcelero.utility;

public class Utility {
	public static boolean validateResponse(byte[] response) {
	    // Check if the response is null or empty
	    if (response == null || response.length == 0) {
	        return false;
	    }
	    
	    // Check if the first byte of the response is 0x00 (success)
	    if (response[0] != (byte)0x00) {
	        return false;
	    }
	    
	    // Check if the second byte of the response is 0x9000 (success)
	    if (response.length > 1 && response[1] != (byte)0x90) {
	        return false;
	    }
	    
	    return true;
	}
	public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
	public static byte[] hexStringToByteArray(String hex) {
	    int len = hex.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
	                             + Character.digit(hex.charAt(i+1), 16));
	    }
	    return data;
	}
}
