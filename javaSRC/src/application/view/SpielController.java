package application.view;

import com.sun.glass.ui.Timer;

import application.model.*;
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
	
	// fires a check of the window size such that the displayed field is always up to date
	AnimationTimer Timer = new AnimationTimer() {		
		@Override
		public void handle(long now) {
			handleSizeChanged();
//			System.out.println("timer");
		}
	};
	
	@FXML
	private void initialize()
	{
		currWidth=gameAnchorPane.getPrefWidth();
		currHeight=gameAnchorPane.getPrefHeight();
		
		backgroundImage.setImage(new Image("resources/Ahorn_Holz.JPG"));
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
		stoneImage.setOpacity(.8);
				
		// emulate mouse click in the middle
		MouseEvent e=new MouseEvent(null, // source - the source of the event. Can be null.
									null, // target - the target of the event. Can be null.
									MouseEvent.MOUSE_CLICKED, //new EventType<MouseEvent>(), // eventType - The type of the event.
									(int)currWidth/2, // x - The x with respect to the source. Should be in scene coordinates if source == null or source is not a Node.
									(int)currHeight/2, // y - The y with respect to the source. Should be in scene coordinates if source == null or source is not a Node.
					/*dummy value*/ -1, // screenX - The x coordinate relative to screen.
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
		handleMouseClicked(e);

		gegner=new SpielAI(spielbrett);

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
		if(gameAnchorPane.getWidth()!=currWidth||gameAnchorPane.getHeight()!=currHeight)
			handleSizeChanged(); // unschoen, aber geht erstmal nur so
		
		// fix pos
		double coord[]= spielbrett.roundCoord(event.getX(), event.getY());

		// set new position of next stone
		stoneImage.setX(coord[0]-spielbrett.getGitterWeite()/2);
		stoneImage.setY(coord[1]-spielbrett.getGitterWeite()/2);
		stoneImage.setFitWidth(spielbrett.getGitterWeite());
		stoneImage.setFitHeight(spielbrett.getGitterWeite());
	}
	
	@FXML
	private void handleSizeChanged()
	{
		currWidth=gameAnchorPane.getWidth();
		currHeight=gameAnchorPane.getHeight();
		
		// this only lets the background image expand, never shrink
		if(backgroundImage.getFitHeight()<currHeight)
			backgroundImage.setFitHeight(currHeight);
		if(backgroundImage.getFitWidth()<currWidth)
			backgroundImage.setFitWidth(currWidth);
				
		spielbrett.redrawGitter(currWidth,currHeight);
		stoneImage.setFitWidth(spielbrett.getGitterWeite());
	}
	
	@FXML
	void handleDragDetected(MouseEvent event) {
		System.out.println("handleDragDetected");
		handleSizeChanged();
		handleMouseMoved(event); // to make the move count where the mouse ended up at the end of the drag
//		spielbrett.redrawGitter(currWidth,currHeight); // to move the pieces to the correct position
//		stoneImage.setFitWidth(spielbrett.getGitterWeite());
		handleSizeChanged();
	}
	
	@FXML
	private void handleMouseClicked(MouseEvent event)
	{
		System.out.println("handleMouseClicked");
		
		//fix mouse pos
		double pos[]=spielbrett.roundCoord(event.getX(), event.getY());
		
		// schon belegt?
		if( spielbrett.steinAt(pos[2], pos[3])!=null)
			return;

		if(spielbrett.makeMove(new Brett.SpielZug((int)pos[2], (int)pos[3], s, stoneImage)))
		{
//			gegner.generateNextMoves();
			
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
	
			iView.setOpacity(.8);
			stoneImage.setOpacity(1);
			
			// let old stone stay, "attach" new one to mouse
			s=sNew;
			stoneImage=iView;
			
			stoneImage.toFront();
		}				
		//spielbrett.printMoves();
	}
	
	@FXML //ka, springt nich an, lass ich hier aber erstmal liegen...
	//ich wollt damit nen debugmodus aktivieren...
	private void handleKeyTyped(KeyEvent event)
	{
		System.out.println("handleKeyTyped");
		System.out.println(event.toString());
	}
}

















