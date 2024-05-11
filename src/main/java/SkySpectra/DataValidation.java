package SkySpectra;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidation {
	public static boolean validate(String data) {
		String dateRegex = "^(19|20)\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";
		Pattern pattern = Pattern.compile(dateRegex);
		Matcher matcher = pattern.matcher(data);
		boolean formatCheck = matcher.matches();
		boolean checkCurrentDate = DataValidation.validateDate(data, "");


		return formatCheck && !checkCurrentDate;
	}

	public static boolean validateDate(String data, String otherDate) {
		boolean checkCurrentDate = false;
		LocalDate currentDate = null;
		try {
			LocalDate enteredDate = LocalDate.parse(data);
			if (otherDate.isEmpty()) {
				currentDate = LocalDate.now();
			} else {
				currentDate = LocalDate.parse(otherDate);
			}

			checkCurrentDate = enteredDate.isBefore(currentDate);
		} catch (DateTimeParseException e) {
			return false;
		}
		return checkCurrentDate;
	}

}