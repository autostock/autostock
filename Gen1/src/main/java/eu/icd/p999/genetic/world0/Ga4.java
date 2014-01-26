package eu.icd.p999.genetic.world0;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Formatter;
import java.util.Scanner;

import lib.Lib;
import lib.Reference;
import static lib.Lib.*;
import static java.lang.Math.*;

public class Ga4 {
	 
	final private static int MINBLOCK  = 100;
	final private static int FAKT  = 3;
	final private static int GENERATIONS  = 10000;
	final private static int LOOPS  = 10000;

	final private static int POPSIZE  = 200;
	final private static int ELITE  = 50;
	final private static int PELITE  = 50;
	final private static int COEV  = 5;
	final private static int ABSTRAKT  = 60;
	//final private static int ABSTRAKT  = 50;

	//final private static int GENLEN  = 20;
	final private static int REGRESSION  = 1;
	final private static int MAX  = 50;
	 
	final private static int STEPS  = 300;
	final private static int STATES  = 300;

	final private static int BUY1  = 0;
	final private static int SELL1  = 1;
	final private static double AKTIE0  = 1000.0;

	final private static int P0  = 2*STEPS;
	final private static int DATALEN  = 1000;
	final private static int TAPELEN  = P0+DATALEN;
	final private static double LIMIT  = 0.9;
	final private static double BMB  = 0.07;
//	final private static int INFO if(info) 

	private static int rnd(int x) { return rand()%(x); };
	private static int rndi(int l, int r) { return l+rnd((r)-(l)); }
	private static int rndb(int l, int r) { return ((base+rnd(2*PELITE))%ELITE); }


//	final private static double PMUTATION  = 0.00008;
//	final private static double PMUTATION  =((1.0*POPSIZE)/4000000.0);
	final private static double PMUTATION  = 0.04;

	final private static double PXOVER  = 0.025;


	final private static double TKOSTEN  = 0.015;



	final private static int STRATEGIES  = 1;
	final private static int STATE0  = STRATEGIES;

	final private static int SYMBOLS =(1+3*5);
	final private static int BSTATES  = 20;
	final private static int GENLEN  = ((BSTATES*SYMBOLS)+STRATEGIES);
	final private static int Limit	= (GENLEN*(GENLEN-1)/2);


	static private double	limit=LIMIT;
	static private double	tkosten=TKOSTEN;
	static private double	bmb=BMB;
	static private int		mach= -1;
	static private int		base= 0;

	static private String	ReadData=null;
	static private int		stopgen=0;
	static private int		info=0;
	static private int		count=0;
	static private int  starttime=0;



	static class testset {
		double[] tape= new double[TAPELEN];
		double min, max;
		int anzgew;
		int code;
	} 
	static private testset[] testset = new testset[ABSTRAKT];
	
	static {
		for (int i = 0; i < testset.length; i++) {
			testset[i]=new testset();
		}
	}


	final private static int MAXHASH  = 9001;

	static class HashTab {
		double gewinn,fitness;
		int anzgew;
		int tcode, mcode;
	};
	static private HashTab[] HashTab = new HashTab [MAXHASH];
	static {
		for (int i = 0; i < HashTab.length; i++) {
			HashTab[i]=new HashTab();
		}
	}

	static private double[] tape;

	static private double[] kurs=new double[DATALEN];
	static private int[] used = new int[BSTATES+1];

	static class machine {
		int[] dist= new int[SYMBOLS]; 
		double[] proz1= new double[SYMBOLS]; 
		double[] proz2= new double[SYMBOLS]; 
		double handel;
	}
	static private machine[] trans =new machine[STATES];
	static {
		for (int i = 0; i < trans.length; i++) {
			trans[i]=new machine();
		}
	}


	//typedef byte genetype[GENLEN];

	static class genotype {
		int[] gene= new int[GENLEN];
		double fitness, rfitness, cfitness, gewinn;
		int sell, buy,hold;
		int code;
	};

	static private genotype[] population= new genotype[POPSIZE+200];
	static {
		for (int i = 0; i < population.length; i++) {
			population[i]=new genotype();
		}
	}

	final private static int ACCU =(POPSIZE+100);
	
	static private int s0,s1,popsize;
	static private int[] katstat= new int[FAKT];


	static private int id;
	static private int datamax;
	private static int tapemax;
	private static int loop;
	private static int generation;
	private static double startkapital, ravg;
	private static double avgbest;
	private static double pmutation=PMUTATION;
	private static double pxover=PXOVER;
	private static int steps=STEPS;

	private static PrintStream statistik=System.out;
	private static PrintStream export;

	private static int tcode=0;
	private static int mcode=200;


	static private void readcmd(int a, int b) {
		Scanner f = fopen_r("cmd.dat","r");
		if (f!= null) {
			Reference ref;
			ref(ref = fscanf(f, "%d %d", a,b), a = Integer.parseInt(ref.values[0]), b = Integer.parseInt(ref.values[1]));
		    fclose(f);
		};
	};

	static private String getfn(int id, String suffix) {
		return new Formatter().format("run%03d.%s", id, suffix).toString();
	};

	static private int scale(int len) {
		return(len+1);
	}

	static private int getHash(int t, int m) {
	 int i;

		t=testset[t].code;
		m=population[m].code;
		i=(201*t+m) % MAXHASH;
		if (m==0) {
			return(0); 
		}
//		printf("getHash(%d,%d)\n", t,m);
		if (HashTab[i].tcode==t && HashTab[i].mcode==m) return(i+1);
		return(-i-1); 
	}




	static private int generate()
	{
	int i,max;
	double k0,k,ko,ku,d;

		max=1*250;
		k0=AKTIE0;
		ku=(1.0-limit)*k0;
		ko=(1.0+limit)*k0;
		k=k0;

		for (i=0; i<max; i++) {
			kurs[i]=k;
			d=rand()%1001 / 1000.0;
			d=k0*bmb*d;
			if (rand()%2==0) d= -d;
			if (k+d<=ku || k+d>=ko) d= -d;
			k+=d;
		};
		
		return (scale(max));
	}

	static private int symbolize(int datamax) {
	double a,d,best;
	int i,j,k,s;

		for(i=0;i<P0; i++) tape[i]=AKTIE0;

		for(j=0; j<datamax; j++,i++) {
			tape[i]=kurs[j];
		};
		return(i);
	}

	static private void produce(int seed) {

//		if (seed) srand(seed);

		datamax=generate();
		tapemax=symbolize(datamax);

		startkapital=100.0*kurs[0];
//		if (seed) srand(seed);


//		printf("datamax=%d; tapemax=%d\n", datamax, tapemax);
	}

//	#if 0
//	void maketestpool(int seed) {
//	static int t=0;
//	static int tlast= -1;
//	int i,r;
//	double max;
//	int gewmax;
//
//	if (generation==0 && t!=0) {
//	  t=0;
//	  tlast= -1;
//	};
//	if (t==0) {
//	    tape= testset[0].tape;
//		produce(seed);
//		for ( t=1; t<ABSTRAKT; t++) {
//		  tape= testset[t].tape;
//		  testset[t].min= +10000000;
//		  testset[t].max= -10000000;
//		  testset[t].anzgew=0;
//		  testset[t].code=tcode++;
//		  produce(0);
//		};
//	} else {
//	#if 0
//		gewmax=testset[0].anzgew;
//		i=0;
//		for ( t=1; t<ABSTRAKT; t++) if ( gewmax<testset[t].anzgew) {
//			gewmax=testset[t].anzgew;
//			i=t;
//		};
////		r=rnd(ABSTRAKT);
////		if (i!=tlast && testset[i].anzgew<30.0*testset[r].anzgew) i=r;
//	    printf(" maketesttape %2d %5d	%5d	", i, gewmax, testset[i].anzgew);
//	#else
//		max=testset[0].max;
//		i=0;
//		for ( t=1; t<ABSTRAKT; t++) if ( max<testset[t].max) {
//			max=testset[t].max;
//			i=t;
//		};
//		r=rnd(ABSTRAKT);
//		if (i!=tlast && testset[i].max<4.0*testset[r].max) i=r;
//	    printf(" maketesttape %2d %10.2f	%10.2f	", i, max, testset[i].max);
//	#endif
//	    tape= testset[i].tape;
//	    testset[i].min= +10000000;
//		testset[i].max= -10000000;
//	    testset[i].anzgew=0;
//		testset[i].code=tcode++;
//		tlast=i;
//
//		produce(seed);
//	}
//	}
//
//	#else

	static private int t, round=0;
	static private int tlast= -1;

	static private void maketestpool(int seed) {
	int i,r,s;
	double max;

	if (t==0) {
	    tape= testset[0].tape;
		produce(seed);
		for ( t=1; t<ABSTRAKT; t++) {
		  tape= testset[t].tape;
		  testset[t].min= +10000000;
		  testset[t].max= -10000000;
		  testset[t].code=tcode++;
		  produce(0);
		};
	} else {
		max=testset[0].max;
		i=0;
		for ( t=1; t<ABSTRAKT; t++) if ( max<testset[t].max) {
			max=testset[t].max;
			i=t;
		};
		s=round;
		round=(round+1)%ABSTRAKT;
		r=round;
//		r=rnd(ABSTRAKT);
		if (i!=tlast && testset[i].max<4.0*testset[r].max) { 
			i=r;
		} else {
			round=s;
		};
		printf(" maketesttape %2d %10.2f	%10.2f	", i, max, testset[i].max);
	    tape= testset[i].tape;
	    testset[i].min= +10000000;
		testset[i].max= -10000000;
		testset[i].code=tcode++;
		tlast=i;

		produce(seed);
	}
	}
//	#endif


	final private static int MAXERR  = 1;
	
	static private double startmachine(int steps, int pos) {
	int state, symbol, count=0, hit,bh;
	double akt,proz1,proz2,proz,p0, handel= 0.0;

		bh=BSTATES;
		p0=tape[P0];
		akt=tape[pos]/p0;
		for(state=0; state<BSTATES; state++) {
			for(symbol=0,hit=0; symbol<SYMBOLS/3&& symbol-hit<=MAXERR; symbol++) {
				proz=tape[pos-trans[state].dist[symbol]]/p0;
				proz1=akt+trans[state].proz1[symbol];
				proz2=akt+trans[state].proz2[symbol];
				if (proz1<=proz && proz<=proz2) hit++; 
				else if (symbol>0) {
					//Bedingung mit größter Chance zum abbruch nach vorn
					trans[BSTATES].dist[0]=trans[state].dist[symbol];
					trans[BSTATES].proz1[0]=trans[state].proz1[symbol];
					trans[BSTATES].proz2[0]=trans[state].proz2[symbol];
					trans[state].dist[symbol]=trans[state].dist[symbol-1];
					trans[state].proz1[symbol]=trans[state].proz1[symbol-1];
					trans[state].proz2[symbol]=trans[state].proz2[symbol-1];
					trans[state].dist[symbol-1]=trans[BSTATES].dist[0];
					trans[state].proz1[symbol-1]=trans[BSTATES].proz1[0];
					trans[state].proz2[symbol-1]=trans[BSTATES].proz2[0];
				};
			}
//			printf("%f %f\n",handel, trans[state].handel);
//	#if 1
			if (symbol-hit<=MAXERR) {
				used[state]++;
//				hit=hit*(state+1)/symbol;
				hit=hit*hit*(BSTATES-state)*(BSTATES-state);
				handel+=(trans[state].handel)*(hit);
				count+=hit;
			}
//	#elif 0
//			if (fabs(handel)<=fabs(trans[state].handel)) {
//				handel=trans[state].handel;
//				bh=state;
//			}
//	#elif 0
//
//			if (symbol==SYMBOLS/3) {
//	/*
//				handel+=trans[state].handel;			
//				count++;
//		        INFO fprintf(export, "%3d %7.2f %2d	", pos, akt, state);
//			for(symbol=0; symbol<SYMBOLS/3; symbol++) {
//				i=pos-trans[state].dist[symbol];
//				last=tape[i];
//				proz1=trans[state].proz1[symbol];
//				proz2=trans[state].proz2[symbol];
//				proz=(last-akt)/tape[P0];
//				INFO fprintf(export, " %d->%.2f [%.2f, %.2f, %.2f];", i, last, proz1, proz, proz2);
//			};
//	*/
//				used[state]++;
//				return (trans[state].handel);
//			}
//	#endif
	loop2: ;
		}
//		used[bh]++;
		if (count!=0) return(handel/count); else {used[BSTATES]++; return(0.0);}
	}

	static private void codemachine(int m) {
	int state,state1, symbol,s,i,p1,p2;

		for(state=0; state<BSTATES; state++) {
			for(symbol=0; symbol<SYMBOLS-1; symbol++) {
				s=population[m].gene[state*SYMBOLS+symbol];

				switch (symbol%3) {
				case 0 : {
					trans[state].dist[symbol/3]=s%200+1;
					p1=population[m].gene[state*SYMBOLS+symbol+1];
					p2=population[m].gene[state*SYMBOLS+symbol+2];
					if (p1<=p2) {
						trans[state].proz1[symbol/3]=((p1/256.0)-0.5)*4.0;
						trans[state].proz2[symbol/3]=((p2/256.0)-0.5)*4.0;
					} else {
						trans[state].proz1[symbol/3]=((p2/256.0)-0.5)*4.0;
						trans[state].proz2[symbol/3]=((p1/256.0)-0.5)*4.0;
					}
				};
				};
			};
			s=population[m].gene[state*SYMBOLS+symbol];
			trans[state].handel=((s/128.0)-1.0);
		};
	}


	static private void printmachine(PrintStream out, int num, double fitness) {
	int state,state1, symbol,i, pc=0;

		fprintf(out,"beg_machine: %d\ntyp: 204 genlen: %d states: %d strat: %d\nsteps:	%d\n", num, 
			GENLEN, BSTATES, STRATEGIES, STEPS);
		fprintf(out,"generation:	%d\n", generation);
		fprintf(out,"indiv:\n");

		for(i=0; i<GENLEN; i++) {
			if (i%16==0) fprintf(out, "%2d:", i/16);
			fprintf(out, " %3d", population[num].gene[i]);
			if(i%16==15) fprintf(out, "\n");
		};
		fprintf(out, "\n");

		fprintf(out, "fitness: %.2f\n", fitness);
		fprintf(out,"end_machine\n\n");
	}

	static private double gfitness;

	static private double evalmachine(int m, int mode, double expected) {
	double konto,k, gewinn;
	int summe,s, state,i;
	int sell=0,buy=0, hold=0;
	double lbest=0.0, handel, g;

		info=mode;

		konto=0.5*startkapital;
		summe=(int)(0.5*(startkapital/AKTIE0));

		if (mode==1) { 
			export=fopen(getfn(id, "txt"), "w");
			fprintf(export, "#%d	#%d\n", id, m);
		};
		for(i=0; i<datamax-1; i++) {
			handel=startmachine(steps, P0+i);
//		    gewinn=(konto+summe*tape[P0+i]);
//			if (m==1) printf("mac: %d[%d] %f %f %f\n", m, i, handel, startkapital, gewinn);

			s=0;
			if (handel<0.0) {
					s=(int)floor(min(-handel, 1.0-tkosten)*konto/tape[P0+i]);
					k= s*tape[P0+i];
					k=(1.0+tkosten)*k; // Kosten der Transkation
					konto+= -k;
					summe+= s;
			} else if ((handel>0.0) && (summe>0)) {
					// Verkaufen
					// strategy[]>0 
					// s <0
					s= (int)-ceil(handel*summe);
//					if(tape[P0+i+1]<tape[P0+i])
//						k= -s*tape[P0+i+1];
//					else
						k= -s*tape[P0+i];

					k=(1.0-tkosten)*k;
					konto+= k;
					summe+= s;
			};
			if(s<0) { sell++;} else {if (s>0) { buy++;} else hold++; };
			if (mode==2) {
				if (s!=0) 
					printf("%5d: %8.2f	%8.2f	%5d		->%8.2f\n", i, tape[P0+i], konto, summe, konto+summe*tape[P0+i]);
				else
					printf("%5d: %8.2f\n", i, tape[P0+i], konto);
			};
			if (mode==1) {
				fprintf(export, "%d %6.4f %6.4f %6.4f\n", i, tape[P0+i]/tape[P0+0], (konto+summe*tape[P0+i])/startkapital, s/10.0+1);
			};
		};

		population[m].sell=sell;
		population[m].buy=buy;
		population[m].hold=hold;
		
		gewinn=(konto+summe*tape[P0+i-1]);

//		if (gewinn<0.8*startkapital) gewinn*= 0.4;	

//		if (gewinn>1.5*ravg) gewinn=ravg; // Zuviel Glück ==>Spezialist für DIESE Kurve

//		if( population[m].sell+	population[m].buy==0 /*&& generation<200*/)
//			gewinn*=0.95;

		population[m].gewinn+=gewinn;

	/*
		g=(gewinn/startkapital) - (tape[P0+i-1]/tape[P0+0]);
	//printf("%d %10g %10g %10g\n",m, gewinn/startkapital,tape[P0+i-1], g);

		population[m].fitness=g;
		population[m].fitness=(100.0*population[m].fitness+g)/101.0;
	/*
	*/
		g=gewinn - expected;
		if (g>0.0) {
			g=sqrt(g);
		} else {
			g= -0.5*sqrt(-g); /* 0.5 für mutige Zocker */
		};
		g=g + expected;

		population[m].fitness+=g;
		gfitness=g;


		if (mode==1) fclose(export);

		return(gewinn);
	}



	/*
	**
	*/

	static private double fitness(int m) {
	int a=10, b=10;
	int k,t,h;
	double f, f1;


		codemachine(m);
		population[m].fitness=0.0;
		population[m].gewinn=0.0;

//		if (m==0) readcmd(&a, &b);
		for ( t=0; t<ABSTRAKT; t++) {
		  h=getHash(t, m);
//		  h= 0;
		  if (h<=0) {
		    tape=testset[t].tape;
		    f=evalmachine(m, (t==a&&m==b)?1:0, startkapital);
			if (h<0) {
			  HashTab[-h-1].fitness=gfitness;
		      HashTab[-h-1].gewinn=f;
			  HashTab[-h-1].tcode=testset[t].code;
			  HashTab[-h-1].anzgew=testset[t].anzgew;
			  HashTab[-h-1].mcode=population[m].code;
			};
		  } else {
//		    tape=testset[t].tape;
//		    f1=evalmachine(m, (t==a&&m==b), startkapital);
			  f=HashTab[h-1].gewinn;
			  population[m].fitness+=HashTab[h-1].fitness;
		      population[m].gewinn+=HashTab[h-1].gewinn;
	/*
			  if (f1!=f) {
				  printf("getHash(%d:%d,%d:%d)->%.2f	%.2f\n", t,HashTab[h-1].tcode,m,HashTab[h-1].mcode, f1, f);
			  }// else printf("hcount %d\n", hcount++);
	*/
		  };

		  if (testset[t].min>f) testset[t].min=f;
		  if (testset[t].max<f) testset[t].max=f;	  
		  if (startkapital  <f) testset[t].anzgew++;	  
//		  testset[t].max+=f;	  
		};
		population[m].fitness/=t;
		population[m].gewinn/=t;
		return(population[m].gewinn);
	}

	static private void quicksort(int l, int r) {
	int i,j;
	double v;

	  if (l<r) {
		  v=population[l].fitness; 

		  j=l;
		  for(i=l+1; i<=r; i++) 
			  if (  population[i].fitness> v) {
				j++;
			    population[popsize+1]=population[i];
			    population[i]=population[j];
			    population[j]=population[popsize+1];
			  };
		  population[popsize+1]=population[l];
		  population[l]=population[j];
		  population[j]=population[popsize+1];

			quicksort(l,   j-1);
			quicksort(j+1, r);
	  };
	};

	static private void init(int l, int r, int x) {
	int i,j,k,p;

		k=(r-l)/MINBLOCK;
		katstat[k]++;
		p=rnd(GENLEN);
		for (i=l; i<r; i++) {
			for (j=0; j<GENLEN/x; j++) {
			  population[i].gene[(p+j)%GENLEN]=rnd(256);
			};
			population[i].code=0;
			fitness(i);
			population[i].code=i+1;
		};
	};

	static private void mutate1() {
	int i;

	  population[ACCU].gene[rnd(GENLEN)]=rnd(256);
	};

	static private void mutate() {
	int i;

		for (i=0; i<max(GENLEN/5,1); i++) {
			  population[ACCU].gene[rnd(GENLEN)]=rnd(256);
		};
	};


	static private void xover(int l, int r) {
	int i,j,p1,p2,p;
	int PARENTNO  = 4;

			p1=rnd(GENLEN);
			p2=rnd(GENLEN);
			for (i=0; i<PARENTNO; i++) {
			  p=rndb(l,r);
			  for (j=0;j<GENLEN/(PARENTNO+2); j++) { 
				population[ACCU].gene[p1]=population[p].gene[p2];
				p1=(p1+1)%GENLEN;
				p2=(p2+1)%GENLEN;
			  };
			};
	};

	static private void xover1(int l, int r) {
	int i,j,p1,p2,p;
	int PARENTNO  = 2;

			p1=rnd(GENLEN);
			p2=rnd(GENLEN);
			for (i=0; i<PARENTNO; i++) {
			  p=rndb(l,r);
			  for (j=0;j<GENLEN/(PARENTNO+35); j++) { 
				population[ACCU].gene[p1]=population[p].gene[p2];
				p1=(p1+1)%GENLEN;
				p2=(p2+1)%GENLEN;
			  };
			};
	};


	static private double insertInd(int l) {
	int i,j, pmin, dmin,d, tmin, t, p,k;
	double min;


	/*
					for (i=0; i<ELITE+5; i++) {
						printf("%3d	",i);
						for (j=0; j<GENLEN; j++) printf("%2u ", population[i].gene[j]);
						printf("->%d\n",population[i].fitness);
					};
			scanf("%c",&i);
	*/
			tmin=GENLEN+1;
			dmin=GENLEN*256*256;
			pmin=base;
//				best=population[pmax].fitness;
			for (k=0; k<2*PELITE; k++){
			  p=(base+k)%ELITE;
//	#if 1
			  if (population[p].gewinn<=1.0*population[pmin].gewinn) pmin=p;
//	#else
//			  if (population[p].gewinn<=1.0*population[l].gewinn) {
//				t=0; d=0;
//	#if 1 
//				  for (j=0; j<GENLEN && t<tmin+2; j++) {
//					  // != : Anz ungl wird minimiert ==> (111): (112)#1 eher ersetzt als(123)#2 
//					  // == : Anz gl   wird minimiert ==> (111): (123)#1 eher ersetzt als(112)#2 ==> viel kleine Änderungen
//					  if (population[p].gene[j]==population[l].gene[j]) {
//						  t++;
////						  d+=GENLEN*256+abs(population[p].gene[j]-population[l].gene[j]);
//						  d++;
//					  }
//				  };
//				if  (	dmin>d 
//					|| (dmin==d && population[p].gewinn<population[pmin].gewinn)
//				    ) 
//				{
////					printf("d(p[%d])=%d\n", p, d);
//					tmin=t; dmin=d; pmin=p; }
//
//	#elif 0 
//				  for (j=0; j<GENLEN && t<tmin+2; j++) {
//					  if (population[p].gene[j]!=population[l].gene[j]) {
//						  t++;
//						  d+=GENLEN*256+abs(population[p].gene[j]-population[l].gene[j]);
//					  }
//				  };
//				if  (	dmin>d 
//					|| (dmin==d && population[p].gewinn<population[pmin].gewinn)
//				    ) 
//				{tmin=t; dmin=d; pmin=p; }
//	#elif 0
//				  for (j=0; j<GENLEN && t<tmin+2; j++) {
//					  if (population[p].gene[j]!=population[l].gene[j]) {
//						  t++;
//					  }
//				  };
//				if  (	tmin>t 
//					|| (tmin==t && population[p].gewinn<population[pmin].gewinn)
//				    ) 
//				{tmin=t; dmin=d; pmin=p; }
//	#endif
//			  };
//	#endif
			};
			if (population[pmin].gewinn<1.0*population[l].gewinn) {
					min=population[l].gewinn-population[pmin].gewinn;
					if (population[pmin].code<mcode-10) p=1; else p=0;
					population[pmin]=population[l];
					population[pmin].code=mcode++;
					printf("newmach %3d/%d-> %10d /%10.2f %10.2f", pmin, p, dmin, population[pmin].gewinn, min);
//					best=population[pmax].fitness;
	/*
					printf("%d <- %d\n", pmax, l);
					for (i=0; i<ELITE; i++) {
						printf("%3d	",i);
						for (j=0; j<GENLEN; j++) if (j%4==0) printf("%2X", population[i].gene[j]);
						printf("->%10.0f\n",population[i].fitness);
					};
			scanf("%c",&i);
			        quicksort(0,ELITE-1);
	*/

					return(min);
			} 
		return(0.0);
	};

	static private double min=0.0;
	static private int gbest= -1;
	static private int kcount=40;
	final static private int MAXPROB  = 4;
	static private int[] prob={10,10,10,10};

	//	double FAKT1  = 1.01;
	final static private double FAKT1  = 1.001;

	static private int eliteGA(double gew, int l, int r, int katas) {
	int c=20,x=20;
	int tm;
	int j,i,k;
	int f;
	int q=0;
	int p,pmax, pmin, pbest;
	double max; double best= -100000;

	if (generation==0&&kcount!=40) {
	gbest= -1;
	kcount=40;
	 for (i=0; i<MAXPROB; i++) prob[i]=10;
	min=0.0;
	};
	//if (count==0) kcount=0;
//		if (katas==1&&(r-l)<=MINBLOCK) init(l,r,3); //katastrophe
		
		if (gew<100000.0) gew=100000.0;
		max=gew;
		min=(2*min+gew)/3.0;

		i=0;
		p=rndb(0,ELITE);
		population[l]=population[p];
		for (j=0,t=0; (max<FAKT1*gew || j<5*600) && j<20*600; j++,t++) {
			p=rndb(0,ELITE);
			population[ACCU]=population[p];
			population[ACCU].code=0;
			r=rnd(kcount); 
			for (k=0; k<MAXPROB&&r>=0; k++) r-=prob[k];
			k--;
			switch (k) {
			case 0:		
				xover(0, ELITE); 
				fitness(ACCU);
				break;
			case 1:
				mutate1();
				fitness(ACCU);
				break;
			case 2:
				mutate();
				fitness(ACCU);
				break;
			case 3:
				xover1(0, ELITE);
				fitness(ACCU);
				break;
			default:
				printf("problem %d %d\n",kcount, k); System.exit(99);
			};

//			if ( 0.95*population[l].gewinn<population[ACCU].gewinn && 
			if ( min<population[ACCU].gewinn && 
				(best=insertInd(ACCU))>0.0
				) {
				max+=(best/ELITE);
				printf(" Max=%.2f<%.2f %d\n", max, FAKT1*gew, j);
				if ( min<population[ACCU].gewinn) {
//					population[l]=population[ACCU];
					min=population[ACCU].gewinn; 
				};
				kcount++;
				prob[k]++;
				j+=600;
			} else min*=0.999;
			if (min<max) min=max;
		};
		printf("(%4d:", t);
		for (k=0; k<MAXPROB; k++) printf(" %3d", prob[k]);
		printf(")\n");

	    return(0);
	};


	static private void report() {
	PrintStream  machrep;
	int i;

		  machrep=fopen(getfn(id, "mac"), "w");
		  fprintf(machrep, "seed:	%d\ntcost:	%f\nbmb:	%f\nlimit:	%f\n",
			id,	tkosten,	bmb,	limit);

		  fprintf(machrep, "pxover:	%f\npmutat:	%f\n",
			PXOVER,	PMUTATION);

		  for(i=0; i<ELITE; i++) {
			codemachine(i);
			printmachine(machrep, i,population[i].gewinn);
		  };
		  fclose(machrep);
	};

	final private static int PART  = 2;

	static private int GA(int l, int r, int katas) {
	int i,m,p=0, n, lasttop=0;
	double f,gew, ravg=0.0,x,mingewinn=0, maxgewinn=0;
	double gew0=1000000, ravg0=0;
		
		for (generation=0; generation<GENERATIONS&& generation-200<lasttop; generation++) {
		  if (generation%3==0) {
			  base=((base/PELITE)+2)%(ELITE/PELITE);
			  base*=PELITE;
			  printf("base=%d\n", base);
		  };
		  gew=2*gew0;
		  for (n=0; (gew>gew0/FAKT1 || n<5 ) && n<10; n++) {
			maketestpool(id+generation);
			for (i=0; i<ABSTRAKT; i++) {
			    testset[i].min= +10000000;
				testset[i].max= -10000000;
			    testset[i].anzgew=0;
			};

//			maketestpool(id); srand(id+g);
			m=0;
			fitness(m);
			gew=population[m].gewinn;
			mingewinn=gew;
			maxgewinn=gew;
			for(m=1; m<ELITE; m++) {
				fitness(m);
				f=population[m].gewinn;
				gew+=f;
				if (mingewinn>f) mingewinn=f;
				if (maxgewinn<f) maxgewinn=f;
			};
			gew/=m;
			if (n==0) gew0=gew;
		    printf("%.2f>=%.2f\n", gew,gew0/FAKT1);
//			if (gew<gew0) { gew0=gew; n+=10; }
		  };
		  gew0=gew;
			if (generation<20) x=generation+1; else x=20;
			ravg=((x-1)*ravg+gew)/(x);
		printf("%3d	%7.0f %7.0f %7.0f %7.0f\n", 
			generation,
			ravg, mingewinn, gew, maxgewinn);

		fprintf(statistik, "%.0f %7.0f %7.0f %7.0f\n", 
			ravg, mingewinn, gew, maxgewinn);
		fflush(statistik);



		if (ravg0<=3*ravg+gew) { // +gew damit man die Spitze bekommt und nicht den fallenden Arm
		    ravg0=3*ravg+gew;
			lasttop=generation;
//			Beep(440, 500);
			report();
		  };
			  p=eliteGA(gew, ELITE, ELITE+popsize, 0);
		};
		return(p);
	};

	private static void touch(File file)
	{
	    try
	    {
	        if (!file.exists())
	            new FileOutputStream(file).close();
	        file.setLastModified(new Date().getTime());
	    }
	    catch (IOException e)
	    {
	    }
	    
	}	static private int FindID(int id) throws IOException {
		int i;

		if (id>=0) return(id);
		//srand( (unsigned) time(NULL));

		id= -1;
		touch(new File("ga.loc"));
		FileOutputStream fd = new FileOutputStream("ga.loc");
		try {
		    java.nio.channels.FileLock lock = fd.getChannel().lock();
		    try {
			    printf("open(%s)\n", fd.getFD());

				for(i=0; i<100; i++) {
					id=rand()%32000;
					if (! new File(getfn(id, "dev")).exists()) {
					  statistik=fopen(getfn(id, "dev"),"a+");
					  fprintf(statistik,"seed:	%d\n", id);
					  printf("open(%d)\n", id);
					  break;
					}
				  id= -1;
				};
				
		    } finally {
		        lock.release();
		    }
		} finally {
		    fd.close();
		}
		if (id<0) System.exit(99);
		return(id);
	};


public static void main(String[] argv) throws IOException {
	// TODO Auto-generated method stub

	int i,sid;
	int years;

		sid=-1;
		years=10;
		tkosten=TKOSTEN;
		bmb=BMB;
		mach= -1;
		ReadData=null;

//		lt();
//		exit(9);

		int argc=argv.length;
		printf("argc %d\n", argc);
		if (argc >1) {
			for (i=1; i<argc; i++) {
				printf("%d:%s\n", i, argv[i]);
				if (strcmp(argv[i], "-r")==0) {
					ReadData=argv[++i];
				} else if (strcmp(argv[i], "-m")==0) {
					mach=atoi(argv[++i]);
				} else if (strcmp(argv[i], "-s")==0) {
					sid=atoi(argv[++i]);
				} else if (strcmp(argv[i], "-b")==0) {
					bmb=atof(argv[++i]);
				} else if (strcmp(argv[i], "-l")==0) {
					limit=atof(argv[++i]);
				} else if (strcmp(argv[i], "-c")==0) {
					tkosten=atof(argv[++i]);
				} else if (strcmp(argv[i], "-y")==0) {
					years=atoi(argv[++i]);
				} else {
						printf("unknown option %s\n", argv[i]);
						System.exit(9);
				}
			};
		};
		
		for(loop=0; loop<LOOPS; loop++) {
			pmutation=PMUTATION;
			id=FindID(sid);

			srand(id);
			popsize=POPSIZE;


			count=0;
			generation=0;
			maketestpool(id);
			init(0, popsize, 1);
			GA(0, popsize,0);
//			pmutation*=0.7;
//			pxover*=0.7;
//			steps+=steps;
			fclose(statistik);
		}
	}

}
