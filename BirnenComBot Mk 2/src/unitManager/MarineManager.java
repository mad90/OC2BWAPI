package unitManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import boiding.BoidingManager;
import boiding.GAParameter;
import bwapi.*;
import utilities.Vector;

public class MarineManager extends UnitManager{
	//Soll sich mit Boiding in einem Verband bewegen
	public BoidingManager boid;
	GAParameter gaparam;

	
	
	
	
	
	
	
	public MarineManager(Unit unit, boolean startLeft, Player self, HashSet<Unit> enemyUnits, Position target, GAParameter gap){
		super(unit, startLeft, self, enemyUnits, target);
		this.boid = new BoidingManager(unit, self, target, gap);
//		this.gaparam = gap;
	}
	
	private void useStimpack(){
		getUnit().useTech(TechType.Stim_Packs);
	}
	
	@Override
	public void doStep(boolean offensive){
		
			
			try {
				PositionOrUnit target = getBestTarget();
				if(target.isUnit()){
					if(this.unit.getGroundWeaponCooldown() == 0 && !this.unit.isAttackFrame() && !this.unit.isStartingAttack() && !this.unit.isAttacking()){
						if(this.unit.canUseTech(TechType.Stim_Packs) && !this.unit.isStimmed() && this.unit.getHitPoints() > 20){
							useStimpack();
						}
//						if(WeaponType.Gauss_Rifle.maxRange() > getDistance(target.getUnit())+20){
						this.unit.attack(target.getUnit());
//						}
//						else if(this.unit.getGroundWeaponCooldown() != 0 || !this.unit.isAttackFrame() || !this.unit.isStartingAttack()){
//							moveBoid(target.getUnit().getPosition());
//						}
					}

//					else if(this.unit.i){
//						moveBoid(target.getUnit().getPosition());
//					}
					
					
				}
				else if(target.isPosition()){
					if(!this.targetchanged || this.reachedlastwp){
						moveBoid(target.getPosition());
					}
					else if(this.targetchanged && !this.reachedlastwp){
						if(this.unit.getDistance(this.waypoints[this.wpindex]) < 50){
							this.wpindex++;
							if(this.wpindex > this.waypoints.length -1){
								this.reachedlastwp = true;
							}
						}
						if(!this.reachedlastwp){
							moveBoid(this.waypoints[this.wpindex]);
						}
						else{
							moveBoid(target.getPosition());
						}
					}
				}
				
//				Unit target = getClosestEnemy();
//				System.out.println("Target: Typ: "+ target.toString());
//				if(target != null){
//				System.out.println("Target: Typ: "+ target.getType().toString()+ " Pos: "+target.getPosition().toString());
//				}
//				
//				if (target == null) {
//					moveBoid(null);
//					return;
//				}
//				else{
//					if(!attackLowestEnemy()){
//						moveBoid(target);
//					}
//				}
//				else if(this.unit.getGroundWeaponCooldown() == 0 && !this.unit.isAttackFrame() && !this.unit.isStartingAttack()
//						&& !this.unit.isAttacking() && target != null) {
//					System.out.println("Marine will angreifen!");
//					if (WeaponType.Gauss_Rifle.maxRange() > getDistance(target) - 20.0) {
//						
//						this.unit.attack(target);
//
//						
//					}
//					else{
//						moveBoid(target);
//					}
//				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			else {
//				if(this.unit.getGroundWeaponCooldown() != 0 &&!this.unit.isAttackFrame() && !this.unit.isStartingAttack() && !this.unit.isAttacking())
//				move(target);
//			}
	}
	
	public void moveBoid(Position target){
		this.unit.move(this.boid.calculateMovePosition(target));
	}
	
	public PositionOrUnit getBestTarget(){
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
		if(!enemies.isEmpty()){
			for(Unit enemy :  enemies){
				if(enemy.getType() == UnitType.Terran_Vulture_Spider_Mine){
					//Wenn möglich zuerst gegnerische Spidermines angreifen, bevor diese explodieren!
					besttarget = new PositionOrUnit(enemy);
					break;
				}
				if(this.targetchanged && enemy.getType() == UnitType.Terran_Supply_Depot){
					//Wenn bereit ein Gebäude zerstört das verbleibende Gebäude fokussieren
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
