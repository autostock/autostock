package eu.icd.p999.overview;

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
import eu.icd.p999.generator.GenUni3;
import eu.icd.p999.generator.HistoryStore1;
import eu.icd.p999.generator.HistoryStore2;
import eu.icd.p999.generator.IGen;
import static lib.Lib.*;


public class Overview1 {
	
	@Test
	public void test1() {
		System.out.println("hello");
		
	}
	
	@Test
	public void checkSome() {
		int N=10;
		IGen[] generator = new IGen[N];
		for (int i = 0; i < generator.length; i++) {
			generator[i]= new GenUni3(0.06);
		}
		for (int i = 0; i < 250; i++) {
			for (int j = 0; j < generator.length; j++) {
				System.out.printf("%f\t", generator[j].next());
			}
			System.out.printf("\n");
		}
	}

	@Test
	public void genHist() {
		ArrayList<Double> hist=new ArrayList<Double>();
		File dir = new File("doc/boerse/dax/");
//		System.out.println(dir.getAbsolutePath());
		File[] list = dir.listFiles();
		for (int i = 0; i < list.length; i++) {
			if (list[i].getName().endsWith(".txt")) {
//				System.out.println(list[i].getName());
				try {
					Scanner in = new Scanner(new FileInputStream(list[i]));
					String token = "";
					while(token!=null) {
						token = in.next();
						token = in.next("\\d+,\\d+").replace(",", ".");
						hist.add(Double.parseDouble(token));
						//System.out.println(token);
					}
					in.close();
				} catch (FileNotFoundException e) {
				} catch (NoSuchElementException e) {
				}
				hist.add(-1.0);
			}
		}

		int P= 5000; // Partition size
		int N= hist.size();
		int M= N/P+1;
		System.out.printf("static private float[] hist= new float[%d];\n", N);
		System.out.printf("static {\n");
		for (int i = 0; i < M; i++) {
			System.out.printf("initHist%d();\n", i);
		}
		System.out.printf("}\n");
		
		int n=0;
		for (int i = 0; i < M; i++) {
			System.out.printf("private static void initHist%d() {\n", i);
			System.out.printf("float[] part= new float[] {\n");
			int dest=n;
			int len=0;
			for (int p=0;p<P && n < N;) {
				for (int j = 0; j<16 && n < N && p<P; j++, n++, p++) {
					len++;
					System.out.print(1*hist.get(n)+"f, ");
				}
				System.out.printf("\n");
			}
			System.out.printf("};\n");
			System.out.printf("System.arraycopy(part, 0, hist, %d, %d);\n", dest, len);
			System.out.printf("};\n");
		}
	}

	@Test
	public void genHist2() {
		File dir = new File("doc/boerse/dax/");
//		System.out.println(dir.getAbsolutePath());
		File[] list = dir.listFiles();
		for (int i = 0; i < list.length; i++) {
			if (list[i].getName().endsWith(".txt")) {
				String name = list[i].getName().replace(".txt", "");
				name=name.substring(0, 1).toUpperCase()+name.substring(1);
				int j=0;
				System.out.printf("public HistoryStore2 add%s() {\n", name);
				System.out.printf("float[] part= new float[] {\n");
				try {
					Scanner in = new Scanner(new FileInputStream(list[i]));
					String token = "";
					while(token!=null) {
						token = in.next();
						token = in.next("\\d+,\\d+").replace(",", ".");
						System.out.print(token+"f, ");
						if (++j%16==0) {
							System.out.printf("\n");
						}
					}
					in.close();
				} catch (FileNotFoundException e) {
				} catch (NoSuchElementException e) {
				}
				System.out.printf("};\n");
				System.out.printf("add(\"%s\", part);\n", name);
				System.out.printf("return this;\n");
				System.out.printf("};\n");
			}
		}
	}

	/**
	 * 
	Date,Open,High,Low,Close,Volume,Adj Close
	2014-01-06,127.84,128.76,127.71,128.36,4800,128.36
	2014-01-03,128.05,128.40,127.18,128.00,11100,128.00
	 */
	@Test
	public void genHist3() {
		File dir = new File("doc/boerse/data/");
//		System.out.println(dir.getAbsolutePath());
		File[] list = dir.listFiles();
		for (int i = 0; i < list.length; i++) {
			if (list[i].getName().endsWith(".csv")) {
				String name = list[i].getName().replace(".csv", "").replace("-", "_");
				name=name.substring(0, 1).toUpperCase()+name.substring(1);
				ArrayList<String> file = new ArrayList<String>();
				try {
					Scanner in = new Scanner(new FileInputStream(list[i]));
					String line = in.nextLine();
					while(line!=null) {
						line = in.nextLine();
						file.add(line);
					}
					in.close();
				} catch (FileNotFoundException e) {
				} catch (NoSuchElementException e) {
				}
				Collections.sort(file);

				int j=0;
				System.out.printf("public HistoryStore2 add%s() {\n", name);
				System.out.printf("float[] part= new float[] {\n");
				for (String string : file) {
					String[] token = string.split(",");
					System.out.print(token[6]+"f, ");
					if (++j%16==0) {
						System.out.printf("\n");
					}
				}
				System.out.printf("};\n");
				System.out.printf("add(\"%s\", part);\n", name);
				System.out.printf("return this;\n");
				System.out.printf("};\n");
			}
		}
	}


	@Test
	public void checkAvg() {
		int N=1001;
		int K=11;
		IGen[] generator = new IGen[N];
		for (int i = 0; i < N; i++) {
//			generator[i]= new GenGauss1(0.019167863);
			generator[i]= new GenHist1();
		}
		for (int i = 0; i < 250; i++) {
			double[] k=new double[N];
			for (int j = 0; j < N; j++) {
				k[j]=generator[j].next();
			}
			Arrays.sort(k);
			double step = 1.0*N/(K-1);
			for (double j = 0; j < N; j+=step) {
				if (j-0.5*step<=N/2 && N/2<j+0.5*step) {
					System.out.printf("%f\t", k[N/2]);
				} else {
					System.out.printf("%f\t", k[(int)(j+0.5)]);
				}
			}
			System.out.printf("%f\n", k[N-1]);
		}
	}

	@Test
	public void getStdDev() {
		int N=10001;
		double[] k=new double[N];
		for (int i = 0; i < N; i++) {
			k[i] = HistoryStore1.next();
			//k[i] = 0.019167863*random.nextGaussian();
		}
		Arrays.sort(k);
		for (int i = 0; i < N; i++) {
			System.out.printf("%f\n", k[i]);
		}
	}

	@Test
	public void getDistrib() {
		int N=1000000;
		int P=201;
		double step=0.001;
		
		double[] v0=new double[P];
		double[] v1=new double[P];
		
		for (int i = 0; i < N; i++) {
			double c0 = 0.019167863*random.nextGaussian();
			//c0=Math.round(700*c0)/700.0;
			int idx=(int)((c0+0.5*step)/step+P/2);
			if (idx<=0) {
				v0[0]++;
			} else if (idx>=P-1) {
				v0[P-1]++;
			} else {
				v0[idx]++;
			}

			double c1 = HistoryStore1.next();
			idx=(int)((c1+0.5*step)/step+P/2);
			if (idx<=0) {
				v1[0]++;
			} else if (idx>=P-1) {
				v1[P-1]++;
			} else {
				v1[idx]++;
			}
		}
		double sum=0;
		for (int i = 0; i < P; i++) {
			if (i<P/2) {
				System.out.printf("%f\t%f\t%f\n", step*(i-P/2), v0[i]/v0[P/2], v1[i]/v1[P/2]);
			} else {
				double d=v1[i]/v1[P/2] - v1[P-i-1]/v1[P/2];
				//d=step*(i-P/2)*d;
				sum+=d;
				System.out.printf("%f\t%f\t%f\t%f\t%f\n", step*(i-P/2), v0[i]/v0[P/2], v1[i]/v1[P/2], sum, d);
			}
		}
	}

	@Test
	public void getSeperateDistribs() {
		int N=10000000;
		int P=201;
		double step=0.001;
		
		IGen[] list= new IGen[] {
				new HistoryStore2().addAllianz(),
				new HistoryStore2().addBabcock(),
				new HistoryStore2().addBasf(),
				new HistoryStore2().addBayer(),
				new HistoryStore2().addBayrhypo(),
				new HistoryStore2().addBMW(),
				new HistoryStore2().addCommerz(),
				new HistoryStore2().addContinen(),
				new HistoryStore2().addDegussa(),
				new HistoryStore2().addDeuBank(),
				new HistoryStore2().addDresdner(),
				new HistoryStore2().addEON(),
				new HistoryStore2().addHenkel(),
				new HistoryStore2().addHoechst(),
				new HistoryStore2().addKarstadt(),
				new HistoryStore2().addLinde(),
				new HistoryStore2().addLufthans(),
				new HistoryStore2().addMAN(),
				new HistoryStore2().addMannesma(),
				new HistoryStore2().addMetallge(),
				new HistoryStore2().addPreussag(),
				new HistoryStore2().addRWE(),
				new HistoryStore2().addSchering(),
				new HistoryStore2().addSiemens(),
				new HistoryStore2().addTelekom(),
				new HistoryStore2().addThyssen(),
				new HistoryStore2().addVW(),
		};
		double[][] v=new double[list.length][P];
		
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < list.length; j++) {
				double c = list[j].next();
				int idx=(int)((c+0.5*step)/step+P/2);
				if (idx<=0) {
					v[j][0]++;
				} else if (idx>=P-1) {
					v[j][P-1]++;
				} else {
					v[j][idx]++;
				}
			}
		}
		for (int i = 0; i < P; i++) {
			System.out.printf("%f", step*(i-P/2));
			for (int j = 0; j < v.length; j++) {
				System.out.printf("\t%f", v[j][i]/v[j][P/2]);
			}
			System.out.printf("\n");
		}
	}

	@Test
	public void getSeperateDistribsEach() {
		int N=10000000;
		int P=201;
		double step=0.001;
		
		IGen[] list= new IGen[] {
				new HistoryStore2().addAllianz(),
				new HistoryStore2().addAllianz_2014_01_06(),
				new HistoryStore2().addMicrosoft_2013_11_19(),
				new HistoryStore2().addMicrosoft_2000_07_14(),
//				new HistoryStore2().addBabcock(),
//				new HistoryStore2().addBasf(),
//				new HistoryStore2().addBayer(),
//				new HistoryStore2().addBayrhypo(),
//				new HistoryStore2().addBMW(),
//				new HistoryStore2().addCommerz(),
//				new HistoryStore2().addContinen(),
//				new HistoryStore2().addDegussa(),
//				new HistoryStore2().addDeuBank(),
//				new HistoryStore2().addDresdner(),
//				new HistoryStore2().addEON(),
//				new HistoryStore2().addHenkel(),
//				new HistoryStore2().addHoechst(),
//				new HistoryStore2().addKarstadt(),
//				new HistoryStore2().addLinde(),
//				new HistoryStore2().addLufthans(),
//				new HistoryStore2().addMAN(),
//				new HistoryStore2().addMannesma(),
//				new HistoryStore2().addMetallge(),
//				new HistoryStore2().addPreussag(),
//				new HistoryStore2().addRWE(),
//				new HistoryStore2().addSchering(),
//				new HistoryStore2().addSiemens(),
//				new HistoryStore2().addTelekom(),
//				new HistoryStore2().addThyssen(),
//				new HistoryStore2().addVW(),
		};
		double[][] v=new double[list.length][P];
		
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < list.length; j++) {
				double c = list[j].next();
				int idx=(int)((c+0.5*step)/step+P/2);
				if (idx<=0) {
					v[j][0]++;
				} else if (idx>=P-1) {
					v[j][P-1]++;
				} else {
					v[j][idx]++;
				}
			}
		}

		for (int j = 0; j < v.length; j++) {
			System.out.printf("%s\t%s\t", list[j].toString(), list[j].toString());
		}
		System.out.printf("\n");
		for (int i = 0; i < P; i++) {
			for (int j = 0; j < v.length; j++) {
				System.out.printf("%f\t%f\t", step*(i-P/2), v[j][i]/v[j][P/2]);
			}
			System.out.printf("\n");
		}
	}

	@Test
	public void getMultipleOrders() {
		int D=250;
		HistoryStore2[] list= new HistoryStore2[] {
				new HistoryStore2().addMicrosoft(D),
				new HistoryStore2().addMicrosoft(D),
				new HistoryStore2().addMicrosoft(D),
				new HistoryStore2().addMicrosoft(D),
				new HistoryStore2().addMicrosoft(D),
				new HistoryStore2().addMicrosoft(D),
				new HistoryStore2().addMicrosoft(D),
				new HistoryStore2().addMicrosoft(D),
				new HistoryStore2().addMicrosoft(D),
				new HistoryStore2().addMicrosoft(D),
		};
		int N=list.length;

		double[] k=new double[N];
		double s0=0;
		for (int i = 0; i < D; i++) {
			double s = list[0].read();
			if (i==0) {
				s0=s;
				for (int j = 0; j < N; j++) {
					k[j]=1;
				}
			}
			System.out.printf("%f", s/s0);
			for (int j = 0; j < N; j++) {
				System.out.printf("\t%f", k[j]);
				k[j]=(1+list[j].next(10, 0.1))*k[j];
			}
			System.out.printf("\n");
		}
		System.out.printf("%d\n", HistoryStore2.missed);
	}
}
