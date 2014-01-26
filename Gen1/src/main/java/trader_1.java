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
public class trader_1 {
	
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

		trader_1 trader = new trader_1();
		trader.execute();
	}

	private String name;
	private double bestand;
	private double capital;
	private double[] history;

	private void execute() throws FileNotFoundException {
		readEvents();
		System.out.println(name+": "+new Random().nextInt()%3);
	}

	/**
	 * opens a file dir+"event-"+id+".xml" having this schema:
	 * <pre>
	<event>
		<portfolio>
			<capital>
				10000.56
			</capital>
			<aktie>
				<name>allianz</name>
				<bestand>200</bestand>
			</aktie>
			<aktie>
				...
			</aktie>	
		</portfolio>
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
	</pre>
	 * @throws FileNotFoundException 
	 */
	private void readEvents() throws FileNotFoundException {
		Vector<Double> h= new Vector<Double>();
		int id=event_id-window;
		FileNotFoundException exception=null;
		for(int i=0; i<window+1; i++, id++) {
			Scanner file;
			try {
				file = new Scanner(new FileInputStream(new File(dir+"event-"+id+".xml")));
			} catch (FileNotFoundException e) {
				exception=e;
				continue;
			}
			file.useDelimiter("[\\s<>]+");
			String prefix="";
			while(file.hasNext()) {
				String token = file.next();
				if(token.equals("portfolio") || token.equals("kurs")) {
					prefix=token;
				}
				String struct = prefix+"."+token;
				if(struct.equals("kurs.value")) {
					h.add(Lib.atof(file.next()));
				}
				if(struct.equals("portfolio.name")) {
					name=file.next();
				}
				if(struct.equals("portfolio.bestand")) {
					bestand=atof(file.next());
				}
				if(struct.equals("portfolio.capital")) {
					capital=Lib.atof(file.next());
				}
			}
			file.close();		
		}
		if (h.size()==0 && exception!=null) {
			throw exception;
		}
		history=new double[h.size()];
		for (int i = 0; i < history.length; i++) {
			history[i]=h.get(i);
		}
	}
}
