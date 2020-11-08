import java.io.IOException;

public abstract class DeadlockPreventer extends DeadlockAlgorithm {

	protected static final String NEED_TITLE = "Need";

	protected IntMatrix maximum = null;
	protected IntMatrix need = null;

	public DeadlockPreventer(String inputPath) throws IOException {
		super(inputPath);
		maximum = inputReader.getMaximumMatrix();
		need = new IntMatrix(maximum);
		need.substraction(allocation);
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

	protected boolean procExecIsSafe(int procNumber) {
		IntMatrix needRow = need.rowToIntMatrix(procNumber);
		fileContent.addLine("Process " + procNumber);
		fileContent.addLine("Need: " + needRow.rowToString(0, " "));
		fileContent.addLine("Work: " + work.rowToString(0, " "));
		return !end[procNumber] && needRow.isLeqToMat(work);
	}

	protected int safeSequenceIteration() throws IllegalArgumentException {
		int procNumber = -1;
		for(int i=0; i<processCount; i++) {
			if(procExecIsSafe(i)) {
				procNumber = i;
				break;
			}
		}

		if(procNumber >= 0) {
			work.addition(allocation.rowToIntMatrix(procNumber));
			end[procNumber] = true;
		}

		return procNumber;
	}
}
