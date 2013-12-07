/**
 * Copyright (c) iCD GmbH & Co. KG, Frechen, Germany, 2013, http://www.icd.eu
 * Author: wrossner
 */
package eu.icd.p999.gen1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author wrossner
 */
public class Tests {
    @Test
    public void testTest3() throws IOException {
    	final BufferedReader in = new BufferedReader(new FileReader("src/test/resources/allianz.valid"));
    	final class Interceptor extends PrintStream
    	{
    	    private String line="";
			private int lineno;
			public Interceptor(OutputStream out)
    	    {
    	        super(out, true);
    	    }
    	    @Override
    	    public void print(String s)
    	    {
    	    	line+=s;
    	    	if (s.endsWith("\n")) {
    	    		lineno++;
    	    		//super.print("intercept: "+line);
    	    		if (line.startsWith(":"))
						try {
							test(line);
						} catch (IOException e) {
							new AssertionError(e);
						}
    	    		line="";    	    		
    	    	}
    	    }
			private void test(String tline) throws IOException {
				String vline=in.readLine();
				while (!vline.startsWith(":")) {
					vline=in.readLine();
				}
				assertEquals("line "+lineno+" does not match", vline, tline.replace(',', '.').replace("\n", ""));
			}
    	}
    	PrintStream out = System.out;
    	System.setOut(new Interceptor(out));
    	
    	Test3.main(new String[] {"-m", "doc/boerse/run31164.mac", "-v", "3", "-r", "src/test/resources/allianz.txt"});
    	System.setOut(out);
    	in.close();
    }
}
