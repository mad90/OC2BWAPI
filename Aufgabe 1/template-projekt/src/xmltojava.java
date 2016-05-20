

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class xmltojava {
	public void getXml(){
		  
		  // Leseobjekt
		     SAXBuilder saxBuilder = new SAXBuilder();
		     
		     //xmlfile - Pfad anpassen!
		     File xmlfile = new File("C:\\Users\\Surface Pro 3\\Desktop\\settings.xml");
		   

		  try {
			  
		   // File zu Dokumenten konvertierung
		   Document document = saxBuilder.build(xmlfile);
		   
		   // Get Rood Node
		   Element rootNode = document.getRootElement();
		   
		   // XML Elemente in einer Liste
		   //Elemente für Aktion
		      List<Element> settingList = rootNode.getChildren("action");
		      
		      //System.out.println("Test");
		      
		      // Einzelnes Auslesen aller Elemente
		      for(int i=0;i<=settingList.size()-1;i++){
		    	 
		       Element element = settingList.get(i);
		       System.out.println(element.getChildText("distancevalue"));
		       System.out.println(element.getChildText("cooldownvalue"));
		       System.out.println(element.getChildText("actionone"));
		       System.out.println(element.getChildText("actiontwo"));
		       System.out.println(element.getChildText("predictionvalue"));
		       System.out.println(element.getChildText("predictionerrorvalue"));
		       System.out.println(element.getChildText("fitnessvalue"));
		       
		      }
		     
		  } catch (Exception e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  
		 }
	
	}
}


