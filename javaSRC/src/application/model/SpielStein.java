package application.model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class SpielStein
{
	Color _farbe;
	Image _img;
	
	public SpielStein()
	{
		this(1.,0.,0.,.5);
		_img=new Image("resources/stone-white.png");
	}
	
	public SpielStein(double red, double green, double blue, double opacity)
	{
		_farbe=new Color(red, green, blue, opacity);
	}
	
	public SpielStein(Color c)
	{	_farbe=c;
	}
	
	public Image getImage()
	{
		return _img;
	}
}
