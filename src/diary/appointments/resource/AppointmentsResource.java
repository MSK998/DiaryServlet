package diary.appointments.resource;

import java.util.*;

//Jersey
import javax.ws.rs.*;
import javax.ws.rs.core.*;

//AWS
//import com.amazonaws.regions.*;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
//import com.amazonaws.services.guardduty.model.City;

import aws.util.*;
import diary.appointments.config.*;
import diary.appointments.model.*;

@SuppressWarnings("serial")

@Path("/appointments")

public class AppointmentsResource {

	@POST
	@Produces(MediaType.TEXT_PLAIN)

	public Response addAppointment(@FormParam("date") long date, @FormParam("duration") int duration,
			@FormParam("description") String description, @FormParam("owner") String owner) {
		try {
			
			//checks the parameters passed in
			System.out.println("GOT CONNECTION");
			System.out.println(date);
			System.out.println(duration);
			System.out.println(description);
			System.out.println(owner);

			//creates a new appointments object
			Appointments appointment = new Appointments(null, date, duration, description, owner);

			//check the appointments object for parameters
			System.out.println(appointment.toString());

			//creates a mapper to save the appointment
			DynamoDBMapper mapper = DynamoDBUtil.getDBMapper(DBConfig.REGION, DBConfig.LOCAL_ENDPOINT);
			mapper.save(appointment);

			System.out.println("SUCCESS 200");

			//prints the time in  in RFC 822, 1036, 1123, 2822 format
			System.out.println(appointment.humanTime());

			//return 201 response
			return Response.status(201).entity(description + " on " + appointment.humanTime() + "added successfully")
					.build();

		} catch (Exception e) {
			System.out.println("FAILURE 400");

			//return 400 if the try fails
			return Response.status(400)
					.entity("Something went wrong. Parameters accepted: date, duration, description, owner")
					.build();

		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search/start={start}&end={end}&owner={owner}")

	public Iterable<Appointments> getAppointmentBetweenDates(@PathParam("start") long start, 
															@PathParam("end") long end,
															@PathParam("owner") String owner) {
		
		//check parameter
		System.out.println("GET REQUEST RECIEVED");
		System.out.println(start);
		System.out.println(end);
		System.out.println(owner);
		
		//creates a mapper to pull data from the server
		DynamoDBMapper mapper = DynamoDBUtil.getDBMapper(DBConfig.REGION, DBConfig.LOCAL_ENDPOINT);
		//creates an empty scan expression to pull all data from the data base
		/***
		 * Kit - i couldn't get the scanExpression to work with the parameters being passed in
		 * because of this i implemented a slow but working solution that involves searching through an 
		 * ArrayList of appointments objects, returning that
		 *
		 */
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		System.out.println("MAPPER AND SCAN CREATED");

		//Loads database into a list
		List<Appointments> appointmentList = mapper.scan(Appointments.class, scanExpression);

		System.out.println("SCANNING THE DATABASE INTO ARRAY");

		//creates an arraylist to put search results into
		List<Appointments> searchResults = new ArrayList<Appointments>();

		//loop through all database entries loaded into the list
		for (Appointments a : appointmentList) {
			//checks if date satisfies the search parameters
			if (a.getDate() >= start && a.getDate() <= end) {
				//checks if the owner matches the search parameter
				if (a.getOwner().equalsIgnoreCase(owner)) {
					//if all is true add to the search results array
					searchResults.add(a);
				}
			}
		}
		System.out.println("RETURNING RESULTS");
		//returns search results
		return searchResults;
	}

	@Path("/delete/id={id}")
	@DELETE
	public Response deleteOneAppointment(@PathParam("id") UUID id) {

		//Checking if the ID parameter was picked up correctly
		System.out.println("Deleting: " + id);
		
		//Creating a DynamoDB mapper
		DynamoDBMapper mapper = DynamoDBUtil.getDBMapper(DBConfig.REGION, DBConfig.LOCAL_ENDPOINT);
		//Sets an appointment object to the one found in the database matching the ID
		Appointments appointment = mapper.load(Appointments.class, id);

		//If the appointment is null return 204 data not found id not found
		if (appointment == null) {
			return Response.status(204).entity("appointment not found").build();
		}

		//uses mapper to delete the assigned appointment
		mapper.delete(appointment);
		
		//sends 200 response if successful
		return Response.status(200).entity("deleted").build();
	}
	
	//update request is POST due to issues involvin the PUT method.
	
	@Path("/update")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateAppointment(@FormParam("id") UUID id,
									@FormParam("date") long date,
									@FormParam("duration") int duration,
									@FormParam("description") String description,
									@FormParam("owner") String owner) {
		
		//checks the parameters that have been sent to the server
		System.out.println(id);
		System.out.println(date);
		System.out.println(duration);
		System.out.println(description);
		System.out.println(owner);
		
		try {
			//new mapper 
			DynamoDBMapper mapper = DynamoDBUtil.getDBMapper(DBConfig.REGION, DBConfig.LOCAL_ENDPOINT);
			
			//creates an object to check if an appointment exists with the passed id
			Appointments appointmentCheck = mapper.load(Appointments.class, id);
			
			//if the check object is null then return a 204
			if(appointmentCheck == null) {
				return Response.status(204).entity("Appointment not found").build();
			}else {
				//if its not empty then update the appointment with the other values that have been passed in
				Appointments appointmentUpdate = mapper.load(Appointments.class, id);
				appointmentUpdate.setDate(date);
				appointmentUpdate.setDescription(description);
				appointmentUpdate.setDuration(duration);
				appointmentUpdate.setOwner(owner);
				
				//check that id matches and the updated parameters have been accepted
				appointmentUpdate.toString();
				
				//saves the object to the database, will replace the current object with the same id
				mapper.save(appointmentUpdate);
			}
			//returns 200 if all works correctly
			return Response.status(200).entity("Appointment Changed").build();
			
		}catch(Exception e){
			
			System.out.println("FAILURE 400");

			//return 400 if the try fails
			return Response.status(400)
					.entity("Something went wrong. Parameters accepted: date, duration, description, owner")
					.build();
			
		}
		
	}

}
