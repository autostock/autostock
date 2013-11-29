
public class Kurs {

	private String time;
	private String name;
	private double value;

	public Kurs(String name, String time, double value) {
		this.name=name;
		this.time=time;
		this.value=value;
	}

	/**
	 * <pre>
	<kurs>
		<timestamp>2013-11-29 17:45 CET</timestamp>
		<name>allianz</name>
		<min>95.4</min>
		<max>97.49</max>
		<end>96.2</end>
		<currency>EURO</currency>
	</kurs>
	 * </pre>
	 */
	public String toXML() {
		String res=
				"	<kurs>\r\n" + 
				"		<timestamp>"+time+"</timestamp>\r\n" + 
				"		<name>"+name+"</name>\r\n" + 
				"		<min/>\r\n" + 
				"		<max/>\r\n" + 
				"		<end>"+value+"</end>\r\n" + 
				"		<currency>unknown</currency>\r\n" + 
				"	</kurs>";
		return res;
	}

}
