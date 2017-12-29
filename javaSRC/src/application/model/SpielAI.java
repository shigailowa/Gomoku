package application.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class SpielAI {
	private Brett _brett;
	private Set<Savegame> _possibleMoves;
	
	public SpielAI(Brett brett) {
		_brett=brett;
		_possibleMoves=new LinkedHashSet<Savegame>();
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
			steine[s.nextMove[1]][s.nextMove[2]]=moveNr%spielerAnz;//make last to-make move count
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
