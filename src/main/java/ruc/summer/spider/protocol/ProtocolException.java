package ruc.summer.spider.protocol;

import java.io.Serializable;

/**
 * Base exception for all protocol handlers
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">xiatian</a>
 * @date 2009-5-8
 *
 */
public class ProtocolException extends Exception implements Serializable {

	private static final long serialVersionUID = -1092521307737871528L;

	public ProtocolException() {
		super();
	}

	public ProtocolException(String message) {
		super(message);
	}

	public ProtocolException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolException(Throwable cause) {
		super(cause);
	}

}
