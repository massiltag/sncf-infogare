package fr.pantheonsorbonne.ufr27.miage.exception;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import fr.pantheonsorbonne.ufr27.miage.exception.UserHasDebtException;

@Provider
public class MembershipCancellationDeniedExceptionMapper implements ExceptionMapper<UserHasDebtException> {

	
	@Override
	public Response toResponse(UserHasDebtException exception) {
		try {
			return Response.status(402,"please pay your dept ("+exception.getDebt()+")").location(new URI("payment/"+exception.getUserId())).build();
		} catch (URISyntaxException e) {
			return Response.serverError().build();
		}
	}

}
