import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeadlockPreventer extends DeadlockAlgorithm {

	private static final String NEED_TITLE = "Need";

	private IntMatrix maximum = null;
	private IntMatrix need = null;

	private List<Integer> processOrder = null;
	private Map<Integer, Boolean> processesTried = null;
	private String safeSeqLine = null;

	public DeadlockPreventer(String inputPath) throws IOException {
		super(inputPath, 0);
		maximum = inputReader.getMaximumMatrix();
		need = new IntMatrix(maximum);
		need.substraction(allocation);
		processOrder = inputReader.getProcressOrder();
		safeSeqLine = "Safe sequence: ";

		processesTried = new HashMap<Integer, Boolean>();
		Iterator<Integer> procNumIter = processOrder.iterator();
		while(procNumIter.hasNext()) {
			processesTried.put(procNumIter.next(), false);
		}
	}

	@Override
	protected void beforeLoop() {
		fileContent.addLine(null);
		recordIntMatrix(NEED_TITLE, need);
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
		processesTried.put(procNumber, true);
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

			boolean safeState = systemStateIsSafe();
			recordArray(END_TITLE, end);

			if(safeState) {
				safeSeqLine += procNumber + " ";
				fileContent.addLine(null);
				recordProcessToExecute(procNumber);
				fileContent.addLine(null);
				recordIntMatrix(ALLOCATION_TITLE, allocation);
				fileContent.addLine(null);
				recordIntMatrix(AVAILABLE_TITLE, available);
				fileContent.addLine(null);
				recordIntMatrix(NEED_TITLE, need);

				processesTried.remove(procNumber);
				makeProcessesUntried();
			}
			else {
				// Cancel allocation
				available = availableCopy;
				allocation = allocationCopy;
				need = needCopy;

				fileContent.addLine(null);
				fileContent.addLine("Executing process " + procNumber
						+ " would put the system in an unsafe state.");

				// The process is put on hold.
				processOrder.add(procNumber);
			}
		}
		else {
			fileContent.addLine("Process " + procNumber
					+ " requests more resources than available.");
			// The process is put on hold.
			processOrder.add(procNumber);
		}

		fileContent.addLine(null);
		recordSafeSequence();

		iteration++;
		return processesTried.containsValue(false);
	}

	public static void main(String[] args) throws Exception {
		DeadlockPreventer dp = new DeadlockPreventer(args[0]);
		dp.execute();
	}

	private void makeProcessesUntried() {
		Set<Integer> procNumbers = processesTried.keySet();
		for(int procNumber: procNumbers) {
			processesTried.put(procNumber, false);
		}
	}

	private boolean procExecIsSafe(int procNumber) {
		IntMatrix needRow = need.rowToIntMatrix(procNumber);
		return !end[procNumber] && needRow.isLeqThanMat(work);
	}

	private void recordSafeSequence() {
		fileContent.addLine(safeSeqLine);
	}

	private boolean systemStateIsSafe() throws IllegalArgumentException {
		for(int i=0; i<processCount; i++) {
			end[i] = false;
		}
		work = new IntMatrix(available);

		while(true) {
			int procIndex = -1;
			for(int i=0; i<processCount; i++) {
				if(procExecIsSafe(i)) {
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
