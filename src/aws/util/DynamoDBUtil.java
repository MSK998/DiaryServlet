package aws.util;


import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.datamodeling.*;

public class DynamoDBUtil {
	
	//class variables
	private static AmazonDynamoDB dbClient = null;
	private static DynamoDBMapper mapper = null;
	
	/**
	 * This method provides a handy way to get a <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/dynamodbv2/datamodeling/DynamoDBMapper.html">DynamoDBMapper</a> object.
	 * The same object is reused in different requests.
	 * 
	 * @param region		The AWS region to connect to. e.g. "eu-west-1".
	 * 					To connect to a local server, use "local".
	 * @parm endPoint	The URL of the local DynamoDB server. e.g. http://localhost:8000
	 * 					This parameter is only used if region is specified as "local".
	 * @return	A DynamoDBMapper object for accessing DynamoDB.
	 */
	
	public static DynamoDBMapper getDBMapper(String region, String endPoint) {
		
		if(DynamoDBUtil.mapper == null) { // no mapper
			DynamoDBUtil.dbClient = getDynamoDBClient (region, endPoint); //get the client
			DynamoDBUtil.mapper = new DynamoDBMapper(dbClient); //create a mapper object
		}
		
		return DynamoDBUtil.mapper;
	}
	
	public static AmazonDynamoDB getDynamoDBClient(String region, String endPoint) {
		if(DynamoDBUtil.dbClient != null) { //if a client exists
			return DynamoDBUtil.dbClient; //return it
		}
		else {
			
			//create one
		
		AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();
		
			if(region.equals("local")) { //if local then set endpoint to the url in config
			EndpointConfiguration epConfig = new AwsClientBuilder.EndpointConfiguration(endPoint, region);
			builder.setEndpointConfiguration(epConfig);
			}
			
			else {//use specified region if not local
			builder.setRegion(region);
			}
		
			return builder.build(); //build and return the client
		}
	}
}
