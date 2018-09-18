package de.pheromir.discordmusicbot.Exceptions;


public class HttpErrorException extends Exception {

	private static final long serialVersionUID = -7315273014977947244L;

	public HttpErrorException() {
		super();
	}
	
	public HttpErrorException(String message) {
		super(message);
	}
	
	public HttpErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public HttpErrorException(Throwable cause) {
		super(cause);
	}
}
