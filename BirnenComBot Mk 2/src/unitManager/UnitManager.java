package unitManager;

import java.util.HashSet;
import java.util.List;

import bwapi.Player;
import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.Unit;
import utilities.Vector;

public class UnitManager {
	//TODO: UnitManager Überklasse implementieren
	//	Punkt rechts oben 2512/976 unten: 2608,2032
	//	Links: unten 1296,2192 oben: 1456,848
	
	public Unit unit;
	public boolean startLeft;
	public Player self;
	boolean enemyInSightRange = false;
	public HashSet<Unit> enemyUnits;
	public Position targetPos;
	public Position[] waypoints=  new Position[2];
	public boolean targetchanged = false;
	public boolean reachedlastwp = false;
	public int wpindex = 0;
	
	public UnitManager(Unit unit, boolean startLeft, Player self, HashSet<Unit> eu, Position target){
		this.unit = unit;
		this.startLeft = startLeft;
		this.self = self;
		this.enemyUnits = eu;
		this.targetPos = target;
		
		if(startLeft){
			if(this.targetPos.equals(new Position(3440, 2720))){ //Unten -> 1. Unterer 2. Oberer Rechts
				this.waypoints = new Position[]{new Position(2608,2032), new Position(2512,976)};
			}
			else if(this.targetPos.equals(new Position(3472, 384))){ //Oben -> 1. Oberer Wegpunkt 2. Unterer Rechts
				this.waypoints = new Position[]{new Position(2512,976), new Position(2608,2032)};
			}
		}
		else{
			if(this.targetPos.equals(new Position(624, 2752))){ //Unten -> 1. Unterer 2. Oberer Links
				this.waypoints = new Position[]{new Position(1400,2192), new Position(1456,848)};
			}
			else if(this.targetPos.equals(new Position(3472, 384))){ //Oben -> 1. Oberer Wegpunkt 2. Unterer Links
				this.waypoints = new Position[]{new Position(1456,848), new Position(1296,2192)};
			}
			
		}
		
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
	
	public Position runAway(HashSet<Unit> units){
		Vector away = Vector.positionToVector(this.unit.getPosition());
		
		for(Unit u : units){
			
			away.add(new Vector(u.getPosition(), this.unit.getPosition()));
		}
		System.out.println("Away Vector: " + away.toString());
		
		away.normalize().scalarMultiply(50);
		System.out.println("Away Vector Normalized: " + away.toString());
		away = away.add(Vector.positionToVector(this.unit.getPosition()));
//		this.unit.move(away.toPosition(), true);
		return away.toPosition();
	}
	
//	protected Unit getClosestEnemy() {
//		Unit result = null;
//		double minDistance = Double.POSITIVE_INFINITY;
//		for (Unit enemy : this.enemyUnits) {
//			double distance = getDistance(enemy);
//			if (distance < minDistance) {
//				minDistance = distance;
//				result = enemy;
//			}
//		}
//
//		return result;
//	}
	
	public PositionOrUnit getClosestEnemy(){
		PositionOrUnit result = null;
		
		double minDistance = Double.POSITIVE_INFINITY;
		for (Unit enemy : this.unit.getUnitsInWeaponRange(this.unit.getType().groundWeapon())) {
			double distance = getDistance(enemy);
			if (distance < minDistance) {
				minDistance = distance;
				result = new PositionOrUnit(enemy);
			}
		}
		if(result == null){
			result = new PositionOrUnit(this.targetPos);
		}
		return result;
		
	}
	

	
	
	
	protected double getDistance(Unit enemy) {
		return this.unit.getPosition().getDistance(enemy.getPosition());
	}
	
	
	public boolean attackLowestEnemy(){
		List<Unit> enemiesinrange = this.unit.getUnitsInRadius(getUnit().getType().sightRange());
		int lowestHP = Integer.MAX_VALUE;
		Unit target = null;
		boolean attacks = false;
		
		for(Unit u :enemiesinrange){
			if(u.getHitPoints() < lowestHP){
				lowestHP = u.getHitPoints();
				target = u;
			}
		}
		
		if(target!= null){
			if(this.unit.getGroundWeaponCooldown() == 0 && !this.unit.isAttackFrame() && !this.unit.isStartingAttack()){
				if(getDistance(target) < this.unit.getType().groundWeapon().maxRange()){
					this.unit.attack(target);
					attacks  = true;
				}
			}
			
		}
		return attacks;
		
		
	}
	
	public int calculateEnemyUnitHpInSight(){
		int totalHP = 0;
		for(Unit u: this.unit.getUnitsInRadius(this.unit.getType().sightRange())){
			if(u.getPlayer() != self && !u.getType().isBuilding()){
				totalHP += u.getHitPoints();
			}
			
		}
		
		return totalHP;
		
	}
	
	public Position runAwayFromTarget(Unit target, int distance){
		Vector away =  new Vector(target.getPosition(), this.unit.getPosition());
		away.normalize();
		away.scalarMultiply(distance);
		
		return new Position(this.unit.getX()+(int) away.getX(), this.unit.getY()+(int) away.getY());
		
		
	}

	public void setTargetPos(Position newTarget){
		this.targetchanged = true;
		this.targetPos = newTarget;
	}
	
	

	
	
	

}
