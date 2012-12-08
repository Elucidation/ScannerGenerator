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
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operations {
	public static void main(String[] args) {
		// Test Replace with
//		String fileIn = "Test_inputs/regexTest.txt";
//		String fileOut = "Test_inputs/testOut.txt";
//		String regex = " a[A-Za-z]*";
//		String replaceWith = " BLOOP";
//		try {
//			replace(regex,replaceWith,fileIn,fileOut);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println("File "+fileOut+":");
//		try {
//			System.out.println( fileToString(fileOut) );
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		// Test Find
		String fileIn = "Test_inputs/regexTest.txt";
		String regex = " a[A-Za-z]*";
		try {
		System.out.println( find(regex, fileIn) );
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
	 * Finds all occurrences of regex matches in infile, returns as array list of strings
	 * @param regex
	 * @param infile
	 * @return
	 * @author Sam
	 * @throws IOException 
	 */
	public static ArrayList<String> find(String regex, String infile) throws IOException{
		
		Pattern regexPattern = Pattern.compile(regex);
	    Matcher matcher = regexPattern.matcher( fileToString(infile) );
	    ArrayList<String> matches = new ArrayList<String>();
	    while(matcher.find()){
	    	matches.add(matcher.group());
	    }
		return matches;
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
