/**
 * 
 */
package in.wordofgod.correct.bible.text.errors;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import in.wordofgod.bible.parser.Bible;
import in.wordofgod.bible.parser.TheWord;
import in.wordofgod.bible.parser.vosgson.Book;
import in.wordofgod.bible.parser.vosgson.Chapter;
import in.wordofgod.bible.parser.vosgson.Verse;

/**
 * 
 */
public class CorrectBibleTextErrors {

	public static final boolean WRITE_LOGS_TO_FILE = true;
	public static String bibleSourcePath, removeSpecialCharactors;
	public static Set<String> allSpecialCharacters = new HashSet<String>();
	public static Set<String> allSpecialCharactersPerVersion = new HashSet<String>();
	public static String outputFolderName = "results";
	public static String[] bibleVersions;
	public static boolean removeExtraSpace, removeHtmlTags, removeHtmlTagsWithEnclosingContent,
			findAllSpecialCharacters;
	public static int removeExtraSpaceCount = 0, removeHtmlTagsCount = 0, removeHtmlTagsWithEnclosingContentCount = 0,
			removeSpecialCharactorsCount = 0;
	public static int extraSpaceCount = 0, htmlTagsCount = 0, htmlTagsWithEnclosingContentCount = 0,
			specialCharactorsCount = 0;
	public static StringBuilder sb = new StringBuilder();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException {

		if (!validInput(args)) {
			return;
		}

		Utils.initSystemOutSettings();

		correctErrors();
	}

	private static void correctErrors() {
		System.out.println("Correct Errors started with the below parameters:");
		System.out.println("findAllSpecialCharacters=" + CorrectBibleTextErrors.findAllSpecialCharacters);
		System.out.println("removeExtraSpace=" + CorrectBibleTextErrors.removeExtraSpace);
		System.out.println("removeHtmlTags=" + CorrectBibleTextErrors.removeHtmlTags);
		System.out.println(
				"removeHtmlTagsWithEnclosingContent=" + CorrectBibleTextErrors.removeHtmlTagsWithEnclosingContent);
		System.out.println("removeSpecialCharactors=" + CorrectBibleTextErrors.removeSpecialCharactors);
		if (CorrectBibleTextErrors.findAllSpecialCharacters) {
			System.out.println(
					"Please note processing may take several minutes to hours based on number of bible versions to findAllSpecialCharacters...");
		}
		File dir = new File(bibleSourcePath);
		String[] versions = bibleVersions;
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".ont") || file.getName().endsWith(".ot") || file.getName().endsWith(".nt")) {
				for (String version : versions) {
					if (file.getName().startsWith(version)) {
						System.out.println("Started processing for the version: " + version);
						extraSpaceCount = 0;
						htmlTagsCount = 0;
						htmlTagsWithEnclosingContentCount = 0;
						specialCharactorsCount = 0;
						allSpecialCharactersPerVersion = new HashSet<String>();
						Bible bible;
						try {
							bible = TheWord.getBible(file.getAbsolutePath(),
									file.getParentFile().getAbsolutePath() + "\\" + version + "-information.ini");
							for (Book book : bible.getBooks()) {
								System.out.println("\nStarted processing : " + book.getShortName());
								for (Chapter chapter : book.getChapters()) {
									System.out.print(chapter.getChapter() + ", ");
									for (Verse verse : chapter.getVerses()) {
										applyCorrections(verse.getText(), book.getShortName() + " "
												+ chapter.getChapter() + ":" + verse.getNumber());
									}
								}
							}
							System.out.println("\nCompleted processing for the version: " + version);
							System.out.println(
									"allSpecialCharacters found in this version: " + allSpecialCharactersPerVersion);
							System.out.println("Total Verses impacted due to removeExtraSpaces: " + extraSpaceCount);
							System.out.println("Total Verses impacted due to removeHtmlTags: " + htmlTagsCount);
							System.out.println("Total Verses impacted due to htmlTagsWithEnclosingContent: "
									+ htmlTagsWithEnclosingContentCount);
							System.out.println(
									"Total Verses impacted due to removeSpecialCharactors: " + specialCharactorsCount);
						} catch (IOException e) {
							e.printStackTrace();
							System.out.println("Unable to open the file: " + e.getMessage());
						}
						Utils.createFile(sb.toString(), file.getName());
						sb = new StringBuilder();
					}
				}
			}
		}
		System.out.println("Completed processing all the bible versions");
		System.out.println("allSpecialCharacters found in for all bible versions: " + allSpecialCharacters);
		System.out.println(
				"Total Verses impacted for all bible versions due to removeExtraSpaces: " + removeExtraSpaceCount);
		System.out
				.println("Total Verses impacted for all bible versions due to removeHtmlTags: " + removeHtmlTagsCount);
		System.out.println("Total Verses impacted for all bible versions due to htmlTagsWithEnclosingContent: "
				+ removeHtmlTagsWithEnclosingContentCount);
		System.out.println("Total Verses impacted for all bible versions due to removeSpecialCharactors: "
				+ removeSpecialCharactorsCount);
	}

	private static void applyCorrections(String text, String reference) {
		String temp;
		if (CorrectBibleTextErrors.removeExtraSpace) {
			temp = text;
			text = Utils.removeExtraSpaces(text);
			if (temp.length() > text.length()) {
				extraSpaceCount++;
				removeExtraSpaceCount++;
			}
		}
		if (CorrectBibleTextErrors.removeHtmlTags) {
			temp = text;
			text = Utils.removeHTMLTags(text);
			if (temp.length() > text.length()) {
				htmlTagsCount++;
				removeHtmlTagsCount++;
			}
		}
		if (CorrectBibleTextErrors.findAllSpecialCharacters) {
			text = Utils.findAllSpecialCharacters(text, reference);
			if (text != null && text != "") {
				allSpecialCharacters.add(text);
				allSpecialCharactersPerVersion.add(text);
			}
		}
		if (CorrectBibleTextErrors.removeSpecialCharactors != null) {
			temp = text;
			text = Utils.removeSpecialCharactors(text);
			if (temp.length() > text.length()) {
				specialCharactorsCount++;
				removeSpecialCharactorsCount++;
			}
		}
		sb.append(text).append("\n");
	}

	private static boolean validInput(String[] args) {
		if (args.length < 1) {
			System.out.println("Please give additional details in the expected format..");
			printHelpMessage();
			return false;
		}
		for (String arg : args) {
			String[] arr = arg.split("=");
			if ("bibleSourcePath".equalsIgnoreCase(arr[0])) {
				bibleSourcePath = arr[1];
			} else if ("bibleVersions".equalsIgnoreCase(arr[0])) {
				bibleVersions = arr[1].split(",");
			} else if ("removeSpecialCharactors".equalsIgnoreCase(arr[0])) {
				removeSpecialCharactors = arr[1];
			} else if ("removeExtraSpace".equalsIgnoreCase(arr[0])) {
				if ("yes".equalsIgnoreCase(arr[1]) || "true".equalsIgnoreCase(arr[1])) {
					removeExtraSpace = true;
				} else {
					removeExtraSpace = false;
				}
			} else if ("removeHtmlTags".equalsIgnoreCase(arr[0])) {
				if ("yes".equalsIgnoreCase(arr[1]) || "true".equalsIgnoreCase(arr[1])) {
					removeHtmlTags = true;
				} else {
					removeHtmlTags = false;
				}
			} else if ("removeHtmlTagsWithEnclosingContent".equalsIgnoreCase(arr[0])) {
				if ("yes".equalsIgnoreCase(arr[1]) || "true".equalsIgnoreCase(arr[1])) {
					removeHtmlTagsWithEnclosingContent = true;
				} else {
					removeHtmlTagsWithEnclosingContent = false;
				}
			} else if ("findAllSpecialCharacters".equalsIgnoreCase(arr[0])) {
				if ("yes".equalsIgnoreCase(arr[1]) || "true".equalsIgnoreCase(arr[1])) {
					findAllSpecialCharacters = true;
				} else {
					findAllSpecialCharacters = false;
				}
			}
		}
		if ((bibleSourcePath == null || bibleSourcePath.length() == 0)) {
			System.out.println("\"bibleSourcePath\" parameter is mandatory");
			return false;
		}
		if ((bibleVersions == null || bibleVersions.length == 0)) {
			System.out.println("\"bibleVersions\" parameter is mandatory");
			return false;
		} else {

		}
		if ((removeSpecialCharactors == null || removeSpecialCharactors.length() == 0) && !removeExtraSpace
				&& !removeHtmlTags && !removeHtmlTagsWithEnclosingContent && !findAllSpecialCharacters) {
			System.out.println(
					"There is nothing to correct, please give input with any of these parameters while running the program..");
			System.out.println(
					"\tremoveSpecialCharactors or removeExtraSpace or removeHtmlTags or removeHtmlTagsWithEnclosingContent");
			return false;
		}
		return true;
	}

	public static void printHelpMessage() {
		System.out.println("\nHelp on Usage of this program:");
		System.out.println(
				"\nSyntax to run this program:\n\tjava -jar correct-bible-text-errors.jar bibleSourcePath=\"C:\\Directory 1\\Sub Directory 2\" bibleVersions=\"TBSI,ERV-ta,CTB1973,TamWCV\" removeExtraSpace=yes removeSpecialCharactors=\"!#$\" removeHtmlTags=yes");
		System.out.println(
				"\nbibleSourcePath => give the full directory path where the bible text & corresponding *-information.ini files are stored");
		System.out.println("\tUse \\\\ or / in instead of just single \\ in the file path");
		System.out.println(
				"\tYou can download bible text/database from https://github.com/yesudas/all-bible-databases/tree/main/Bibles/TheWord-Bible-Databases");
		System.out.println(
				"\tDownload both *-information.ini files as well as bible text files in the format *.ont or *.nt");
		System.out.println(
				"bibleVersions => Use comma separated listed of all bible versions Ex.TBSI,ERV-ta,CTB1973,TamWCV ");
		System.out.println("removeExtraSpace => Optional value, giving the value \"yes\" removes extra spaces");
		System.out.println(
				"removeSpecialCharactors => Optional value, can contain more than one special characters, it can include alphabets as well, should be enclosed inside double quotes. Ex. removeSpecialCharactors=\"!@$\".");
		System.out.println(
				"removeHtmlTags => Optional value, giving the value \"yes\" removes only the html tags like <h>, <el>, </h>, </b>, etc not the content");
		// System.out.println(
		// "removeHtmlTagsWithEnclosingContent => Optional value, giving the value
		// \"yes\" removes both the html tags and the content within the opening and
		// ending of the html tags");
		System.out.println(
				"\tIf both removeHtmlTags and removeHtmlTagsWithEnclosingContent are given as input then removeHtmlTagsWithEnclosingContent takes precedence");
		System.out.println(
				"findAllSpecialCharacters => Optional value, giving the value \"yes\" finds all the special characters and adds them to the results-logs-<timestamp>.txt file");
		System.out.println(
				"\tPlease note processing may take several minutes to hours based on number of bible versions to findAllSpecialCharacters...");
		System.out.println(
				"\nExample 1: java -jar correct-bible-text-errors.jar bibleSourcePath=\"C:\\Directory 1\\Sub Directory 2\" bibleVersions=\"TBSI,ERV-ta,CTB1973,TamWCV\" removeExtraSpace=yes removeSpecialCharactors=\"!#$\" removeHtmlTags=yes");
		System.out.println(
				"\nExample 2: java -jar correct-bible-text-errors.jar bibleSourcePath=\"C:\\Directory 1\\Sub Directory 2\" bibleVersions=\"KJV\" removeExtraSpace=yes removeSpecialCharactors=\"^\" removeHtmlTagsWithEnclosingContent=yes");
	}

}
