package utilities;

import java.util.ArrayList;

import bwapi.Player;
import bwapi.Unit;


public class UnitSet {
	int id;
	Unit unit;
	private Player self;
	
//	public UnitSet(){	
//		
//	}
	
	public UnitSet(int iid,Unit uunit){
		this.id=iid;
		this.unit=uunit;		
	}
	public ArrayList<UnitSet> initUnit(){
		ArrayList<UnitSet> unitSet=new ArrayList<UnitSet>();
		for (Unit myUnit : self.getUnits()){
			unitSet.add(new UnitSet(myUnit.getID(),myUnit));
		}
		return unitSet;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

}
