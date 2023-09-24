package com.logpose.ph2.batch.log;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Setter;

/**
 * Wrap LogEvent so that the serialization of context comes out as an object instead of
 * an array of key/values
 *
 * Created by johnbush on 4/11/16.
 */
@JsonFormat(shape= JsonFormat.Shape.OBJECT)
public class JsonLogEvent implements LogEvent {
    private LogEvent event;
    @Setter
    private Message mssg;
    
    public JsonLogEvent(LogEvent e) {
        this.event = e;
        context = e.getContextMap();
    }

    private Map context = new HashMap();

    public Map getContext() {
        return context;
    }

    public void setContext(Map context) {
        this.context = context;
    }

    @Override
    public Map<String, String> getContextMap() {
        return event.getContextMap();
    }

    @Override
    public ThreadContext.ContextStack getContextStack() {
        return event.getContextStack();
    }

    @Override
    public String getLoggerFqcn() {
        return event.getLoggerFqcn();
    }

    @Override
    public Level getLevel() {
        return event.getLevel();
    }

    @Override
    public String getLoggerName() {
        return event.getLoggerName();
    }

    @Override
    public Marker getMarker() {
        return event.getMarker();
    }

    @Override
	public Message getMessage() {
        return this.mssg;
    }
    
    @Override
    public long getTimeMillis() {
        return event.getTimeMillis();
    }

    @Override
    public StackTraceElement getSource() {
        return event.getSource();
    }

    @Override
    public String getThreadName() {
        return event.getThreadName();
    }

    @Override
    public Throwable getThrown() {
        return event.getThrown();
    }

    @Override
    public ThrowableProxy getThrownProxy() {
        return event.getThrownProxy();
    }

    @Override
    public boolean isEndOfBatch() {
        return event.isEndOfBatch();
    }

    @Override
    public boolean isIncludeLocation() {
        return event.isIncludeLocation();
    }

    @Override
    public void setEndOfBatch(boolean b) {
        event.setEndOfBatch(b);
    }

    @Override
    public void setIncludeLocation(boolean b) {
        event.setIncludeLocation(b);
    }

	@Override
	public LogEvent toImmutable() {
		return event.toImmutable();
	}

	@Override
	public ReadOnlyStringMap getContextData() {
		return event.getContextData();
	}

	@Override
	public Instant getInstant() {
		return event.getInstant();
	}

	@Override
	public long getThreadId() {
		return event.getThreadId();
	}

	@Override
	public int getThreadPriority() {
		return event.getThreadPriority();
	}

	@Override
	public long getNanoTime() {
		return event.getNanoTime();
	}
}
