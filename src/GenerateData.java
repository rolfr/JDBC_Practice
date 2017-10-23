import java.io.*;


public class GenerateData {
	private static final char[] vowels = new char[] { 'a','e','i','o','u' };
	private static final char[] consonants = new char[20];
	
	static {
		int count = 0;
		for (char c = 'a'; c <= 'z'; c++)
		{
			boolean isVowel = false;
			for (int i = 0; i < vowels.length; i++)
			{
				if (c == vowels[i])
				{
					isVowel = true;
					break;
				}
			}
			if (!isVowel && c != 'q')
			{
				consonants[count] = c;
				count++;
			}
		}
	}
	
	public String[] Names(int count) {
		String[] names = new String[count];
		for (int i = 0; i < count; i++)
		{
			int firstNameLength = (int)(Math.random() * 4) + 4;
			int lastNameLength = (int)(Math.random() * 8) + 4;
			
			String firstName = this.Name(firstNameLength);
			String lastName = this.Name(lastNameLength);
			names[i] = firstName + " " + lastName;
		}
		return names;
	}
	
	private String Name(int length)	{
		int consonantCount = 0, vowelCount = 0;
		StringWriter sw = new StringWriter();
		boolean isVowel;
		
		for(int i = 0; i < length; i++)
		{
			if (i == 0)
			{
				isVowel = Math.random() > 0.8;
				if (isVowel)
					vowelCount++;
				else
					consonantCount++;				
				sw.append(EnglishLetter(true, isVowel));
			}
			else if (consonantCount > 1)
			{
				consonantCount = 0;
				sw.append(EnglishLetter(false, true));
			}
			else if (vowelCount > 1)
			{
				vowelCount = 0;
				sw.append(EnglishLetter(false, false));
			}
			else
			{
				isVowel = Math.random() > 0.5;
				if (isVowel)
					vowelCount++;
				else
					consonantCount++;
				sw.append(EnglishLetter(false, isVowel));
			}				
		}
		return sw.toString();
	}
	
	static private char lastLetter = '\0';
	private char EnglishLetter(boolean isUpperCase, boolean isVowel) {
		boolean isUnique = false;
		char candidate = '\0';
		while (!isUnique)
		{
			int letter = (int)(Math.random() * (isVowel ? vowels.length : consonants.length));
			candidate = (char)((isVowel ? vowels[letter] : consonants[letter]) - (isUpperCase ? 'a' - 'A' : 0));
			isUnique = (candidate != lastLetter);
		}
		lastLetter = candidate;
		return candidate;
	}
}
