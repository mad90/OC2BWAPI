package unitManager;

import bwapi.Player;
import bwapi.Unit;

public class SiegeTankManager extends UnitManager {
	
	public SiegeTankManager(Unit unit, boolean startLeft, Player self){
		super(unit, startLeft, self);
	}
	
	private void useSiege(){
		getUnit().siege();
	}
	
	@Override
	public void doStep(boolean offensive){
		
	}
	


}
