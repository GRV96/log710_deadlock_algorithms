public final class FileUtil {

	public static final String FILE_EXTENSION = "txt";
	public static final char PERIOD = '.';

	/**
	 * Inserts a suffix in a filepath between the filename and the '.'
	 * character preceding the extension. If the filepath does not have an
	 * extension, the suffix is simply appended to the filepath.
	 * @param filepath - the filepath to which a suffix must be added
	 * @param suffix - the string to be added to filepath
	 * @return a new filepath with suffix appended to the filename
	 */
	public static String addSuffixToPath(String filepath, String suffix) {
		String pathAndName = null;
		String extension = null;
		String newPath = null;
		int dotIndex = filepath.lastIndexOf(PERIOD);
		if(dotIndex > 0) {
			pathAndName = filepath.substring(0, dotIndex);
			extension = filepath.substring(dotIndex);
			newPath = pathAndName + suffix + extension;
		}
		else {
			newPath = filepath + suffix;
		}
		return newPath;
	}

	/**
	 * Returns the extension of the given filepath. The extension is defined
	 * as the substring ranging from the last occurrence of '.' (exclusive) to
	 * the end of the filepath.
	 * @param filepath - a filepath
	 * @return the extension of filepath or null if it does not have one
	 */
	public static String getFileExtension(String filepath) {
		String extension = null;
		int dotIndex = filepath.lastIndexOf(PERIOD);
		if(dotIndex >= 0) {
			extension = filepath.substring(dotIndex+1);
		}
		return extension;
	}
}
