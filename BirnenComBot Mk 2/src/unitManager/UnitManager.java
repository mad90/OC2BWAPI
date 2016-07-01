package unitManager;

import java.util.List;

import bwapi.Player;
import bwapi.Unit;

public class UnitManager {
	//TODO: UnitManager Überklasse implementieren
	
	public Unit unit;
	public boolean startLeft;
	public Player self;
	boolean enemyInSightRange = false;
	
	public UnitManager(Unit unit, boolean startLeft, Player self){
		this.unit = unit;
		this.startLeft = startLeft;
		this.self = self;
	}
	
	public void doStep(boolean offensive){
		
//		System.out.println("Marine!"+ this.unit.getType().toString());
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
    
	public void checkEnemyInSight(){
		List<Unit> nearbyUnits = this.unit.getUnitsInRadius(this.unit.getType().sightRange());
		boolean enemy = false;
		
		for(Unit u: nearbyUnits){
			if(u.getPlayer() != this.self){
				enemy= true;
				break;
			}
		}
		this.enemyInSightRange = enemy;
	}
	
	public boolean getEnemyInSightRange(){
		return this.enemyInSightRange;
	}
	
	public void turn(){
		
	}
	
	
	

}
