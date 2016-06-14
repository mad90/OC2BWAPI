
import bwapi.*;

import java.util.ArrayList;
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
	double rlin;	//Höhe der Linien-Formation
	double rlinsep; //Seperation innerhalb der Linien-Formation
	double wr1a;	//Gewicht für Regel 1 wenn kein Feind gesehen wurde
	double wr1b;	//Gewicht für Regel 1 wenn wir Feind sehen
	double wr2;		//Gewicht für Regel 2
	double wr3;		//Gewicht für Regel 3
	double wr4;		//Gewicht für Regel 4
	static int seperation = 20;
	static int searchradius = 50;
	
	
	
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
		//Gibt alle eignene Marine in einem Radius um die Einheit zurück
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
		//Gibt den Mittelwert der Blickrichtung der Einheiten als Radiant zurück, 0.0 ist Osten
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
//			//Wenn Mittelpunkt weiter entfernt als Kohäsion -> bewege dich Richtung Mittelpunkt
//			
//			//Erstelle Vektor von dieser Einheit zum Mittelpunkt
//			Vector v = new Vector(this.unit.getPosition(), getMediumPosition(this.getNearbyMarines()));
//			//Normalisieren und Strecken des Vektors um einen Vektor einer bestimmten Länge in eine Richtung zu erhalten
//			v =  v.normalize().scalarMultiply(lengthmuliplier);
//			Position targetPos = new Position((int)(this.unit.getPosition().getX()+v.getX()), (int)(this.unit.getPosition().getY()+v.getY()));
//			this.unit.move(targetPos);
//			
//		}
//		else if(this.unit.getDistance(getMediumPosition(this.getNearbyMarines())) <= this.seperation){
//			//Wenn Entfernung zum Mittelpunkt geringer als Seperation -> entferne dich vom Mittelpunkt
//			
//			Vector v = new Vector(getMediumPosition(this.getNearbyMarines()),this.unit.getPosition());
//			//Normalisieren und Strecken des Vektors um einen Vektor einer bestimmten Länge in eine Richtung zu erhalten
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
		//TODO: Regel 3 richtig berechnen für Reihen
		Vector vr3 = vr2.add(vrcoh);
		//TODO: Regel 4 richtig berechnen für Linien
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
	
	public Position calculateBestLine(){
		//Berechnung der Bereiche der Linien (Bereiche horizontal)
		int leftborder = this.unit.getPosition().getX() - this.searchradius;
		int rightborder = this.unit.getPosition().getX() + this.searchradius;
		
		int nbrlin = (int)((this.searchradius*2)/(this.rlin+this.rlinsep));
		//Zum Speichern der Anzahl der Einheiten in Line
		ArrayList<HashSet<Unit>> alllines = new ArrayList<HashSet<Unit>>();
		
		while(alllines.size() < nbrlin){
			alllines.add(new HashSet<Unit>());
		}
		
		int ulin = 0;
		//Zähle Einheiten in den einzelnen Bereichen
		for(Unit u : getNearbyMarines()){
			ulin = (int)((leftborder - u.getPosition().getX())/this.rlin + this.rlinsep);
			alllines.get(ulin).add(u);
		}
		
		//Bereich mit den meisten Einheiten auswählen
		int tmphighest = -1;
		for(HashSet<Unit> lin : alllines){
			if(tmphighest > lin.size()){
				tmphighest = lin.size();
			}
		}
		return getMediumPosition(alllines.get(tmphighest));
		
	}
		
	
	
	public Position calculateBestCol(){
		//Berechnung der Zeilen (Bereiche vertikal)
		int topborder = this.unit.getPosition().getY() - this.searchradius;
		int botborder = this.unit.getPosition().getY() + this.searchradius;
		
		int nbrcol = (int)((this.searchradius*2)/(this.rcol+this.rcolsep));
		//Zum Speichern der Anzahl der Einheiten in Column
		ArrayList<HashSet<Unit>> allcolumns = new ArrayList<HashSet<Unit>>();
		
		while(allcolumns.size() < nbrcol){
			allcolumns.add(new HashSet<Unit>());
		}
		
		int ucol = 0;
		//Zähle Einheiten in den einzelnen Bereichen
		for(Unit u : getNearbyMarines()){
			ucol = (int)((topborder - u.getPosition().getY())/this.rcol + this.rcolsep);
			allcolumns.get(ucol).add(u);
		}
		
		//Bereich mit den meisten Einheiten auswählen
		int tmphighest = -1;
		for(HashSet<Unit> col : allcolumns){
			if(tmphighest > col.size()){
				tmphighest = col.size();
			}
		}
		return getMediumPosition(allcolumns.get(tmphighest));
		
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
