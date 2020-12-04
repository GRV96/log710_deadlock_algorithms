import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class stores a file's content as a collection of text lines. These
 * lines can be obtained through an accessor by index or an iterator. The
 * lines' order in their file is preserved in this class.
 * @author Guyllaume Rousseau
 */
public class FileContent implements Iterable<String> {

	private static final String EMPTY_STRING = "";

	private List<String> lineList = null;

	/**
	 * This constructor creates an instance containing no text lines.
	 */
	public FileContent() {
		lineList = new ArrayList<String>();
	}

	/**
	 * This constructor stores the given file's lines in the same order as in
	 * their file. Every line is trimmed with method String.trim before it is
	 * stored.
	 * @param file - a file containing text
	 * @throws IOException if an I/O error occurs reading from the file or a
	 * malformed or unmappable byte sequence is read
	 */
	public FileContent(File file) throws IOException {
		lineList = Files.readAllLines(file.toPath());

		int lineCount = getLineCount();
		for(int i=0; i<lineCount; i++) {
			String line = lineList.get(i);
			String trimmedLine = line.trim();
			if(!line.equals(trimmedLine)) {
				lineList.set(i, trimmedLine);
			}
		}
	}

	/**
	 * Adds a line of text after all the currently contained lines.
	 * @param line - a line of text. If it is null, an empty line is added.
	 */
	public void addLine(String line) {
		if(line == null) {
			lineList.add(EMPTY_STRING);
		}
		else {
			lineList.add(line.trim());
		}
	}

	/**
	 * Adds a line of text n times after all the currently contained lines.
	 * @param line - a line of text. If it is null, empty lines are added.
	 * @param n - the number of times line must be added
	 */
	public void addLine(String line, int n) {
		for(int i=0; i<n; i++) {
			addLine(line);
		}
	}

	/**
	 * Returns the text line at the given index.
	 * @param lineIndex - the index of the wanted line
	 * @return the text line at lineIndex
	 */
	public String getLine(int lineIndex) {return lineList.get(lineIndex);}

	/**
	 * Returns the number of text lines contained in this instance.
	 * @return a number of text lines
	 */
	public int getLineCount() {return lineList.size();}

	@Override
	public Iterator<String> iterator() {
		return lineList.iterator();
	}
}
