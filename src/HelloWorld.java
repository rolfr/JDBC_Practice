
public class HelloWorld {

	public static void main(String[] args) {
		GenerateData dataGen = new GenerateData();
		String[] bogusNames = dataGen.Names(100000);
		
		JdbcClass jdbc = new JdbcClass();
        try {
			jdbc.insertPersons(bogusNames);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
