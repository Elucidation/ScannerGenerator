package Source;

import java.util.ArrayList;

/** Examples from given doc
 * The string list consists of strings; each string is represented by the tuple : <file-name, line, start-index, end-index> .
 * String-list-1 = {�gxyz�h<�ffile1.txt�f, 30, 70, 100>, �gpqr�h<�ffile1.txt�f, 200>}, 
 * String-list-2 = {�gxyz�h <file-2.txt�f, 90>} 
 * Here String-list-1 shows two strings : �gxyz�h occuring at index locations 30, 70 and 100 in file-1.txt and �gpqr�h in file1.txt at location 200. 
 * @author Sam
 *
 */
public class StringMatch {
	private String str;
	private ArrayList<FileLoc> filelocs;

	public StringMatch(String str) {
		this.str = str;
		this.filelocs = new ArrayList<FileLoc>();		
	}
	public StringMatch(String str, String filename, ArrayList<Integer> locations) {
		this(str);
		this.filelocs.add(new FileLoc(filename,locations));
	}
	@Override
	public String toString() {
		return "\""+str+"\""+filelocs;
	}
}
class FileLoc {
	private String filename;
	private ArrayList<Integer> locations;
	public FileLoc(String filename, ArrayList<Integer> locations) {
		this.filename = filename;
		this.locations = locations;
	}
	@Override
	public String toString() {
		return "<'"+filename+"',"+locations+">";
	}
}