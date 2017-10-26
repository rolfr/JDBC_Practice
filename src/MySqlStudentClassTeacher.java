import java.sql.*;
import java.util.Scanner;

public final class MySqlStudentClassTeacher extends StudentClassTeacher{
	public MySqlStudentClassTeacher()
	{
		this.setConnection();
	}
	
	public void setConnection()
	{
		// This will load the MySQL driver, each DB has its own driver
    	try
    	{   // MySQL database driver
    		Class.forName("com.mysql.jdbc.Driver");
    		
    		System.out.print("Please enter the database username:");    		
    		Scanner scan = new Scanner(System.in);
    		String username = scan.nextLine();
    		System.out.printf("Please enter the password for '%s':", username);
    		String password = scan.nextLine();
    		scan.close();
    		
    		// MySQL database on home server
    		this.connectionString = String.format("jdbc:mysql://rolfr-toshiba:3306/studentclassteacher?user=%s&password=%s", username, password);
    		this.connection = DriverManager.getConnection(this.connectionString);
    	}
    	catch (ClassNotFoundException ce)
    	{
    		System.out.printf("%s%n", ce);
    	}
    	catch (SQLException se)
    	{   // in case I want to do something specific later for each exception type
    		System.out.printf("%s%n", se);
    	}
	}
}
