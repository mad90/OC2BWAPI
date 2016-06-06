package XCSPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GeneticAlgorithm {
	static double mutationChance = 5;
	
	
	
	public PopulationEntry fortuneWheel(XCS xcs){
		double totalFitness = 0; //Aufaddierte Fitness
		double[] keyspaces = new double[xcs.ActionSet.size()]; //Speichert die "Begrenzungen" für das Wheel
		int it = 0; //Iterator für das Double Array
		HashMap<Double, PopulationEntry> wheel = new HashMap<Double, PopulationEntry>(); //Speichert die obere Grenze seines Bereichs, sowie den Entry
		Random random = new Random();
		int chosen;
		// Da mir leider keine Möglichkeit in Java einfällt Zahlenbereiche auf Buckets zu mappen gehen ich leider diesen Umweg
		
		for (PopulationEntry e: xcs.ActionSet){
			if(e.getFitness()>= 0){
			totalFitness += e.getFitness();
			keyspaces[it] = totalFitness;
			it++;
			wheel.put(totalFitness, e);
			}
			else{
				it++;
			}
		}
		chosen = random.nextInt((int) totalFitness);
		double key = Double.POSITIVE_INFINITY;
		
		for(int i = 0; i >= keyspaces.length; i++){
			if(chosen <= keyspaces[i]){
				key = keyspaces[i];
				break;
			}
			
		}
		
		
		
		
		
		if(key != Double.POSITIVE_INFINITY){
		return wheel.get(key);
		}
		else{
			return null;
		}
	}
	


	public ArrayList<PopulationEntry> crossOver(PopulationEntry parent1, PopulationEntry parent2){
		//Liefert 2 durch Crossover entstandene Nachkommen zurück
		
		ArrayList<PopulationEntry> offsprings=new ArrayList<PopulationEntry>();
		PopulationEntry offspring1=new PopulationEntry();
		PopulationEntry offspring2=new PopulationEntry();
		
		//copy parents classifier 
		offspring1=parent1;
		offspring2=parent2;
		
		double chiGA=0.5;//probabilität für Anwendung von crossover 
		//Literatur XCS Algorithic Description Seite 13.
		if(Math.random()<chiGA){
			
			//prediction, prediction error und fitness sind durchschnittliche Zahl von parent1 und parent2	
			//hier sind die drei Werte für offspring1 und offspring2 gleich
			
			offspring1.prediction=(parent1.prediction+parent2.prediction)/2;//prediction
			offspring2.prediction=offspring1.prediction;
			
			offspring1.predictionError=(parent1.predictionError+parent2.predictionError)/2;//prediction error
			offspring2.predictionError=offspring1.predictionError;
			
			offspring1.fitness=(parent1.fitness+parent2.fitness)/2;//fitness
			offspring2.fitness=offspring1.fitness;		
			

			//temporäre variablen für Distanz und Cooldown von parent1
			int parent1_patterndistance=parent1.patterndistance;
			int parent1_patterncooldown=parent1.patterncooldown;
			
			//Vertauschensverfahren. Es gibt nur zwei möglichkeiten. Vertauschung von Distanz oder Vertauschung von Cooldown
			
			Random random = new Random();
			int i=random.nextInt(2);
			if(i==0){
				parent1.patterndistance=parent2.patterndistance;
				parent2.patterndistance=parent1_patterndistance;
			}else{
				parent1.patterncooldown=parent2.patterncooldown;
				parent2.patterncooldown=parent1_patterncooldown;
			}
					
				
				
		}
//		/* 
//		***********ich weiss es nicht, was in den Zeile 18 und 19 bedeutet
//		else{
//			offspring1.fitness*=0.1;
//			offspring2.fitness*=0.1;			
//		}
//		*/
	
		
		offsprings.add(offspring1);
		offsprings.add(offspring2);		
		
		return offsprings;
	}
	
	
/*

	public ArrayList<PopulationEntry> crossOver(PopulationEntry parent1, PopulationEntry parent2){
		//Liefert 2 durch Crossover entstandene Nachkommen zurück
		ArrayList<PopulationEntry> offsprings = null;
		
		
		
		
		return offsprings;
	}
	*/
	public PopulationEntry mutation(PopulationEntry input){
		//Mutiert mit einer gewissen Chance das Pattern oder die Action des Entry
		Random random = new Random();
		if(random.nextInt(100) < mutationChance){
		//Ziel der Mutation 1 Distance, 2 Cooldown, 3 Aktion
		int target = random.nextInt(3)+1;
		int current;
		switch (target){
			case 1: current = input.patterndistance;
					//Abfrage um Doppelwildcards zu verhindern
					if(input.patterndistance == 0 || input.patterncooldown == 0){
						//Erlaubt keine Wildcards
						while(input.patterndistance == current){
							input.patterndistance = random.nextInt(3)+1;
							}
					}
					else{
						while(input.patterndistance == current){
							//Erlaubt Wildcard (0)
							input.patterndistance = random.nextInt(4);
							}
					}
					
					
			case 2: current = input.patterncooldown;
					//Abfrage um Doppelwildcards zu verhindern
					if(input.patterndistance == 0 || input.patterncooldown == 0){
						//Erlaubt keine Wildcards
						while(input.patterncooldown == current){
							input.patterncooldown = random.nextInt(6)+1;
							}
					}
					else{
						while(input.patterncooldown == current){
							//Erlaubt Wildcard (0)
							input.patterncooldown = random.nextInt(7);
							}
					}
			case 3: current = input.getAction();
			while(input.getAction() == current){
				input.action = random.nextInt(2)+1;
				}
		}
		
		}
	
	return input;
	}

}
