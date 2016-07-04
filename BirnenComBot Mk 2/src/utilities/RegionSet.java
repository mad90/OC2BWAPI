package utilities;

import java.util.ArrayList;

import bwapi.Game;
import bwapi.Region;

public class RegionSet {
	
	private Game game;
	
	int id;
	Region region;
	int distance;
	ArrayList<Region> path=new ArrayList<Region>();
	int totalDistance;
//	public RegionSet(){
//		
//	}
	public RegionSet(int iid,Region rregion){
		this.id=iid;
		this.region=rregion;
		this.distance=Integer.MAX_VALUE;
		this.path=null;
		this.totalDistance=0;
	}
	public RegionSet(int iid,Region rregion,int idistance,ArrayList<Region> aregion){
		this.id=iid;
		this.region=rregion;
		this.distance=idistance;
		this.path=aregion;
		this.totalDistance=0;
	}

	
	public ArrayList<RegionSet> initRegion(){
		ArrayList<RegionSet> regionSet=new ArrayList<RegionSet>();		
		for(bwapi.Region r:game.getAllRegions()){			
			regionSet.add(new RegionSet(r.getID(),r));
		}		
		return regionSet;
	}
	
	
	
	public int getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(int totalDistance) {
		this.totalDistance = totalDistance;
	}
	public ArrayList<Region> getPath() {
		return path;
	}
	public void setPath(ArrayList<Region> path) {
		this.path = path;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	

}
