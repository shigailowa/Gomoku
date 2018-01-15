package application.view;

import application.Main;
import application.model.*;
import application.model.Brett.SpielZug;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	
	AnimationTimer zweiAiTimer;
	
	SpielAI gegner;

	SpielStein s;
	double currWidth, currHeight;
	ImageView lastPlayed;
	
	@FXML
	private void initialize()
	{
		currWidth=gameAnchorPane.getPrefWidth();
		currHeight=gameAnchorPane.getPrefHeight();
		
		backgroundImage.setImage((Image) Main.optionen.getOption("BackgroundImage"));
		backgroundImage.setFitWidth(gameAnchorPane.getPrefWidth());
		backgroundImage.setFitHeight(gameAnchorPane.getPrefHeight());
		backgroundImage.setPreserveRatio(false);
		
		spielbrett=new Brett((int)Main.optionen.getOption("brettgroesse"), gameAnchorPane.getPrefWidth(), gameAnchorPane.getPrefHeight());		
		gameAnchorPane.getChildren().addAll(spielbrett.getGitter());
		
		double min=Math.min(currWidth, currHeight);
		double pseudoGitterWeite=min/spielbrett.getDim(); // gebraucht, da zurzeit noch nicht verfuegbar in class Brett
		
		s=new SpielStein((int)Main.optionen.getOption("anfangsFarbe")); // da bei erneutem spiel vllt. anders
		stoneImage.setImage(s.getImage());
		stoneImage.setFitWidth(pseudoGitterWeite);
		stoneImage.setX(-1000); // out of view
		stoneImage.toFront(); // pack den spielstein vor das gitter
		stoneImage.setOpacity((double) Main.optionen.getOption("nextStoneOpacity"));
		
		lastPlayed=new ImageView();
		lastPlayed.setImage(new Image("resources/lastPlayedStone.png"));
		lastPlayed.setX(-1000); // out of view
		lastPlayed.setFitWidth(pseudoGitterWeite);
		gameAnchorPane.getChildren().add(lastPlayed);
		
		if((boolean)Main.optionen.getOption("anfangInMitte"))
			// erster zug in der Mitte
			handleMouseClicked((int)currWidth/2, (int)currHeight/2);

		
		if((int) Main.optionen.getOption("anzahlAi")==2)
		{
			Main.optionen.setOption("aiFaengtAn", false);
			zweiAiTimer=new AnimationTimer()
			{
				@Override
				public void handle(long time)
				{
					stoneImage.setX(-1000); // move out of view
					if(time%(int)Main.optionen.getOption("twoAiSpeed")==0)
						letAImakeMove();
				}
			};
			zweiAiTimer.start();
		}
		
		if((int)Main.optionen.getOption("anzahlAi")>0 ) // es gibt nur einen ai Gegner
		{
			gegner=new SpielAI(spielbrett);
			gegner.updateMoves();

			// let ai make a move
			System.out.println("aimove");
			Main.optionen.printOption("aiFaengtAn");
			if(!(boolean) Main.optionen.getOption("aiFaengtAn"))
			{
				System.out.println("anzZuege:"+spielbrett.getSpielZuege().size());
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
			}
		}
		
		gameAnchorPane.widthProperty().addListener(new ChangeListener<Number>()
		{	@Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{	handleSizeChanged();	}	});
		
		gameAnchorPane.heightProperty().addListener(new ChangeListener<Number>()
		{	@Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{	handleSizeChanged();	}	});
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
		
		//TODO: make the background image always stick nicely to the grid when resizing
		backgroundImage.setLayoutX(-(backgroundImage.getFitWidth ()-currWidth )/2);
		backgroundImage.setLayoutY(-(backgroundImage.getFitHeight()-currHeight)/2);
				
		spielbrett.redrawGitter(currWidth==0?gameAnchorPane.getPrefWidth():currWidth, currHeight==0?gameAnchorPane.getPrefHeight():currHeight);
		stoneImage.setFitWidth(spielbrett.getGitterWeite());
		
		// update lastMove image
		updateLastPlayedPos();		
		stoneImage.setX(-1000); // move out of view until next mouse movement
	}
	
	// only update the relative position of the circle on the screen
	private void updateLastPlayedPos()
	{
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
	
	// emulates a mouseclick at a given position
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

		if(event.isSynthesized()==false && (int)Main.optionen.getOption("anzahlAi")==2)
			return; // dont let the click by a player count if only ai are playing
		
		//fix mouse pos
		double pos[]=spielbrett.roundCoord(event.getX(), event.getY());
		
		// schon belegt?
		if( spielbrett.steinAt(pos[2], pos[3])!=null)
			return;

		SpielZug naechsterZug=new Brett.SpielZug((int)pos[2], (int)pos[3], s, stoneImage);
		if(spielbrett.makeMove(naechsterZug))
		{			
			if(gegner!=null)
				gegner.updateMoves();
			
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
			
			// update potentially wrong displayed stones, caused by potential dragging of the mouse
			handleSizeChanged(true);

			if(handleGewinner())
				return;
			if(		(   spielbrett.getSpielZuege().size()>2 
					  ||(boolean)Main.optionen.getOption("aiFaengtAn") )
					&&(int)Main.optionen.getOption("anzahlAi")==1 ) // sonst keine oder zwei ai-spieler
				letAImakeMove();
		}
	}
	
	public void letAImakeMove()
	{
		System.out.println("letAImakeMove");
		if(gegner!=null)
		{
			Integer[][] zuege=gegner.getBestMoves();
			int zugNum=(int)(Math.random()*zuege.length); // of the generated best moves, take one at random
			if(zuege.length==0)
			{
				// handle Unentschieden
				System.out.println("brett voll!");
				return;
			}
			else if (zuege.length==1)
			{
				zugNum=0;
				System.out.println("nur einer frei!");
			}
			
			SpielZug naechsterZug = new Brett.SpielZug(zuege[zugNum][0], zuege[zugNum][1], s, stoneImage);
			
			if (spielbrett.makeMove(naechsterZug))
			{
				gegner.updateMoves();
				
				SpielStein sNew=new SpielStein((s.getColor()+1)%spielbrett.getSpieler());
				
				// new wrap for the next stones image
				ImageView iView = new ImageView();
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
				
				// update potentially wrong displayed stones, caused by potential dragging of the mouse
				handleSizeChanged(true);

				if(handleGewinner())
					return;
			}
		}
	}
	
	// wird gerufen um gewinnerbehandlung zu starten
	private boolean handleGewinner()
	{
		boolean erg=checkIfGewinner();
		if(erg)
			System.out.println("es jibt nen Gewinner!"); // TODO: do something, someone has won!
		return erg;
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

















