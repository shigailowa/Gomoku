package application.model;

import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.LongToDoubleFunction;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import application.model.Brett.SpielZug;
import application.model.SpielAI.Savegame;

public class SpielAI {
	private Brett _brett;
	private ArrayList<LinkedHashSet<Savegame>> _possibleMoves;
	
	
	public SpielAI(Brett brett) {
		_brett=brett;
		_possibleMoves=new ArrayList<LinkedHashSet<Savegame>>();
		System.out.println("_pm:"+_possibleMoves);
		System.out.println("pm.size:"+_possibleMoves.size());
		if(brett.getSpielZuege().size() > 0)
		{
			Savegame wurzel = new Savegame(brett);
			LinkedHashSet<Savegame>menge =new LinkedHashSet<Savegame>();
			menge.add(wurzel);
			System.out.println("moveNr:"+wurzel.moveNr);
			_possibleMoves.add(wurzel.moveNr-1, menge);//TODO: ? -1
		}
		else
			System.out.println("das brett hat noch keine steine!");
	}
	
	public void generateNextMoves()
	{
//		System.out.println("_pm.gen{:"+_possibleMoves);

		int tiefe=_possibleMoves.size()-1;
//		System.out.println("generateNextMoves::tiefe:"+tiefe);
		for (Iterator<Savegame> it = (_possibleMoves.get(tiefe)).iterator(); it.hasNext();) // for each leaf
		{
			Savegame leaf = (Savegame) it.next(); // current leaf
//			LinkedHashSet<Savegame> newLeaves=new LinkedHashSet<Savegame>(); // leaves of the current leaf
//			newLeaves.addAll(leaf.generateNextMoves()); // generate and add new leaves to local var
//			_possibleMoves.get(tiefe).addAll(newLeaves); // add generated leaves to possibleMoves
			
			Double[][] h=leaf.generateHeuristic();
			System.out.println("Heuristic: ");
			for (int j = 0; j < leaf.dim; j++)
			{
				for (int i = 0; i < leaf.dim; i++)
					System.out.print(h[i][j]==null?".":String.format("%1.0f", h[i][j]));
				System.out.println();
			}
			System.out.println();
		
		}
		
//		System.out.println("_pm.gen}:"+_possibleMoves);
	}
	
	public void updateMoves()
	{
//		System.out.println("SpielAi::makeMove brett.zuege.size:"+_brett.getSpielZuege().size());
//		System.out.println("SpielAi::makeMove _possibleMo.size:"+_possibleMoves.size());
		
		for (LinkedHashSet<Savegame> moeglicherZug : _possibleMoves) {
			if (moeglicherZug.size()>1)
			{
				System.out.println("move:"+_brett.getSpielZuege().get(moeglicherZug.iterator().next().moveNr));
			}
		}

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
				System.out.println(zug);
			}
		}
	}
	
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

		@Deprecated // da erstmal nur die heuristik via matrix benutzt werden soll
		public LinkedHashSet<Savegame> generateNextMoves()
		{
			System.out.println("Savegame::generateNextMoves");

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

		public Double[][] generateHeuristic()
		{
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
			
			// delete positions where there is already a stone placed
			for (int i = 0; i < erg.length; i++)
				for (int j = 0; j < erg.length; j++)
					if(steine[i][j]>=0)
						erg[i][j]=null;
			
			//TODO: bewertung
			
			
			return erg;
		}
		
		/*
		public Savegame(Savegame s, int xNew, int yNew)
		{
			// remember move to make next
			nextMove=new int[2];
			nextMove[0]=xNew;
			nextMove[1]=yNew;
			
			spielerAnz=s.spielerAnz;
			moveNr=s.moveNr+1;
			steine=new int[dim][dim];
			dim=s.dim;
			System.arraycopy(s.steine, 0, steine, 0, dim);

			//make last to-make move count
			steine[s.nextMove[1]][s.nextMove[2]]=moveNr%spielerAnz;
		}
		*/

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
}









