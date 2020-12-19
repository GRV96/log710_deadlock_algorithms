import java.io.File;
import java.io.IOException;

/**
 * This abstract class contains the data and methods that all deadlock
 * algorithms may need.
 * @author Guyllaume Rousseau
 */
public abstract class DeadlockAlgorithm {

	/**
	 * Title of the allocation matrix
	 */
	protected static final String ALLOCATION_TITLE = "Allocation";

	/**
	 * Title of the available matrix
	 */
	protected static final String AVAILABLE_TITLE = "Available";

	/**
	 * Title of the end boolean array
	 */
	protected static final String END_TITLE = "End";

	/**
	 * Title of the request matrix
	 */
	protected static final String REQUEST_TITLE = "Request";

	/**
	 * Title of the work matrix
	 */
	protected static final String WORK_TITLE = "Work";

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

	protected OutputFileWriter outputWriter = null;

	/**
	 * The number of processes involved in the algorithm
	 */
	protected int processCount = -1;

	/**
	 * The allocation matrix matches the processes (rows) with the number of
	 * resources of each type (columns) allocated to them.
	 */
	protected IntMatrix allocation = null;

	/**
	 * The available matrix indicates the number of available resources of
	 * each type (columns). This is a row matrix.
	 */
	protected IntMatrix available = null;

	/**
	 * The request matrix indicates the number of resources of each type
	 * (columns) requested by the processes (rows).
	 */
	protected IntMatrix request = null;

	/**
	 * The work matrix is initialized with the content of available. It is
	 * changed and compared to another matrix in each iteration of an
	 * algorithm. This is a row matrix whose length is equal to the number of
	 * resource types.
	 */
	protected IntMatrix work = null;

	/**
	 * The end array indicates whether a process has been executed (true) or
	 * not (false). Its length is equal to the number of processes involved in
	 * the algorithm.
	 */
	protected Boolean[] end = null;

	/**
	 * The number of the current iteration
	 */
	protected int iteration;

	/**
	 * This constructor initializes the data of a deadlock algorithm with the
	 * content of the text file designated by inputPath.
	 * @param inputPath - path of the input file
	 * @param outputPathSuffix - a suffix to append to the input file's name
	 * to form that of the output file
	 * @throws IOException if the file designated by inputPath is non-existent
	 * or does not have the extension .txt
	 */
	protected DeadlockAlgorithm(String inputPath, String outputPathSuffix)
			throws IOException {
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

		allocation = inputReader.getAllocationMatrix();

		initAvailableMatrix();

		request = inputReader.getRequestMatrix();

		end = new Boolean[processCount];
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
	 * This method executes the deadlock algorithm. It records the available
	 * matrix in fileContent, calls beforeLoop and, if it returns true, calls
	 * method loop repeatedly until it returns false.
	 * @throws Exception if loop throws one
	 */
	public final void execute() throws Exception {
		fileContent.addLine(null);
		recordIntMatrix(AVAILABLE_TITLE, available);

		iteration = 1;
		boolean keepLooping = beforeLoop();
		while(keepLooping) {
			keepLooping = loop();
		}

		outputWriter.writeToFile(fileContent);
	}

	/**
	 * Initializes the row matrix available. Available is created as a copy of
	 * the resource matrix from the input file, then the sum of every column
	 * of the allocation matrix is substracted from the corresponding cell of
	 * available.
	 */
	protected void initAvailableMatrix() {
		available = inputReader.getResourceMatrix();
		IntMatrix allocColumnSum = allocation.columnSumMatrix();
		available.substraction(allocColumnSum);
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
		String line = "";
		for(int i=0; i<array.length; i++) {
			line += array[i] + " ";
		}
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
		String line = arrayTitle + ": ";
		for(int i=0; i<array.length; i++) {
			line += array[i] + " ";
		}
		fileContent.addLine(line);
		return line;
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
			String line = "";
			for(int j=0; j<matrix.columns; j++) {
				line += matrix.get(i, j) + " ";
			}
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
}
