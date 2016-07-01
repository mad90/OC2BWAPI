package xcs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;



public class XCS {
	 ArrayList<PopulationEntry> population;
	 ArrayList<PopulationEntry> MatchSet;
	 ArrayList<PopulationEntry> ActionSet;
	 boolean useMemory = false;
	 int minEntriesMatchSet = 3; //Mindest Menge an Sets die im MatchSet sein müssen
	 
	 int maxsize;		 //Maximale Anzahl der PopulationList die am Ende des Zyklus vorhanden sein sollen
	 int nextChosenAction; //Gewählt nächste Aktion
	 double beta =  0.2; //Lernrate
	 double alpha = 0.1; //Modifikator um zu Kontrollieren wie stark die Accuracy sinkt wenn den Fehler größer als das Ziel war
	 double v = 5;		 //Modifikator um zu Kontrollieren wie stark die Accuracy sinkt wenn den Fehler größer als das Ziel war
	 double errortarget = 0.1; //Fehler der nicht überschritten werden soll
	 double perChanceOnRandomAction = 5;
	 
	
	 //int minAmntActions = 2; //Mindest Anzahl an verschiedenen Aktionen im MatchSet
	
	
	
	public XCS(int startsize,int maxsize)  {
		this.maxsize = maxsize;
		if(useMemory){
			try {
				this.population = readFromFlatFile();
				
				if(this.population.isEmpty()){
					
					//Falls FlatFile nicht gelesen werden konnte, oder leer ist
					this.population = generatePopulation(startsize);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
		this.population = generatePopulation(startsize);
		
		}
		
	}
	
	public int xcsLoop(int inputdistance, int inputcooldown){
		
//		 ArrayList<PopulationEntry> MatchSet = new ArrayList<PopulationEntry>();
//		 ArrayList<PopulationEntry> ActionSet = new ArrayList<PopulationEntry>();
		 
//		 System.out.println("Population vor MatchSet: " + this.population.PopulationList.size());
		 this.MatchSet = getMatchSet(inputdistance, inputcooldown);
	 
		 //Wenn MatchSet leer -> Covering
		 //Covering direkt auf das MatchSet ausführen!
		 System.out.println("Pattern: "+inputdistance +", "+ inputcooldown+ "\n" +"MatchSet vor Covering" + this.MatchSet.toString());
		 
		 this.MatchSet = covering(inputdistance, inputcooldown, this.MatchSet);
		 System.out.println(this.MatchSet.toString());
		 //System.out.println("Pattern: "+inputdistance +", "+ inputcooldown+ "\n" + this.MatchSet.toString());
		 float[] predictionarray = calculatePredictionArray(this.MatchSet);
		 for(int i=0; i< predictionarray.length; i++){
			 System.out.println("PredictionArray: " +predictionarray[i]);
			 
		 }
		 chooseAction(predictionarray);
		 this.ActionSet = createActionSet(this.MatchSet);
		 
		 return getNextChosenAction();
		 
	
		
	}
	
//	public void distributeReward(double reward){
//		this.updateSet(reward);
//	}
	
	public static ArrayList<PopulationEntry> generatePopulation(int size){
		//Generiert Population Größe size und zufälligen Entries
		
		ArrayList<PopulationEntry> output = new ArrayList<PopulationEntry>();
		
		for(int i = 0; i < size; i++){
			output.add(new PopulationEntry());
		}
		
		return output;
	}
	
	
	
	
	
	
	public void updatePopulation(){
		//Zunächst alle Einträge in der Population mit dem selben Pattern und Action wie im ActionSet löschen
		for(PopulationEntry as : this.ActionSet){
			for(PopulationEntry e: this.population){
				if(as.patterndistance == e.patterndistance && as.patterncooldown == e.patterncooldown && as.action == e.action){
					this.population.remove(e);
				}
				
				
			}
		}
		
		//Alle Einträge des ActionSets in die Population kopieren
		for(PopulationEntry as: this.ActionSet){
			this.population.add(as);
		}
		
	
	}
	public void updateActionSet(double reward){
		//System.out.println("Starte UpdateActionSet");
		//Passt die Werte von Prediction, PredictionError und Fitness in Abhängigkeit des Rewards an
		double totalk = 0;
		double beta = this.beta;
		double alpha = this.alpha;
		double v = this.v;
		double errortarget = this.errortarget;
		
		for(PopulationEntry e : this.ActionSet){
			//TODO: Nachkommastellen bergrenzen!
			
			e.incrementExperience();
			//System.out.println("Erfahrung: " +e.experience);
			
			//Update PredictionError
			//Ansatz Wilson XCS Beschreibung
//			if(e.getExperience() < (1/beta)){
//				//System.out.println("Delta PredictionError: "+ (Math.abs(reward - e.predictionError)-e.predictionError)/ e.getExperience());
//				e.predictionError += ((Math.abs(reward - e.predictionError)-e.predictionError)/ e.getExperience());
//				
//			}
//			else{
//				//System.out.println("Delta PredictionError: "+ beta*(Math.abs(reward - e.predictionError)-e.predictionError));
//				e.prediction += beta*(Math.abs(reward - e.predictionError)-e.predictionError);
//			}
			//Ansatz Vorlesungsfolie
			e.predictionError = e.predictionError + (beta*(Math.abs(reward-e.prediction)-e.predictionError));
			
			//Update Prediction
			//Ansatz Wilson XCS Beschreibung
//			if(e.getExperience() < (1/beta)){
//				//System.out.println("Delta Prediction: "+ ((reward - e.prediction)/ e.getExperience()));
//				e.prediction += ((reward - e.prediction)/ e.getExperience());
//				
//				
//			}
//			else{
//				//System.out.println("Delta Prediction: "+ beta*(reward - e.prediction));
//				e.prediction += beta*(reward - e.prediction);
//			}
			//Ansatz Vorlesungsfolie
			
			e.prediction = e.prediction + (beta*(reward-e.prediction));
			
			
			totalk += alpha*(Math.pow((e.predictionError/errortarget), (v*-1)));
			
			
			
		}
		for(PopulationEntry e : this.ActionSet){
			//Fitness Update
			double kj = alpha*(Math.pow((e.predictionError/errortarget), (v*-1)));
			double k = (kj/totalk);
			e.fitness = e.fitness + (beta*(k-e.fitness));
		}
		
	}
	
	public ArrayList<PopulationEntry> covering(int distance, int cooldown, ArrayList<PopulationEntry> MatchSet){
		
		
		
		//Erweiterung des MatchSets bis die minimalen Anforderung erreicht sind
		while(MatchSet.size() < this.minEntriesMatchSet){
		//Fügt einem zum Pattern passenden Eintrag in die Population hinzu
		PopulationEntry e = new PopulationEntry(distance, cooldown);
		//System.out.println("Füge Element hinzu!");
		MatchSet.add(e);
		}
		boolean areEqual = true;
		int firstAction = MatchSet.get(0).getAction();
		for(PopulationEntry e: MatchSet){
			if(e.getAction() != firstAction){
				areEqual = false;
				
				
			}
			
		}
		if(areEqual){
			PopulationEntry e = new PopulationEntry(distance, cooldown, firstAction);
			MatchSet.add(e);
		}
		
		return MatchSet;
		
	}
	
	
	public void writeToFlatFile() throws FileNotFoundException{
		try(  PrintWriter out = new PrintWriter("XCS Memory.txt")  ){
			String s = "";
			for(PopulationEntry e: this.population){
				s += e.patterndistance + " "+ e.patterncooldown +" " + e.action + " " + e.prediction +" "+ e.predictionError + " " + e.fitness +"\r\n";
			}
			out.println(s);
		}
		
	}
	
	public ArrayList<PopulationEntry> readFromFlatFile() throws FileNotFoundException{
		ArrayList<PopulationEntry> populationList = new ArrayList<PopulationEntry>();
		
		boolean notEnd = false;
		Scanner file = new Scanner(new File("XCS Memory.txt"));
		while(file.hasNextLine()&& notEnd){
		
			String split[] = file.nextLine().split(" ");
			
			int d = Integer.parseInt(split[0]);
			int c = Integer.parseInt(split[1]);
			int a = Integer.parseInt(split[2]);
			
			double p = Double.parseDouble(split[3]);
			double pe =  Double.parseDouble(split[4]);
			double f =  Double.parseDouble(split[5]);
			PopulationEntry e = new PopulationEntry(d, c, a, p, pe, f);
			
			populationList.add(e);
			System.out.println("Zeile eingelesen!");
			if(!file.hasNextLine()){
				notEnd = false;
			}
			


	}
		System.out.println("Einlesen Fertig!");
		return populationList;
		
	}
	
	public ArrayList<PopulationEntry> getMatchSet(int inputdistance, int inputcooldown){
		//Erstellt ein MatchSet basierend auf den beobachteten Parametern Distanz und Waffencooldown
		// 0 Entspricht einer Wildcard auf dem Parameter
		ArrayList<PopulationEntry> MatchSet = new ArrayList<PopulationEntry>();
		
		for(PopulationEntry e: this.population){
			if((inputdistance == e.getPatternDistance() && inputcooldown == e.getPatternCooldown()) ||
				(e.getPatternDistance()== 0 && inputcooldown == e.getPatternCooldown()) ||
				(inputdistance == e.getPatternDistance() && e.getPatternCooldown() == 0) ||
				(e.getPatternDistance()== 0 && e.getPatternCooldown() == 0)){
				MatchSet.add(e);
				
			}
						
		}
		
	return MatchSet;
	}
	
	public float[] calculatePredictionArray(ArrayList<PopulationEntry> MatchSet){
		//Berechnet das PredictionArray aus einem MatchSet
		float[][] tmpresult = new float[2][2];
		float[] result = new float[2];
		
		for(PopulationEntry e: MatchSet){

			tmpresult[e.getAction()-1][0] += (e.getPrediciton()*e.getFitness());
			tmpresult[e.getAction()-1][1] += e.getFitness();
			
			
		}
		for(int i = 0; i < tmpresult.length; i++){
			
			result[i] = (tmpresult[i][0])/(tmpresult[i][1]);
			//System.out.println(result[i]);
						
		}
		
		return result;
	}
	
	
	public ArrayList<PopulationEntry> createActionSet(ArrayList<PopulationEntry> MatchSet){
		//Erstellt das ActionSet anhand der bereits gewählten Aktion und eines MatchSets
		 ArrayList<PopulationEntry> ActionSet = new ArrayList<PopulationEntry>();

		 for(PopulationEntry e: MatchSet){
			 if(e.getAction()== this.getNextChosenAction()){
				 ActionSet.add(e);
				 
				 
			 }
		 }
		return ActionSet;
	}
	
	public int getNextChosenAction(){
		return this.nextChosenAction;
	}
	
	public void setNextChosenAction(int action){
		this.nextChosenAction = action;
		
	}


	public void chooseAction(float[] predictionArray){
		//Wählt anhand des PredictionArrays eine Aktion aus
		 int chosenAction = Integer.MAX_VALUE;
		 boolean areEqual = true;
		 float tmp = 0;
		 Random r = new Random();
		 if((r.nextInt(100)+1)> this.perChanceOnRandomAction){
			 for(int i = 0; i < predictionArray.length; i++){
				 if(predictionArray[0] != predictionArray[i]){
					 //Prüft ob alle Aktionen gleich gut sind
					 areEqual = false;
				 }
								 
				 if(predictionArray[i] > tmp){
					 
					 tmp = predictionArray[i];
					 //System.out.println("Prediction Wert für "+ (i+1) +": "+predictionArray[i]+"\n");
					 
					 chosenAction = i+1;
				 }
			 }
			 if(areEqual){
				 //Wähle bei Gleichstand zufällige Aktion
				Random random = new Random(); 
				chosenAction = random.nextInt(predictionArray.length)+1;
			 }
		 }
		 else{
			 chosenAction = r.nextInt(2)+1;
		 }
		 
		 if(chosenAction == Integer.MAX_VALUE){
			 chosenAction = r.nextInt(2)+1;
		 }
		 //System.out.println("Gewählte Aktion: " + chosenAction + "\n");
		 this.nextChosenAction = chosenAction;
		
	}
	
	

}
