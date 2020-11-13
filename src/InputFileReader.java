import java.io.IOException;

public class InputFileReader {

	private static final String PROCESS_COUNT = "Processes: ";
	private static final String RESOURCE_COUNT = "Resources: ";
	private static final String RESOURCE_ARRAY = "Resources";
	private static final String ALLOCATION_MATRIX = "Allocation";
	private static final String MAXIMUM_MATRIX = "Maximum";
	private static final String REQUEST_MATRIX = "Request";

	private int processCount = -1;
	private int resourceCount = -1;

	private IntMatrix allocation = null;
	private IntMatrix maximum = null;
	private IntMatrix resources = null;
	private IntMatrix request = null;

	public InputFileReader(FileContent inputFileContent)
			throws IOException, NumberFormatException {
		parseInputLines(inputFileContent);
	}

	private static IntMatrix extractIntMatrix(FileContent fileContent,
			int startLine, int lines, int columns)
					throws IllegalArgumentException, NumberFormatException {
		IntMatrix matrix = null;
		if(lines == 1) {
			int[] intArray = new int[columns];
			lineToIntArray(fileContent.getLine(startLine), intArray);
			matrix = new IntMatrix(intArray);
		}
		else if(lines >= 2) {
			int endLine = startLine + lines;
			int[][] intArray2d = new int[lines][columns];
			linesToIntArray2d(fileContent, startLine, endLine, intArray2d);
			matrix = new IntMatrix(intArray2d);
		}
		return matrix;
	}

	private static void initIntArray(int[] intArray, int value) {
		for(int i=0; i<intArray.length; i++) {
			intArray[i] = value;
		}
	}

	private static void initIntArray(int[][] intArray, int value) {
		for(int i=0; i<intArray.length; i++) {
			initIntArray(intArray[i], value);
		}
	}

	public IntMatrix getAllocationMatrix() {
		return allocation==null? null: new IntMatrix(allocation);
	}

	public IntMatrix getMaximumMatrix() {return new IntMatrix(maximum);}

	public int getProcessCount() {return processCount;}

	public int getResourceCount() {return resourceCount;}

	public IntMatrix getResourceMatrix() {
		return resources==null? null: new IntMatrix(resources);
	}

	public IntMatrix getRequestMatrix() {
		return request==null? null: new IntMatrix(request);
	}

	private static void linesToIntArray2d(FileContent fileContent,
			int startLine, int endLine, int[][] intArray2d)
					throws NumberFormatException {
		for(int i=0, lineIndex=startLine; lineIndex<endLine; i++, lineIndex++) {
			String line = fileContent.getLine(lineIndex);
			lineToIntArray(line, intArray2d[i]);
		}
	}

	private static void lineToIntArray(String line, int[] intArray)
			throws NumberFormatException {
		String[] numbers = line.split(" ");

		int length = numbers.length>=intArray.length?
				intArray.length: numbers.length;

		for(int j=0; j<length; j++) {
			// Can throw NumberFormatException.
			intArray[j] = Integer.parseInt(numbers[j]);
		}
	}

	private void parseInputLines(FileContent fileContent)
			throws NumberFormatException {
		String procCountStr =
				fileContent.getLine(0).substring(PROCESS_COUNT.length());
		// Can throw NumberFormatException.
		processCount = Integer.parseUnsignedInt(procCountStr);

		String resourceCountStr =
				fileContent.getLine(1).substring(RESOURCE_COUNT.length());
		// Can throw NumberFormatException.
		resourceCount = Integer.parseUnsignedInt(resourceCountStr);

		int lineCount = fileContent.getLineCount();
		for(int lineIndex=2; lineIndex<lineCount; lineIndex++) {
			String line = fileContent.getLine(lineIndex);

			if(line.equals(ALLOCATION_MATRIX)) {
				allocation = extractIntMatrix(fileContent,
						lineIndex+1, processCount, resourceCount);
				lineIndex += processCount;
			}
			else if(line.equals(MAXIMUM_MATRIX)) {
				maximum = extractIntMatrix(fileContent,
						lineIndex+1, processCount, resourceCount);
			}
			else if(line.equals(RESOURCE_ARRAY)) {
				resources = extractIntMatrix(fileContent,
						++lineIndex, 1, resourceCount);
			}
			else if(line.equals(REQUEST_MATRIX)) {
				request = extractIntMatrix(fileContent,
						lineIndex+1, processCount, resourceCount);
				lineIndex += processCount;
			}
		}
	}
}
