package com.logpose.ph2.batch.log;

import java.nio.charset.Charset;

import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.databind.ObjectWriter;

abstract class AbstractJacksonLayout extends AbstractStringLayout {
	private static final long serialVersionUID = 1L;
	
	protected static final String DEFAULT_EOL = "\r\n";
	protected static final String COMPACT_EOL = Strings.EMPTY;
	protected final String eol;
	protected final ObjectWriter objectWriter;
	protected final boolean compact;
	protected final boolean complete;

	protected AbstractJacksonLayout(final ObjectWriter objectWriter, final Charset charset, final boolean compact,
			final boolean complete) {
		super(charset);
		this.objectWriter = objectWriter;
		this.compact = compact;
		this.complete = complete;
		this.eol = compact ? COMPACT_EOL : DEFAULT_EOL;
	}


}
