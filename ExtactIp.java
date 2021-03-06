import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExtractIp {
	
	/**
	 * Get Ipv4 of a named Card
	 * @param args
	 */
	public static void main(String[] args) {

		// Arguments 
		if (args.length != 1 || (args[0].equals("-h") || args[0].equals("--help") || args[0].equals("help"))) {
			help();
			System.exit(0);
		}

		String osName = System.getProperty("os.name");
		String isWindowRegex = "win";
		Pattern isWindowPattern = Pattern.compile(isWindowRegex, Pattern.CASE_INSENSITIVE);
		Boolean isWindow = isWindowPattern.matcher(osName).find();
		
		
		String config = null;
		if (isWindow) {
			config = getStreamOutputProcess("ipconfig",  "/all");
		} else {
			config = getStreamOutputProcess("ifconfig");
		}
		
		
		
		String card = args[0];
		
		String regexIp = null;
		if (isWindow) {
			regexIp = card + ".*?IPv4[\\.\\s]*:\\s(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
		} else {
			regexIp = card + ":.+?inet (\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\s+netmask";
		}
		
		
		Pattern ipPattern = Pattern.compile(regexIp, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher ipv4Matcher = ipPattern.matcher(config);
		
		
		ArrayList<String> ipv4 = new ArrayList();
		while (ipv4Matcher.find()) {
			ipv4.add(ipv4Matcher.group(1));
		}
		
		if (ipv4.size()>0) {
			for (String ip : ipv4) {
				System.out.println("IPv4 trouvée pour la carte nommée " + card + ": " + ip);
			}
		} else {
			System.out.println("Aucune carte nommée \"" + card + "\" ne possède d'ipv4");
		}
		
	}
	
	public static String getStreamOutputProcess(String... processName) {
		ProcessBuilder processbuilder = new ProcessBuilder(processName);
		processbuilder.redirectErrorStream(true);
		
		try {
			Process process = processbuilder.start();
			try (BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				StringBuilder string = new StringBuilder();
				while (true) {
					String line = stream.readLine();
					if (line == null) {
						break;
					}
					string.append(line).append("\n");
				}
				process.waitFor();
				return string.toString();
			}
			
		} catch (Exception exception) {
			throw new RuntimeException("Cannot launch process " + processName);
		}
	}
	
	public static void help() {
		String help = "extractIp - v1\n"
				+"Usage: extractIp [cardName]"
				+"\n"
				+"cardName: The name of the card to extract IPv4\n";
		
		System.out.println(help);
	}
	

}

