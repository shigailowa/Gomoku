package application.model;

import javafx.scene.image.Image;

public class SpielStein
{
	int _farbe;
	Image _img;
	
	static Image blackStone=new Image("resources/stone-black.png");
	static Image whiteStone=new Image("resources/stone-white.png");
	
	public SpielStein(int farbe)
	{
		_farbe=farbe;
		switch (farbe) {
		case 0:
			_img=whiteStone;
			break;
		
		case 1:
			_img=blackStone;
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
