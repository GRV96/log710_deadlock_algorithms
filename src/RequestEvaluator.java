import java.io.IOException;
import java.util.Scanner;

public class RequestEvaluator extends DeadlockPreventer {

	private int resourceCount = -1;

	private Scanner keyboardScanner;

	public RequestEvaluator(String inputPath) throws IOException {
		super(inputPath);
		resourceCount = inputReader.getResourceCount();
		maximum = inputReader.getMaximumMatrix();
		need = new IntMatrix(maximum);
		need.substraction(allocation);
		keyboardScanner = new Scanner(System.in);

		int[][] reqArray = {{-1, -1, -1},
				{-1, -1, -1},
				{-1, -1, -1},
				{-1, -1, -1},
				{-1, -1, -1}};
		request = new IntMatrix(reqArray);
	}

	@Override
	protected boolean beforeLoop() {
		boolean startLoop = true;
		fileContent.addLine(null);
		recordIntMatrix(NEED_TITLE, need);
		fileContent.addLine(null);

		if(!systemStateIsSafe()) {
			fileContent.addLine("ERROR! The system is in an unsafe state.");
			startLoop = false;
		}

		initAvailableMatrix();

		return startLoop;
	}

	@Override
	protected boolean loop() {
		String line = "Process and request: ";
		System.out.print(line);
		String procAndReqLine = keyboardScanner.nextLine();
		if(procAndReqLine.toLowerCase().equals("q")) {
			return false;
		}
		fileContent.addLine(null, 2);
		fileContent.addLine(line + procAndReqLine);
		fileContent.addLine(null);

		int[] procAndReq = new int[resourceCount+1];
		makeProcAndReqArray(procAndReqLine, procAndReq);
		int procNumber = procAndReq[0];

		for(int j=0; j<resourceCount; j++) {
			request.set(procNumber, j, procAndReq[j+1]);
		}

		IntMatrix requestRow = request.rowToIntMatrix(procNumber);
		if(!request.isLeqToMat(need)) {
			fileContent.addLine("ERROR! Process " + procNumber
					+ " requests more resources than allowed.");
			return false;
		}
		else if(requestRow.isLeqToMat(available)) {
			IntMatrix availableCopy = new IntMatrix(available);
			IntMatrix allocationCopy = new IntMatrix(allocation);
			IntMatrix needCopy = new IntMatrix(need);

			// Simulate allocation
			available.substraction(requestRow);
			allocation.additionOnRow(request, procNumber);
			need.substractionOnRow(request, procNumber);

			recordIntMatrix(NEED_TITLE, need);
			fileContent.addLine(null);
			recordIntMatrix(WORK_TITLE, work);
			fileContent.addLine(null);
			boolean safeState = systemStateIsSafe();
			recordArray(END_TITLE, end);
			fileContent.addLine(null);
			recordIntMatrix(ALLOCATION_TITLE, allocation);
			fileContent.addLine(null);
			recordIntMatrix(AVAILABLE_TITLE, available);
			fileContent.addLine(null);
			recordIntMatrix(NEED_TITLE, need);
			fileContent.addLine(null);
			recordIntMatrix(WORK_TITLE, work);
			fileContent.addLine(null);

			if(safeState) {
				recordProcessToExecute(procNumber);
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
			fileContent.addLine("Process " + procNumber
					+ " requests more resources than available.");
		}

		return true;
	}

	private static void makeProcAndReqArray(String procAndReqStr,
			int[] procAndReqArray) {
		String[] strArray = procAndReqStr.split(" ");
		for(int i=0; i<procAndReqArray.length; i++) {
			procAndReqArray[i] = Integer.parseInt(strArray[i]);
		}
	}

	public static void main(String[] args) throws Exception {
		RequestEvaluator re = new RequestEvaluator(args[0]);
		re.execute();
	}

	private boolean systemStateIsSafe() throws IllegalArgumentException {
		initEndArray();
		work = new IntMatrix(available);
		while(true) {
			int procNumber = safeSequenceIteration();
			if(procNumber < 0) {
				return endArrayIsTrue();
			}
		}
	}
}
