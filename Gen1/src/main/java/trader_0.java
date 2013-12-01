import static lib.Lib.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import lib.Lib;

/**
 *  Aufruf
 *   	<program> -data <data-directory> -event <event-id>
 *   Beispiel:
 *   	trader_123456.exe -data c:/temp/boerse/ -event 1003700
 *   
 */
public class trader_0 {
	
	private static String dir="";
	private static int event_id;
	private static int window;

	/**
	 * -data data-directory
	 * -event event-id
	 * -window size (default is 250)
	 * 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] argv) throws FileNotFoundException {
		window=250;
		int argc = argv.length;
		if (argc > 1) {
			for (int i = 0; i < argc; i++) {
				if (strcmp(argv[i], "-data") == 0) {
					dir = argv[++i];
				} else if (strcmp(argv[i], "-event") == 0) {
					event_id = atoi(argv[++i]);
				} else if (strcmp(argv[i], "-window") == 0) {
					window = atoi(argv[++i]);
				} else {
					printf("unknown option %s\n", argv[i]);
					System.exit(9);
				}
			}
		}

		trader_0 trader = new trader_0();
		trader.think();
	}

	private String name;
	private int bestand;
	private double capital;
	private double[] history;

	private void think() throws FileNotFoundException {
		readPortfolio();
		readHistory();
		System.out.println(name+": "+new Random().nextInt()%3);
	}

	private void readHistory() {
		Vector<Double> h= new Vector<Double>(); 
		try {
			for(int i=event_id;;i--) {
				h.add(0, readEvent(i));
			}
		} catch (FileNotFoundException e) {
		}
		history=new double[h.size()];
		for (int i = 0; i < history.length; i++) {
			history[i]=h.get(i);
		}
	}

	/**
	<event>
		<kurs>
			<timestamp>2013-11-29 17:45 CET</timestamp>
			<name>allianz</name>
			<min>95.4</min>
			<max>97.49</max>
			<value>96.2</value>
			<currency>EURO</currency>
		</kurs>
		<kurs>
			...
		</kurs>
	</event>
	 */
	private double readEvent(int id) throws FileNotFoundException {
		Scanner file = new Scanner(new FileInputStream(new File(dir+"event-"+id+".xml")));
		file.useDelimiter("[\\s<>]+");
		try {
			for(int i=0;;i++) {
				String token = file.next();
				if(token.equals("value") || token.equals("end")) {
					return Lib.atof(file.next());
				}
			}
		} catch (NoSuchElementException e) {
		} finally {
			file.close();		
		}
		throw new NumberFormatException("no value found in event "+id);
	}

	private void readPortfolio() throws FileNotFoundException {
		Scanner file = new Scanner(new FileInputStream(new File(dir+"portfolio-"+event_id+".xml")));
		file.useDelimiter("[\\s<>]+");
		try {
			for(int i=0;;i++) {
				String token = file.next();
				if(token.equals("name")) {
					name=file.next();
				} else if(token.equals("bestand")) {
					bestand=file.nextInt();
				} else if(token.equals("capital")) {
					capital=Lib.atof(file.next());
				}
			}
		} catch (NoSuchElementException e) {
		} finally {
			file.close();		
		}
	}


}
