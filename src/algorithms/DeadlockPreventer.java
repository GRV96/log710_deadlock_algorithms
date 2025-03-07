package algorithms;

import java.io.IOException;

import files.InputFileException;
import files.InputFileReader;
import matrix.IntMatrix;

/**
 * This abstract class contains the data and methods that all deadlock
 * prevention algorithms may need.
 * @author Guyllaume Rousseau
 */
public abstract class DeadlockPreventer extends DeadlockAlgorithm {

	/**
	 * A message announcing the start of the banker's algorithm
	 */
	private static final String BANKERS_ALGO_LINE = "Banker's algorithm";

	/**
	 * The title of the matrix Need
	 */
	protected static final String NEED_TITLE = "Need";

	/**
	 * The String "\t"
	 */
	private static final String TABULATION = "\t";

	/**
	 * The maximum matrix matches processes (rows) with the maximum number of
	 * each type of resource (columns) they can have.
	 */
	protected IntMatrix maximum = null;

	/**
	 * The need matrix matches processes (rows) with the maximum number of
	 * each type of resource (columns) they may need in addition to the
	 * resources already allocated to them.
	 */
	protected IntMatrix need = null;

	/**
	 * This constructor parses the text file designated by inputPath in order
	 * to obtain the data that the deadlock prevention algorithms require. In
	 * addition to the data obtained by the superclass' constructor, it
	 * initializes matrices Maximum and Need.
	 * @param inputPath - path of the input file
	 * @param outputPathSuffix - a suffix to append to the input file's name
	 * @throws InputFileException if the input file contains a fault
	 * @throws IOException if the file designated by inputPath is non-existent
	 * or does not have the extension .txt
	 */
	protected DeadlockPreventer(String inputPath, String outputPathSuffix)
			throws InputFileException, IOException {
		// Can throw InputFileException or IOException.
		super(inputPath, outputPathSuffix);
		maximum = inputReader.getMatrixMaximum();
		if(maximum == null) {
			String message = makeUndefinedMatrixMsg(
					InputFileReader.MATRIX_MAXIMUM_TITLE);
			throw new InputFileException(message);
		}
		need = new IntMatrix(maximum);
		need.subtraction(allocation);
	}

	/**
	 * Announces the start of the banker's algorithm in the output file.
	 */
	protected void announceBankersAlgorithm() {
		fileContent.addLine(BANKERS_ALGO_LINE);
	}

	/**
	 * Performs an iteration of the banker's algorithm.
	 * @param recordData - If true, the banker's algorithm's data is recorded
	 * in the output file.
	 * @return the index of a process safe to execute or -1 if there is none
	 * @throws IllegalArgumentException if an addition is attempted with
	 * matrix Work and a matrix of different dimensions
	 */
	protected int bankersAlgorithmIter(boolean recordData)
			throws IllegalArgumentException {
		int procIndex = -1;
		for(int i=0; i<processCount; i++) {
			boolean safe = procExecIsSafe(i, recordData);

			if(recordData) {
				fileContent.addLine(null);
			}

			if(safe) {
				procIndex = i;
				break;
			}
		}

		if(procIndex >= 0) {
			work.addition(allocation.rowToIntMatrix(procIndex));
			finish[procIndex] = true;
		}

		if(recordData) {
			recordArrayOneLine(FINISH_TITLE, booleanArrayToCharArray(finish));
		}

		return procIndex;
	}

	/**
	 * Determines whether all the elements of array Finish are true.
	 * @return true if all the elements of array Finish are true, false if at
	 * least one element is false.
	 */
	protected boolean finishArrayIsTrue() {
		for(int i=0; i<finish.length; i++) {
			if(!finish[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets all the values in Boolean array Finish to false.
	 */
	protected void initArrayFinish() {
		for(int i=0; i<processCount; i++) {
			finish[i] = false;
		}
	}

	/**
	 * Determine whether the execution of process procNumber would put the
	 * system in an unsafe sate.
	 * @param procIndex - the index of a process
	 * @param recordData - If true, the data determining whether the execution
	 * is safe is recorded in the output file.
	 * @return true if the system would stay safe after the process'
	 * execution, false otherwise
	 */
	protected boolean procExecIsSafe(int procIndex, boolean recordData) {
		IntMatrix needRow = need.rowToIntMatrix(procIndex);
		if(recordData) {
			fileContent.addLine("\tProcess " + procIndex);
			fileContent.addLine("\tFinish[" + procIndex + "]: "
					+ booleanToChar(finish[procIndex], true));
			recordIntMatrixRow(TABULATION + NEED_TITLE, need, procIndex);
			recordIntMatrixRow(TABULATION + WORK_TITLE, work, 0);
		}
		return !finish[procIndex] && needRow.isLeqToMat(work);
	}
}
