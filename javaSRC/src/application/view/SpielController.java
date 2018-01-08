package application.view;

import java.util.List;

import com.sun.corba.se.spi.ior.MakeImmutable;
import com.sun.glass.ui.Timer;
import com.sun.org.apache.bcel.internal.classfile.LocalVariableTable;

import application.Main;
import application.model.*;
import application.model.Brett.SpielZug;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SpielController {
	@FXML AnchorPane gameAnchorPane;
	@FXML ImageView backgroundImage;
	@FXML ImageView stoneImage;
	Brett spielbrett;
	
	SpielAI gegner;
	
	SpielStein s;
	double currWidth, currHeight;
	ImageView lastPlayed;
	
	// fires a check of the window size such that the displayed field is always up to date
	AnimationTimer Timer = new AnimationTimer() {		
		@Override
		public void handle(long now) {
			handleSizeChanged();
		}
	};
	
	@FXML
	private void initialize()
	{
		currWidth=gameAnchorPane.getPrefWidth();
		currHeight=gameAnchorPane.getPrefHeight();
		
		backgroundImage.setImage((Image) Main.optionen.getOption("BackgroundImage"));
		backgroundImage.setFitWidth(gameAnchorPane.getPrefWidth());
		backgroundImage.setFitHeight(gameAnchorPane.getPrefHeight());
		backgroundImage.setPreserveRatio(false);
		
		spielbrett=new Brett(19, gameAnchorPane.getPrefWidth(), gameAnchorPane.getPrefHeight());		
		gameAnchorPane.getChildren().addAll(spielbrett.getGitter());
		
		double min=Math.min(currWidth, currHeight);
		double pseudoGitterWeite=min/spielbrett.getDim();
		
		s=new SpielStein(0);
		stoneImage.setImage(s.getImage());
		stoneImage.setFitWidth(pseudoGitterWeite);
		stoneImage.setX(-1000);// out of view
		stoneImage.toFront(); // pack den spielstein vor das gitter
		stoneImage.setOpacity((double) Main.optionen.getOption("nextStoneOpacity"));
				

		Timer.start();
		
		lastPlayed=new ImageView();
		lastPlayed.setImage(new Image("resources/lastPlayedStone.png"));
		lastPlayed.setX(-1000); // out of view
		lastPlayed.setFitWidth(pseudoGitterWeite);
		gameAnchorPane.getChildren().add(lastPlayed);
		
		
		System.out.println("middle move");
		handleMouseClicked((int)currWidth/2, (int)currHeight/2);

		gegner=new SpielAI(spielbrett);
		gegner.updateMoves();

		handleSizeChanged(true); // force a redraw
		
		// let ai make a move
		System.out.println("aimove");
		//if((boolean) Main.optionen.getOption("aiFaengtAn"))
		//{
			Integer[][] zuege=gegner.getBestMoves();
			
			System.out.println("pseudoGitterWeite"+pseudoGitterWeite);
			
			System.out.println("AIMOVES:");
			for (int i = 0; i < zuege.length; i++) {
				for (int j = 0; j < zuege[i].length; j++) {
					System.out.print(i+" "+j+" : "+zuege[i][j]*pseudoGitterWeite+spielbrett.getRandX()+"; ");
				}
				System.out.println();
			}
			System.out.println("END AIMOVES:");
			
			int zugNum=(int)(Math.random()*zuege.length); // of the generated best moves, take one at random
			handleMouseClicked((int)(zuege[zugNum][0]*pseudoGitterWeite+spielbrett.getRandX()),
							   (int)(zuege[zugNum][1]*pseudoGitterWeite+spielbrett.getRandY()));
		//}

		//TODO wtf?
		handleMouseClicked(385, 385);
		handleSizeChanged(true); // force a redraw
	}
	
	@FXML
	private void handleMouseMoved(MouseEvent event)
	{
		// fix pos
		double coord[]= spielbrett.roundCoord(event.getX(), event.getY());

		// update position of next-stone
		stoneImage.setX(coord[0]-spielbrett.getGitterWeite()/2);
		stoneImage.setY(coord[1]-spielbrett.getGitterWeite()/2);
		stoneImage.setFitWidth( spielbrett.getGitterWeite());
		stoneImage.setFitHeight(spielbrett.getGitterWeite());
	}

	@FXML // to emulate a default parameter (of false)
	private void handleSizeChanged()
	{	handleSizeChanged(false);	}
	
	private void handleSizeChanged(boolean forceIt)
	{
		if(forceIt||currWidth!=gameAnchorPane.getWidth()||currHeight!=gameAnchorPane.getHeight())
		{
			currWidth=gameAnchorPane.getWidth();
			currHeight=gameAnchorPane.getHeight();
		}
		else // nothing to fix
			return;
		
		// this only lets the background image expand, never shrink
		if(backgroundImage.getFitHeight()<currHeight)
			backgroundImage.setFitHeight(currHeight);
		if(backgroundImage.getFitWidth()<currWidth)
			backgroundImage.setFitWidth(currWidth);
		
		//TODO: make the background image always stick to the grid when resizing
		backgroundImage.setLayoutX(-(backgroundImage.getFitWidth ()-currWidth )/2);
		backgroundImage.setLayoutY(-(backgroundImage.getFitHeight()-currHeight)/2);
				
		spielbrett.redrawGitter(currWidth==0?gameAnchorPane.getPrefWidth():currWidth, currHeight==0?gameAnchorPane.getPrefHeight():currHeight);
		stoneImage.setFitWidth(spielbrett.getGitterWeite());

		// update lastMove image
		if(spielbrett.getSpielZuege().size()!=0)
		{
			SpielZug lastMove = spielbrett.getSpielZuege().get(spielbrett.getSpielZuege().size()-1);
			lastPlayed.setX(lastMove.x*spielbrett.getGitterWeite()+spielbrett.getRandX()-spielbrett.getGitterWeite()/2);
			lastPlayed.setY(lastMove.y*spielbrett.getGitterWeite()+spielbrett.getRandY()-spielbrett.getGitterWeite()/2);
			lastPlayed.setFitHeight(spielbrett.getGitterWeite());
			lastPlayed.setFitWidth(spielbrett.getGitterWeite());
			lastPlayed.toFront();
		}
	}
	
	@FXML
	void handleDragDetected(MouseEvent event)
	{
		// to make the move count where the mouse ended up at the end of the drag
		handleMouseMoved(event);
	}
	
	// emulate a mouseclick at a given position
	private void handleMouseClicked(int x, int y)
	{
		System.out.println(x+" "+y);
		// emulate mouse click in the middle
		MouseEvent e=new MouseEvent(null, // source - the source of the event. Can be null.
									null, // target - the target of the event. Can be null.
									MouseEvent.MOUSE_CLICKED, //new EventType<MouseEvent>(), // eventType - The type of the event.
									x, // x - The x with respect to the source. Should be in scene coordinates if source == null or source is not a Node.
									y, // y - The y with respect to the source. Should be in scene coordinates if source == null or source is not a Node.
						/* dummy */ -1, // screenX - The x coordinate relative to screen.
									-1, // screenY - The y coordinate relative to screen.
									MouseButton.PRIMARY, // button - the mouse button used
									1, // clickCount - number of click counts
									false, // shiftDown - true if shift modifier was pressed.
									false, // controlDown - true if control modifier was pressed.
									false, // altDown - true if alt modifier was pressed.
									false, // metaDown - true if meta modifier was pressed.
									true, // primaryButtonDown - true if primary button was pressed.
									false, // middleButtonDown - true if middle button was pressed.
									false, // secondaryButtonDown - true if secondary button was pressed.
									true, // synthesized - if this event was synthesized
									false, // popupTrigger - whether this event denotes a popup trigger for current platform
									true, // stillSincePress - see isStillSincePress()
									null // pickResult - pick result. Can be null, in this case a 2D pick result without any further values is constructed based on the scene coordinates and target
								   );
		// let it do its thing
		handleMouseClicked(e);
	}
	
	@FXML
	private void handleMouseClicked(MouseEvent event)
	{
//		System.out.println("Click "+event.getX()+" "+event.getY());

		//fix mouse pos
		double pos[]=spielbrett.roundCoord(event.getX(), event.getY());
		
		// schon belegt?
		if( spielbrett.steinAt(pos[2], pos[3])!=null)
			return;

		SpielZug naechsterZug=new Brett.SpielZug((int)pos[2], (int)pos[3], s, stoneImage);
		if(spielbrett.makeMove(naechsterZug))
		{
			handleSizeChanged(true); // update potentially wrong displayed stones

			if(gegner!=null)
			{
				gegner.updateMoves();
			}
			
			SpielStein sNew=new SpielStein((s.getColor()+1)%spielbrett.getSpieler());
			
			// new wrap for the next stones image
			ImageView iView=new ImageView();
			gameAnchorPane.getChildren().addAll(iView);

			// set its properties as a copy of the last stone
			iView.setImage(sNew.getImage());
			iView.setX(stoneImage.getX());
			iView.setY(stoneImage.getY());
			iView.setFitWidth(stoneImage.getFitWidth());
			iView.setFitHeight(stoneImage.getFitHeight());
			
			stoneImage.setImage(s.getImage());
	
			iView.setOpacity((double) Main.optionen.getOption("nextStoneOpacity"));
			stoneImage.setOpacity(1);
			
			// let old stone stay, "attach" new one to mouse
			s=sNew;
			stoneImage=iView;
			
			stoneImage.toFront();
			
			if(checkIfGewinner())
				System.out.println("es jibt nen Gewinner!"); //TODO: do something, someone has won!
			
			if(gegner!=null)
			{
				Integer[][] zuege=gegner.getBestMoves();
				int zugNum=(int)(Math.random()*zuege.length); // of the generated best moves, take one at random
				naechsterZug=new Brett.SpielZug(zuege[zugNum][0], zuege[zugNum][1], s, stoneImage);
				
				if (spielbrett.makeMove(naechsterZug))
				{
					handleSizeChanged(true); // update potentially wrong displayed stones

					gegner.updateMoves();
					
					sNew=new SpielStein((s.getColor()+1)%spielbrett.getSpieler());
					
					// new wrap for the next stones image
					iView=new ImageView();
					gameAnchorPane.getChildren().addAll(iView);

					// set its properties as a copy of the last stone
					iView.setImage(sNew.getImage());
					iView.setX(stoneImage.getX());
					iView.setY(stoneImage.getY());
					iView.setFitWidth(stoneImage.getFitWidth());
					iView.setFitHeight(stoneImage.getFitHeight());
					
					stoneImage.setImage(s.getImage());
			
					iView.setOpacity((double) Main.optionen.getOption("nextStoneOpacity"));
					stoneImage.setOpacity(1);
					
					// let old stone stay, "attach" new one to mouse
					s=sNew;
					stoneImage=iView;
					
					stoneImage.toFront();
					
					if(checkIfGewinner())
						System.out.println("es jibt nen Gewinner!"); // TODO: do something, someone has won!
				}
					
			}
		}
		stoneImage.setX(-1000); // move the "next" stone out of view
	}
	
	private boolean checkIfGewinner()
	{
		SpielZug letzterZug = spielbrett.getSpielZuege().get(spielbrett.getSpielZuege().size()-1);
		
		boolean erg=false;
		for (int richtungX = -1; richtungX < 2 && erg==false; richtungX++)
		{
			for (int richtungY = -1; richtungY < 2 && erg==false; richtungY++)
			{
				if(richtungX==0&&richtungY==0) // mitte nicht abpruefen
					continue;
				
				for (int tiefe = 1; tiefe < (int)Main.optionen.getOption("inEinerReihe") && erg==false; tiefe++)
				{
					// ist an der zu pruefenden stelle ein stein? (ist zu pruefende pos auf dem brett?)
					SpielStein pruefStein = spielbrett.steinAt(letzterZug.x + richtungX*tiefe,
															   letzterZug.y + richtungY*tiefe);
					if(pruefStein==null)
						break;
					
					// hat dieser die richtige farbe?
					if(pruefStein.getColor()!=letzterZug.stein.getColor())
						break;
					
					//merke dass es einen gewinner gibt
					if(tiefe==(int)Main.optionen.getOption("inEinerReihe")-1)
						erg=true;
				}
			}
		}
		return erg;
	}
	
	@FXML
	private void handleKeyPressed(KeyEvent event)
	{
//		System.out.println("handleKeyPressed: "+event.getCode()+" "+event.toString());
	}
	
	@FXML
	private void handleKeyReleased(KeyEvent event)
	{
//		System.out.println("handleKeyReleased: "+event.getCode()+" "+event.toString());
	}
}

















