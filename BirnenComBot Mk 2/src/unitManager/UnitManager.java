package unitManager;

import bwapi.Unit;

public class UnitManager {
	//TODO: UnitManager �berklasse implementieren
	
	public static Unit unit;
	
	public UnitManager(Unit unit){
		this.unit = unit;
	}
	
	
	
	public Unit getUnit(){
		return this.unit;
	}
	
	
	

}
