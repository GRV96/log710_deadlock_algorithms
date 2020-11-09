import java.io.IOException;
import java.util.Scanner;

public class RequestEvaluator extends DeadlockPreventer {

	private static final String[] FALSE_STRINGS = {"0", "f", "false", "n", "no"};
	private static final String[] TRUE_STRINGS = {"1", "t", "true", "y", "yes"};

	private static final String PROC_REQ_PROMPT = "Process and request: ";
	private static final String REQ_EVAL_SUFFIX = "_req_eval";

	private boolean recordBankersAlgoData;
	private int resourceCount = -1;

	private Scanner keyboardScanner;

	public RequestEvaluator(String inputPath, boolean recordBankerAlgoData)
			throws IOException {
		super(inputPath, REQ_EVAL_SUFFIX);
		this.recordBankersAlgoData = recordBankerAlgoData;
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
		fileContent.addLine(null);
		recordIntMatrix(NEED_TITLE, need);
		fileContent.addLine(null);

		String line = null;
		if(systemStateIsSafe()) {
			line = "The system's initial state is safe.";
		}
		else {
			line = "WARNING! The system's initial state is unsafe.";
		}

		System.out.println(line);
		if(recordBankersAlgoData) {
			fileContent.addLine(null);
		}
		fileContent.addLine(line);

		initAvailableMatrix();

		return true;
	}

	@Override
	protected boolean loop() {
		String line = PROC_REQ_PROMPT;
		System.out.print("\n" + line);
		String procAndReqStr = keyboardScanner.nextLine();
		if(procAndReqStr.toLowerCase().equals("q")) {
			return false;
		}
		fileContent.addLine(null, 2);
		fileContent.addLine(line + procAndReqStr);
		fileContent.addLine(null);

		int[] procAndReq = new int[resourceCount+1];
		makeProcAndReqArray(procAndReqStr, procAndReq);
		int procNumber = procAndReq[0];

		for(int j=0; j<resourceCount; j++) {
			request.set(procNumber, j, procAndReq[j+1]);
		}

		IntMatrix needRow = need.rowToIntMatrix(procNumber);
		IntMatrix requestRow = request.rowToIntMatrix(procNumber);
		if(!requestRow.isLeqToMat(needRow)) {
			line = "ERROR! Process " + procNumber
					+ " requests more resources than allowed.";
			System.out.println(line);
			fileContent.addLine(line);

			line = recordIntMatrixRow(REQUEST_TITLE, request, procNumber);
			System.out.println(line);

			line = recordIntMatrixRow(NEED_TITLE, need, procNumber);
			System.out.println(line);
			return true;
		}
		else if(requestRow.isLeqToMat(available)) {
			IntMatrix availableCopy = new IntMatrix(available);
			IntMatrix allocationCopy = new IntMatrix(allocation);
			IntMatrix needCopy = new IntMatrix(need);

			// Simulate allocation
			available.substraction(requestRow);
			allocation.additionOnRow(request, procNumber);
			need.substractionOnRow(request, procNumber);

			boolean safeState = systemStateIsSafe();
			if(recordBankersAlgoData) {
				fileContent.addLine(null);
			}
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

				line = "Executing process " + procNumber
						+ " would put the system in an unsafe state.";
				System.out.println(line);
				fileContent.addLine(line);
			}
		}
		else {
			line = "Process " + procNumber
					+ " requests more resources than available.";
			System.out.println(line);
			fileContent.addLine(line);

			line = recordIntMatrixRow(REQUEST_TITLE, request, procNumber);
			System.out.println(line);
			line = recordIntMatrixRow(AVAILABLE_TITLE, available, 0);
			System.out.println(line);
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
		RequestEvaluator re = new RequestEvaluator(args[0],
				stringToBoolean(args[1]));
		re.execute();
	}

	private static boolean stringToBoolean(String str)
			throws IllegalArgumentException {
		String lwStr = str.toLowerCase();

		for(int i=0; i<FALSE_STRINGS.length; i++) {
			if(FALSE_STRINGS[i].equals(lwStr)) {
				return false;
			}
		}

		for(int i=0; i<TRUE_STRINGS.length; i++) {
			if(TRUE_STRINGS[i].equals(lwStr)) {
				return true;
			}
		}

		throw new IllegalArgumentException(str
				+ " does not match a boolean value.");
	}

	private boolean systemStateIsSafe() throws IllegalArgumentException {
		initEndArray();
		work = new IntMatrix(available);
		if(recordBankersAlgoData) {
			announceBankersAlgorithm();
		}
		while(true) {
			int procNumber = bankersAlgorithmIter(recordBankersAlgoData);
			if(procNumber < 0) {
				return endArrayIsTrue();
			}
		}
	}
}
