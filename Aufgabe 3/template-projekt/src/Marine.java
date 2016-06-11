
import bwapi.*;

import java.util.HashSet;

/**
 * Created by Steffen Wilke on 31.05.2016.
 */
public class Marine {

	private final HashSet<Unit> enemyUnits;
	final private Unit unit;
	double rsig; 	//Abstand zu seinen Nachbarn
	double rsep; 	//Abstand der Seperation
	double rcol; 	//Breite der Reihen-Formation
	double rcolsep; //Seperation innerhalb der Reihen-Formation
	double rlin;	//H�he der Linien-Formation
	double rlinsep; //Seperation innerhalb der Linien-Formation
	double wr1a;	//Gewicht f�r Regel 1 wenn kein Feind gesehen wurde
	double wr1b;	//Gewicht f�r Regel 1 wenn wir Feind sehen
	double wr2;		//Gewicht f�r Regel 2
	double wr3;		//Gewicht f�r Regel 3
	double wr4;		//Gewicht f�r Regel 4
	static int seperation = 20;
	
	
	
	Game game;
	static int lengthmuliplier = 15;

	public Marine(Unit unit, HashSet<Unit> enemyUnits, Game game) {
		this.unit = unit;
		this.enemyUnits = enemyUnits;
		this.game = game;
	}

	
	
	
	
	
	public void step() {
		
		Unit target = getClosestEnemy();
		
		if (target == null) {
			return;
		}
		else if(this.unit.getGroundWeaponCooldown() == 0 && !this.unit.isAttackFrame() && !this.unit.isStartingAttack()
				&& !this.unit.isAttacking() && target != null) {
			if (WeaponType.Gauss_Rifle.maxRange() > getDistance(target) - 20.0) {
				
				this.unit.attack(target);
			}
		}
		else {
			move(target);
		}
	}
	
	
	private HashSet<Unit> getNearbyMarines(){
		//Gibt alle eignene Marine in einem Radius um die Einheit zur�ck
		HashSet<Unit> nearbyMarines = new HashSet<>();
		for(Unit u : this.unit.getUnitsInRadius(this.seperation)){
			if(u.getType() == UnitType.Terran_Marine && u.getPlayer() ==  game.self()){
				nearbyMarines.add(u);
			}
		}
		return nearbyMarines;
		
	}
	
	public static Position getMediumPosition(HashSet<Unit> units){
		//Berechnet den Mittelpunkt zwischen den Postionen der Einheiten
		int x = 0;
		int y = 0;
		
		if(units.size()> 0)
		{
		for(Unit u : units){
			x += u.getPosition().getX();
			y += u.getPosition().getY();
		}
		
		x = (int)(x/units.size());
		y = (int)(y/units.size());
		}
		
		return (new Position(x,y));
	}
	
	public static double getMediumAngle(HashSet<Unit> units){
		//Gibt den Mittelwert der Blickrichtung der Einheiten als Radiant zur�ck, 0.0 ist Osten
		double angle = 0;
		if(units.size()> 0){
			for(Unit u: units){
				angle += u.getAngle();
			}
			angle = (angle/ units.size());
		}
		return angle;
		
	}
	
	
	
	
	
	
	

	private void move(Unit target) {
		if (target == null) {
			return;
		}
		System.out.println("move()");
		// TODO: Implement the flocking behavior in this method.
//		if(this.getNearbyMarines().size() > 0){
//		if(this.unit.getDistance(getMediumPosition(this.getNearbyMarines())) >= this.cohesion){
//			//Wenn Mittelpunkt weiter entfernt als Koh�sion -> bewege dich Richtung Mittelpunkt
//			
//			//Erstelle Vektor von dieser Einheit zum Mittelpunkt
//			Vector v = new Vector(this.unit.getPosition(), getMediumPosition(this.getNearbyMarines()));
//			//Normalisieren und Strecken des Vektors um einen Vektor einer bestimmten L�nge in eine Richtung zu erhalten
//			v =  v.normalize().scalarMultiply(lengthmuliplier);
//			Position targetPos = new Position((int)(this.unit.getPosition().getX()+v.getX()), (int)(this.unit.getPosition().getY()+v.getY()));
//			this.unit.move(targetPos);
//			
//		}
//		else if(this.unit.getDistance(getMediumPosition(this.getNearbyMarines())) <= this.seperation){
//			//Wenn Entfernung zum Mittelpunkt geringer als Seperation -> entferne dich vom Mittelpunkt
//			
//			Vector v = new Vector(getMediumPosition(this.getNearbyMarines()),this.unit.getPosition());
//			//Normalisieren und Strecken des Vektors um einen Vektor einer bestimmten L�nge in eine Richtung zu erhalten
//			v =  v.normalize().scalarMultiply(lengthmuliplier);
//			Position targetPos = new Position((int)(this.unit.getPosition().getX()+v.getX()), (int)(this.unit.getPosition().getY()+v.getY()));
//			this.unit.move(targetPos);
//		}	
//			
//			
//			
//		}
//		this.unit.move(new Position(target.getPosition().getX(), target.getPosition().getY()), false);
		this.unit.move(calculateMovePosition(target), false);
	}
	
	public Position calculateMovePosition(Unit target){
		//Bisschen rumtesten
		//Vektor zum Gegner target
		Vector vr1 = new Vector(this.unit.getPosition(), target.getPosition());
		//Seperation
		Vector vr2 = new Vector(0,0);
		for(Unit u: getNearbyMarines()){
			if(this.unit.getDistance(u) < this.seperation){
				vr2 = vr2.add(new Vector(u.getPosition(), this.unit.getPosition()));
			}
		}
		//Cohesion
		Vector vrcoh = new Vector(this.unit.getPosition(), getMediumPosition(getNearbyMarines()));
		//TODO: Regel 3 richtig berechnen f�r Reihen
		Vector vr3 = vr2.add(vrcoh);
		//TODO: Regel 4 richtig berechnen f�r Linien
		Vector vr4 = vr2.add(vrcoh);
		
		
		Vector pc = new Vector(this.unit.getPosition().getX(), this.unit.getPosition().getY());
		if(getNearbyMarines().isEmpty()){
			Vector pcnew = pc.add((vr1.scalarMultiply(wr1a)),(vr3.scalarMultiply(wr3)),(vr4.scalarMultiply(wr4)));
			pcnew = pcnew.normalize().scalarMultiply(lengthmuliplier);
			return new Position((int)pcnew.getX(),(int) pcnew.getY());
		}
		else{
			Vector pcnew= pc.add(vr2.scalarMultiply(wr2));
			pcnew = pcnew.normalize().scalarMultiply(lengthmuliplier);
			return new Position((int)pcnew.getX(),(int) pcnew.getY());
		}

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
