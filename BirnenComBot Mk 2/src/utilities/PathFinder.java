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
	public static Game game;
	public Player self;
	UnitSet unitSet;
	RegionSet regionSet;
	ArrayList<Region> totalVisitedRegion=new ArrayList<Region>();
//	HashMap<Region,Cost> allVistited=new HashMap<Region,Cost>();
	ArrayList<Region> path=new ArrayList<Region>();
	int increment=0;//bestimmen,dass path ändern oder nicht
	ArrayList<UnitSet> initUnitSet=new ArrayList<UnitSet>();
	ArrayList<RegionSet> initRegionSet=new ArrayList<RegionSet>();
	ArrayList<RegionSet> visitedRegion=new ArrayList<RegionSet>();
	
	public void init(){		
		initUnitSet=unitSet.initUnit();
		initRegionSet=regionSet.initRegion();
	}
	public Region findUnitRegion(int id){		
		Region region=null;
		if(unitSet.id==id){			
			region=unitSet.getUnit().getRegion();
		}
		return region;
	}

	public ArrayList<Region> findNeighbour(bwapi.Region r){
		ArrayList<Region> neighbour=new ArrayList<Region>();
		List<bwapi.Region> neighbourRegion=r.getNeighbors();
		for(Region region:neighbourRegion){
			neighbour.add(region);
		}
//		neighbour=(ArrayList<Region>) neighbourRegion;		
		return neighbour;
	}
//	public void firstCalculate(Region r){
//		ArrayList<Region> neighbour=new ArrayList<Region>();
//		neighbour=findNeighbour(r);
//		init();
//		for(Region region:neighbour){
//			
//		}
//		
//	}
	public void calculateDistance(Region r){
		ArrayList<Region> neighbour=new ArrayList<Region>();
//		ArrayList<Region> tempPath=new ArrayList<Region>();
		
//		init(); // TODO Wo kann man init() legen.
//		for(RegionSet regionSet:initRegionSet){
//			if(r==regionSet.getRegion()){
//				regionSet.path.add(r);								
//			}
//		}k
		neighbour=findNeighbour(r);
		
		for(Region region:neighbour){			
			for(RegionSet regionSet:initRegionSet){				
				if((region==regionSet.getRegion())){
					regionSet.path.add(r);
					if(regionSet.distance==Integer.MAX_VALUE){
						int tempDistance;
						regionSet.setDistance(0);
						for(RegionSet tempR:initRegionSet){
							if(r==tempR.region){
								regionSet.setDistance(tempR.getDistance());
								break;
							}
						}
						tempDistance=r.getDistance(region)+regionSet.getDistance();
						regionSet.path.add(region);
						regionSet.setTotalDistance(tempDistance);
						visitedRegion.add(regionSet);// TODO ist diese Code hier geeignet?
					}else{						
//						if(regionSet.totalDistance!=0){						
							int tempDistance=0;
							regionSet.setDistance(0);
							for(RegionSet tempR:initRegionSet){
								if(r==tempR.region){
									for(int i=1;i<tempR.path.size()-1;i++){							
										tempR.distance+=tempR.path.get(i-1).getDistance(tempR.path.get(i));
									}
									tempDistance=tempR.distance+r.getDistance(region);
								}
							}
							if(regionSet.totalDistance>tempDistance){
								regionSet.setDistance(tempDistance);// TODO
								regionSet.setTotalDistance(tempDistance);
								regionSet.path.clear();
								for(RegionSet tempR:initRegionSet){
									if(r==tempR.region){
										regionSet.path=tempR.path;
										break;
									}										
								}
								regionSet.path.add(region);
							}
							visitedRegion.add(regionSet);// TODO ist diese Code hier geeignet?
					}					
				}
			}
		}		
	}
	public Region findNext(ArrayList<RegionSet> regionSet){
		Region next=null;
		int temp=regionSet.get(0).totalDistance;
		for(RegionSet regionset:visitedRegion){
			if(temp>regionset.getTotalDistance()){
				temp=regionset.totalDistance;
				next=regionset.region;
			}			
		}
		return next;
	}
	
	public void dijkstraAlgorithmus(Region target){
		init();
		for(UnitSet unitSet:initUnitSet){			
			Region startRegion=findUnitRegion(unitSet.getId());
			ArrayList<Region> neighbour=findNeighbour(startRegion);
			ArrayList<Region> path=new ArrayList<Region>();
//			for(Region region:neighbour){
//				if(region!=target){
				boolean b=true;
				calculateDistance(startRegion);
				do{		
					Region next=findNext(visitedRegion);
					calculateDistance(next);
					for(RegionSet regionSet:visitedRegion){
						if(regionSet.getRegion()==target){
							path=regionSet.path;
							b=false;
						}
					}
				}while(b);
//			}
		}
	}
	public ArrayList<Region> dijkstraAlgorithmus(int regionID){
		Region target=null;
		init();
		for(RegionSet regionSet:initRegionSet){
			if(regionSet.id==regionID){
				target=regionSet.region;
			}
		}
		for(UnitSet unitSet:initUnitSet){			
			Region startRegion=findUnitRegion(unitSet.getId());
			ArrayList<Region> neighbour=findNeighbour(startRegion);
//			ArrayList<Region> path=new ArrayList<Region>();
//			for(Region region:neighbour){
//				if(region!=target){
				boolean b=true;
				calculateDistance(startRegion);
				do{		
					Region next=findNext(visitedRegion);
					calculateDistance(next);
					for(RegionSet regionSet:visitedRegion){
						if(regionSet.getRegion()==target){
							path=regionSet.path;
							b=false;
						}
					}
				}while(b);
//			}
		}
		return path;
	}

//	public boolean checkTargetInNeighbour(Region r){
//		boolean targetInNeighbour=false;
//		
//		return targetInNeighbour;
//		
//	}

//	public ArrayList<Region> path(Region r){
//		
//	}
	
	
	
	
	
	
	
		
	
	
	public void run(){
		for (Unit myUnit : self.getUnits()){
			myUnit.getPosition();
		}
		
		
//		dijkstraAlgorithmus();
	}
//	public void dijkstra(bwapi.Region target){		
////		ArrayList<Region> unitCurrentRegion=findCurrent();
//		
////		unitCurrentRegion=findCurrent();
//		ArrayList<bwapi.Region> unitCurrentRegion=findNeighbour(findcurrentRegion());		
//		for(bwapi.Region r:unitCurrentRegion){
//			if(r.getCenter()==target.getCenter()){
//				attack(target.getCenter());
//				System.out.println("b!");
//			}else{
//				path.add(findShortestPath(r));
////				increment++;
//				System.out.println("c!");
//			}
//			while(path.get(path.size()-1).getCenter()!=target.getCenter()){
//				path.add(findShortestPath(path.get(path.size()-1)));
////				increment++;
//			}
//			for (Unit myUnit : self.getUnits()){
//				if(myUnit.getType()==UnitType.Terran_Medic){
//					for(int i=0;i<path.size();i++){
//						move(path.get(i).getCenter());
//					}
//				}
//				if(myUnit.getRegion()==r){
//					for(int i=0;i<path.size();i++){
//						move(path.get(i).getCenter());
//					}
//				}
//			}
//			
//		}
//		
//		
//		
//		
//	}
	
	public void dijkstraAlgorithm(Region target){
		ArrayList<Region> unitCurrentRegion=findCurrent();
		System.out.println("a!");
//		unitCurrentRegion=findCurrent();
		
		for(Region r:unitCurrentRegion){
			if(r.getCenter()==target.getCenter()){
				attack(target.getCenter());
				System.out.println("b!");
			}else{
				path.add(findShortestPath(r));
//				increment++;
				System.out.println("c!");
			}
			while(path.get(path.size()-1).getCenter()!=target.getCenter()){
				path.add(findShortestPath(path.get(path.size()-1)));
//				increment++;
			}
			for (Unit myUnit : self.getUnits()){
				if(myUnit.getType()==UnitType.Terran_Medic){
					for(int i=0;i<path.size();i++){
						move(path.get(i).getCenter());
					}
				}
				if(myUnit.getRegion()==r){
					for(int i=0;i<path.size();i++){
						move(path.get(i).getCenter());
					}
				}
			}
			
		}
		
		
//		ArrayList<Position> unitCurrentPosition=new ArrayList<Position>();
//		ArrayList<Region> unitCurrentRegion=new ArrayList<Region>();
//		unitCurrentRegion=findCurrent();
//		unitCurrentPosition=findCurrentPosition();
//		for(Position p:unitCurrentPosition){
//			if(p.getX()>(target.getBoundsLeft()+50)&&p.getX()<(target.getBoundsRight()+50)&&p.getY()>(target.getBoundsTop()+50)&&p.getY()<(target.getBoundsBottom()+50)){
//				attack(target.getCenter());
//			}else{
//				for(Region r:unitCurrentRegion){
////					findNeighbour(r);
//					Region moveTo=findShortestPath(r,target);
//					move(moveTo.getCenter());
//				}
//			}
//		}
//		
//		for(Region r:unitCurrentRegion){
////			findNeighbour(r);
//			Region moveTo=findShortestPath(r,target);
//			
//		}
		
		
		
	}
	



	private void move(Position center) {
		
		for (Unit myUnit : self.getUnits()){
			myUnit.attack(center);
		}
	}
	private void attack(Position center) {
		
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
//	public ArrayList<Region> findNeighbour(bwapi.Region r){ 
//		ArrayList<Region> neighbour=new ArrayList<Region>();
//		List<bwapi.Region> neighbourRegion=r.getNeighbors();
//		for(Region region:neighbourRegion){
//			neighbour.add(region);
//		}
////		neighbour=(ArrayList<Region>) neighbourRegion;		
//		return neighbour;
//	}
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
					increment++;
				}
			}			
			return path;
		}
		
	}
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
	
		

	
	
	
//	public  ArrayList<Node> findShortestPath(Node[] nodes, Vector[] vectors, Node target) {
//	       int[][] Weight = initializeWeight(nodes, vectors);
//	       int[] D = new int[nodes.length];
//	       Node[] P = new Node[nodes.length];
//	       ArrayList<Node> C = new ArrayList<Node>();
//	       
//	       
//	       // initialize:
//	       // (C)andidate set,
//	       // (D)yjkstra special path length, and
//	       // (P)revious Node along shortest path
//	       for(int i=0; i<nodes.length; i++){
//	           C.add(nodes[i]);
//	           D[i] = Weight[0][i];
//	           if(D[i] != Integer.MAX_VALUE){
//	               P[i] = nodes[0];
//	           }
//	       }
//	       
//	       
//	    // crawl the graph
//	       for(int i=0; i<nodes.length-1; i++){
//	           // find the lightest Edge among the candidates
//	           int l = Integer.MAX_VALUE;
//	           Node n = nodes[0];
//	           for(Node j : C){
//	               if(D[j] < l){
//	                   n = j;
//	                   l = D[j];
//	               }
//	           }
//	           C.remove(n);
//	           
//	           // see if any Edges from this Node yield a shorter path than from source->that Node
//	           for(int j=0; j<nodes.length-1; j++){
//	               if(D[n.name] != Integer.MAX_VALUE && Weight[n.name][j] != Integer.MAX_VALUE && D[n.name]+Weight[n.name][j] < D[j]){
//	                   // found one, update the path
//	                   D[j] = D[n.name] + Weight[n.name][j];
//	                   P[j] = n;
//	               }
//	           }
//	       }
//	       // we have our path. reuse C as the result list
//	       C.clear();
//	       int loc = target.name;
//	       C.add(target);
//	       // backtrack from the target by P(revious), adding to the result list
//	       while(P[loc] != nodes[0]){
//	           if(P[loc] == null){
//	               // looks like there's no path from source to target
//	               return null;
//	           }
//	           C.add(0, P[loc]);
//	           loc = P[loc].name;
//	       }
//	       C.add(0, nodes[0]);
//	       return C;  
//	}
//	   private int[][] initializeWeight(Node[] nodes, Vector[] vectors){
//	       int[][] Weight = new int[nodes.length][nodes.length];
//	       for(int i=0; i<nodes.length; i++){
//	           Arrays.fill(Weight[i], Integer.MAX_VALUE);
//	       }
//	       for(Vector e : vectors){
//	           Weight[e.start][e.target] = e.weight;	//Gewicht ist die Länge der Kanten von der startenden Knoten bis Zielknoten.
//	       }
//	       return Weight;
//	   }
	   
	   
	   
	   
	   
	   
	   

	
	
	

	
	
	
	
		
		
	
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
