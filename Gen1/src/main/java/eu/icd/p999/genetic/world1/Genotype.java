package eu.icd.p999.genetic.world1;

import java.util.Random;

public class Genotype {
	private static Random random = new Random();

	static long idSource;
	long id;
	int[] gene;
	private double fitness;

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public Genotype(int geneSize) {
		id=idSource++;
		gene=new int[geneSize];
		for (int i = 0; i < gene.length; i++) {
			gene[i]=random.nextInt(256);
		}
	}

	public void mutate() {
		id=idSource++;
		gene[random.nextInt(gene.length)]=random.nextInt(256);
	}

	public void crossover(Genotype genotype) {
		id=idSource++;
		int cut=random.nextInt(gene.length-2)+1; // inside
		switch (random.nextInt(2)) {
		case 0:
			for (int i = cut; i < gene.length; i++) {
				gene[i] = genotype.gene[i];
			}
		case 1:
			for (int i = 0; i < cut; i++) {
				gene[i] = genotype.gene[i];
			}
		}
	}

}
