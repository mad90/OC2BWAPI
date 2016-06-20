
import java.util.Random;
import java.util.Scanner;

import XCSPackage.PopulationEntry;

import java.util.ArrayList;
import java.io.*;

public class Population implements Serializable {
	
//	GAParameter g;
//	int action;
//	float predict;
//	float predictError;
//	int fitness;				//ich weiß nicht,ob fitness float ist, weil man fitness mit bestimmten Formel kakulation kann.!!!!!!!!!!!!!!
	static double crossoverRate=0.9;
	static double mutationRate=0.2;
	Random rand=new Random();
//	GAParameter[] gaChildren;
	ArrayList<GAParameter> arrayGA=new ArrayList<GAParameter>();	//noch nicht benutzt, nur definiert.
	
	
	
	public Population(){
		this.arrayGA.add(new GAParameter());
	}
	//,int action,float predict,float predictError
	public Population(ArrayList<GAParameter> g){
		this.arrayGA=g;
//		this.g=g;
//		this.predict=predict;
//		this.predictError=predictError;
//		this.action=action;
//		this.fitness=fitness;
//		this.crossoverRate=crossover;
//		this.mutationRate=mutation;
	}
	
	//parents suchen, um crossover und mutation auszuführen
	//Benutzt man fitness, um Parents zu wählen. Muss man Population oder GAParameter nach fitness sortieren.
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
	

	
	//Zwei Methoden, um crossover und mutation auszuführen. Also Eingaben sind unterschiedlich.
	//Anhand der Rate von crossover und mutation führen die GA, also als ivolve gezeichent.
	/*wenn die Zahl kleiner als crossover rate, dann fürht man crossover aus.
	 * wenn die Zahl kleiner als mutation rate, dann fürht man mutation aus.
		crossover und mutation können gleichzeitig ausführen.
		
	 * wenn die Zahl kleiner als mutation rate, dann fürht man mutation aus.
		wenn crossover kann nicht ausführen, kann mutation noch einzig ausführen.
	 * 
	 */
	public GAParameter[] evolve(GAParameter parent1,GAParameter parent2){
		GAParameter[] gaChildren;
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
	public GAParameter[] evolve(GAParameter[] parent12){
		GAParameter[] gaChildren;
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
	
	public String toString(){
		String s="";
		for(GAParameter g:this.arrayGA){
			s+=g.toString();
		}
		return s;
	}
	
	public void writeToFlatFile() throws FileNotFoundException{
		try(  PrintWriter out = new PrintWriter("Population.txt")  ){
			
			String s = "";
			for(GAParameter e: this.arrayGA){
				s+=e.toString();
								
				
			}
			out.println(s);
		}
		
	}
	
	public ArrayList<GAParameter> readFromFlatFile() throws FileNotFoundException{
		ArrayList<GAParameter> populationList = new ArrayList<GAParameter>();
		
		boolean notEnd = false;
		Scanner file = new Scanner(new File("Population.txt"));
		while(file.hasNextLine()&& notEnd){
		
			String split[] = file.nextLine().split(", ");
			
//			int d = Integer.parseInt(split[0]);
//			int c = Integer.parseInt(split[1]);
//			int a = Integer.parseInt(split[2]);
			double[] dGA=new double[11];
			
			for(int i=0;i<dGA.length;i++){
				dGA[i]=Double.parseDouble(split[i]);
			}
			int f=Integer.parseInt(split[dGA.length]);
			
			
//			
//			double p = Double.parseDouble(split[3]);
//			double pe =  Double.parseDouble(split[4]);
//			double f =  Double.parseDouble(split[5]);
			GAParameter e = new GAParameter();
			
			populationList.add(e);
			System.out.println("Zeile eingelesen!");
			
			if(!file.hasNextLine()){
				notEnd = false;
			}
			


	}
	
	public ArrayList<GAParameter> getArrayGA() {
		return arrayGA;
	}
	public void setArrayGA(ArrayList<GAParameter> arrayGA) {
		this.arrayGA = arrayGA;
	}

	//set Methode
//	public double setCrossover(double crossover) {
//		this.crossoverRate=crossover;
//		return this.crossoverRate;
//	}
//	public double setMutation(double mutation) {
//		this.mutationRate=mutation;
//		return this.mutationRate;
//	}
	
	
	//get Methode
//	public GAParameter getGAParameter(){
//		return this.g;
//	}	
//	public int getAction(){
//		return this.action;
//	}
//	public float getPredict(){
//		return this.predict;
//	}
//	public float getPredictError(){
//		return this.predictError;
//	}	
//	public int getFitness(){
//		return this.fitness;
//	}	
//	public double getCrossover() {
//		return this.crossoverRate;
//	}
//	public double getMutation() {
//		return this.mutationRate;
//	}
//	
	
	
	
	

}
