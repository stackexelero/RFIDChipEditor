package com.stackexcelero.utility;

import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.TerminalFactory;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

@SuppressWarnings("restriction")
public class CardReaderManager {
	
	private static CardReaderManager instance = null;
	
    private TerminalFactory terminalFactory;
    private CardTerminal cardTerminal;
    private Card card;
    private final int maxTransceiveLength = 253;
    
    private CardReaderManager() {
        terminalFactory = TerminalFactory.getDefault();
    }
    
    public static CardReaderManager getInstance() {
        if (instance == null) {
            instance = new CardReaderManager();
        }
        return instance;
    }
    
    public void connect() throws CardException {
        if (card == null) {
            List<CardTerminal> terminals = terminalFactory.terminals().list();
            if (terminals.isEmpty()) {
                throw new CardException("No card terminals available.");
            }
            cardTerminal = terminals.get(0);
            card = cardTerminal.connect("*");
        }
    }
    
    public void disconnect() throws CardException {
        if (card != null) {
            card.disconnect(true);
            card = null;
        }else
        	throw new CardException("Card is not connected. Please call connect() first.");
    }
    
    public byte[] sendCommand(byte[] command) throws CardException {
    	if (card == null) {
            throw new CardException("Card is not connected. Please call connect() first.");
        }
    	if (command.length > maxTransceiveLength) {
            throw new IllegalArgumentException("Command length exceeds maximum transceive length of 253");
        }
        CommandAPDU apdu = new CommandAPDU(command);
        ResponseAPDU response = card.getBasicChannel().transmit(apdu);
        return response.getBytes();
    }
}
