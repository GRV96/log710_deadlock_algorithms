import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class OutputFileWriter {

	private File outputFile = null;
	private String outputPath = null;

	public OutputFileWriter(String outputPath) throws NullPointerException {
		this.outputPath = outputPath;
	}

	private void createFile() throws IOException {
		int dotIndex = outputPath.lastIndexOf('.');
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
		createFile();
		FileWriter fw = new FileWriter(outputFile);
		Iterator<String> contentIterator = fileContent.iterator();
		while(contentIterator.hasNext()) {
			fw.write(contentIterator.next() + "\n");
		}
		fw.flush();
		fw.close();
	}
}
