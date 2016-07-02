package boiding;

import java.util.ArrayList;
import java.util.Random;
import java.io.*;
public class GAParameter implements Serializable {
	
	//r gehören zu [0,1]
	double r_sig;			//Range of neighborhood of current character
	double r_sep;			//Range of separation
	double r_col;			//Width of column formation
	double r_col_sep;		//Range for separation in column formation
	double r_lin;			//Height of line formation
	double r_lin_sep;		//Range for separation in line formation
	//w gehören zu [0,2]
	double w_r1_a;			//Weight for rule 1 when doesn't see the opponent yet
	double w_r1_b;			//Weight for rule 1 when see the opppnent
	double w_r2;			//Weight for rule 2
	double w_r3;			//Weight for rule 3
	double w_r4;			//Weight for rule 4
	Random rand=new Random();
	int gesamtHP;		//gesamt HP für unsere Marine Einheiten, um fitmess zu kalkulation. fitness=gesamtHP-lostHP.
	int fitness;
	ArrayList<GAParameter> arrayGA=new ArrayList<GAParameter>();		//jetzt noch unbenutzt, nur definiert. vllt um GAP. zu speichern.

	
	//zwei Constructor für Klass GAParameter
	public GAParameter(){
		r_sig=rand.nextDouble();
		while(r_sig == 0){
			r_sig=rand.nextDouble();
		}
		r_sep=rand.nextDouble();
		while(r_sep == 0){
			r_sep=rand.nextDouble();
		}
		r_col=rand.nextDouble();
		while(r_col == 0){
			r_col=rand.nextDouble();
		}
		r_col_sep=rand.nextDouble();
		while(r_col_sep == 0){
			r_col_sep=rand.nextDouble();
		}
		r_lin=rand.nextDouble();
		while(r_lin == 0){
			r_lin=rand.nextDouble();
		}
		r_lin_sep=rand.nextDouble();
		while(r_lin_sep == 0){
			r_lin_sep=rand.nextDouble();
		}
		
		
		w_r1_a=rand.nextDouble()+1;
		while(w_r1_a == 0){
			w_r1_a=rand.nextDouble()+1;
		}
		w_r1_b=rand.nextDouble()+1;
		while(w_r1_b == 0){
			w_r1_b=rand.nextDouble()+1;
		}
		w_r2=rand.nextDouble()+1;
		while(w_r2 == 0){
			w_r2=rand.nextDouble()+1;
		}
		w_r3=rand.nextDouble()+1;
		while(w_r3 == 0){
			w_r3=rand.nextDouble()+1;
		}
		w_r4=rand.nextDouble()+1;
		while(w_r4 == 0){
			w_r4=rand.nextDouble()+1;
		}
		
	}	
	public GAParameter(double rsig,double rsep,double rcol,double rcolsep,double rlin,double rlinsep,double wr1a,double wr1b,double wr2,double wr3,double wr4){
		this.r_sig=rsig;
		this.r_sep=rsep;
		this.r_col=rcol;
		this.r_col_sep=rlinsep;
		this.r_lin=rlin;
		this.r_lin_sep=rlinsep;
		this.w_r1_a=wr1a;
		this.w_r1_b=wr1b;
		this.w_r2=wr2;
		this.w_r3=wr3;
		this.w_r4=wr4;		
	}	
	public GAParameter(double[] doubleGA,int fitness){
		this.r_sig=doubleGA[0];
		this.r_sep=doubleGA[1];
		this.r_col=doubleGA[2];
		this.r_col_sep=doubleGA[3];
		this.r_lin=doubleGA[4];
		this.r_lin_sep=doubleGA[5];
		this.w_r1_a=doubleGA[6];
		this.w_r1_b=doubleGA[7];
		this.w_r2=doubleGA[8];
		this.w_r3=doubleGA[9];
		this.w_r4=doubleGA[10];
		this.fitness=fitness;
	}
	
	//crossover und mutation durch Array sind relativ einfach zu implementieren
	//System.arraycopy zu benutzen ist relativ einfach, child Array zu erstellen
	public double[] changeToIntarray(GAParameter g){
		double[] p=new double[11];
		p[0]=g.r_sig;
		p[1]=g.r_sep;
		p[2]=g.r_col;
		p[3]=g.r_col_sep;
		p[4]=g.r_lin;
		p[5]=g.r_lin_sep;
		p[6]=g.w_r1_a;
		p[7]=g.w_r1_b;
		p[8]=g.w_r2;
		p[9]=g.w_r3;
		p[10]=g.w_r4;	
		
		return p;			
	}
	//verändert int array zum Klass GAParameter
	public GAParameter changeToGAParameter(double[] p){
		GAParameter g=new GAParameter();
		g.r_sig=p[0];
		g.r_sep=p[1];
		g.r_col=p[2];
		g.r_col_sep=p[3];
		g.r_lin=p[4];
		g.r_lin_sep=p[5];
		g.w_r1_a=p[6];
		g.w_r1_b=p[7];
		g.w_r2=p[8];
		g.w_r3=p[9];
		g.w_r4=p[10];		
		
		return g;		
	}
	
	
	//crossover benutzt arraycopy, um position zu vertaschen. 
	//Eingaben sind zwei GAParameter
	public GAParameter[] crossover(GAParameter parent1,GAParameter parent2){
		double[] iParent1;
		double[] iParent2;		
		GAParameter child1=new GAParameter();
		GAParameter child2=new GAParameter();
		iParent1=changeToIntarray(parent1);
		iParent2=changeToIntarray(parent2);
		double[] iChild1=new double[iParent1.length];
		double[] iChild2=new double[iParent1.length];
		int pivot=rand.nextInt(iParent1.length-1)+1;	//one point crossover,pivot gehören zu [1-10]
		//darunter kriegt man children array
		//erst child
		System.arraycopy(iParent1, 0, iChild1, 0, pivot);
		System.arraycopy(iParent2, pivot, iChild1, pivot, (iParent1.length-pivot));
		//zweite child
		System.arraycopy(iParent2, 0, iChild2, 0, pivot);
		System.arraycopy(iParent1, pivot, iChild2, pivot, (iParent1.length-pivot));
		child1=changeToGAParameter(iChild1);
		child2=changeToGAParameter(iChild2);
		
		return new GAParameter[] {child1,child2};		
	}
	//gleich wie darüber liegende Methode, aber Eingaben sind unterschied, also GAParameter[]
	public GAParameter[] crossover(GAParameter[] parents){
		double[] iParent1;
		double[] iParent2;		
		GAParameter child1=new GAParameter();
		GAParameter child2=new GAParameter();
		iParent1=changeToIntarray(parents[0]);
		iParent2=changeToIntarray(parents[1]);
		double[] iChild1=new double[iParent1.length];
		double[] iChild2=new double[iParent1.length];
		int pivot=rand.nextInt(iParent1.length-1)+1;	//one point crossover,pivot gehören zu [1-10]
		//darunter kriegt man children array
		System.arraycopy(iParent1, 0, iChild1, 0, pivot);
		System.arraycopy(iParent2, pivot, iChild1, pivot, (iParent1.length-pivot));
		System.arraycopy(iParent2, 0, iChild2, 0, pivot);
		System.arraycopy(iParent1, pivot, iChild2, pivot, (iParent1.length-pivot));
		child1=changeToGAParameter(iChild1);
		child2=changeToGAParameter(iChild2);
		
		return new GAParameter[] {child1,child2};		
	}
	
	//fitness ausrechnen. anhand der Literatur ist fitness=gesamtHP von unseren Einheiten- verlierte HP von unsere Einheiten 
	//!!!!!Sonstig weiß ich nicht, ob man fitnessCalculation hier legen kann? Denn nach Krieg kann man fitness bekommen.
	//noch nicht fertig!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public int fitnessCalculate(int losthp){
		int fitness;
//		int gesamtHP;
		int lostHP=losthp;
		if((gesamtHP-lostHP)>0){
			fitness=gesamtHP-lostHP;
		}else if((gesamtHP-lostHP)==0){
			fitness=0;
		}else{
			fitness=-1;
		}
		return fitness;
	}
	
	
	//Mutation für die Parameter.
	//ich lege die Mutationsstelle in Nachfahren an der gleiche Stelle. z.B. lege die 3. Position für beide Children.
	//Vordere 6 Parameter sind in [0,1], deshalb verändern wir von 1 zu 0 oder von 0 zu 1;
	//hintere 5 Parameter sind in [0,2], deshalb gibt es meht Möglichkeit, deswegen gibt es viele if... else if...9 Möglichkeiten.
	//d.h. (00)(11)(22)(10)(01)(21)(12)(02)(20)
	public GAParameter[] mutate(GAParameter ch1,GAParameter ch2){
		GAParameter child=new GAParameter();
		GAParameter child1;
		GAParameter child2;
		
		double i;
		double[] i1=child.changeToIntarray(ch1);
		double[] i2=child.changeToIntarray(ch2);
		int pivot=rand.nextInt(i1.length);
		double d1=i1[pivot];
		double d2=i2[pivot];
		if(pivot<6){
			i=rand.nextDouble();
			while(i1[pivot]==i){
				i=rand.nextDouble();
			}
			i1[pivot]=i;
			i=rand.nextDouble();
			while(i1[pivot]==i){
				i=rand.nextDouble();
			}
			i2[pivot]=i;

		}else{
			i=rand.nextDouble()+1;
			while(i1[pivot]==i){
				i=rand.nextDouble()+1;
			}
			i1[pivot]=i;
			i=rand.nextDouble()+1;
			while(i1[pivot]==i){
				i=rand.nextDouble()+1;
			}
			i2[pivot]=i;
		
	
		}
		
		child1=changeToGAParameter(i1);
		child2=changeToGAParameter(i2);
		
		return new GAParameter[] {child1,child2};		
	}
	public GAParameter[] mutate(GAParameter[] children){
		GAParameter child1;
		GAParameter child2;
		double i;					//tempöre Variable.
		
		double[] i1=children[0].changeToIntarray(children[0]);
		double[] i2=children[1].changeToIntarray(children[1]);
		int pivot=rand.nextInt(i1.length);
		if(pivot<6){
			i=rand.nextDouble();
			while(i1[pivot]==i){
				i=rand.nextDouble();
			}
			i1[pivot]=i;
			i=rand.nextDouble();
			while(i1[pivot]==i){
				i=rand.nextDouble();
			}
			i2[pivot]=i;

//			}
		}else{
			i=rand.nextDouble()+1;
			while(i1[pivot]==i){
				i=rand.nextDouble()+1;
			}
			i1[pivot]=i;
			i=rand.nextDouble()+1;
			while(i1[pivot]==i){
				i=rand.nextDouble()+1;
			}
			i2[pivot]=i;

		}
		child1=changeToGAParameter(i1);
		child2=changeToGAParameter(i2);
		
		return new GAParameter[] {child1,child2};		
	}
	
	public String toString(){
		String s = this.r_sig+" " +this.r_sep+" "+this.r_col+" "+this.r_col_sep+" "+this.r_lin+" "+this.r_lin_sep+" "+this.w_r1_a+" "+this.w_r1_b+" "+this.w_r2+" "+this.w_r3+" "+this.w_r4+" "+this.fitness+"\r\n";
		return s;
	}
	
	public double getR_sig() {
		return r_sig;
	}
	public void setR_sig(double r_sig) {
		this.r_sig = r_sig;
	}
	public double getR_sep() {
		return r_sep;
	}
	public void setR_sep(double r_sep) {
		this.r_sep = r_sep;
	}
	public double getR_col() {
		return r_col;
	}
	public void setR_col(double r_col) {
		this.r_col = r_col;
	}
	public double getR_col_sep() {
		return r_col_sep;
	}
	public void setR_col_sep(double r_col_sep) {
		this.r_col_sep = r_col_sep;
	}
	public double getR_lin() {
		return r_lin;
	}
	public void setR_lin(double r_lin) {
		this.r_lin = r_lin;
	}
	public double getR_lin_sep() {
		return r_lin_sep;
	}
	public void setR_lin_sep(double r_lin_sep) {
		this.r_lin_sep = r_lin_sep;
	}
	public double getW_r1_a() {
		return w_r1_a;
	}
	public void setW_r1_a(double w_r1_a) {
		this.w_r1_a = w_r1_a;
	}
	public double getW_r1_b() {
		return w_r1_b;
	}
	public void setW_r1_b(double w_r1_b) {
		this.w_r1_b = w_r1_b;
	}
	public double getW_r2() {
		return w_r2;
	}
	public void setW_r2(double w_r2) {
		this.w_r2 = w_r2;
	}
	public double getW_r3() {
		return w_r3;
	}
	public void setW_r3(double w_r3) {
		this.w_r3 = w_r3;
	}
	public double getW_r4() {
		return w_r4;
	}
	public void setW_r4(double w_r4) {
		this.w_r4 = w_r4;
	}
	public int getGesamtHP() {
		return gesamtHP;
	}
	public void setGesamtHP(int gesamtHP) {
		this.gesamtHP = gesamtHP;
	}
	public int getFitness() {
		return fitness;
	}
	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	
	
	
	
	//set Methode
//	public void setRsig(int rsig){
//		r_sig=rsig;
////		return this.r_sig;
//	}
//	public void setRsep(int rsep){
//		r_sep=rsep;
////		return this.r_sep;
//	}
//	public double setRcol(int rcol){
//		r_col=rcol;
//		return this.r_col;
//	}
//	public double setRcolsep(int rcolsep){
//		r_col_sep=rcolsep;
//		return this.r_col_sep;
//	}
//	public int setRlin(int rlin){
//		r_lin=rlin;
//		return this.r_lin;
//	}
//	public int setRlinsep(int rlinsep){
//		r_lin_sep=rlinsep;
//		return this.r_lin_sep;
//	}
//	public int setWr1a(int wr1a){
//		w_r1_a=wr1a;
//		return this.w_r1_a;
//	}
//
//	public int setWr1b(int wr1b){
//		w_r1_b=wr1b;
//		return this.w_r1_b;
//	}
//	
//	public int setWr2(int wr2){
//		w_r2=wr2;
//		return this.w_r2;
//	}
//	
//	public int setWr3(int wr3){
//		w_r3=wr3;
//		return this.w_r3;
//	}
//	
//	public int setWr4(int wr4){
//		w_r4=wr4;
//		return this.w_r4;
//	}
//	
//	
//	//get Methode
//	public int getRsig(){
//		return this.r_sig;
//	}
//	
//	public int getRsep(){
//		return this.r_sep;
//	}
//	
//	public int getRcol(){
//		return this.r_col;
//	}
//	
//	public int getRcolsep(){
//		return this.r_col_sep;
//	}
//	
//	public int getRlin(){
//		return this.r_lin;
//	}
//	
//	public int getRlinsep(){
//		return this.r_lin_sep;
//	}
//	
//	public int getWr1a(){
//		return this.w_r1_a;
//	}
//
//	public int getWr1b(){
//		return this.w_r1_b;
//	}
//	
//	public int getWr2(){
//		return this.w_r2;
//	}
//	
//	public int getWr3(){
//		return this.w_r3;
//	}
//	
//	public int getWr4(){
//		return this.w_r4;
//	}
//	
//	public int getFitness(){
//		return this.fitness;
//	}
}
