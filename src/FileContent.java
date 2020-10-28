import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

public class FileContent implements Iterable<String> {

	private List<String> lineList = null;

	public FileContent(File inputFile) throws IOException {
		lineList = Files.readAllLines(inputFile.toPath());
	}

	public String getLine(int lineIndex) {return lineList.get(lineIndex);}

	public int getLineCount() {return lineList.size();}

	@Override
	public Iterator<String> iterator() {
		return lineList.iterator();
	}
}
