package XCSPackage;

import java.util.Random;
public class PopulationEntry {
	
	int patterndistance; //Kategorisierter Abstand zum nächsten Gegner
	int patterncooldown; //Kategorisierte Zeit bis Waffe wieder einsatzbereit
	int action;			 //Auszuführende Aktion
	double prediction;
	double predictionError;
	double fitness;
	int experience; //Wie Erfahren das Pattern bereits ist

	public PopulationEntry(){
		//Zufällige Initialisierung des Pattern
		Random random = new Random();
		this.patterndistance = random.nextInt(6);
		this.patterncooldown = random.nextInt(3);
		if(patterndistance == 0 && patterncooldown == 0){
			//Soll doppelte Wildcards verhindern
			if(random.nextBoolean()){
				this.patterndistance = random.nextInt(5)+1;
			}
			else{
				this.patterncooldown = random.nextInt(2)+1;
			}
			
		}
		this.action = random.nextInt(2)+1;
		this.prediction = 10.0;
		this.predictionError = 0.0;
		this.fitness = 0.01;
		this.experience = 0;
		
	}
	
	public PopulationEntry(int distance, int cooldown ){
		//Initialisierung mit festgelegtem Pattern oder einer Wildcard und zufälliger Aktion
		Random random = new Random();
		if(random.nextBoolean()){
			this.patterndistance = 0;
		}
		else{
		this.patterndistance = distance;
		}
		if(random.nextBoolean()){
			this.patterncooldown = 0;
		}
		else{
		this.patterncooldown = cooldown;
		}
		if(this.patterncooldown == 0 && this.patterndistance == 0){
			if(random.nextBoolean()){
				this.patterndistance = distance;
			}
			else{
				this.patterncooldown = cooldown;
			}
		}
		this.action = random.nextInt(2)+1;
		this.prediction = 10.0;
		this.predictionError = 0.0;
		this.fitness = 0.01;
		this.experience = 0;
		
	}
	
	public PopulationEntry(int distance, int cooldown, int notAction ){
		//Initialisierung mit festgelegtem Pattern und zufälliger Aktion die nicht dem Parameter entspricht
		Random random = new Random();
		if(random.nextBoolean()){
			this.patterndistance = 0;
		}
		else{
		this.patterndistance = distance;
		}
		if(random.nextBoolean()){
			this.patterncooldown = 0;
		}
		else{
		this.patterncooldown = cooldown;
		}
		if(this.patterncooldown == 0 && this.patterndistance == 0){
			if(random.nextBoolean()){
				this.patterndistance = distance;
			}
			else{
				this.patterncooldown = cooldown;
			}
		}
		
		this.action = 1;
		while(this.action == notAction){
		this.action = random.nextInt(2)+1;
		}
		this.prediction = 10.0;
		this.predictionError = 0.0;
		this.fitness = 0.01;
		this.experience = 0;
		
	}
	
	public PopulationEntry(int distance, int cooldown, int action, double p, double pe, double f ){
		this.patterndistance = distance;
		this.patterncooldown = cooldown;
		this.action = action;
		this.prediction = p;
		this.predictionError = pe;
		this.fitness = f;
	}
	
	
	
	
	
	public int getPatternDistance(){
		return this.patterndistance;
	}
	
	
	public int getPatternCooldown(){
		return this.patterncooldown;
	}
	
	public int getAction(){
		return this.action;
	}
	
	public double getPrediciton(){
		return this.prediction;
	}


	public void setPrediction(float prediction) {
		this.prediction = prediction;
	}

	public double getPredictionError() {
		return this.predictionError;
	}

	public void setPredictionError(float predictionError) {
		this.predictionError = predictionError;
	}
	
	public double getFitness() {
		return this.fitness;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}
	
	public String toString(){
		//Gibt Entry Parameter als String zurück für Debugging
		String s = new String();
		s = "D "+ this.patterndistance + " CD " + this.patterncooldown + " A " + this.action + " P " + this.prediction +
			" PE " + this.predictionError +" F " + this.fitness + "\n";
		return s;
	}
	
	public int getExperience(){
		return this.experience;
	}
	
	public void incrementExperience(){
		this.experience++;
	}
	

	
	
	
	
}
