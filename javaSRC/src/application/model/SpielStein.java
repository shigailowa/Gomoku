package application.model;

import javafx.scene.paint.Color;

public class SpielStein
{
	Color _farbe;
	
	public SpielStein()
	{	this(1.,0.,0.,.5);
	}
	
	public SpielStein(double red, double green, double blue, double opacity)
	{	_farbe=new Color(red, green, blue, opacity);
	}
	
	public SpielStein(Color c)
	{	_farbe=c;
	}
}
