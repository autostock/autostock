package eu.icd.p999.gen1;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Vector;


public class Portfolio {

	private double capital;
	private Collection<Aktie> content=new Vector<Aktie>();

	public Portfolio(double capital) {
		this.capital=capital;
	}

	public void add(Aktie aktie) {
		content.add(aktie);		
	}

	public void transaction(String name, int stueck, double prize, double cost) {
		for (Aktie aktie : content) {
			if (aktie.getName().equals(name)) {
				aktie.add(stueck);				
				if (capital+prize-cost<0) throw new IllegalStateException("capital below zero");
				capital+=prize-cost;
				return;
			}
		}
		throw new NoSuchElementException(name+"does not exists!");
	}

	public String toXML(int level) {
		String res=
				new String(new char[level]).replace('\0', '\t')+"<portfolio>\r\n" + 
				new String(new char[level]).replace('\0', '\t')+"	<capital>"+capital+"</capital>\r\n";
		for (Aktie aktie : content) {
			res+=aktie.toXML(level+1);
		}
		res+=new String(new char[level]).replace('\0', '\t')+"</portfolio>";
		return res;
	}

	public double getCapital() {
		return capital;
	}

}
