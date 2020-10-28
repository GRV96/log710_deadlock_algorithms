import java.io.IOException;

public class InputFileReader {

	private static final String PROCESS_COUNT = "Processes: ";
	private static final String RESOURCE_COUNT = "Resources: ";
	private static final String ALLOCATION_ARRAY = "Allocation";
	private static final String RESOURCE_ARRAY = "Resources";
	private static final String REQUEST_ARRAY = "Request";

	private FileContent inputFileContent;

	private int processCount = -1;
	private int resourceCount = -1;

	private IntMatrix allocation = null;
	private IntMatrix resources = null;
	private IntMatrix request = null;

	public InputFileReader(FileContent inputFileContent) throws IOException,
	IllegalArgumentException, NumberFormatException {
		this.inputFileContent = inputFileContent;
		parseInputLines();
	}

	private IntMatrix extractIntMatrix(int startLine, int lines, int columns)
			throws IllegalArgumentException, NumberFormatException {
		IntMatrix matrix = null;
		if(lines == 1) {
			int[] intArray = new int[columns];
			lineToIntArray(inputFileContent.getLine(startLine), intArray);
			matrix = new IntMatrix(intArray);
		}
		else if(lines >= 2) {
			int endLine = startLine + lines;
			int[][] intArray2d = new int[lines][columns];
			linesToIntArray2d(startLine, endLine, intArray2d);
			matrix = new IntMatrix(intArray2d);
		}
		return matrix;
	}

	public IntMatrix getAllocationMatrix() {return new IntMatrix(allocation);}

	public int getProcessCount() {return processCount;}

	public int getResourceCount() {return resourceCount;}

	public IntMatrix getResourceMatrix() {return new IntMatrix(resources);}

	public IntMatrix getRequestMatrix() {return new IntMatrix(request);}

	private void linesToIntArray2d(int startLine, int endLine, int[][] intArray2d)
			throws IllegalArgumentException, NumberFormatException {
		for(int i=0, lineIndex=startLine; lineIndex<endLine; i++, lineIndex++) {
			String line = inputFileContent.getLine(lineIndex);
			lineToIntArray(line, intArray2d[i]);
		}
	}

	private static void lineToIntArray(String line, int[] intArray)
			throws IllegalArgumentException, NumberFormatException {
		String[] numbers = line.split(" ");

		if(numbers.length < intArray.length) {
			throw new IllegalArgumentException("Line " + line
					+ " does not contain enough numbers.");
		}

		for(int j=0; j<intArray.length; j++) {
			// Can throw NumberFormatException.
			intArray[j] = Integer.parseUnsignedInt(numbers[j]);
		}
	}

	private void parseInputLines()
			throws IllegalArgumentException, NumberFormatException {
		String procCountStr =
				inputFileContent.getLine(0).substring(PROCESS_COUNT.length());
		// Can throw NumberFormatException.
		processCount = Integer.parseUnsignedInt(procCountStr);

		String resourceCountStr =
				inputFileContent.getLine(1).substring(RESOURCE_COUNT.length());
		// Can throw NumberFormatException.
		resourceCount = Integer.parseUnsignedInt(resourceCountStr);

		int lineCount = inputFileContent.getLineCount();
		for(int lineIndex=2; lineIndex<lineCount; lineIndex++) {
			String line = inputFileContent.getLine(lineIndex);

			if(line.equals(ALLOCATION_ARRAY)) {
				allocation = extractIntMatrix(lineIndex+1,
						processCount, resourceCount);
				lineIndex += processCount;
			}
			else if(line.equals(RESOURCE_ARRAY)) {
				resources = extractIntMatrix(++lineIndex, 1, resourceCount);
			}
			else if(line.equals(REQUEST_ARRAY)) {
				request = extractIntMatrix(lineIndex+1,
						processCount, resourceCount);
				lineIndex += processCount;
			}
		}
	}
}
