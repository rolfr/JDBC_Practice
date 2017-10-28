import java.util.*;

public class JdbcSqlTest {

	public static void main(String[] args) {
		GenerateData dataGen = new GenerateData();
		String[] personNames = dataGen.Names(100000);
		String[] classNames = dataGen.ClassNames(1000);
		
		System.out.print("Please select 1 to use MySQL, 2 to use Azure SQL: ");
		Scanner scan = new Scanner(System.in);
		int choice = scan.nextInt();
		
		DatabaseType dbType;
		if (choice <= 1)
			dbType = DatabaseType.MySQL;
		else
			dbType = DatabaseType.AzureSQL;			
		
		IStudentClassTeacher jdbc = new DbAccessClassFactory().getDbInterface(dbType);
        try {
			jdbc.insertPersons(personNames);
			jdbc.insertClasses(classNames, "on");
			jdbc.registerStudents(2, 6);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
