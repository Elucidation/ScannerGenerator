package Source;

import java.util.ArrayList;

/** Examples from given doc
 * The string list consists of strings; each string is represented by the tuple : <file-name, line, start-index, end-index> .
 * String-list-1 = {ÅgxyzÅh<Åffile1.txtÅf, 30, 70, 100>, ÅgpqrÅh<Åffile1.txtÅf, 200>}, 
 * String-list-2 = {ÅgxyzÅh <file-2.txtÅf, 90>} 
 * Here String-list-1 shows two strings : ÅgxyzÅh occuring at index locations 30, 70 and 100 in file-1.txt and ÅgpqrÅh in file1.txt at location 200. 
 * @author Sam
 *
 */
public class StringMatch {
	private String str, filename;
	private ArrayList<Integer> locations;

	public StringMatch(String str, String filename, ArrayList<Integer> locations) {
		this.str = str;
		this.filename = filename;
		this.locations = locations;
	}
	@Override
	public String toString() {
		return "\""+str+"\"<'"+filename+"',"+locations+">";
	}
}
