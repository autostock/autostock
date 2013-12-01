import static lib.Lib.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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
public class onetest {
	private static String name;

	private static int window;

	private static int start;
	private static int tests;

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
		int event_id = Math.abs(rand())%(1000000-100000)+1000000;
		for (i = 0; i<window; i++) {
			PrintStream file = new PrintStream(new File(dir+"event-"+(event_id+i)+".xml"));
			file.println("<event>");
			file.println("	<!-- window "+(i)+" -->");
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
		for (; i<kurse.size(); i++) {
			kurs = kurse.get(i);
			value=kurs.getValue();
			//Event
			PrintStream file = new PrintStream(new File(dir+"event-"+(event_id+i)+".xml"));
			file.println("<event>");
			file.println("	<!-- test "+(i)+" -->");
			file.println(kurs.toXML());
			file.println("</event>");
			file.close();
			//Portfolio
			file = new PrintStream(new File(dir+"portfolio-"+(event_id+i)+".xml"));
			file.println(portfolio.toXML(0));
			file.close();
			Vector<String> lines= new Vector<String>(); 
			lines=system(new String[] {testee, "-data", dir, "-event", ""+ (event_id+i)});
			for (int j=0; j<lines.size(); j++) {
				String line = lines.get(j);
				System.out.print(line);

				String[] token = line.split("\\s+");
				int stueck=Integer.parseInt(token[1]);
				if (stueck!=0) {
					String name=token[0].replaceAll(":", "");
					portfolio.transaction(name, stueck, -stueck*value, 0.0);
					System.out.println(portfolio.getCapital());
				}
			}
		}
	};

}
