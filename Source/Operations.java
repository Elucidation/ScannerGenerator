package Source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operations {
	private static final int MAX_ITER = 10;
	
	public static void main(String[] args) {
		// Test Replace with
//		String fileIn = "C:\\repos\\scannergenerator\\Test_inputs\\regexTest.txt";
//		String fileOut = "C:\\repos\\scannergenerator\\Test_inputs\\testOut.txt";
//		String regex = "the";
//		String replaceWith = "";
//		try {
//			recursiveReplace(regex,replaceWith,fileIn,fileOut);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		System.out.println("File "+fileOut+":");
//		try {
//			System.out.println( fileToString(fileOut) );
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		// Test Find
		String fileIn = "Test_inputs/regexTest.txt";
		String fileIn2 = "Test_inputs/regexTest_2.txt";
		String regex = "th[a-z]{4,5}";
		String regex2 = "th[a-z]{5}";
		try {
			ArrayList<StringMatch> a = find(regex, fileIn);
			ArrayList<StringMatch> b = find(regex2, fileIn2);
			System.out.println("A: "+ a );
			System.out.println("B: "+ b );
			
			System.out.println("UNION : "+ union(a, b) );
			System.out.println("INTERS: "+ inters(a, b) );
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns file contents as one string
	 * @param filename
	 * @return
	 * @throws IOException 
	 * @author Sam
	 */
	public static String fileToString(String filename) throws IOException {
		BufferedReader f = new BufferedReader(new FileReader(filename));
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = f.readLine()) != null)
			sb.append(line+"\n");
		f.close();
		return sb.toString();
	}
	/**
	 * Writes text to filename, creating if file doesn't exist, overwriting if it does
	 * @param filename
	 * @param text
	 * @return true if successful, throw if not
	 * @throws IOException
	 * @author Sam 
	 */
	public static boolean stringToFile(String filename, String text) throws IOException {
		BufferedWriter f = new BufferedWriter(new FileWriter(filename));
		f.write(text);
		f.close();
		return true;
	}
	
	/**
	 * Replaces all occurence of regex in string with toReplaceWith
	 * @param text string of text to do replacing with
	 * @param regex string of regex
	 * @param toReplaceWith string to replace with
	 * @return new string with parts replaced
	 * @author Sam
	 */
	private static String replaceAll(String text, String regex, String toReplaceWith) {
		Pattern regexPattern = Pattern.compile(regex);
	    Matcher matcher = regexPattern.matcher(text.toString());
	    return matcher.replaceAll(toReplaceWith);
	}
	
	/**
	 * Replace all occurence of regex w/ ascii_str in infile, writing output to outfile
	 * @param regex
	 * @param ascii_str
	 * @param infile
	 * @param outfile
	 * @return true if successful, throw otherwise
	 * @throws IOException
	 * @author Sam
	 */
	public static boolean replace(String regex,String ascii_str,String infile,String outfile) throws IOException{	
		String original = fileToString(infile); 
		String replaced = replaceAll(original , regex, ascii_str);
		stringToFile(outfile, replaced);
		return true;
	}
	
	/**
	 * Repeated replace on string until no matches found or MAX_ITER(probably 10) is hit
	 * @param regex string of regex
	 * @param ascii_str string to replace with
	 * @param infile string filename
	 * @param outfile string filename
	 * @return true if finished before hitting max-iter, false otherwise, still creates new file regardless
	 * @throws IOException
	 */
	public static boolean recursiveReplace(String regex,String ascii_str,String infile,String outfile) throws IOException{
		String original = fileToString(infile);
		String replaced = original;
		int i;
		for(i=0;i<MAX_ITER;i++){
			String oldReplaced = replaced;
			replaced = replaceAll(replaced,regex,ascii_str);
			if(oldReplaced.equals(replaced))
				break;
		}
		stringToFile(outfile,replaced);
		return i < 10;
	}

	/**
	 * Finds all occurrences of regex matches in infile, returns as ArrayList<StringMatch>
	 * @param regex
	 * @param infile
	 * @return 
	 * @author Sam
	 * @throws IOException 
	 */
	public static ArrayList<StringMatch> find(String regex, String infile) throws IOException{
		System.out.println("OPERATION FIND "+regex+" IN "+ infile);
		Pattern regexPattern = Pattern.compile(regex);
	    Matcher matcher = regexPattern.matcher( fileToString(infile) );
	    HashMap<String, Set<Integer> > matches = new HashMap<String, Set<Integer> >();
	    while (matcher.find()) {
	    	String s = matcher.group();
	    	int startIndex = matcher.start();
	    	if (matches.containsKey(s))
	    		matches.get(s).add(startIndex);
	    	else {
	    		Set<Integer> v = new HashSet<Integer>();
	    		v.add(startIndex);
	    		matches.put(s, v );
	    	}
	    }
	    System.out.println(matches);
	    ArrayList<StringMatch> allMatches = new ArrayList<StringMatch>();
	    for (Entry<String, Set<Integer>> entry : matches.entrySet()) {
	    	allMatches.add( new StringMatch(entry.getKey(),infile, entry.getValue()) );
	    }
		return allMatches;
	}
	
	/** Returns the StringMatch from given ArrayList<StringMatch> that has the highest number of locations in all files.
	 * 
	 * @param matches
	 * @return
	 */
	public static StringMatch maxfreqstring(ArrayList<StringMatch> matches){

		int bestLength = 0;
		StringMatch maxFreqString = null;
		
		for(StringMatch match : matches){
			int count = 0;
			for(FileLoc loc: match.getFilelocs()){
				count += loc.getNumLoc();
			}
			if(count > bestLength) {
				bestLength = count;
				maxFreqString = match;
			}
		}
		
		return maxFreqString;
		
	}

	/**
	 * Does intersection of two given ArrayList<StringMatch>'s
	 * For Example:
	 * String-list-1 = {"xyz"<'file1.txt', 30, 70, 100><file-2.txt', 20,40>, "pqr"<'file1.txt', 200>}
	 * String-list-2 = {"xyz" <file-2.txt', 90, 100>} 
	 * 
	 * Here String-list-1 shows two strings : "xyz" occuring at index locations 30, 70 and 100 in file-1.txt + file-2.txt in 20 and 40, and "pqr" in file1.txt at location 200. 
	 * Similarly, String-list-2 shows a string xyz. The string intersection should result in :
	 * String-list-1 inters String-list-2 = {"xyz" <'file1.txt', 30, 70, 100><'file-2.txt', 20, 40, 90, 100>}
	 * @param a
	 * @param b
	 * @return
	 */
	public static ArrayList<StringMatch> inters(ArrayList<StringMatch> a, ArrayList<StringMatch> b) {
		ArrayList<StringMatch> c = new ArrayList<StringMatch>();
		for (StringMatch sm : a) {
			if (b.contains(sm)) {
				StringMatch x = sm.union(b.get(b.indexOf(sm)));
				c.add(x);
			}
		}
		return c;
	}
	
	/**
	 * Returns union of two given ArrayList<StringMatch>'s
	 * @param a
	 * @param b
	 * @return
	 */
	public static ArrayList<StringMatch> union(ArrayList<StringMatch> a, ArrayList<StringMatch> b) {
		ArrayList<StringMatch> c = new ArrayList<StringMatch>();
		for (StringMatch sm : a) {
			if (!b.contains(sm)) {
				c.add(sm);
			}
		}
		for (StringMatch sm : a) {
			if (b.contains(sm)) {
				StringMatch x = sm.union(b.get(b.indexOf(sm)));
				c.add(x);
			}
		}
		// Add all remaining in b not in a 
		for (StringMatch sm : b) {
			if (!a.contains(sm)) {
				c.add(sm);
			}
		}
		return c;
	}
}
