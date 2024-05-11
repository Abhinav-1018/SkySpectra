package SkySpectra;


import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class UrlValidator {

    /*
     * Validates the given URL.
     *
     * @param url The URL to be validated.
     * @return true if the URL is valid, false otherwise.
     */
    public static boolean validate(String url) throws UnknownHostException {
        try {
            // Check for protocol (http or https)
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return false;
            }

            // Create URL object and check for proper syntax
            URL obj = new URL(url);

            // Check for valid host name
            if (obj.getHost() == null) {
                return false;
            }

            // Check for valid TLD (top level domain)
            String tld = obj.getHost().substring(obj.getHost().lastIndexOf(".") + 1);
            if (!tld.matches("[a-zA-Z]{2,}")) {
                return false;
            }

            // Check for file extension (if any)
            String path = obj.getPath();
            if (path != null && !path.equals("")) {
                String[] parts = path.split("\\.");
                if (parts.length > 0) {
                    String extension = parts[parts.length - 1];
                    if (!extension.matches("[a-zA-Z0-9]{2,}")) {
                        return false;
                    }
                }
            }

            // If all checks pass, return true
            return true;

        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + url);
            return false;
        }
    }
}
