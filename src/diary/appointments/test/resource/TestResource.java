package diary.appointments.test.resource;

//JAX-RS
import javax.ws.rs.*;
import javax.ws.rs.core.*;

//AWS SDK
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.datamodeling.*;

import aws.util.*;
import diary.appointments.model.*;

@Path("/test")
public class TestResource
{
@GET
@Produces(MediaType.TEXT_PLAIN)
public Response dummyGet()
{
return Response.status(200).entity("Congratulations! Jersey is working!").build();
} //end method
} //end class
