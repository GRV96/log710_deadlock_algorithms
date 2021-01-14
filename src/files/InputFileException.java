package files;

/**
 * This exception should be thrown when a fault in the input file prevents the
 * parsing of data that the algorithm needs.
 * @author Guyllaume Rousseau
 */
public class InputFileException extends Exception {

	private static final long serialVersionUID = 3652202447295283665L;

	private static final String LOCATION_MSG_TEMPLATE = "Fault in matrix ";

	/**
	 * This default constructor creates an instance with no message.
	 */
	public InputFileException() {
		super();
	}

	/**
	 * This constructor creates an instance with a message to explain the
	 * fault in the input file.
	 * @param message - a message that explains the fault
	 */
	public InputFileException(String message) {
		super(message);
	}

	/**
	 * Creates a message that locates the fault in a matrix defined in the
	 * input file.
	 * @param matrixTitle - title of the matrix that contains the fault
	 * @return the location message
	 */
	public static String makeLocationMessage(String matrixTitle) {
		return LOCATION_MSG_TEMPLATE + matrixTitle;
	}

	/**
	 * Creates a message that locates the fault in a matrix defined in the
	 * input file and explains the fault. This method calls
	 * InputFileException.makeLocationMessage.
	 * @param matrixTitle - title of the matrix that contains the fault
	 * @param message - an explanation of the fault
	 * @return the message that locates and explains the fault. If parameter
	 * message is null, the returned message only contains the location
	 * message.
	 */
	public static String makeMessage(String matrixTitle, String message) {
		String exceptionMessage =
				makeLocationMessage(matrixTitle);
		if(message != null) {
			exceptionMessage += ": " + message;
		}
		return exceptionMessage;
	}
}
