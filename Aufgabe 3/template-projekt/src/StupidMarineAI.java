
import bwapi.*;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by Steffen Wilke on 31.05.2016.
 */
public class StupidMarineAI extends DefaultBWListener implements Runnable {
	
	private static boolean usememory = true;
	private final Mirror bwapi;
	private Game game;
	private Player self;
	private final HashSet<Marine> marines;
	private final HashSet<Unit> enemyUnits;
	private int frame;
	Population population;
	GAParameter chosenGAParam;
	private boolean populated = false;

	public StupidMarineAI() throws FileNotFoundException {
		System.out.println("This is the StupidMarineAI! :)");

		this.bwapi = new Mirror();
		this.marines = new HashSet<Marine>();
		this.enemyUnits = new HashSet<Unit>();
	
	}

	public static void main(String[] args) throws FileNotFoundException {
		new StupidMarineAI().run();
	}

	@Override
	public void onStart() {
		//Initialisiere Population
		if(Population.getNumberOfEntriesInFlatFile() < 5 ||!usememory){
			GAParameter tmpgap = new GAParameter();
			this.chosenGAParam = tmpgap;
			this.population = new Population(tmpgap);
		}
		else{
			setPopulated(true);
			try {
				this.population = new Population(Population.readFromFlatFile());
				GAParameter[] children = Population.evolve(this.population.chooseGAParameter(), this.population.chooseGAParameter());
				Random rand=new Random();
				int chosen = rand.nextInt(2);
				
				this.chosenGAParam = children[chosen];
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("ReadFromFlatFile() fehlgeschlagen! Benutze stattdessen zufälligen GAParameter");
				GAParameter tmpgap = new GAParameter();
				this.chosenGAParam = tmpgap;
				this.population = new Population(tmpgap);
			}
		}

		this.game = this.bwapi.getGame();
		this.self = game.self();
		this.frame = 0;

		// complete map information
		this.game.enableFlag(1);

		// user input
		this.game.enableFlag(1);
		this.game.setLocalSpeed(0);
		this.game.setGUI(false);

	}

	@Override
	public void onFrame() {
		int i = 0;
		System.out.println("Anzahl der gesamten Marines: " + this.marines.size());
		for (Marine m : this.marines) {
			i++;
			System.out.println("\n"+"-------Durchlauf "+ i+" ---------");
			try {
				m.step();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Etwas lief im Durchgang "+ i +" schief!");
				e.printStackTrace();
			}
			System.out.println("-----------------------------"+"\n");
		}

		if (frame % 1000 == 0) {
			System.out.println("Frame: " + frame);
		}
		frame++;
	}

	@Override
	public void onUnitCreate(Unit unit) {
		if (unit.getType() == UnitType.Terran_Marine) {
			if (unit.getPlayer().equals(this.self)) {
				this.marines.add(new Marine(unit, this.enemyUnits, game, this.chosenGAParam));
			} else {
				this.enemyUnits.add(unit);
			}
		}
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		if (unit == null) {
			return;
		}

		Marine rm = null;
		for (Marine marine : marines) {
			if (marine.getID() == unit.getID()) {
				rm = marine;
				break;
			}
		}
		marines.remove(rm);

		Unit rmUnit = null;
		for (Unit u : enemyUnits) {
			if (u.getID() == unit.getID()) {
				rmUnit = u;
				break;
			}
		}

		enemyUnits.remove(rmUnit);
	}

	@Override
	public void onEnd(boolean winner) {
		if(winner){
			this.chosenGAParam.setFitness(calculateFitness());
			if(usememory){
				this.chosenGAParam.setFitness(calculateFitness());
				Population.appendToFlatFile(this.chosenGAParam);
			}
		}
		this.marines.clear();
		this.enemyUnits.clear();
		
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

		if (unit.getType() == UnitType.Terran_Marine && !unit.getPlayer().equals(this.self)) {
			if (!this.enemyUnits.contains(unit)) {
				this.enemyUnits.add(unit);
			}
		}
	}

	@Override
	public void onUnitHide(Unit unit) {
		if (unit.getType() == UnitType.Terran_Marine && !unit.getPlayer().equals(this.self)) {
			if (!this.enemyUnits.contains(unit)) {
				this.enemyUnits.remove(unit);
			}
		}
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
	
	public void setPopulated(boolean bool){
		this.populated = bool;
	}
	
	public int calculateFitness(){
		int fitness = 0;
		if(marines.isEmpty()){
			return -50;
		}
		else{
			for(Marine m: marines){
				fitness += m.getUnit().getHitPoints();
			}
			return fitness;
		}
	}
	
	
	
	
	
	
}
