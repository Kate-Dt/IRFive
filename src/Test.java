import java.io.File;

public class Test {
	public static void main(String[] args)
	{
		File path = new File("D:/BigEnglishCTest2");

		String file1 = "Dickens.txt";
		String file2 = "Sonnet.txt";
		File fileOne = new File(file1);

		long startTime = System.currentTimeMillis();

		Collection c = new Collection(path);

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;

		System.out.println("\nFiles in collection: "+c.getFilesNumber());
		System.out.println("Words in collection: "+c.wordsInCollection());
		System.out.println("Words in dictionary (all blocks): "+c.dict.size());
		System.out.println("Size of collection in kilobytes: "+c.getCollectionSizeInKilobytes());
		System.out.println("Size of dictionary in kilobytes: "+c.dict.getDictSizeInKilobytes());
		System.out.println("\n***\nElapsed time: "+elapsedTime);

		//		System.out.println("smth");
	}
}
