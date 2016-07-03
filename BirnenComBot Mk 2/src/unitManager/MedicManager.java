package unitManager;

import java.util.HashSet;
import java.util.List;

import boiding.BoidingManager;
import boiding.GAParameter;
import bwapi.Player;
import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.Unit;

public class MedicManager extends UnitManager{
	public BoidingManager boid;
	GAParameter gaparam;
	
	
	public MedicManager(Unit unit, boolean startLeft, Player self, HashSet<Unit> enemyUnits, Position target, GAParameter gap){
		super(unit, startLeft, self, enemyUnits, target);
		this.boid = new BoidingManager(unit, self, target, gap);
	}
	
	
	@Override
	public void doStep(boolean offensive){
		
		try {
			PositionOrUnit target = getBestHealingTarget();
			if(target.isUnit()){
				if(this.unit.getGroundWeaponCooldown() == 0 && !this.unit.isAttackFrame() && !this.unit.isStartingAttack() && !this.unit.isAttacking()){
					this.unit.attack(target.getUnit());
				}
				else{
					moveBoid(target.getUnit().getPosition());
				}
			}
			else if(target.isPosition()){
				moveBoid(target.getPosition());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	
	
	public PositionOrUnit getBestHealingTarget(){
		//Gibt bei verletzten organischen Einheiten diese zurück, ansonsten targetPos
		List<Unit> nearbyunits = this.unit.getUnitsInRadius(this.unit.getType().sightRange());
		HashSet<Unit> nearbyallies = new HashSet<Unit>();
		PositionOrUnit bestHealingTarget = new PositionOrUnit(this.targetPos);
		int lowestHP = Integer.MAX_VALUE;
		if(!nearbyunits.isEmpty()){
			for(Unit u: nearbyunits){
				if(u.getType().isOrganic() && u.getPlayer() == this.self){
					nearbyallies.add(u);
				}
			}
		}
		
		if(!nearbyallies.isEmpty()){
			for(Unit ally: nearbyallies){
				if(ally.getHitPoints() < lowestHP && ally.getHitPoints() != ally.getInitialHitPoints()){
					lowestHP = ally.getHitPoints();
					bestHealingTarget = new PositionOrUnit(ally);
				}
			}
		}
		else{
			bestHealingTarget = new PositionOrUnit(this.targetPos);
		}
		
		return bestHealingTarget;

	}
	
	public void moveBoid(Position target){
		this.unit.move(this.boid.calculateMovePosition(target));
	}
	
	
}
