package com.logpose.ph2.batch.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PoCLogger {
	private Logger LOG = LogManager.getLogger("app");

	public void log(Level level, String mssg, Object... args) {
		ObjectMapper objectWriter = new ObjectMapper();
		StringBuilder builder = new StringBuilder();
		builder.append("app:");
		builder.append(mssg);
		builder.append(": [");
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (null == arg) {
				builder.append("null, ");
			} else {
				String packageName = arg.getClass().getPackage().getName();
				if (packageName.startsWith("java.lang")) {
					builder.append("\"");
					builder.append(arg.toString());
					builder.append("\", ");
				} else {
					builder.append("{");
					try {
						builder.append(objectWriter.writeValueAsString(arg));
					} catch (JsonProcessingException e) {
						builder.append(arg.toString());
					}
					builder.append("},");
				}
			}
		}
		builder.append("]");
		LOG.log(level, builder.toString());
	}
}
