package eu.icd.p999.generator;
import static lib.Lib.*;

public class GenUni3 implements IGen {
	private double maxVolatile;
	private double k;
	private boolean first=true;

	/**
	 * Uniformly distributed Kursverläufe >= 0.<br>
	 * Startkapital k0=1.
	 * 
	 * @param maxVolatile in [0.0, 1.0] Mit 100*maxVolatile ist entsprechender Prozentwert. 
	 */
	public GenUni3(double maxVolatile) {
		this.maxVolatile=maxVolatile;
		k = 1;
	}
	
	/**
	 * Beim ersten Aufruf wird k0=1 geliefert.<br>
	 * Ansonsten der neue Kurswert k' mit k' = k + k*random()<br>
	 * <br>
	 * Wobei random() uniformly distributed im Intervall ]-maxVolatile, maxVolatile[ liegt. 
	 * @return Neuer Kurswert
	 */
	public double next() {
		if (first) {
			first=false;
			return 1;
		};
		
		double d = k * maxVolatile * random.nextDouble();
		if (rand() % 2 == 0)
			d = -d;

		if (k + d >= 0) {
			k += d;
		}
		return k;
	};
}
