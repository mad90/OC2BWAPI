package unitManager;

import bwapi.TechType;
import bwapi.*;

public class MarineManager extends UnitManager{
	
	
	public MarineManager(Unit unit){
		super(unit);
	}
	
	private void useStimpack(){
		this.unit.useTech(TechType.Stim_Packs);
	}

}
