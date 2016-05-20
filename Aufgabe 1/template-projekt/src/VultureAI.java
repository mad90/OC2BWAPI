import bwapi.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class VultureAI  extends DefaultBWListener implements Runnable {

    private final Mirror bwapi;
    
    private Game game;

    private Player self;

    private Vulture vulture;

    private ArrayList<Unit> enemyUnits;

    private int frame;
    
    int unitsKilled = 0;
    


    public VultureAI() {
        System.out.println("This is the VultureAI! :)");
        this.bwapi = new Mirror();
    }

    public static void main(String[] args) {
        new VultureAI().run();
    }

    @Override
    public void onStart() {
        enemyUnits = new ArrayList<Unit>();
        this.game = this.bwapi.getGame();
        this.self = game.self();
        this.frame = 0;

        // complete map information
        this.game.enableFlag(0);
        
        // user input
        this.game.enableFlag(1);
        this.game.setLocalSpeed(10);
        
        
    }

    @Override
    public void onFrame() {
    	if(frame % 30 == 0){
    		try {
    			//System.out.println("Größe Enemy List in VultureAI: " + this.enemyUnits.size());
				vulture.step();
//				System.out.println("Step()");
				updateEnemyList();
				updateEnemyListAI();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        

        if (frame % 1000 == 0) {
            System.out.println("Frame: " + frame);
        }
        frame++;
    }

    @Override
    public void onUnitCreate(Unit unit) {
        System.out.println("New unit discovered " + unit.getType());
        UnitType type = unit.getType();
        //TODO: Möglichkeit einbauen sich an voriger Spiele zu erinnern.
        if (type == UnitType.Terran_Vulture) {
            if (unit.getPlayer() == this.self) {
                this.vulture = new Vulture(unit, bwapi, enemyUnits);
            }
        } else if (type == UnitType.Protoss_Zealot) {
            if (unit.getPlayer() != this.self && unit.exists()) {
                enemyUnits.add(unit);
//                if(!this.vulture.enemyUnits.contains(unit)){
//                this.vulture.enemyUnits.add(unit);
//                this.vulture.setTargetPos(unit.getPosition());
//                }
            }
        }
    }
    
    
    @Override
    public void onUnitDestroy(Unit unit) {
    	if(this.enemyUnits.contains(unit)){
    		
    		this.enemyUnits.remove(unit);
    		this.updateEnemyList();
			this.updateEnemyListAI();
    		this.vulture.unit.move(this.vulture.getTargetPos());
    		try {
				wait(70);
				while(this.vulture.unit.isMoving()){
					wait(20);
				}
			this.vulture.setTargetPos(this.vulture.getClosestEnemy().getPosition());

			
    		//this.vulture.enemyUnits.remove(unit);
//    		this.enemyUnits.remove(unit);
    		

//				this.vulture.addClosestEnemyToEnemyList();
				//this.vulture.setTargetPos(this.vulture.enemyUnits.get(unitsKilled).getPosition());
//				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    

    @Override
    public void onEnd(boolean winner) {
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

    @Override
    public void onUnitShow(Unit unit) {

    }

    @Override
    public void onUnitHide(Unit unit) {
    }

    @Override
    public void onUnitMorph(Unit unit) {

    }

    @Override
    public void onUnitRenegade(Unit unit) {

    }

    @Override
    public void onSaveGame(String gameName) {
    }

    @Override
    public void onUnitComplete(Unit unit) {
    }

    @Override
    public void onPlayerDropped(Player player) {
    }

    @Override
    public void run() {
        this.bwapi.getModule().setEventListener(this);
        this.bwapi.startGame();
    }
    
//    @Override
//    public void onUnitShow(Unit unit){
//    	if(unit.getType() == UnitType.Protoss_Zealot && !(this.enemyUnits.contains(unit))){
//    	System.out.println("Zealot entdeckt!");
//    	this.vulture.targetPos = unit.getPosition();
//    	}
//    }
    
    public void updateEnemyListAI(){
    	this.vulture.enemyUnits = this.enemyUnits;
//    	if(unitsKilled <= this.enemyUnits.size()){
//    	this.vulture.setTargetPos(this.enemyUnits.get(unitsKilled).getPosition());
//    	}
    }
    
    public void updateEnemyList(){
    	for(Unit enemy : game.enemy().getUnits()){
    		if(!(this.enemyUnits.contains(enemy)) && enemy.exists() && enemy.getType() == UnitType.Protoss_Zealot){
    			this.enemyUnits.add(enemy);
    		}

    		
    	}
    }
}
