package unitManager;

import bwapi.Unit;

public class SiegeTankManager extends UnitManager {
	
	public SiegeTankManager(Unit unit){
		super(unit);
		
	}
	
	private void useSiege(){
		this.unit.siege();
	}

}
