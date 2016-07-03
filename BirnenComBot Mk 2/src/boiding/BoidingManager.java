package boiding;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import utilities.Vector;

public class BoidingManager {
	public Unit unit;
	
	static int seperation = 50;
	static int searchradius = 300;
	static int broadth = 80;
	
//	HashSet<Unit> enemyUnits;
	GAParameter gaparameter;
	static int lengthmuliplier = 15;
	public Player self;
	Position targetPos;
	
	
	public BoidingManager(Unit u, Player self, Position targetPos, GAParameter gap){
//		this.enemyUnits = eu;
		this.unit = u;
		this.self = self;
		this.targetPos =  targetPos;
		this.gaparameter = gap;
	}
	
	public BoidingManager(Unit u, Player self, Position targetPos) throws FileNotFoundException{
		this.unit = u;
		this.self =  self;
		this.targetPos = targetPos;
		
		if(Population.getNumberOfEntriesInFlatFile() > 0){
			ArrayList<GAParameter> tmppop = Population.readFromFlatFile();
			this.gaparameter = tmppop.get(0);
		}
		else{
			this.gaparameter = new GAParameter();
		}
	}
	
	
	private HashSet<Unit> getNearbyMarines(int radius){
		//Gibt alle eignene Marine/Medics in einem Radius um die Einheit zurück
		HashSet<Unit> nearbyMarines = new HashSet<>();
		for(Unit u : this.unit.getUnitsInRadius(radius)){
			if(u.getType().isOrganic() && u.getPlayer() ==  this.self && u != this.unit){
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
	
	//Boiding
	public Position calculateMovePosition(Position target){
		//Debug Ausgaben
//		System.out.println("Suchradius: "+(this.gaparameter.getR_sig()*this.searchradius));
//		System.out.println("Line Breite: "+ (this.gaparameter.getR_lin()*broadth));
//		System.out.println("Line Seperation: "+ (this.gaparameter.getR_lin_sep()*seperation));
//		System.out.println("Column Breite: "+ (this.gaparameter.getR_col()*broadth));
//		System.out.println("Column Seperation: "+ (this.gaparameter.getR_col_sep()*seperation));
		
		//Bisschen rumtesten
		//Vektor zum Gegner target
		//Rule 1)
		
		Vector vr1;
		if(target != null){
//			System.out.println("Target Position: "+ target.getPosition().toString());
			vr1 = new Vector(this.unit.getPosition(), target);
		}
		else{
			//Wenn keine Ziele, Laufe zur Mitte der Karte
			 vr1= new Vector(this.unit.getPosition(), this.targetPos);
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
//					System.out.println("Vector Vr2 vor Addition mit tmpv: " + vr2.toString());
//					System.out.println("Vektor tmpv: "+ tmpv.toString());
					vr2 = vr2.add(tmpv);
//					System.out.println("Vektor vr2: " + vr2.toString());
					
				}
			}
//			System.out.println("Vektor Seperation: " + vr2.toString());
//			vr2 = new Vector(this.unit.getPosition(), vr2.toPosition());
//			System.out.println("Seperation Vektor + Position: " + vr2.toString());
		}
		else{
//			System.out.println("Keine Nahen Marines für Seperation!");
		}
		
		//Den aufaddierten Vektor auf die aktuelle Position addieren

		
		//Cohesion
		Vector vrcoh = new Vector(this.unit.getPosition(), getMediumPosition(getNearbyMarines((int)(this.gaparameter.getR_sig()*this.searchradius))));
//		System.out.println("Vektor Cohesion: "+vrcoh.toString());
		
		//Rule 3) In das Zentrum der geeignetsten Column bewegen
		Vector vr3 = calculateBestCol();
//		System.out.println("Vektor Zentrum Column: " + vr3.toString());
		
		//Rule 4) In das Zentrum der geeignetsten Line bewegen
		Vector vr4 = calculateBestLine();
		
//		System.out.println("Vektor Zentrum Line: " + vr4.toString());
		
		
		Vector pc = new Vector(this.unit.getPosition().getX(), this.unit.getPosition().getY());
//		System.out.println("Vector pc: " + pc.toString());
		if(sepmarines.isEmpty()){
			if(target == null){
				Vector pcnew = pc.add((vr1.scalarMultiply(this.gaparameter.getW_r1_a())),(vr3.scalarMultiply(this.gaparameter.getW_r3())),(vr4.scalarMultiply(this.gaparameter.getW_r4())));
			}
			else{
				Vector pcnew = pc.add((vr1.scalarMultiply(this.gaparameter.getW_r1_b())),(vr3.scalarMultiply(this.gaparameter.getW_r3())),(vr4.scalarMultiply(this.gaparameter.getW_r4())));
			}
			Vector pcnew = pc.add((vr1.scalarMultiply(this.gaparameter.getW_r1_a())),(vr3.scalarMultiply(this.gaparameter.getW_r3())),(vr4.scalarMultiply(this.gaparameter.getW_r4())));
//			System.out.println("pcnew vorher vor Normalisierung: " + pcnew.toString());
			pcnew = pcnew.normalize().scalarMultiply(lengthmuliplier);
//			System.out.println("pcnew vorher nach Normalisierung: " + pcnew.toString());
//			pcnew = new Vector(this.unit.getX(), this.unit.getY()).add(pcnew);
//			System.out.println("Aktuelle Position: " + this.unit.getPosition().toString());
			Position newPos = new Position((int)(pcnew.getX()+this.unit.getX()),(int) (pcnew.getY()+this.unit.getY()));
//			System.out.println("Keine Seperation, Target Position: "+ newPos.toString());
			return newPos;
		}
		else{
			Vector pcnew= pc.add(vr2.scalarMultiply(this.gaparameter.getW_r2()));
			pcnew = pcnew.normalize().scalarMultiply(lengthmuliplier);
//			System.out.println("pcnew vorher: " + pcnew.toString());
//			pcnew = new Vector(this.unit.getX(), this.unit.getY()).add(pcnew);
//			System.out.println("Aktuelle Position: " + this.unit.getPosition().toString());
			Position newPos = new Position((int)(pcnew.getX()+this.unit.getX()),(int) (pcnew.getY()+this.unit.getY()));
//			System.out.println("Seperation, Target Position: "+ newPos.toString());
			return newPos;
		}

	}
	
	public Vector calculateBestLine(){
		//Berechnung der Bereiche der Linien (Bereiche horizontal)
//		System.out.println("----Start calculateBestLine()----");
		int leftborder = this.unit.getPosition().getX() - ((int)(this.gaparameter.getR_sig()*this.searchradius));
//		int rightborder = this.unit.getPosition().getX() + this.searchradius;
		
		int nbrlin = (int)((((this.gaparameter.getR_sig()*this.searchradius))*2)/((this.gaparameter.getR_lin()*broadth)+(this.gaparameter.getR_lin_sep()*seperation)));
		//Zum Speichern der Anzahl der Einheiten in Line
		ArrayList<HashSet<Unit>> alllines = new ArrayList<HashSet<Unit>>();
//		System.out.println("Anzahl der gefundenen Linien: " + nbrlin);
		
		for(int i = 0; i <= nbrlin; i++){
//			System.out.println("Add new Line!");
			alllines.add(new HashSet<Unit>());
		}
//		System.out.println("Anzahl der HashSets in AllLines: "+ alllines.size());
		
		int ulin = 0;
//		System.out.println("Anzahl der Nahen Marine "+getNearbyMarines(this.searchradius).size());
		//Zähle Einheiten in den einzelnen Bereichen
		for(Unit u : getNearbyMarines(((int)(this.gaparameter.getR_sig()*this.searchradius)))){
			double tmp =((u.getPosition().getX() -leftborder)/((this.gaparameter.getR_lin()*broadth)+(this.gaparameter.getR_lin_sep()*seperation)));
			ulin = (int) tmp;
//			System.out.println("Ulin ohne runden: "+ ((u.getPosition().getX() -leftborder)/((this.gaparameter.getR_lin()*broadth)+(this.gaparameter.getR_lin_sep()*seperation))));
//			System.out.println("Einheit befindet sich in Linie: "+ulin);
//			System.out.println("AllLines hat "+alllines.size()+" HashSets!");
//			System.out.println("Ulin ist "+ulin);
//			System.out.println("Länge AllLines "+alllines.size());
//			System.out.println("Anzahl der Lines "+ nbrlin);
//			System.out.println("\n"+"------------");
//			if(ulin > alllines.size()){
//				ulin--;
//			}
//			if(ulin >= alllines.size()&&alllines.size()>0){
//				//Dirty Workaround TODO: besser lösen
//				ulin = alllines.size()-1;
//			}
			if(ulin >= alllines.size() && alllines.size() != 0){
				ulin = alllines.size() - 1;
			}
			else if(ulin >= alllines.size() && alllines.size() == 0){
				ulin = 0;
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
//		System.out.println("Anzahl Einheiten in Line mit den Meisten: " + alllines.get(chosenlin).size());
		if(chosenlin != Integer.MIN_VALUE){
		//Ergebnis als Vector von Eigener Einheit zum Ziel
		Position bestlinepos = getMediumPosition(alllines.get(chosenlin));
		return new Vector(this.unit.getPosition(), bestlinepos);
		}
		else return new Vector(0,0);
		
	}
		
	
	
	public Vector calculateBestCol(){
		//Sollte laut Tests funktionieren
//		System.out.println("----Beginn calculateBestCol()----");
		//Berechnung der Zeilen (Bereiche vertikal)
		//(0/0) Top left
		int topborder = this.unit.getPosition().getY() - (int)(this.gaparameter.getR_sig()*this.searchradius);
//		int botborder = this.unit.getPosition().getY() + this.searchradius;
		
		int nbrcol = (int)((((this.gaparameter.getR_sig()*this.searchradius))*2)/((this.gaparameter.getR_col()*broadth)+(this.gaparameter.getR_col_sep()*seperation)));
//		System.out.println("Anzahl der Columns: " +nbrcol); //Bis hier korrekt
		//Zum Speichern der Anzahl der Einheiten in Column
		ArrayList<HashSet<Unit>> allcolumns = new ArrayList<HashSet<Unit>>();
		
		for(int i = 0;i <= nbrcol; i++){
			allcolumns.add(new HashSet<Unit>());
		}
//		System.out.println("Anzahl der Colums im HashSet: "+allcolumns.size()); //Funktioniert
		int ucol = 0;
		//Zähle Einheiten in den einzelnen Bereichen
		for(Unit u : getNearbyMarines(((int)(this.gaparameter.getR_sig()*this.searchradius)))){
			
			
			
			double tmp = ((u.getPosition().getY() - topborder)/((this.gaparameter.getR_col()*broadth)+(this.gaparameter.getR_col_sep()*seperation)));
//			System.out.println("Unit in Col" + tmp + " von " + nbrcol + " Zeilen" );
			ucol = (int) tmp;
//			System.out.println("Ucol nach int cast" + ucol);
//			System.out.println("Ucol ohne runden "+(u.getPosition().getY() - topborder)/((this.gaparameter.getR_col()*broadth)+(this.gaparameter.getR_col_sep()*seperation)));
//			System.out.println("Ucol ist "+ucol);
//			System.out.println("Länge Allcolums "+allcolumns.size());
//			System.out.println("Anzahl der Columns "+ nbrcol);
//			System.out.println("\n"+"------------");
//			if(ucol > allcolumns.size()){
//				ucol--;
//			}
//			if(ucol >= allcolumns.size() && allcolumns.size()>0){
//				//Dirty Workaround TODO: besser lösen
//				ucol = allcolumns.size()-1;
//			}
			if(ucol >= allcolumns.size() && allcolumns.size() != 0){
				ucol = allcolumns.size() - 1;
			}
			else if(ucol >= allcolumns.size() && allcolumns.size() == 0){
				ucol = 0;
			}
			
			allcolumns.get(ucol).add(u);
		}
//		System.out.println("Einheiten in HashSets hinzugefügt!");//Funktioniert

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
//		System.out.println("Anzahl Einheiten in Spalte mit den Meisten: " + allcolumns.get(chosencol).size());
		
		//
		if(chosencol != Integer.MIN_VALUE){
		Position bestcolpos = getMediumPosition(allcolumns.get(chosencol));
		return new Vector(this.unit.getPosition(), bestcolpos);
		}
		//Nullvektor wenn eine Colums
		else return new Vector(0,0);
	}

}
