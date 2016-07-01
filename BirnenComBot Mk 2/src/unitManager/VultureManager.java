package unitManager;

import java.util.List;

import bwapi.Player;
import bwapi.Position;
import bwapi.TechType;
import bwapi.Unit;
import xcs.VultureXCS;

public class VultureManager extends UnitManager{
	
	public int spidermines = 3;
	public VultureXCS vultureXCS;
	
	
	
	public VultureManager(Unit unit, boolean startLeft, Player self){
		super(unit, startLeft, self);
		this.vultureXCS = new VultureXCS(5, 20);
		
	}
	
	private void useSpiderMine(){
		getUnit().useTech(TechType.Spider_Mines);
	}
	
	@Override
	public void doStep(boolean offensive){
		checkEnemyInSight();
//		System.out.println("doStep() Vulture!");
		
		if(offensive){
//			System.out.println("Scouting!");
			System.out.println("Scouting: "+ this.unit.getType().toString()+ " ManagerType: "+ this.getClass().getName());
			scouting();
		}
		
	}

	
	public void scouting(){
		//Test
		
		if(!this.getEnemyInSightRange()){
			getUnit().move(getScountingPosition());

		}
		else{
			runAway(this.unit.getUnitsInRadius(this.unit.getType().sightRange()));
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
