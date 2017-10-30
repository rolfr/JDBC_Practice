import java.util.*;

public class TraverseTreeWithoutRecursion {

	public class TreeNode {
		public int studentId;
		public int[] classes;
		public TreeNode left;
		public TreeNode right;
		
		public TreeNode() {}
		public TreeNode(int studentId, int[] classes) {
			this.studentId = studentId;
			this.classes = classes;
		}
	}
	
	TreeNode root = null;
	
	public void traverseTreeInOrder() {
		Stack<TreeNode> stack = new Stack<TreeNode>();
		TreeNode node = root;
		boolean isDone = false;
		
		do {
			if (node != null) {
				stack.push(node);
				node = node.left;
			}
			else {
				if (!stack.isEmpty())
				{
					node = stack.pop();
					System.out.print(node.studentId + " ");
					node = node.right;
				}
				else
					isDone = true;
			}
		} while (!isDone);
	}
	
	public void insertNode(int studentId, int[] classes) {
		TreeNode node = new TreeNode(studentId, classes);
		this.insertNode(this.root, node);		
	}
	
	public void insertNode(TreeNode root, TreeNode node)
	{
		if (root == null)
		{
			this.root = node;
			return;
		}
		
		if (node.studentId < root.studentId)
		{
			if (root.left == null)
			{
				root.left = node;
				return;
			}
			insertNode(root.left, node);
			return;
		}
		
		if (node.studentId > root.studentId)
		{
			if (root.right == null)
			{
				root.right = node;
				return;
			}
			insertNode(root.right, node);
			return;
		}
		
		throw new IllegalArgumentException("Duplicate StudentId value is not allowed (" + node.studentId + ")");
	}
	
	public void generateRandomTree(int studentCount)
	{
		ArrayList<Integer> studentList = new ArrayList<Integer>();
		for (int i = 0; i < studentCount; i++) {
			studentList.add(i + 1);
		}

		int median = studentCount / 2;
		this.root = new TreeNode(median, new int[] { 1, 2, 3 });
		studentList.remove(median - 1);  // item 0 is studentId 1, etc.
		int size = studentList.size();
		
		while (size > 0) {
			int choice = (int)(Math.random() * size);
			this.insertNode(studentList.get(choice), new int[] { 1, 2, 3 } );
			studentList.remove(choice);
			size--;
		}
	}
	
	public void generateStaticTestTree() {
		int[] studentList = new int[] { 10, 6, 16, 3, 8, 15, 18, 1, 4, 7, 9, 12, 17, 20, 2, 5, 11, 13, 19, 14 };
		for (int student : studentList) {
			this.insertNode(student, new int[] { 1, 2, 3 });
		}
	}
}
