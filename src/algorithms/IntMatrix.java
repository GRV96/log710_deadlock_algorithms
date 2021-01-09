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
			throw new IllegalArgumentException("Content's length is 0.");
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
		exceptionForVaryingRowLength(content);

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
		exceptionForDifferentDimensions(other);

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
	 * performed
	 * @throws IllegalArgumentException if row is out of bounds or
	 * dimensionsAreEqual(other) returns false
	 */
	public void additionOnRow(IntMatrix other, int row)
			throws IllegalArgumentException {
		exceptionForIllegalRowIndex(row);
		exceptionForDifferentDimensions(other);

		for(int j=0; j<columns; j++) {
			matrix[row][j] += other.matrix[row][j];
		}
	}

	/**
	 * Indicates whether the given index matches a column of this matrix.
	 * @param columnIndex - the index of a column
	 * @return true if columnIndex ranges from 0 to columns-1, false otherwise
	 */
	public boolean columnIndexIsInBounds(int columnIndex) {
		return 0 <= columnIndex && columnIndex < columns;
	}

	/**
	 * Calculates the sum of the numbers in the specified column.
	 * @param column - a column index
	 * @return the sum of the numbers in the column
	 * @throws IllegalArgumentException if column is out of bounds
	 */
	public int columnSum(int column) throws IllegalArgumentException {
		exceptionForIllegalColumnIndex(column);

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
	 * @throws IllegalArgumentException if destination and source do not have
	 * the same dimensions as destination
	 */
	private static void copyContent(IntMatrix destination, IntMatrix source)
			throws IllegalArgumentException {
		destination.exceptionForDifferentDimensions(source);
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
	 * Throws an IllegalArgumentException if the other matrix does not have
	 * the same dimensions as this one.
	 * @param other - another instance of IntMatrix
	 * @throws IllegalArgumentException if other does not have the same
	 * dimensions as this
	 */
	private void exceptionForDifferentDimensions(IntMatrix other)
			throws IllegalArgumentException {
		if(!dimensionsAreEqual(other)) {
			throw new IllegalArgumentException(
					"The matrices must have the same dimensions. This: " + rows
					+ "×" + columns + "; other: " + other.rows + "×"
					+ other.columns + ".");
		}
	}

	/**
	 * Throws an IllegalArgumentException if the given column index is out of
	 * bounds.
	 * @param columnIndex - a column index
	 * @throws IllegalArgumentException if columnIndex is out of bounds
	 */
	private void exceptionForIllegalColumnIndex(int columnIndex)
			throws IllegalArgumentException {
		if(!columnIndexIsInBounds(columnIndex)) {
			throw new IllegalArgumentException(
					"Column indices range from 0 to " + (columns-1)
					+ ". Index " + columnIndex + "is out of bounds.");
		}
	}

	/**
	 * Throws an IllegalArgumentException if the given row index is out of
	 * bounds.
	 * @param rowIndex - a row index
	 * @throws IllegalArgumentException if rowIndex is out of bounds
	 */
	private void exceptionForIllegalRowIndex(int rowIndex)
			throws IllegalArgumentException {
		if(!rowIndexIsInBounds(rowIndex)) {
			throw new IllegalArgumentException(
					"Row indices range from 0 to "+ (rows-1)
					+ ". Index " + rowIndex + " is out of bounds.");
		}
	}

	/**
	 * Throws an IllegalArgumentException if the rows of intArray2d do not
	 * have the same length.
	 * @param intArray2d - a 2-dimensional integer array
	 * @throws IllegalArgumentException if IntMatrix.rowLengthIsConstant
	 * returns false
	 */
	private static void exceptionForVaryingRowLength(int[][] intArray2d)
			throws IllegalArgumentException {
		if(!rowLengthIsConstant(intArray2d)) {
			throw new IllegalArgumentException("Row length is not constant.");
		}
	}

	/**
	 * Accesses the number at the given coordinates in this matrix.
	 * @param row - a row index
	 * @param column - a column index
	 * @return the number at the given coordinates
	 * @throws IllegalArgumentException if row or column is out of bounds
	 */
	public int get(int row, int column)
			throws IllegalArgumentException {
		exceptionForIllegalRowIndex(row);
		exceptionForIllegalColumnIndex(column);
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
	 * Determines whether every number in this matrix is less than or equal to
	 * the number in other at the same coordinates.
	 * @param other - another instance of IntMatrix
	 * @return true if every number in this matrix is less than or equal to the
	 * corresponding number in other, false otherwise
	 * @throws IllegalArgumentException if dimensionsAreEqual(other) returns
	 * false
	 */
	public boolean isLeqToMat(IntMatrix other)
			throws IllegalArgumentException {
		exceptionForDifferentDimensions(other);

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
	 * Indicates whether the given index matches a row of this matrix.
	 * @param rowIndex - the index of a row
	 * @return true if rowIndex ranges from 0 to rows-1, false otherwise
	 */
	public boolean rowIndexIsInBounds(int rowIndex) {
		return 0 <= rowIndex && rowIndex < rows;
	}

	/**
	 * Determines whether a 2-dimensional integer array's rows all have the
	 * same length. The first index represents rows; the second represents
	 * columns.
	 * @param intArray2d - a 2-dimensional integer array
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
	 * @throws IllegalArgumentException if row is out of bounds
	 */
	public Integer[] rowToArray(int row) {
		exceptionForIllegalRowIndex(row);

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
	 * @throws IllegalArgumentException if row is out of bounds
	 */
	public IntMatrix rowToIntMatrix(int row) {
		exceptionForIllegalRowIndex(row);
		return new IntMatrix(matrix[row]);
	}

	/**
	 * Creates a string containing all the numbers in the specified row of
	 * this matrix. The numbers are separated in the string by the given
	 * separator.
	 * @param row - a row index
	 * @param separator - a sequence of characters to separate the numbers
	 * @return a string containing the numbers in the given row
	 * @throws IllegalArgumentException if row is out of bounds
	 */
	public String rowToString(int row, String separator)
			throws IllegalArgumentException {
		exceptionForIllegalRowIndex(row);

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
	 * @throws IllegalArgumentException if row is out of bounds
	 */
	public int rowSum(int row) throws IllegalArgumentException {
		exceptionForIllegalRowIndex(row);

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
	 * @throws IllegalArgumentException if row or column is out of bounds
	 */
	public void set(int row, int column, int number)
			throws IllegalArgumentException {
		exceptionForIllegalRowIndex(row);
		exceptionForIllegalColumnIndex(column);
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
