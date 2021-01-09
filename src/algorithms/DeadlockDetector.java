package algorithms;

import java.io.IOException;

import data.IntMatrix;

/**
 * This class implements a deadlock detection algorithm.
 * @author Guyllaume Rousseau
 */
public class DeadlockDetector extends DeadlockAlgorithm {

	/**
	 * This constructor initializes the data of the deadlock detection
	 * algorithm with the content of the text file designated by inputPath.
	 * @param inputPath - path of the input file
	 * @throws IOException if the file designated by inputPath is non-existent
	 * or does not have the extension .txt
	 */
	public DeadlockDetector(String inputPath) throws IOException {
		super(inputPath, RESULT_SUFFIX); // Can throw IOException.
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

		int procIndex = -1;
		for(int i=0; i<processCount; i++) {
			if(!end[i] && request.rowToIntMatrix(i).isLeqToMat(work)) {
				procIndex = i;
				break;
			}
		}

		if(procIndex > -1) {
			work.addition(allocation.rowToIntMatrix(procIndex));
			end[procIndex] = true;

			recordIterationNumber(iteration);
			recordProcessToExecute(procIndex);
			fileContent.addLine(null);
			recordWorkAndSaveItsState();
			fileContent.addLine(null);
			recordEndAndSaveItsState();
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
	 * @throws Exception if the DeadlockDetector constructor or
	 * DeadlockAlgorithm.execute throws one
	 */
	public static void main(String[] args) throws Exception {
		DeadlockDetector dd = new DeadlockDetector(args[0]);
		dd.execute();
	}
}
