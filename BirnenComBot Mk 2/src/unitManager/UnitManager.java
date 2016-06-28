package unitManager;

import java.util.List;

import bwapi.Player;
import bwapi.Unit;

public class UnitManager {
	//TODO: UnitManager Überklasse implementieren
	
	public static Unit unit;
	public boolean startLeft;
	private Player self;
	
	public UnitManager(Unit unit, boolean startLeft, Player self){
		this.unit = unit;
		this.startLeft = startLeft;
		this.self = self;
	}
	
	public void doStep(boolean offensive){
		
	}
	
	public Unit getUnit(){
		return this.unit;
	}
	
	public boolean getStartLeft(){
		return this.startLeft;
	}
	
    public boolean emptyOrFriendly(List<Unit> units){
    	boolean eof = true;
    	
    	if(units.isEmpty()){
    		return eof;
    	}

    	for(Unit u : units){
    		if(!u.getPlayer().equals(this.self)){
    			eof = false;
    			break;
    		}
    	}
    	return eof;
    	   	
    }
	
	
	

}
