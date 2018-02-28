import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Collection {

	Dictionary dict;
	String[] files;
	private int filesCounter;
	private double collectionSizeBytes;
	private int wordsInCollection;
	

	Collection(File dir)
	{
		File[] files = dir.listFiles();
		double bytes;
		dict = new Dictionary(files.length);
		
		for (int i = 0; i < files.length; i++)
		  {
		  	if (files[i].isFile())
		  	{
		  		try {
					processFile(files[i], filesCounter);
					dict.addFileName(files[i].getName(), i);
					filesCounter++;
					bytes = files[i].length();
					collectionSizeBytes+=bytes;
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
		  	}
		  }
		
//		dict.createMatrix();
		dict.saveDictionary();
		
	}
	
	
	public double getCollectionSizeInKilobytes()
	{
		return collectionSizeBytes/1024;
	}
	
	public double getCollectionSizeInBytes()
	{
		return collectionSizeBytes;
	}
	
	public int wordsInCollection()
	{
		return wordsInCollection;
	}
	
	public void showDictionary()
	{
		System.out.print(dict);
	}
	
	public int getFilesNumber()
	{
		return filesCounter;
	}
	
	public void processFile(File file, int fileIndex) throws IOException
	{
		
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	    String line = null;
	    while((line = br.readLine())!= null ){
 
	    	String[] tokens = line.split("[\\W]");
	        
	        for (String s : tokens)
	        {
//	        	System.out.println(s+" FILENAME: "+file.getName());

	        	if (!s.equals(""))
	        	{
	        	wordsInCollection++;
	        	dict.addWord(s.toLowerCase(), fileIndex);
	        	
	        	}
	        	
	        }
	        
	        if (dict.timeToSave()){
	        	System.out.println(dict.initialMemory+" current: "+dict.currentMemory);
	        	dict.saveSegment();
	        }
	        	
	        
	    }
	    
	}
	
	public static void main(String[] args) {
		
		
		
	}

}
