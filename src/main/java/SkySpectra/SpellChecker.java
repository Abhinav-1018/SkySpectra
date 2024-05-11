package SkySpectra;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SpellChecker {
	public static void main(String[] args) {
//		List<String> dictionary = readDictionaryFromFile(
//				"/Users/vraj/eclipse-workspace/FlightAnalysis/src/Files/city_names.txt");
//		if (dictionary.isEmpty()) {
//			System.out.println("Dictionary is empty or file not found.");
//			return;
//		}
//
//		Scanner scanner = new Scanner(System.in);
//
//		// Prompt the user to enter the city name
//		System.out.print("Enter the city name: ");
//
//		// Read the city name entered by the user
//		String wordToCheck = scanner.nextLine();
//
//		// Display the entered city name
//		System.out.println("You entered: " + wordToCheck);
//
//		scanner.close();
//		spellCheck(wordToCheck, dictionary);
	}

	private static List<String> readDictionaryFromFile(String filePath) {
		List<String> dictionary = new ArrayList<>();

		try {
			File file = new File(filePath);
			Scanner scanner = new Scanner(file);

			while (scanner.hasNext()) {
				dictionary.add(scanner.next().toLowerCase()); // Convert to lowercase for case-insensitive search
			}

			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return dictionary;
	}

	public static String spellCheck(String word) {
		word = word.toLowerCase().trim();
		ArrayList<String> wordList = new ArrayList<String>();
		int threshold = 2; // Maximum allowed edit distance

		List<String> dictionary = readDictionaryFromFile(
				"./Files/city_names.txt");
		
		if (dictionary.isEmpty()) {
			System.out.println("Dictionary is empty or file not found.");
			return "";
		}
		
		
		for (String dictWord : dictionary) {
			dictWord = dictWord.toLowerCase().trim();
			int distance = calculateLevenshteinDistance(word, dictWord);
			if (distance <= threshold) {
				wordList.add(dictWord);
			}
		}

		return word.equals(wordList.get(0)) ? "OK" : wordList.get(0);
	}

	private static int calculateLevenshteinDistance(String word1, String word2) {
		int[][] dp = new int[word1.length() + 1][word2.length() + 1];

		for (int i = 0; i <= word1.length(); i++) {
			for (int j = 0; j <= word2.length(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = min(dp[i - 1][j - 1] + (word1.charAt(i - 1) == word2.charAt(j - 1) ? 0 : 1),
							dp[i - 1][j] + 1, dp[i][j - 1] + 1);
				}
			}
		}

		return dp[word1.length()][word2.length()];
	}

	private static int min(int a, int b, int c) {
		return Math.min(a, Math.min(b, c));
	}
}
