package com.logpose.ph2.batch.log;

import org.apache.logging.log4j.message.Message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FilteredMessage implements  Message {
	
	private Message sourceMessage;
	private String message;
	
	@Override
	public String getFormattedMessage() {
		return message;
	}

	@Override
	public String getFormat() {
		return this.sourceMessage.getFormat();
	}

	@Override
	public Object[] getParameters() {
		return this.sourceMessage.getParameters();
	}

	@Override
	public Throwable getThrowable() {
		return this.sourceMessage.getThrowable();
	}
}