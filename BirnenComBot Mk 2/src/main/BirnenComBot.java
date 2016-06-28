package main;

import java.util.ArrayList;
import java.util.HashSet;

import bwapi.*;
import bwta.*;
import unitManager.*;

public class BirnenComBot  extends DefaultBWListener implements Runnable{
	
	private Mirror mirror = new Mirror();
	
    private Game game;
    private Player self;
	
	public HashSet<UnitManager> myunits = new HashSet<UnitManager>();
	public HashSet<Unit> mybuildings = new HashSet<Unit>();
	public HashSet<Unit> enemyunits = new HashSet<Unit>();

    private ArrayList<Position> posenemybuild = new ArrayList<>();
    
    boolean startleft;
    
    @Override
    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
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
        
    }
    
	@Override
	public void onFrame() {

	}
	
	@Override
	public void onUnitDestroy(Unit unit) {
		if (unit == null) {
			return;
		}

		UnitManager rm = null;
		for (UnitManager um : myunits) {
			if (um.getUnit().getID() == unit.getID()) {
				rm = um;
				break;
			}
		}
		myunits.remove(rm);

		Unit rmUnit = null;
		for (Unit u : enemyunits) {
			if (u.getID() == unit.getID()) {
				rmUnit = u;
				break;
			}
		}
		enemyunits.remove(rmUnit);
	}
	
	@Override
	public void onUnitCreate(Unit unit) {
		if(!unit.getPlayer().equals(this.self) && !unit.getType().equals(UnitType.Terran_Supply_Depot) && !this.enemyunits.contains(unit)){
			this.enemyunits.add(unit);
			
		}
	}
	
	
	
	
	@Override
	public void onEnd(boolean winner) {
		this.myunits.clear();
		this.enemyunits.clear();
		this.posenemybuild.clear();
		this.mybuildings.clear();
	}

	@Override
	public void onSendText(String text) {
	}

	@Override
	public void onReceiveText(Player player, String text) {
	}

	@Override
	public void onPlayerLeft(Player player) {
	}

	@Override
	public void onNukeDetect(Position position) {
	}

	@Override
	public void onUnitEvade(Unit unit) {
	}
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void initializeStart(){
    	//Initialisiert eigene Startposition, sowie die UnitManager
    	StartPosIsLeft();
    	
        for (Unit u: self.getUnits()){
        	if(!myunits.contains(u)){
        		
        		if(u.getType() != UnitType.Terran_Supply_Depot){
        			if(u.getType() == UnitType.Terran_Marine){
        				myunits.add(new MarineManager(u));
        			}
        			else if(u.getType() == UnitType.Terran_Medic){
        				myunits.add(new MedicManager(u));
        			}
        			else if(u.getType() == UnitType.Terran_Siege_Tank_Tank_Mode){
        				myunits.add(new SiegeTankManager(u));
        			}
        			else if(u.getType() == UnitType.Terran_Vulture){
        				myunits.add(new VultureManager(u));
        			}
        		}
        		else{
        			mybuildings.add(u);
        		}
        	}
        }
    	
    	
        
        
    	if(getStartLeft()){
    		this.posenemybuild.add(new Position(3440, 2720));
    		this.posenemybuild.add(new Position(3472, 384));
    		
    	}
    	else if(!getStartLeft()){
    		this.posenemybuild.add(new Position(624, 384));
    		this.posenemybuild.add(new Position(624, 2752));
    		
    	}
    }
    
    public void StartPosIsLeft(){
    	//Prüft ob man auf der Karte links startet und setzt startLeft entsprechend
    	boolean left = false;
    	for(Unit u: this.mybuildings){
    		if(u.getPosition().getX() == 624 && (u.getPosition().getY() == 384 || u.getPosition().getY() == 2752 )){
    			left=true;
    		}
    	}
    	this.startleft = left;
    }
    
    
    
    
    
    public boolean getStartLeft(){
    	return this.startleft;
    }
    
    
	


}
