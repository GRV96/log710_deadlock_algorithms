import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeadlockPreventer extends DeadlockAlgorithm {

	private static final String NEED_TITLE = "Need";

	private IntMatrix maximum = null;
	private IntMatrix need = null;

	private List<Integer> processOrder = null;
	private List<Integer> safeSequence = null;
	private String safeSeqLine = null;

	public DeadlockPreventer(String inputPath) throws IOException {
		super(inputPath, 0);
		for(int i=0; i<processCount; i++) {
			end[i] = false;
		}
		maximum = inputReader.getMaximumMatrix();
		need = new IntMatrix(maximum);
		need.substraction(allocation);
		processOrder = inputReader.getProcressOrder();
		safeSequence = new ArrayList<Integer>();
		safeSeqLine = "Safe sequence: ";
	}

	private void addProcToSafeSeq(int procIndex) {
		safeSequence.add(procIndex);
		safeSeqLine += procIndex + " ";
	}

	private boolean endArrayIsTrue() {
		for(int i=0; i<end.length; i++) {
			if(!end[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean loop() throws Exception {
		if(iteration == 0) {
			if(!systemStateIsSafe()) {
				fileContent.addLine("ERROR! The system is in an unsafe state.");
				return false;
			}
			else {
				iteration++;
				return true;
			}
		}

		fileContent.addLine(null, 2);
		recordIterationNumber(iteration);
		fileContent.addLine(null);

		int procNumber = processOrder.remove(0);
		IntMatrix requestRow = request.rowToIntMatrix(procNumber);
		IntMatrix needRow = need.rowToIntMatrix(procNumber);

		if(!requestRow.isLeqThanMat(needRow)) {
			fileContent.addLine("ERROR! Process " + procNumber
					+ " requests more resources than allowed.");
			return false;
		}
		else if(requestRow.isLeqThanMat(available)) {
			IntMatrix availableCopy = new IntMatrix(available);
			IntMatrix allocationCopy = new IntMatrix(allocation);
			IntMatrix needCopy = new IntMatrix(need);

			// Simulate allocation
			available.substraction(requestRow);
			allocation.additionOnRow(request, procNumber);
			need.substractionOnRow(request, procNumber);

			if(systemStateIsSafe()) {
				recordProcessToExecute(procNumber);
				fileContent.addLine(null);
				addProcToSafeSeq(procNumber);
				recordSafeSequence();
				fileContent.addLine(null);
				recordIntMatrix(ALLOCATION_TITLE, allocation);
				fileContent.addLine(null);
				recordIntMatrix(AVAILABLE_TITLE, available);
				fileContent.addLine(null);
				recordIntMatrix(NEED_TITLE, need);
			}
			else {
				// Cancel allocation
				available = availableCopy;
				allocation = allocationCopy;
				need = needCopy;
				fileContent.addLine("Executing process " + procNumber
						+ " would put the system in an unsafe state.");
			}
		}
		else {
			if(processOrder.size() > 0) {
				processOrder.add(procNumber);
			}
			fileContent.addLine("Process " + procNumber
					+ " requests more resources than available.");
		}

		iteration++;
		return processOrder.size() > 0;
	}

	public static void main(String[] args) throws Exception {
		DeadlockPreventer dp = new DeadlockPreventer(args[0]);
		dp.execute();
	}

	private void recordSafeSequence() {
		fileContent.addLine(safeSeqLine);
	}

	private boolean systemStateIsSafe() throws IllegalArgumentException {
		work = new IntMatrix(available);

		while(true) {
			int procIndex = -1;
			for(int i=0; i<processCount; i++) {
				IntMatrix needRow = need.rowToIntMatrix(i);
				if(!end[i] && needRow.isLeqThanMat(work)) {
					procIndex = i;
					break;
				}
			}

			if(procIndex >= 0) {
				work.addition(allocation.rowToIntMatrix(procIndex));
				end[procIndex] = true;
			}
			else {
				return endArrayIsTrue();
			}
		}
	}
}
