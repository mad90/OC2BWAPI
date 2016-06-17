
import java.util.ArrayList;
import java.util.Random;
public class GAParameter {
	
	//r gehören zu [0,1]
	int r_sig;			//Range of neighborhood of current character
	int r_sep;			//Range of separation
	int r_col;			//Width of column formation
	int r_col_sep;		//Range for separation in column formation
	int r_lin;			//Height of line formation
	int r_lin_sep;		//Range for separation in line formation
	//w gehören zu [0,2]
	int w_r1_a;			//Weight for rule 1 when doesn't see the opponent yet
	int w_r1_b;			//Weight for rule 1 when see the opppnent
	int w_r2;			//Weight for rule 2
	int w_r3;			//Weight for rule 3
	int w_r4;			//Weight for rule 4
	Random rand=new Random();
	int gesamtHP;		//gesamt HP für unsere Marine Einheiten, um fitmess zu kalkulation. fitness=gesamtHP-lostHP.
	ArrayList<GAParameter> arrayGA=new ArrayList<GAParameter>();		//jetzt noch unbenutzt, nur definiert. vllt um GAP. zu speichern.

	//zwei Constructor für Klass GAParameter
	public GAParameter(){
		r_sig=rand.nextInt(2);
		r_sep=rand.nextInt(2);
		r_col=rand.nextInt(2);
		r_col_sep=rand.nextInt(2);
		r_lin=rand.nextInt(2);
		r_lin_sep=rand.nextInt(2);
		w_r1_a=rand.nextInt(3);
		w_r1_b=rand.nextInt(3);
		w_r2=rand.nextInt(3);
		w_r3=rand.nextInt(3);
		w_r4=rand.nextInt(3);		
	}	
	public GAParameter(int rsig,int rsep,int rcol,int rcolsep,int rlin,int rlinsep,int wr1a,int wr1b,int wr2,int wr3,int wr4){
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
	
	
	//crossover und mutation durch Array sind relativ einfach zu implementieren
	//System.arraycopy zu benutzen ist relativ einfach, child Array zu erstellen
	public int[] changeToIntarray(GAParameter g){
		int[] p=new int[11];
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
	public GAParameter changeToGAParameter(int[] p){
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
		int[] iParent1;
		int[] iParent2;		
		GAParameter child1=new GAParameter();
		GAParameter child2=new GAParameter();
		iParent1=changeToIntarray(parent1);
		iParent2=changeToIntarray(parent2);
		int[] iChild1=new int[iParent1.length];
		int[] iChild2=new int[iParent1.length];
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
		int[] iParent1;
		int[] iParent2;		
		GAParameter child1=new GAParameter();
		GAParameter child2=new GAParameter();
		iParent1=changeToIntarray(parents[0]);
		iParent2=changeToIntarray(parents[1]);
		int[] iChild1=new int[iParent1.length];
		int[] iChild2=new int[iParent1.length];
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
	public GAParameter[] mutate(GAParameter[] children){
		GAParameter child1;
		GAParameter child2;
		
		int[] i1=children[0].changeToIntarray(children[0]);
		int[] i2=children[1].changeToIntarray(children[1]);
		int pivot=rand.nextInt(i1.length);
		if(pivot<6){
			if(i1[pivot]==1&&i2[pivot]==1){
				i1[pivot]=0;
				i2[pivot]=0;
			}else if(i1[pivot]==0&&i2[pivot]==1){
				i1[pivot]=1;
				i2[pivot]=0;
			}else if(i1[pivot]==1&&i2[pivot]==0){
				i1[pivot]=0;
				i2[pivot]=1;
			}else{
				i1[pivot]=1;
				i2[pivot]=1;
			}
		}else{
			if(i1[pivot]==2&&i2[pivot]==2){
				i1[pivot]=rand.nextInt(2);
				i2[pivot]=rand.nextInt(2);
			}else if(i1[pivot]==0&&i2[pivot]==0){
				i1[pivot]=rand.nextInt(2)+1;
				i2[pivot]=rand.nextInt(2)+1;
			}else if(i1[pivot]==0&&i2[pivot]==2){
				i1[pivot]=rand.nextInt(2)+1;
				i2[pivot]=rand.nextInt(2);
			}else if(i1[pivot]==2&&i2[pivot]==0){
				i1[pivot]=rand.nextInt(2);
				i2[pivot]=rand.nextInt(2)+1;				
			}else if(i1[pivot]==2&&i2[pivot]==1){
				i1[pivot]=rand.nextInt(2);
				if(rand.nextInt(2)<1){
					i2[pivot]=0;
				}else{
					i2[pivot]=2;
				}
			}else if(i1[pivot]==1&&i2[pivot]==2){
				if(rand.nextInt(2)<1){
					i1[pivot]=0;
				}else{
					i1[pivot]=2;
				}
				i2[pivot]=rand.nextInt(2);
			}else if(i1[pivot]==1&&i2[pivot]==1){
				if(rand.nextInt(2)<1){
					if(rand.nextInt(2)<1){
						i1[pivot]=0;
						i2[pivot]=2;
					}else{
						i1[pivot]=2;
						i2[pivot]=0;
					}
				}else{
					if(rand.nextInt(2)<1){
						i1[pivot]=0;
						i2[pivot]=0;
					}else{
						i1[pivot]=2;
						i2[pivot]=2;
					}
				}
					
			}else if(i1[pivot]==1&&i2[pivot]==0){
				if(rand.nextInt(2)<1){
					i1[pivot]=0;
				}else{
					i1[pivot]=2;
				}
				i2[pivot]=rand.nextInt(2)+1;
			}else if(i1[pivot]==0&&i2[pivot]==1){
				i1[pivot]=rand.nextInt(2)+1;
				if(rand.nextInt(2)<1){
					i2[pivot]=0;
				}else{
					i2[pivot]=2;
				}
			}
			
		}
		child1=changeToGAParameter(i1);
		child2=changeToGAParameter(i2);
		
		return new GAParameter[] {child1,child2};		
	}
	
	
	
	
	
	//set Methode
	public int setRsig(int rsig){
		r_sig=rsig;
		return this.r_sig;
	}
	public int setRsep(int rsep){
		r_sep=rsep;
		return this.r_sep;
	}
	public int setRcol(int rcol){
		r_col=rcol;
		return this.r_col;
	}
	public int setRcolsep(int rcolsep){
		r_col_sep=rcolsep;
		return this.r_col_sep;
	}
	public int setRlin(int rlin){
		r_lin=rlin;
		return this.r_lin;
	}
	public int setRlinsep(int rlinsep){
		r_lin_sep=rlinsep;
		return this.r_lin_sep;
	}
	public int setWr1a(int wr1a){
		w_r1_a=wr1a;
		return this.w_r1_a;
	}

	public int setWr1b(int wr1b){
		w_r1_b=wr1b;
		return this.w_r1_b;
	}
	
	public int setWr2(int wr2){
		w_r2=wr2;
		return this.w_r2;
	}
	
	public int setWr3(int wr3){
		w_r3=wr3;
		return this.w_r3;
	}
	
	public int setWr4(int wr4){
		w_r4=wr4;
		return this.w_r4;
	}
	
	
	//get Methode
	public int getRsig(){
		return this.r_sig;
	}
	
	public int getRsep(){
		return this.r_sep;
	}
	
	public int getRcol(){
		return this.r_col;
	}
	
	public int getRcolsep(){
		return this.r_col_sep;
	}
	
	public int getRlin(){
		return this.r_lin;
	}
	
	public int getRlinsep(){
		return this.r_lin_sep;
	}
	
	public int getWr1a(){
		return this.w_r1_a;
	}

	public int getWr1b(){
		return this.w_r1_b;
	}
	
	public int getWr2(){
		return this.w_r2;
	}
	
	public int getWr3(){
		return this.w_r3;
	}
	
	public int getWr4(){
		return this.w_r4;
	}
}
