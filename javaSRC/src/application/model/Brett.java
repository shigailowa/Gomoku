package application.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.glass.ui.Pixels.Format;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.RETURN;

import application.Main;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Brett {
	
	private int _dim; // Dimension des Spielbretts
	private int _spieler; //anz spieler
	
	//array in dem die Spielsteine liegen
	private SpielStein[][] _brett;
	
	private List<Line> _gitterVert;
	private List<Line> _gitterHorz;
	private List<Line> _gitter;
	private List<Line> _punkte;
	
	private List<SpielZug> _SpielZuege;
	
	private double _gitterWeite;
	private double _randX;
	private double _randY;
	
	// wether a next move has to be adjacent diagonally or horizontally or vertically to any previous move
	private boolean _CheckAdjacent;
	/*
	 * Konstruiert ein leeres Brett mit dim Feldern im Quadrat
	 * @param dim Dimension des Brettes
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
		
		_SpielZuege=new ArrayList<SpielZug>();
		_CheckAdjacent=true;
		
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
		_gitterWeite=min/_dim;

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
		
		// move alrady played stones
		_SpielZuege.forEach(s->{
			s.iView.setX(_randX + s.x*_gitterWeite-_gitterWeite/2);
			s.iView.setY(_randY + s.y*_gitterWeite-_gitterWeite/2);
			s.iView.setFitWidth(_gitterWeite);
			s.iView.setFitHeight(_gitterWeite);
		});

		
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
if(Main.DEBUG)System.out.println("Brett::roundCoord:: \n"+x+" "+y+"  inc");
		
		x=Math.max(_randX, x);
		y=Math.max(_randY, y);

if(Main.DEBUG)System.out.println("Brett::roundCoord:: "+x+" "+y+"  max");
		
		x=Math.min(x, _randX + (_dim-1)*_gitterWeite);
		y=Math.min(y, _randY + (_dim-1)*_gitterWeite);

if(Main.DEBUG)System.out.println("Brett::roundCoord:: "+x+" "+y+"  min");

		x+=_gitterWeite/2;
		y+=_gitterWeite/2;

if(Main.DEBUG)System.out.println("Brett::roundCoord:: "+x+" "+y+"  +gi  "+_gitterWeite/2);
		
		x-=_randX;
		y-=_randY;
		
if(Main.DEBUG)System.out.println("Brett::roundCoord:: "+x+" "+y+"  -ra  "+_randX+" "+_randY);
		
		x-=x%_gitterWeite;
		y-=y%_gitterWeite;

if(Main.DEBUG)System.out.println("Brett::roundCoord:: "+x+" "+y+"  -mo  "+_gitterWeite);

		x+=_randX;
		y+=_randY;
		
if(Main.DEBUG)System.out.println("Brett::roundCoord:: "+x+" "+y+"  fin");

		return new double[]{x, y, Math.round((x-_randX)/_gitterWeite), Math.round((y-_randY)/_gitterWeite)};
		//first pair is in display coordinates, second pair is borad indices
	}
	
	public SpielStein steinAt(int x, int y)
	{
if(Main.DEBUG) System.out.println("Brett::steinAt:: "+x+" "+y);
		if(x>=0&&x<_dim  &&  y>=0&&y<_dim) // liegt (x, y) auf brett?
			return _brett[x][y];
		return null;
	}
	
	// allow implicit cast from double to int
	public SpielStein steinAt(double x, double y)
	{	return steinAt((int)x, (int)y);	}
	
	private boolean steinSet(int x, int y, SpielStein s)
	{
		if(x>=0&&x<_dim && y>=0&&y<_dim  &&  _brett[x][y] == null)
		{
			_brett[x][y]=s;
			return true;
		}
		return false;
	}
		
	public boolean makeMove(SpielZug zug)
	{
		/*
		//debug ausgabe der gespeicherten brett-matrix
		for (int y = 0; y < _dim; y++) {
			for (int x = 0; x < _dim; x++)
				if(x==zug.x&&y==zug.y)
					System.out.print("X");
				else if(_brett[x][y]==null)
					System.out.print(".");
				else
					System.out.print(_brett[x][y]._farbe+"");
			System.out.println();
		}System.out.println(zug.x+" "+zug.y);
		*/

		
		if(_CheckAdjacent && _SpielZuege.size()>0 && zug.x>0 && zug.x<_dim && zug.y>0 && zug.y<_dim &&
			_brett[zug.x-1][zug.y-1]==null&&
			_brett[zug.x-1][zug.y  ]==null&&
			_brett[zug.x-1][zug.y+1]==null&&
			_brett[zug.x  ][zug.y-1]==null&&
			_brett[zug.x  ][zug.y+1]==null&&
			_brett[zug.x+1][zug.y-1]==null&&
			_brett[zug.x+1][zug.y  ]==null&&
			_brett[zug.x+1][zug.y+1]==null
			)
			return false;
		
		boolean b=steinSet(zug.x, zug.y, zug.stein);
		if(b)
			_SpielZuege.add(zug);
		return b;
	}
	
	// to wrap the data of an occured move
	public static class SpielZug{
		public int x, y;
		public SpielStein stein;
		public ImageView iView;
		
		public SpielZug(int x, int y, SpielStein stein, ImageView iView) {
			this.x=x;
			this.y=y;
			this.stein=stein;
			this.iView=iView;
		}
		
		
		public String toString() {
			return String.format("x:%2d ", x)+String.format("y:%2d", y)+" f:"+stein._farbe;
		}
	}
	
	public void printMoves()
	{
		for (int i = 0; i < _SpielZuege.size(); i++)
			System.out.println(String.format("Zug%3d: ", i)+_SpielZuege.get(i));
		System.out.println();
	}

	public final List<SpielZug> getSpielZuege()
	{	return _SpielZuege;	}
	
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
	
	public final int getSpieler()
	{	return _spieler;	}
	
	public final SpielStein[][] getBrett()
	{	return _brett;	}

}
