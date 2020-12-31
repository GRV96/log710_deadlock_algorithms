package algorithms;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This interactive class determines whether requests entered by the user in
 * the console would put the system in an unsafe state, i.e. a state in which
 * the processes would risk to be deadlocked. A user input is a sequence of
 * numbers separated by spaces. The first number is the process index; the
 * other ones represent the requested quantity of each resource.
 * @author Guyllaume Rousseau
 */
public class RequestEvaluator extends DeadlockPreventer {

	/**
	 * An array of strings associated with the boolean value false
	 */
	private static final String[] FALSE_STRINGS =
		{"0", "f", "false", "n", "no"};

	/**
	 * An array of strings associated with the boolean value true
	 */
	private static final String[] TRUE_STRINGS =
		{"1", "t", "true", "y", "yes"};

	/**
	 * Console prompt for the process and the requested resources
	 */
	private static final String PROC_REQ_PROMPT =
			"Process and request (\"q\" to quit): ";

	/**
	 * A suffix appended to the input file's name to form the output file's
	 * name
	 */
	private static final String REQ_EVAL_SUFFIX = "_req_eval";

	/**
	 * If true, detailed data from the banker's algorithm will be recorded in
	 * the output file.
	 */
	private boolean recordBankersAlgoData;

	/**
	 * The number of resource types that can be requested
	 */
	private int resourceTypeCount = -1;

	/**
	 * A scanner to obtain the user's input in the console
	 */
	private Scanner keyboardScanner;

	/**
	 * This constructor initializes the data needed to evaluate the safety of
	 * process executions.
	 * @param inputPath - path of the input file
	 * @param recordBankersAlgoData - If true, detailed data from the banker's
	 * algorithm will be recorded in the output file.
	 * @throws IOException if the file designated by inputPath is non-existent
	 * or does not have the extension .txt
	 */
	public RequestEvaluator(String inputPath, boolean recordBankersAlgoData)
			throws IOException {
		super(inputPath, REQ_EVAL_SUFFIX);
		this.recordBankersAlgoData = recordBankersAlgoData;
		resourceTypeCount = inputReader.getResourceTypeCount();
		keyboardScanner = new Scanner(System.in);
		request = new IntMatrix(processCount, resourceTypeCount, -1);
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
		workRecord.clear();
		endRecord.clear();

		String line = PROC_REQ_PROMPT;
		System.out.print("\n" + line);
		String procAndReqStr = keyboardScanner.nextLine();
		if(procAndReqStr.toLowerCase().equals("q")) {
			return false;
		}
		fileContent.addLine(null, 2);
		fileContent.addLine(line + procAndReqStr);
		fileContent.addLine(null);

		int[] procAndReq = new int[resourceTypeCount+1];
		makeProcAndReqArray(procAndReqStr, procAndReq);
		int procNumber = procAndReq[0];

		for(int j=0; j<resourceTypeCount; j++) {
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

			recordIntMatrix(ALLOCATION_TITLE, allocation);
			fileContent.addLine(null);
			recordIntMatrix(AVAILABLE_TITLE, available);
			fileContent.addLine(null);
			recordIntMatrix(NEED_TITLE, need);
			fileContent.addLine(null);
			recordArrayStates(WORK_TITLE, workRecord);
			fileContent.addLine(null);
			recordArrayStates(END_TITLE, endRecord);
			fileContent.addLine(null);

			boolean execProc = false;
			if(safeState) {
				line = "Process " + procNumber + " can be executed.";
				System.out.println(line);
				fileContent.addLine(line);

				line = "Do you want to execute it? ";
				System.out.print(line);
				String execChoice = keyboardScanner.nextLine();
				fileContent.addLine(line + execChoice);
				try {
					execProc = stringToBoolean(execChoice);
				}
				catch(IllegalArgumentException iae) {
					line = iae.getMessage();
					System.out.println(line);
					fileContent.addLine(line);
					line = "The process will not be executed.";
					System.out.println(line);
					fileContent.addLine(line);
				}
			}
			else {
				line = "Executing process " + procNumber
						+ " would put the system in an unsafe state.";
				System.out.println(line);
				fileContent.addLine(line);
			}
			if(!execProc) { // execProc is false if safeState is false.
				// Cancel allocation
				available = availableCopy;
				allocation = allocationCopy;
				need = needCopy;
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

	/**
	 * Fills an array containing the process index and the number of resources
	 * of each type that it requests.
	 * @param procAndReqStr - the string entered by the user representing a
	 * process and a resource request
	 * @param procAndReqArray - the array that will contain the process index
	 * (index 0) and the resources it requests (other indices)
	 */
	private static void makeProcAndReqArray(String procAndReqStr,
			int[] procAndReqArray) {
		String[] strArray = procAndReqStr.split(" ");
		for(int i=0; i<procAndReqArray.length; i++) {
			procAndReqArray[i] = Integer.parseInt(strArray[i]);
		}
	}

	/**
	 * Starts the interactive request evaluation application.
	 * @param args
	 * <p>0: path of the input file
	 * <p>1: a string meaning true or false. If true, detailed data of the
	 * banker's algorithm will be recorded in the output file.
	 * @throws Exception if the RequestEvaluator constructor or
	 * DeadlockAlgorithm.execute throws one
	 */
	public static void main(String[] args) throws Exception {
		boolean recordBAData = stringToBoolean(args[1]);
		RequestEvaluator re = new RequestEvaluator(args[0], recordBAData);
		re.execute();
	}

	/**
	 * Determines a boolean value corresponding to the given string.
	 * @param str - a string that can be associated with the value true or
	 * false
	 * @return the boolean value matching str
	 * @throws IllegalArgumentException if no boolean value matches str
	 */
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

		throw new IllegalArgumentException("\"" + str
				+ "\" does not match a boolean value.");
	}

	/**
	 * This method runs the banker's algorithm to determine whether the system's
	 * current state is safe.
	 * @return true if the system's state is safe, false otherwise
	 * @throws IllegalArgumentException if an addition is attempted with the
	 * work matrix and a matrix of different dimensions in
	 * DeadlockPreventer.bankersAlgorithmIter.
	 */
	private boolean systemStateIsSafe() throws IllegalArgumentException {
		initEndArray();
		int safeSeqLength = 0;
		work = new IntMatrix(available);
		if(recordBankersAlgoData) {
			announceBankersAlgorithm();
		}
		while(true) {
			int procNumber = bankersAlgorithmIter(recordBankersAlgoData);
			workRecord.add(work.rowToArray(0));
			endRecord.add(Arrays.copyOf(end, end.length));

			if(procNumber < 0) {
				return false;
			}
			else {
				safeSeqLength++;
			}
			if(safeSeqLength >= processCount) {
				return true;
			}
		}
	}
}
