package com.stackexcelero.command;

import com.stackexcelero.model.Response;
import javax.smartcardio.CardException;

@SuppressWarnings("restriction")
public interface Command {
	Response execute() throws CardException;
}
