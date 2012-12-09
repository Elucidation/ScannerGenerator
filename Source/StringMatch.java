package Source;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** Examples from given doc
 * The string list consists of strings; each string is represented by the tuple : <file-name, line, start-index, end-index> .
 * String-list-1 = {gxyzh<file1.txt, 30, 70, 100>, gpqrh<file1.txt, 200>}, 
 * String-list-2 = {gxyzh<file-2.txt, 90>} 
 * Here String-list-1 shows two strings : gxyzh occuring at index locations 30, 70 and 100 in file-1.txt and gpqrh in file1.txt at location 200. 
 * @author Sam
 *
 */
public class StringMatch {
	private String str;
	private Set<FileLoc> filelocs;

	public Set<FileLoc> getFilelocs() {
		return filelocs;
	}
	public StringMatch(String str) {
		this.str = str;
		this.filelocs = new HashSet<FileLoc>();		
	}
	public StringMatch(String str, String filename, Set<Integer> locations) {
		this(str);
		this.filelocs.add(new FileLoc(filename,locations));
	}
	
	/**
	 * Joins two StringMatches where string has to match
	 * @param other
	 * @return
	 */
	public StringMatch union(StringMatch other) {
		if (!str.equals(other.str))
			return null;
		StringMatch joined = new StringMatch(str);
		// Add all of my filelocs
		joined.filelocs.addAll(filelocs);		
		// merge all common filelocs in mine
		for (FileLoc fl : joined.filelocs) {
			if (other.filelocs.contains(fl)) {
				fl.mergeIntoSelf(other.getFileLoc(fl));
			}
		}
		// Add all of other filelocs w/ different file-names
		joined.filelocs.addAll(other.filelocs);
		
		return joined;
	}	
	
	/** Get's fileloc in StringMatch which has same name as fileloc given
	 * 
	 * @param fl
	 * @return
	 */
	public FileLoc getFileLoc(FileLoc given) {
		for (FileLoc fl : filelocs) {
			if (given.equals(fl))
				return fl;
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == this.getClass()) return equals((StringMatch)obj);
		return false;
	}
	/**
	 * Comparison is solely by filename, even if locations are different.
	 * @param other
	 * @return
	 */
	public boolean equals(StringMatch other) {
		return str.equals(other.str);
	}
	
	@Override
	public int hashCode() {
		return str.hashCode();
	}
	
	@Override
	public String toString() {
		return "\""+str+"\""+filelocs;
	}
}
class FileLoc {
	private String filename;
	private Set<Integer> locations;
	public FileLoc(String filename, Set<Integer> locations) {
		this.filename = filename;
		this.locations = locations;
	}
	public int getNumLoc() {
		return this.locations.size();
	}
	
	/**
	 * Returns the union between this file-loc and another
	 * @param other
	 * @return
	 */
	public FileLoc union(FileLoc other) {
		if (!this.filename.equals(other.filename))
			return null;
		Set<Integer> joined = new HashSet<Integer>(this.locations);
		joined.addAll(other.locations);
		return new FileLoc(this.filename, joined );
	}
	
	public void mergeIntoSelf(FileLoc other) {
		if (!this.filename.equals(other.filename))
			System.out.println("WARNING: Merging two filelocs where filenames don't match! : "+filename + " vs "+other.filename);
		locations.addAll(other.locations);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == this.getClass()) return equals((FileLoc)obj);
		return false;
	}
	/**
	 * Comparison is solely by filename, even if locations are different.
	 * @param other
	 * @return
	 */
	public boolean equals(FileLoc other) {
		return this.filename.equals(other.filename);
	}
	
	@Override
	public int hashCode() {
		return this.filename.hashCode();
	}
	
	
	@Override
	public String toString() {
		return "<'"+filename+"',"+locations+">";
	}
}