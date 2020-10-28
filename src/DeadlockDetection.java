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
		FileContent inputFileContent = new FileContent(inputFile);

		InputFileReader ifr = new InputFileReader(inputFileContent);

		int processCount = ifr.getProcessCount();

		IntMatrix allocation = ifr.getAllocationMatrix();

		IntMatrix available = ifr.getResourceMatrix();
		for(int j=0; j<available.columns; j++) {
			available.set(0, j, available.get(0, j)-allocation.columnSum(j));
		}
		IntMatrix work = new IntMatrix(available);

		IntMatrix request = ifr.getRequestMatrix();

		boolean[] end = new boolean[processCount];
		for(int i=0; i<processCount; i++) {
			end[i] = allocation.rowSum(i) == 0;
		}

		while(true) {
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
			}
			else {
				System.out.print("These processes are deadlocked: ");
				String procNumbers = "";
				for(int i=0; i<processCount; i++) {
					if(!end[i]) {
						procNumbers += i + " ";
					}
				}
				System.out.println(procNumbers);
				break;
			}
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
