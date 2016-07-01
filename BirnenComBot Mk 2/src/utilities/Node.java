package utilities;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import bwapi.Game;
import bwapi.Position;
import bwta.Chokepoint;

public class Node {
	//Speichert BWAPI Region und die Information aus BWTA ob es ein Chokepoint ist
	public bwapi.Region region;
	boolean choke = false;
	Position chokeposition = null;
	
	public Node(bwapi.Region region, List<Chokepoint> chokepoints, Game game){
		//TODO: Testen ob Chokepoints korrekt erkannt werden
		
		this.region = region;
		Iterator<Chokepoint> it = chokepoints.iterator();
//		System.out.println("Start der While Schleife!");

		for(Chokepoint cp :  chokepoints){
			
			if(game.getRegionAt(cp.getCenter()).getID() == this.region.getID()){
//				System.out.println("Chokepoint entdeckt!");
				this.choke = true;
				this.chokeposition = cp.getCenter();
				break;
				
			}
		}
//		System.out.println("Node erstellt!");
		
		
	}
	
	
	public Position getChokePosition(){
			return this.getChokePosition();
	}
	
	public bwapi.Region getRegion(){
		return this.region;
	}
	
	public boolean getChoke(){
		return this.choke;
	}
	
	

}
