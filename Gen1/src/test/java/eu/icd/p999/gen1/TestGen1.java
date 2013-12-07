/**
 * Copyright (c) iCD GmbH & Co. KG, Frechen, Germany, 2013, http://www.icd.eu
 * Author: wrossner
 */
package eu.icd.p999.gen1;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author wrossner
 */
public class TestGen1 {
    @Test
    public void testGen1Kurs() {
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(Kurs.class);

		Kurs kurs = new Kurs("wkn777", "12-13-12", 98.7);
		System.out.println(xstream.toXML(kurs));
		//xstream.alias("rss", Rss.class);
//		xstream.alias("channel", Channel.class);
//		xstream.alias("item", Item.class);
//		xstream.aliasField("dc:creator", Item.class, "author");
//        xstream.addImplicitCollection(Channel.class, "item");
    }

    @Test
    public void testGen1Portfolio() {
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(Portfolio.class);

		Portfolio portfolio = new Portfolio(100000);
		portfolio.add(new Aktie("wkn001"));
		portfolio.add(new Aktie("wkn002"));
		System.out.println(xstream.toXML(portfolio));
    }

}
