package lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Formatter;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;


public class Lib {
	static public Scanner stdin=new Scanner(System.in);

	public static int rand() {
		return Math.abs(random.nextInt());
	}

	public static Random random = new Random();

	public static double fabs(double handel) {
		return Math.abs(handel);
	}

	public static String sprintf(String format, Object... args) {
		return new Formatter().format(format, args).toString();
	};

	public static void fprintf(PrintStream out, String format, Object... args) {
		out.printf(format, args);
	
	}

	public static void printf(String format, Object... args) {
		fprintf(System.out, format, args);
	
	}

	public static int atoi(String string) {
		return Integer.parseInt(string);
	}

	public static double atof(String string) {
		return Double.parseDouble(string.replace(',', '.'));
	}

	public static int strcmp(String a, String b) {
		return a.compareTo(b);
	}

	public static String strcpy(String str1, String str2) {
		return str1+str2;
	}

	public static void fflush(PrintStream out) {
		out.flush();		
	}

	public static PrintStream fopen(String name, String mode) {
		try {
			return new PrintStream(new File(name));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public static Scanner fopen_r(String name, String mode) {
		try {
			return new Scanner(new FileInputStream(new File(name)));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public static int ref(Reference reference, Object... args) {
		return reference.count;
	}

	public static Reference fscanf(Scanner in, String format, Object... args) {
		Reference ref = new Reference();
		int n=0;
		for (int i = 0; i < format.length(); i++) {
			if (format.charAt(i)=='%') n++;
		}
		String[] token = format.split("\\s+");
		ref.values = new String[n];
		for (int i = 0; i < n; i++) {
			ref.values[i]=(args[i]==null?"":args[i].toString());
		}
		try {
			for (int i = 0; i < token.length; i++) {
				if (token[i].startsWith("%")) {
					String tmp=in.next();
					ref.values[ref.count]=tmp.substring(0, tmp.length() - (token[i].length()-2));
					//System.out.print(ref.values[ref.count]+" ");
					ref.count++;
				}
			}
			//System.out.println();
		} catch (NoSuchElementException e) {
		}
		return ref;
	}

	public static Reference scanf(String format, Object... args) {
		return fscanf(stdin, format, args);
	}

	public static Reference sscanf(String input, String format, Object... args) {
		return fscanf(new Scanner(input), format, args);
	}

	public static String gets(Scanner in) {
		try {
			return in.nextLine();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

}
