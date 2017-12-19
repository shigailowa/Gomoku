package application.model;

import java.time.chrono.MinguoChronology;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import sun.text.resources.ro.CollationData_ro;

public class Brett {
	
	private int _dim; // Dimension des Spielbretts
	private int _spieler; //anz spieler
	
	//array in dem die SPielsteine liegen
	private SpielStein[][] _brett;
	
	private List<Line> _gitterVert;
	private List<Line> _gitterHorz;
	private List<Line> _gitter;
	
	private double _gitterWeite;
	private double _randX;
	private double _randY;
	
	/*
	 * Konstruiert ein leeres Brett mit dim Feldern im Quadrat
	 * @param dim 
	 */
	public Brett(int dim, double x, double y)
	{		
		if(dim <= 0)
			dim=1;
		_dim=dim;
		_spieler=2;
		_brett=new SpielStein[_dim][_dim];
		_gitterVert=new ArrayList<Line>();
		_gitterHorz=new ArrayList<Line>();
		_gitter=new ArrayList<Line>();
		
		for (int i = 0; i < _dim; i++)
		{
			_gitterVert.add(new Line()); // vertikale linien
			_gitterHorz.add(new Line()); // horizonatale linien
		}
		
		redrawGitter(x, y);
		
		_gitter.addAll(_gitterVert);
		_gitter.addAll(_gitterHorz);
		
		_gitter.forEach(l->{
			l.setStrokeWidth(_gitterWeite/20);
			l.setFill(Color.LIGHTGREY);
			l.setStroke(Color.LIGHTGRAY);
		});
	}
	
	public void redrawGitter(double x, double y)
	{
		double min=Math.min(x, y);
		_randX=0;
		_randY=0;
		
		_gitterWeite=(int)Math.ceil(min/_dim);
		
		if(x>y)
			_randX=(Math.max(x, y)-min)/2;
		else
			_randY=(Math.max(x, y)-min)/2;
		
		for (int i = 0; i < _dim; i++)
		{
			_gitterVert.get(i).setStartX(_randX+    i*_gitterWeite+_gitterWeite/2);
			_gitterVert.get(i).setEndX(  _randX+    i*_gitterWeite+_gitterWeite/2);
			_gitterVert.get(i).setStartY(_randY+                   _gitterWeite/2);
			_gitterVert.get(i).setEndY(  _randY+ _gitterWeite*_dim-_gitterWeite/2);
								     
			_gitterHorz.get(i).setStartX(_randX+                   _gitterWeite/2);					     
			_gitterHorz.get(i).setEndX(  _randX+ _gitterWeite*_dim-_gitterWeite/2);					     
			_gitterHorz.get(i).setStartY(_randY+    i*_gitterWeite+_gitterWeite/2);					     
			_gitterHorz.get(i).setEndY(  _randY+    i*_gitterWeite+_gitterWeite/2);					     
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
	
	public double getRandX()
	{	return _randX;	}

	public double getRandY()
	{	return _randY;	}
}
