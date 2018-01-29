package application.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;

import application.Main;
import application.model.Brett.SpielZug;

/**
 * Class that generates next possible moves.
 * 
 * this is littered with commented sysouts
 * and needs work to be a capable ai
 * currently this just plays possible
 * moves and sometimes ones that where detected
 * by an attempt at implementing chain-detection
 */
public class SpielAI
{
	private Brett _brett;
	private ArrayList<LinkedHashSet<Savegame>> _possibleMoves;
	
	/** 
	 * Creates an ai obj and sets the internal data according to the present brett
	 * @param brett Brett of which an ai gets created
	 */
	public SpielAI(Brett brett) 
	{
		_brett=brett;
		_possibleMoves=new ArrayList<LinkedHashSet<Savegame>>();
//		System.out.println("_pm:"+_possibleMoves);
//		System.out.println("pm.size:"+_possibleMoves.size());
		if(brett.getSpielZuege().size() > 0)
		{
			Savegame wurzel = new Savegame(brett);
			LinkedHashSet<Savegame>menge =new LinkedHashSet<Savegame>();
			menge.add(wurzel);
//			System.out.println("moveNr:"+wurzel.moveNr);
			_possibleMoves.add(wurzel.moveNr-1, menge);
		}
//		else
//			System.out.println("das brett hat noch keine steine!");
	}
	
	/**
	 * Room for future expansion
	 * should look at every possible move at the end of the List of possible moves and generate a Savegame for each
	 * such that those can be evaluated via heuristic
	 */
	public void generateNextMoves()
	{
		int tiefe=_possibleMoves.size()-1;
//		System.out.println("generateNextMoves::tiefe:"+tiefe);
		for (Iterator<Savegame> it = (_possibleMoves.get(tiefe)).iterator(); it.hasNext();) // for each leaf
		{
			Savegame leaf = (Savegame) it.next(); // current leaf
//			LinkedHashSet<Savegame> newLeaves=new LinkedHashSet<Savegame>(); // leaves of the current leaf
//			newLeaves.addAll(leaf.generateNextMoves()); // generate and add new leaves to local var
//			_possibleMoves.get(tiefe).addAll(newLeaves); // add generated leaves to possibleMoves
			
			Double[][] h=leaf.generateHeuristic();
//			System.out.println("Heuristic: ");
//			for (int j = 0; j < leaf.dim; j++)
//			{
//				for (int i = 0; i < leaf.dim; i++)
//					System.out.print(h[i][j]==null?(leaf.steine[i][j]<0?".":(leaf.steine[i][j]==0?"W":"B")):String.format("%1.0f", h[i][j]));
//				System.out.println();
//			}
//			System.out.println();
		}
//		System.out.println("_pm.gen}:"+_possibleMoves);
	}
	
	/**
	 * looks at its brett and updatesa and ads untracked occured moves
	 */
	public void updateMoves()
	{
//		for (LinkedHashSet<Savegame> moeglicherZug : _possibleMoves) {
//			if (moeglicherZug.size()>1)
//			{
//				System.out.println("move:"+_brett.getSpielZuege().get(moeglicherZug.iterator().next().moveNr));
//			}
//		}

		for (int i = 0; i < _brett.getSpielZuege().size(); i++) 
		{
			SpielZug zug = _brett.getSpielZuege().get(i);
			if(_possibleMoves.size()<=i)
			{
				// generate missing move
				Savegame s=new Savegame(_brett);
				LinkedHashSet<Savegame> menge=new LinkedHashSet<Savegame>();
				menge.add(s);
				_possibleMoves.add(i, menge);
			}
			else if(_possibleMoves.get(i).size()!=1)
			{
				//TODO: clip tree
				// if fiture minimax gets implemented
//				System.out.println(zug);
			}
		}

		// vorerst auskommentiert, da das noch nicht vorhanden ist
		//		generateNextMoves();
	}
	
	/**
	 * @return Integer[][] list of Brett Positions of possible moves.
	 * Data Representation: [Nr][0=x, 1=y]
	 */
	public Integer[][] getBestMoves()
	{
		Double[][] heuristic=_possibleMoves.get(_possibleMoves.size()-1).iterator().next().generateHeuristic();
		
		double bestValue=Double.NEGATIVE_INFINITY;
		int amountBest=0;
		int dim =_possibleMoves.get(_possibleMoves.size()-1).iterator().next().dim;
		
		// find best value
		for (int i = 0; i < heuristic.length; i++)
			for (int j = 0; j<heuristic[i].length; j++)
				if(heuristic[i][j] != null)
					if(bestValue < heuristic[i][j])
					{
						bestValue=heuristic[i][j];
						amountBest=1;
					}
					else if (bestValue==heuristic[i][j])
						amountBest++;

		Integer[][] erg = new Integer[amountBest][2];
		
		for (int i = 0; i < heuristic.length; i++)
			for (int j = 0; j < heuristic[i].length; j++)
				if(heuristic[i][j] != null 
					&& bestValue==heuristic[i][j])
				{
					amountBest--;
					erg[amountBest][0]=i;
					erg[amountBest][1]=j;
				}
		return erg;
	}
	
	/**
	 * Internal Class to save a gamestate such that future possile moves can be generated.
	 * And the current state of the board can be saved more simplified
	 */
	public static class Savegame
	{
		int moveNr;
		int[][] steine;
		int spielerAnz;
		int dim;
		int[] nextMove;
		
		public Savegame(Brett brett) {
			SpielStein[][] s=brett.getBrett();
			dim=brett.getDim();
			steine=new int[dim][dim];
			moveNr=0;
			for (int x = 0; x < dim; x++)// copy colors
				for (int y = 0; y < dim; y++)
					if(s[x][y]!=null)
					{
						steine[x][y]=s[x][y].getColor();
						moveNr++;
					}
					else
						steine[x][y]=-1;
			spielerAnz=brett.getSpieler();
		}

		/**
		 * placeholder for future generation
		 * this would generate leaves and their heuristic
		 */
		
		@Deprecated // da erstmal nur die heuristik via matrix benutzt werden soll
		public LinkedHashSet<Savegame> generateNextMoves()
		{
//			System.out.println("Savegame::generateNextMoves");
			LinkedHashSet<Savegame> erg = new LinkedHashSet<Savegame>();
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					if(steine[i][j]>=0) // auf dem feld ist ein stein
					{
						//TODO: brett generieren...
					}
				}
			}
			
			return erg;
		}

		/**
		 * Generates a Double[][] Matrix which holds all possible locations for a future move from this SaveGame
		 * each Position that is not playable contains null any other a score for that positions move
		 */
		public Double[][] generateHeuristic()
		{
			//TODO: almost everything, this has been put on hold untill anythigng else if completely done
			// this was not the focus, so it was abandoned
			
			Double[][] erg= new Double[dim][dim];
			
			// generate possible location
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					if(steine[i][j]>=0) // auf dem feld ist ein stein
					{	
						if(i+1<dim&&j+1<dim)
							erg[i+1][j+1]=new Double(0);
						if(i+1<dim)
							erg[i+1][j  ]=new Double(0);
						if(i+1<dim&&j-1>=0)
							erg[i+1][j-1]=new Double(0);
						if(j+1<dim)
							erg[i  ][j+1]=new Double(0);
						if(j-1>=0)
							erg[i  ][j-1]=new Double(0);
						if(i-1>=0&&j+1<dim)
							erg[i-1][j+1]=new Double(0);
						if(i-1>=0)
							erg[i-1][  j]=new Double(0);
						if(i-1>=0&&j-1>=0)
							erg[i-1][j-1]=new Double(0);
					}
				}
			}
			
			// delete positions where there is already a stone present
			for (int i = 0; i < erg.length; i++)
				for (int j = 0; j < erg.length; j++)
					if(steine[i][j]>=0)
						erg[i][j]=null;
			
			// bewertung:
			int spielendeFarbe=moveNr%spielerAnz;
			ArrayList<Double[][]> unbeschraenkteReihe= new ArrayList<Double[][]>();
			ArrayList<Double[][]> beschraenkteReihe= new ArrayList<Double[][]>();
						
			// befuelle arrays mit klonen der spielbaren zuege
			for (int i = 0; i < (int) Main.optionen.getOption("inEinerReihe")-1; i++) // -1 da sonst schon gewonnen
			{
				// nicht nur erste ebene klonen, auch die darunter liegende
				Double[][] klon1 = twoDeepCloneDouble(erg);
				Double[][] klon2 = twoDeepCloneDouble(erg);
				
				unbeschraenkteReihe.add(klon1);
				beschraenkteReihe.add(klon2);
			}
			
			/*
			for (int i = 0; i < steine.length; i++)
			{
				int startDerZaehlung=-1; // noch keiner
				int laengeDerKette=0; // noch keiner
				boolean obenBeschraenkt=false;
				for (int j = 0; j < steine[i].length; j++)
				{
					// es gibt einen stein der momentan spielenden farbe
					if(steine[i][j]==spielendeFarbe)
					{
						if(j>0) // nicht der allererste
						{
							if(steine[i][j-1]<0) // vorheriges feld ist frei
							{
								startDerZaehlung=j;
								laengeDerKette=1;
							}
							else if(steine[i][j-1]==spielendeFarbe) // vorheriges feld ist mit gleicher farbe belegt
								laengeDerKette++; // einer mehr gefunden
							else // stein anderer farbe auf vorherigem feld
							{
								startDerZaehlung=j;
								laengeDerKette=1;
								obenBeschraenkt=true;
							}
						}
						else // aller erster stein auf dem feld
						{
							startDerZaehlung=j;
							laengeDerKette=1;
						}
					}
					// noch ein stein wurde gefunden
					else if(steine[i][j]==spielendeFarbe&&laengeDerKette!=0)
					{
						laengeDerKette++;
					}
					else if(steine[i][j]!=spielendeFarbe) // ende der kette || ignorieren
					{
						if(laengeDerKette>0) // davor wurden steine gefunden
						{
							if(obenBeschraenkt && steine[i][j]>=0) // auf beiden seiten blockiert
							{									   // ignoriere gefundene kette
								// reset variables
								laengeDerKette=0;
								startDerZaehlung=-1;
								obenBeschraenkt=false;
							}
							else if (obenBeschraenkt) // nur oben beschraenkt
							{
								beschraenkteReihe.get(laengeDerKette-1)[i][j]++;//merke unten
							}
							else // nicht beschraenkt
							{
								System.out.println("laenge der kette:"+laengeDerKette);
								unbeschraenkteReihe.get(laengeDerKette-1)[i][startDerZaehlung-1]++;// merke anfang
								if(unbeschraenkteReihe.get(laengeDerKette-1)[i][j]!=null)
									unbeschraenkteReihe.get(laengeDerKette-1)[i][j]++; // merke ende
								else
									System.out.println("i:"+i+" j:"+j);
							}
						}	
					}
				}
			}
			*/
			
			for (int x = 0; x < steine.length; x++)
			{
				int kettenLaenge=0;
				for (int y = 0; y < steine.length; y++)
				{
					if(steine[x][y]==spielendeFarbe) // stein gefunden
					{
						if(y+1<dim&&steine[x][y+1]==spielendeFarbe) // darunter frei?
						{
							kettenLaenge++;
							continue;
						}
						// behandlung fuer ketten der laenge 1:
						if(y>0&&steine[x][y-1]<0 && kettenLaenge==1) // darueber frei
						{
							if(y+1<dim&&steine[x][y+1]<0) // darunter frei
							{
								//merke positionen
								unbeschraenkteReihe.get(0)[x][y-1]++;
								unbeschraenkteReihe.get(0)[x][y+1]++;
							}
							else
							{
								beschraenkteReihe.get(0)[x][y-1]++;
							}
						}
						else if (y+1<dim&&steine[x][y+1]<0) // darunter frei
						{
							// merke position
							beschraenkteReihe.get(0)[x][y+1]++;
						}
						// sonst beide seiten nicht frei, also ignorieren
					}
				}
			}
			
		/*
			System.out.println("farbe die dran ist:"+spielendeFarbe);
			
			for (int index=0; index<beschraenkteReihe.size(); index++)
			{
				System.out.println("beschraenkt der laenge:"+(index+1));
				Double[][] momentan = beschraenkteReihe.get(index);
				for (int j = 0; j < momentan.length; j++)
				{
					for (int i = 0; i < momentan[j].length; i++)
					{
						if(momentan[i][j]!=null)
							System.out.print("["+momentan[i][j]+"]");
						else if(steine[i][j]>=0)
						{
							if(steine[i][j]==0)
								System.out.print("[ W ]");
							else if(steine[i][j]==1)
								System.out.print("[ S ]");
						}
						else
							System.out.print("[   ]");
					}
					System.out.println();
				}
				System.out.println();
			}
			
			for (int index=0; index<unbeschraenkteReihe.size(); index++)
			{
				System.out.println("unbeschraenkt der laenge:"+(index+1));
				Double[][] momentan = unbeschraenkteReihe.get(index);
				for (int j = 0; j < momentan.length; j++) {
					for (int i = 0; i < momentan[j].length; i++) {
						if(momentan[i][j]!=null)
							System.out.print("["+momentan[i][j]+"]");
						else if(steine[i][j]>=0)
						{
							if(steine[i][j]==0)
								System.out.print("[ W ]");
							else if(steine[i][j]==1)
								System.out.print("[ S ]");
						}
						else
							System.out.print("[   ]");
					}
					System.out.println();
				}
				System.out.println();
			}
			
		 	*/
						
			// check if there is a move that HAS to be made
			// * 3 with 2 open ends
				// each is a -Infinity
			// * 4 with 1 end
				// that is a -Infinity
			
			// durchlaufe alle positionen?
			// finde vorher auch 1er, 2er etc. variabel abhaengig von der gewinnlaenge
			// finde 3er, merke diese in einermatrix, wo ende offen (markiere dies gleich mit)
			// zusätzliche matrix, wo drin steht wieviele 3er gefunden wurden von der position aus
			// suche nur von oben nach unten/ links nach rechts, um doppelte zaehlung zu vermeiden
			// diagonal?
			// erst anfangen, wo es gefaehrlich wird, dann vom linken rand nach oben rechts suchen, links weiter runter gehen, 
			// dann am unteren rand nach rechts weiter, bis es sicher ist
			// analog dazu die anderen diagonalen absuchen, 
			// also vom oberen rand aus links angefangen, dann nach links unten suchend nach rechts bewegen, dann am rechtsn rand herunter weiter
			// suche jeweils weiter nach 4ern, speichere diese in einer matrix fuer 4er mit geschlossenem ende
			
			
			// zwickmuele
			// * 2 mal 2 mit beiden seiten offen, wo ein ende der jeweiligen an einer gleichen stelle liegt
			// * ein kreuz mit offener mitte, wo 2 dreier des gegners als kreuz auftreten koennen
			// * varianten mit 3 wo ende zu ist und offner 2er
			// * varianten mit 2 geschlossenen 3ern
			// * ofener 2er, dazu ein offener 2er, welcher vom konstruiernden feld 1 frei laesst, 
			//   sodass ein offener 3er entsteht und ein offner 4er wo einer fehlt
				// the constructing pos is a -Infinity
			
			// etc, fuer sich selbst, falls das ein siegender zug ist...
				// +Infinity
			
			// falls nichts davon gefunden wurde, finde alle positionen wo eine reihe n lang des gegners blockiert wird
			// und wieviele m mal das pro position auftritt
			
			// analog fuer sich selbst zum bauen
			
			/*
			System.out.println("beschraenkt 0:");
			printDoubleArray(unbeschraenkteReihe.get(0));
			System.out.println();
			
			System.out.println("pre-messing:");
			printDoubleArray(erg);
			System.out.println();
			
			System.out.println("add-test:");
			Double[][] foo= addDoubleArray(erg, unbeschraenkteReihe.get(0));
			printDoubleArray(foo);
			System.out.println();
			
			
			System.out.println("copy test:");
			Double[][] bar = twoDeepCloneDouble(unbeschraenkteReihe.get(0));
			printDoubleArray(bar);
			System.out.println();
			
			
			System.out.println("mult-test:");
			multDoubleArray(unbeschraenkteReihe.get(0), 42.);
			printDoubleArray(bar);
			System.out.println();
			
			*/
			
			
			Double[] beschraenktFaktor={1., 2., 3., Double.POSITIVE_INFINITY}; //TODO: find better values
			for (int i = 0; i<beschraenkteReihe.size()&&beschraenkteReihe.get(i)!=null; i++)
				multDoubleArray(beschraenkteReihe.get(i), beschraenktFaktor.length>i?beschraenktFaktor[i]:beschraenktFaktor[beschraenktFaktor.length-1]);

			Double[] unbeschraenktFaktor={1., 2., Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY}; //TODO: find better values
			for (int i = 0; i<unbeschraenkteReihe.size()&&unbeschraenkteReihe.get(i)!=null; i++)
				multDoubleArray(unbeschraenkteReihe.get(i), unbeschraenktFaktor.length>i?unbeschraenktFaktor[i]:beschraenktFaktor[beschraenktFaktor.length-1]);
			
			
			for (Double[][] d : beschraenkteReihe)
				erg = addDoubleArray(erg, d);

/*			System.out.println();
			System.out.println("after adding once");
			printDoubleArray(erg);
*/
			for (Double[][] d : unbeschraenkteReihe)
				erg = addDoubleArray(erg, d);
			
/*			System.out.println("beschraenkt(0:)");
			printDoubleArray(beschraenkteReihe.get(0));
			System.out.println();
			System.out.println("beschraenkt(1:)");
			printDoubleArray(beschraenkteReihe.get(1));
			System.out.println();
			System.out.println("beschraenkt(2:)");
			printDoubleArray(beschraenkteReihe.get(2));
			System.out.println();
			System.out.println("beschraenkt(3:)");
			printDoubleArray(beschraenkteReihe.get(3));
			System.out.println();
			
			System.out.println("unbeschraenkt(0:)");
			printDoubleArray(unbeschraenkteReihe.get(0));
			System.out.println();
			System.out.println("unbeschraenkt(1:)");
			printDoubleArray(unbeschraenkteReihe.get(1));
			System.out.println();
			System.out.println("unbeschraenkt(2:)");
			printDoubleArray(unbeschraenkteReihe.get(2));
			System.out.println();
			System.out.println("unbeschraenkt(3:)");
			printDoubleArray(unbeschraenkteReihe.get(3));
			System.out.println();
*/
			

/*			System.out.println();
			System.out.println("after adding second, pre-return");
			printDoubleArray(erg);
			System.out.println();
*/
			return erg;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + dim;
			result = prime * result + moveNr;
			result = prime * result + Arrays.hashCode(nextMove);
			result = prime * result + spielerAnz;
			result = prime * result + Arrays.deepHashCode(steine);
			return result;
		}

		@Override
		public String toString() {
			String erg="Savegame[moveNr=" + moveNr + ", spielerAnz=" + spielerAnz + ", dim=" + dim ;
			if(nextMove != null)
				erg += ", nextMove=[" + nextMove[0] + ", " + nextMove[1] + "]";
			erg +=", steine=" ;
			
			for (int i = 0; i < dim; i++) {
				erg+="\n";
				for (int j = 0; j < dim; j++) {
					if(steine[i][j]<0)
						erg+=".";
					else
						erg += steine[i][j];
				}
			}
			erg+=" ]";
			return erg;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Savegame other = (Savegame) obj;
			if (dim != other.dim)
				return false;
			if (moveNr != other.moveNr)
				return false;
			if (!Arrays.equals(nextMove, other.nextMove))
				return false;
			if (spielerAnz != other.spielerAnz)
				return false;
			if (!Arrays.deepEquals(steine, other.steine))
				return false;
			return true;
		}
	}
	
	/**
	 * Adds two Matrices
	 * @param a and
	 * @param b
	 * @return new object is of greatest size of the two inputs
	 */
	public static Double[][] addDoubleArray(Double[][] a, Double[][] b)
	{
		if(a==null&&b==null)
			return null;
		
		Double[][] erg = new Double[Math.max(a!=null?a.length:0, b!=null?b.length:0)][];
				
		//fill with sub array that are long enough
		for (int i = 0; i < erg.length; i++)
		{
			erg[i]=new Double[Math.max(
					((a.length>i)?a[i].length:0),
					((b.length>i)?b[i].length:0)  )];
						
			//initialise values
			for (int j = 0; j < erg[i].length; j++)
			{
				if(a!=null&&i<a.length)
					erg[i][j] = (a[i]!=null&&j<a[i].length)?a[i][j]:null;
				if(erg[i][j]!=null)
				{
					if(b!=null&&i<b.length)
						erg[i][j] += (b[i]!=null&&j<b[i].length)?b[i][j]:null;
				}
				else if(b!=null&&i<b.length)
					erg[i][j]=(b[i]!=null&&j<b[i].length)?b[i][j]:null;
			}
		}
		return erg;
	}
	
	/**
	 * Multiplies a Matrix with a factor
	 * 
	 * @param a Double[][] to be multiplied with
	 * @param f factor
	 */
	public static void multDoubleArray(Double[][] a, double f)
	{
		if(a==null)
			return;
		
		for (int i = 0; i < a.length; i++)
			for (int j = 0; a[i]!=null && j<a[i].length; j++)
				if(a[i][j]!=null)
					if(a[i][j]==0) // hmm NaN weirdness
						a[i][j] = 0.;
					else
						a[i][j] *= f;
	}

	/**
	 * Not only clones the first layer, but also the second
	 * 
	 * @param a Double[][] to be cloned
	 * @return deep cloned Double[][]
	 */
	public static Double[][] twoDeepCloneDouble(Double[][] a)
	{
		if(a==null)
			return null;
		
		Double[][] erg=a.clone();
		for (int i = 0; i < erg.length; i++)
			erg[i]=a[i].clone();

		return erg;
	}

	/** 
	 * prints the Double[][] matrix
	 * @param a
	 */
	public static void printDoubleArray(Double[][] a)
	{
		for (int i = 0; i < a.length; i++)
		{
			System.out.print("i="+i+": ");
			for (int j = 0; j < a[i].length; j++)
				System.out.print((a[i][j]==null?"nul":a[i][j])+" ");
			System.out.println();
		}
	}
}









