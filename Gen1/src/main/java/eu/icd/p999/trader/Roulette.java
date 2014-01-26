package eu.icd.p999.trader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

import org.junit.Test;

import eu.icd.p999.generator.GenGauss1;
import eu.icd.p999.generator.GenHist1;
import eu.icd.p999.generator.GenRoulette;
import eu.icd.p999.generator.GenUni3;
import eu.icd.p999.generator.HistoryStore1;
import eu.icd.p999.generator.HistoryStore2;
import eu.icd.p999.generator.IGen;
import eu.icd.p999.generator.RouletteStock0;
import static lib.Lib.*;


public class Roulette {
	private static final int DAYS = 250;

	@Test
	public void getExamples() {
		IGen[] list= new IGen[] {
				new RouletteStock0(new GenRoulette(), 0),
				new RouletteStock0(new GenRoulette(), 0),
				new RouletteStock0(new GenRoulette(), 0),
		};
		double[][] v=new double[list.length][DAYS];
		
		
		for (int d = 0; d < DAYS; d++) {
			for (int j = 0; j < list.length; j++) {
				v[j][d]=list[j].next();
			}
		}
		for (int d = 0; d < DAYS; d++) {
			for (int j = 0; j < v.length; j++) {
				System.out.printf("\t%d", (int)v[j][d]);
			}
			System.out.printf("\n");
		}
	}

	@Test
	public void getStrategie1() {
		int N=100000000;
		double[] astock =new double[DAYS];
		double[] awin =new double[DAYS];
		double[] min =new double[DAYS];
		double[] max =new double[DAYS];
		for (int i = 0; i < N; i++) {
			IGen stock=new RouletteStock0(new GenRoulette(), 0);
			

			double start=100;
			double win=start;
			double s0 = stock.next();
			double k0=start;
			for (int d = 0; d < DAYS; d++) {
				win-=k0;
				double s1 = stock.next();
				if (s1>s0) {
					win+=2*k0;
					k0=start;
				} else {
					k0=k0+k0;
				}
				s0=s1;
//				System.out.printf("%f\t%g\n", s0, win);
				astock[d]=s0;
				awin[d]=win;
				if (min[d]>win) min[d]=win;
				if (max[d]<win) max[d]=win;
			}
		}
		for (int d = 0; d < DAYS; d++) {
			System.out.printf("%g\t%g\t%g\t%g\n", astock[d]/N, max[d]/N, awin[d]/N, max[d]/N);
		}
	}
}
