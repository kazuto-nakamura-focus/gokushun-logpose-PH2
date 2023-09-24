package com.logpose.ph2.batch.log;

import java.util.Map;

import lombok.Data;

@Data
public class LoggingMaskParameter {
	private String appTag;
	private String dbTag;
	private Map<String, String> fieldPatterns;
	private Map<String, String> dataPatterns;
}
