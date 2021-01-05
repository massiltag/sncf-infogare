package fr.pantheonsorbonne.ufr27.miage.exception.mapper;

import fr.pantheonsorbonne.ufr27.miage.exception.RetardException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RetardExceptionMapper implements ExceptionMapper<RetardException> {

	
	@Override
	public Response toResponse(RetardException exception) {
		return Response.status(402,exception.getMessage()).build();
	}

}
