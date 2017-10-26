public interface IStudentClassTeacher extends IJdbc {
	public abstract void insertPerson(String name);
	public abstract void insertPersons(String[] names);
	public abstract void insertClasses(String[] names, String teacherLastNamesEndWith);
	public abstract void registerStudents(int minCourseload, int maxCourseload);
}
