package com.stackexcelero.reader;



public class NTAG216Reader {
	private static final byte[] READ_COMMAND = new byte[] {
			(byte)0x30, /*Read command*/
			(byte)0x04, /*From block 5*/
			(byte)0x00, 
			(byte)0xE1, /*To block E1*/
			(byte)0x00};
	/*
	private byte[] bytesToRead = new byte[4];
	private byte[] bytesToWrite = new byte[4];
	
	private final int totalBlocks = 222;
	private final int totalBytes = 888;
	
	private NTAG216 tag;
	
	public NTAG216Reader() throws Exception {
		NTAG216 tag = new NTAG216();
		
		// Connect to the reader
	    TerminalFactory factory = TerminalFactory.getDefault();
	    CardTerminals terminals = factory.terminals();
	    List<CardTerminal> list = terminals.list();
	    CardTerminal terminal = list.get(0); // assume there's only one terminal
	    Card card = terminal.connect("*");
	    CardChannel channel = card.getBasicChannel();
		tag.setMemory(readAllBlocksFromCardChannel(channel));
		channel.getCard().disconnect(false);
		this.tag = tag;
	}
	
	public byte[] readBlock(int blockNum) throws Exception {
	    if (blockNum < 0 || blockNum > 0xFF) {
	        throw new IllegalArgumentException("Block number must be between 0x00 and 0xFF");
	    }

	    int offset = blockNum * 4;
	    if (offset >= tag.getMemory().length) {
	        throw new IllegalArgumentException("Block number is out of range");
	    }

	    int length = Math.min(maxTransceiveLength, tag.getMemory().length - offset);
	    byte[] blockData = Arrays.copyOfRange(tag.getMemory(), offset, offset + length);
	    return blockData;
	}
	protected byte[] readAllBlocksFromCardChannel(CardChannel channel) throws Exception {
	    // Define the read command
	    byte[] readCommand = new byte[] {(byte) 0xFF, (byte) 0xB0, 0x00, 0x00, 0x10};

	    ByteArrayOutputStream stream = new ByteArrayOutputStream();

	    for (int i = 0; i <= 0xE1; i++) {
	        // Set the block number in the read command
	        readCommand[3] = (byte) i;

	        // Send the read command and receive the response
	        byte[] response = channel.transmit(new CommandAPDU(readCommand)).getBytes();

	        // Check the response status
	        if (response[response.length - 2] == (byte) 0x90 && response[response.length - 1] == 0x00) {
	            // If the response status is success, add the block data to the output stream
	            byte[] data = Arrays.copyOfRange(response, 0, response.length - 2);
	            stream.write(data);
	        } else {
	            // If the response status is not success, throw an exception
	            throw new Exception("Read block " + i + " failed with status " + byteArrayToHexString(response));
	        }
	    }

	    return stream.toByteArray();
	}
	protected byte[] readUID(CardChannel channel) throws Exception {
		// Create GET VERSION command APDU
		byte[] getVersion = new byte[] {(byte)0xFF, (byte)0xCA, 0x00, 0x00, 0x00};
		// Wrap into CommandAPDU object
		CommandAPDU command = new CommandAPDU(getVersion);

		// Transmit command and receive response
		ResponseAPDU response = channel.transmit(command);
		// Check if response is successful (SW1SW2 = 9000h)
		if (response.getSW() == 0x9000) {
		    // Get data bytes from response
		    byte[] data = response.getData();
		    
		    // Check if data length is at least 8 bytes
		    if (data.length >= 8) {
		        // Get UID from first 7 bytes of data
		        byte[] uid = Arrays.copyOfRange(data, 0, 7);
		        // Print UID as hex string
		        System.out.println("UID: " + DatatypeConverter.printHexBinary(uid));
		        return uid;
		    }
		}
		return new byte[0];
	}
	public byte[] readAllBlocks() throws Exception {
	    byte[] allBlocks = new byte[256 * 4]; // 256 blocks, 4 bytes per block

	    for (int i = 0; i < 256; i++) {
	        byte[] blockData = readBlock(i);
	        System.arraycopy(blockData, 0, allBlocks, i * 4, blockData.length);
	    }

	    return allBlocks;
	}
	
	public void writeTag(String filePath, int startBlock, int endBlock) throws IOException {
	    // Open the file and read the data
	    byte[] data = Files.readAllBytes(Paths.get(filePath));

	    // Make sure the data fits in the specified range of blocks
	    if (data.length > (endBlock - startBlock + 1) * 4) {
	        throw new IllegalArgumentException("Data is too large for the specified block range");
	    }

	    // Send the write commands for each block
	    int blockNumber = startBlock;
	    int startIndex = 0;
	    while (blockNumber <= endBlock && startIndex < data.length) {
	        // Calculate the number of bytes to write to this block
	        int numBytes = Math.min(4, data.length - startIndex);

	        // Copy the bytes from the data array to the blockData array
	        byte[] blockData = new byte[4];
	        System.arraycopy(data, startIndex, blockData, 0, numBytes);

	        // Send the write command for this block
	        byte[] command = buildWriteCommand(blockNumber, blockData);
	        transmitCommand(command);

	        // Increment the block number and the start index
	        blockNumber++;
	        startIndex += numBytes;
	    }
	}
	public void transmitCommand(byte[] command) throws IOException {
	    // Connect to the tag
	    connect();

	    // Send the command
	    byte[] response = tag.transceive(command);

	    // Check the response status
	    if (response == null || response.length < 2) {
	        throw new IOException("No response or invalid response length");
	    }
	    if (response[0] != (byte) 0x00 || response[1] != (byte) 0x00) {
	        throw new IOException("Error response: " + bytesToHex(response));
	    }

	    // Disconnect from the tag
	    disconnect();
	}
	public byte[] buildWriteCommand(int blockNumber, byte[] blockData) {
	    byte[] command = new byte[6];
	    command[0] = (byte) 0xA2;  // Write command code
	    command[1] = (byte) blockNumber;  // Block number
	    System.arraycopy(blockData, 0, command, 2, 4);  // Block data
	    return command;
	}

	public byte[] getBytesToRead() {
		return bytesToRead;
	}

	public void setBytesToRead(byte[] bytesToRead) {
		this.bytesToRead = bytesToRead;
	}

	public byte[] getBytesToWrite() {
		return bytesToWrite;
	}

	public void setBytesToWrite(byte[] bytesToWrite) {
		this.bytesToWrite = bytesToWrite;
	}

	public static byte[] getReadCommand() {
		return READ_COMMAND;
	}

	public int getTotalBlocks() {
		return totalBlocks;
	}

	public int getTotalBytes() {
		return totalBytes;
	}

	public int getMaxTransceiveLength() {
		return maxTransceiveLength;
	}

	public NTAG216 getTag() {
		return tag;
	}

	public void setTag(NTAG216 tag) {
		this.tag = tag;
	}
	
	public String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
	public byte[] hexStringToByteArray(String hex) {
	    int len = hex.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
	                             + Character.digit(hex.charAt(i+1), 16));
	    }
	    return data;
	}*/
}
