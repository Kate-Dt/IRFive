import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class Test {

	
	public static void main(String[] args) 
	{
		File path = new File("D:/BigEnglishCTest");
	
		String file1 = "Dickens.txt";
		String file2 = "Sonnet.txt";
		File fileOne = new File(file1);
		
		long startTime = System.currentTimeMillis();

		Collection c = new Collection(path);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
		
		System.out.println("\nFiles in collection: "+c.getFilesNumber());
		System.out.println("Words in collection: "+c.wordsInCollection());
		System.out.println("Words in dictionary: "+c.dict.size());
		System.out.println("Size of collection in kilobytes: "+c.getCollectionSizeInKilobytes());
		System.out.println("Size of dictionary in kilobytes: "+c.dict.getDictSizeInKilobytes());
		
		
		System.out.println("\n***\nElapsed time: "+elapsedTime);

		
	}
}
