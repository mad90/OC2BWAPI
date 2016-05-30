import bwapi.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

public class Vulture {

    private final Mirror bwapi;
    public ArrayList<Unit> enemyUnits;
    final Unit unit;
    XCS vultureXCS;
    public Position targetPos;
   
    
    
    /*
     * Aufbau Muster: Kategorie Abstand + Kategorie Cooldown: z.B. 5+1 -> 51
     * 
     * Aktionen: 1 Angriff
     * 			 2 Rückzug
     * 
     * [5, 1, 1, 10.0, 0.0, 0.01 ]
     * Initialisierung mit: P = 10.0, pe = 0.0, und F = 0.01
     */

    public Vulture(Unit unit, Mirror bwapi, ArrayList<Unit> enemyUnits) {
        this.unit = unit;
        this.bwapi = bwapi;
        this.enemyUnits = enemyUnits;
        this.vultureXCS = new XCS(40, 40);
        //Position des ersten Ziels
        this.targetPos = enemyUnits.get(0).getPosition();
        
        
    }

    public void step() throws InterruptedException {
    	try {
    	System.out.println("TargetPos: "+targetPos.toTilePosition().toString());
    	int distance = categorizeDistance();
    	System.out.println(" Distance" + distance);
    	int cooldown = categorizeCooldown();
    	System.out.println(" cooldown" + cooldown);
    	int action;
    	
    	System.out.println("Pattern: "+ distance+ ", "+cooldown);
    	//System.out.println(this.enemyUnitsToString());
    	//System.out.println(this.vultureXCS.population.toString());
     	action = this.vultureXCS.xcsLoop(distance, cooldown);
     	System.out.println("Aktion" + action);
     	//System.out.println("Action: "+action);
     	//System.out.println("TargetPos: X " + this.targetPos.getX() + ", Y " + this.targetPos.getY() );
//    	System.out.println("Closest Enemy: "+ this.getClosestEnemy().getX()+ ", "+ this.getClosestEnemy().getY());
//    	System.out.println("TargetPos: " + this.targetPos.getX() +", " + this.targetPos.getY());
     	System.out.println("Aktion" +action);
    	if(action == 1){
    		
    		this.attackClosestEnemy();
    		System.out.println("Angriff!");
    		wait(30);
    		}
    	else if(action== 2){
    		this.runAwayFrom(getClosestEnemy());
    		System.out.println("Rückzug!");
    		wait(15);
    		}
        
    	double reward = calculateReward();
    	//System.out.println("Reward: "+ reward);
    	this.vultureXCS.updateActionSet(reward);
    	this.vultureXCS.updatePopulation();
    	//this.addClosestEnemyToEnemyList();
    	//this.targetPos = getClosestEnemy().getPosition();
    	//System.out.println("Größe Enemy List: " + this.enemyUnits.size());
    	} catch (NullPointerException e2){
    		System.out.println("Exception abgewehrt");
            this.unit.move(this.targetPos);
            wait(30);
    	}
    	try {
			this.vultureXCS.writeToFlatFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    private void move(Unit target) {
        unit.move(new Position(target.getPosition().getX(), target.getPosition().getY()), false);
    }


    public Unit getClosestEnemy() {
        Unit result = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Unit enemy : this.bwapi.getGame().enemy().getUnits()) {
        	if (enemy.isVisible(this.bwapi.getGame().self())){
            double distance = this.getDistance(enemy);
            if (distance < minDistance) {
                minDistance = distance;
                result = enemy;
            }
        	}

        }

        return result;
    }

    private double getDistance(Unit enemy) {
    	
    	// Null check für enemy
    	/* if (enemy != null) {
             System.out.println("Enemy is ned NULL");
         }
         else {
             System.out.println("Jackpot");
         }*/
    	
    	return this.unit.getPosition().getDistance(enemy.getPosition());
    }
    
    private double getDistanceClosestEnemy(){
    	//System.out.println("Distanz zum nächsten Gegner: " + this.unit.getPosition().getDistance(this.getClosestEnemy().getPosition()));
    	return this.unit.getPosition().getDistance(this.getClosestEnemy().getPosition());
    }

    
    
	private int categorizeDistance(){
		/*
		 * Kategorien des Abstandes:
		 * 0-25%    der eigenen Reichweite: 1
		 * 26-50%   der eigenen Reichweite: 2
		 * 51-75%   der eigenen Reichweite: 3
		 * 76-100%  der eigenen Reichweite: 4
		 * 101-160% der eigenen Reichweite: 5
		 * >160%	der eigenen Reichweite: 6
		 * 			Wildcard:				0
		 */
		int category = 0;
		double weaponrange = WeaponType.Fragmentation_Grenade.maxRange();
		
		double i = (this.getDistanceClosestEnemy()*100)/(weaponrange);
		 System.out.println("Get Distance closes Enemy test bestanden");
		//System.out.println("% Distanz: "+ i);
		
		if(i <= 25){
			category = 1;
		}
		else if(i > 25 && i <= 50){
			category = 2;
		}
		else if(i > 50 && i <= 75){
			category = 3;
		}
		else if(i > 75 && i <= 100){
			category = 4;
		}
		else if(i > 100 && i <= 160){
			category = 5;
		}
		else if(i > 160){
			category = 6;
		}
				
		return category;
				
	}
	
	private int categorizeCooldown(){
		/*
		 * TODO: Mehr Kategorien?
		 * Kategorien Waffencooldown:
		 * 0		Waffe bereit: 		1
		 * 1-14 	Waffe bald bereit: 	2
		 * 15-30	Waffe nicht bereit: 3
		 * 			Wildcard:			0
		 */
		int category = 0;
		int cd = this.unit.getGroundWeaponCooldown();
		if(cd == 0){
			category = 1;
		}
		else if(cd > 0 && cd < 15){
			category  = 2;
		}
		else if(cd >= 15){
			category = 3;
		}
		return category;
	}
	
	public void runAwayFrom(Unit enemy) throws InterruptedException{
		if(enemy != null){
		double[] vector = new double[2];
		int skalar = 20;
		vector[0] = (unit.getX() - enemy.getX());
		vector[1] = (unit.getY() - enemy.getY());
		//Normierung des Vektors
		double length = Math.sqrt(Math.pow(vector[0], 2)+ Math.pow(vector[1], 2));
		vector[0] = vector[0]/length;
		vector[1] = vector[1]/length;
		//System.out.println("X: "+ vector[0] + " Y: " + vector[1]);
		int x = (int) (vector[0]*skalar);
		int y = (int) (vector[1]*skalar);
		
		Position myPos = new Position((unit.getX()+(x)), (unit.getY()+(y)));
		
		//System.out.println("X: "+ myPos.getX() + " Y: " + myPos.getY());
		this.unit.move(myPos);
		}
		else if (enemy == null){
			//System.out.println("Kein Gegner zum weglaufen");
			this.unit.move(this.targetPos);
			wait(50);
			while(unit.isMoving()){
			wait(20);
			}
			}
			
	}
		
	
	
	public void attackClosestEnemy() throws InterruptedException{
		Unit enemy = getClosestEnemy();
		if(enemy != null){
			this.unit.attack(enemy);
			while(unit.getGroundWeaponCooldown() == 0){
				wait(10);
			}
			
		}
		else if (enemy == null){
			//System.out.println("Kein Gegner zum angreifen");
			unit.move(this.targetPos);
			wait(50);
			while(unit.isMoving()){
				wait(20);
			}
//			this.targetPos = unit.getUnitsInWeaponRange(arg0)
			
		}
		
	}
	
	public double calculateReward(){
		double enemyHpLoss = 0;
		for (Unit enemy : this.enemyUnits){
			enemyHpLoss += ((enemy.getInitialHitPoints()-enemy.getHitPoints())+(enemy.getType().maxShields() - enemy.getShields()));
		}
		
		double reward = enemyHpLoss - (unit.getInitialHitPoints()-unit.getHitPoints());
		
		
		
		return reward;
	}
	
	public ArrayList<Unit> getEnemyUnits(){
		return this.enemyUnits;
	}
	
	public void addClosestEnemyToEnemyList(){
		Unit enemy = this.getClosestEnemy();
		if(!this.enemyUnits.contains(enemy)){
		this.enemyUnits.add(enemy);
		//this.targetPos = enemy.getPosition();
		}
	}
	
	public void setTargetPos(Position target){
		this.targetPos = target;
	}
	
	public Position getTargetPos(){
		return this.targetPos;
		
	}
	
	public String enemyUnitsToString(){
		String s = "";
		for (Unit enemy : this.enemyUnits){
			s += "Einheitentyp: " + enemy.getType().toString()+ "X, Y: "+enemy.getX()+ "; "+ enemy.getY();
		}
		return s;
	}

        
	

    
}
