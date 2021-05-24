package algorithms;

import java.io.IOException;
import java.util.Scanner;

import files.InputFileException;
import matrix.IntMatrix;

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
	 * All indications of incorrect console input should begin with this
	 * string.
	 */
	private static final String INCORRECT_INPUT_WARNING =
			"Incorrect input! ";

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
	 * This constructor parses the text file designated by inputPath in order
	 * to obtain the data required to evaluate the safety of process
	 * executions. In addition to the data obtained by the superclass'
	 * constructor, it initializes the number of resource types.
	 * @param inputPath - path of the input file
	 * @param recordBankersAlgoData - If true, detailed data from the banker's
	 * algorithm will be recorded in the output file.
	 * @throws InputFileException if the input file contains a fault
	 * @throws IOException if the file designated by inputPath is non-existent
	 * or does not have the extension .txt
	 */
	public RequestEvaluator(String inputPath, boolean recordBankersAlgoData)
			throws InputFileException, IOException {
		// Can throw InputFileException or IOException.
		super(inputPath, REQ_EVAL_SUFFIX);
		this.recordBankersAlgoData = recordBankersAlgoData;
		resourceTypeCount = inputReader.getResourceTypeCount();
		keyboardScanner = new Scanner(System.in);

		/*
		 * A row of matrix Request is filled with user input at each iteration
		 * of method loop. Request does not need to be reset in method
		 * beforeLoop. -1 represents undefined data.
		 */
		request = new IntMatrix(processCount, resourceTypeCount, -1);
	}

	@Override
	protected void afterLoop() {
		System.out.println();
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

		return true;
	}

	@Override
	protected boolean loop() {
		workStates.clear();
		finishStates.clear();

		String line = PROC_REQ_PROMPT;
		System.out.print("\n" + line);
		String procAndReqStr = keyboardScanner.nextLine();
		if(procAndReqStr.toLowerCase().equals("q")) {
			return false;
		}
		fileContent.addLine(null, 2);
		fileContent.addLine(line + procAndReqStr);

		int[] procAndReq = null;
		try {
			procAndReq = makeProcAndReqArray(procAndReqStr);
		}
		catch(Exception e) {
			line = INCORRECT_INPUT_WARNING + e.getMessage();
			System.err.println(line);
			fileContent.addLine(line);
			return true;
		}

		int procNumber = procAndReq[0];
		if(!request.rowIndexIsInBounds(procNumber)) {
			line = INCORRECT_INPUT_WARNING
					+ "Process numbers range from 0 to " + (processCount-1)
					+ ". There is no process " + procNumber + ".";
			System.err.println(line);
			fileContent.addLine(line);
			return true;
		}

		for(int j=0; j<resourceTypeCount; j++) {
			request.set(procNumber, j, procAndReq[j+1]);
		}

		fileContent.addLine(null);

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
			available.subtraction(requestRow);
			allocation.additionOnRow(request, procNumber);
			need.subtractionOnRow(request, procNumber);

			boolean safeState = systemStateIsSafe();

			if(recordBankersAlgoData) {
				fileContent.addLine(null);
			}
			recordIntMatrix(ALLOCATION_TITLE, allocation);
			fileContent.addLine(null);
			recordIntMatrix(AVAILABLE_TITLE, available);
			fileContent.addLine(null);
			recordIntMatrix(NEED_TITLE, need);
			fileContent.addLine(null);
			recordWorkAndFinishStates();
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
	 * Creates an array containing a process' index and the number of resources
	 * of each type requested by that process. Those numbers are obtained from
	 * procAndReqStr, where they are separated by spaces. They must be natural
	 * integers.
	 * @param procAndReqStr - a string entered by the user representing a
	 * process and a resource request
	 * @throws IllegalArgumentException if the number of integers in
	 * procAndReqStr is not the number of resource types + 1
	 * @throws NumberFormatException if Integer.parseUnsignedInt throws one
	 * @return an array containing the process index (index 0) and the
	 * resources that it requests (other indices)
	 */
	private int[] makeProcAndReqArray(String procAndReqStr)
			throws IllegalArgumentException, NumberFormatException {
		String[] strArray = procAndReqStr.split(" ");
		int arrayLength = strArray.length;

		if(arrayLength != resourceTypeCount+1) {
			throw new IllegalArgumentException("1 process number and "
					+ resourceTypeCount
					+ " numbers of resources are expected.");
		}

		int[] procAndReqArray = new int[arrayLength];
		for(int i=0; i<arrayLength; i++) {
			procAndReqArray[i] = Integer.parseUnsignedInt(strArray[i]);
		}

		return procAndReqArray;
	}

	/**
	 * Starts the interactive request evaluation application.
	 * @param args
	 * <p>0: path of the input file
	 * <p>1: a string meaning true or false. If true, detailed data of the
	 * banker's algorithm will be recorded in the output file.
	 */
	public static void main(String[] args) {
		try {
			boolean recordBAData = stringToBoolean(args[1]);
			RequestEvaluator re = new RequestEvaluator(args[0], recordBAData);
			re.execute();
		}
		catch(InputFileException ife) {
			System.err.println(ife.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
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
	 * @throws IllegalArgumentException if an addition is attempted with
	 * matrix Work and a matrix of different dimensions in
	 * DeadlockPreventer.bankersAlgorithmIter.
	 */
	private boolean systemStateIsSafe() throws IllegalArgumentException {
		initArrayFinish();
		work = new IntMatrix(available);
		int safeSeqLength = 0;

		if(recordBankersAlgoData) {
			announceBankersAlgorithm();
		}

		while(true) {
			int procNumber = bankersAlgorithmIter(recordBankersAlgoData);

			saveWorkAndFinishState();

			if(procNumber < 0) {
				return false;
			}
			else {
				safeSeqLength++;
			}

			if(safeSeqLength >= processCount) {
				return true;
			}

			if(recordBankersAlgoData) {
				fileContent.addLine(null);
			}
		}
	}
}
