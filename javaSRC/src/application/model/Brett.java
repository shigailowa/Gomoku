package application.model;

import java.util.ArrayList;
import java.util.List;

public class Brett {
	
	private int _dim; // Dimension des Spielbretts
	private int _spieler;
	
	private List<SpielStein> _brett;
	
	/*
	 * Konstruiert ein leeres Brett mit dim Feldern im Quadrat
	 * @param dim 
	 */
	public Brett(int dim)
	{	if(dim <= 0)
			dim=1;
		_dim=dim;
		_spieler=2;
		_brett=new ArrayList<SpielStein>(_dim*_dim);
	}
	
	public SpielStein at(int x, int y)
	{	if(x>0&&y>0)
			return _brett.get(x*y);		
		return null;
	}
}
