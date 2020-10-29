package getuser;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class DbConnection {
    public static Connection getDbConnection(HashMap dbSecret, LambdaLogger lambdaLogger) throws SQLException {
        String url = "jdbc:" + dbSecret.get("engine")+"://"+dbSecret.get("host")+":"+dbSecret.get("port")+"/"+ dbSecret.get("dbInstanceIdentifier");
        String username = dbSecret.get("username").toString();
        String password = dbSecret.get("password").toString();

        lambdaLogger.log("Attempting to get connection to url: "+url);
        return  DriverManager.getConnection(url, username, password);
    }
}
