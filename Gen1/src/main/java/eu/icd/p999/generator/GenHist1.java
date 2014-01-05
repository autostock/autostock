package eu.icd.p999.generator;
import static lib.Lib.*;

public class GenHist1 implements IGen {
	private double k;
	private boolean first=true;

	/**
	 * Distribution based on real history. Aber Kursverläufe >= 0.<br>
	 * Startkapital k0=1.
	 */
	public GenHist1() {
		k = 1;
	}
	
	/**
	 * Beim ersten Aufruf wird k0=1 geliefert.<br>
	 * Ansonsten der neue Kurswert k' mit k' = k + k*random()<br>
	 * <br>
	 * Wobei random() verteilt ist wie in den historischen Daten beschrieben. 
	 * @return Neuer Kurswert
	 */
	public double next() {
		if (first) {
			first=false;
			return 1;
		};
		double maxVolatile= HistoryStore1.next();
		
		double d = k * maxVolatile;
		if (k + d > 0) {
			k += d;
		} else {
			k=0;
		}
		return k;
	};
}
