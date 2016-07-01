package unitManager;

import bwapi.*;

public class MarineManager extends UnitManager{
	
	
	public MarineManager(Unit unit, boolean startLeft, Player self){
		super(unit, startLeft, self);
	}
	
	private void useStimpack(){
		getUnit().useTech(TechType.Stim_Packs);
	}
	
	@Override
	public void doStep(boolean offensive){
//		System.out.println("Marine!"+ this.unit.getType().toString());
	}

}
