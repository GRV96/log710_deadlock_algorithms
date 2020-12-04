/**
 * Instances of this class extract data required by the deadlock algorithms
 * from input files. When a line of text containing numbers is mentioned in
 * this class, it is implied that the numbers are separated by spaces.
 * @author Guyllaume Rousseau
 */
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

	/**
	 * This constructor reads the data from a FileContent instance.
	 * @param inputFileContent - a FileContent instance containing data
	 * required by a deadlock algorithm
	 * @throws NumberFormatException if parsing a string for an int fails
	 */
	public InputFileReader(FileContent inputFileContent)
			throws NumberFormatException {
		parseInputLines(inputFileContent);
	}

	/**
	 * Creates an IntMatrix instance from rows of integral numbers recorded in
	 * fileContent.
	 * @param fileContent - a FileContent instance containing lines of integral
	 * numbers
	 * @param startLine - the index of the first line of integral numbers
	 * @param lines - the number of lines of the matrix to create
	 * @param columns - the number of columns of the matrix to create
	 * @return a new IntMatrix containing integral numbers from the specified
	 * lines of fileContent
	 * @throws IllegalArgumentException if a constructor of IntMatrix receives
	 * an invalid array of integral numbers
	 * @throws NumberFormatException if parsing a string for an int fails
	 */
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

	/**
	 * Sets all the squares of intArray to the same value.
	 * @param intArray - a 1-dimensional int array
	 * @param number - the value to put in all squares of intArray
	 */
	private static void initIntArray(int[] intArray, int number) {
		for(int i=0; i<intArray.length; i++) {
			intArray[i] = number;
		}
	}

	/**
	 * Sets all the squares of intArray to the same value.
	 * @param intArray - a 2-dimensional int array
	 * @param number - the value to put in all squares of intArray
	 */
	private static void initIntArray(int[][] intArray, int number) {
		for(int i=0; i<intArray.length; i++) {
			initIntArray(intArray[i], number);
		}
	}

	/**
	 * Accessor of the allocation matrix
	 * @return a copy of the allocation matrix
	 */
	public IntMatrix getAllocationMatrix() {
		return allocation==null? null: new IntMatrix(allocation);
	}

	/**
	 * Accessor of the maximum matrix
	 * @return a copy of the maximum matrix
	 */
	public IntMatrix getMaximumMatrix() {return new IntMatrix(maximum);}

	/**
	 * Accessor of the number of processes
	 * @return the number of processes
	 */
	public int getProcessCount() {return processCount;}

	/**
	 * Accessor of the number of resource types
	 * @return the number of resource types
	 */
	public int getResourceCount() {return resourceCount;}

	/**
	 * Accessor of the resource matrix
	 * @return a copy of the resource matrix
	 */
	public IntMatrix getResourceMatrix() {
		return resources==null? null: new IntMatrix(resources);
	}

	/**
	 * Accessor of the request matrix
	 * @return a copy of the request matrix
	 */
	public IntMatrix getRequestMatrix() {
		return request==null? null: new IntMatrix(request);
	}

	/**
	 * Fills intArray2d with integral numbers from lines recorded in
	 * fileContent.
	 * @param fileContent - a FileContent instance containing lines of integral
	 * numbers
	 * @param startLine - the index of the first line of integral numbers
	 * @param endLine - the index of the last line of integral numbers + 1
	 * @param intArray2d - a 2-dimensional int array
	 * @throws NumberFormatException if parsing a string for an int fails
	 */
	private static void linesToIntArray2d(FileContent fileContent,
			int startLine, int endLine, int[][] intArray2d)
					throws NumberFormatException {
		for(int i=0, lineIndex=startLine; lineIndex<endLine; i++, lineIndex++) {
			String line = fileContent.getLine(lineIndex);
			lineToIntArray(line, intArray2d[i]);
		}
	}

	/**
	 * Fills intArray with integral numbers from a line of text.
	 * @param line - a line of text containing integral numbers
	 * @param intArray - a 1-dimensional int array
	 * @throws NumberFormatException if parsing a string for an int fails
	 */
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

	/**
	 * Reads a FileContent instance to define the number of processes, the
	 * number of resource types and the matrices needed by a deadlock
	 * algorithm.
	 * @param fileContent - a FileContent instance
	 * @throws NumberFormatException if parsing a string for an int fails
	 */
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
