package eu.icd.p999.overview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.junit.Test;

import eu.icd.p999.generator.GenGauss1;
import eu.icd.p999.generator.GenHist1;
import eu.icd.p999.generator.GenUni3;
import eu.icd.p999.generator.HistoryStore1;
import eu.icd.p999.generator.HistoryStore2;
import eu.icd.p999.generator.IGen;
import static lib.Lib.*;


public class Download {
	private URL url;

	private InputStream read() {
		try {
//            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
//			return con.getInputStream();
			return url.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void readFeed() throws IOException {
		InputStream in = read();

		int c;
		for (int i=0; i<100000 && (c = in.read()) != -1; i++) {
			System.out.print((char) c);
		}
	}


	@Test
	public void download() throws IOException {
//		Set Date Range
//		Start Date:	Dec 28 2000
//		End Date:	Jan 7 2014
//		http://ichart.finance.yahoo.com/table.csv?s=ALV.F&a=11&b=28&c=2000&d=00&e=7&f=2014&g=d&ignore=.csv
		String feedUrl = "http://ichart.finance.yahoo.com/table.csv?s=ALV.F&a=11&b=28&c=2000&d=00&e=7&f=2014&g=d&ignore=.csv";
		this.url = new URL(feedUrl);
		readFeed();
	}
	
}
