package unitManager;

import java.util.HashSet;
import java.util.List;

import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import utilities.Vector;

public class UnitManager {
	//TODO: UnitManager Überklasse implementieren
	
	public Unit unit;
	public boolean startLeft;
	public Player self;
	boolean enemyInSightRange = false;
	public HashSet<Unit> enemyUnits;
	public Position targetPos;
	
	public UnitManager(Unit unit, boolean startLeft, Player self, HashSet<Unit> eu, Position target){
		this.unit = unit;
		this.startLeft = startLeft;
		this.self = self;
		this.enemyUnits = eu;
		this.targetPos = target;
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
	
	public void runAway(List<Unit> units){
		Vector away = Vector.positionToVector(this.unit.getPosition());
		
		for(Unit u : units){
			
			away.add(new Vector(u.getPosition(), this.unit.getPosition()));
		}
		System.out.println("Away Vector: " + away.toString());
		
		away.normalize().scalarMultiply(100);
		System.out.println("Away Vector Normalized: " + away.toString());
		away = away.add(Vector.positionToVector(this.unit.getPosition()));
		this.unit.move(away.toPosition(), true);
		
	}
	
	protected Unit getClosestEnemy() {
		Unit result = null;
		double minDistance = Double.POSITIVE_INFINITY;
		for (Unit enemy : this.enemyUnits) {
			double distance = getDistance(enemy);
			if (distance < minDistance) {
				minDistance = distance;
				result = enemy;
			}
		}

		return result;
	}
	
	protected double getDistance(Unit enemy) {
		return this.unit.getPosition().getDistance(enemy.getPosition());
	}
	
	
	

	
	
	

}
