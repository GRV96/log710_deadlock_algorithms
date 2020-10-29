import java.io.File;
import java.io.IOException;

public class DeadlockDetection {

	private static final String INPUT_FILE_EXTENSION = "txt";

	private static String getFileExtension(String filePath) {
		String extension = null;
		int dotIndex = filePath.lastIndexOf('.');
		if(dotIndex >= 0) {
			extension = filePath.substring(dotIndex+1);
		}
		return extension;
	}

	public static void main(String[] args) throws Exception {
		// Rows: processes
		// Columns: resources allocated to processes

		String extension = getFileExtension(args[0]);
		if(extension==null || !extension.equals(INPUT_FILE_EXTENSION)) {
			throw new IOException("The input file must have the extension \""
					+ INPUT_FILE_EXTENSION + "\".");
		}
		File inputFile = new File(args[0]);
		FileContent fileContent = new FileContent(inputFile);

		InputFileReader ifr = new InputFileReader(fileContent);

		int processCount = ifr.getProcessCount();

		IntMatrix allocation = ifr.getAllocationMatrix();

		IntMatrix available = ifr.getResourceMatrix();
		for(int j=0; j<available.columns; j++) {
			available.set(0, j, available.get(0, j)-allocation.columnSum(j));
		}
		IntMatrix work = new IntMatrix(available);

		IntMatrix request = ifr.getRequestMatrix();

		Boolean[] end = new Boolean[processCount];
		for(int i=0; i<processCount; i++) {
			end[i] = allocation.rowSum(i) == 0;
		}

		fileContent.addLine(null);
		recordIntMatrix(fileContent, "Avaiable", available);
		fileContent.addLine(null);
		recordArray(fileContent, "End", end);

		int iteration = 1;
		while(true) {
			fileContent.addLine(null, 2);
			fileContent.addLine("ITERATION " + iteration);

			int procIndex = -1;
			for(int i=0; i<processCount; i++) {
				if(!end[i] && requestLeqWork(request.rowToIntMatrix(i), work)) {
					procIndex = i;
					break;
				}
			}

			if(procIndex > -1) {
				work.addition(allocation.rowToIntMatrix(procIndex));
				end[procIndex] = true;

				fileContent.addLine("Process " + procIndex + " executed");
				fileContent.addLine(null);
				recordIntMatrix(fileContent, "Work", work);
				fileContent.addLine(null);
				recordArray(fileContent, "End", end);
			}
			else {
				String deadlockProcs = "These processes are deadlocked: ";
				for(int i=0; i<processCount; i++) {
					if(!end[i]) {
						deadlockProcs += i + " ";
					}
				}
				fileContent.addLine(deadlockProcs);
				break;
			}
			iteration++;
		}
		OutputFileWriter ofw = new OutputFileWriter(args[0]);
		ofw.writeToFile(fileContent);
	}

	private static <T> void recordArray(FileContent fc,
			String arrayTitle, T[] array) {
		fc.addLine(arrayTitle);
		String line = "";
		for(int i=0; i<array.length; i++) {
			line += array[i] + " ";
		}
		fc.addLine(line);
	}

	private static void recordIntMatrix(FileContent fc,
			String matrixTitle, IntMatrix matrix) {
		fc.addLine(matrixTitle);
		for(int i=0; i<matrix.rows; i++) {
			String line = "";
			for(int j=0; j<matrix.columns; j++) {
				line += matrix.get(i, j) + " ";
			}
			fc.addLine(line);
		}
	}

	private static boolean requestLeqWork(IntMatrix requestRow, IntMatrix work) {
		for(int j=0; j<work.columns; j++) {
			if(requestRow.get(0, j) > work.get(0, j)) {
				return false;
			}
		}
		return true;
	}
}
