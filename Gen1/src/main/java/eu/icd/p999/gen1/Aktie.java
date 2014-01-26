package eu.icd.p999.gen1;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("aktie")
public class Aktie {

	private String name;
	private double bestand;

	public Aktie(String name) {
		this.name=name;
	}

	public String getName() {
		return name;
	}

	public void add(int stueck2) {
		bestand+=stueck2;		
	}

	public double getBestand() {
		return bestand;		
	}

	public String toXML(int level) {
		String res=
				new String(new char[level]).replace('\0', '\t')+"<aktie>\r\n" + 
				new String(new char[level]).replace('\0', '\t')+"	<name>"+name+"</name>\r\n" +
				new String(new char[level]).replace('\0', '\t')+"	<bestand>"+bestand+"</bestand>\r\n" +
				new String(new char[level]).replace('\0', '\t')+"</aktie>\r\n";
		return res;
	}

}
