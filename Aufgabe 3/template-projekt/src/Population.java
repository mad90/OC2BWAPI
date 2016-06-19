
import java.util.Random;
import java.util.ArrayList;
import java.io.*;

public class Population implements Serializable {
	
	GAParameter g;
//	int action;
//	float predict;
//	float predictError;
	int fitness;				//ich wei� nicht,ob fitness float ist, weil man fitness mit bestimmten Formel kakulation kann.!!!!!!!!!!!!!!
	double crossoverRate;
	double mutationRate;
	Random rand=new Random();
	GAParameter[] gaChildren;
	ArrayList<GAParameter> arrayGA=new ArrayList<GAParameter>();	//noch nicht benutzt, nur definiert.
	
	
	
	public Population(){
		
	}
	//,int action,float predict,float predictError
	public Population(GAParameter g,int fitness,double crossover,double mutation){
		this.g=g;
//		this.predict=predict;
//		this.predictError=predictError;
//		this.action=action;
		this.fitness=fitness;
		this.crossoverRate=crossover;
		this.mutationRate=mutation;
	}
	
	//parents suchen, um crossover und mutation auszuf�hren
	//Benutzt man fitness, um Parents zu w�hlen. Muss man Population oder GAParameter nach fitness sortieren.
	//!!!!!!!noch nicht fertig!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public GAParameter[] selectParents(){
		GAParameter parent1 = null;
		GAParameter parent2 = null;
		int highestfitness = Integer.MIN_VALUE;
		GAParameter[] parents=new GAParameter[2];
		
		for(GAParameter gap : this.arrayGA){
			if(gap.getFitness() > highestfitness){
				parent1 = gap;
				highestfitness = gap.getFitness();
			}
		}
		parents[0] = parent1;
		
		for(GAParameter gap : this.arrayGA){
			if(gap.getFitness() > highestfitness && gap != parent1){
				parent2 = gap;
				highestfitness = gap.getFitness();
			}
		}
		parents[1] = parent2;
		return parents;
	}
	

	
	//Zwei Methoden, um crossover und mutation auszuf�hren. Also Eingaben sind unterschiedlich.
	//Anhand der Rate von crossover und mutation f�hren die GA, also als ivolve gezeichent.
	/*wenn die Zahl kleiner als crossover rate, dann f�rht man crossover aus.
	 * wenn die Zahl kleiner als mutation rate, dann f�rht man mutation aus.
		crossover und mutation k�nnen gleichzeitig ausf�hren.
		
	 * wenn die Zahl kleiner als mutation rate, dann f�rht man mutation aus.
		wenn crossover kann nicht ausf�hren, kann mutation noch einzig ausf�hren.
	 * 
	 */
	public GAParameter[] ivolve(GAParameter parent1,GAParameter parent2){
		GAParameter child=new GAParameter();
		GAParameter[] children={parent1,parent2};
		GAParameter[] parents={parent1,parent2};
		if (rand.nextFloat() <= crossoverRate){			
			
			children=child.crossover(parents);
			if(rand.nextFloat()<=mutationRate){
				children=child.mutate(children);
			}
			gaChildren=children;
		}else{
			//wenn die Zahl kleiner als mutation rate, dann f�rht man mutation aus.
			//hier wenn crossover kann nicht ausf�hren, kann mutation noch einzig ausf�hren.
			if(rand.nextFloat()<=mutationRate){
				children=child.mutate(parents);
			}
			gaChildren=children;
		}
		return gaChildren;
	}	
	public GAParameter[] ivolve(GAParameter[] parent12){
		GAParameter child=new GAParameter();
		GAParameter[] children=new GAParameter[2];
//		GAParameter[] parents=parent12;
		if (rand.nextFloat() <= crossoverRate){				
			children=child.crossover(parent12);
			if(rand.nextFloat()<=mutationRate){
				children=child.mutate(children);
			}
			gaChildren=children;
		}else{
			if(rand.nextFloat()<=mutationRate){
				children=child.mutate(parent12);
			}
			gaChildren=children;
		}
		return gaChildren;
	}

	//set Methode
	public double setCrossover(double crossover) {
		this.crossoverRate=crossover;
		return this.crossoverRate;
	}
	public double setMutation(double mutation) {
		this.mutationRate=mutation;
		return this.mutationRate;
	}
	
	
	//get Methode
	public GAParameter getGAParameter(){
		return this.g;
	}	
//	public int getAction(){
//		return this.action;
//	}
//	public float getPredict(){
//		return this.predict;
//	}
//	public float getPredictError(){
//		return this.predictError;
//	}	
	public int getFitness(){
		return this.fitness;
	}	
	public double getCrossover() {
		return this.crossoverRate;
	}
	public double getMutation() {
		return this.mutationRate;
	}
	
	
	
	
	

}
