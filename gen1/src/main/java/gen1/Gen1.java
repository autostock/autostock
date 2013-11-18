package gen1;

import java.util.Random;

public class Gen1 {
	static final double AKTIE0 = 1000.0;

	static final double LIMIT = 0.9;
	static final double BMB = 0.07;

	static double limit = LIMIT;
	static double bmb = BMB;
	static double aktie = AKTIE0;
	static int anz = 1000;
	static int days = 250;
	static Random random = new Random();

	static int generate() {
		int i, j, max;
		double k0, k, ko, ku, d;

		for (j = 0; j < anz; j++) {
			max = days;
			// max=10000;
			k0 = aktie;
			ku = (1.0 - limit) * k0;
			if (ku < 0.0)
				ku = 0.0;
			ko = (1.0 + limit) * k0;
			k = k0;

			for (i = 0; i < max; i++) {
				printf(":%d/%d %10.2f\n", j, i, k);
				d = rand() % 1001 / 1000.0;
				d = k0 * bmb * d;
				if (rand() % 2 == 0)
					d = -d;
				if (k + d <= ku)
					d = -d;
				// if (k+d<=ku) d= d;
				else if (k + d >= ko)
					d = -d;
				// k+=d;
				if (k > ku)
					k += d;
			}
			;
			printf("stop\n");
		}
		;
		return (0);
	};

	private static int rand() {
		return Math.abs(random.nextInt());
	}

	private static void printf(String format, Object... args) {
		System.out.printf(format, args);

	}

	public static void main(String[] argv) {
		int i, s = -1;

		bmb = BMB;

		int argc = argv.length;
		if (argc > 1) {
			for (i = 1; i < argc; i++) {
				if (strcmp(argv[i], "-b") == 0) {
					bmb = atof(argv[++i]);
				} else if (strcmp(argv[i], "-l") == 0) {
					limit = atof(argv[++i]);
				} else if (strcmp(argv[i], "-s") == 0) {
					s = atoi(argv[++i]);
				} else if (strcmp(argv[i], "-n") == 0) {
					anz = atoi(argv[++i]);
				} else if (strcmp(argv[i], "-d") == 0) {
					days = atoi(argv[++i]);
				} else if (strcmp(argv[i], "-a") == 0) {
					aktie = atof(argv[++i]);
				} else {
					printf("unknown option %s\n", argv[i]);
					System.exit(9);
				}
			}
			;
		}
		;

		if (s >= 0)
			random.setSeed(s);

		generate();
	}

	private static int atoi(String string) {
		return Integer.parseInt(string);
	}

	private static double atof(String string) {
		return Double.parseDouble(string);
	}

	private static int strcmp(String a, String b) {
		return a.compareTo(b);
	}

}
