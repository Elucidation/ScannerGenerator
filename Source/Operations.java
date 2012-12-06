package Source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operations {

	public Operations(){
		
	}
	
	public boolean replace(String regex,String ascii_str,String infile,String outfile){
		File in = new File(infile);
		File out = new File(outfile);
		FileInputStream inFile;
		FileOutputStream outFile;
		try {
			inFile = new FileInputStream(in);
			outFile = new FileOutputStream(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		StringBuffer inputFileText = new StringBuffer();
		char c;
		try {
			c = (char) inFile.read();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		while(c != (char)65535){
			inputFileText.append(c);	
			try {
				c = (char) inFile.read();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
				
		Pattern regexPattern = Pattern.compile(regex);
	    Matcher matcher = regexPattern.matcher(inputFileText.toString());
	    String outFileText = matcher.replaceAll(ascii_str);	
		for(int i=0;i<outFileText.length();i++){
			try {
				outFile.write(outFileText.charAt(i));
			} catch (IOException e) {
				e.printStackTrace();
				return false;			
			}
		}		
		try {
			inFile.close();
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;		
		}
		return true;
	}
	
	public String[] find(String regex, String infile){
		File in = new File(infile);
		FileInputStream inFile;
		try {
			inFile = new FileInputStream(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		StringBuffer inputFileText = new StringBuffer();
		char c;
		try {
			c = (char) inFile.read();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}

		while(c != (char)65535){
			inputFileText.append(c);	
			try {
				c = (char) inFile.read();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		Pattern regexPattern = Pattern.compile(regex);
	    Matcher matcher = regexPattern.matcher(inputFileText.toString());
	    ArrayList<String> a = new ArrayList<String>();
	    while(matcher.find()){
	    	a.add(matcher.group());
	    }
		String[] results = new String[a.size()];
		return   a.toArray(results);
	}
	
	public String[] intersec(String[] a, String[] b){

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
	
	public int hash(String[] a){
		return a.length;
	}
	public void print(int a){
		System.out.println(a);
	}

}
