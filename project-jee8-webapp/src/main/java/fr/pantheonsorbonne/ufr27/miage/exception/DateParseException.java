package fr.pantheonsorbonne.ufr27.miage.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DateParseException extends Exception implements ExceptionMapper<DateParseException> {
	private static final long serialVersionUID = 1L;

	public DateParseException() {
		super("Bad Date Format");
	}

	public DateParseException(String string) {
		super(string);
	}

	@Override
	public Response toResponse(DateParseException exception) {
		return Response.status(Status.BAD_REQUEST).entity(exception.toString()).type("text/plain").build();
	}
}