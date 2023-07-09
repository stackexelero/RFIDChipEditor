package com.stackexcelero.utility;

import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import com.stackexcelero.command.ReadCommand;
import com.stackexcelero.model.Response;
import com.stackexcelero.reader.NTAG216ReaderFacade;

@SuppressWarnings("restriction")
public class demo {

	public static void main(String[] args) throws Exception {
		//demo1();
		test();
	    
		System.exit(0);
	}
	
	private static void test() throws Exception {
		NTAG216ReaderFacade reader = new NTAG216ReaderFacade();
		Response res = reader.executeCommand(new ReadCommand());
		for(int i=0;i<res.getData().length;i++) {
			System.out.println(res.getData()[i]);
		}
	}

	private static void demo1() throws CardException {
		// Display the list of terminals
	    TerminalFactory factory = TerminalFactory.getDefault();
	    List<CardTerminal> terminals = factory.terminals().list();
	    System.out.println("Terminals: " + terminals);
	
	    // Use the first terminal
	    CardTerminal terminal = terminals.get(0);
	
	    // Connect wit hthe card
	    Card card = terminal.connect("*");
	    System.out.println("Card: " + card);
		//CardChannel channel = card.getBasicChannel();
		
		
		
	    byte[] byteArr = readData(card, (byte) 0x02);
	    System.out.println(Utility.byteArrayToHexString(byteArr));
	    for(int i=0;i<byteArr.length;i++) {
	    	System.out.printf("%02X ", byteArr[i]);
	    }
	}

	public static byte[] readData(Card c, byte block) throws CardException {
		byte cla = (byte) 0xFF;  
		byte ins = (byte) 0xB0;
		byte p1 = (byte) 0x00;
		byte p2 = (byte) 0x04;
		byte lc = (byte) 0x04; // Length of command data
		byte d1 = (byte) 0x00;
		byte d2 = (byte) 0x00;
		byte le = (byte) 0x04; // Expected length of response data

		// Put content of request into an array
		byte[] params = new byte[] { cla, ins, p1, p2, lc, d1, d2, le };
        
        //Open card channel to transmit data
        CardChannel channel = c.getBasicChannel();
        
        //set APDU command with my request data 
        CommandAPDU command = new CommandAPDU(params);
        
        //Transmit the data through the channel using APDU command
        ResponseAPDU response = channel.transmit(command);
        
        //Validates the response
        //validateResponse(response);
        
        //Returns the response in array of bytes
        return response.getData();
    }
	
	private static void validateResponse(ResponseAPDU response)
            throws CardException {
        if (response.getSW1() != 144) {
            throw new CardException(
                    "No fue posible cargar la autenticaci?n");
        }
    }

}
