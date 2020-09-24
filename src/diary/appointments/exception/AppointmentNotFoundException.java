package diary.appointments.exception;

import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class AppointmentNotFoundException extends WebApplicationException {
	
	public AppointmentNotFoundException(UUID id) {
		super(Response.status(Response.Status.NOT_FOUND).entity("Appointment id " + id + " not found").type("text/plain").build());
	}
}
