package algorithms;

import java.io.IOException;

/**
 * This class attempts to make a sequence of processes that can be executed
 * in that sequence's order without putting the system in an unsafe state. The
 * system is in an unsafe state if a deadlock can occur.
 * @author Guyllaume Rousseau
 */
public class SafeSequenceMaker extends DeadlockPreventer {

	private static final String SAFE_SEQ_SUFFIX = "_safe_seq";

	private int safeSeqLength;
	private String safeSeqLine = null;

	/**
	 * This constructor initializes the data necessary to make a sequence of
	 * safe process executions.
	 * @param inputPath - path of the input file
	 * @throws IOException if the file designated by inputPath is non-existent
	 * or does not have the extension .txt
	 */
	public SafeSequenceMaker(String inputPath) throws IOException {
		super(inputPath, SAFE_SEQ_SUFFIX); // Can throw IOException.
	}

	/**
	 * Adds a process index to the sequence of processes that can be safely
	 * executed.
	 * @param procNumber - the index of a process
	 */
	private void addProcToSafeSeq(int procNumber) {
		safeSeqLength++;
		safeSeqLine += procNumber + " ";
	}

	@Override
	protected void afterLoop() {
		fileContent.addLine(null, 2);
		recordWorkAndEndStates();
	}

	@Override
	protected boolean beforeLoop() {
		safeSeqLength = 0;
		safeSeqLine = "Safe sequence: ";
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
			addProcToSafeSeq(procNumber);
			recordProcessToExecute(procNumber);
			fileContent.addLine(null);
			recordWorkAndSaveItsState();
			saveEndState();
			fileContent.addLine(null);
			recordSafeSequence();
			keepLooping = safeSeqLength < processCount;
		}
		else if(safeSeqLength < processCount) {
			recordNegativeResult();
			keepLooping = false;
		}

		iteration++;
		return keepLooping;
	}

	/**
	 * Starts the algorithm that makes safe process execution sequences.
	 * @param args - The input file path is the only argument.
	 * @throws Exception if the SafeSequenceMaker constructor or
	 * DeadlockAlgorithm.execute throws one
	 */
	public static void main(String[] args) throws Exception {
		SafeSequenceMaker ssm = new SafeSequenceMaker(args[0]);
		ssm.execute();
	}

	/**
	 * Records a failure to make a safe sequence including all processes.
	 */
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

	/**
	 * Records in fileContent a line listing the index of the processes that
	 * could be safely executed so far.
	 */
	private void recordSafeSequence() {
		fileContent.addLine(safeSeqLine);
	}
}
