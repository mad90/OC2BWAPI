import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FlatFileParserReader {
	
	
//	
//	public void writeToFlatFile() throws FileNotFoundException{
//		try(  PrintWriter out = new PrintWriter("XCS Memory.txt")  ){
//			String s = "";
//			for(PopulationEntry e: this.population.PopulationList){
//				s += e.patterndistance + " "+ e.patterncooldown +" " + e.action + " " + e.prediction +" "+ e.predictionError + " " + e.fitness +"\r\n";
//			}
//			out.println(s);
//		}
//		
//	}
	
	public void readFromFlatFile() throws FileNotFoundException{
//		ArrayList<PopulationEntry> populationList = new ArrayList<PopulationEntry>();
		
		
		Scanner file = new Scanner(new File("XCS Memory.txt"));
		while(file.hasNextLine()){
		
			String split[] = file.nextLine().split(" ");
			
			int d = Integer.parseInt(split[0]);
			int c = Integer.parseInt(split[1]);
			int a = Integer.parseInt(split[2]);
			
			double p = Double.parseDouble(split[3]);
			double pe =  Double.parseDouble(split[4]);
			double f =  Double.parseDouble(split[5]);
			
			System.out.println(d +" "+ c +" "+ a +" "+ String.valueOf(p)+" "+String.valueOf(pe)+" "+ String.valueOf(f));
			file.nextLine();

	}
//		return populationList;
		
	}

}
