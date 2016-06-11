
import bwapi.*;

import java.util.HashSet;

/**
 * Created by Steffen Wilke on 31.05.2016.
 */
public class Marine {

	private final HashSet<Unit> enemyUnits;
	final private Unit unit;

	public Marine(Unit unit, HashSet<Unit> enemyUnits) {
		this.unit = unit;
		this.enemyUnits = enemyUnits;
	}

	public void step() {
		Unit target = getClosestEnemy();
		if (target == null) {
			return;
		}
		if (this.unit.getGroundWeaponCooldown() == 0 && !this.unit.isAttackFrame() && !this.unit.isStartingAttack()
				&& !this.unit.isAttacking() && target != null) {
			if (WeaponType.Gauss_Rifle.maxRange() > getDistance(target) - 20.0) {
				this.unit.attack(target);
			}
		} else {
			move(target);
		}
	}

	private void move(Unit target) {
		if (target == null) {
			return;
		}
		// TODO: Implement the flocking behavior in this method.
		this.unit.move(new Position(target.getPosition().getX(), target.getPosition().getY()), false);
	}

	private Unit getClosestEnemy() {
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

	private double getDistance(Unit enemy) {
		return this.unit.getPosition().getDistance(enemy.getPosition());
	}

	public int getID() {
		return this.unit.getID();
	}
}
