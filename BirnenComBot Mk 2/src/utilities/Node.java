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
	Position chokeposition;
	
	public Node(bwapi.Region region, List<Chokepoint> chokepoints, Game game){
		
		this.region = region;
		Iterator<Chokepoint> it = chokepoints.iterator();
		
		while(!choke && it.hasNext()){
			Chokepoint cp = it.next();
			if(game.getRegionAt(cp.getPoint()).equals(region)){
				this.choke = true;
				this.chokeposition = cp.getPoint();
			}
			
			
		}
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
