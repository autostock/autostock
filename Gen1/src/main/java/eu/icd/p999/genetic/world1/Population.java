package eu.icd.p999.genetic.world1;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Population {

	private int populationSize;
	private int geneSize;
	private double mutationRate;
	private double crossoverRate;
	Genotype[] population;
	private double bestFitness;
	private static Random random = new Random();

	public Population(int populationSize, int geneSize, double mutationRate, double crossoverRate) {
		this.populationSize = populationSize;
		this.geneSize = geneSize;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		population = new Genotype[populationSize];
		for (int i = 0; i < population.length; i++) {
			population[i]=new Genotype(geneSize);
		}		
		bestFitness=-Double.MAX_VALUE;
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public void calcFitness(Phenotype phenotype) {
		bestFitness=-Double.MAX_VALUE;
		for (int i = 0; i < population.length; i++) {
			phenotype.makePhenotype(population[i]);
			double f=population[i].getFitness();
			if (bestFitness<f) {
				bestFitness=f;
			}
		}		
		
	}

	public void nextGeneration() {
		Arrays.sort(population, new Comparator<Genotype>() {
			@Override
			public int compare(Genotype o1, Genotype o2) {
				return -Double.compare(o1.getFitness(), o2.getFitness());
			}});
		for (int i = population.length-1; i>population.length/10; i--) {
			if (random.nextDouble()<crossoverRate) {
				population[i].crossover(population[random.nextInt(i)]);
			} 
			if (random.nextDouble()<mutationRate) {
				population[i].mutate();
			} 
		}
	}

}
