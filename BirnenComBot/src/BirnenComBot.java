import java.awt.List;
import java.util.ArrayList;
import java.util.HashSet;

import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;

public class BirnenComBot extends DefaultBWListener {

   

	private Mirror mirror = new Mirror();
	//Position Gebäude rechts 3440, 2720; 3472, 384
	//				   links  624, 384; 624, 2752

    private Game game;

    private Player self;
    private HashSet enemyUnitsLocation = new HashSet();
    private ArrayList<Unit> Enemies = new ArrayList<Unit>();
    private ArrayList<Unit> MyUnits = new ArrayList<>();
    private ArrayList<Unit> MyBuildings = new ArrayList<>();
    private ArrayList<Position> PosEnemyBuildings = new ArrayList<>();
    int buildingsDestroyed = 0;
    private enum States{
    	MOVE,
    	ATTACK,
    	FLEE
    }
    	
    
    

    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    @Override
    public void onUnitCreate(Unit unit) {
        System.out.println("New unit discovered " + unit.getType());
    }
    
    @Override
    public void onUnitDestroy(Unit unit){
    	if(unit.getPlayer() != self && unit.getType() == UnitType.Terran_Supply_Depot){
    		this.buildingsDestroyed++;
    	}
    	
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();

        //Use BWTA to analyze map
        //This may take a few minutes if the map is processed first time!
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
        
        int i = 0;
        for(BaseLocation baseLocation : BWTA.getBaseLocations()){
        	System.out.println("Base location #" + (++i) + ". Printing location's region polygon:");
        	for(Position position : baseLocation.getRegion().getPolygon().getPoints()){
        		System.out.print(position + ", ");
        	}
        	System.out.println();
        }
        
        //get  my Units
        for (Unit myUnit: self.getUnits()){
        	if(!MyUnits.contains(myUnit)){
        		
        		if(myUnit.getType() != UnitType.Terran_Supply_Depot){
        			MyUnits.add(myUnit);
        		}
        		else{
        			MyBuildings.add(myUnit);
        		}
        	}
        }
        
        initializeStart();
        System.out.println("Is Left" + StartPosIsLeft());



    }

    @Override
    public void onFrame() {
        //game.setTextSize(10);
    	strategyDestroyBuildings();
    }

    public static void main(String[] args) {
        new BirnenComBot().run();
    }
    
    public boolean StartPosIsLeft(){
    	boolean left = false;
    	for(Unit u: this.MyBuildings){
    		if(u.getPosition().getX() == 624 && (u.getPosition().getY() == 384 || u.getPosition().getY() == 2752 )){
    			left=true;
    		}
    	}
    	return left;
    	
    }
    
    public void initializeStart(){
    	if(StartPosIsLeft()){
    		this.PosEnemyBuildings.add(new Position(3440, 2720));
    		this.PosEnemyBuildings.add(new Position(3472, 384));
    		
    	}
    	else if(!StartPosIsLeft()){
    		this.PosEnemyBuildings.add(new Position(624, 384));
    		this.PosEnemyBuildings.add(new Position(624, 2752));
    		
    	}
    }
    
    public void strategyDestroyBuildings(){
    	for(Unit myUnit : MyUnits){
    		if(new Vector(myUnit.getPosition(), PosEnemyBuildings.get(buildingsDestroyed)).getLength() < 100){
    			//TODO: Vernüftige Angriffroutine schreiben die nicht blockiert
    			
    		}
    		else{
    		myUnit.move(PosEnemyBuildings.get(buildingsDestroyed));
    		}
    	}
    	
    }
    	
    
}