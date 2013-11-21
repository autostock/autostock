
public class Test3 {
//	final private static int POPSIZE = 10000;
//	final private static int MAXGENS = 100;
	final private static int POPSIZE = 1000;
	final private static int MAXGENS = 4000;
	final private static int LOOPS = 1000;

	final private static int POPBESTSIZE = (POPSIZE/4);

//	final private static double PMUTATION = 0.01;

//	final private static int PXOVER = 0.3;
//	final private static int PMUTATION = 0.0004;
	final private static double PMUTATION = 0.00004;

//	final private static int PXOVER = 0.15;
	final private static double PXOVER = 0.6;


	final private static double TKOSTEN = 0.015;

	final private static int STRATEGIES = 50;

	final private static int SYMBOLS = (1+3* 5);

	final private static int BSTATES = 10;
	final private static int GENLEN = ((BSTATES*SYMBOLS)+STRATEGIES);

	final private static int STEPS = 120;
	final private static int STATES = 300;
	final private static int STATE0 = STRATEGIES;

	final private static int BUY1 = 0;
	final private static int SELL1 = 1;
	final private static double AKTIE0 = 1000.0;

	final private static int P0 = 2*STEPS;
	final private static int DATALEN = 10000;
	final private static int TAPELEN = P0+DATALEN;
	final private static double BMB = 0.1;
	final private static double LIMIT = 0.9;
//	final private static int PRINTMODE(m) = ((printmode&m)!=0);
	private static boolean PRINTMODE(int m) { return ((printmode&m)!=0); };


	int		info=0;


	static int[] tape = new int[TAPELEN];
	static double[] kurs = new double[DATALEN];
	String[] dkurs= new String[DATALEN];

	typedef unsigned char byte;

	struct machine {
		int dist[SYMBOLS]; double proz1[SYMBOLS]; double proz2[SYMBOLS]; double handel;
	} trans[STATES];


	typedef byte genetype[GENLEN];

	static class genotype {
		genetype gene;
		double fitness, rfitness, cfitness, gewinn;
		int sell, buy,hold;
	};

	genotype[] population = new genotype [POPSIZE+2];

	static double[] symval=new double[]{0.0, 40.0, 90.0, -40.0, -90.0 };
	static String[] names=	new String[]		{" #s1", " +b2", " -s1", " -s2"};

	static double strategy[STRATEGIES]=	{0.07,    0.8,  -0.004,  -0.05};
	static int stratuse[STRATEGIES];

	static double	limit=LIMIT;

	static double	tkosten=TKOSTEN;
	static double	bmb=BMB;
	static int		mach= -1;
	static String	ReadData=NULL;
	static String	machname="";
	static String	aktienname=NULL;
	static int		bstates=BSTATES;
	static int		strategies=STRATEGIES;
	static int runtime;
	static FILE * outfile=stdout;

	static int id;
	static int datamax;
	static int tapemax;
	static int loop;
	static int generation;
	static int anzmach=POPSIZE;

	static double startkapital, ravg;
	static double startkapital0=0.0;
	static double avgbest;
	static double pmutation=PMUTATION;
	static double pxover=PXOVER;
	static int steps=STEPS;
	static int printmode=7;
	static double globalP0;


	static FILE *statistik;
	static FILE * export;


	static String basename(String fn) {
	int i;
	if (fn==NULL) return("noname");
	for(i=strlen(fn); i>0; i--) 
		if (fn[i]=='/' || fn[i]=='\\') 
			return(&fn[i+1]);

	return(fn);
	}
		
	static String getfn(int id, Stringsuffix) {
	static char fn[1000];
		sprintf(fn, "run%03d.%s", id, suffix);
		return(fn);
	};

	final private static int WINDOW = 80;
	
	static int scale(int len1) {
	int i,w, len=len1;
	double k;
	double max=0.0, d;

		if (len<WINDOW) len=WINDOW;
		w=len-WINDOW;
		if (w<0) w=0;
		for(i=w; i<len; i++) {
			k=kurs[i];
			if(i>0) {
				d=kurs[i]-kurs[i-1];
				if (d<0.0) d = -d;
				if (d>max) max=d;
			}
		};

//		if (ReadData!=NULL && 1)	printf("len=%d; maxDelta=%f\n", len, max);
		symval[0]= 0.0;
		symval[1]= +0.4*max;
		symval[2]= +0.9*max;
		symval[3]= -0.4*max;
		symval[4]= -0.9*max;
	/*
		symval[1]= +0.03*max;
		symval[2]= +0.07*max;
		symval[3]= -0.03*max;
		symval[4]= -0.07*max;
		printf("max delta %f\n", max);
	*/
		if (ReadData==NULL) {
		    d=kurs[i-2]-kurs[i-1];
		    if (d<0.0) d = -d;
		    kurs[i]=kurs[i-1]-d;
			return(len+1);
		} else
			return(len);
	}

	final private static int TRADINGDAYS = 264;
	//final private static int TRADINGDAYS 600

	static int read() {
	int s=1,i;
	float k;
	double max=0.0, d;

		for(i=0; i<DATALEN&& s==1; i++) {
			s=scanf("%f", &k);
			kurs[i]=k;
			if(i>0) {
				d=kurs[i]-kurs[i-1];
				if (d<0.0) d = -d;
				if (d>max) max=d;
			}
		};
		kurs[i]=kurs[i-1]/2;
		return(scale(i+1));
	}

	static int generate()
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
			d=rand()%1000 / 1000.0;
			d=k0*bmb*(2.0*d-1.0);
			if (k+d<=ku || k+d>=ko) d= -d;
			k+=d;
		};
		
		return (scale(max));
	}

	static int symbolize(int datamax) {
	double a,d,best;
	int i,j,k,s;

		for(i=0;i<P0; i++) tape[i]=AKTIE0;

		tape[i]=kurs[0];
		for(j=0; j<datamax; j++,i++) {
			tape[i]=kurs[j];
		};
		return(i);
	}

	static void resymbolize(int datamax) {
		scale(datamax);
		symbolize(datamax);
	};

	static void initialize() {
	int i,j;

		for(i=0; i<POPSIZE; i++) {
			for(j=0; j<GENLEN; j++) {
				population[i].gene[j]=rand()%256;
			}
		}
	}

	final private static double TR = 56.0;
	final private static int MAXERR = 1;
	
	static double startmachine(int steps, int pos) { 
	int i,state, symbol, count=0, hit,bh;
	double akt,proz1,proz2,proz,p0=0.0, handel= 0.0;

		bh=BSTATES;
	/*
		p0=tape[0];
		for (i=0; i<pos; i++) {
			p0=(TR*p0+tape[i])/(TR+1.0);
		};
//		p0=tape[P0];
	*/
		p0=globalP0;
//		if (pos%100==0) printf("p0=%10.2f\n", p0);
		akt=tape[pos]/p0;
//		printf("tape[%d]=%f %f\n", P0, p0, akt);
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
	if (true) {
			if (symbol-hit<=MAXERR) {
//				used[state]++;
//				hit=hit*(state+1)/symbol;
				hit=hit*hit*(BSTATES-state)*(BSTATES-state);
				handel+=(trans[state].handel)*(hit);
				count+=hit;
			}
	} else if (false) {
			if (fabs(handel)<=fabs(trans[state].handel)) {
				handel=trans[state].handel;
				bh=state;
			}
	} else if (false) {

			if (symbol==SYMBOLS/3) {
	/*
				handel+=trans[state].handel;			
				count++;
		        INFO fprintf(export, "%3d %7.2f %2d	", pos, akt, state);
			for(symbol=0; symbol<SYMBOLS/3; symbol++) {
				i=pos-trans[state].dist[symbol];
				last=tape[i];
				proz1=trans[state].proz1[symbol];
				proz2=trans[state].proz2[symbol];
				proz=(last-akt)/tape[P0];
				INFO fprintf(export, " %d->%.2f [%.2f, %.2f, %.2f];", i, last, proz1, proz, proz2);
			};
	*/
//				used[state]++;
				return (trans[state].handel);
			}
	}
	loop2: ;
		}
//		used[bh]++;
		if (count!=0) return(handel/count); else {/*used[BSTATES]++;*/ return(0.0);};
	}

	static void codemachine(int m) {
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


	static double startmachines(int steps, int pos) {
	int i;
	double h=0.0,handel=0.0;

		for (i=0; i<anzmach; i++) {
			codemachine(i);

			handel+=startmachine(steps, pos);
		};
		return(handel/i);
	};

	static double xstartmachines(int steps, int pos) {
	int i;
	double h=0.0,handel=0.0;

		for (i=0; i<anzmach; i++) {
			codemachine(i);

			h=startmachine(steps, pos);
			if (fabs(handel)<fabs(h) ) handel=h;
		};
		return(handel);
	};

	static void showmachine(int m) {
	int state,state1, symbol,s,i,p1,p2;

		printf("machine %d\n", m);
		for(state=0; state<BSTATES; state++) {
			printf("%2d:%5.2f>", state, trans[state].handel);
			for(symbol=0; symbol<SYMBOLS/3; symbol++) {
				printf("%3d[%4.0f,%4.0f]",
					trans[state].dist[symbol],
					trans[state].proz1[symbol]*100,
					trans[state].proz2[symbol]*100);
			};
			printf("\n");
		};
	}

	double best=0.0;


	static void produce() {

		datamax=generate();
		tapemax=symbolize(datamax);

		if (startkapital0==0.0) 
			startkapital=100.0*kurs[0];
		else 
			startkapital=startkapital0;


//		printf("datamax=%d; tapemax=%d\n", datamax, tapemax);
	}

	static String fnorm(String str) {
	static char str2[100];
	String str1;
		for(str1=str2 ;*str; str++, str1++) 
			if (*str==',') *str1='.'; else *str1= *str;

		*str1= *str; // 0
		return(str2);
	};

	static void readdata(String ReadData) {
	float f;
	int i,r,n=0,w=0;
	char fn[100];
	char str[100];
	char str2[100];
	char line[100];
	String err;
	double k,k0,bank, res,konto, cost, handel, avg=0.0, avgk=0.0;
	int summe, s, lasthandel;

//		if(PRINTMODE(2)) printf("%5d %10.2f %7.2f %10.2f %7.2f %7.2f\n", n, k, 100.0*w/n,res, avg/n, avgk/n);
	  if(PRINTMODE(16)) printf("%15s %7s %10s\n", "Name", "AnzGew%", "Avg(Strat)");
	  if(PRINTMODE(2)) printf("%5s %10s %7s %10s %10s %10s\n", "Jahr", "Endkurs", "AnzGew%", "Gew%", "Avg(Strat)", "Avg(S0)");
	  while (1) {
		i=0;
	    if(PRINTMODE(4)) fprintf(stderr, "Bitte erstes Datum und Kurs: ");
		strcpy(str, "                   "); strcpy(str2, str);
		r=scanf("%s %s", str, str2);
//		printf("r=%d str=%s\n", r, str);
		if (r<=0) goto exit;
		sscanf(fnorm(str2), "%f", &f); k=f;
		k0=k;

		if (startkapital0==0.0) 
			startkapital=100.0*k;
		else 
			startkapital=startkapital0;

		if (PRINTMODE(1)&&!PRINTMODE(8)) printf("startkapital	%f\n", startkapital);

		for(i=0;i<P0; i++) tape[i]=k; 
		tape[i]=k;
		tape[i+1]=k;
		globalP0=tape[P0];

		datamax=1;

		    konto=0.5*(1-tkosten)*startkapital;
			summe=0.5*(startkapital/k);
			bank=0.5*(tkosten)*startkapital;

			if (PRINTMODE(1)) {
			  if (PRINTMODE(8)) {
				  sprintf(fn, "kurs%04d.txt", n+1); 
				  fflush(outfile);
				  outfile=fopen(fn,"w");
				  if (outfile==0) outfile=stdout;
			  };
			  fprintf( outfile, "%-12s %10s %5s %5s %10s %10s %5s %10s\n", 
				"Datum", "Kurs", "Handel", "Depot", "Kasse", "Depot+Kasse", "Proz", "Bank");
			  fprintf( outfile, ":%-12s %10.2f %5d %5d %10.2f %10.2f %6.2f  %10.2f\n", str, k, summe, summe, konto, 
				  k*summe+konto, 100.0*(k*summe+konto)/startkapital, bank);
			};

		gets(line);
	    if(PRINTMODE(4)) fprintf(stderr, "Bitte Datum und Kurs: ");
		strcpy(line, "                                   "); err=gets(line);
		lasthandel=i;
		while ((r=sscanf(line, "%s %s", str, str2))==2 ) {
			sscanf(fnorm(str2), "%f", &f); k=f;

//			printf("	%s %f\n", str, k);
			i++;
			tape[i]=k;
			tape[i+1]=k;

			datamax++;

			handel=startmachines(steps, i);
	if (false) {
//			printf("%d %d %f\n",lasthandel, summe, handel);
			if (lasthandel<i-25 && summe==0 && handel>=0.0 && handel<0.2) {
				handel= -0.9;
				globalP0=tape[i];
//				printf("po=%10.2f\n", globalP0);
			};
	} 
			s=0;
			if (handel<0.0) {
					s=floor(-handel*konto/tape[i]);
					cost= s*tape[i];
					cost=(1.0+tkosten)*cost; // Kosten der Transkation
					bank+= fabs(tkosten*cost);
					konto+= -cost;
					summe+= s;
			} else if ((handel>0.0) && (summe>0)) {
					// Verkaufen
					// strategy[]>0 
					// s <0
					s= -ceil(handel*summe);
//					if(tape[P0+i+1]<tape[P0+i])
//						k= -s*tape[P0+i+1];
//					else
						cost= -s*tape[i];

					cost=(1.0-tkosten)*cost;
					bank+= fabs(tkosten*cost);
					konto+= cost;
					summe+= s;
			};
			if (s!=0) lasthandel=i;

			if (PRINTMODE(1)) fprintf( outfile, ":%-12s %10.2f %5d %5d %10.2f %10.2f %6.2f  %10.2f\n", str, k, s, summe, konto, konto+summe*k, 100.0*(konto+summe*k)/startkapital, bank);
			if(PRINTMODE(4)) fprintf(stderr, "Bitte Datum und Kurs: ");
			strcpy(line, "                                   "); err=gets(line);
			if (err==NULL) strcpy(line,"");	
		};
		res=(konto+summe*k);
		if (res>startkapital) w++;
		res=100.0*(res/startkapital-1.0);
		avg+=res;
		avgk+=50*(k/k0-1);
//		avgk+=100.0*k;
		n++;
		if(PRINTMODE(2)) printf("%5d %10.2f %7.2f %10.2f %7.2f %7.2f\n", n, k, 100.0*w/n,res, avg/n, avgk/n);

	  };

	exit:
	  if(PRINTMODE(16)) printf("%15s %7.2f %10.2f %10.2f %10.2f\n", (aktienname?aktienname:machname), 100.0*w/n, avg/n, avgk/n, avg/n-avgk/n);
//		printf("datamax=%d; tapemax=%d\n", datamax, tapemax);
	}


	static int readmachs(String fn) {
	char s1[SYMBOLS][8], str[20];
	FILE * f;
	int mach,r, pc, state, state1,i,gen,genlen;
	float fac, fitness;

		f=fopen(fn, "r");
		if (f==NULL) {
			fprintf(stderr, "Problems with file '%s'\n", fn);
			return(0);
		};
	/*
			printf("%s\n", fn);
	seed:	97
	tcost:	0.010000
	bmb:	0.030000
	limit:	0.300000
	pxover:	0.600000
	pmutat:	0.000040
	*/
		r=fscanf(f, "%s %f", str, &fac);
		r=fscanf(f, "%s %f", str, &fac);
		r=fscanf(f, "%s %f", str, &fac);
		r=fscanf(f, "%s %f", str, &fac);
		r=fscanf(f, "%s %f", str, &fac);
		r=fscanf(f, "%s %f", str, &fac);

		anzmach=0;
		while (r=fscanf(f, "%s %d", str, &mach)>1) {
//		   printf("%s %d\n", str, mach);
	/*
	beg_machine: 0

	typ: 1 genlen: 649 states: 128 strat: 9
	steps:	120
	generation:	5999
	states:
	*/

		gen=0;
		r=fscanf(f, "%s", str);
		while (r) {
//		   printf("%s\n", str);
			if (strcmp(str, "typ:")==0)		r=fscanf(f, "%f", &fac);
			if (strcmp(str, "genlen:")==0)	r=fscanf(f, "%d", &genlen);
			if (gen==0&&strcmp(str, "states:")==0)	r=fscanf(f, "%d", &bstates);
			if (strcmp(str, "strat:")==0)	r=fscanf(f, "%d", &strategies);
			if (strcmp(str, "steps:")==0)	r=fscanf(f, "%d", &steps);
			if (strcmp(str, "generation:")==0) r=fscanf(f, "%d", &gen);

			if ((gen!=0&&strcmp(str, "states:")==0)|| strcmp(str, "indiv:")==0) {

				for (pc=0; pc<genlen; pc++) {
				    if(pc%16==0) fscanf(f, "%d:", &state);
//					printf("%2d:", state);
	/*
	18:  32 114  40 ...
	*/
				    r=fscanf(f, "%d", &state1);
//					printf("%4d", state1);
				    population[anzmach].gene[pc]=state1;
//				  printf("\n");
				};
			};

	/*
	fitness: 126190.82
	end_machine
	*/
			if (strcmp(str, "fitness:")==0) r=fscanf(f, "%f", &fitness);
			if (strcmp(str, "end_machine")==0) r=0; else	r=fscanf(f, "%s", str);
		    population[anzmach].fitness=fitness;
		};

//		  codemachine(anzmach);
//		  printmachine(stdout,anzmach, fitness);
		  anzmach++;
		};


		return(anzmach);
	}

	static void main(String argv[])
	{
	int i,s=1;
	int years;

		years=1;
		tkosten=TKOSTEN;
		bmb=BMB;
		mach= -1;
		machname= "";
		ReadData=NULL;

		if (argc >1) {
			for (i=1; i<argc; i++) {
				if (strcmp(argv[i], "-r")==0) {
					ReadData=argv[++i];
				} else if (strcmp(argv[i], "-m")==0) {
					machname=argv[++i];
				} else if (strcmp(argv[i], "-n")==0) {
					aktienname=argv[++i];
				} else if (strcmp(argv[i], "-v")==0) {
					printmode=atoi(argv[++i]);
				} else if (strcmp(argv[i], "-k")==0) {
					startkapital0=atof(argv[++i]);
				} else if (strcmp(argv[i], "-s")==0) {
					s=atoi(argv[++i]);
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
						exit(9);
				}
			};
		};


		    readmachs(machname);
			readdata("nofile");
		return 0;
	}


}
