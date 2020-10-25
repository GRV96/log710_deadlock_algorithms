public class IntMatrix {

	public final int rows;
	public final int columns;
	private int[][] matrix;

	public IntMatrix(int[] content) throws IllegalArgumentException {
		if(content.length == 0) {
			throw new IllegalArgumentException();
		}
		rows = 1;
		columns = content.length;
		matrix = new int[rows][columns];
		copyContent(matrix[0], content);
	}

	public IntMatrix(int[][] content) throws IllegalArgumentException {
		if(!rowLengthIsConstant(content)) {
			throw new IllegalArgumentException();
		}
		rows = content.length;
		columns = content[0].length;
		matrix = new int[rows][columns];
		copyContent(matrix, content);
	}

	public IntMatrix(IntMatrix other) {
		this.rows = other.rows;
		this.columns = other.columns;
		this.matrix = new int[rows][columns];
		copyContent(this, other);
	}

	public void addition(IntMatrix other) throws IllegalArgumentException {
		if(!dimensionsAreEqual(other)) {
			throw new IllegalArgumentException();
		}
		for(int i=0; i<rows; i++) {
			for(int j=0; j<columns; j++) {
				matrix[i][j] += other.matrix[i][j];
			}
		}
	}

	public int columnSum(int column)throws IllegalArgumentException {
		if(column<0 || columns<column) {
			throw new IllegalArgumentException();
		}
		int sum = 0;
		for(int i=0; i<rows; i++) {
			sum += matrix[i][column];
		}
		return sum;
	}

	private static void copyContent(int[] destination, int[] source) {
		for(int i=0; i<source.length; i++) {
			destination[i] = source[i];
		}
	}

	private static void copyContent(int[][] destination, int[][] source) {
		for(int i=0; i<source.length; i++) {
			copyContent(destination[i], source[i]);
		}
	}

	private static void copyContent(IntMatrix destination, IntMatrix source) {
		copyContent(destination.matrix, source.matrix);
	}

	public boolean dimensionsAreEqual(IntMatrix other) {
		return rows == other.rows && columns == other.columns;
	}

	public int get(int row, int column)
			throws ArrayIndexOutOfBoundsException {
		return matrix[row][column];
	}

	public IntMatrix getOpposite() {
		IntMatrix opposite = new IntMatrix(this);
		for(int i=0; i<rows; i++) {
			for(int j=0; j<columns; j++) {
				opposite.matrix[i][j] *= -1;
			}
		}
		return opposite;
	}

	public static boolean rowLengthIsConstant(int[][] intArray2d) {
		final int rowCount = intArray2d.length;
		if(rowCount == 0) {
			return false;
		}
		else if(rowCount == 1) {
			return true;
		}
		final int rowLength = intArray2d[0].length;
		for(int i=1; i<rowCount; i++) {
			if(intArray2d[i].length != rowLength) {
				return false;
			}
		}
		return true;
	}

	public IntMatrix rowToIntMatrix(int row) {
		if(row<0 || rows<=row) {
			return null;
		}
		return new IntMatrix(matrix[row]);
	}

	public int rowSum(int row) throws IllegalArgumentException {
		if(row<0 || rows<row) {
			throw new IllegalArgumentException();
		}
		int sum = 0;
		for(int j=0; j<columns; j++) {
			sum += matrix[row][j];
		}
		return sum;
	}

	public void set(int row, int column, int value)
			throws ArrayIndexOutOfBoundsException {
		matrix[row][column] = value;
	}

	public void substraction(IntMatrix other) throws IllegalArgumentException {
		addition(other.getOpposite());
	}
}
