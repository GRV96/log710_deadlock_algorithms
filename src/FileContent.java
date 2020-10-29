import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileContent implements Iterable<String> {

	private static final String EMPTY_STRING = "";

	private List<String> lineList = null;

	public FileContent() {
		lineList = new ArrayList<String>();
	}

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

	public void addLine(String line) {
		if(line == null) {
			lineList.add(EMPTY_STRING);
		}
		else {
			lineList.add(line.trim());
		}
	}

	public void addLine(String line, int n) {
		for(int i=0; i<n; i++) {
			addLine(line);
		}
	}

	public String getLine(int lineIndex) {return lineList.get(lineIndex);}

	public int getLineCount() {return lineList.size();}

	@Override
	public Iterator<String> iterator() {
		return lineList.iterator();
	}
}
