package eu.icd.p999.generator;
import static lib.Lib.*;

public class GenAbsolutUni1 implements IGen {
	private double maxVolatile;
	private double kLower;
	private double kUpper;
	private double k;
	private boolean first=true;

	/**
	 * Historisches Gen1. War als C Programm realisiert unter Verwendung von rand().<br>
	 * Uniformly distributed Kursverläufe innerhalb limit.<br>
	 * Startkapital ist auf k0=1 normiert.
	 * 
	 * @param maxVolatile in [0.0, 1.0] Mit 100*maxVolatile ist entsprechender Prozentwert. 
	 * @param limit Der Kurswert bleibt immer in [max(0, (1.0 - limit) * k0), (1.0 + limit) * k0] 
	 */
	public GenAbsolutUni1(double maxVolatile, double limit) {
		this.maxVolatile=maxVolatile;

		kLower = (1.0 - limit);
		if (kLower < 0.0)
			kLower = 0.0;
		kUpper = (1.0 + limit);
		k = 1;
	}
	
	/**
	 * Beim ersten Aufruf wird k0=1 geliefert.<br>
	 * Ansonsten der neue Kurswert k' mit k' = k + k0*random()<br>
	 * <br>
	 * Wobei random() uniformly distributed im Intervall [-maxVolatile, maxVolatile] liegt. 
	 * Aber hier, als historische Besonderheit, im Intervall max. 2001 diskrete Werte annimmt. 	
	 * Gleichzeitig wird dafür gesorgt, dass k' innerhalb des limit-Intervalls liegt.
	 * 
	 * @return Neuer Kurswert
	 */
	public double next() {
		if (first) {
			first=false;
			return 1;
		};
		
		double d = rand() % 1001 / 1000.0;
		d = maxVolatile * d;
		if (rand() % 2 == 0)
			d = -d;
		if (k + d <= kLower)
			d = -d;
		else if (k + d >= kUpper)
			d = -d;

		if (k > kLower)
			k += d;
		return k;
	};
}
