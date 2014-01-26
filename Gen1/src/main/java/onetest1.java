import static lib.Lib.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

import eu.icd.p999.gen1.Aktie;
import eu.icd.p999.gen1.Kurs;
import eu.icd.p999.gen1.Portfolio;

/**
 * @author wrossner
 *
 */
public class onetest1 {
	private static String name;

	private static int window;

	private static int start;
	private static int tests;
	private static int verbose=0;

	private static final int VERBOSE_KURSE = 0x1;
	private static final int VERBOSE_INFO = 0x02;
	private static final int VERBOSE_STDOUT = 0x04;
	private static final int VERBOSE_WINDOW = 0x08;
	private static final int VERBOSE_TEST = 0x10;
	private static final int VERBOSE_TESTEE = 0x20;

	private static String dir="target/tmp/";

	private static String testee="dir";
	
	/**
	 * -name aktienname (default is stdin)
	 * -window size (default is 250)
	 * -tests size (default is 1 use 250 for a testee)
	 * -start start-from-end (default is 0)
	 * @throws Exception 
	 */
	public static void main(String[] argv) throws Exception {
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
				} else if (strcmp(argv[i], "-dir") == 0 || strcmp(argv[i], "-data") == 0) {
					dir = argv[++i]+"/";
				} else if (strcmp(argv[i], "-testee") == 0) {
					testee = argv[++i];
				} else if (strcmp(argv[i], "-verbose") == 0 || strcmp(argv[i], "-v") == 0) {
					verbose = atoi(argv[++i]);
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
	 * @throws Exception 
	 */
	static void generate() throws Exception {
		Vector<Kurs> kurse = new Vector<Kurs>();
		readKurs(kurse);

		int i;
		int event_id = Math.abs(rand())%(1000000-100000)+1000000;
		for (i = 0; i<window; i++) {
			if (isVerbose(VERBOSE_KURSE)) kurs(kurse.get(i));
			File f = new File(dir+"event-"+(event_id+i)+".xml");
			if (isVerbose(VERBOSE_WINDOW)) System.out.printf("writing window file %3d: %s\n", i+1, f.getAbsolutePath());
			PrintStream file = new PrintStream(f);
			file.println("<event>");
			file.println("	<!-- window "+(i+1)+" -->");
			file.println(kurse.get(i).toXML());
			file.println("</event>");
			file.close();
		}
		Kurs kurs = kurse.get(i-1);
		double capital=10000;
		double value=kurs.getValue();
		Portfolio portfolio=new Portfolio(capital);
		Aktie aktie=new Aktie(kurs.getName());
		portfolio.add(aktie);
		portfolio.transaction(kurs.getName(), (int)(0.5*capital/value), -(int)(0.5*capital/value)*value, 0.0);
		
		if (isVerbose(VERBOSE_WINDOW) && isVerbose(VERBOSE_TEST)) System.out.println();
		if (isVerbose(VERBOSE_INFO)) info(value, portfolio);

		for (int t=1; i<kurse.size(); t++,i++) {
			kurs = kurse.get(i);
			if (isVerbose(VERBOSE_KURSE)) kurs(kurse.get(i));
			value=kurs.getValue();
			//Event
			File f = new File(dir+"event-"+(event_id+i)+".xml");
			if (isVerbose(VERBOSE_TEST)) System.out.printf("writing test %d/%d  file: %s\n", t, tests, f.getAbsolutePath());
			PrintStream file = new PrintStream(f);
			file.println("<event>");
			file.println("	<!-- test "+(t)+" -->");
			file.println(kurs.toXML());
			file.println(portfolio.toXML(1));
			file.println("</event>");
			file.close();
					
			String[] cmd = new String[] {testee, "-window", ""+window, "-data", dir, "-event", ""+ (event_id+i)};
			if (isVerbose(VERBOSE_TESTEE)) System.out.printf("calling testee: %s\n", Arrays.toString(cmd));
			Vector<String> lines= system(cmd);
			
			if (isVerbose(VERBOSE_STDOUT)) {
				for (int j = 0; j < lines.size(); j++) {
					String line = lines.get(j);
					System.out.print("  " + new File(testee).getName() + "> "
							+ line);
				}
			}
			for (int j=0; j<lines.size(); j++) {
				String line = lines.get(j);
				String[] token = line.split("\\s+");
				int stueck=Integer.parseInt(token[1]);
				if (stueck!=0) {
					String name=token[0].replaceAll(":", "");
					portfolio.transaction(name, stueck, -stueck*value, 0.0);
				}
			}
			if (isVerbose(VERBOSE_INFO)) info(value, portfolio);
		}
	}

	private static void kurs(Kurs kurs) {
		System.out.printf(kurs.getTimestamp()+"> Kurse{");
		System.out.printf("%s: %.4f; ", kurs.getName(), kurs.getValue());
		System.out.printf("}\n");
	}

	private static boolean isVerbose(int mode) {
		return (verbose&mode)!=0;
	}

	private static void info(double value, Portfolio portfolio) {
		System.out.printf("Portfolio{");
		double total=portfolio.getCapital();
		for (Aktie aktie1 : portfolio.content) {
			System.out.printf("%s: %.2f Stück; ", aktie1.getName(), aktie1.getBestand());
			total+=aktie1.getBestand()*value;
		}
		System.out.printf("Konto: %.2f Geld; Total: %.2f Geld}\n", portfolio.getCapital(), total);
	}

	private static void readKurs(Vector<Kurs> kurse)
			throws FileNotFoundException {
		Vector<String> list = new Vector<String>();
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
	};

}
