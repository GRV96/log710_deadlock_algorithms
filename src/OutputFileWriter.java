import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class OutputFileWriter {

	private static final String NEW_LINE = "\n";
	private static final char PERIOD = '.';

	private File outputFile = null;
	private String outputPath = null;

	public OutputFileWriter(String outputPath) throws NullPointerException {
		this.outputPath = outputPath;
	}

	private void createOutputFile() throws IOException {
		int dotIndex = outputPath.lastIndexOf(PERIOD);
		String pathAndName = outputPath.substring(0, dotIndex);
		String extension = outputPath.substring(dotIndex);
		outputFile = new File(pathAndName + extension);

		int fileNumber = 2;
		while(!outputFile.createNewFile()) {
			outputFile = new File(pathAndName + fileNumber + extension);
			fileNumber++;
		}
	}

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
