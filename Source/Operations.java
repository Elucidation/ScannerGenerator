package Source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

}
