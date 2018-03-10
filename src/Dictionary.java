import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class Dictionary{
	public static int DICT_ID = 0;
	public static int S_ID = 1;
	private final int MAX_SEGMENT_WORDS = 85000;
	private final int MAX_DICT_WORDS = 85000;
	private int filesNumber;
	private int wordsInHashMap = 0;
	private int wordsInDictionary = 0;
	private int totalWordsInDictionaryBlocks = 0;
	private boolean segmentsCreated;
	private double dictSizeInBytes = 0;
	private String[] fileNames;
	private ArrayList<String> segments;
	private HashMap<String, HashSet<Integer>> words;
	private TreeMap<String, TreeSet<Integer>> merged;

	public Dictionary(int filesNumber) {
		this.filesNumber = filesNumber;
		words = new HashMap<String, HashSet<Integer>>();
		segments = new ArrayList<String>();
		fileNames = new String[filesNumber];
		DICT_ID++;
	}

	public void addWord(String word, int fileIndex)	{
		if (words.containsKey(word))		{
			words.get(word).add(fileIndex);
		}
		else {
			words.put(word, new HashSet<Integer>());
			words.get(word).add(fileIndex);
			wordsInHashMap++;
			if (wordsInHashMap == MAX_SEGMENT_WORDS) {
				saveSegment();
				wordsInHashMap = 0;
			}
		}
	}

	public void saveSegment() {
		try {
			File file = new File ("s"+(S_ID++)+".txt");
			segments.add(file.getName());
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			List<String> lines = new ArrayList<String>();
			List<String> wordsSorted = new ArrayList<String>(words.keySet());
			Collections.sort(wordsSorted);
			List<Integer> id;
			Iterator<String> w = wordsSorted.iterator();
			Iterator<Integer> ids;
			String word;
			while (w.hasNext()) {
				word = w.next();
				String line = word+" ";
				//	System.out.print("s: "+word+" \n");
				id = new ArrayList<Integer>(words.get(word));
				Collections.sort(id);
				ids = id.iterator();
				while (ids.hasNext()){
					//		        		System.out.print(ids.next()+" ");
					line += ids.next()+" ";
				}
				//		        	System.out.println(line);
				lines.add(line);
			}
			Iterator<String> listIterator = lines.iterator();
			while (listIterator.hasNext()) {
				writer.write(listIterator.next());
				writer.newLine();
			}
			//		        writer.flush();
			writer.close();
			words.clear();
			lines.clear();
			wordsSorted.clear();
			segmentsCreated = true;
			words = new HashMap<String, HashSet<Integer>>();
		} catch(Exception e) {
			System.err.println("Cannot create file.");
		}
	}

	public void saveDictionary() {
		if (!segmentsCreated){
			simpleSave();
		}
		else {
			if (!words.isEmpty()) {
				saveSegment();
			}
			try	{
				BufferedReader[] readers = new BufferedReader[segments.size()];
				Iterator<String> segm = segments.iterator();
				int segmentsCounter = readers.length;
				for (int i = 0; i < segments.size(); i++) {
					readers[i] = new BufferedReader(new FileReader("D:/HeliosWorkspace2/IRFive/"+segm.next()));
				}
				merged = new TreeMap<String, TreeSet<Integer>>();
				while (segmentsCounter > 0){
					for (int i = 0; i < readers.length; i++){
						String line = readers[i].readLine();
						if (line == null){
							segmentsCounter--;
						} else{
							String[] tokens = line.split(" ");
							String word = tokens[0];
							if (merged.containsKey(word)) {
								TreeSet ids = merged.get(word);
								for (int j = 1; j < tokens.length; j++)
									ids.add(tokens[j]);
							}
							else {
								merged.put(word, new TreeSet<Integer>());
								TreeSet ids = merged.get(word);
								for (int j = 1; j < tokens.length; j++) {
									ids.add(tokens[j]);
								}
								wordsInDictionary++;
								if (wordsInDictionary == MAX_DICT_WORDS)	{
									savePartialDictionary(merged);
									merged = new TreeMap<String, TreeSet<Integer>>();
									wordsInDictionary = 0;
									totalWordsInDictionaryBlocks+= MAX_DICT_WORDS;
								}
								//System.out.println(wordsInDictionary);
							}
						}
					}
				}
				if (!merged.isEmpty()){
					savePartialDictionary(merged);
					totalWordsInDictionaryBlocks += merged.size();
				}
				for (int i = 0; i < readers.length; i++) {
					readers[i].close();
				}
				System.out.println("---------> all blocks processed");
			}catch(Exception e)	{
				System.err.println("Cannot create file.");
				e.printStackTrace();
			}
		}
	}

	private void savePartialDictionary(TreeMap<String, TreeSet<Integer>> merged){
		File file = new File("Dictionary"+DICT_ID+".txt");
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
			Iterator<String> it = merged.keySet().iterator();
			String writeLine;
			while (it.hasNext()) {
				String word = it.next();
				writeLine = word +" : ";
				Iterator<Integer> ids = merged.get(word).iterator();
				while (ids.hasNext()) {
					writeLine += ids.next()+" ";
				}
				writer.write(writeLine);
				writer.newLine();
			}
			writer.flush();
			writer.close();
			dictSizeInBytes += file.length();
			System.out.println("Dictionary "+DICT_ID+" is saved to the folder.");
			DICT_ID++;
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * save dictionary if there are no segments
	 */
	private void simpleSave() {
		try	{
			File f = new File("Dictionary"+DICT_ID+".txt");
			f.delete();
			File file = new File("Dictionary"+DICT_ID+".txt");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter
					(new FileOutputStream(file), "windows-1251"));
			List<String> toDict = new ArrayList<String>();
			words.remove("");
			List<String> w = new ArrayList<String>(words.keySet());
			Collections.sort(w);
			Iterator<String> it = w.iterator();
			String line, word;
			while (it.hasNext()) {
				word = it.next();
				line = word+" : ";
				List<Integer> ids = new ArrayList<Integer>(words.get(word));
				Collections.sort(ids);
				Iterator<Integer> itId = ids.iterator();
				while (itId.hasNext()) {
					line += itId.next()+" ";
				}
				// System.out.println(line);
				toDict.add(line);
			}
			Iterator<String> lines = toDict.iterator();
			while(lines.hasNext()) {
				writer.write(lines.next());
				writer.newLine();
			}
			writer.flush();
			writer.close();
			dictSizeInBytes += file.length();
			System.out.println("------>Dictionary is saved to the folder.");
		} catch (Exception e){
			System.out.println("SimpleSave: Cannot create file.");
			e.printStackTrace();
		}
	}

	public String wordsMapEntry(String key) {
		String res = "";
		if (words.containsKey(key))	{
			res+=key+": ";
		}
		Iterator it = words.get(key).iterator();
		while (it.hasNext()) {
			res+=it.next()+", ";
		}
		return res;
	}

	private int hash(String word) {
		int hash = 0;
		int size = words.size();
		for (int i = 0; i < word.length(); i++)	{
			hash = (31 * hash + word.charAt(i)) % size;
		}
		return hash;
	}

	public String filesFound(int number) {
		String res  = "";
		String s = Integer.toBinaryString(number);
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i)=='1') {
				res+="�"+fileNames[filesNumber-1-i]+"\n";
			}
		}
		return res;
	}

	void addFileName(String fileName, int fileIndex) {
		fileNames[fileIndex] = fileName;
	}

	private String toString(byte[] ar){
		String res = "";
		for (int i = 0; i< ar.length; i++) {
			res += ar[i];
		}
		return res;
	}

	public int size() {
		return totalWordsInDictionaryBlocks;
	}

	public double getDictSizeInBytes() 	{
		return dictSizeInBytes;
	}

	public double getDictSizeInKilobytes()	{
		return (dictSizeInBytes/1024);
	}

	public static void main(String[] args)	{
		Dictionary d = new Dictionary(11);
		d.addWord("something", 10);
		d.addWord("this", 2);
		d.addWord("get", 5);
		d.addWord("this", 3);
		d.addWord("this", 4);
		d.addWord("this", 6);
		d.addWord("this", 5);
		d.addWord("this", 8);
		d.addWord("this", 7);
		d.addWord("this", 10);
		d.addWord("this", 9);
		d.addWord("get", 7);
		d.addWord("get", 8);
		d.addWord("get", 0);

		Iterator it = d.words.keySet().iterator();

		while (it.hasNext())
		{
			System.out.println(it.next());
		}
	}
}
