import static lib.Lib.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

/**
 * @author wrossner
 *
 */
public class onetest {
	private static String name;

	private static int window;

	private static int start;
	private static int tests;
	/**
	 * -name aktienname (default is stdin)
	 * -window size (default is 250)
	 * -tests size (default is 1 use 250 for a testee)
	 * -start start-from-end (default is 0)
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] argv) throws FileNotFoundException {
		int argc = argv.length;
		window=250;
		tests=1;
		if (argc > 1) {
			for (int i = 0; i < argc; i++) {
				if (strcmp(argv[i], "-name") == 0) {
					name = argv[++i];
				} else if (strcmp(argv[i], "-window") == 0) {
					window = atoi(argv[++i]);
				} else if (strcmp(argv[i], "-tests") == 0) {
					tests = atoi(argv[++i]);
				} else if (strcmp(argv[i], "-start") == 0) {
					start = atoi(argv[++i]);
				} else {
					printf("unknown option %s\n", argv[i]);
					System.exit(9);
				}
			}
		}

		generate();
	}

	/**
	 * date		kurse
	 * ...
	 * :0/17     782,44
	 * :0/18     723,15
	 * :0/19     735,68
	 * ...
	 * 
	 * @return
	 * @throws FileNotFoundException 
	 */
	static void generate() throws FileNotFoundException {
		Scanner in;
		if (name==null) {
			in=new Scanner(System.in);
			name="WKN "+rand();
		} else {
			in=new Scanner(new FileInputStream(new File(name)));
			name=new File(name).getName();
			int p=name.lastIndexOf('.');
			if (p>0) name= name.substring(0, p);
		}
		Vector<String> list = new Vector<String>();
		Vector<Kurs> kurse = new Vector<Kurs>();
		try {
			for (;;) {
				String line= in.nextLine();
				list.add(line);
			}
		} catch (NoSuchElementException e) {
		}
		in.close();
		
		int begin=list.size()-start-window-tests;
		int end=list.size()-start;
		
		for (int i = begin; i<end; i++) {
			String line= list.get(i);
			String[] token = line.split("\\s+");
			kurse.add(new Kurs(name, token[0], atof(token[1])));
		}
		int i;
		for (i = 0; i<window; i++) {
			System.out.println("	<!-- window "+(i)+" -->");
			System.out.println(kurse.get(i).toXML());
		}
		for (; i<kurse.size(); i++) {
			System.out.println("	<!-- test "+(i)+" -->");
			System.out.println(kurse.get(i).toXML());
		}
	};

}
