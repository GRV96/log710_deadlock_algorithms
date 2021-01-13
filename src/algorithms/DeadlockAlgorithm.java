package algorithms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import data.IntMatrix;
import files.FileContent;
import files.FileUtil;
import files.InputFileException;
import files.InputFileReader;
import files.OutputFileWriter;

/**
 * This abstract class contains the data and methods that all deadlock
 * algorithms may need.
 * @author Guyllaume Rousseau
 */
public abstract class DeadlockAlgorithm {

	/**
	 * This message is written in the output file if the allocated resources
	 * exceed the number of existent resources.
	 */
	private static final String ALLOC_EXCEEDS_RES_MSG =
			"ERROR! The allocation exceeds the existent resources.";

	/**
	 * Title of matrix Allocation
	 */
	protected static final String ALLOCATION_TITLE = "Allocation";

	/**
	 * Title of matrix Available
	 */
	protected static final String AVAILABLE_TITLE = "Available";

	/**
	 * Title of boolean array End
	 */
	protected static final String END_TITLE = "End";

	/**
	 * Title of boolean array End's state list
	 */
	protected static final String END_THROUGH_ITERS_TITLE =
			"End through the iterations";

	/**
	 * Title of matrix Request
	 */
	protected static final String REQUEST_TITLE = "Request";

	/**
	 * Title of matrix Work
	 */
	protected static final String WORK_TITLE = "Work";

	/**
	 * Title of matrix Work's state list
	 */
	protected static final String WORK_THROUGH_ITERS_TITLE =
			"Work through the iterations";

	/**
	 * A suffix that can be appended to the input file's name to form the
	 * output file's name
	 */
	protected static final String RESULT_SUFFIX = "_result";

	/**
	 * This object is initially filled with the content of the input file. As
	 * the algorithm is performed, more text can be added to it in order to
	 * record the algorithm's steps. In the end, fileContent is written in the
	 * output file.
	 */
	protected FileContent fileContent = null;

	/**
	 * This object parses fileContent to extract the data required by the
	 * algorithm.
	 */
	protected InputFileReader inputReader = null;

	/**
	 * This object creates the output file and stores lines of text meant to
	 * be written in it.
	 */
	protected OutputFileWriter outputWriter = null;

	/**
	 * The number of processes involved in the algorithm
	 */
	protected int processCount = -1;

	/**
	 * Matrix Allocation matches the processes (rows) with the number of
	 * resources of each type (columns) allocated to them.
	 */
	protected IntMatrix allocation = null;

	/**
	 * True if the allocated resources are less than or equal to the number of
	 * existent resources. More precisely, this field is true if the sum of
	 * each column j of matrix Allocation is less than or equal to the number
	 * in cell (0, j) of matrix Resources. This field is false otherwise.
	 */
	protected final boolean allocLeqResources;

	/**
	 * Matrix Available indicates the number of available resources of each
	 * type (columns). This is a row matrix.
	 */
	protected IntMatrix available = null;

	/**
	 * Matrix Request indicates the number of resources of each type (columns)
	 * requested by the processes (rows).
	 */
	protected IntMatrix request = null;

	/**
	 * Matrix Work is initialized with the content of Available. It is changed
	 * and compared to another matrix in each iteration of an algorithm. This
	 * is a row matrix whose length is equal to the number of resource types.
	 */
	protected IntMatrix work = null;

	/**
	 * Boolean array End indicates whether a process has been executed (true)
	 * or not (false). Its length is equal to the number of processes involved
	 * in the algorithm.
	 */
	protected Boolean[] end = null;

	/**
	 * The number of the current iteration. Iteration number starts at 1.
	 */
	protected int iteration;

	/**
	 * The state of matrix Work can be saved in this list at each iteration.
	 */
	protected List<Integer[]> workStates = null;

	/**
	 * The state of array End can be saved in this list at each iteration.
	 * Character 'T' means true; 'F' means false.
	 */
	protected List<Character[]> endStates = null;

	/**
	 * This constructor initializes the data of a deadlock algorithm with the
	 * content of the text file designated by inputPath.
	 * @param inputPath - path of the input file
	 * @param outputPathSuffix - a suffix to append to the input file's name
	 * to form that of the output file
	 * @throws InputFileException if the input file contains a fault
	 * @throws IOException if the file designated by inputPath is non-existent
	 * or does not have the extension .txt
	 */
	protected DeadlockAlgorithm(String inputPath, String outputPathSuffix)
			throws InputFileException, IOException {
		String extension = FileUtil.getFileExtension(inputPath);
		if(extension==null || !extension.equals(FileUtil.TXT_EXTENSION)) {
			throw new IOException("The input file must have the extension \""
					+ FileUtil.TXT_EXTENSION + "\".");
		}
		File inputFile = new File(inputPath);
		if(!inputFile.exists()) {
			throw new IOException("File " + inputPath + " does not exist.");
		}
		fileContent = new FileContent(inputFile);
		inputReader = new InputFileReader(fileContent);
		String outputPath =
				FileUtil.addSuffixToPath(inputPath, outputPathSuffix);
		outputWriter = new OutputFileWriter(outputPath);

		processCount = inputReader.getProcessCount();

		IntMatrix resources = inputReader.getMatrixResources();
		allocation = inputReader.getMatrixAllocation();
		// If false, Available contains at least one negative number.
		allocLeqResources = allocation.columnSumMatrix().isLeqToMat(resources);

		available = resources;
		IntMatrix allocColumnSum = allocation.columnSumMatrix();
		available.substraction(allocColumnSum);

		request = inputReader.getMatrixRequest();

		end = new Boolean[processCount];

		workStates = new ArrayList<Integer[]>();
		endStates = new ArrayList<Character[]>();
	}

	/**
	 * This method is meant to be overridden. It is executed once after the
	 * algorithm ends. The default implementation does nothing.
	 */
	protected void afterLoop() {}

	/**
	 * Converts an array to a line of text. Array elements are separated by
	 * spaces in the text line.
	 * @param <T> - the array's data type
	 * @param array - the array to convert
	 * @return a line of text containing the array's elements
	 */
	private <T> String arrayToTextLine(T[] array) {
		String line = "";
		for(int i=0; i<array.length; i++) {
			line += array[i] + " ";
		}
		return line;
	}

	/**
	 * This method is meant to be overridden. It is executed once before the
	 * algorithm starts. If necessary, it can perform some checks to determine
	 * whether the algorithm can be executed. The default implementation
	 * returns true and does nothing else.
	 * @return true if the algorithm can be executed, false otherwise
	 */
	protected boolean beforeLoop() {return true;}

	/**
	 * Converts an array of Boolean objects to an array of Character objects.
	 * In the output array, 'T' means true, and 'F' means false.
	 * @param boolArray - an array of Boolean objects
	 * @return an array of Character objects
	 */
	protected static Character[] booleanArrayToCharArray(Boolean[] boolArray) {
		int valueCount = boolArray.length;
		Character[] charArray = new Character[valueCount];

		for(int i=0; i<valueCount; i++) {
			charArray[i] = boolArray[i]? 'T': 'F';
		}

		return charArray;
	}

	/**
	 * This method executes the deadlock algorithm. It records matrix Available
	 * in fileContent, calls beforeLoop and, if it returns true, calls method
	 * loop repeatedly until it returns false. At last, execute calls afterLoop
	 * then writes the output file.
	 * @throws Exception if loop throws one
	 */
	public final void execute() throws Exception {
		fileContent.addLine(null);
		recordIntMatrix(AVAILABLE_TITLE, available);

		if(!allocLeqResources) {
			System.err.print(ALLOC_EXCEEDS_RES_MSG + "\n\n");
			fileContent.addLine(null);
			fileContent.addLine(ALLOC_EXCEEDS_RES_MSG);
			outputWriter.writeToFile(fileContent);
			return;
		}

		iteration = 1;
		boolean keepLooping = beforeLoop();
		while(keepLooping) {
			keepLooping = loop();
		}
		afterLoop();

		outputWriter.writeToFile(fileContent);
	}

	/**
	 * Performs an iteration of the algorithm. It is repeatedly called by
	 * execute until it returns false. No other method should call loop. The
	 * instance variable iteration should be incremented in this method.
	 * @return true if the algorithm must continue, false otherwise
	 * @throws Exception if necessary
	 */
	protected abstract boolean loop() throws Exception;

	/**
	 * Records a 1-dimensional array in fileContent so it will be written in
	 * the output file. The array's values are separated by spaces.
	 * @param <T> - the array's data type
	 * @param arrayTitle - The title is recorded on the line above the array.
	 * @param array - the 1-dimensional array to record
	 */
	protected <T> void recordArray(String arrayTitle, T[] array) {
		fileContent.addLine(arrayTitle);
		String line = arrayToTextLine(array);
		fileContent.addLine(line);
	}

	/**
	 * Records a 1-dimensional array in fileContent so it will be written in
	 * the output file. The array's values are separated by spaces.
	 * @param <T> - the array's data type
	 * @param arrayTitle - The title is recorded on the same line as the
	 * array, at the beginning.
	 * @param array - the 1-dimensional array to record
	 * @return the line of text that represents the array and was recorded
	 */
	protected <T> String recordArrayOneLine(String arrayTitle, T[] array) {
		String line = arrayTitle + ": " + arrayToTextLine(array);
		fileContent.addLine(line);
		return line;
	}

	/**
	 * Records in fileContent an array's states through this algorithm's
	 * iterations.
	 * @param <T> - the arrays' data type
	 * @param title - The title is recorded on the line above the states's
	 * record.
	 * @param stateList - Contains an array's successive states.
	 */
	protected <T> void recordArrayStates(String title, List<T[]> stateList) {
		fileContent.addLine(title);
		int iterCount = 1;
		Iterator<T[]> stateIter = stateList.iterator();
		while(stateIter.hasNext()) {
			T[] array = stateIter.next();
			String line = Integer.toString(iterCount)
					+ ") " + arrayToTextLine(array);
			fileContent.addLine(line);
			iterCount++;
		}
	}

	/**
	 * Records the End array in fileContent and adds its current state to
	 * its state list. End's elements are represented by 'T' (true) and 'F'
	 * (false).
	 */
	protected void recordEndAndSaveItsState() {
		Character[] endAsCharArray = booleanArrayToCharArray(end);
		recordArray(END_TITLE, endAsCharArray);
		endStates.add(endAsCharArray);
	}

	/**
	 * Records a matrix in fileContent so it will be written in the output
	 * file. The values in the matrix's rows are separated with spaces.
	 * @param matrixTitle - The title is recorded on the line above the matrix.
	 * @param matrix - the matrix to record
	 */
	protected void recordIntMatrix(String matrixTitle, IntMatrix matrix) {
		fileContent.addLine(matrixTitle);
		for(int i=0; i<matrix.rows; i++) {
			Integer[] array = matrix.rowToArray(i);
			String line = arrayToTextLine(array);
			fileContent.addLine(line);
		}
	}

	/**
	 * Records the specified row of a matrix in fileContent so it will be
	 * written in the output file. The values in the matrix's row are separated
	 * with spaces.
	 * @param matrixTitle - The title is recorded on the same line as the
	 * matrix row, at the beginning.
	 * @param matrix - the matrix of which a row is to be recorded
	 * @param row - the index of the row to record
	 * @return the line of text that represents the matrix row and was recorded
	 */
	protected String recordIntMatrixRow(String matrixTitle,
			IntMatrix matrix, int row) {
		String line = matrixTitle;
		if(matrix.rows > 1) {
			line += "[" + row + "]";
		}
		line += ": " + matrix.rowToString(row, " ");
		fileContent.addLine(line);
		return line;
	}

	/**
	 * Records the number of the current iteration in fileContent so it will
	 * be written in the output file. This method produces the line
	 * "ITERATION n".
	 * @param iteration - number of the current iteration
	 */
	protected void recordIterationNumber(int iteration) {
		fileContent.addLine("ITERATION " + iteration);
	}

	/**
	 * Records in fileContent the index of a process selected for execution.
	 * This method produces the line "Process n executed".
	 * @param procIndex - index of the executed process
	 */
	protected void recordProcessToExecute(int procIndex) {
		fileContent.addLine("Process " + procIndex + " executed");
	}

	/**
	 * Records Work's and End's successive states in fileContent. They are
	 * separated by an empty line.
	 */
	protected void recordWorkAndEndStates() {
		recordArrayStates(WORK_THROUGH_ITERS_TITLE, workStates);
		fileContent.addLine(null);
		recordArrayStates(END_THROUGH_ITERS_TITLE, endStates);
	}

	/**
	 * Records matrix Work in fileContent and adds its current state to its
	 * state list.
	 */
	protected void recordWorkAndSaveItsState() {
		recordIntMatrix(WORK_TITLE, work);
		workStates.add(work.rowToArray(0));
	}

	/**
	 * Adds End's current state to its state list.
	 */
	protected void saveEndState() {
		Character[] endAsCharArray = booleanArrayToCharArray(end);
		endStates.add(endAsCharArray);
	}

	/**
	 * Adds Work's current state to its state list.
	 */
	protected void saveWorkState() {
		workStates.add(work.rowToArray(0));
	}

	/**
	 * Adds Work's and End's current state to their respective state list.
	 */
	protected void saveWorkAndEndState() {
		saveWorkState();
		saveEndState();
	}
}
