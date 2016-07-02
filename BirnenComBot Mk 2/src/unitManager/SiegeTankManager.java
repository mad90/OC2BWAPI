package unitManager;

import java.util.HashSet;

import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;

public class SiegeTankManager extends UnitManager {
	
	public SiegeTankManager(Unit unit, boolean startLeft, Player self, HashSet<Unit> enemyUnits, Position target){
		super(unit, startLeft, self, enemyUnits, target);
	}
	
	private void useSiege(){
		getUnit().siege();
	}
	
	@Override
	public void doStep(boolean offensive){
		
	}
	


}
