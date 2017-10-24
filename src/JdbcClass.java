import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Scanner;

public class JdbcClass {
	private static Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    
    static {
    	// This will load the MySQL driver, each DB has its own driver
    	try
    	{
    		Class.forName("com.mysql.jdbc.Driver");
    	}
    	catch (ClassNotFoundException e)
    	{
    		System.out.printf("%s", e);
    	}
    	
        // Setup the connection with the DB
    	try
    	{
    		System.out.print("Please enter the database password:");
    		Scanner scan = new Scanner(System.in);
    		String password = scan.next();
    		scan.close();
    		
    		connect = DriverManager.getConnection("jdbc:mysql://rolfr-toshiba:3306/studentclassteacher?user=rolf&password=" + password);
    	}
    	catch (SQLException e)
    	{
    		System.out.printf("%s",  e);
    	}
    }
    
    public void insertPerson(String name)
    {
    	try
    	{
	    	statement = connect.createStatement();
	    	statement.execute("INSERT INTO person (Name) VALUES ('" + name + "')");
    	}
    	catch (SQLException e)
    	{
    		System.out.printf("%s", e);
    		return;
    	}    	

    	System.out.printf("Successfully added person %s.", name);
    }
    
    public void insertPersons(String[] names)
    {
    	try
    	{
	    	String nameList = "";
	    	for(int i = 1; i <= names.length; i++)
	    	{
	    		nameList += "(?),";
	    	}
	    	nameList = nameList.substring(0, nameList.length() - 1);
	    	preparedStatement = connect.prepareStatement("INSERT INTO person (Name) VALUES " + nameList);

	    	// Because I apparently can't instantiate a PreparedStatement without a SQL statement,
	    	//  I need to use a separate for loop to apply the .setString values afterwards.
	    	for(int i = 1; i <= names.length; i++)
	    	{
	    		preparedStatement.setString(i,  names[i - 1]);
	    	}
	    	preparedStatement.executeUpdate();
    	}
    	catch (SQLException e)
    	{
    		System.out.printf("%s", e);
    		return;
    	}
    	
    	System.out.printf("Successfully added %d person records.", names.length);
    }
    
    public void insertClass(String name)
    {
    	int teacherId = this.getTeacherId("ion");
    	try
    	{
	    	statement = connect.createStatement();
	    	statement.execute("INSERT INTO class (Name, TeacherId) VALUES ('" + name + teacherId + "')");
    	}
    	catch (SQLException e)
    	{
    		System.out.printf("%s", e);
    		return;
    	}    	

    	System.out.printf("Successfully added person %s.", name);
    }
    
    public void insertClasses(String[] classNames)
    {
    	
    }
    
    public int getTeacherId(String suffix)
    {
    	return 0;
    	//statement = connect.
    }

    // You need to close the resultSet
    public void finalize() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
        	System.out.printf("%s", e);
        }
    }
}
