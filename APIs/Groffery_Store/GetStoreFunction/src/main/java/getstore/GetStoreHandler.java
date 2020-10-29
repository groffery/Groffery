package getstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.google.gson.JsonSyntaxException;

/**
 * Handler for requests to Lambda function.
 */
public class GetStoreHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public static final Map<String, String> headers = createHeaders();
    public static final String BAD_STORE_ID = "Store Id received cannot be recognised.";
    public static final String GET_STORE_QUERY = "select * from `store` where store_id = ?";
    public static final String DB_CONNECTION_ERROR = "Error in establishing database connection.";
    public static final String ERROR_BODY_RESPONSE = "Error in fetching store details.";
    public static boolean errorFlag = false;

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        if(Objects.isNull(input))throw new IllegalArgumentException("Input is null");
        if(Objects.isNull(context))throw new IllegalArgumentException("Context is null");

        LambdaLogger lambdaLogger = context.getLogger();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers).withIsBase64Encoded(false);
        int storeId;
        try{
            if(Objects.nonNull(input.getQueryStringParameters()))
                storeId = Integer.parseInt(input.getQueryStringParameters().get("storeId"));
            else
                return response
                        .withStatusCode(400)
                        .withBody(BAD_STORE_ID);
        }catch (JsonSyntaxException | NumberFormatException e){
            return response
                    .withStatusCode(400)
                    .withBody(BAD_STORE_ID);
        }

        HashMap<String ,String> secret = GetSecret.getSecretValue();

        try(Connection con = DbConnection.getDbConnection(secret, lambdaLogger)){
            Store store = getStore(con, storeId, lambdaLogger);
            if(errorFlag){
                return response
                        .withStatusCode(500)
                        .withBody(ERROR_BODY_RESPONSE);
            }
            String jsonResponse = gson.toJson(store);
            return response
                    .withStatusCode(200)
                    .withBody(jsonResponse);
        } catch (SQLException e) {
            lambdaLogger.log("Error: " + e.getMessage());
            return response
                    .withBody(DB_CONNECTION_ERROR)
                    .withStatusCode(500);
        }
    }

    private Store getStore(Connection con, int storeId, LambdaLogger lambdaLogger) {
        Store store = new Store();
        lambdaLogger.log("getStore() ");
        try(PreparedStatement getStoreStmt = con.prepareStatement(GET_STORE_QUERY)){
            getStoreStmt.setInt(1,storeId);
            ResultSet resultSet = getStoreStmt.executeQuery();
            if(resultSet.next()){
                store.setStoreId(resultSet.getInt("store_id"));
                store.setStoreName(resultSet.getString("store_name"));
                store.setStoreEmail(resultSet.getString("store_email"));
                store.setStorePhoneNo(resultSet.getString("store_phone_no"));
                store.setStoreManager(resultSet.getString("store_manager"));
                store.setStoreLocation(resultSet.getString("store_location"));
            }
        }catch (SQLException se){
            lambdaLogger.log("\ngetstore(). Error: " + se.getMessage());
            errorFlag = true;
        }
        return store;
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
