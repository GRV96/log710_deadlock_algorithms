
public class DeadlockDetection {

	public static void main(String[] args) {
		// Rows: processes
		// Columns: resources allocated to processes

		final int processCount = 3;

		int[][] allocationArray = {
				{0, 0, 1, 0},
				{2, 0, 0, 1},
				{0, 1, 2, 0}};
		IntMatrix allocation = new IntMatrix(allocationArray);

		int[] availableArray = {2, 1, 0, 0};
		IntMatrix available = new IntMatrix(availableArray);
		IntMatrix work = new IntMatrix(available);

		int[][] requestArray = {
				{2, 0, 0, 1},
				//{1, 0, 1, 0},
				{0, 1, 3, 0},
				{2, 1, 0, 0}};
		IntMatrix request = new IntMatrix(requestArray);

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
