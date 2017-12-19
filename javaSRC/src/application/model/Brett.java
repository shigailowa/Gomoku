package application.model;

import java.time.chrono.MinguoChronology;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Line;

public class Brett {
	
	private int _dim; // Dimension des Spielbretts
	private int _spieler; //anz spieler
	
	//array in dem die SPielsteine liegen
	private SpielStein[][] _brett;
	
	private List<Line> _gitter;
	
	private double _gitterWeite;
	
	/*
	 * Konstruiert ein leeres Brett mit dim Feldern im Quadrat
	 * @param dim 
	 */
	public Brett(int dim, double x, double y)
	{	
		double min=Math.min(x, y);
		double max=Math.max(x, y);
		if(dim <= 0)
			dim=1;
		_dim=dim;
		_spieler=2;
		_brett=new SpielStein[_dim][_dim];
		_gitter=new ArrayList<Line>();
		_gitterWeite=(int)Math.ceil(min/_dim);
		for (int i = 0; i < _dim; i++)
		{
			_gitter.add(new Line(i*_gitterWeite+_gitterWeite/2,                   _gitterWeite/2,
								 i*_gitterWeite+_gitterWeite/2, _gitterWeite*_dim-_gitterWeite/2)); // vertikale linien
			_gitter.add(new Line(               _gitterWeite/2,    i*_gitterWeite+_gitterWeite/2,
							  _gitterWeite*_dim-_gitterWeite/2,    i*_gitterWeite+_gitterWeite/2)); // horizonatale linien
		}
	}
	
	public SpielStein at(int x, int y)
	{
		if(x>=0&&y>=0)//liegt (x, y) auf brett?
			return _brett[x][y];
		return null;
	}
	
	public int getDim() 
	{	return _dim;	}
	
	public List<Line> getGitter()
	{	return _gitter;	}
	
	public double getGitterWeite() 
	{	return _gitterWeite;	}
}
