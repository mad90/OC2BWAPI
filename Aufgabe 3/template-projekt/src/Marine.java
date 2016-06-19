
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
	
	
	private HashSet<Unit> getNearbyMarines(int radius){
		//Gibt alle eignene Marine in einem Radius um die Einheit zurück
		HashSet<Unit> nearbyMarines = new HashSet<>();
		for(Unit u : this.unit.getUnitsInRadius(radius)){
			if(u.getType() == UnitType.Terran_Marine && u.getPlayer() ==  game.self() && u != this.unit){
				nearbyMarines.add(u);
			}
		}
		return nearbyMarines;
		
	}
	
	public Position getMediumPosition(HashSet<Unit> units){
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
		return (new Position(x,y));
		}
		else{
			return this.unit.getPosition();
		}
		
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
//			move(target);
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
		
//		if(!this.unit.isMoving()){
		this.unit.move(calculateMovePosition(target), false);
//		System.out.println("calculateMovePosition() ausgeführt!");
//		}
	}
	
	public Position calculateMovePosition(Unit target){
		//Bisschen rumtesten
		//Vektor zum Gegner target
		//Rule 1)
		
		Vector vr1;
		if(target != null){
			vr1 = new Vector(this.unit.getPosition(), target.getPosition());
		}
		else{
			//Wenn keine Ziele, Laufe zur Mitte der Karte
			 vr1= new Vector(this.unit.getPosition(), new Position(2048,1536));
		}
		//Seperation
		//Rule 2)

		Vector vr2 = new Vector(0,0);
		HashSet<Unit> sepmarines = getNearbyMarines(this.seperation);
		if(!sepmarines.isEmpty()){
			for(Unit u: sepmarines){
				if(this.unit.getDistance(u) < this.seperation){
					//Addiere Vektoren von zu nahen Einheiten zur eigenen Einheit auf
					//TODO: Einheiten verklumpen
					Vector tmpv = new Vector(u.getPosition(),this.unit.getPosition());
					System.out.println("Vector Vr2 vor Addition mit tmpv: " + vr2.toString());
					System.out.println("Vektor tmpv: "+ tmpv.toString());
					vr2 = vr2.add(tmpv);
					System.out.println("Vektor vr2: " + vr2.toString());
					
				}
			}
			System.out.println("Vektor Seperation: " + vr2.toString());
//			vr2 = new Vector(this.unit.getPosition(), vr2.toPosition());
//			System.out.println("Seperation Vektor + Position: " + vr2.toString());
		}
		else{
			System.out.println("Keine Nahen Marines für Seperation!");
		}
		
		//Den aufaddierten Vektor auf die aktuelle Position addieren

		
		//Cohesion
		Vector vrcoh = new Vector(this.unit.getPosition(), getMediumPosition(getNearbyMarines(this.searchradius)));
		System.out.println("Vektor Cohesion: "+vrcoh.toString());
		
		//Rule 3) In das Zentrum der geeignetsten Column bewegen
		Vector vr3 = calculateBestCol();
		System.out.println("Vektor Zentrum Column: " + vr3.toString());
		
		//Rule 4) In das Zentrum der geeignetsten Line bewegen
		Vector vr4 = calculateBestLine();
		
		System.out.println("Vektor Zentrum Line: " + vr4.toString());
		
		
		Vector pc = new Vector(this.unit.getPosition().getX(), this.unit.getPosition().getY());
//		System.out.println("Vector pc: " + pc.toString());
		if(sepmarines.isEmpty()){
			
			Vector pcnew = pc.add((vr1.scalarMultiply(wr1a)),(vr3.scalarMultiply(wr3)),(vr4.scalarMultiply(wr4)));
			System.out.println("pcnew vorher vor Normalisierung: " + pcnew.toString());
			pcnew = pcnew.normalize().scalarMultiply(lengthmuliplier);
			System.out.println("pcnew vorher nach Normalisierung: " + pcnew.toString());
//			pcnew = new Vector(this.unit.getX(), this.unit.getY()).add(pcnew);
			System.out.println("Aktuelle Position: " + this.unit.getPosition().toString());
			Position newPos = new Position((int)(pcnew.getX()+this.unit.getX()),(int) (pcnew.getY()+this.unit.getY()));
			System.out.println("Keine Seperation, Target Position: "+ newPos.toString());
			return newPos;
		}
		else{
			Vector pcnew= pc.add(vr2.scalarMultiply(wr2));
			pcnew = pcnew.normalize().scalarMultiply(lengthmuliplier);
//			System.out.println("pcnew vorher: " + pcnew.toString());
//			pcnew = new Vector(this.unit.getX(), this.unit.getY()).add(pcnew);
			System.out.println("Aktuelle Position: " + this.unit.getPosition().toString());
			Position newPos = new Position((int)(pcnew.getX()+this.unit.getX()),(int) (pcnew.getY()+this.unit.getY()));
			System.out.println("Seperation, Target Position: "+ newPos.toString());
			return newPos;
		}

	}
	
	public Vector calculateBestLine(){
		//Berechnung der Bereiche der Linien (Bereiche horizontal)
		System.out.println("----Start calculateBestLine()----");
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
		System.out.println("Anzahl der HashSets in AllLines: "+ alllines.size());
		
		int ulin = 0;
//		System.out.println("Anzahl der Nahen Marine "+getNearbyMarines(this.searchradius).size());
		//Zähle Einheiten in den einzelnen Bereichen
		for(Unit u : getNearbyMarines(this.searchradius)){
			ulin = (int)((u.getPosition().getX() -leftborder)/((this.rlin*broadth)+(this.rlinsep*seperation)));
//			System.out.println("Einheit befindet sich in Linie: "+ulin);
//			System.out.println("AllLines hat "+alllines.size()+" HashSets!");
			if(ulin == alllines.size()){
				ulin--;
			}
			alllines.get(ulin).add(u);
//			System.out.println("Einheit zur Linie "+ ulin+" hinzugefügt!" );
		}
//		System.out.println("Einheiten in HashSets hinzugefügt (Lines)!");
		
		
		
		//Bereich mit den meisten Einheiten auswählen
		int tmphighest = Integer.MIN_VALUE;
		int chosenlin = Integer.MIN_VALUE;
		int i = 0;
//		System.out.println("Anzahl der Reihen in AllLines" + alllines.size());
		for(HashSet<Unit> lin : alllines){
			
//			System.out.println("tmphighest: "+ tmphighest+" Lin: "+ lin.size());
			if(tmphighest < lin.size()){
				tmphighest = lin.size();
				chosenlin = i;
				
			}
			i++;
		}
		System.out.println("Anzahl Einheiten in Line mit den Meisten: " + alllines.get(chosenlin).size());
		if(chosenlin != Integer.MIN_VALUE){
		//Ergebnis als Vector von Eigener Einheit zum Ziel
		Position bestlinepos = getMediumPosition(alllines.get(chosenlin));
		return new Vector(this.unit.getPosition(), bestlinepos);
		}
		else return new Vector(0,0);
		
	}
		
	
	
	public Vector calculateBestCol(){
		//Sollte laut Tests funktionieren
		System.out.println("----Beginn calculateBestCol()----");
		//Berechnung der Zeilen (Bereiche vertikal)
		//(0/0) Top left
		int topborder = this.unit.getPosition().getY() - this.searchradius;
//		int botborder = this.unit.getPosition().getY() + this.searchradius;
		
		int nbrcol = (int)((this.searchradius*2)/((this.rcol*broadth)+(this.rcolsep*seperation)));
//		System.out.println("Anzahl der Columns: " +nbrcol); //Bis hier korrekt
		//Zum Speichern der Anzahl der Einheiten in Column
		ArrayList<HashSet<Unit>> allcolumns = new ArrayList<HashSet<Unit>>();
		
		for(int i = 0;i < nbrcol; i++){
			allcolumns.add(new HashSet<Unit>());
		}
//		System.out.println("Anzahl der Colums im HashSet: "+allcolumns.size()); //Funktioniert
		int ucol = 0;
		//Zähle Einheiten in den einzelnen Bereichen
		for(Unit u : getNearbyMarines(this.searchradius)){
			ucol = (int)((u.getPosition().getY() - topborder)/((this.rcol*broadth)+(this.rcolsep*seperation)));
//			System.out.println("Ucol ist "+ucol);
			if(ucol == allcolumns.size()){
				ucol--;
			}
			allcolumns.get(ucol).add(u);
		}
		System.out.println("Einheiten in HashSets hinzugefügt!");//Funktioniert
		//Bereich mit den meisten Einheiten auswählen
		int tmphighest = Integer.MIN_VALUE;
		int chosencol = Integer.MIN_VALUE;
		int i = 0;
		for(HashSet<Unit> col : allcolumns){
			if(tmphighest < col.size())
			{
//				System.out.println("Col.size()" + col.size());
				tmphighest = col.size();
				chosencol = i;
			}
			i++;
		}
		System.out.println("Anzahl Einheiten in Spalte mit den Meisten: " + allcolumns.get(chosencol).size());
		
		//
		if(chosencol != Integer.MIN_VALUE){
		Position bestcolpos = getMediumPosition(allcolumns.get(chosencol));
		return new Vector(this.unit.getPosition(), bestcolpos);
		}
		//Nullvektor wenn eine Colums
		else return new Vector(0,0);
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
