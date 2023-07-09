package com.stackexcelero.model;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.stackexcelero.utility.Utility;

public class Response {
	private byte[] data;
	private boolean success;
	
	public Response(byte[] data) {
        this.data = data;
        this.success = Utility.validateResponse(data);
    }

	public byte[] getData() {
		return data;
	}

	public boolean isSuccess() {
		return success;
	}
	
//	public String getDataAsString() {
//	    return new String(data, StandardCharsets.UTF_8);
//	}
//	
//	public int getDataAsInt() {
//	    return ByteBuffer.wrap(data).getInt();
//	}
}
