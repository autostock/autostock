package lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Formatter;
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
		return Double.parseDouble(string);
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
		// TODO Auto-generated method stub
		return null;
	}

	public static Reference scanf(String format, Object... args) {
		return fscanf(stdin, format, args);
	}

	public static Reference sscanf(String input, String format, Object... args) {
		return fscanf(new Scanner(input), format, args);
	}

	public static String gets() {
		return stdin.nextLine();
	}

}
