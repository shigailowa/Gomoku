package application.model;

import javafx.scene.image.Image;

public class SpielStein
{
	int _farbe;
	Image _img;
	
	/*
	public SpielStein()
	{
		this(1.,0.,0.,.5);
		_img=new Image("resources/stone-white.png");
	}*/
	
	/*
	public SpielStein(double red, double green, double blue, double opacity)
	{
		_farbe=new Color(red, green, blue, opacity);
		_img=new Image("resources/stone-white.png");
	}
	*/
	
	public SpielStein(int farbe)
	{
		_farbe=farbe;
		switch (farbe) {
		case 0:
			_img=new Image("resources/stone-white.png");
			break;
		case 1:
			_img=new Image("resources/stone-black.png");
			break;
		default:
			System.out.println("SpeielStein::constructor:: mit farbe'"+farbe+"' kann ich nichts anfangen -> kein bild gesetzt");
			break;
		}
	}
	
	public final int getColor()
	{
		return _farbe;
	}
	
	public final Image getImage()
	{
		return _img;
	}
}
