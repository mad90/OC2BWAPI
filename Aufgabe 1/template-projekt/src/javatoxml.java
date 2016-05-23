
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;

public class javatoxml {
	
	//Werte distance, cooldown, action, prediction, predictionerror und fitness

	public static void main(String[] args) {
		// dateipfadangabe - Pfad anpassen!

					File xmlfile = new File("C:\\Users\\Surface Pro 3\\Desktop\\settings.xml");

		try {
			

			// datei existiert nicht
			if (!xmlfile.exists()) {
				// root Element
				Element settings = new Element("settings");
				// macht settings zu parameter von document
				Document document = new Document(settings);

				//settingparameter
				Element action = new Element("action");

				action.setAttribute(new Attribute("setid", "1"));
				
				
				//distance
				action.addContent(new Element("distancevalue").setText("xy"));
				//cooldown
				action.addContent(new Element("cooldownvalue").setText("yes"));
				//action
				action.addContent(new Element("actionone").setText("1"));
				action.addContent(new Element("actiontwo").setText("2"));
				//prediction
				action.addContent(new Element("predictionvalue").setText("1.00"));
				//predictionerror
				action.addContent(new Element("predictionerrorvalue").setText("1"));
				//fitness
				action.addContent(new Element("fitnessvalue").setText("1.00old"));
				

/*
				//Neues Element für Fitness
				Element fitness = new Element("fitness");

				action.setAttribute(new Attribute("fitnessid", "1"));
				action.addContent(new Element("actionone").setText("1.00"));
							*/	
				
				
				// fügt attribut zu rootknoten
				document.getRootElement().addContent(action);

				XMLOutputter xmlOutput = new XMLOutputter();

				// konsolen ausgabe
				xmlOutput.output(document, System.out);

				// schreibe file

				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(document, new FileWriter("C:\\Users\\Surface Pro 3\\Desktop\\settings.xml"));

				System.out.println("settings Datei erstellt");

				// datei existiert
			} else {
				System.out.println("settings Datei bereits vorhanden");
				
				
				
				//überschreiben einfach neue Werte mitgeben				
			
				// root Element
				Element settings = new Element("settings");
				// macht settings zu parameter von document
				Document document = new Document(settings);

				//action element erstellen + attribut
				Element action = new Element("action");

				action.setAttribute(new Attribute("setid", "1"));
				
				
				//distance
				action.addContent(new Element("distancevalue").setText("xy"));
				//cooldown
				action.addContent(new Element("cooldownvalue").setText("yes"));
				//action
				action.addContent(new Element("actionone").setText("1"));
				action.addContent(new Element("actiontwo").setText("2"));
				//prediction
				action.addContent(new Element("predictionvalue").setText("1.00"));
				//predictionerror
				action.addContent(new Element("predictionerrorvalue").setText("1"));
				//fitness
				action.addContent(new Element("fitnessvalue").setText("1.00new"));
				

				// fügt attribut zu rootknoten
				document.getRootElement().addContent(action);

				XMLOutputter xmlOutput = new XMLOutputter();

				// konsolen ausgabe
				xmlOutput.output(document, System.out);

				// schreibe file

				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(document, new FileWriter("C:\\Users\\Surface Pro 3\\Desktop\\settings.xml"));

				System.out.println("settings Datei überschrieben");
				
				
				}
				
			if (xmlfile.exists()){
			
			xmltojava readxml= new xmltojava();
			readxml.getXml();
			
		}
		}

		catch (Exception e) {
			System.out.println("settings Datei konnte nicht erstellt werden");
		}

	}

}
