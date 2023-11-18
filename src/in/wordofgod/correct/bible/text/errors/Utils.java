/**
 * 
 */
package in.wordofgod.correct.bible.text.errors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */
public class Utils {

	private static List<String> tamilVowels = Arrays
			.asList(new String[] { "்", "ா", "ி", "ீ", "ு", "ூ", "ெ", "ே", "ை", "ொ", "ோ", "ௌ" });

	public static String trimDictionaryWord(String word) {
		return word.trim().strip().trim();
	}

	static void initSystemOutSettings() {
		if (CorrectBibleTextErrors.WRITE_LOGS_TO_FILE) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH mm ss");
			String timeStamp = dateFormat.format(new Date());
			timeStamp = timeStamp.replaceAll(" ", "-");
			File outputFolder = new File(
					CorrectBibleTextErrors.bibleSourcePath + "/" + CorrectBibleTextErrors.outputFolderName);
			outputFolder.mkdirs();
			File outputFile = new File(
					CorrectBibleTextErrors.bibleSourcePath + "/" + CorrectBibleTextErrors.outputFolderName + "/"
							+ CorrectBibleTextErrors.outputFolderName + "-logs-" + timeStamp + ".txt");
			System.out.println("Logs of this program is stored at :: " + outputFile.getAbsolutePath());
			try {
				System.setOut(new PrintStream(outputFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createFile(String text, String fileName) {
		String filePath = CorrectBibleTextErrors.bibleSourcePath + "/" + CorrectBibleTextErrors.outputFolderName + "/"
				+ fileName;
		try {
			Files.writeString(Path.of(filePath), text);
			System.out.println("Created the file: " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String removeHTMLTags(String text) {
		while (text.contains("<")) {
			int startPos = text.indexOf("<");
			int endPos = text.indexOf(">");
			String htmlTag = text.substring(startPos, endPos + 1);
			text = text.replace(htmlTag, " ");
		}
		// remove new extra spaces introduced
		return removeExtraSpaces(text);
	}

	public static boolean deleteOutputFileIfAlreadyExists() {
		File outputFolder = new File(
				CorrectBibleTextErrors.bibleSourcePath + "/" + CorrectBibleTextErrors.outputFolderName);
		return outputFolder.delete();
	}

	public static String removeExtraSpaces(String text) {
		text = text.trim();
		for (int i = 1; i <= 5; i++) {
			text = text.replace("  ", " ");
		}
		return text;
	}

	public static String removeSpecialCharactors(String text) {
		for (char ch : CorrectBibleTextErrors.removeSpecialCharactors.toCharArray()) {
			text = text.replace(ch + "", " ");
		}
		// remove new extra spaces introduced
		return removeExtraSpaces(text);
	}

	public static String findAllSpecialCharacters(String text, String reference) {
		String temp = "";
		Pattern p = Pattern.compile("[^?0-9\\., :;\\?\\!\\(\\)\\-\\'\\\"\\“\\”\\[\\]\\<\\>\\/\\p{L}]");
		Matcher m = p.matcher(text);
		while (m.find()) {
			if (!tamilVowels.contains(m.group(0))) {
				temp = temp + m.group(0);
			}
		}
		if (temp != null && temp != "") {
			System.out.println("\n" + reference + " :: " + text);
			System.out.println("Special Characters :: " + temp);
		}
		return temp;
	}

}
