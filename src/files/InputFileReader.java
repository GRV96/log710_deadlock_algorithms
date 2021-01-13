package files;

import java.util.regex.PatternSyntaxException;

import data.IntMatrix;

/**
 * Instances of this class extract data required by the deadlock algorithms
 * from input files. When a line of text containing numbers is mentioned in
 * this class, it is implied that the numbers are separated by spaces.
 * @author Guyllaume Rousseau
 */
public class InputFileReader {

	private static final String PROCESS_COUNT = "Processes: ";
	private static final String RESOURCE_TYPE_COUNT = "Resource types: ";

	private static final String MATRIX_ALLOCATION_TITLE = "Allocation";
	private static final String MATRIX_MAXIMUM_TITLE = "Maximum";
	private static final String MATRIX_REQUEST_TITLE = "Request";
	private static final String MATRIX_RESOURCES_TITLE = "Resources";

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
	 * @return a new IntMatrix containing integral numbers from the specified
	 * lines of fileContent or null if lines is less than 1
	 * @throws IllegalArgumentException if a constructor of IntMatrix receives
	 * an invalid array of integral numbers
	 * @throws NumberFormatException if parsing a string for an int fails
	 */
	private static IntMatrix extractIntMatrix(FileContent fileContent,
			int startLine, int lines)
					throws IllegalArgumentException, NumberFormatException {
		IntMatrix matrix = null;
		if(lines == 1) {
			int[] intArray =
					lineToIntArray(fileContent.getLine(startLine), SPACE_STR);
			matrix = new IntMatrix(intArray);
		}
		else if(lines >= 2) {
			int endLine = startLine + lines;
			int[][] intArray2d = linesToIntArray2d(fileContent, startLine,
					endLine, SPACE_STR);
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
	 * @param endLine - the index of the last line of integral numbers + 1
	 * @param separator - a regular expression matching the characters that
	 * separate the numbers in string line
	 * @return a 2-dimensional array containing the numbers from the selected
	 * lines
	 * @throws IllegalArgumentException if endLine is less than or equal to
	 * startLine
	 * @throws NumberFormatException if parsing a string for an int fails
	 * @throws PatternSyntaxException if the syntax of regular expression
	 * separator is invalid
	 */
	private static int[][] linesToIntArray2d(FileContent fileContent,
			int startLine, int endLine, String separator)
					throws IllegalArgumentException,
					NumberFormatException, PatternSyntaxException {
		if(endLine <= startLine) {
			throw new IllegalArgumentException(
					"Parameter endLine must be greater than startLine.");
		}

		int[][] intArray2d = new int[endLine-startLine][];
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
	 * @throws NumberFormatException if parsing a string for an int fails
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
			intArray[j] = Integer.parseInt(numbers[j]);
		}

		return intArray;
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

		String resourceTypeCountStr =
				fileContent.getLine(1).substring(RESOURCE_TYPE_COUNT.length());
		// Can throw NumberFormatException.
		resourceTypeCount = Integer.parseUnsignedInt(resourceTypeCountStr);

		int lineCount = fileContent.getLineCount();
		for(int lineIndex=2; lineIndex<lineCount; lineIndex++) {
			String line = fileContent.getLine(lineIndex);

			if(line.equals(MATRIX_ALLOCATION_TITLE)) {
				allocation = extractIntMatrix(fileContent,
						lineIndex+1, processCount);
				lineIndex += processCount;
			}
			else if(line.equals(MATRIX_MAXIMUM_TITLE)) {
				maximum = extractIntMatrix(fileContent,
						lineIndex+1, processCount);
			}
			else if(line.equals(MATRIX_RESOURCES_TITLE)) {
				resources = extractIntMatrix(fileContent, ++lineIndex, 1);
			}
			else if(line.equals(MATRIX_REQUEST_TITLE)) {
				request = extractIntMatrix(fileContent,
						lineIndex+1, processCount);
				lineIndex += processCount;
			}
		}
	}
}
