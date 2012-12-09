package Source;

import java.util.ArrayList;

/** Examples from given doc
 * The string list consists of strings; each string is represented by the tuple : <file-name, line, start-index, end-index> .
 * String-list-1 = {gxyzh<ffile1.txtf, 30, 70, 100>, gpqrh<ffile1.txtf, 200>}, 
 * String-list-2 = {gxyzh <file-2.txtf, 90>} 
 * Here String-list-1 shows two strings : gxyzh occuring at index locations 30, 70 and 100 in file-1.txt and gpqrh in file1.txt at location 200. 
 * @author Sam
 *
 */
public class StringMatch {
	private String str;
	private ArrayList<FileLoc> filelocs;

	public ArrayList<FileLoc> getFilelocs() {
		return filelocs;
	}
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
	public int getNumLoc() {
		return this.locations.size();
	}
	@Override
	public String toString() {
		return "<'"+filename+"',"+locations+">";
	}
}