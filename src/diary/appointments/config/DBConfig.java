package diary.appointments.config;

public class DBConfig {
	
	//sets the database name
	public static final String DYNAMODB_TABLE_NAME="diary-appointments";
	
	//Sets the region to the local-host
	public static final String REGION = "local";
	
	//Sets the end-point of the DynamoDB
	public static final String LOCAL_ENDPOINT = "http://localhost:8000";

}
