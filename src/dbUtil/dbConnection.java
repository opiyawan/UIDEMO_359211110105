package dbUtil;

import java.sql.Connection;

public class dbConnection {
    private static  final  String SQCONN = "jdbc:sqlite:school.sqlite";

    public static Connection getConnection()  {
        try{
            Class.forName("org.sqlite.JDBC");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}//class
