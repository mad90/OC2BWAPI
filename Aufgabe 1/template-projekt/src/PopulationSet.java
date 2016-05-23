import java.util.ArrayList;
import java.util.Random;

public class PopulationSet {
	
	 ArrayList<PopulationEntry> PopulationList = new ArrayList<PopulationEntry>(); //Population
	 
	 int maxsize;		 //Maximale Anzahl der PopulationList die am Ende des Zyklus vorhanden sein sollen
	 int nextChosenAction; //Gewählt nächste Aktion
	 double beta =  0.2; //Lernrate
	 double alpha = 0.1; //Modifikator um zu Kontrollieren wie stark die Accuracy sinkt wenn den Fehler größer als das Ziel war
	 double v = 5;		 //Modifikator um zu Kontrollieren wie stark die Accuracy sinkt wenn den Fehler größer als das Ziel war
	 double errortarget = 0.1; //Fehler der nicht überschritten werden soll
	 double perChanceOnRandomAction = 5;
	
	public PopulationSet(int size, int maxsize){
		//Erstellt ein Population Set der gewünschten Größe mit zufällig initialisierten Einträgen
		this.maxsize = maxsize;
		for(int i = 0; i<size; i++){
			PopulationEntry entry = new PopulationEntry();
			this.PopulationList.add(entry);
			System.out.println(entry.toString());
			
		}
	}
	
	public PopulationSet(ArrayList<PopulationEntry> PopulationList, int maxsize){
		//Bestehende Population verwenden
		this.maxsize = maxsize;
		this.PopulationList = PopulationList;
		
	}
		
	public ArrayList<PopulationEntry> getPopulation(){
		return this.PopulationList;
	}
	
	public ArrayList<PopulationEntry> getMatchSet(int inputdistance, int inputcooldown){
		//Erstellt ein MatchSet basierend auf den beobachteten Parametern Distanz und Waffencooldown
		// 0 Entspricht einer Wildcard auf dem Parameter
		ArrayList<PopulationEntry> MatchSet = new ArrayList<PopulationEntry>();
		
		for(PopulationEntry e: this.PopulationList){
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
	
//	public void updateSet(ArrayList<PopulationEntry> ActionSet, double reward){
//		//Passt die Werte von Prediction, PredictionError und Fitness in Abhängigkeit des Rewards an
//		double totalk = 0;
//		
//		for(PopulationEntry e : ActionSet){
//			e.incrementExperience();
//			//Update Prediction
//			if(e.getExperience() < (1/this.beta)){
//				e.prediction += ((reward - e.prediction)/ e.getExperience());
//				
//			}
//			else{
//				e.prediction += this.beta*(reward - e.prediction);
//			}
//			//Update PredictionError
//			if(e.getExperience() < (1/this.beta)){
//				e.predictionError += ((Math.abs(reward - e.predictionError)-e.predictionError)/ e.getExperience());
//				
//			}
//			else{
//				e.prediction += this.beta*(Math.abs(reward - e.predictionError)-e.predictionError);
//			}
//			
//			totalk += this.alpha*(Math.pow((e.predictionError/this.errortarget), (this.v*-1)));
//			
//			
//			
//		}
//		for(PopulationEntry e : ActionSet){
//			//Fitness Update
//			double kj = this.alpha*(Math.pow((e.predictionError/this.errortarget), (this.v*-1)));
//			double k = (kj/totalk);
//			e.fitness += this.beta*(k-e.fitness);
//		}
//		
//	}

	
	public void ChoseAction(float[] predictionArray){
		//Wählt anhand des PredictionArrays eine Aktion aus
		 int chosenAction = 99;
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
		 
		 if(chosenAction == 99){
			 chosenAction = r.nextInt(2)+1;
		 }
		 //System.out.println("Gewählte Aktion: " + chosenAction + "\n");
		 this.nextChosenAction = chosenAction;
		
	}
	
//	public void covering(int distance, int cooldown, ArrayList<PopulationEntry> matchSet){
//		//Fügt einem zum Pattern passenden Eintrag in die Population hinzu
//		PopulationEntry e = new PopulationEntry(distance, cooldown);
//		matchSet.add(e);
//	}
	
	
	
	public boolean isFull(){
		//Prüft ob die PopulationList seine maximale Größe erreicht oder überschritten hat
		if(this.PopulationList.size() >= this.maxsize){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public void cropPopulationSet(){
		//TODO: Reduziert die Anzahl der Entry auf die maxsize
		
		
		
	}
	
	public String toString(){
		String s = "";
		for(PopulationEntry e: this.PopulationList){
			s += e.toString();
			
		}
		return s;
	}
	
	public boolean isEmpty(){
		if(this.PopulationList.isEmpty()){
			return true;
		}
		else{
			return false;
		}
			
	}
	
	
	
}


