package main;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.colorchooser.ColorSelectionModel;

import bwapi.Color;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.Region;
import unitManager.MarineManager;
import unitManager.MedicManager;
import unitManager.SiegeTankManager;
import unitManager.UnitManager;
import unitManager.VultureManager;

public class BirnenComBot  extends DefaultBWListener implements Runnable{
	
	private Mirror mirror;
	
    private Game game;
    private Player self;
	
	public HashSet<UnitManager> myunits;
	public HashSet<Unit> mybuildings;
	public HashSet<Unit> enemyunits;

    private ArrayList<Position> posenemybuild;
    
    boolean startleft;
    
    //Offensiver/Defensiver Modus
    boolean offensive = true;
    
	public BirnenComBot(){
		System.out.println("BirnenComBot ready for duty!");
		this.mirror  = new Mirror();
		this.myunits = new HashSet<UnitManager>();
		this.mybuildings = new HashSet<Unit>();
		this.enemyunits = new HashSet<Unit>();

	    this.posenemybuild = new ArrayList<>();

	
	}
    
    
    
	public static void main(String[] args){
		new BirnenComBot().run();
	}
	
	
    
    
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
        initializeStart();
        System.out.println("Initialisiere Start");
        
        
        
        
    }
    
	@Override
	public void onFrame() {
		drawRegions();
		
		step();

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
		if(!unit.getPlayer().equals(this.self) && !unit.getType().equals(UnitType.Terran_Supply_Depot)/* && !this.enemyunits.contains(unit)*/){
			this.enemyunits.add(unit);
			
		}
		else if(unit.getPlayer().equals(this.self) && unit.getType().equals(UnitType.Terran_Supply_Depot)){
			this.mybuildings.add(unit);
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
    
    
    public void step(){
    	//Ruft die doStep() Funktion für jeden Manager in myUnits auf
    	
    	for(UnitManager um: myunits){
    		System.out.println("Step()");
    		um.doStep(getOffensive());
    	}
    	
    }
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void initializeStart(){
    	//Initialisiert eigene UnitManager
    	
    	for(Unit u: self.getUnits()){
    		if(u.getType() == UnitType.Terran_Supply_Depot){
    			mybuildings.add(u);
    		}
    		
    	}
    	System.out.println("Size mybuildings"+ mybuildings.size());
    	startPosIsLeft();
    	System.out.println("StartPos ist links: " + getStartLeft());
    	

    	
        for (Unit u: self.getUnits()){
        	if(u.getType() != UnitType.Buildings &&!myunits.contains(u)){
        		    if(u.getType() == UnitType.Terran_Marine){
        				myunits.add(new MarineManager(u, getStartLeft(), this.self));
        			}
        			else if(u.getType() == UnitType.Terran_Medic){
        				myunits.add(new MedicManager(u, getStartLeft(), this.self));
        			}
        			else if(u.getType() == UnitType.Terran_Siege_Tank_Tank_Mode){
        				myunits.add(new SiegeTankManager(u, getStartLeft(), this.self));
        			}
        			else if(u.getType() == UnitType.Terran_Vulture){
        				myunits.add(new VultureManager(u, getStartLeft(), this.self));
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
    
    public void startPosIsLeft(){
    	//Prüft ob man auf der Karte links startet und setzt startLeft entsprechend
    	boolean left = false;
    	for(Unit u: this.mybuildings){
    		System.out.println(u.toString());
    		if(u.getPosition().getX() == 624 && (u.getPosition().getY() == 384 || u.getPosition().getY() == 2752 )){
    			left=true;
    		}
    	}
    	this.startleft = left;
    }
    
    
    
    
    
    public boolean getStartLeft(){
    	return this.startleft;
    }
    
    public boolean getOffensive(){
    	return this.offensive;
    }
    
    public void drawRegions(){
    	for(bwapi.Region r : game.getAllRegions()){
    		game.drawBoxMap(r.getBoundsLeft(), r.getBoundsTop(), r.getBoundsRight(), r.getBoundsBottom(), Color.Yellow);
    		game.drawTextMap(r.getCenter(), "Region ["+r.getCenter().getX()+", "+ r.getCenter().getY()+"] DefensePriority: " + r.getDefensePriority());
    		

    	}
    	
    	
    }
    

    
    
	


}
