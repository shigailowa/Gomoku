package application.view;

import java.util.List;

import com.sun.glass.ui.Timer;

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
		
		backgroundImage.setImage(new Image("resources/Ahorn_Holz.JPG")); //TODO aus options
		backgroundImage.setFitWidth(gameAnchorPane.getPrefWidth());
		backgroundImage.setFitHeight(gameAnchorPane.getPrefHeight());
		backgroundImage.setPreserveRatio(false);
		
		spielbrett=new Brett(19, gameAnchorPane.getPrefWidth(), gameAnchorPane.getPrefHeight());		
		gameAnchorPane.getChildren().addAll(spielbrett.getGitter());
		
		s=new SpielStein(0);
		stoneImage.setImage(s.getImage());
		stoneImage.setFitWidth(spielbrett.getGitterWeite());
		stoneImage.setX(-1000);// out of view
		stoneImage.toFront(); // pack den spielstein vor das gitter
		stoneImage.setOpacity(.7); //TODO aus options
				
		// emulate mouse click in the middle
		MouseEvent e=new MouseEvent(null, // source - the source of the event. Can be null.
									null, // target - the target of the event. Can be null.
									MouseEvent.MOUSE_CLICKED, //new EventType<MouseEvent>(), // eventType - The type of the event.
									(int)currWidth/2, // x - The x with respect to the source. Should be in scene coordinates if source == null or source is not a Node.
									(int)currHeight/2, // y - The y with respect to the source. Should be in scene coordinates if source == null or source is not a Node.
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
		Timer.start();
		
		lastPlayed=new ImageView();
		lastPlayed.setImage(new Image("resources/lastPlayedStone.png"));
		lastPlayed.setX(-1000); // out of view
		lastPlayed.setFitWidth(spielbrett.getGitterWeite());
		gameAnchorPane.getChildren().add(lastPlayed);
		
		handleMouseClicked(e);
		handleSizeChanged(true); // force the redraw

		gegner=new SpielAI(spielbrett);

		//TODO clean up ai or player beginning
		boolean aiFaengtAn=false;//TODO:aus einstellungen
		if(!aiFaengtAn)
		{
			gegner.generateNextMoves();
			//gegner.getMove();
		}
	}
	
	@FXML
	private void handleMouseMoved(MouseEvent event)
	{
		// fix pos
		double coord[]= spielbrett.roundCoord(event.getX(), event.getY());

		// update position of next-stone
		stoneImage.setX(coord[0]-spielbrett.getGitterWeite()/2);
		stoneImage.setY(coord[1]-spielbrett.getGitterWeite()/2);
		stoneImage.setFitWidth(spielbrett.getGitterWeite());
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
				
		spielbrett.redrawGitter(currWidth,currHeight);
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
	
	@FXML
	private void handleMouseClicked(MouseEvent event)
	{
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
	
			iView.setOpacity(.7); //TODO aus options
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
			
					iView.setOpacity(.7); //TODO aus options
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
				
				for (int tiefe = 1; tiefe < 5 && erg==false; tiefe++) // TODO: 5 aus den optionen holen
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
					if(tiefe==5-1) //TODO aus optionen
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

















