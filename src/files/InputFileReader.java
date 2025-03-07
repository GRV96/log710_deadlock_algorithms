package files;

import java.util.regex.PatternSyntaxException;

import matrix.IntMatrix;

/**
 * Instances of this class extract data required by the deadlock algorithms
 * from input files. When a line of text containing numbers is mentioned in
 * this class, it is implied that the numbers are separated by spaces.
 * @author Guyllaume Rousseau
 */
public class InputFileReader {

	private static final String PROCESS_COUNT_INDICATION = "Processes: ";
	private static final String RES_TYPE_COUNT_INDICATION = "Resource types: ";

	public static final String MATRIX_ALLOCATION_TITLE = "Allocation";
	public static final String MATRIX_MAXIMUM_TITLE = "Maximum";
	public static final String MATRIX_REQUEST_TITLE = "Request";
	public static final String MATRIX_RESOURCES_TITLE = "Resources";

	private static final String INT_PARSING_EXCEP_MSG =
			"failure to parse an unsigned integer.";

	private static final String SPACE_STR = " ";

	private int processCount = -1;
	private int resourceTypeCount = -1;

	private IntMatrix allocation = null;
	private IntMatrix maximum = null;
	private IntMatrix resources = null;
	private IntMatrix request = null;

	/**
	 * This constructor reads the data from a FileContent instance.
	 * @param inputFileContent - a FileContent instance containing data
	 * required by a deadlock algorithm
	 * @throws InputFileException if the input file contains a fault
	 */
	public InputFileReader(FileContent inputFileContent)
			throws InputFileException {
		parseInputLines(inputFileContent);
	}

	/**
	 * Throws an InputFileException if the number of columns in matrix is
	 * different from the number of resource types.
	 * @param matrix - an IntMatrix instance defined according to the input
	 * file
	 * @param matrixTitle - the title of matrix
	 * @throws InputFileException if matrix.columns != resourceTypeCount
	 */
	private void exceptionForColumnCount(IntMatrix matrix, String matrixTitle)
			throws InputFileException {
		if(matrix.columns != resourceTypeCount) {
			throw new InputFileException("The number of columns in "
					+ matrixTitle
					+ " does not match the number of resource types.");
		}
	}

	/**
	 * Throws an InputFileException if the number of processes or resource
	 * types is undefined. The exceptions' message signals that an attempt to
	 * parse the specified matrix was made in this condition.
	 * @param matrixTitle - the title of a matrix in the input file
	 * @throws InputFileException if the number of processes or resource types
	 * is currently undefined
	 */
	private void exceptionForUndefProcResCount(String matrixTitle)
			throws InputFileException {
		if(processCount < 0) {
			if(resourceTypeCount < 0) {
				throw new InputFileException("Attempt to parse matrix "
						+ matrixTitle
						+ " while the number of processes and "
						+ "resource types is undefined");
			}
			else {
				throw new InputFileException("Attempt to parse matrix "
						+ matrixTitle
						+ " while the number of processes is undefined");
			}
		}
		else if(resourceTypeCount < 0) {
			throw new InputFileException("Attempt to parse matrix "
					+ matrixTitle
					+ " while the number of resource types is undefined");
		}
	}

	/**
	 * Creates an IntMatrix instance from rows of integral numbers recorded in
	 * fileContent.
	 * @param fileContent - a FileContent instance containing lines of integral
	 * numbers
	 * @param startLine - the index of the first line of integral numbers
	 * @param lines - the number of lines of the matrix to create
	 * @return a new IntMatrix containing integral numbers from the specified
	 * lines of fileContent or null if lines is less than 1
	 * @throws IllegalArgumentException if lines is less than 1
	 * @throws InputFileException if the input file contains a fault
	 */
	private static IntMatrix extractIntMatrix(FileContent fileContent,
			int startLine, int lines)
					throws IllegalArgumentException, InputFileException {
		IntMatrix matrix = null;
		if(lines < 1) {
			throw new IllegalArgumentException(
					"Parameter lines must be greater than or equal to 1.");
		}
		else if(lines == 1) {
			int[] intArray = null;

			try {
				intArray = lineToIntArray(
						fileContent.getLine(startLine), SPACE_STR);
			}
			catch(NumberFormatException nfe) {
				throw new InputFileException(INT_PARSING_EXCEP_MSG);
			}

			try {
				matrix = new IntMatrix(intArray);
			}
			catch(IllegalArgumentException iae) {
				throw new InputFileException(
						"it does not contain any element.");
			}
		}
		else { // lines >= 2
			int[][] intArray2d = null;

			try {
				intArray2d = linesToIntArray2d(fileContent, startLine,
						lines, SPACE_STR);
			}
			catch(NumberFormatException nfe) {
				throw new InputFileException(INT_PARSING_EXCEP_MSG);
			}

			try {
				matrix = new IntMatrix(intArray2d);
			}
			catch(IllegalArgumentException iae) {
				throw new InputFileException(
						"its rows have different lengths.");
			}
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
	 * Accessor of matrix Allocation
	 * @return a copy of matrix Allocation
	 */
	public IntMatrix getMatrixAllocation() {
		return allocation==null? null: new IntMatrix(allocation);
	}

	/**
	 * Accessor of matrix Maximum
	 * @return a copy of matrix Maximum
	 */
	public IntMatrix getMatrixMaximum() {
		return maximum==null? null: new IntMatrix(maximum);
	}

	/**
	 * Accessor of matrix Request
	 * @return a copy of matrix Request
	 */
	public IntMatrix getMatrixRequest() {
		return request==null? null: new IntMatrix(request);
	}

	/**
	 * Accessor of matrix Resources
	 * @return a copy of matrix Resources
	 */
	public IntMatrix getMatrixResources() {
		return resources==null? null: new IntMatrix(resources);
	}

	/**
	 * Accessor of the number of processes
	 * @return the number of processes
	 */
	public int getProcessCount() {return processCount;}

	/**
	 * Accessor of the number of resource types
	 * @return the number of resource types
	 */
	public int getResourceTypeCount() {return resourceTypeCount;}

	/**
	 * Creates a 2-dimensional array filled with integral numbers from lines
	 * stored in fileContent.
	 * @param fileContent - a FileContent instance containing lines of integral
	 * numbers
	 * @param startLine - the index of the first line of integral numbers
	 * @param lines - the number of lines of the array to create
	 * @param separator - a regular expression matching the characters that
	 * separate the numbers in string line
	 * @return a 2-dimensional array containing the numbers from the selected
	 * lines
	 * @throws IllegalArgumentException if lines is less than 1
	 * @throws NumberFormatException if parsing a string for an int fails
	 * @throws PatternSyntaxException if the syntax of regular expression
	 * separator is invalid
	 */
	private static int[][] linesToIntArray2d(FileContent fileContent,
			int startLine, int lines, String separator)
					throws IllegalArgumentException,
					NumberFormatException, PatternSyntaxException {
		if(lines < 1) {
			throw new IllegalArgumentException(
					"Parameter lines must be greater than or equal to 1.");
		}

		int[][] intArray2d = new int[lines][];
		int endLine = startLine + lines;
		for(int i=0, lineIndex=startLine; lineIndex<endLine; i++, lineIndex++) {
			String line = fileContent.getLine(lineIndex);
			intArray2d[i] = lineToIntArray(line, separator);
		}
		return intArray2d;
	}

	/**
	 * Creates an array filled with integral numbers from a line of text. In
	 * that line, the numbers must be separated by a sequence of characters
	 * matching parameter separator.
	 * @param line - a line of text containing integral numbers
	 * @param separator - a regular expression matching the characters that
	 * separate the numbers in string line
	 * @return an array containing the integral numbers from line
	 * @throws NumberFormatException if parsing a string for an unsigned
	 * integer fails
	 * @throws PatternSyntaxException if the syntax of regular expression
	 * separator is invalid
	 */
	private static int[] lineToIntArray(String line, String separator)
			throws NumberFormatException, PatternSyntaxException {
		// Can throw PatternSyntaxException.
		String[] numbers = line.split(separator);
		int arrayLength = numbers.length;
		int[] intArray = new int[arrayLength];

		for(int j=0; j<arrayLength; j++) {
			// Can throw NumberFormatException.
			intArray[j] = Integer.parseUnsignedInt(numbers[j]);
		}

		return intArray;
	}

	/**
	 * Reads a FileContent instance to define the number of processes, the
	 * number of resource types and the matrices needed by a deadlock
	 * algorithm.
	 * @param fileContent - a FileContent instance
	 * @throws InputFileException if the input file contains a fault
	 */
	private void parseInputLines(FileContent fileContent)
			throws InputFileException {
		int lineCount = fileContent.getLineCount();
		for(int lineIndex=0; lineIndex<lineCount; lineIndex++) {
			String line = fileContent.getLine(lineIndex);

			if(line.indexOf(PROCESS_COUNT_INDICATION) >= 0) {
				String procCountStr =
						line.substring(PROCESS_COUNT_INDICATION.length());
				try {
					processCount = Integer.parseUnsignedInt(procCountStr);
				}
				catch(NumberFormatException nfe) {
					throw new InputFileException(
							"Error when parsing the number of processes: "
									+ procCountStr);
				}

				if(processCount < 2) {
					throw new InputFileException(
							"At least two processes must be involved.");
				}
			}
			else if(line.indexOf(RES_TYPE_COUNT_INDICATION) >= 0) {
				String resourceTypeCountStr =
						line.substring(RES_TYPE_COUNT_INDICATION.length());
				try {
					resourceTypeCount =
							Integer.parseUnsignedInt(resourceTypeCountStr);
				}
				catch(NumberFormatException nfe) {
					throw new InputFileException(
							"Error when parsing the number of resource types: "
									+ resourceTypeCountStr);
				}

				if(resourceTypeCount < 1) {
					throw new InputFileException(
							"There must be at least one resource type.");
				}
			}
			else if(line.equals(MATRIX_ALLOCATION_TITLE)) {
				exceptionForUndefProcResCount(MATRIX_ALLOCATION_TITLE);
				try {
					allocation = extractIntMatrix(fileContent,
							lineIndex+1, processCount);
				}
				catch (InputFileException ife) {
					String message = InputFileException.makeMessage(
							MATRIX_ALLOCATION_TITLE, ife.getMessage());
					throw new InputFileException(message);
				}
				exceptionForColumnCount(allocation, MATRIX_ALLOCATION_TITLE);
				lineIndex += processCount;
			}
			else if(line.equals(MATRIX_MAXIMUM_TITLE)) {
				exceptionForUndefProcResCount(MATRIX_MAXIMUM_TITLE);
				try {
					maximum = extractIntMatrix(fileContent,
							lineIndex+1, processCount);
				}
				catch (InputFileException ife) {
					String message = InputFileException.makeMessage(
							MATRIX_MAXIMUM_TITLE, ife.getMessage());
					throw new InputFileException(message);
				}
				exceptionForColumnCount(maximum, MATRIX_MAXIMUM_TITLE);
			}
			else if(line.equals(MATRIX_RESOURCES_TITLE)) {
				exceptionForUndefProcResCount(MATRIX_RESOURCES_TITLE);
				try {
					resources = extractIntMatrix(fileContent, ++lineIndex, 1);
				}
				catch (InputFileException ife) {
					String message = InputFileException.makeMessage(
							MATRIX_RESOURCES_TITLE, ife.getMessage());
					throw new InputFileException(message);
				}
				exceptionForColumnCount(resources, MATRIX_RESOURCES_TITLE);
			}
			else if(line.equals(MATRIX_REQUEST_TITLE)) {
				exceptionForUndefProcResCount(MATRIX_REQUEST_TITLE);
				try {
					request = extractIntMatrix(fileContent,
							lineIndex+1, processCount);
				}
				catch (InputFileException ife) {
					String message = InputFileException.makeMessage(
							MATRIX_REQUEST_TITLE, ife.getMessage());
					throw new InputFileException(message);
				}
				exceptionForColumnCount(request, MATRIX_REQUEST_TITLE);
				lineIndex += processCount;
			}
		}
	}
}
