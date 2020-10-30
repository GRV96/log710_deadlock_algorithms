import java.io.IOException;

public class DeadlockDetector extends DeadlockAlgorithm {

	private IntMatrix request = null;

	protected DeadlockDetector(String inputPath) throws IOException {
		super(inputPath);
		request = inputReader.getRequestMatrix();
		for(int i=0; i<processCount; i++) {
			end[i] = allocation.rowSum(i) == 0;
		}
	}

	@Override
	protected boolean loop() throws Exception {
		boolean keepLooping = true;
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

			recordIterationNumber(iteration);
			recordProcessToExecute(procIndex);
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
			keepLooping = false;
		}
		iteration++;
		return keepLooping;
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
