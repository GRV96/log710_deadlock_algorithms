import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class InputFileReader {

	private static final String PROC_COUNT = "Processes: ";
	private static final String RESOURCE_COUNT = "Resources: ";
	private static final String ALLOCATION_ARRAY = "Allocation";
	private static final String RESOURCE_ARRAY = "Resources";
	private static final String REQUEST_ARRAY = "Request";

	private static final String REQUIRED_EXTENSION = "txt";

	private File inputFile;
	private List<String> inputFileLines;

	private int processCount = -1;
	private int resourceCount = -1;

	private IntMatrix allocation;
	private IntMatrix resources;
	private IntMatrix request;

	public InputFileReader(String filePath)
			throws IOException, NumberFormatException, RuntimeException {
		String extension = getFileExtension(filePath);
		if(extension==null || !extension.equals(REQUIRED_EXTENSION)) {
			throw new IOException("The input file must have the extension "
					+ REQUIRED_EXTENSION + ".");
		}

		inputFile = new File(filePath);
		inputFileLines = Files.readAllLines(inputFile.toPath());
		parseInputLines();
	}

	private static String getFileExtension(String filePath) {
		String extension = null;
		int dotIndex = filePath.lastIndexOf('.');
		if(dotIndex >= 0) {
			extension = filePath.substring(dotIndex+1);
		}
		return extension;
	}

	public IntMatrix getAllocationMatrix() {return new IntMatrix(allocation);}

	public int getProcessCount() {return processCount;}

	public int getResourceCount() {return resourceCount;}

	public IntMatrix getResourceMatrix() {return new IntMatrix(resources);}

	public IntMatrix getRequestMatrix() {return new IntMatrix(request);}

	private void linesToIntArray2d(int[][] intArray2d, int startLine, int endLine)
			throws NumberFormatException, RuntimeException {
		for(int i=0, lineIndex=startLine; lineIndex<endLine; i++, lineIndex++) {
			String line = inputFileLines.get(lineIndex);
			lineToIntArray(line, intArray2d[i]);
		}
	}

	private static void lineToIntArray(String line, int[] intArray)
			throws NumberFormatException, RuntimeException {
		String[] numbers = line.split(" ");

		if(numbers.length < intArray.length) {
			throw new RuntimeException("A line does not contain enough numbers.");
		}

		for(int j=0; j<intArray.length; j++) {
			intArray[j] = Integer.parseUnsignedInt(numbers[j]);
		}
	}

	private void parseInputLines() throws NumberFormatException, RuntimeException {
		String procCountStr =
				inputFileLines.get(0).substring(PROC_COUNT.length());
		processCount = Integer.parseUnsignedInt(procCountStr);

		String resourceCountStr =
				inputFileLines.get(1).substring(RESOURCE_COUNT.length());
		resourceCount = Integer.parseUnsignedInt(resourceCountStr);

		int[][] allocationArray = new int[processCount][resourceCount];
		int[] resourceArray = new int[resourceCount];
		int[][] requestArray = new int [processCount][resourceCount];

		int lineCount = inputFileLines.size();
		for(int lineIndex=0; lineIndex<lineCount; lineIndex++) {
			String line = inputFileLines.get(lineIndex);

			if(line.equals(ALLOCATION_ARRAY)) {
				int start = lineIndex + 1;
				int bound = start + processCount;
				linesToIntArray2d(allocationArray, start, bound);
				allocation = new IntMatrix(allocationArray);
				lineIndex += processCount;
			}
			else if(line.equals(RESOURCE_ARRAY)) {
				lineToIntArray(inputFileLines.get(++lineIndex), resourceArray);
				resources = new IntMatrix(resourceArray);
			}
			else if(line.equals(REQUEST_ARRAY)) {
				int start = lineIndex + 1;
				int bound = start + processCount;
				linesToIntArray2d(requestArray, start, bound);
				request = new IntMatrix(requestArray);
				lineIndex += processCount;
			}
		}
	}
}
