package files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * This class writes lines of text from a FileContent object in a text file.
 * The path to the output file is a constructor parameter.
 * @author Guyllaume Rousseau
 */
public class OutputFileWriter {

	/**
	 * A String object only containing the '\n' character
	 */
	private static final String NEW_LINE = "\n";

	/**
	 * This object represents the file in which the output is written.
	 */
	private File outputFile = null;

	/**
	 * The path to the output file
	 */
	private String outputPath = null;

	/**
	 * The instance created by this constructor can write data in a file whose
	 * path is parameter outputPath.
	 * @param outputPath - the path to the output file
	 * @throws NullPointerException if outputPath is null
	 */
	public OutputFileWriter(String outputPath) throws NullPointerException {
		this.outputPath = outputPath;
		if (outputPath == null)
		{
			throw new NullPointerException(
					"Parameter outputPath cannot be null.");
		}
	}

	/**
	 * Creates the file meant to contain lines of text. If the file already
	 * exists, a number is appended to the new file's name to differentiate it.
	 * @throws IOException if an I/O error occurs when the output file is
	 * created
	 */
	private void createOutputFile() throws IOException {
		int dotIndex = outputPath.lastIndexOf(FileUtil.PERIOD);
		String pathAndName = outputPath.substring(0, dotIndex);
		String extension = outputPath.substring(dotIndex);
		outputFile = new File(pathAndName + extension);

		int fileNumber = 2;
		while(!outputFile.createNewFile()) {
			outputFile = new File(pathAndName + fileNumber + extension);
			fileNumber++;
		}
	}

	/**
	 * Records the lines from fileContent in a new text file whose path has
	 * been given to this class' constructor.
	 * @param fileContent - an object containing lines of text to write to a
	 * text file
	 * @throws IOException if an I/O error occurs or if the file exists but is
	 * a directory rather than a regular file, does not exist but cannot be
	 * created or cannot be opened for any other reason
	 */
	public void writeToFile(FileContent fileContent) throws IOException {
		createOutputFile();
		FileWriter fw = new FileWriter(outputFile);
		Iterator<String> contentIterator = fileContent.iterator();
		while(contentIterator.hasNext()) {
			fw.write(contentIterator.next() + NEW_LINE);
		}
		fw.flush();
		fw.close();
	}
}
