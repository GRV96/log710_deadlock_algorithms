import java.io.IOException;

public abstract class DeadlockPreventer extends DeadlockAlgorithm {

	private static final String BANKERS_ALGO_LINE = "Banker's algorithm";
	protected static final String NEED_TITLE = "Need";

	protected IntMatrix maximum = null;
	protected IntMatrix need = null;

	public DeadlockPreventer(String inputPath, String outputPathSuffix)
			throws IOException {
		super(inputPath, outputPathSuffix);
		maximum = inputReader.getMaximumMatrix();
		need = new IntMatrix(maximum);
		need.substraction(allocation);
	}

	protected void announceBankersAlgorithm() {
		fileContent.addLine(BANKERS_ALGO_LINE);
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
			fileContent.addLine("end[" + procNumber + "]: " + end[procNumber]);
			recordIntMatrixRow(NEED_TITLE, need, procNumber);
			recordIntMatrixRow(WORK_TITLE, work, 0);
		}
		return !end[procNumber] && needRow.isLeqToMat(work);
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
}
