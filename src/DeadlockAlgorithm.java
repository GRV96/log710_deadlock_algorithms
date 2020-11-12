import java.io.File;
import java.io.IOException;

public abstract class DeadlockAlgorithm {

	protected static final String ALLOCATION_TITLE = "Allocation";
	protected static final String AVAILABLE_TITLE = "Available";
	protected static final String END_TITLE = "End";
	protected static final String REQUEST_TITLE = "Request";
	protected static final String WORK_TITLE = "Work";

	protected static final String RESULT_SUFFIX = "_result";

	protected FileContent fileContent = null;
	protected InputFileReader inputReader = null;
	protected OutputFileWriter outputWriter = null;

	protected int processCount = -1;

	protected IntMatrix allocation = null;
	protected IntMatrix available = null;
	protected IntMatrix request = null;
	protected IntMatrix work = null;

	protected Boolean[] end = null;

	protected int iteration;

	protected DeadlockAlgorithm(String inputPath, String outputPathSuffix)
			throws IOException {
		String extension = FileUtil.getFileExtension(inputPath);
		if(extension==null || !extension.equals(FileUtil.FILE_EXTENSION)) {
			throw new IOException("The input file must have the extension \""
					+ FileUtil.FILE_EXTENSION + "\".");
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

	protected boolean beforeLoop() {return true;}

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

	protected void initAvailableMatrix() {
		available = inputReader.getResourceMatrix();
		IntMatrix allocColumnSum = allocation.columnSumMatrix();
		available.substraction(allocColumnSum);
	}

	protected abstract boolean loop() throws Exception;

	protected <T> void recordArray(String arrayTitle, T[] array) {
		fileContent.addLine(arrayTitle);
		String line = "";
		for(int i=0; i<array.length; i++) {
			line += array[i] + " ";
		}
		fileContent.addLine(line);
	}

	protected <T> String recordArrayOneLine(String arrayTitle, T[] array) {
		String line = arrayTitle + ": ";
		for(int i=0; i<array.length; i++) {
			line += array[i] + " ";
		}
		fileContent.addLine(line);
		return line;
	}

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

	protected void recordIterationNumber(int iteration) {
		fileContent.addLine("ITERATION " + iteration);
	}

	protected void recordProcessToExecute(int procIndex) {
		fileContent.addLine("Process " + procIndex + " executed");
	}
}
