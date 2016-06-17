
import bwapi.*;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Steffen Wilke on 31.05.2016.
 */
public class Marine {

	private final HashSet<Unit> enemyUnits;
	final private Unit unit;
	//Skalare [0,1]
	double rsig = 1; 	//Abstand zu seinen Nachbarn
	double rsep = 1; 	//Abstand der Seperation
	double rcol = 1; 	//Breite der Reihen-Formation
	double rcolsep = 1; //Seperation innerhalb der Reihen-Formation
	double rlin = 1;	//Höhe der Linien-Formation
	double rlinsep = 1; //Seperation innerhalb der Linien-Formation
	//Gewichte [0,2]
	double wr1a = 1;	//Gewicht für Regel 1 wenn kein Feind gesehen wurde
	double wr1b = 1;	//Gewicht für Regel 1 wenn wir Feind sehen
	double wr2 = 1;		//Gewicht für Regel 2
	double wr3 = 1;		//Gewicht für Regel 3
	double wr4 = 1;		//Gewicht für Regel 4
	//Basiswerte
	static int seperation = 15;
	static int searchradius = 120;
	static int broadth = 40;
	
	
	
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
			move(target);
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
		for(Unit u : this.unit.getUnitsInRadius(this.searchradius)){
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
//		if (target == null) {
//			return;
//		}
//		System.out.println("move()");
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
		calculateBestLine();
		System.out.println("calculateBestLine() -DONE-");
		//this.unit.move(calculateMovePosition(target), false);
	}
	
	public Position calculateMovePosition(Unit target){
		//Bisschen rumtesten
		//Vektor zum Gegner target
		//Rule 1)
		Vector vr1 = new Vector(this.unit.getPosition(), target.getPosition());
		
		//Seperation
		//Rule 2)
		Vector vr2 = new Vector(0,0);
		for(Unit u: getNearbyMarines()){
			if(this.unit.getDistance(u) < this.seperation){
				//Addiere Vektoren von zu nahen Einheiten zur eigenen Einheit auf
				vr2 = vr2.add(new Vector(this.unit.getPosition(), u.getPosition()));
			}
		}
		//Cohesion
		Vector vrcoh = new Vector(this.unit.getPosition(), getMediumPosition(getNearbyMarines()));
		
		//Rule 3) In das Zentrum der geeignetsten Column bewegen
		Position centcol = calculateBestCol();
		Vector vr3 = new Vector(centcol.getX(), centcol.getY());
		
		//Rule 4) In das Zentrum der geeignetsten Line bewegen
		Position centlin = calculateBestLine();
		Vector vr4 = new Vector(centlin.getX(), centlin.getY());
		
		
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
//		int rightborder = this.unit.getPosition().getX() + this.searchradius;
		
		int nbrlin = (int)((this.searchradius*2)/((this.rlin*broadth)+(this.rlinsep*seperation)));
		//Zum Speichern der Anzahl der Einheiten in Line
		ArrayList<HashSet<Unit>> alllines = new ArrayList<HashSet<Unit>>();
//		System.out.println("Anzahl der gefundenen Linien: " + nbrlin);
		
		for(int i = 0;i < nbrlin; i++){
//			System.out.println("Add new Line!");
			alllines.add(new HashSet<Unit>());
		}
		System.out.println("----------------");
		
		int ulin = 0;
		System.out.println("Anzahl der Nahen Marine "+getNearbyMarines().size());
		//Zähle Einheiten in den einzelnen Bereichen
		for(Unit u : getNearbyMarines()){
			ulin = (int)((u.getPosition().getX() -leftborder)/((this.rlin*broadth)+(this.rlinsep*seperation)));
//			System.out.println("Einheit befindet sich in Linie: "+ulin);
			alllines.get(ulin).add(u);
			System.out.println("Einheit zur Linie "+ ulin+" hinzugefügt!" );
		}
		
		//Bereich mit den meisten Einheiten auswählen
		int tmphighest = Integer.MIN_VALUE;
//		System.out.println("Anzahl der Reihen in AllLines" + alllines.size());
		for(HashSet<Unit> lin : alllines){
//			System.out.println("tmphighest: "+ tmphighest+" Lin: "+ lin.size());
			if(tmphighest < lin.size()){
				tmphighest = lin.size();
			}
		}
		System.out.println("Anzahl Einheiten in Line mit den Meisten: " + alllines.get(tmphighest).size());
		if(tmphighest != Integer.MIN_VALUE){
		return getMediumPosition(alllines.get(tmphighest));
		}
		else return this.unit.getPosition();
		
	}
		
	
	
	public Position calculateBestCol(){
		//Berechnung der Zeilen (Bereiche vertikal)
		int topborder = this.unit.getPosition().getY() - this.searchradius;
//		int botborder = this.unit.getPosition().getY() + this.searchradius;
		
		int nbrcol = (int)((this.searchradius*2)/((this.rcol*broadth)+(this.rcolsep*seperation)));
		//Zum Speichern der Anzahl der Einheiten in Column
		ArrayList<HashSet<Unit>> allcolumns = new ArrayList<HashSet<Unit>>();
		
		for(int i = 0;i < nbrcol; i++){
			allcolumns.add(new HashSet<Unit>());
		}
		
		int ucol = 0;
		//Zähle Einheiten in den einzelnen Bereichen
		for(Unit u : getNearbyMarines()){
			ucol = (int)((u.getPosition().getY() - topborder)/((this.rcol*broadth)+(this.rcolsep*seperation)));
			allcolumns.get(ucol).add(u);
		}
		
		//Bereich mit den meisten Einheiten auswählen
		int tmphighest = Integer.MIN_VALUE;
		for(HashSet<Unit> col : allcolumns){
			if(tmphighest < col.size()){
				tmphighest = col.size();
			}
		}
		System.out.println("Anzahl Einheiten in Spalte mit den Meisten: " + allcolumns.get(tmphighest).size());
		
		//
		if(tmphighest != Integer.MIN_VALUE){
		return getMediumPosition(allcolumns.get(tmphighest));
		}
		else return this.unit.getPosition();
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
