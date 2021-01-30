package netNation;
import java.sql.*;
public class DB_Connector {
	// make sure you change url,username and password 
	private static String url = "jdbc:mysql://localhost/netnation?allowMultiQueries=true";
        private static String driverName = "com.mysql.cj.jdbc.Driver";
        private static String username = "user";
        private static String password = "password";
        private static Connection con;

        public static Connection getConnection() {
                try {
                        Class.forName(driverName);
                        try {
                                con = DriverManager.getConnection(url, username, password);
                        } catch (SQLException ex) {
                        // log an exception. fro example:
                        System.out.println("Failed to create the database connection.");
                        }
                } catch (ClassNotFoundException ex) {
                        // log an exception. for example:
                        System.out.println("Driver not found.");
                }
                return con;
        }
        public static void closeConnection(){
                if (con != null) {
                	try {
                        	con.close();
                        } catch (SQLException e) {
				System.out.println(e.getSQLState());
			}
                }
        }
        public static void executeQuery(String query){
                if (con != null) {
                        try {
                                Statement st = con.createStatement();
                                st.executeUpdate(query);
                                st.close();
                        } catch(SQLException e){
                                System.out.println(e.getSQLState());
                        }
                } else {
                        System.out.println("No Database Connection.");
                }
        }
}
