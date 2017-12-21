package application.model;

import java.time.chrono.MinguoChronology;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import sun.text.resources.ro.CollationData_ro;

public class Brett {
	
	private int _dim; // Dimension des Spielbretts
	private int _spieler; //anz spieler
	
	//array in dem die SPielsteine liegen
	private SpielStein[][] _brett;
	
	private List<Line> _gitterVert;
	private List<Line> _gitterHorz;
	private List<Line> _gitter;
	private List<Line> _punkte;
	
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
		_punkte=new ArrayList<Line>();
		
		for (int i = 0; i < _dim; i++)
		{
			_gitterVert.add(new Line()); // vertikale linien
			_gitterHorz.add(new Line()); // horizonatale linien
		}

		if(_dim%2!=0)
			_punkte.add(new Line(x/2, y/2, x/2, y/2));
		
		redrawGitter(x, y);
		
		_punkte.forEach(l->l.setStrokeWidth(_gitterWeite/10));
		
		_gitter.addAll(_gitterVert);
		_gitter.addAll(_gitterHorz);
		_gitter.addAll(_punkte);
		
		_gitter.forEach(l->{
			l.setFill(Color.WHITE);
			l.setStroke(Color.LIGHTGRAY);
			l.setStrokeLineCap(StrokeLineCap.ROUND);
		});
	}
	
	public void redrawGitter(double x, double y)
	{		
		double min=Math.min(x, y);
		_gitterWeite=(int)Math.ceil(min/_dim);

		//ah, ffs das gitter stimmt auch irgendwie manchmal nicht so ganz
		_punkte.forEach(l->l.relocate(x/2, y/2));
		_punkte.forEach(l->l.setStrokeWidth(_gitterWeite/2));

		_randX=_gitterWeite/2;
		_randY=_gitterWeite/2;
		
		if(x>y)
			_randX+=(Math.max(x, y)-min)/2;
		else
			_randY+=(Math.max(x, y)-min)/2;

		for (int i = 0; i < _dim; i++)
		{
			_gitterVert.get(i).setStartX(_randX+        i*_gitterWeite);
			_gitterVert.get(i).setStartY(_randY                       );
			_gitterVert.get(i).setEndX(  _randX+        i*_gitterWeite);
			_gitterVert.get(i).setEndY(  _randY+ _gitterWeite*(_dim-1));
								     
			_gitterHorz.get(i).setStartX(_randX                       );
			_gitterHorz.get(i).setStartY(_randY+        i*_gitterWeite);
			_gitterHorz.get(i).setEndX(  _randX+ _gitterWeite*(_dim-1));
			_gitterHorz.get(i).setEndY(  _randY+        i*_gitterWeite);
		}
		
		// neue strich-dicke
		_gitterHorz.forEach(l->l.setStrokeWidth(_gitterWeite/20));
		_gitterVert.forEach(l->l.setStrokeWidth(_gitterWeite/20));
		
		//umrandung dicker
		_gitterVert.get(0).setStrokeWidth(_gitterWeite/10);
		_gitterVert.get(_dim-1).setStrokeWidth(_gitterWeite/10);
		_gitterHorz.get(0).setStrokeWidth(_gitterWeite/10);
		_gitterHorz.get(_dim-1).setStrokeWidth(_gitterWeite/10);
	}
	
	public double[] roundCoord(double x, double y)
	{
		boolean debug=false;
if(debug)System.out.println("\n"+x+" "+y+"  inc");
		
		x=Math.max(_randX, x);
		y=Math.max(_randY, y);

if(debug)System.out.println(x+" "+y+"  max");
		
		x=Math.min(x, _randX + (_dim-1)*_gitterWeite);
		y=Math.min(y, _randY + (_dim-1)*_gitterWeite);

if(debug)System.out.println(x+" "+y+"  min");

		x+=_gitterWeite/2;
		y+=_gitterWeite/2;

if(debug)System.out.println(x+" "+y+"  +gi  "+_gitterWeite/2);
		
		x-=_randX;
		y-=_randY;
		
if(debug)System.out.println(x+" "+y+"  -ra  "+_randX+" "+_randY);
		
		x-=x%_gitterWeite;
		y-=y%_gitterWeite;

if(debug)System.out.println(x+" "+y+"  -mo  "+_gitterWeite);

		x+=_randX;
		y+=_randY;
		
if(debug)System.out.println(x+" "+y+"  fin");
		
//		event.getX()-event.getX()%spielbrett.getGitterWeite()+spielbrett.getGitterWeite()/2+spielbrett.getRandX()%spielbrett.getGitterWeite()
		return new double[]{x, y};
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
