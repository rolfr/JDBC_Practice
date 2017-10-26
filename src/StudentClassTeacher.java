import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class StudentClassTeacher implements IStudentClassTeacher {
	protected String connectionString;
	protected Connection connection = null;
	private static String[] initStatements;
	private HashMap<String, String> sqlStatementMappings;
	
	static {
		initStatements = new String[6];
		initStatements[0] = "DROP TABLE StudentClass";
		initStatements[1] = "DROP TABLE class";
		initStatements[2] = "DROP TABLE person";
		initStatements[3] = "CREATE TABLE Person (" +
							"PersonId INT PRIMARY KEY NOT NULL AUTO_INCREMENT, " + 
							"Name VARCHAR(40) NOT NULL);";
		initStatements[4] = "CREATE TABLE Class (" +
							"ClassId INT PRIMARY KEY NOT NULL AUTO_INCREMENT, " +
							"Name VARCHAR(35) NOT NULL, " +
							"TeacherId INT NOT NULL);";
		initStatements[5] = "CREATE TABLE StudentClass (" +
							"StudentId INT NOT NULL, " +
							"ClassId INT NOT NULL, " +
							"Grade VARCHAR(2));";		
	}

	public Connection getConnection()
	{
		if (this.connection == null)
			this.setConnection();
		return this.connection;
	}
	
	public abstract void setConnection();
	
	protected final void addSqlStatementMapping(String toReplace, String replaceWith) {
		this.sqlStatementMappings.put(toReplace, replaceWith);
	}
	
	public void initTables() {
    	try
    	{
    		Statement statement = this.connection.createStatement();
    		for (String batch : initStatements)
    		{
    			for (String mapping : sqlStatementMappings.keySet())
    			{
    				batch = batch.replaceAll(mapping, sqlStatementMappings.get(mapping));
    			}
    			statement.addBatch(batch);
    		}
    		statement.executeBatch();
    		statement.close();
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
    		Statement statement = this.connection.createStatement();
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
	    	PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO person (Name) VALUES " + nameList);

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
	    	PreparedStatement statement = this.connection.prepareStatement("INSERT INTO class (Name, TeacherId) VALUES " + values);
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
    
    public void registerStudents(int minCourseload, int maxCourseload)
    {
    	ArrayList<Integer> students = new ArrayList<Integer>();
    	ArrayList<Integer> classes = new ArrayList<Integer>();
    	
    	try
    	{
    		// get the list of students (persons who aren't teachers)
    		Statement statement = this.connection.createStatement();
    		ResultSet resultSet = statement.executeQuery("SELECT PersonId FROM person "
    													+ "WHERE person.PersonId NOT IN ( "
    													+ " SELECT TeacherId FROM class)");
    		while (resultSet.next() == true)
    			students.add(resultSet.getInt(1));  
    		
    		// get the list of classes
    		resultSet = statement.executeQuery("SELECT ClassId FROM class");
    		while (resultSet.next() == true)
    			classes.add(resultSet.getInt(1));
    		
    		// for each student, assign them to 'minCourseload' to 'maxCourseload' classes
    		HashMap<Integer, ArrayList<Integer>> mappings = new HashMap<Integer, ArrayList<Integer>>();
    		String values = "";
    		for (int student : students)
    		{
    			int courseLoad = (int)(Math.random() * (maxCourseload - minCourseload) + minCourseload);
    			ArrayList<Integer> picks = new ArrayList<Integer>();
    			for (int classChoice = 0; classChoice < courseLoad; )
    			{
    				int choice = (int)(Math.random() * classes.size());
    				if (!picks.contains(choice))
					{
    					classChoice++;
    					picks.add(choice);
    	    			values += "(?,?),";
					}
    			}
    			mappings.put(student, picks);
    		}
    		values = values.substring(0, values.length() - 1);
    		
    		// 
    		PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO StudentClass (StudentId, ClassId) VALUES " + values);
    		int valueCount = 1;
    		for (int studentId : students)
    		{
    			for (int classId : mappings.get(studentId))
    			{
    				preparedStatement.setInt(valueCount++, studentId);
    				preparedStatement.setInt(valueCount++, classId);
    			}
    		}
    		preparedStatement.execute();
    		preparedStatement.close();
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
	    	Statement statement = this.connection.createStatement();
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
            if (this.connection != null) {
            	this.connection.close();
            }
        } catch (Exception e) {
        	System.out.printf("%s%n", e);
        }
    }
}
