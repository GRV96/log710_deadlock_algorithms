import java.io.IOException;

public class DeadlockDetector extends DeadlockAlgorithm {

	private IntMatrix request = null;
	//private null;
	//private null;
	//private null;
	//private null;
	//private null;
	//private null;
	//private null;

	protected DeadlockDetector(String inputPath) throws IOException {
		super(inputPath);

		request = inputReader.getRequestMatrix();

		for(int i=0; i<processCount; i++) {
			end[i] = allocation.rowSum(i) == 0;
		}
	}

	@Override
	public void execute() throws IOException {
		fileContent.addLine(null);
		recordIntMatrix("Available", available);
		fileContent.addLine(null);
		recordArray("End", end);

		int iteration = 1;
		while(true) {
			fileContent.addLine(null, 2);

			int procIndex = -1;
			for(int i=0; i<processCount; i++) {
				if(!end[i] && requestIsLeqThanWork(i)) {
					procIndex = i;
					break;
				}
			}

			if(procIndex > -1) {
				work.addition(allocation.rowToIntMatrix(procIndex));
				end[procIndex] = true;

				writeIterationNumber(iteration);
				writeProcessToExecute(procIndex);
				fileContent.addLine(null);
				recordIntMatrix("Work", work);
				fileContent.addLine(null);
				recordArray("End", end);
			}
			else {
				String procNumbers = "";
				for(int i=0; i<processCount; i++) {
					if(!end[i]) {
						procNumbers += i + " ";
					}
				}
				if(procNumbers.length() > 0) {
					fileContent.addLine("These processes are deadlocked: "
							+ procNumbers);
				}
				else {
					fileContent.addLine("No deadlock occured.");
				}
				break;
			}
			iteration++;
		}

		outputWriter.writeToFile(fileContent);
	}

	public static void main(String[] args) throws Exception {
		DeadlockDetector dd = new DeadlockDetector(args[0]);
		dd.execute();
	}

	private boolean requestIsLeqThanWork(int requestRow) {
		for(int j=0; j<work.columns; j++) {
			if(request.get(requestRow, j) > work.get(0, j)) {
				return false;
			}
		}
		return true;
	}
}
