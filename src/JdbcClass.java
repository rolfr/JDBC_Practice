import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class JdbcClass {
	private static Connection connect = null;    
    static {
    	// This will load the MySQL driver, each DB has its own driver
    	try
    	{
    		Class.forName("com.mysql.jdbc.Driver");
    	}
    	catch (ClassNotFoundException e)
    	{
    		System.out.printf("%s%n", e);
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
    		System.out.printf("%s%n",  e);
    	}
    }
    
    public JdbcClass()
    {
    	this.Init();
    }
    
    private void Init()
    {
    	try
    	{
    		Statement statement = connect.createStatement();
    		statement.addBatch("DROP TABLE StudentClass");
    		statement.addBatch("DROP TABLE class");
    		statement.addBatch("DROP TABLE person");
    		statement.addBatch("CREATE TABLE Person ( "
    							+ "PersonId INT PRIMARY KEY NOT NULL AUTO_INCREMENT, "
    							+ "Name VARCHAR(40) NOT NULL)");
    		statement.addBatch("CREATE TABLE Class ( "
    							+ "ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT, "
    							+ "Name VARCHAR(35) NOT NULL, "
    							+ "TeacherId INT NOT NULL)");
    		statement.addBatch("CREATE TABLE StudentClass ( "
    							+ "StudentId INT NOT NULL, "
    							+ "ClassId INT NOT NULL,Grade VARCHAR(2))");
    		statement.executeBatch();
    	}
    	catch (SQLException e)
    	{
    		System.out.println("Failed to reset database!");
    	}
    }
    
    public void insertPerson(String name)
    {
    	try
    	{
    		Statement statement = connect.createStatement();
	    	statement.execute("INSERT INTO person (Name) VALUES ('" + name + "')");
	    	statement.close();
    	}
    	catch (SQLException e)
    	{
    		System.out.printf("%s%n", e);
    		return;
    	}

    	System.out.printf("Successfully added person %s.%n", name);
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
	    	PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO person (Name) VALUES " + nameList);

	    	// Because I apparently can't instantiate a PreparedStatement without a SQL statement,
	    	//  I need to use a separate for loop to apply the .setString values afterwards.
	    	for(int i = 1; i <= names.length; i++)
	    	{
	    		preparedStatement.setString(i,  names[i - 1]);
	    	}
	    	preparedStatement.executeUpdate();
    		preparedStatement.close();
	    	System.out.printf("Successfully added %d person records.%n", names.length);
    	}
    	catch (SQLException e)
    	{
    		System.out.printf("%s%n", e);
    		return;
    	}	
    }
    
    public void insertClasses(String[] classNames, String teacherLastNamesEndWith)
    {
    	ArrayList<Model.Person> teachers = this.getTeachers(teacherLastNamesEndWith);
    	String values = "";
    	HashMap<String, Integer> classTeacherMappings = new HashMap<String, Integer>();
    	for (String className : classNames)
    	{
    		int teacherIndex = (int)(Math.random() * teachers.size());
    		values += "(?,?),";
    		classTeacherMappings.put(className, teachers.get(teacherIndex).id);
    	}
    	values = values.substring(0, values.length() - 1);
    	
    	try
    	{
	    	PreparedStatement statement = connect.prepareStatement("INSERT INTO class (Name, TeacherId) VALUES " + values);
	    	for (int parameterIndex = 1; parameterIndex <= classNames.length * 2; parameterIndex++)
	    	{
	    		statement.setString(parameterIndex, classNames[parameterIndex / 2]);
	    		parameterIndex++;
	    		statement.setInt(parameterIndex, classTeacherMappings.get(classNames[parameterIndex / 2 - 1]));
	    	}
	    	statement.execute();
    		statement.close();
        	System.out.printf("Successfully added %d Classes.%n", classNames.length);  
    	}
    	catch (SQLException e)
    	{
    		System.out.printf("%s%n", e);
    	}
    	  	
    }
    
    // Select persons with a last name ending in 'suffix'
    private ArrayList<Model.Person> getTeachers(String suffix)
    {
    	ArrayList<Model.Person> al = new ArrayList<Model.Person>();
    	try
    	{
	    	Statement statement = connect.createStatement();
	    	ResultSet resultSet = statement.executeQuery("SELECT * FROM person WHERE RIGHT(Name, " + suffix.length() + ") = '" + suffix + "'");
    	    
	    	while (resultSet.next() == true)
	    	{
	    		Model model = new Model();
	    		model.person.id = resultSet.getInt(1);
	    		model.person.name = resultSet.getString(2);
	    		al.add(model.person);	    		
	    	}
	    	resultSet.close();
    		statement.close();
    	}
    	catch (SQLException e)
    	{
    		System.out.printf("%s%n", e);
    		return null;
    	}
    	
    	return al;    	
    }

    // You need to close the resultSet
    public void finalize() {
        try {
            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
        	System.out.printf("%s%n", e);
        }
    }
}
