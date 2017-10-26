public class DbAccessClassFactory {
	public IStudentClassTeacher getDbInterface(DatabaseType databaseType)
	{
		switch (databaseType)
		{
		case MySQL:
			return new MySqlStudentClassTeacher();
		case AzureSQL:
			return new AzureSqlStudentClassTeacher();
		}
		
		return new MySqlStudentClassTeacher();
	}
}
