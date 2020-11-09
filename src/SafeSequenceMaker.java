import java.io.IOException;

public class SafeSequenceMaker extends DeadlockPreventer {

	private static final String SAFE_SEQ_SUFFIX = "_safe_seq";

	private int safeSeqLength;
	private String safeSeqLine = null;

	public SafeSequenceMaker(String inputPath) throws IOException {
		super(inputPath, SAFE_SEQ_SUFFIX);
		safeSeqLine = "Safe sequence: ";
	}

	private void addProcToSafeSeq(int procNumber) {
		safeSeqLine += procNumber + " ";
	}

	@Override
	protected boolean beforeLoop() {
		safeSeqLength = 0;
		initEndArray();
		work = new IntMatrix(available);
		return true;
	}

	@Override
	protected boolean loop() {
		boolean keepLooping = false;

		fileContent.addLine(null, 2);
		recordIterationNumber(iteration);
		fileContent.addLine(null);

		announceBankersAlgorithm();
		int procNumber = bankersAlgorithmIter(true);
		fileContent.addLine(null);
		if(procNumber >= 0) {
			safeSeqLength++;
			addProcToSafeSeq(procNumber);
			recordProcessToExecute(procNumber);
			fileContent.addLine(null);
			recordArray(END_TITLE, end);
			fileContent.addLine(null);
			recordIntMatrix(WORK_TITLE, work);
			fileContent.addLine(null);
			recordSafeSequence();
			keepLooping = safeSeqLength < processCount;
		}
		else if(safeSeqLength < processCount) {
			recordNegativeResult();
		}
		iteration++;
		return keepLooping;
	}

	public static void main(String[] args) throws Exception {
		SafeSequenceMaker ssm = new SafeSequenceMaker(args[0]);
		ssm.execute();
	}

	private void recordNegativeResult() {
		if(safeSeqLength > 0) {
			fileContent.addLine("Only " + safeSeqLength + " processes out of "
					+ processCount + " could be executed safely.");
			fileContent.addLine(null);
			recordSafeSequence();
		}
		else {
			fileContent.addLine("No process could be executed safely.");
		}
	}

	private void recordSafeSequence() {
		fileContent.addLine(safeSeqLine);
	}
}
