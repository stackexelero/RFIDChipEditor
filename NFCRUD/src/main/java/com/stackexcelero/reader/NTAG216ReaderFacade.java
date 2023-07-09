package com.stackexcelero.reader;

import javax.smartcardio.CardException;

import com.stackexcelero.command.Command;
import com.stackexcelero.model.Response;
import com.stackexcelero.utility.CardReaderManager;

@SuppressWarnings("restriction")
public class NTAG216ReaderFacade {
	private CardReaderManager readerManager;
	
	public NTAG216ReaderFacade() {
        readerManager = CardReaderManager.getInstance();
    }
	
	private void connect() throws CardException {
        readerManager.connect();
    }

    private void disconnect() throws CardException {
        readerManager.disconnect();
    }
    
    public Response executeCommand(Command command) throws Exception {
    	connect();
    	Response response = null;
    	try {
    		response = command.execute();
    	} catch (CardException e) {
    	    System.err.println("CardException: " + e.getMessage());
    	} finally {
    	    try {
    	    	disconnect();
    	    } catch (CardException e) {
    	        System.err.println("Error disconnecting: " + e.getMessage());
    	    }
    	}
    	
    	return response;
    }
}
