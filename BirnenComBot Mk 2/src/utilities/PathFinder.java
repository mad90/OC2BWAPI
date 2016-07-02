package utilities;

import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Region;
import bwapi.Unit;
import bwapi.WalkPosition;
import bwapi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;


public class PathFinder {
	private static Game game;
	private Player self;
	ArrayList<Region> totalVisitedRegion=new ArrayList<Region>();
//	HashMap<> // TODO Jeden besuchende Knoten müssen in HashMap speichern, die Region und Gewicht bzw Cost oder Länge des Pfads beinhaltet.
//	HashMap<Region,Cost> allVistited=new HashMap<Region,Cost>();
	ArrayList<Region> path=new ArrayList<Region>();
	int increament=0;//bestimmen,dass path ändern oder nicht
	public void run(){// TODO
		for (Unit myUnit : self.getUnits()){
			myUnit.getPosition();
		}
		
		
//		dijkstraAlgorithmus();
	}
	public void dijkstraAlgorithmus(Position startP,Position zielP){	// TODO	
		
		
		
		
		
	}
	
	public void dijkstraAlgorithmus(Region target){
		
		ArrayList<Position> unitCurrentPosition=new ArrayList<Position>();
		ArrayList<Region> unitCurrentRegion=new ArrayList<Region>();
		unitCurrentRegion=findCurrent();
		unitCurrentPosition=findCurrentPosition();
		for(Position p:unitCurrentPosition){
			if(p.getX()>(target.getBoundsLeft()+50)&&p.getX()<(target.getBoundsRight()+50)&&p.getY()>(target.getBoundsTop()+50)&&p.getY()<(target.getBoundsBottom()+50)){
				attack(target.getCenter());
			}else{
				for(Region r:unitCurrentRegion){
//					findNeighbour(r);
					Region moveTo=findShortestPath(r,target);
					move(moveTo.getCenter());
				}
			}
		}
		
		for(Region r:unitCurrentRegion){
//			findNeighbour(r);
			Region moveTo=findShortestPath(r,target);
			
		}
		
		
		
	}


	private void move(Position center) {
		// TODO Auto-generated method stub
		for (Unit myUnit : self.getUnits()){
			myUnit.attack(center);
		}
	}
	private void attack(Position center) {
		// TODO Auto-generated method stub
		for (Unit myUnit : self.getUnits()){
			myUnit.attack(center);
		}
	}
	public Region positionInRegion(bwapi.Position p){
		Region region=null;
		for(bwapi.Region r:game.getAllRegions()){
			if(p.getX()>=r.getBoundsLeft()&&p.getX()<=r.getBoundsRight()&&p.getY()>=r.getBoundsTop()&&p.getY()<=r.getBoundsBottom()){
				region=r;
			}
		}
		return region;
	}
	
	public Position findPosition(Position position){
		Position p=position;		
		return p;
	}
	public ArrayList<Region> findKandidaten(){
		ArrayList<Region> kandidaten=new ArrayList<Region>();
		for(bwapi.Region r:game.getAllRegions()){ 
    		kandidaten.add(r);
		}
		return kandidaten;
	}
	public ArrayList<Region> findCurrent(){
		ArrayList<Region> currentRegion=new ArrayList<Region>();
		for (Unit myUnit : self.getUnits()){			
			bwapi.Region region=myUnit.getRegion();
			currentRegion.add(region);
		}
		return currentRegion;
	}
	public ArrayList<Position> findCurrentPosition(){
		ArrayList<Position> unitCurrentPosition=new ArrayList<Position>();
		for (Unit myUnit : self.getUnits()){			
			unitCurrentPosition.add(myUnit.getPosition());
		}
		return unitCurrentPosition;
	}
	//currentUnitRegion zu finden
	public ArrayList<Region> findNeighbour(){
		ArrayList<Region> neighbour=new ArrayList<Region>();
		for (Unit myUnit : self.getUnits()){			
			bwapi.Region region=myUnit.getRegion();
			List<bwapi.Region> neighbourRegion=region.getNeighbors();
			neighbour=(ArrayList<Region>) neighbourRegion;
//			ArrayList<Position> temp;
//			for(Region t:neighbourRegion){
//				neighbour.add(neighbourRegion);
//			}				
		}
		return neighbour;
	}	
	// Nachbarn der Nachbarn zu finden.
	public ArrayList<Region> findNeighbour(bwapi.Region r){// TODO
		ArrayList<Region> neighbour=new ArrayList<Region>();
		List<bwapi.Region> neighbourRegion=r.getNeighbors();
		for(Region region:neighbourRegion){
			neighbour.add(region);
		}
//		neighbour=(ArrayList<Region>) neighbourRegion;		
		return neighbour;
	}
	public ArrayList<Integer> findCost(bwapi.Region region){
		ArrayList<Integer> cost=new ArrayList<Integer>();
		ArrayList<Region> neighbour=new ArrayList<Region>();
		neighbour=findNeighbour(region);
		for(Region r:neighbour){
			cost.add(region.getDistance(r));
		}			
		return cost;
	}
	public int findLowestCost(bwapi.Region region){
//		ArrayList<Integer> cost=new ArrayList<Integer>();
		int lowestCost;
		ArrayList<Region> neighbour=new ArrayList<Region>();
		neighbour=findNeighbour(region);
		lowestCost=region.getDistance(neighbour.get(0));
		for(Region r:neighbour){
			if(lowestCost>region.getDistance(r)){
				lowestCost=region.getDistance(r);
			}
		}			
		return lowestCost;
	}
	public int findLowestCost(bwapi.Region start, bwapi.Region target){
		int lowestCost;
		ArrayList<Region> neighbour=new ArrayList<Region>();
		neighbour=findNeighbour(start);
		lowestCost=start.getDistance(neighbour.get(0))+neighbour.get(0).getDistance(target);
		for(Region r:neighbour){
			if(lowestCost>(start.getDistance(r)+r.getDistance(target))){
			lowestCost=	start.getDistance(r)+r.getDistance(target);
			}				
		}			
		return lowestCost;
	}
	//Finden shortest Path
	public Region findShortestPath(bwapi.Region region){		
		Region shortest=null;
		ArrayList<Region> neighbour=new ArrayList<Region>();
		neighbour=findNeighbour(region);
		int i=region.getDistance(neighbour.get(0));
		for(Region r:neighbour){
			if(i>region.getDistance(r)){
				i=region.getDistance(r);
				shortest=r;
			}
		}
		return shortest;		
	}
	//Finden Path von start zu target, jedes Mal speichert man nur ein Region.
	public ArrayList<Region> findShortestPathArray(bwapi.Region startRegion,bwapi.Region targetRegion){
		
		if(startRegion==targetRegion){
			return path;
		}else{
			int lowestCost;
			ArrayList<Region> neighbour=new ArrayList<Region>();
			neighbour=findNeighbour(startRegion);
			lowestCost=startRegion.getDistance(neighbour.get(0))+neighbour.get(0).getDistance(targetRegion);
			for(Region r:neighbour){
				if(lowestCost>(startRegion.getDistance(r)+r.getDistance(targetRegion))){
					lowestCost=	startRegion.getDistance(r)+r.getDistance(targetRegion);
				}				
			}
			for(Region r:neighbour){
				if(lowestCost==(startRegion.getDistance(r)+r.getDistance(targetRegion))){
					path.add(r);
					increament++;
				}
			}			
			return path;
		}
		
	}
	//suchen die näheste region zu targetRegion
	public Region findShortestPath(bwapi.Region startRegion,bwapi.Region targetRegion){
		int lowestCost;
		Region region=null;
		ArrayList<Region> neighbour=new ArrayList<Region>();
		neighbour=findNeighbour(startRegion);
		lowestCost=startRegion.getDistance(neighbour.get(0))+neighbour.get(0).getDistance(targetRegion);
		for(Region r:neighbour){
			if(lowestCost>(startRegion.getDistance(r)+r.getDistance(targetRegion))){
			lowestCost=	startRegion.getDistance(r)+r.getDistance(targetRegion);
			}				
		}
		for(Region r:neighbour){
			if(lowestCost==(startRegion.getDistance(r)+r.getDistance(targetRegion))){
				region=r;
			}
		}
		return region;
	}
//	public ArrayList<Double> getCost(Position current,ArrayList<Position> neighbour,Position target) {
//		ArrayList<Double> cost=new ArrayList<Double>();
//		for(Position p:neighbour){
//			int cx1=p.getX()-current.getX();
//			int cy1=p.getY()-current.getY();
//			double result1=(double)(Math.sqrt((cx1*cx1)+(cy1*cy1)));
//			int cx2=p.getX()-target.getX();
//			int cy2=p.getY()-target.getY();
//			double result2=(double)(Math.sqrt((cx2*cx2)+(cy2*cy2)));
//			double result=result1+result2;
//			cost.add(result);
//		}
//		return cost;
//	}
	
//	public Position selectShortest(ArrayList<Double> arrayCost){
//		double x=arrayCost.get(0);
//		int shortestPass;
//		for(int i=1;i<arrayCost.size();i++){			
//			if(x>arrayCost.get(i)){
//				x=arrayCost.get(i);
//				shortestPass=i;
//			}
//		}
//		
//		return x;		
//	}
	

	
	
	
		
		
	
//	public static final int MAX_WALKABLE_RANGE = 20;
//	public static Node[][] walkableNodes;
//	private static int mapWalkWidth;
//	private static int mapWalkHeight;
//	
//	public PathFinder(){
//		mapWalkWidth = getMapWalkWidth();
//		mapWalkHeight = getMapWalkHeight();		
//		// Init walkable map in TileMap.
//		walkableNodes = new Node[mapWalkWidth][mapWalkHeight];
//		for (int wx = 0; wx < mapWalkWidth; wx++) {
//			walkableNodes[wx] = new Node[mapWalkHeight];
//			for (int wy = 0; wy < mapWalkHeight; wy++) {
//				walkableNodes[wx][wy] = new Node(wx, wy);
//			}
//		}
//		
//		
//	}
//	
//	public void findPath(){
//		ArrayList<Integer> distance=new ArrayList<Integer>();
//		
//		
//	}
//	
//	
//	
//	public float getCost(int x, int y, int tx, int ty) {		
//		float dx = tx - x;
//		float dy = ty - y;		
//		float result = (float) (Math.sqrt((dx*dx)+(dy*dy)));		
//		return result;
//	}
//	public float getCost(Position p1,Position p2) {		
//		float dx = p2.getX() - p1.getX();
//		float dy = p2.getY() - p2.getY();		
//		float result = (float) (Math.sqrt((dx*dx)+(dy*dy)));		
//		return result;
//	}
//	public boolean isNotWalkable(int x,int y){
//		boolean bWalkable=game.isWalkable(x, x);
//		return bWalkable;
//	}
//	public boolean isNotWalkable(Position p){
//		boolean bWalkable=game.isWalkable(p.getX(),p.getY());
//		return bWalkable;
//	} 
//	
//	
//	
//	
//	
//	
//	
//	public static int getMapWidth() {
//		return game.mapWidth();
//	}
//
//	/**
//	 * @return the width of the map in walk tiles
//	 */
//	public static int getMapWalkWidth() {
//		return 4 * getMapWidth();
//	}
//
//	/**
//	 * @return the height of the map in build tiles
//	 */
//	public static int getMapHeight() {
//		return game.mapHeight();
//	}
//
//	/**
//	 * @return the height of the map in walk tiles
//	 */
//	public static int getMapWalkHeight() {
//		return 4 * getMapHeight();
//	}

}
