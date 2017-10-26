import java.sql.Connection;

public interface IJdbc {
	public Connection getConnection();
	public void setConnection();
	public void initTables();	
}
