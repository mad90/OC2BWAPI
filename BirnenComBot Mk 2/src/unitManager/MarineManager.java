package unitManager;

import java.util.ArrayList;
import java.util.HashSet;

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
				Unit target = getClosestEnemy();
				
				if(target != null){
				System.out.println("Target: Typ: "+ target.getType().toString()+ " Pos: "+target.getPosition().toString());
				}
				
				if (target == null) {
					moveBoid(null);
					return;
				}
				else if(this.unit.getGroundWeaponCooldown() == 0 && !this.unit.isAttackFrame() && !this.unit.isStartingAttack()
						&& !this.unit.isAttacking() && target != null) {
					if (WeaponType.Gauss_Rifle.maxRange() > getDistance(target) - 20.0) {
						
						this.unit.attack(target);

						
					}
					else{
						moveBoid(target);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			else {
//				if(this.unit.getGroundWeaponCooldown() != 0 &&!this.unit.isAttackFrame() && !this.unit.isStartingAttack() && !this.unit.isAttacking())
//				move(target);
//			}
	}
	
	public void moveBoid(Unit target){
		this.unit.move(this.boid.calculateMovePosition(target));
	}
	


	
	
	
	
	
}
