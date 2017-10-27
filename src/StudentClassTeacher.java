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
	private HashMap<String, String> sqlStatementMappings = new HashMap<String,String>();
	protected int queryParameterLimit = 0;
	
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

	public Connection getConnection() {
		if (this.connection == null)
			this.setConnection();
		return this.connection;
	}
	
	public abstract void setConnection();
	
	protected final void addSqlStatementMapping(String toReplace, String replaceWith) {
		this.sqlStatementMappings.put(toReplace, replaceWith);
	}
	
	public void initTables() {
    	try {
    		Statement statement = this.connection.createStatement();
    		for (String batch : initStatements) {
    			for (String mapping : sqlStatementMappings.keySet()) {
    				batch = batch.replaceAll(mapping, sqlStatementMappings.get(mapping));
    			}
    			statement.addBatch(batch);
    		}
    		statement.executeBatch();
    		statement.close();
    	}
    	catch (SQLException e) {
    		System.out.println("Failed to reset database!");
    	}
	}
	
    public void insertPerson(String name) {
    	try {
    		Statement statement = this.connection.createStatement();
	    	statement.execute("INSERT INTO person (Name) VALUES ('" + name + "')");
	    	statement.close();
    	}
    	catch (SQLException e) {
    		System.out.printf("%s%n", e);
    		return;
    	}

    	System.out.printf("Successfully added person %s.%n", name);
    }
    
    public void insertPersons(String[] names) {
    	try {
    		// this batching code should be refactored out
    		int paramLimit = (this.queryParameterLimit > 0) ? this.queryParameterLimit : names.length;
    		
			int fullBatches = names.length / paramLimit;
			int leftoverItems = names.length % paramLimit;
			int batches = 0;
			if (leftoverItems > 0)
				batches = fullBatches + 1;
			else {
				leftoverItems = paramLimit;
				batches = fullBatches;
			}
    	
	    	// some transactions need to be broken up into batches, particularly against cloud services
	    	for (int batchIndex = 1; batchIndex <= batches; batchIndex++) {
		    	String nameList = "";
		    	int batchStart = 1 + ((batchIndex - 1) * paramLimit); // e.g. for limit of 1000: 1, 1001, 2001, ...
		    	int batchEnd = (batchIndex < batches) 
		    					? batchIndex * paramLimit             // e.g. for limit of 1000: 1000, 2000, 3000, ...
		    					: batchStart + leftoverItems - 1;
		    	
		    	for (int i = batchStart; i <= batchEnd; i++) {
		    		nameList += "(?),";
		    	}
		    	nameList = nameList.substring(0, nameList.length() - 1);
		    	PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO person (Name) VALUES " + nameList);
	
		    	// Because I apparently can't instantiate a PreparedStatement without a SQL statement,
		    	//  I need to use a separate for loop to apply the .setString values afterwards.
		    	int paramCount = 1;
		    	for(int i = batchStart; i <= batchEnd; i++) {
		    		preparedStatement.setString(paramCount++,  names[i - 1]);
		    	}
		    	preparedStatement.executeUpdate();
	    		preparedStatement.close();
	    	}
	    	System.out.printf("Successfully added %d person records.%n", names.length);
    	}
    	catch (SQLException e) {
    		System.out.printf("%s%n", e);
    		return;
    	}	
    }
    
    public void insertClasses(String[] classNames, String teacherLastNamesEndWith)
    {
		// this batching code should be refactored
		int paramLimit = (this.queryParameterLimit > 0) ? this.queryParameterLimit : classNames.length;

		int fullBatches = classNames.length / paramLimit;
		int leftoverItems = classNames.length % paramLimit;
		int batches = 0;
		if (leftoverItems > 0)
			batches = fullBatches + 1;
		else {
			leftoverItems = paramLimit;
			batches = fullBatches;
		}
	
    	// some transactions need to be broken up into batches, particularly against cloud services
    	for (int batchIndex = 1; batchIndex <= batches; batchIndex++) {

	    	int batchStart = 1 + ((batchIndex - 1) * paramLimit); // e.g. for limit of 1000: 1, 1001, 2001, ...
	    	int batchEnd = (batchIndex < batches) 
	    					? batchIndex * paramLimit  // e.g. for limit of 1000: 1000, 2000, 3000, ...
	    					: batchStart + leftoverItems - 1;
	    	ArrayList<Model.Person> teachers = this.getTeachers(teacherLastNamesEndWith);
	    	String values = "";
	    	HashMap<String, Integer> classTeacherMappings = new HashMap<String, Integer>();
	    	
	    	for (int classIndex = batchStart; classIndex <= batchEnd; classIndex++) {
	    		int teacherIndex = (int)(Math.random() * teachers.size());
	    		values += "(?,?),";
	    		classTeacherMappings.put(classNames[classIndex - 1], teachers.get(teacherIndex).id);
	    	}
	    	values = values.substring(0, values.length() - 1);
	    	
	    	try {
		    	PreparedStatement statement = this.connection.prepareStatement("INSERT INTO class (Name, TeacherId) VALUES " + values);
		    	int parameterIndex = 1;
		    	for (int classIndex = batchStart; classIndex <= batchEnd; classIndex++) {
		    		statement.setString(parameterIndex++, classNames[classIndex - 1]);
		    		statement.setInt(parameterIndex++, classTeacherMappings.get(classNames[classIndex - 1]));
		    	}
		    	statement.execute();
	    		statement.close();
	        	System.out.printf("Successfully added %d Classes.%n", classNames.length);  
	    	}
	    	catch (SQLException e) {
	    		System.out.printf("%s%n", e);
	    	}
		}    	  	
    }
    
    public void registerStudents(int minCourseload, int maxCourseload) {
    	ArrayList<Integer> students = new ArrayList<Integer>();
    	ArrayList<Integer> classes = new ArrayList<Integer>();
    	int totalClassRegistrations = 0;
    	
    	try {
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
    		
    		// this batching code should be refactored
    		// the resulting INSERT query has 2 params per student/class pair.  There can be up to (students * maxCourseload) entries to insert
    		int paramLimit = (this.queryParameterLimit > 0) ? this.queryParameterLimit / maxCourseload : students.size();
			int fullBatches = students.size() / paramLimit;
			int leftoverItems = students.size() % paramLimit;
			int batches = 0;
			if (leftoverItems > 0)
				batches = fullBatches + 1;
			else {
				leftoverItems = paramLimit;
				batches = fullBatches;
			}
			
	    	// some transactions need to be broken up into batches, particularly against cloud services
	    	for (int batchIndex = 1; batchIndex <= batches; batchIndex++) {

		    	int batchStart = 1 + ((batchIndex - 1) * paramLimit); // e.g. for limit of 1000: 1, 1001, 2001, ...
		    	int batchEnd = (batchIndex < batches) 
		    					? batchIndex * paramLimit  // e.g. for limit of 1000: 1000, 2000, 3000, ...
		    					: batchStart + leftoverItems - 1;
	    		// for each student, assign them to 'minCourseload' to 'maxCourseload' classes
	    		HashMap<Integer, ArrayList<Integer>> mappings = new HashMap<Integer, ArrayList<Integer>>();
	    		String values = "";
	    		for (int studentIndex = batchStart; studentIndex <= batchEnd; studentIndex++) {
	    			int courseLoad = (int)(Math.random() * (maxCourseload - minCourseload + 1) + minCourseload);
	    			ArrayList<Integer> picks = new ArrayList<Integer>();
	    			for (int classChoice = 0; classChoice < courseLoad; ) {
	    				int choice = (int)(Math.random() * classes.size());
	    				if (!picks.contains(choice))
						{
	    					classChoice++;
	    					totalClassRegistrations++;
	    					picks.add(choice);
	    	    			values += "(?,?),";
						}
	    			}
	    			mappings.put(students.get(studentIndex - 1), picks);
	    		}
	    		values = values.substring(0, values.length() - 1);
	    		
	    		// 
	    		PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO StudentClass (StudentId, ClassId) VALUES " + values);
	    		int valueCount = 1;
	    		for (int studentIndex = batchStart; studentIndex <= batchEnd; studentIndex++) {
	    			for (int classId : mappings.get(students.get(studentIndex - 1))) {
	    				preparedStatement.setInt(valueCount++, students.get(studentIndex - 1));
	    				preparedStatement.setInt(valueCount++, classId);
	    			}
	    		}
	    		preparedStatement.execute();
	    		preparedStatement.close();
	    		System.out.printf("Successfully registered %d students for a total of %d class registrations.%n", batchEnd, totalClassRegistrations);
	    	}
    	}
    	catch (SQLException e) {
    		System.out.printf("%s%n", e);
    	}
    }
    
    // Select persons with a last name ending in 'suffix'
    private ArrayList<Model.Person> getTeachers(String suffix) {
    	ArrayList<Model.Person> al = new ArrayList<Model.Person>();
    	try {
	    	Statement statement = this.connection.createStatement();
	    	ResultSet resultSet = statement.executeQuery("SELECT * FROM person WHERE RIGHT(Name, " + suffix.length() + ") = '" + suffix + "'");
    	    
	    	while (resultSet.next() == true) {
	    		Model model = new Model();
	    		model.person.id = resultSet.getInt(1);
	    		model.person.name = resultSet.getString(2);
	    		al.add(model.person);	    		
	    	}
	    	resultSet.close();
    		statement.close();
    	}
    	catch (SQLException e) {
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
