package unitManager;

import java.util.HashSet;
import java.util.List;

import boiding.BoidingManager;
import boiding.GAParameter;
import bwapi.Player;
import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class SiegeTankManager extends UnitManager {
	
	public BoidingManager boid;
	
	public SiegeTankManager(Unit unit, boolean startLeft, Player self, HashSet<Unit> enemyUnits, Position target, GAParameter gaparam){
		super(unit, startLeft, self, enemyUnits, target);
		this.boid = new BoidingManager(unit, self, target, gaparam);
	}
	
	private void useSiege(){
		if(getUnit().canUseTech(TechType.Tank_Siege_Mode)){
			getUnit().useTech(TechType.Tank_Siege_Mode);
		}
		
	}
	
	@Override
	public void doStep(boolean offensive){
		PositionOrUnit target = getBestTankTarget();
		try {
			if(target.isUnit()){
				if(this.unit.getGroundWeaponCooldown() == 0 && !this.unit.isAttackFrame() && !this.unit.isStartingAttack() && !this.unit.isAttacking()){
					if(!this.unit.isSieged() && this.unit.canSiege()){
						useSiege();
					}
					else if(this.unit.canAttack(target.getUnit()) && this.unit.isSieged()){
						this.unit.rightClick(target.getUnit());
					}
				}
			}
			else if(target.isPosition()){
				if(this.unit.getType().equals(UnitType.Terran_Siege_Tank_Siege_Mode)){
					this.unit.unsiege();
				}
				
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public PositionOrUnit getBestTankTarget(){
		//Wenn Gegner in Weaponrange gibt Gegner mit wenigster HP, sonst gibt TargetPos
		PositionOrUnit besttarget = new PositionOrUnit(this.targetPos);
//		List<Unit> nearunits = getUnit().getUnitsInRadius(getUnit().getType().groundWeapon().maxRange());
		List<Unit> nearunits = getUnit().getUnitsInRadius(UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().maxRange());
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
		if(!enemies.isEmpty()){
			for(Unit enemy :  enemies){
				if(this.targetchanged && enemy.getType() == UnitType.Terran_Supply_Depot){
					//Wenn bereit ein Gebäude zerstört das verbleibende Gebäude fokussieren
					besttarget = new PositionOrUnit(enemy);
					return besttarget;
				}
				if(enemy.getType() == UnitType.Terran_Siege_Tank_Siege_Mode || enemy.getType() == UnitType.Terran_Siege_Tank_Tank_Mode){
					//Wenn Möglich gegnerische SiegeTanks fokusieren!
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
	


}
