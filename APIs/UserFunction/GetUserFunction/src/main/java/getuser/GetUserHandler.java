package getuser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public static final Map<String, String> headers = createHeaders();
    public static final String BAD_USER_ID = "User Id received cannot be recognised.";
    public static final String GET_USER_QUERY = "select * from `user` where user_id = ?";
    public static final String DB_CONNECTION_ERROR = "Error in establishing database connection.";
    public static final String ERROR_BODY_RESPONSE = "Error in fetching user details.";
    public static boolean errorFlag = false;

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        if(Objects.isNull(input))throw new IllegalArgumentException("Input is null");
        if(Objects.isNull(context))throw new IllegalArgumentException("Context is null");

        LambdaLogger lambdaLogger = context.getLogger();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers).withIsBase64Encoded(false);
        int userId;
        try{
            if(Objects.nonNull(input.getQueryStringParameters()))
                userId = Integer.parseInt(input.getQueryStringParameters().get("userId"));
            else
                return response
                        .withStatusCode(400)
                        .withBody(BAD_USER_ID);
        }catch (JsonSyntaxException | NumberFormatException e){
            return response
                    .withStatusCode(400)
                    .withBody(BAD_USER_ID);
        }

        HashMap<String ,String> secret = GetSecret.getSecretValue();

        try(Connection con = DbConnection.getDbConnection(secret, lambdaLogger)){
            User user = getUser(con, userId, lambdaLogger);
            if(errorFlag){
                return response
                        .withStatusCode(500)
                        .withBody(ERROR_BODY_RESPONSE);
            }
            String jsonResponse = gson.toJson(user);
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

    private User getUser(Connection con, int userId, LambdaLogger lambdaLogger) {
        User user = new User();
        lambdaLogger.log("getUser() ");
        try(PreparedStatement getUserStmt = con.prepareStatement(GET_USER_QUERY)){
            getUserStmt.setInt(1,userId);
            ResultSet resultSet = getUserStmt.executeQuery();
            if(resultSet.next()){
                user.setUserId(resultSet.getInt("user_id"));
                user.setUserName(resultSet.getString("user_name"));
                user.setUserEmail(resultSet.getString("user_email"));
                user.setUserMobileNo(resultSet.getString("user_mobile_no"));
                user.setGender(resultSet.getString("gender"));
                user.setAge(resultSet.getInt("age"));
            }
        }catch (SQLException se){
            lambdaLogger.log("\ngetUser(). Error: " + se.getMessage());
            errorFlag = true;
        }
        return user;
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
