package eu.icd.p999.generator;
import static lib.Lib.*;

public class GenRoulette implements IGen {
	/**
	 * Uniformly distributed Values 0 (green) und 1 bis 36  (European)<br>
	 * @param i 
	 * 
	 */
	public GenRoulette() {
	}
	
	/**
	 * @return random()
	 * Wobei random() uniformly distributed im Intervall [0, 36]. 
	 */
	public double next() {
		return random.nextInt(37);
	};
}
