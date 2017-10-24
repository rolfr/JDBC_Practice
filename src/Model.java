
public class Model {
	public Model()
	{
		this.person = new Person();
		this.$class = new Class();
		this.studentClass = new StudentClass();
	}
	
	public class Person {
		public int id;
		public String name;
	}
	
	public class Class {
		public int id;
		public String name;
		public int teacherId;
	}
	
	public class StudentClass {
		public int studentId;
		public int classId;
		public String grade;
	}
	
	public Person person;
	public Class $class;
	public StudentClass studentClass;
}
