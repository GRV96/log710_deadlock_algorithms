import java.io.File;
import java.io.IOException;

public abstract class DeadlockAlgorithm {

	protected FileContent fileContent = null;
	protected InputFileReader inputReader = null;
	protected OutputFileWriter outputWriter = null;

	protected int processCount = -1;

	protected IntMatrix allocation = null;
	protected IntMatrix available = null;
	protected IntMatrix work = null;

	protected Boolean[] end = null;

	protected DeadlockAlgorithm(String inputPath) throws IOException {
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
				FileUtil.addSuffixToPath(inputPath, FileUtil.RESULT_SUFFIX);
		outputWriter = new OutputFileWriter(outputPath);

		processCount = inputReader.getProcessCount();

		allocation = inputReader.getAllocationMatrix();

		available = inputReader.getResourceMatrix();
		for(int j=0; j<available.columns; j++) {
			available.set(0, j, available.get(0, j)-allocation.columnSum(j));
		}
		work = new IntMatrix(available);

		end = new Boolean[processCount];
	}

	public abstract void execute() throws Exception;

	protected <T> void recordArray(String arrayTitle, T[] array) {
		fileContent.addLine(arrayTitle);
		String line = "";
		for(int i=0; i<array.length; i++) {
			line += array[i] + " ";
		}
		fileContent.addLine(line);
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

	protected void writeProcessToExecute(int procIndex) {
		fileContent.addLine("Process " + procIndex + " executed");
	}

	protected void writeIterationNumber(int iteration) {
		fileContent.addLine("ITERATION " + iteration);
	}
}
