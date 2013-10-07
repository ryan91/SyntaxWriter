package mvc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * This is a script that updates texmaker's settings file by own commands
 * declared in a tex class file. It makes all "new command" and "renew command"
 * names accessible in texmaker's default proposals
 * 
 * @version 1.1
 * @author Yannick Runge
 *
 */
public class IniWriter {
	
	/* Shows a little introduction when launching. Disable if you feel
	 * like you don't need it */
	private static boolean interactive = true;
	
	/* These are the default strings the script will look for.
	 * Adjust them if you know what you're doing */
	private static String className = "myClass.cls";
	private static String ini = "texmaker.ini";
	private static String newIni = "texmaker_modified.ini";
	
	private static boolean parseBoolFromString(String s) {
		if (s.equals("false") || s.equals("0"))
			return false;
		else
			return true;
	}
	
	/**
	 * Handles user input
	 * 
	 * @param	scanner			Used for user inputs
	 * @param	defaultFileName	The default file name
	 * @param	message			The message shown at the beginning
	 * @return	What the user entered
	 */
	private static String enterName(Scanner scanner, String defaultFileName, String message) {
		
		System.out.println(message + "(default: " + defaultFileName + ")");
		
		String entry = scanner.nextLine();
		
		if (!entry.isEmpty())
			defaultFileName = entry;
		
		return defaultFileName;
	}
	
	public static void interactiveLaunch() {
		Scanner scanner = new Scanner(System.in);
		
			System.out.println(
					"\n" +
					"This is a little script that handles your texmaker auto completition.\n" +
					"It reads a class file and detects all lines containing \"newcommand\".\n" +
					"The newcommand structure must be the following for the script to work:\n\n" +
					"\\newcommand\n" +
					"{\\nameOfCommand}[numberOfParameters (optional)]\n" +
					"{what it does}\n\n" +
					"If the structure is in any way different it might just ignore it or it crashes," +
					" just saying.\n\n" +
					"The program needs following parameters to work:\n" +
					"* a class file to read from\n" +
					"* an .ini file which was exported from texmaker\n" +
					"* the name of the created file (must be different from the .ini file it reads from)\n\n" +
					"By hitting enter you confirm that you are not a complete retard and read my introduction carefully."
					);
		
			scanner.nextLine();
		
		try {
			
			String className = enterName(
					scanner,
					IniWriter.className,
					"Please enter file name of the tex class");
			
			HashSet<String> names = readTexClass(className);
			
			String iniName = enterName(
					scanner,
					IniWriter.ini,
					"Please enter the file name of your ini file");
			
			String newIniName = enterName(
					scanner,
					IniWriter.newIni,
					"Please enter the file name of your new file");
			
			if (iniName.equals(newIniName)) {
				System.out.println("The name of the new ini may not be the same as the old one. Going to exit");
				System.exit(0);
			}
			
			writeIniFile(names, iniName, newIniName);
			
			System.out.println("\nFile written on " + newIniName);
			
			scanner.close();
			
		} catch (FileNotFoundException fnfe) {
			System.err.println("This file does not exist. Going to exit");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Could not write file. Maybe you don't have enough permissions or so.");
			System.exit(1);
		}
	}
	
	public static void nonInteractiveLaunch() {
		
		try {
			HashSet<String> names = readTexClass(IniWriter.className);
			
			if (IniWriter.ini.equals(IniWriter.newIni)) {
				System.out.println("The name of the new ini may not be the same as the old one. Going to exit");
				System.exit(0);
			}
			
			writeIniFile(names, IniWriter.ini, IniWriter.newIni);
			
			System.out.println("\nFile written on " + IniWriter.newIni);
			
		} catch (FileNotFoundException fnfe) {
			System.err.println("This file does not exist. Going to exit");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Could not write file. Maybe you don't have enough permissions or so.");
			System.exit(1);
		}
	}
	
	private static void setOption(String key, String value) {
		switch (key) {
		
		case "input":
			IniWriter.className = value;
			IniWriter.ini = value;
			break;
		
		case "ini":
			IniWriter.ini = value;
			break;
			
		case "class":
			IniWriter.className = value;
			break;
			
		case "output":
		case "newini":
			IniWriter.newIni = value;
			break;
		
		case "interactive":
			IniWriter.interactive = parseBoolFromString(value);
			break;
			
		default:
			System.out.println("Could not set option for " + key + ". Going to exit.");
			System.exit(0);
		}
		
		System.out.println("Set option " + key + " to " + value);
	}
	
	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 0)
			System.out.println("Setting options...");
		for (String s : args) {
			
			if (!s.contains("=")) {
				System.out.println("Every parameter must contain an \"=\" sign. Going to exit");
				return;
			}
			
			String[] param = s.split("=");
			
			if (param.length != 2) {
				System.out.println("The parameter " + s + " contains more than 1 \"=\" sign");
				return;
			}
			
			IniWriter.setOption(param[0], param[1]);
		}
		if (args.length != 0)
			System.out.println("...done");
		
		if (IniWriter.interactive)
			IniWriter.interactiveLaunch();
		else
			IniWriter.nonInteractiveLaunch();
	}

	public static void writeIniFile(HashSet<String> names, String readName, String writeName) throws FileNotFoundException, IOException {
		
		FileReader fr = new FileReader(readName);
		BufferedReader br = new BufferedReader(fr);
		
		FileWriter fw = new FileWriter(writeName);
		
		String line;
		while (true) {
			
			line = br.readLine();
			
			if (line == null)
				break;
			
			if (line.contains("Editor\\UserCompletion")) {
				
				String allCommands = line.substring(line.indexOf('=') + 1, line.length());
				String[] existingCommands = allCommands.split(", ");
				System.out.println("detecting existing names...");
				for (String s : existingCommands) {
					names.add(s);
					String print = new String(s);
					print = print.replaceAll("\\\\x2022", "*");
					print = "\\" + print.replaceAll("\\\\", "");
					System.out.println("+ " + print);
				}
				
				line = line.substring(0, line.indexOf('=') + 1);
				for (String s : names)
					line += s + ", ";
				line = line.substring(0, line.length() - 2);
			}
			
			fw.write(line + "\n");
		}
		br.close();
		fr.close();
		fw.close();
	}
	
	private static HashSet<String> readTexClass(String fileName) throws FileNotFoundException, IOException {
		
		HashSet<String> names = new HashSet<String>();
		
		FileReader fr = new FileReader(fileName);
		
		BufferedReader br = new BufferedReader(fr);
			
		String line = "";
		
		System.out.println("detecting...");
		
		while ((line = br.readLine()) != null) {
			
			if ( !line.contains("%") && line.contains("newcommand")) {
				
				line = br.readLine();
				
				if (!(line.contains("{") && line.contains("}")))
					continue;
				
				int left = line.indexOf('{');
				int right = line.lastIndexOf('}');
					
				String commandName = line.substring(left + 1, right);
				
				if (!(line.contains("[") && line.contains("]"))) {
					System.out.println("+ " + commandName.replaceAll("\\\\x2022", "*"));
					commandName = "\\" + commandName;
					names.add(commandName);
					continue;
				}
	
				left = line.indexOf('[');
				right = line.indexOf(']');
				
				int numberOfParam = 0;
				
				
				try {
					numberOfParam = Integer.parseInt(line.substring(left + 1, right));
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
					continue;
				}
				
				
				for (int i = 0; i < numberOfParam; ++i)
					commandName += "{\\x2022}";
				
				System.out.println("+ " + commandName.replaceAll("\\\\x2022", "*"));
				commandName = "\\" + commandName;
				names.add(commandName);
			}
		}
		
		fr.close();
			
		return names;
	}
}
