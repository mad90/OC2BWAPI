package unitManager;

import bwapi.TechType;
import bwapi.Unit;

public class VultureManager extends UnitManager{
	
	public int spidermines = 3;
	
	public VultureManager(Unit unit){
		super(unit);
	}
	
	private void useSpiderMine(){
		this.unit.useTech(TechType.Spider_Mines);
	}

}
