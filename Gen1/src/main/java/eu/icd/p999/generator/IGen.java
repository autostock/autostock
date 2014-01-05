package eu.icd.p999.generator;

public interface IGen {
	/**
	 * Beim ersten Aufruf wird k0=1 geliefert.<br>
	 * Ansonsten der neue Kurswert<br>
	 * @return Neuer Kurswert
	 */
	public double next();
}
