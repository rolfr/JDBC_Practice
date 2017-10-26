import java.sql.*;
import java.util.Scanner;
import com.microsoft.sqlserver.jdbc.*;

public final class AzureSqlStudentClassTeacher extends StudentClassTeacher {
	public AzureSqlStudentClassTeacher()
	{
		this.addSqlStatementMapping("AUTO_INCREMENT", "IDENTITY(1,1)");
		this.setConnection();
		this.initTables();
		this.queryParameterLimit = 2000;
	}
	
	public void setConnection()
	{
		// This will load the MySQL driver, each DB has its own driver
		try
		{
			System.out.print("Please enter the Azure SQL database username:");    		
			Scanner scan = new Scanner(System.in);
			String username = scan.nextLine();
			System.out.printf("Please enter the password for '%s':", username);
			String password = scan.nextLine();
			scan.close();
			
			// Connect to Microsoft Azure SQL database
    		this.connectionString = "jdbc:sqlserver://programprinterdb.database.windows.net:1433;database=ProgramPrinterMusicData;user=" + username"@programprinterdb;password=" + password + ";encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"; //, username, password);
    		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    		this.connection = DriverManager.getConnection(connectionString);
		}
		catch (Exception e)
		{
			System.out.printf("%s%n", e);
		}
	}
}
