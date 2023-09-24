package com.logpose.ph2.batch.log;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Plugin(name = "PoCJsonLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE, printObject = true)
public final class JsonLayout extends AbstractJacksonLayout {
	static final String CONTENT_TYPE = "application/json";

	private Pattern fieldPattern;
	private Pattern dataPattern;
	private String apptag;
	private String dbtag;
	private static final long serialVersionUID = 1L;

	protected JsonLayout(final boolean locationInfo, final boolean properties, final boolean complete,
			final boolean compact,
			final Charset charset) {
		super(new JacksonFactory.JSON().newWriter(locationInfo, properties, compact), charset, compact, complete);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String path = "src/main/resources/mask.json";
			LoggingMaskParameter config = objectMapper.readValue(Paths.get(path).toFile(), LoggingMaskParameter.class);
			apptag = config.getAppTag();
			dbtag = config.getDbTag();
			Collection<String> patterns = config.getFieldPatterns().values();
			fieldPattern = Pattern.compile(patterns.stream().collect(Collectors.joining("|")),
					Pattern.MULTILINE);
			patterns = config.getDataPatterns().values();
			dataPattern = Pattern.compile(patterns.stream().collect(Collectors.joining("|")),
					Pattern.MULTILINE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns appropriate JSON headers.
	 *
	 * @return a byte array containing the header, opening the JSON array.
	 */
	@Override
	public byte[] getHeader() {
		if (!this.complete) {
			return null;
		}
		final StringBuilder buf = new StringBuilder();
		buf.append('[');
		buf.append(this.eol);
		return buf.toString().getBytes(this.getCharset());
	}

	/**
	 * Returns appropriate JSON footer.
	 *
	 * @return a byte array containing the footer, closing the JSON array.
	 */
	@Override
	public byte[] getFooter() {
		if (!this.complete) {
			return null;
		}
		return (this.eol + ']' + this.eol).getBytes(this.getCharset());
	}

	@Override
	public Map<String, String> getContentFormat() {
		final Map<String, String> result = new HashMap<String, String>();
		result.put("version", "2.0");
		return result;
	}

	@Override
	/**
	 * @return The content type.
	 */
	public String getContentType() {
		return CONTENT_TYPE + "; charset=" + this.getCharset();
	}

	/**
	 * Creates a JSON Layout.
	 *
	 * @param locationInfo If "true", includes the location information in the generated JSON.
	 * @param properties If "true", includes the thread context in the generated JSON.
	 * @param complete If "true", includes the JSON header and footer, defaults to "false".
	 * @param compact If "true", does not use end-of-lines and indentation, defaults to "false".
	 * @param charset The character set to use, if {@code null}, uses "UTF-8".
	 * @return A JSON Layout.
	 */
	@PluginFactory
	public static AbstractJacksonLayout createLayout(
			// @formatter:off
			@PluginAttribute(value = "locationInfo", defaultBoolean = false) final boolean locationInfo,
			@PluginAttribute(value = "properties", defaultBoolean = false) final boolean properties,
			@PluginAttribute(value = "complete", defaultBoolean = false) final boolean complete,
			@PluginAttribute(value = "compact", defaultBoolean = false) final boolean compact,
			@PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset
	// @formatter:on
	) {
		return new JsonLayout(locationInfo, properties, complete, compact, charset);
	}

	/**
	 * Creates a JSON Layout using the default settings.
	 *
	 * @return A JSON Layout.
	 */
	public static AbstractJacksonLayout createDefaultLayout() {
		return new JsonLayout(false, false, false, false, Charsets.UTF_8);
	}

	/**
	 * Formats a {@link org.apache.logging.log4j.core.LogEvent}.
	 * 
	 * @param event The LogEvent.
	 * @return The XML representation of the LogEvent.
	 */
	@Override
	public String toSerializable(final LogEvent event) {
		try {
			JsonLogEvent evt = new JsonLogEvent(event);
			Message mssg = event.getMessage();
			String data = mssg.getFormattedMessage();

			//* tagをチェック
		if (data.startsWith(this.apptag)) {
				// * マスキング処理実行
				data = this.masking(this.fieldPattern, data);
				data = this.masking(this.dataPattern, data);
			} else if (data.startsWith(this.dbtag)) {
				data = this.masking(this.dataPattern, data);
			}
			FilteredMessage fmssg = new FilteredMessage(mssg, data);
			evt.setMssg(fmssg);
			String json = this.objectWriter.writeValueAsString(evt);
			return json;
		} catch (final JsonProcessingException e) {
			// Should this be an ISE or IAE?
			LOGGER.error(e);
			return Strings.EMPTY;
		}
	}

	private String masking(Pattern pattern, String message) {
		StringBuilder sb = new StringBuilder(message);
		Matcher matcher = pattern.matcher(sb);
		while (matcher.find()) {
			IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
				if (matcher.group(group) != null) {
					IntStream.range(matcher.start(group), matcher.end(group)).forEach(i -> sb.setCharAt(i, '*'));
				}
			});
		}
		return sb.toString();
	}
}
