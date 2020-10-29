package addstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

/**
 * Handler for requests to Lambda function.
 */
public class AddStoreHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public static final Map<String, String> headers = createHeaders();
    public static final String DB_CONNECTION_ERROR = "Error in connecting to the database";
    public static final String SUCCESSFUL_RESPONSE_BODY = "Store data has been successfully added";
    public static final String BAD_INPUT_ERROR = "The values received weren't as expected.";
    public static final String ERROR_RESPONSE_BODY = "There has been an error in adding store details.";
    public static final String ADD_STORE_QUERY = "insert into store(store_name, store_email, store_phone_no, store_manager, store_location) values (?, ?, ? ,? ,?)";
    public static boolean errorFlag = false;

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        if(Objects.isNull(input))throw new IllegalArgumentException("Input is null");
        if(Objects.isNull(context))throw new IllegalArgumentException("Context is null");

        LambdaLogger lambdaLogger = context.getLogger();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers).withIsBase64Encoded(false);

        Store storeDetails = null;
        try{
            storeDetails = gson.fromJson(input.getBody(), Store.class);
        } catch(JsonParseException je){
            lambdaLogger.log("\nAddStoreHandler() :: Error in parsing the request body. Error: " + je.getMessage());
            response.withBody(BAD_INPUT_ERROR).withStatusCode(400);
        }

        if(Objects.isNull(storeDetails)){
            response.withBody("No request body received.").withStatusCode(400);
        }

        HashMap<String ,String> secret = GetSecret.getSecretValue();

        try(Connection con = DbConnection.getDbConnection(secret, lambdaLogger)){
            addStore(con, lambdaLogger, storeDetails);
            if(errorFlag){
                return response
                        .withBody(ERROR_RESPONSE_BODY)
                        .withStatusCode(500);
            }
            return response
                    .withStatusCode(200)
                    .withBody(SUCCESSFUL_RESPONSE_BODY);
        } catch (SQLException e) {
            lambdaLogger.log("\nError: " + e.getMessage());
            return response
                    .withBody(DB_CONNECTION_ERROR)
                    .withStatusCode(500);
        }
    }

    private void addStore(Connection con, LambdaLogger lambdaLogger, Store storeDetails) {
        try(PreparedStatement addStoreStatement = con.prepareStatement(ADD_STORE_QUERY)){
            addStoreStatement.setString(1, storeDetails.getStoreName());
            addStoreStatement.setString(2, storeDetails.getStoreEmail());
            addStoreStatement.setString(3, storeDetails.getStorePhoneNo());
            addStoreStatement.setString(4, storeDetails.getStoreManager());
            addStoreStatement.setString(5, storeDetails.getStoreLocation());

            if(addStoreStatement.executeUpdate() != 1)
                errorFlag = true;
        }catch(SQLException se){
            lambdaLogger.log("AddStoreHandler.addStore(): ERROR: " + se.getMessage());
            errorFlag = true;
        }
    }

    private static Map<String, String> createHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Access-Control-Allow-Headers", "*");

        return headers;
    }
}
