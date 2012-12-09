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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operations {
	public static void main(String[] args) {
		// Test Replace with
		String fileIn = "C:\\repos\\scannergenerator\\Test_inputs\\regexTest.txt";
		String fileOut = "C:\\repos\\scannergenerator\\Test_inputs\\testOut.txt";
		String regex = "the";
		String replaceWith = "";
		try {
			recursiveReplace(regex,replaceWith,fileIn,fileOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("File "+fileOut+":");
		try {
			System.out.println( fileToString(fileOut) );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Test Find
//		String fileIn = "Test_inputs/regexTest.txt";
//		String regex = "a[a-z]*c";
//		try {
//		System.out.println( find(regex, fileIn) );
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
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
	
	public static boolean recursiveReplace(String regex,String ascii_str,String infile,String outfile) throws IOException{
		String original = fileToString(infile);
		String replaced = original;
		int i;
		for(i=0;i<10;i++){
			String oldReplaced = replaced;
			replaced = replaceAll(replaced,regex,ascii_str);
			if(oldReplaced.equals(replaced)){
				stringToFile(outfile,replaced);
				return true;
			}
		}
		if(i==10){
			return false;
		}
		else{
			stringToFile(outfile,replaced);
			return true;
		}
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
	    HashMap<String, ArrayList<Integer> > matches = new HashMap<String, ArrayList<Integer> >();
	    System.out.println();
	    while (matcher.find()) {
	    	String s = matcher.group();
	    	int startIndex = matcher.start();
	    	if (matches.containsKey(s))
	    		matches.get(s).add(startIndex);
	    	else {
	    		ArrayList<Integer> v = new ArrayList<Integer>();
	    		v.add(startIndex);
	    		matches.put(s, v );
	    	}
	    }
	    System.out.println(matches);
	    ArrayList<StringMatch> allMatches = new ArrayList<StringMatch>();
	    for (Entry<String, ArrayList<Integer>> entry : matches.entrySet()) {
	    	allMatches.add( new StringMatch(entry.getKey(),infile, entry.getValue()) );
	    }
		return allMatches;
	}
	
	public static String[] intersec(String[] a, String[] b){

		HashSet<String> hs = new HashSet<String>();
		for(int i=0;i<a.length;i++){
			hs.add(a[i]);
		}
		HashSet<String> rs = new HashSet<String>();
		for(int i=0;i<b.length;i++){
			if(hs.contains(b[i])){
				rs.add(b[i]);
			}
		}
		
		String[] results = new String[rs.size()];
		return rs.toArray(results);
	}
	
	public static int hash(String[] a){
		return a.length;
	}
	public static void print(int a){
		System.out.println(a);
	}
	
	/** 
	 * Returns null if string list is empty.
	 * @param stringList
	 * @return
	 * @author Sam
	 */
	public static String maxfreqstring(ArrayList<String> stringList){
		String maxString = null;
		int maxCount = 0;
		HashMap<String,Integer> counts = new HashMap<String,Integer>();
		for (String s : stringList) {
			if (counts.containsKey(s)) {
				int c = counts.get(s)+1;
				counts.put(s, c);
				if (c > maxCount) {
					maxCount = c;
					maxString = s;
				}
			}
			else
				counts.put(s,1);
		}
				
		return maxString;
	}
	
	//This needs to be tested
	public static String[] union(String[] a, String[] b){

		HashSet<String> hs = new HashSet<String>();
		for(int i=0;i<a.length;i++){
			hs.add(a[i]);
		}
		for(int i=0;i<b.length;i++){
				hs.add(b[i]);
			
		}
		
		String[] results = new String[hs.size()];
		return hs.toArray(results);
	}
	//This needs to be tested
	public static String[] diff(String[] a, String[] b){

		HashSet<String> hs = new HashSet<String>();
		for(int i=0;i<a.length;i++){
			hs.add(a[i]);
		}
		
		for(int i=0;i<b.length;i++){
				if(hs.contains(b[i])){
					hs.remove(b[i]);
				}
			
		}
		
		String[] results = new String[hs.size()];
		return hs.toArray(results);
		
	}

}
