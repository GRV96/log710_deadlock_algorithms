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

	protected void announceBankersAlgorithm() {
		fileContent.addLine(BANKERS_ALGO_LINE);
	}

	protected int bankersAlgorithmIter(boolean recordData)
			throws IllegalArgumentException {
		int procNumber = -1;
		for(int i=0; i<processCount; i++) {
			if(procExecIsSafe(i, recordData)) {
				procNumber = i;
				break;
			}
		}

		if(procNumber >= 0) {
			work.addition(allocation.rowToIntMatrix(procNumber));
			end[procNumber] = true;
		}

		if(recordData) {
			recordArrayOneLine(END_TITLE, end);
		}

		return procNumber;
	}

	protected boolean endArrayIsTrue() {
		for(int i=0; i<end.length; i++) {
			if(!end[i]) {
				return false;
			}
		}
		return true;
	}

	protected void initEndArray() {
		for(int i=0; i<processCount; i++) {
			end[i] = false;
		}
	}

	protected boolean procExecIsSafe(int procNumber, boolean recordData) {
		IntMatrix needRow = need.rowToIntMatrix(procNumber);
		if(recordData) {
			fileContent.addLine("Process " + procNumber);
			fileContent.addLine("End[" + procNumber + "]: " + end[procNumber]);
			recordIntMatrixRow(NEED_TITLE, need, procNumber);
			recordIntMatrixRow(WORK_TITLE, work, 0);
		}
		return !end[procNumber] && needRow.isLeqToMat(work);
	}
}
