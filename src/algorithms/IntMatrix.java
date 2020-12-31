package algorithms;

/**
 * This class represents 2-dimensional matrices containing integral numbers.
 * @author Guyllaume Rousseau
 */
public class IntMatrix {

	public final int rows;
	public final int columns;
	private int[][] matrix;

	/**
	 * This constructor creates a matrix with the specified number of rows and
	 * columns. All its cells are are set to the value of parameter content.
	 * @param rows - this matrix's number of rows
	 * @param columns - this matrix's number of columns
	 * @param content - the number in all this matrix's cells
	 * @throws IllegalArgumentException if the number of rows or columns is 0
	 * or less
	 */
	public IntMatrix(int rows, int columns, int content)
			throws IllegalArgumentException {
		if(rows <= 0) {
			throw new IllegalArgumentException(
					"The number of rows must be greater than 0.");
		}
		if(columns <= 0) {
			throw new IllegalArgumentException(
					"The number of columns must be greater than 0.");
		}

		this.rows = rows;
		this.columns = columns;

		matrix = new int[rows][columns];
		for(int i=0; i<rows; i++) {
			for(int j=0; j<columns; j++) {
				matrix[i][j] = content;
			}
		}
	}

	/**
	 * This constructor creates a row matrix by copying the content of a
	 * 1-dimensional int array.
	 * @param content - a 1-dimensional int array
	 * @throws IllegalArgumentException if content has 0 element
	 */
	public IntMatrix(int[] content) throws IllegalArgumentException {
		if(content.length == 0) {
			throw new IllegalArgumentException("Row length is not constant.");
		}
		rows = 1;
		columns = content.length;
		matrix = new int[rows][columns];
		copyContent(matrix[0], content);
	}

	/**
	 * This constructor creates a matrix by copying the content of a
	 * 2-dimensional int array.
	 * @param content - a 2-dimensional int array
	 * @throws IllegalArgumentException
	 * if IntMatrix.rowLengthIsConstant(content) returns false
	 */
	public IntMatrix(int[][] content) throws IllegalArgumentException {
		if(!rowLengthIsConstant(content)) {
			throw new IllegalArgumentException("Row length is not constant.");
		}
		rows = content.length;
		columns = content[0].length;
		matrix = new int[rows][columns];
		copyContent(matrix, content);
	}

	/**
	 * This constructor creates a matrix by copying the content of another
	 * instance of IntMatrix.
	 * @param other - another instance of IntMatrix
	 */
	public IntMatrix(IntMatrix other) {
		this.rows = other.rows;
		this.columns = other.columns;
		this.matrix = new int[rows][columns];
		copyContent(this, other);
	}

	/**
	 * Adds the number in every cell of other to the number at the same
	 * coordinates in this matrix. The sums are recorded in this matrix.
	 * @param other - another instance of IntMatrix
	 * @throws IllegalArgumentException if dimensionsAreEqual(other) returns
	 * false
	 */
	public void addition(IntMatrix other) throws IllegalArgumentException {
		if(!dimensionsAreEqual(other)) {
			throw new IllegalArgumentException(
					"The matrices have different dimensions.");
		}
		for(int i=0; i<rows; i++) {
			for(int j=0; j<columns; j++) {
				matrix[i][j] += other.matrix[i][j];
			}
		}
	}

	/**
	 * Adds the number in every cell in the specified row of other to the
	 * number at the same coordinates in this matrix. The sums are recorded in
	 * this matrix.
	 * @param other - another instance of IntMatrix
	 * @param row - the index of the row in which the addition is to be
	 * performed.
	 * @throws IllegalArgumentException if dimensionsAreEqual(other) returns
	 * false
	 */
	public void additionOnRow(IntMatrix other, int row)
			throws IllegalArgumentException {
		if(!dimensionsAreEqual(other)) {
			throw new IllegalArgumentException(
					"The matrices have different dimensions.");
		}
		for(int j=0; j<columns; j++) {
			matrix[row][j] += other.matrix[row][j];
		}
	}

	/**
	 * Calculates the sum of the numbers in the specified column.
	 * @param column - a column index
	 * @return the sum of the numbers in the column
	 * @throws IllegalArgumentException if this matrix does not have a column
	 * matching the given column index
	 */
	public int columnSum(int column) throws IllegalArgumentException {
		if(column<0 || columns<column) {
			throw new IllegalArgumentException("This matrix does not have column "
					+ column + ".");
		}
		int sum = 0;
		for(int i=0; i<rows; i++) {
			sum += matrix[i][column];
		}
		return sum;
	}

	/**
	 * Creates a row matrix containing the sum of the numbers in each column
	 * of this matrix.
	 * @return a new IntMatrix instance containing the column sums of this
	 * matrix
	 * @throws IllegalArgumentException if columnSum throws one
	 */
	public IntMatrix columnSumMatrix()
			throws IllegalArgumentException {
		int[] sumArray = new int[columns];
		for(int j=0; j<columns; j++) {
			sumArray[j] = columnSum(j);
		}
		return new IntMatrix(sumArray);
	}

	/**
	 * Copies the content of a 1-dimensional int array into another
	 * 1-dimensional int array.
	 * @param destination - the array in which the copy is performed
	 * @param source - the copied array
	 */
	private static void copyContent(int[] destination, int[] source) {
		for(int i=0; i<source.length; i++) {
			destination[i] = source[i];
		}
	}

	/**
	 * Copies the content of a 2-dimensional int array into another
	 * 2-dimensional int array.
	 * @param destination - the array in which the copy is performed
	 * @param source - the copied array
	 */
	private static void copyContent(int[][] destination, int[][] source) {
		for(int i=0; i<source.length; i++) {
			copyContent(destination[i], source[i]);
		}
	}

	/**
	 * Copies the content of a matrix into another matrix.
	 * @param destination - the matrix in which the copy is performed
	 * @param source - the copied matrix
	 */
	private static void copyContent(IntMatrix destination, IntMatrix source) {
		copyContent(destination.matrix, source.matrix);
	}

	/**
	 * Determines whether this matrix and other have the same dimensions.
	 * @param other - another instance of IntMatrix
	 * @return true if this and other have the same dimensions, false otherwise
	 */
	public boolean dimensionsAreEqual(IntMatrix other) {
		return rows == other.rows && columns == other.columns;
	}

	/**
	 * Accesses the number at the given coordinates in this matrix.
	 * @param row - a row index
	 * @param column - a column index
	 * @return the number at the given coordinates
	 * @throws ArrayIndexOutOfBoundsException if row or column is out of bounds
	 */
	public int get(int row, int column)
			throws ArrayIndexOutOfBoundsException {
		return matrix[row][column];
	}

	/**
	 * Creates an IntMatrix instance whose each value is the opposite of this
	 * matrix's value at the same coordinates.
	 * @return the IntMatrix containing the opposites
	 */
	public IntMatrix getOpposite() {
		IntMatrix opposite = new IntMatrix(this);
		for(int i=0; i<rows; i++) {
			for(int j=0; j<columns; j++) {
				opposite.matrix[i][j] *= -1;
			}
		}
		return opposite;
	}

	/**
	 * Determines whether every number in this matrix is lesser or equal to
	 * the number in other at the same coordinates.
	 * @param other - another instance of IntMatrix
	 * @return true if every number in this matrix is lesser or equal to the
	 * corresponding number in other, false otherwise
	 * @throws IllegalArgumentException if dimensionsAreEqual(other) returns
	 * false
	 */
	public boolean isLeqToMat(IntMatrix other)
			throws IllegalArgumentException {
		if(!dimensionsAreEqual(other)) {
			throw new IllegalArgumentException(
					"The matrices have different dimensions.");
		}
		for(int i=0; i<rows; i++) {
			for(int j=0; j<columns; j++) {
				if(get(i, j) > other.get(i, j)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Determines whether a 2-dimensional array's rows all have the same
	 * length. The first index represents rows; the second represents columns.
	 * @param intArray2d - a 2-dimensional array
	 * @return false if intArray2d has no row or all its rows do not have the
	 * same length; true if intArray2d has one row or all its rows have the same
	 * length
	 */
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

	/**
	 * Creates an array containing a copy of the specified row of this matrix.
	 * @param row - a row index
	 * @return the content of the specified row in an array
	 */
	public Integer[] rowToArray(int row) {
		Integer[] rowArray = new Integer[columns];
		for(int j=0; j<columns; j++) {
			rowArray[j] = matrix[row][j];
		}
		return rowArray;
	}

	/**
	 * Creates a row matrix containing the numbers in the specified row of
	 * this matrix.
	 * @param row - a row index
	 * @return a new matrix containing the specified row
	 */
	public IntMatrix rowToIntMatrix(int row) {
		if(row<0 || rows<=row) {
			return null;
		}
		return new IntMatrix(matrix[row]);
	}

	/**
	 * Creates a string containing all the numbers in the specified row of
	 * this matrix. The numbers are separated in the string by the given
	 * separator.
	 * @param row - a row index
	 * @param separator - a sequence of characters to separate the numbers
	 * @return a string containing the numbers in the given row
	 */
	public String rowToString(int row, String separator) {
		if(row<0 || rows<=row) {
			return null;
		}

		String rowStr = "";
		for(int j=0; j<columns; j++) {
			rowStr += matrix[row][j] + separator;
		}

		return rowStr;
	}

	/**
	 * Calculates the sum of the numbers in the specified row.
	 * @param row - a row index
	 * @return the sum of the numbers in the row
	 * @throws IllegalArgumentException if this matrix does not have a row
	 * matching the given row index
	 */
	public int rowSum(int row) throws IllegalArgumentException {
		if(row<0 || rows<row) {
			throw new IllegalArgumentException("This matrix does not have row "
					+ row + ".");
		}
		int sum = 0;
		for(int j=0; j<columns; j++) {
			sum += matrix[row][j];
		}
		return sum;
	}

	/**
	 * Sets the number a the given coordinates in this matrix.
	 * @param row - a row index
	 * @param column - a column index
	 * @param number - the number to put at the given coordinates.
	 * @throws ArrayIndexOutOfBoundsException if row or column is out of bounds
	 */
	public void set(int row, int column, int number)
			throws ArrayIndexOutOfBoundsException {
		matrix[row][column] = number;
	}

	/**
	 * Substracts the number in every cell of other from the number at the
	 * same coordinates in this matrix. The differences are recorded in this
	 * matrix.
	 * @param other - another instance of IntMatrix
	 * @throws IllegalArgumentException if dimensionsAreEqual(other) returns
	 * false
	 */
	public void substraction(IntMatrix other) throws IllegalArgumentException {
		addition(other.getOpposite());
	}

	/**
	 * Substracts the number in every cell in the specified row of other from
	 * the number at the same coordinates in this matrix. The differences are
	 * recorded in this matrix.
	 * @param other - another instance of IntMatrix
	 * @param row - the index of the row in which the substraction is to be
	 * performed.
	 * @throws IllegalArgumentException if dimensionsAreEqual(other) returns
	 * false
	 */
	public void substractionOnRow(IntMatrix other, int row)
			throws IllegalArgumentException {
		additionOnRow(other.getOpposite(), row);
	}
}
