public class DeadlockDetection {

	public static void main(String[] args) throws Exception {
		// Rows: processes
		// Columns: resources allocated to processes

		InputFileReader ifr = new InputFileReader(args[0]);
		int processCount = ifr.getProcessCount();

		IntMatrix allocation = ifr.getAllocationMatrix();

		IntMatrix available = ifr.getResourceMatrix();
		for(int j=0; j<available.columns; j++) {
			available.set(0, j, available.get(0, j)-allocation.columnSum(j));
		}
		IntMatrix work = new IntMatrix(available);

		IntMatrix request = ifr.getRequestMatrix();

		boolean[] end = new boolean[processCount];
		for(int i=0; i<processCount; i++) {
			end[i] = allocation.rowSum(i) == 0;
		}

		while(true) {
			int procIndex = -1;
			for(int i=0; i<processCount; i++) {
				if(!end[i] && requestLeqWork(request.rowToIntMatrix(i), work)) {
					procIndex = i;
					break;
				}
			}

			if(procIndex > -1) {
				work.addition(allocation.rowToIntMatrix(procIndex));
				end[procIndex] = true;
			}
			else {
				System.out.print("These processes are deadlocked: ");
				String procNumbers = "";
				for(int i=0; i<processCount; i++) {
					if(!end[i]) {
						procNumbers += i + " ";
					}
				}
				System.out.println(procNumbers);
				break;
			}
		}
	}

	private static boolean requestLeqWork(IntMatrix requestRow, IntMatrix work) {
		for(int j=0; j<work.columns; j++) {
			if(requestRow.get(0, j) > work.get(0, j)) {
				return false;
			}
		}
		return true;
	}
}
