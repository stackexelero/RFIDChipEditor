package com.stackexcelero.command;

import javax.smartcardio.CardException;

import com.stackexcelero.model.Response;
import com.stackexcelero.utility.CardReaderManager;

@SuppressWarnings("restriction")
public class ReadCommand implements Command{
	CardReaderManager readerManager;
	int startBlock;
	int endBlock;
	int blockNum;
	
	public ReadCommand(int startBlock, int endBlock) {
		readerManager = CardReaderManager.getInstance();
		this.startBlock = startBlock;
		this.endBlock = endBlock;
		this.blockNum = -1;
	}
	public ReadCommand(int numBlock) {
		readerManager = CardReaderManager.getInstance();
		this.startBlock = -1;
		this.endBlock = -1;
		this.blockNum = numBlock;
	}
	public ReadCommand() {
		readerManager = CardReaderManager.getInstance();
		this.startBlock = -1;
		this.endBlock = -1;
		this.blockNum = -1;
	}
	@Override
	public Response execute() throws CardException {
		//Dont have CountBlocks command so i defined this variable manually
		int totalBlocks = 231; 
		
		// Read ALL of the blocks in memory content
        if (startBlock < 0 && endBlock < 0 && blockNum < 0) {
        	
        	// Set up the command to read all of the memory contents
        	int toBlock = totalBlocks - 1;
        	int numBytes = totalBlocks * 4;
        	byte[] command = new byte[] {
        	    (byte)0x30, // Read command
        	    (byte)0x00, // From block 0
        	    (byte)0x00,
        	    (byte)toBlock, // To last block in memory
        	    (byte)0x00,
        	    (byte)numBytes // Number of bytes to read (4 bytes per block)
        	};
            byte[] response = readerManager.sendCommand(command);
            return new Response(response);
            
        }
        // Read a range of blocks
        else if (startBlock >= 0 && endBlock > 0 && blockNum < 0) {
            if (endBlock >= totalBlocks) {
                throw new IllegalArgumentException("End block is out of range.");
            }
            // Calculate the number of blocks to read
            int numBlocks = endBlock - startBlock + 1;
            byte[] command = new byte[] {
                (byte)0x30,             // Read command
                (byte)startBlock,       // From startBlock
                (byte)0x00,
                (byte)endBlock,         // To endBlock
                (byte)0x00,
                (byte)((numBlocks * 4) & 0xFF) // Number of bytes to read (4 bytes per block)
            };
            byte[] response = readerManager.sendCommand(command);
            return new Response(response);
        }
        // Read specific block
        else if (startBlock < 0 && endBlock < 0 && blockNum >= 0) {
            byte[] command = new byte[] {
                (byte)0x30,       // Read command
                (byte)blockNum,   // From specified block number
                (byte)0x00,
                (byte)0x00,       // To same block number
                (byte)0x00,
                (byte)0x04        // Number of bytes to read (4 bytes per block)
            };
            byte[] response = readerManager.sendCommand(command);
            return new Response(response);
        }
        else {
            throw new IllegalArgumentException("Invalid combination of parameters.");
        }
		
		
	}
	
}
