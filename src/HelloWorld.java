
public class HelloWorld {

	public static void main(String[] args) {
		GenerateData dataGen = new GenerateData();
		String[] personNames = dataGen.Names(10000);
		String[] classNames = dataGen.ClassNames(250);
		
		JdbcClass jdbc = new JdbcClass();
        try {
			jdbc.insertPersons(personNames);
			jdbc.insertClasses(classNames, "on");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
