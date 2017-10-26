
public class JdbcSqlTest {

	public static void main(String[] args) {
		GenerateData dataGen = new GenerateData();
		String[] personNames = dataGen.Names(1000);
		String[] classNames = dataGen.ClassNames(30);
		
		MySqlStudentClassTeacher jdbc = new MySqlStudentClassTeacher();
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
