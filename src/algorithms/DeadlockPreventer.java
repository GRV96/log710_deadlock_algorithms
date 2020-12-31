package algorithms;

import java.io.IOException;

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
	 * Title of the need matrix
	 */
	protected static final String NEED_TITLE = "Need";

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
	 * This constructor initializes the data of a deadlock prevention
	 * algorithm with the content of the text file designated by inputPath.
	 * @param inputPath - path of the input file
	 * @param outputPathSuffix - a suffix to append to the input file's name
	 * @throws IOException if the file designated by inputPath is non-existent
	 * or does not have the extension .txt
	 */
	public DeadlockPreventer(String inputPath, String outputPathSuffix)
			throws IOException {
		super(inputPath, outputPathSuffix); // Can throw IOException.
		maximum = inputReader.getMaximumMatrix();
		need = new IntMatrix(maximum);
		need.substraction(allocation);
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
	 * @throws IllegalArgumentException if an addition is attempted with the
	 * work matrix and a matrix of different dimensions
	 */
	protected int bankersAlgorithmIter(boolean recordData)
			throws IllegalArgumentException {
		int procIndex = -1;
		for(int i=0; i<processCount; i++) {
			if(procExecIsSafe(i, recordData)) {
				procIndex = i;
				break;
			}
		}

		if(procIndex >= 0) {
			work.addition(allocation.rowToIntMatrix(procIndex));
			end[procIndex] = true;
		}

		if(recordData) {
			recordArrayOneLine(END_TITLE, booleanArrayToCharArray(end));
		}

		return procIndex;
	}

	/**
	 * Determines whether all the squares of the end array contain true.
	 * @return true if all the squares of the end array contain true of false
	 * if at least one square contains false.
	 */
	protected boolean endArrayIsTrue() {
		for(int i=0; i<end.length; i++) {
			if(!end[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets all the values in end to false.
	 */
	protected void initEndArray() {
		for(int i=0; i<processCount; i++) {
			end[i] = false;
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
			fileContent.addLine("Process " + procIndex);
			fileContent.addLine("End[" + procIndex + "]: " + end[procIndex]);
			recordIntMatrixRow(NEED_TITLE, need, procIndex);
			recordIntMatrixRow(WORK_TITLE, work, 0);
		}
		return !end[procIndex] && needRow.isLeqToMat(work);
	}
}
