package algorithms;

import java.io.IOException;

import data.IntMatrix;
import files.InputFileException;
import files.InputFileReader;

/**
 * This class implements a deadlock detection algorithm.
 * @author Guyllaume Rousseau
 */
public class DeadlockDetector extends DeadlockAlgorithm {

	/**
	 * This constructor parses the text file designated by inputPath in order
	 * to obtain the data that the deadlock detection algorithms require. In
	 * addition to the data obtained by the superclass' constructor, it
	 * initializes matrices Request and Work and the Boolean array End.
	 * @param inputPath - path of the input file
	 * @throws InputFileException if the input file contains a fault
	 * @throws IOException if the file designated by inputPath is non-existent
	 * or does not have the extension .txt
	 */
	public DeadlockDetector(String inputPath)
			throws InputFileException, IOException {
		// Can throw InputFileException or IOException.
		super(inputPath, RESULT_SUFFIX);

		request = inputReader.getMatrixRequest();
		if(request == null) {
			String message = makeUndefinedMatrixMsg(
					InputFileReader.MATRIX_REQUEST_TITLE);
			throw new InputFileException(message);
		}

		work = new IntMatrix(available);

		for(int i=0; i<processCount; i++) {
			end[i] = allocation.rowSum(i) == 0;
		}
	}

	@Override
	protected void afterLoop() {
		fileContent.addLine(null);
		recordWorkAndEndStates();
	}

	@Override
	protected boolean beforeLoop() {
		fileContent.addLine(null);
		recordArray(END_TITLE, booleanArrayToCharArray(end));
		return true;
	}

	@Override
	protected boolean loop() throws Exception {
		boolean keepLooping = true;
		fileContent.addLine(null, 2);

		int procNumber = -1;
		for(int i=0; i<processCount; i++) {
			if(!end[i] && request.rowToIntMatrix(i).isLeqToMat(work)) {
				procNumber = i;
				break;
			}
		}

		if(procNumber >= 0) {
			work.addition(allocation.rowToIntMatrix(procNumber));
			end[procNumber] = true;

			recordIterationNumber(iteration);
			recordProcessToExecute(procNumber);
			fileContent.addLine(null);
			recordIntMatrix(REQUEST_TITLE + "[" + procNumber + "]",
					request.rowToIntMatrix(procNumber));
			fileContent.addLine(null);
			recordEndAndSaveItsState();
			fileContent.addLine(null);
			recordWorkAndSaveItsState();
		}
		else {
			String procNumbers = "";
			for(int i=0; i<processCount; i++) {
				if(!end[i]) {
					procNumbers += i + " ";
				}
			}
			if(procNumbers.length() > 0) {
				fileContent.addLine("These processes are deadlocked: "
						+ procNumbers);
			}
			else {
				fileContent.addLine("No deadlock occured.");
			}
			keepLooping = false;
		}
		iteration++;
		return keepLooping;
	}

	/**
	 * Starts the deadlock detection algorithm.
	 * @param args - The input file path is the only argument.
	 */
	public static void main(String[] args) {
		try {
			DeadlockDetector dd = new DeadlockDetector(args[0]);
			dd.execute();
		}
		catch(InputFileException ife) {
			System.err.println(ife.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
