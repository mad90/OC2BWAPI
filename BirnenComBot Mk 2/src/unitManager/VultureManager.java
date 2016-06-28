package unitManager;

import bwapi.Player;
import bwapi.Position;
import bwapi.TechType;
import bwapi.Unit;

public class VultureManager extends UnitManager{
	
	public int spidermines = 3;
	
	
	public VultureManager(Unit unit, boolean startLeft, Player self){
		super(unit, startLeft, self);
	}
	
	private void useSpiderMine(){
		getUnit().useTech(TechType.Spider_Mines);
	}
	
	@Override
	public void doStep(boolean offensive){
		System.out.println("doStep() Vulture!");
		
		if(offensive){
			scouting();
		}
		
	}

	
	public void scouting(){
		//Test
		
		if(this.emptyOrFriendly(getUnit().getUnitsInRadius(getUnit().getType().sightRange()))){
			getUnit().move(getScountingPosition());

		}
		
		
	}
	
	public Position getScountingPosition(){
		//TODO: Scounting Target berechnen
		Position target = new Position(0,0);
		
		if(getStartLeft()){
			//TODO: Dynamische Position berechnen
			target = new Position(3440, 1552);
		}
		else{
			//TODO: Dynamische Position berechnen
			target = new Position(620, 1552);
			
		}
		
		return target;
	}
	
	
	
	
}
