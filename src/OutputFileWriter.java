import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class OutputFileWriter {

	private File outputFile = null;
	private String inputPath = null;

	public OutputFileWriter(String inputPath) throws NullPointerException {
		this.inputPath = inputPath;
	}

	private void createFile() throws IOException {
		int dotIndex = inputPath.lastIndexOf('.');
		String pathAndName = inputPath.substring(0, dotIndex);
		pathAndName += "_result";
		String extension = inputPath.substring(dotIndex);
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
			fw.write(contentIterator.next()+"\n");
		}
		fw.flush();
		fw.close();
	}
}
