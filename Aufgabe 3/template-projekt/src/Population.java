
import java.util.Random;
import java.util.ArrayList;

public class Population {
	
	GAParameter g;
//	int action;
//	float predict;
//	float predictError;
	int fitness;				//ich weiß nicht,ob fitness float ist, weil man fitness mit bestimmten Formel kakulation kann.!!!!!!!!!!!!!!
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
	
	//parents suchen, um crossover und mutation auszuführen
	//Benutzt man fitness, um Parents zu wählen. Muss man Population oder GAParameter nach fitness sortieren.
	//!!!!!!!noch nicht fertig!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public GAParameter[] selectParents(){
		GAParameter[] parents=new GAParameter[2];
		for(int i=0;i<2;i++){
//			parents[i]=;
		}
		return parents;
	}
	

	
	//Zwei Methoden, um crossover und mutation auszuführen. Also Eingaben sind unterschiedlich.
	//Anhand der Rate von crossover und mutation führen die GA, also als ivolve gezeichent.
	/*wenn die Zahl kleiner als crossover rate, dann fürht man crossover aus.
	 * wenn die Zahl kleiner als mutation rate, dann fürht man mutation aus.
		crossover und mutation können gleichzeitig ausführen.
		
	 * wenn die Zahl kleiner als mutation rate, dann fürht man mutation aus.
		wenn crossover kann nicht ausführen, kann mutation noch einzig ausführen.
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
			//wenn die Zahl kleiner als mutation rate, dann fürht man mutation aus.
			//hier wenn crossover kann nicht ausführen, kann mutation noch einzig ausführen.
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
