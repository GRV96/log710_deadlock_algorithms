public final class FileUtil {

	public static final String FILE_EXTENSION = "txt";
	public static final String RESULT_SUFFIX = "_result";

	public static String addSuffixToPath(String filePath, String suffix) {
		String pathAndName = null;
		String extension = null;
		String newPath = null;
		int dotIndex = filePath.lastIndexOf('.');
		if(dotIndex > 0) {
			pathAndName = filePath.substring(0, dotIndex);
			extension = filePath.substring(dotIndex);
			newPath = pathAndName + suffix + extension;
		}
		else {
			newPath = filePath + suffix;
		}
		return newPath;
	}

	public static String getFileExtension(String filePath) {
		String extension = null;
		int dotIndex = filePath.lastIndexOf('.');
		if(dotIndex >= 0) {
			extension = filePath.substring(dotIndex+1);
		}
		return extension;
	}
}
