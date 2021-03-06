package unitManager;

import java.util.HashSet;
import java.util.List;

import bwapi.Player;
import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import xcs.VultureXCS;

public class VultureManager extends UnitManager{
	
	public int spidermines = 3;
	public VultureXCS vultureXCS;
	public boolean isScouting = true;
	public boolean usedSpiderMineTrick = false;
	public boolean reachedwaypoint = false;
	private Position waypoint = new Position(2048, 1536);
	
	
	
	public VultureManager(Unit unit, boolean startLeft, Player self, HashSet<Unit> enemyUnits, Position target){
		super(unit, startLeft, self, enemyUnits, target);
		this.vultureXCS = new VultureXCS(5, 20);
		
		
	}
	
	private void useSpiderMine(Position target){
		getUnit().useTech(TechType.Spider_Mines, target);
	}
	
	@Override
	public void doStep(boolean offensive){
		if(offensive){
			try {
				PositionOrUnit target = getBestScoutingPosition();
				if(checkLayMine()){
					layMine();
				}
				else{
				if(target.isUnit()){
					
//					if(this.unit.getGroundWeaponCooldown() == 0 && !this.unit.isAttackFrame() && !this.unit.isStartingAttack() && !this.unit.isAttacking()){
						this.kite(target.getUnit());
//					}
//					else{
//						this.unit.move(target.getUnit().getPosition());
//					}
					
					
				}
				else if(target.isPosition()){
					if(!reachedwaypoint && this.unit.getPosition().getApproxDistance(waypoint) < 100){
						this.reachedwaypoint = true;
					}
					
					if(!reachedwaypoint){
						this.unit.move(waypoint);
					}
					else{
						if(!this.targetchanged || this.reachedlastwp){
							this.unit.move(target.getPosition());
						}
						else if(this.targetchanged && !this.reachedlastwp){
							if(this.unit.getDistance(this.waypoints[this.wpindex]) < 50){
								this.wpindex++;
								if(this.wpindex > this.waypoints.length -1){
									this.reachedlastwp = true;
								}
							}
							if(!this.reachedlastwp){
								this.unit.move(this.waypoints[this.wpindex]);
							}
							else{
								this.unit.move(target.getPosition());
							}
						
						}
					}
					
				}
				}
			
			
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			
		}
	}
		
		

		
//		checkEnemyInSight();
////		System.out.println("doStep() Vulture!");
//		System.out.println("Enemy in Sight:" + this.getEnemyInSightRange()); 
//		
//		if(offensive){
////			System.out.println("Scouting!");
//			System.out.println("Scouting: "+ this.unit.getType().toString()+ " ManagerType: "+ this.getClass().getName());
//			scouting();
//		}

	
	public PositionOrUnit getBestScoutingPosition(){
		//Wenn Gegner in Weaponrange gibt Gegner mit wenigster HP, sonst gibt TargetPos
		PositionOrUnit besttarget = new PositionOrUnit(this.targetPos);
//		List<Unit> nearunits = getUnit().getUnitsInRadius(getUnit().getType().groundWeapon().maxRange());
		List<Unit> nearunits = getUnit().getUnitsInRadius(getUnit().getType().sightRange());
		HashSet<Unit> enemies = new HashSet<Unit>();
		int lowestHP = Integer.MAX_VALUE;
//		System.out.println("Beginn erste Schleife");
		if(!nearunits.isEmpty()){
			for(Unit u: nearunits){
				if(u.getPlayer() != this.self){
					enemies.add(u);
				}
				
			}
		}
//		System.out.println("Beginn zweite Schleife");
		if((enemies != null || !enemies.isEmpty())){
			for(Unit enemy :  enemies){
				if(nearbyTank()){
					besttarget =  new PositionOrUnit(runAway(enemies));
					return besttarget;
				}
				
				if(enemy.getType() == UnitType.Terran_Vulture_Spider_Mine){
					//Wenn m�glich zuerst gegnerische Spidermines angreifen, bevor diese explodieren!
					besttarget = new PositionOrUnit(enemy);
					return besttarget;
				}
				else if(enemy.getHitPoints() < lowestHP){
					lowestHP = enemy.getHitPoints();
					besttarget = new PositionOrUnit(enemy);
					
				}
			}
			
		}
		else{
			besttarget = new PositionOrUnit(this.targetPos);
			
		}
		
		return besttarget;
	
	}

	
//	public void scouting(){
//		//Test
//		
//		if(!this.unit.isMoving() && !this.getEnemyInSightRange()){
//			getUnit().move(getScountingPosition());
//
//		}
//		else if(this.getEnemyInSightRange() && !this.unit.isMoving()){
//			runAway(this.unit.getUnitsInRadius(this.unit.getType().sightRange()));
//		}
//		
//	}
	
	public Position getScountingPosition(){
		//TODO: Scounting Target berechnen
		Position target = this.unit.getPosition();
		
		if(getStartLeft()){
			//TODO: Dynamische Position berechnen
			target = new Position(3440, 1552);
		}
		else{
			//TODO: Dynamische Position berechnen
			target = new Position(620, 1552);
			
		}
		
		return target;
	}
	
	public void spiderMineTrick(){
		this.usedSpiderMineTrick = true;
		
		useSpiderMine(this.unit.getPosition());
		useSpiderMine(this.unit.getTargetPosition());
		useSpiderMine(this.unit.getTargetPosition());
		
		
	}
	
	public boolean nearbyTank(){
		//12 ist Range Siege Mode Tank
		boolean tank = false;
		for(Unit u : this.unit.getUnitsInRadius(13)){
			if(u.getPlayer() != this.self && (u.getType() == UnitType.Terran_Siege_Tank_Tank_Mode || u.getType() == UnitType.Terran_Siege_Tank_Siege_Mode)){
				tank = true;
			}
		}
		return tank;
	}
	
	public void kite(Unit target){
		//Methode f�r Kiting, falls XCS nicht rechtzeitig funktioniert
		HashSet<Unit> enemies =  new HashSet<Unit>();
		for(Unit u: getUnit().getUnitsInRadius(getUnit().getType().sightRange())){
			if(u.getPlayer() == this.self && !u.getType().isBuilding()){
				enemies.add(u);
			}
		}
//		System.out.println("Enemies in Kite ist emptry " +enemies.isEmpty());
		if(this.unit.getGroundWeaponCooldown() == 0 && !this.unit.isAttackFrame() && !this.unit.isStartingAttack() && !this.unit.isAttacking()){
			this.unit.attack(target);
		}
		else if(!enemies.isEmpty() && this.unit.isInWeaponRange(target)){
//				System.out.println("RunAway!");
				this.unit.move(runAwayFromTarget(target, 16));
		}
		}
	

	public boolean checkLayMine(){
		HashSet<Unit> enemies = new HashSet<Unit>();
		List<Unit> nearunits = getUnit().getUnitsInRadius(getUnit().getType().sightRange());
		if(!nearunits.isEmpty()){
			for(Unit u: nearunits){
				if(u.getPlayer() != this.self){
					enemies.add(u);
				}
				
			}
		}
		else if(nearunits.isEmpty()){
			return false;
		}
		
		if(nearbyTank() && this.spidermines > 0){
			return true;
		
		}
		if(enemies.size() > 3){
			return true;
		}
		return false;
	}
	
	public void layMine(){
		HashSet<Unit> enemies = new HashSet<Unit>();
		List<Unit> nearunits = getUnit().getUnitsInRadius(getUnit().getType().sightRange());
		Position target;
		if(nearbyTank()){
			if(!nearunits.isEmpty()){
				for(Unit u: nearunits){
					if(u.getPlayer() != this.self && !u.getType().isBuilding()){
						if(u.getType() ==  UnitType.Terran_Siege_Tank_Siege_Mode || u.getType() ==  UnitType.Terran_Siege_Tank_Tank_Mode){
							target = u.getPosition();
							this.unit.useTech(TechType.Spider_Mines, target);
							try {
								wait(150);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							this.spidermines--;
							break;
						}
					}
					
				}
			}
		}
		else{
			if(!nearunits.isEmpty()){
				for(Unit u: nearunits){
					if(u.getPlayer() != this.self && !u.getType().isBuilding()){
						if(u.getType().isOrganic()){
							target = u.getPosition();
							this.unit.useTech(TechType.Spider_Mines, target);
							try {
								wait(150);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							this.spidermines--;
							break;
						}
					}
					
				}
			}
			
			
		}
	}
	
	
	
	
	
	
	
}
	
	

	
	
	
	

