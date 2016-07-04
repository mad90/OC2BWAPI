package boiding;

import java.util.Random;
import java.util.Scanner;



import java.util.ArrayList;
import java.util.HashMap;
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
		//Erstellt eine Population mit einem zufälligen GAParameter
		this.arrayGA.add(new GAParameter());
	}
	//,int action,float predict,float predictError
	public Population(ArrayList<GAParameter> g){
		this.arrayGA=g;

	}
	
	public Population(GAParameter... gaparameter){
		//Kontruktor der beliebig viele GAParameter annehmen kann
		for(GAParameter g: gaparameter){
			this.arrayGA.add(g);
		}
	}
	
	//parents suchen, um crossover und mutation auszuführen
	//Benutzt man fitness, um Parents zu wählen. Muss man Population oder GAParameter nach fitness sortieren.

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
	public static GAParameter[] evolve(GAParameter parent1,GAParameter parent2){
		Random rand=new Random();
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
	public static GAParameter[] evolve(GAParameter[] parent12){
		Random rand=new Random();
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
			
			String s = toString();
			out.println(s);
		}
		
	}
	
	public static ArrayList<GAParameter> readFromFlatFile() throws FileNotFoundException{
		ArrayList<GAParameter> populationList = new ArrayList<GAParameter>();
		
		boolean notEnd = true;
		Scanner file = new Scanner(new File("read/Population.txt"));
//		file.nextLine();
//		System.out.println("hasNextLine(): " + file.hasNextLine());

		while(file.hasNextLine()&& notEnd){
			String split[] = file.nextLine().split(" ");
//			System.out.println("Split[0] " + split[0]);
//			System.out.println("Split[] length: " + split.length);
			
//			int d = Integer.parseInt(split[0]);
//			int c = Integer.parseInt(split[1]);
//			int a = Integer.parseInt(split[2]);
			double[] dGA=new double[11];
//			System.out.println("Erster Double: " + dGA[0]);
			if(!split[0].isEmpty()){
			for(int i=0;i<dGA.length;i++){
				dGA[i]=Double.parseDouble(split[i]);
			}
			int f = Integer.parseInt(split[dGA.length]);
			populationList.add(new GAParameter(dGA,f));
//			System.out.println("hasNextLine(): "+file.hasNextLine());
//			file.nextLine();
			if(!file.hasNextLine()){
				notEnd = false;
			}
//			file.nextLine();
			}
			else{
				return populationList;
			}
//			
//			double p = Double.parseDouble(split[3]);
//			double pe =  Double.parseDouble(split[4]);
//			double f =  Double.parseDouble(split[5]);

			

			
		}
		return populationList;
	}
	
	public static void appendToFlatFile(GAParameter... gap){
		try
		{
		    String filename= "write/Population.txt";
		    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
		    for(GAParameter g: gap){
		    	fw.write(g.toString());//appends the string to the file
		    }
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
		
		
		
	}
	
	public static int getNumberOfEntriesInFlatFile(){
		try {
			Scanner file = new Scanner(new File("Population.txt"));
			boolean notEnd = true;
			int i = 0;
			while(file.hasNextLine() && notEnd){
				i++;
				if(!file.hasNextLine()){
					notEnd = false;
				}
				else{
					file.nextLine();
				}
			}
			return i-1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}
	}
	
	public GAParameter chooseGAParameter(){
		int totalFitness = 0;
		int[] keyareas = new int[this.getArrayGA().size()];
		HashMap<Integer,GAParameter> hashmap = new HashMap<Integer, GAParameter>();
		int i = 0;
		Random random = new Random();
		
		for(GAParameter gap: this.arrayGA){
			totalFitness += gap.getFitness();
			hashmap.put(totalFitness, gap);
			keyareas[i] = totalFitness;
			i++;
		}
		int chosen = random.nextInt(totalFitness);
		int key = Integer.MIN_VALUE;
		
		for(i = 0; i <= keyareas.length; i++){
			if(chosen < keyareas[i]){
				key= keyareas[i];
				break;
			}
			
		}
		
		if(key != Integer.MIN_VALUE){
			return hashmap.get(key);
		}
		else{
			return null;
		}
		
	}
	
	public void checkMutation(){
		if(rand.nextDouble() > this.mutationRate){
			
			
		}
	}
	
	
	
	
	public ArrayList<GAParameter> getArrayGA() {
		return arrayGA;
	}
	public void setArrayGA(ArrayList<GAParameter> arrayGA) {
		this.arrayGA = arrayGA;
	}
	
	


	
	
	
	

}
