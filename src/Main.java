import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

class test{
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String Dn = "com.mysql.cj.jdbc.Driver";
        Class.forName(Dn);
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String pass = "";
        Connection con = DriverManager.getConnection(url, user, pass);
        if (con != null)
            System.out.println("connection successfull");
        else
            System.out.println("failed");
        DatabaseMetaData dbmd=con.getMetaData();
        /*(1) String getDatabaseProductName() throws SQLException;*/
// @return database product name
        System.out.println(dbmd.getDatabaseProductName());//MySQL
        /*(2) String getDatabaseProductVersion() throws SQLException;*/
//@return database version number
        System.out.println(dbmd.getDatabaseProductVersion());
//5.5.5-10.4.28-MariaDB
        System.out.println(dbmd.getDatabaseMajorVersion());
//5
        System.out.println(dbmd.getDatabaseMinorVersion());
//5
    }
}

