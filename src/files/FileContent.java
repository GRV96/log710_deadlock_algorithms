package files;

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
	 * the file.
	 * @param file - a file containing text
	 * @throws IOException if an I/O error occurs reading from the file or a
	 * malformed or unmappable byte sequence is read
	 */
	public FileContent(File file) throws IOException {
		lineList = Files.readAllLines(file.toPath());
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
			lineList.add(line);
		}
	}

	/**
	 * Adds a line of text n times after the currently contained lines.
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
	 * @throws IndexOutOfBoundsException
	 * if lineIndex < 0 || this.getLineCount() <= lineIndex
	 */
	public String getLine(int lineIndex) throws IndexOutOfBoundsException {
		return lineList.get(lineIndex);
	}

	/**
	 * Returns the number of text lines contained in this instance.
	 * @return a number of text lines
	 */
	public int getLineCount() {return lineList.size();}

	@Override
	public Iterator<String> iterator() {return lineList.iterator();}
}
