package XCSPackage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XCS {
	 PopulationSet population;
	 ArrayList<PopulationEntry> MatchSet;
	 ArrayList<PopulationEntry> ActionSet;
	 //schaltet spielübergreifendes Lernen ein -> Zugriff auf XCSMemory
	 boolean useMemory = true;
	 boolean noDoubleEntry = true;
	 int minEntriesMatchSet = 3; //Mindest Menge an Sets die im MatchSet sein müssen
	 
	
	 //int minAmntActions = 2; //Mindest Anzahl an verschiedenen Aktionen im MatchSet
	
	
	
	public XCS(int startsize,int maxsize)  {
		if(useMemory){
			try {
				this.population = new PopulationSet(readFromFlatFile(), maxsize);
				System.out.println("Verwende gemerkte Population");
				if(this.population.isEmpty()){
					
					//Falls FlatFile nicht gelesen werden konnte, oder leer ist
					this.population = new PopulationSet(startsize, maxsize);
					System.out.println("Population war leer");
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
		this.population = new PopulationSet(startsize ,maxsize);
		System.out.println("Verwende zufällige Population");
		}
		
	}
	
	public int xcsLoop(int inputdistance, int inputcooldown){
		
//		 ArrayList<PopulationEntry> MatchSet = new ArrayList<PopulationEntry>();
//		 ArrayList<PopulationEntry> ActionSet = new ArrayList<PopulationEntry>();
		 
//		 System.out.println("Population vor MatchSet: " + this.population.PopulationList.size());
		 this.MatchSet = this.population.getMatchSet(inputdistance, inputcooldown);
	 
		 //Wenn MatchSet leer -> Covering
		 //Covering direkt auf das MatchSet ausführen!
		 System.out.println("Pattern: "+inputdistance +", "+ inputcooldown+ "\n" +"MatchSet vor Covering" + this.MatchSet.toString());
		 
		 this.MatchSet = covering(inputdistance, inputcooldown, this.MatchSet);
		 System.out.println(this.MatchSet.toString());
		 //System.out.println("Pattern: "+inputdistance +", "+ inputcooldown+ "\n" + this.MatchSet.toString());
		 float[] predictionarray = population.calculatePredictionArray(this.MatchSet);
		 for(int i=0; i< predictionarray.length; i++){
			 System.out.println("PredictionArray: " +predictionarray[i]);
			 
		 }
		 population.ChoseAction(predictionarray);
		 this.ActionSet = population.createActionSet(this.MatchSet);
		 
		 return population.getNextChosenAction();
		 
	
		
	}
	
//	public void distributeReward(double reward){
//		this.updateSet(reward);
//	}
	
	public void updatePopulation(){
		//Zunächst alle Einträge in der Population mit dem selben Pattern und Action wie im ActionSet löschen
		for(PopulationEntry as : ActionSet){
			for(PopulationEntry e: population.PopulationList){
				if(as.patterndistance == e.patterndistance && as.patterncooldown == e.patterncooldown && as.action == e.action){
					this.population.PopulationList.remove(e);
				}
				
				
			}
		}
		
		//Alle Einträge des ActionSets in die Population kopieren
		for(PopulationEntry as: ActionSet){
			this.population.PopulationList.add(as);
		}
		
	
	}
	public void updateActionSet(double reward){
		//System.out.println("Starte UpdateActionSet");
		//Passt die Werte von Prediction, PredictionError und Fitness in Abhängigkeit des Rewards an
		double totalk = 0;
		double beta = this.population.beta;
		double alpha = this.population.alpha;
		double v = this.population.v;
		double errortarget = this.population.errortarget;
		
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
	
//	public void writePopulationSetToFile() throws FileNotFoundException{
//
//		// dateipfadangabe - Pfad anpassen!
//					String filename = "XCS Memory.xml";
//					File xmlfile = new File(filename);
//
//		try {
//			
//
//			// datei existiert nicht
//			if (!xmlfile.exists()) {
//				// root Element
//				Element settings = new Element("settings");
//				// macht settings zu parameter von document
//				Document document = new Document(settings);
//
//				//settingparameter
//				Element action = new Element("action");
//
//				action.setAttribute(new Attribute("setid", "1"));
//				
//				for(PopulationEntry e : this.population.PopulationList){
//				//distance
//				action.addContent(new Element("distancevalue").setText(String.valueOf(e.patterndistance)));
//				//cooldown
//				action.addContent(new Element("cooldownvalue").setText(String.valueOf(e.patterncooldown)));
//				//action
//				action.addContent(new Element("actionone").setText(String.valueOf(e.action)));
//				//action.addContent(new Element("actiontwo").setText("2"));
//				//prediction
//				action.addContent(new Element("predictionvalue").setText(String.valueOf(e.prediction)));
//				//predictionerror
//				action.addContent(new Element("predictionerrorvalue").setText(String.valueOf(e.predictionError)));
//				//fitness
//				action.addContent(new Element("fitnessvalue").setText(String.valueOf(e.fitness)));
//				
//
//				// fügt attribut zu rootknoten
//				document.getRootElement().addContent(action);
//				}
//				
//
///*
//				//Neues Element für Fitness
//				Element fitness = new Element("fitness");
//
//				action.setAttribute(new Attribute("fitnessid", "1"));
//				action.addContent(new Element("actionone").setText("1.00"));
//							*/	
//				
//				
//				// fügt attribut zu rootknoten
//				document.getRootElement().addContent(action);
//
//				XMLOutputter xmlOutput = new XMLOutputter();
//
//				// konsolen ausgabe
//				xmlOutput.output(document, System.out);
//
//				// schreibe file
//
//				xmlOutput.setFormat(Format.getPrettyFormat());
//				xmlOutput.output(document, new FileWriter("filename"));
//
//				//System.out.println("settings Datei erstellt");
//
//				// datei existiert
//			} else {
//				System.out.println("settings Datei bereits vorhanden");
//				
//				
//				
//				//überschreiben einfach neue Werte mitgeben				
//			
//				// root Element
//				Element settings = new Element("settings");
//				// macht settings zu parameter von document
//				Document document = new Document(settings);
//
//				//action element erstellen + attribut
//				Element action = new Element("action");
//
//				action.setAttribute(new Attribute("setid", "1"));
//				
//				for(PopulationEntry e : this.population.PopulationList){
//				//distance
//				action.addContent(new Element("distancevalue").setText(String.valueOf(e.patterndistance)));
//				//cooldown
//				action.addContent(new Element("cooldownvalue").setText(String.valueOf(e.patterncooldown)));
//				//action
//				action.addContent(new Element("actionone").setText(String.valueOf(e.action)));
//				//action.addContent(new Element("actiontwo").setText("2"));
//				//prediction
//				action.addContent(new Element("predictionvalue").setText(String.valueOf(e.prediction)));
//				//predictionerror
//				action.addContent(new Element("predictionerrorvalue").setText(String.valueOf(e.predictionError)));
//				//fitness
//				action.addContent(new Element("fitnessvalue").setText(String.valueOf(e.fitness)));
//				
//
//				// fügt attribut zu rootknoten
//				document.getRootElement().addContent(action);
//				}
//
//				XMLOutputter xmlOutput = new XMLOutputter();
//
//				// konsolen ausgabe
//				xmlOutput.output(document, System.out);
//
//				// schreibe file
//
//				xmlOutput.setFormat(Format.getPrettyFormat());
//				xmlOutput.output(document, new FileWriter("filename"));
//
//				System.out.println("settings Datei überschrieben");
//				
//				
//				}
//				
//			if (xmlfile.exists()){
//			
//			xmltojava readxml= new xmltojava();
//			readxml.getXml();
//			
//		}
//		}
//
//		catch (Exception e) {
//			System.out.println("settings Datei konnte nicht erstellt werden");
//		}
//		
//	}
	
//	public void readPopulationSetFromFile() throws FileNotFoundException{
//		
//			  
//			  // Leseobjekt
//			     SAXBuilder saxBuilder = new SAXBuilder();
//			     
//			     //xmlfile - Pfad anpassen!
//			     File xmlfile = new File("XCS Memory.xml");
//			   
//
//			  try {
//				  
//			   // File zu Dokumenten konvertierung
//			   Document document = saxBuilder.build(xmlfile);
//			   
//			   // Get Rood Node
//			   Element rootNode = document.getRootElement();
//			   
//			   // XML Elemente in einer Liste
//			   //Elemente für Aktion
//			      List<Element> settingList = rootNode.getChildren("action");
//			      
//			      //System.out.println("Test");
//			      
//			      // Einzelnes Auslesen aller Elemente
//			      for(int i=0;i<=settingList.size()-1;i++){
//			    	 
//			       Element element = settingList.get(i);
//			       System.out.println(element.getChildText("distancevalue"));
//			       System.out.println(element.getChildText("cooldownvalue"));
//			       System.out.println(element.getChildText("actionone"));
//			       System.out.println(element.getChildText("actiontwo"));
//			       System.out.println(element.getChildText("predictionvalue"));
//			       System.out.println(element.getChildText("predictionerrorvalue"));
//			       System.out.println(element.getChildText("fitnessvalue"));
//			       
//			      }
//			     
//			  } catch (Exception e) {
//			   // TODO Auto-generated catch block
//			   e.printStackTrace();
//			  
//			 }
//		
//	}
	
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
		//System.out.println("Einlesen Fertig!");
		return populationList;
		
	}
	
	
	
	
	
	public void writeToFlatFile() throws FileNotFoundException{
		try(  PrintWriter out = new PrintWriter("XCS Memory.txt")  ){
			
			String s = "";
			for(PopulationEntry e: this.population.PopulationList){
								
				s += e.patterndistance + " "+ e.patterncooldown +" " + e.action + " " + e.prediction +" "+ e.predictionError + " " + e.fitness +"\r\n";
				
			}
			out.println(s);
		}
		
	}
	
	
	
//Nach Dupletten prüfen
	public boolean equalpopulation(boolean noDoubleEntry){
		
		for(PopulationEntry e: this.population.PopulationList){
			for(PopulationEntry c: this.population.PopulationList){
	if (e.patterndistance == c.patterndistance && e.patterncooldown == c.patterncooldown && e.action == c.action){
	noDoubleEntry = true;
	break;
	}
	
	else{
	noDoubleEntry = false;
	}
			}
		}
			
	return noDoubleEntry;
				
	}
	
	//cleanen der population
	public void doublecheck(){
		
		if (equalpopulation(noDoubleEntry)==true){
		for(PopulationEntry e: this.population.PopulationList){
			if (e.patterndistance == e.patterndistance && e.patterncooldown == e.patterncooldown && e.action == e.action){
				//remove
			//e.remove();
				}
				
				else{
					//nur zum debuggen
				System.out.println("Keine Duplikate vorhanden");
				}
				
			}
			
			
		}
		
				
	}

	
	

}
