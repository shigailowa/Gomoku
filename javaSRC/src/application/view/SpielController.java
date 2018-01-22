package application.view;

import java.util.ArrayList;
import java.util.List;

import application.Main;
import application.model.Brett;
import application.model.Brett.SpielZug;
import application.model.SpielAI;
import application.model.SpielStein;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SpielController {
	
	@FXML private CheckBox mitteBeginnCheckBox;
	@FXML private ImageView backgroundImage;
	@FXML private Tab ueberTab;
	@FXML private CheckBox anlegenCheckBox;
	@FXML private Label brettGroesseLabel;
	@FXML private AnchorPane einstellungenAnchorPane;
	@FXML private AnchorPane gameAnchorPane;
	@FXML private CheckBox aiCheckBox;
	@FXML private Tab helpTab;
	@FXML private Tab gameTab;
	@FXML private TextField brettGroesseTextField;
	@FXML private RadioButton zweiSpielerButton;
	@FXML private ImageView stoneImage;
	@FXML private ComboBox<String> brettGroesseBox;
	@FXML private ToggleButton bild2Button;
	@FXML private Tab einstellungenTab;
	@FXML private TextField anzahlReiheTextField;
	@FXML private ToggleButton bild1Button;
	@FXML private RadioButton einSpielerButton;
	@FXML private RadioButton aiButton;
	@FXML private TextArea hilfeText;
	@FXML private TextArea uberText;
	@FXML private Button neuButton;
	@FXML private Button startenButton;
	
	//ToggleGroup fuer radioButtons 1Spieler, 2Spieler, AI
	final ToggleGroup radioButtonGroup = new ToggleGroup();
	
	//ToggleGroup für Bilder
	final ToggleGroup bildGroup = new ToggleGroup();
	
	//Optionen fuer ComboBox
	ObservableList<String> choiceBoxOptions = 
			FXCollections.observableArrayList(
				"Tic Tac Toe",
				"Gomoku 15",
				"Gomoku 17",
				"Gomoku 19 (Go)",
				" "
			);
	
	Brett spielbrett;
	
	AnimationTimer zweiAiTimer;
	
	SpielAI gegner;

	SpielStein s;
	double currWidth, currHeight;
	ImageView lastPlayed;
	List<ImageView> winningStone;
	
	static long lastTime=0;
	
	@FXML private void initialize()
	{	
		currWidth=gameAnchorPane.getPrefWidth();
		currHeight=gameAnchorPane.getPrefHeight();
		
		backgroundImage.setImage((Image) Main.optionen.getOption("BackgroundImage"));
		backgroundImage.setPreserveRatio(false);
		backgroundImage.setFitWidth(gameAnchorPane.getPrefWidth());
		backgroundImage.setFitHeight(gameAnchorPane.getPrefHeight());
		
		//RadioButtons der ToggleGroup zuordnen
		einSpielerButton.setToggleGroup(radioButtonGroup);
		zweiSpielerButton.setToggleGroup(radioButtonGroup);
		aiButton.setToggleGroup(radioButtonGroup);
		
		brettGroesseBox.setItems(choiceBoxOptions);

		ImageView bild1 = new ImageView("resources/Ahorn_Holz.JPG");
		
		bild1.setFitHeight(bild1Button.getPrefHeight());
		bild1.setFitWidth(bild1Button.getPrefWidth());
		
		bild1Button.setGraphic(bild1);
		bild1Button.setToggleGroup(bildGroup);
		
		
		ImageView bild2 = new ImageView("resources/AhornMaser01.JPG");
		
		bild2.setFitHeight(bild2Button.getPrefHeight());
		bild2.setFitWidth(bild2Button.getPrefWidth());
		
		bild2Button.setGraphic(bild2);
		bild2Button.setToggleGroup(bildGroup);
		
		standardEinstellungen();
		
		gameAnchorPane.widthProperty().addListener(new ChangeListener<Number>()
		{	@Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{	handleSizeChanged();	}	});
		
		gameAnchorPane.heightProperty().addListener(new ChangeListener<Number>()
		{	@Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{	handleSizeChanged();	}	});
	}
	
	//alle standardeinstellungen
	private void standardEinstellungen()
	{
		einSpielerButton.setSelected(true);
		brettGroesseTextField.setText("19");
		brettGroesseLabel.setText("19");
		brettGroesseBox.setValue("Gomoku 19 (Go)");
		anzahlReiheTextField.setText("5");
		anlegenCheckBox.setSelected(true);
		aiCheckBox.setSelected(true);
		mitteBeginnCheckBox.setSelected(true);
		hilfeText.setEditable(false);
		uberText.setEditable(false);
		bild1Button.setSelected(true);
	}
	
	//baue Brett auf
	private void bildeBrett()
	{
		spielbrett=new Brett((int) Main.optionen.getOption("brettgroesse"), gameAnchorPane.getPrefWidth(), gameAnchorPane.getPrefHeight());		
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
		//	System.out.println("foo?");//
			handleMouseClicked((int)(currWidth/2), (int)(currHeight/2));

		System.out.println("cW:"+currWidth+"cH:"+currHeight);
		
		if((int) Main.optionen.getOption("anzahlAi")==2)
		{
			Main.optionen.setOption("aiFaengtAn", false);
			zweiAiTimer=new AnimationTimer()
			{
				@Override
				public void handle(long time)
				{
					stoneImage.setX(-1000); // move out of view
					if(time>lastTime)
					{
						lastTime=time+(int)Main.optionen.getOption("twoAiSpeed");
						letAImakeMove();
					}
				}
			};
			zweiAiTimer.start();
		} // only ai
		
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
		} // 1 ai
	} // bildeBrett()
	
	
	//spieler auswahl
	@FXML private void handleSpielerAnzahlButton(ActionEvent event)
	{
		if (einSpielerButton.isSelected())
			Main.optionen.setOption("anzahlAi",1);
		
		if (zweiSpielerButton.isSelected())
			Main.optionen.setOption("anzahlAi",0);
		
		if (aiButton.isSelected())
			Main.optionen.setOption("anzahlAi", 2);
	}
	
	//brettgroesse box
	@FXML private void handleBrettGroesseBox(ActionEvent event)
	{	
		String brettGroesseText = brettGroesseBox.getSelectionModel().getSelectedItem();
		
		if (brettGroesseText == "Tic Tac Toe")
			brettGroesseTextField.setText("3");
		
		if (brettGroesseText == "Gomoku 15" )
			brettGroesseTextField.setText("15");

		if (brettGroesseText == "Gomoku 17" )
			brettGroesseTextField.setText("17");
		
		if (brettGroesseText == "Gomoku 19 (Go)" )
		{
			brettGroesseTextField.setText("19");
			standardEinstellungen();
		}
		
		if (brettGroesseText == " " )
			brettGroesseTextField.setText(" ");
		
		String brettGroesse = brettGroesseTextField.getText();
		brettGroesseLabel.setText(brettGroesse);
		Main.optionen.setOption("brettgroesse", Integer.parseInt(brettGroesse));
	}
	
	//brettgroesse textfeld
	@FXML private void handleBrettGroesseFeld(ActionEvent event)
	{
		String brettGroesse = brettGroesseTextField.getText();		
		brettGroesseLabel.setText(brettGroesse);
	
		switch (brettGroesse)
		{
		case "3":  brettGroesseBox.setValue("Tic Tac Toe"); break;
		case "15": brettGroesseBox.setValue("Gomoku 15"); break;
		case "17": brettGroesseBox.setValue("Gomoku 17"); break;
		case "19": brettGroesseBox.setValue("Gomoku 19 (Go)"); break;
		default:   brettGroesseBox.setValue(" "); break;
		}
	
		Main.optionen.setOption("brettgroesse", Integer.parseInt(brettGroesse));
	}

	//spielregeln
	@FXML private void handleSpielregeln(ActionEvent event)
	{
		Main.optionen.setOption("inEinerReihe", Integer.parseInt(anzahlReiheTextField.getText()));
		Main.optionen.setOption("nurAnlegen", anlegenCheckBox.isSelected());		
		Main.optionen.setOption("aiFaengtAn", aiCheckBox.isSelected());				
		Main.optionen.setOption("anfangInMitte", mitteBeginnCheckBox.isSelected());
	}
	
	
	//hintergrund bild 1
	@FXML private void handleBackground(ActionEvent event) {
		
		if (bild1Button.isSelected())
		{
			backgroundImage.setImage(new Image("resources/Ahorn_Holz.JPG"));
			Main.optionen.setOption("BackgroundImage", new Image("resources/Ahorn_Holz.JPG"));
		}
			
		if (bild2Button.isSelected())
		{
			backgroundImage.setImage(new Image("resources/AhornMaser01.JPG"));
			Main.optionen.setOption("BackgroundImage", new Image("resources/AhornMaser01.JPG"));
		}
	}
	
	//neustart
	@FXML private void handleNeuButton()
	{
		bildeBrett();
	}
	
	//start button
	@FXML private void handleStartButton(ActionEvent event)
	{
		startenButton.setDisable(true);
		startenButton.setVisible(false);
		bildeBrett();
	}
	
	@FXML private void handleMouseMoved(MouseEvent event)
	{
		// fix pos
		double coord[]= spielbrett.roundCoord(event.getX(), event.getY());

		// update position of next-stone
		stoneImage.setX(coord[0]-spielbrett.getGitterWeite()/2);
		stoneImage.setY(coord[1]-spielbrett.getGitterWeite()/2);
		stoneImage.setFitWidth( spielbrett.getGitterWeite());
		stoneImage.setFitHeight(spielbrett.getGitterWeite());
	}

	// to emulate a default parameter (of false)
	@FXML private void handleSizeChanged()
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
				
		if(spielbrett!=null)
		{	
			spielbrett.redrawGitter(currWidth==0?gameAnchorPane.getPrefWidth():currWidth, currHeight==0?gameAnchorPane.getPrefHeight():currHeight);
			stoneImage.setFitWidth(spielbrett.getGitterWeite());
		}
		
		// update lastMove image
		updatePlayMarkers();
		stoneImage.setX(-1000); // move out of view until next mouse movement
	}
	
	// only update the relative position of the circle on the screen
	private void updatePlayMarkers()
	{
		if(spielbrett!=null&&spielbrett.getSpielZuege().size()!=0)
		{
			SpielZug lastMove = spielbrett.getSpielZuege().get(spielbrett.getSpielZuege().size()-1);
			lastPlayed.setX(lastMove.x*spielbrett.getGitterWeite()+spielbrett.getRandX()-spielbrett.getGitterWeite()/2);
			lastPlayed.setY(lastMove.y*spielbrett.getGitterWeite()+spielbrett.getRandY()-spielbrett.getGitterWeite()/2);
			lastPlayed.setFitHeight(spielbrett.getGitterWeite());
			lastPlayed.setFitWidth(spielbrett.getGitterWeite());
			lastPlayed.toFront();
		}
		checkIfGewinner(); // this implicitly removes old and redraws them
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
//		System.out.println(x+" "+y);
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
		System.out.println("Click "+event.getX()+" "+event.getY());

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
//		System.out.println("letAImakeMove");
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
		if(spielbrett==null||spielbrett.getSpielZuege().size()==0)
			return false; // noch nichts da zum ueberpruefen
		
		SpielZug letzterZug = spielbrett.getSpielZuege().get(spielbrett.getSpielZuege().size()-1);

		if(winningStone!=null) // remove potential leftovers
			gameAnchorPane.getChildren().removeAll(winningStone);

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
					{
						erg=true;
						winningStone=new ArrayList<ImageView>();
						Image w=(Image)Main.optionen.getOption("WinningStone");
						for (int i = 0; i < (int)Main.optionen.getOption("inEinerReihe"); i++)
						{
							ImageView iv=new ImageView(w);
							iv.setX((letzterZug.x + richtungX*i-.5)*spielbrett.getGitterWeite()+spielbrett.getRandX());
							iv.setFitHeight(letzterZug.iView.getFitHeight());
							
							iv.setY((letzterZug.y + richtungY*i-.5)*spielbrett.getGitterWeite()+spielbrett.getRandY());
							iv.setFitWidth(letzterZug.iView.getFitWidth());

							winningStone.add(iv);
						}
						gameAnchorPane.getChildren().addAll(winningStone);
						lastPlayed.toFront();
					}
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

















