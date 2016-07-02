package unitManager;

import java.util.HashSet;

import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;

public class MedicManager extends UnitManager{
	
	
	public MedicManager(Unit unit, boolean startLeft, Player self, HashSet<Unit> enemyUnits, Position target){
		super(unit, startLeft, self, enemyUnits, target);
	}
	
	
	@Override
	public void doStep(boolean offensive){
		
	}

	
	
	
}
