package eu.icd.p999.genetic.world1;

import static lib.Lib.rand;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Random;


public class Test1 implements Phenotype {
	private static class Stat {

		public static int count;
		public double kurs;
		public double konto;
		public double depot;
		public double total;
		public double trade;
		
	}
	private static final int DAYS = 250+250;
	
	private static class Tri {
		int count;
		double fitness;
		double[] kurs=new double[DAYS];
		double[][] avgs=new double[DAYS][256];

		Tri() {
			gen0();
			for (int i = 0; i < avgs.length; i++) {
				for (int j = 0; j < avgs[i].length; j++) {
					double sum=0;
					int count=0;
					for (int k = Math.max(i-j, 0); k < i; k++) {
						sum+=kurs[k];
						count++;
					}
					avgs[i][j]=sum/count;
				}
			}
		}

		private void gen0() {
			kurs[0] = 1000;

			for (int i = 1; i < kurs.length; i++) {
				double change=2*(random.nextDouble()-0.5); // in [-1, 1]
				if (change>=0) {
					change=0.06*change*kurs[i-1];
				} else {
					change=(0.06/1.00)*change*kurs[i-1];
				}
				if (true || kurs[i-1] + change >= 0) {
					kurs[i] = kurs[i-1] + change;
				} else {
					kurs[i] = kurs[i-1];
				}
			}
		}

		private void gen1() {
			kurs[0] = 1000;

			for (int i = 1; i < kurs.length; i++) {
//				double change=2*(random.nextDouble()-0.5); // in [-1, 1]
//				change=0.06*change*kurs[i-1];
				double change=random.nextGaussian();
				if (change>=0) {
					change=0.06*change*kurs[i-1];
				} else {
					change=(0.06/1.00)*change*kurs[i-1];
				}
				if (true || kurs[i-1] + change >= 0) {
					kurs[i] = kurs[i-1] + change;
				} else {
					kurs[i] = kurs[i-1];
				}
			}
		}
		private void gen2() {
			double start = 1000;
			double end=start+500*random.nextGaussian();
			if (end<0) end=0;
			kurs[0]=start;
			kurs[DAYS-1]=end;
			gen2(0, DAYS-1);
		}

		private void gen2(int a, int b) {
			if (a<b-1) {
				int m=(a+b)/2;
				double middle=(kurs[a]+kurs[b])/2;
				middle=middle+0.06*(kurs[a]-kurs[b])*random.nextGaussian();
				if (middle<0) middle=0;
				kurs[m]=middle;
				gen2(a, m);
				gen2(m, b);
			}
			
		}
	}

	private static final int TRIES = 101;
	private static Random random = new Random();
	private static Tri[] tries;

	static void generate() {
		if (tries==null) {
			tries=new Tri[TRIES];
			for (int t = 0; t < tries.length; t++) {
				tries[t]= new Tri();
			}
		} else {
			double sum=0;
			for (int t = 0; t < tries.length; t++) {
				tries[t].fitness/=tries[t].count;
				sum+=tries[t].fitness;
			}
			for (int t = 0; t < tries.length; t++) {
				if (true || tries[t].fitness/sum>random.nextDouble()/tries.length) {
					tries[t]= new Tri();
				}
				tries[t].fitness=0;
				tries[t].count=0;
			}
		}
	}

	public static void main(String[] args) {
		new Test1().execute();
	}

	private int generation;
	private static Stat[] stat=new Stat[DAYS];
	
	static {
		for (int i = 250; i < DAYS; i++) {
			stat[i]=new Stat();
		}
	}

	private void execute() {
		Population population = new Population(100, 3*100, 0.1, 0.7);
		generate();
		population.calcFitness(this);
		for (generation = 0; generation<100000; generation++) {
			generate();
			population.nextGeneration();
			population.calcFitness(this);
			if (generation%40==0) {
				int oldest=0;
				for (int i = 0; i < population.population.length; i++) {
					if (population.population[i].id<population.population[oldest].id) {
						oldest=i;
					}
				}
				System.out.println(toString(population.population[oldest]));
				trade(population.population[oldest], 0, true);
			}
			double sum=0;
			for (int i = 0; i < population.population.length; i++) {
				sum+=population.population[i].getFitness();
			}
			System.out.printf("%d\t%5.3f\t%5.3f\n", generation, population.getBestFitness(), sum/population.population.length);
			
		}
	}

	@Override
	public void makePhenotype(Genotype genotype) {		
		double fsum=0;
		for (int tri = 0; tri < TRIES; tri++) {
			double fitness = trade(genotype, tri, false);
			fsum+=fitness;
		}
		genotype.setFitness(fsum/TRIES);
	}

	private double trade(Genotype genotype, int tri, boolean mode) {
		final double k0=100*tries[tri].kurs[250];
		double weigth=1; //Math.min(0.5+0.5*generation/400, 1.0);
		double konto0=weigth    *k0;
		double depot0=(1-weigth)*k0/tries[tri].kurs[250];
		double konto=konto0;
		double depot=depot0;
		double depotMax=konto0/tries[tri].kurs[250];
		for (int day = 250; day < tries[tri].avgs.length; day++) {
			double trade=0;
			int count=0;
			for (int i = 0; i < genotype.gene.length; i+=3) {
				int a=genotype.gene[i];
				int b=genotype.gene[i+1];
				int t=genotype.gene[i+2];
				// two avg lines are crossing?
				if (       tries[tri].avgs[day-1][a] > tries[tri].avgs[day-1][b] && tries[tri].avgs[day][a] <= tries[tri].avgs[day][b] ) {
					trade+=t;
					count++;
				} else if (tries[tri].avgs[day-1][a] < tries[tri].avgs[day-1][b] && tries[tri].avgs[day][a] >= tries[tri].avgs[day][b] ) {
					trade-=t;
					count++;
				}
			}
			if (count>0) {
				trade=trade/count; // in [-255 , 255]
				if (trade>128) {
					trade=(trade-128)/128; // in [0, 1]
					double transfer=konto*trade;
					double cost=0.015*transfer;
					konto-=transfer-cost;
					depot+=transfer/tries[tri].kurs[day];
				} else if (trade<-128) {
					trade=(trade+128)/128; // in [-1, 0]
					double transfer=-depot*trade;
					double cost=0.015*transfer*tries[tri].kurs[day];
					depot-=transfer;
					konto+=transfer*tries[tri].kurs[day]-cost;
				} else {
					trade=0;
				}
			}
			if (mode) {
				System.out.printf("%5.2f\t%10.2f\t%10.2f\t%10.2f\t%.2f\n", tries[tri].kurs[day]/tries[tri].kurs[250], konto/konto0, depot/depotMax, (konto+depot*tries[tri].kurs[day])/k0, trade);
				//System.out.printf("%5.2f\t%10.2f\t%10.2f\t%10.2f\t%.2f\n", stat[day].kurs/Stat.count, stat[day].konto/Stat.count, stat[day].depot/Stat.count, stat[day].total/Stat.count, stat[day].trade/Stat.count);
			} else { 
				stat[day].kurs+=tries[tri].kurs[day]/tries[tri].kurs[250];
				stat[day].konto+=konto/konto0;
				stat[day].depot+=depot/depotMax;
				stat[day].total+=(konto+depot*tries[tri].kurs[day])/k0;
				stat[day].trade+=trade;
				if (day==300) Stat.count++;
			}
		}
		double fitness=(konto+depot*tries[tri].kurs[tries[tri].avgs.length-1])/k0;
		tries[tri].fitness+=fitness;
		tries[tri].count++;
		return fitness;
	}

	public String toString(Genotype genotype) {
		String res="id="+genotype.id+" / "+Genotype.idSource+"\n";
		for (int i = 0; i < genotype.gene.length; i+=3) {
			int a=genotype.gene[i];
			int b=genotype.gene[i+1];
			double trade=genotype.gene[i+2]; // in [0 , 255]
			if (trade>128+64) {
				trade=(trade-(128+64))/64; // in [0, 1]
			} else if (trade<64) {
				trade=(trade-64)/64; // in [-1, 0]
			} else {
				trade=0;
			}
			res+=new Formatter().format("%2d: (%3d, %3d) -> %.2f\n", i/3, a, b, trade).toString();
		}
		return res;
	}

}
