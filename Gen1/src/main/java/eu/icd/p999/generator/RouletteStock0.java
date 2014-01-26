package eu.icd.p999.generator;


public class RouletteStock0 implements IGen {

	private boolean first=true;
	private double k;
	private IGen generator;

	public RouletteStock0(IGen generator, double k0) {
		k=k0;
		this.generator=generator;
	}

	@Override
	public double next() {
		if (first) {
			first=false;
			return 0;
		};
		int r=(int)generator.next();
		if (r==0) {
			
		} else if ((r&1)==0) {
			k+=1;
		} else {
			k-=1;
		}
		return k;
	}

}
