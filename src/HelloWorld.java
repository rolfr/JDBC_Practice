
public class HelloWorld {

	public static void main(String[] args) {
		GenerateData dataGen = new GenerateData();
		String[] bogusNames = dataGen.Names(10000);
		
		for(String n : bogusNames)
		{
			System.out.println(n);
		}
		
		/*JdbcClass jdbc = new JdbcClass();
        try {
			jdbc.readDataBase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
