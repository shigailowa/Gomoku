package application.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import application.Main;
import application.model.*;
import application.model.Brett.SpielZug;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SpielController {
	
	@FXML private CheckBox mitteBeginnCheckBox;
	@FXML private ImageView backgroundImage;
	@FXML private TabPane tabPaneSwitch; // 33 heigher than the gameAnchorPane such that the actual game is square to begin with
	@FXML private Tab ueberTab;
	@FXML private CheckBox anlegenCheckBox;
	@FXML private Label brettGroesseLabel;
	@FXML private AnchorPane einstellungenAnchorPane;
	@FXML private AnchorPane gameAnchorPane; // holds the actual game
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
	@FXML private Button spielStartenButton;
	@FXML private Button startButton;
	@FXML private Button newGameButton;
	@FXML private ToggleButton pauseGameButton;
	@FXML private Button zuruecksetzenButton;
	@FXML private AnchorPane wrapAnchorPane;
	@FXML private Slider aiSpeedSlider;
	
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

	boolean gameDone;
	SpielStein s;
	double currWidth, currHeight;
	ImageView lastPlayed;
	List<ImageView> winningStone;
	
	// stuff for ai
	SpielAI gegner;
	static long lastTime=0;
	boolean aiPaused = false;
	AnimationTimer zweiAiTimer;
	
	@FXML private void initialize()
	{	
		gameDone=false;
		currWidth=gameAnchorPane.getPrefWidth();
		currHeight=gameAnchorPane.getPrefHeight();
		
		backgroundImage.setImage((Image) Main.optionen.getOption("BackgroundImageAhornHolz"));
		backgroundImage.setPreserveRatio(false);
		backgroundImage.setFitWidth(gameAnchorPane.getPrefWidth());
		backgroundImage.setFitHeight(gameAnchorPane.getPrefHeight());
		
		//RadioButtons der ToggleGroup zuordnen
		einSpielerButton.setToggleGroup(radioButtonGroup);
		zweiSpielerButton.setToggleGroup(radioButtonGroup);
		aiButton.setToggleGroup(radioButtonGroup);
		
		einSpielerButton.setSelected(true); //TODO: oder was auch immer aus den Einstellungen
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
		
		pauseGameButton.setDisable(true);
		pauseGameButton.setVisible(false);
		
		newGameButton.setDisable(true);
		newGameButton.setVisible(false);
		
		aiSpeedSlider.setDisable(true);
		aiSpeedSlider.setVisible(false);
		
		standardEinstellungen();
		
		wrapAnchorPane.widthProperty().addListener(new ChangeListener<Number>()
		{	@Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{	handleSizeChanged();	}	});
		
		wrapAnchorPane.heightProperty().addListener(new ChangeListener<Number>()
		{	@Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{	handleSizeChanged();	}	});
		aiSpeedSlider.setValue(12);
		aiSpeedSlider.valueProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				Double eps=1e-10;
				if(aiSpeedSlider.getValue()<eps)
					aiSpeedSlider.setValue(eps);
				Main.optionen.setOption("twoAiSpeed", (long)((Math.pow(1.06, aiSpeedSlider.getMax()- aiSpeedSlider.getValue())-.9875)*1000000000));
			}
		});
	}
	
	//alle standardeinstellungen
	private void standardEinstellungen()
	{
		aiButton.setSelected(true);
		brettGroesseTextField.setText("19");
		brettGroesseLabel.setText("19");
		brettGroesseBox.setValue("Gomoku 19 (Go)");
		anzahlReiheTextField.setText("5");
		anlegenCheckBox.setSelected(true);
		aiCheckBox.setSelected(true);
		aiCheckBox.setDisable(true);
		mitteBeginnCheckBox.setSelected(true);
		hilfeText.setEditable(false);
		uberText.setEditable(false);
		bild1Button.setSelected(true);
		backgroundImage.setImage((Image) Main.optionen.getOption("BackgroundImageAhornHolz"));
	}
	
	/**
	 * baue Brett auf und starte das spiel
	 */
	public void bildeBrett()
	{
		gameDone=false;
		newGameButton.setDisable(false);
		newGameButton.setVisible(true);
		
		spielbrett=new Brett((int) Main.optionen.getOption("brettgroesse"), gameAnchorPane.getPrefWidth(), gameAnchorPane.getPrefHeight());		
		gameAnchorPane.getChildren().addAll(spielbrett.getGitter());
		
		double min=Math.min(currWidth, currHeight);
		double pseudoGitterWeite=min/spielbrett.getDim(); // gebraucht, da zurzeit noch nicht verfuegbar in class Brett
		
		s=new SpielStein((int)Main.optionen.getOption("anfangsFarbe")); // da bei erneutem spiel vllt. anders
		stoneImage.setImage(s.getImage());
		stoneImage.setFitWidth(pseudoGitterWeite);
		stoneImage.toFront(); // pack den spielstein vor das gitter
		stoneImage.setOpacity((double) Main.optionen.getOption("nextStoneOpacity"));
		
		lastPlayed=new ImageView();
		lastPlayed.setImage(new Image("resources/lastPlayedStone.png"));
		lastPlayed.setFitWidth(pseudoGitterWeite);
		gameAnchorPane.getChildren().add(lastPlayed);
		
		if((boolean)Main.optionen.getOption("anfangInMitte"))
			// erster zug in der Mitte
			handleMouseClicked((int)(currWidth/2), (int)(currHeight/2));
		
		if((int) Main.optionen.getOption("anzahlAi")==2)
		{
			pauseGameButton.setDisable(false);
			pauseGameButton.setVisible(true);
			aiSpeedSlider.setDisable(false);
			aiSpeedSlider.setVisible(true);
			aiPaused=false;

			Main.optionen.setOption("aiFaengtAn", false);
			zweiAiTimer=new AnimationTimer()
			{
				@Override
				public void handle(long time)
				{
					stoneImage.setX(-1042); // move out of view
					if(time>lastTime+(long)Main.optionen.getOption("twoAiSpeed")&&!aiPaused&& gameTab.isSelected())
					{
						lastTime=time;
						letAImakeMove();
					}
				}
			};
			zweiAiTimer.start();
		} // only ai
		
		if((int)Main.optionen.getOption("anzahlAi")>0) // es gibt nur einen ai Gegner
		{
			gegner=new SpielAI(spielbrett);
			gegner.updateMoves();

			// let ai make a move
			if(!(boolean) Main.optionen.getOption("aiFaengtAn"))
			{
				Integer[][] zuege=gegner.getBestMoves();
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
		{
			Main.optionen.setOption("anzahlAi", 1);
			aiCheckBox.setDisable(false);
		}
			
		
		if (zweiSpielerButton.isSelected())
		{
			Main.optionen.setOption("anzahlAi", 0);
			aiCheckBox.setDisable(true);
		}
			
		if (aiButton.isSelected())
		{
			Main.optionen.setOption("anzahlAi", 2);
			aiCheckBox.setDisable(true);
		}
	}
	
	//brettgroesse box
	@FXML private void handleBrettGroesseBox(ActionEvent event)
	{	
		String brettGroesseText = brettGroesseBox.getSelectionModel().getSelectedItem();
		
		switch (brettGroesseText)
		{
		case "Tic Tac Toe": brettGroesseTextField.setText("3"); break;
			//TODO: lade einstellungen fuer tic tac toe
		case "Gomoku 15":   brettGroesseTextField.setText("15"); break;
		case "Gomoku 17":   brettGroesseTextField.setText("17"); break;
		case "Gomoku 19 (Go)":
			brettGroesseTextField.setText("17");
			standardEinstellungen(); // lade standard
			//TODO: sieh standard an und passe an
			break;

		default:
			brettGroesseTextField.setText("");
			break;
		}
		
		String brettGroesse = brettGroesseTextField.getText();
		if(brettGroesse.length() > 0 )
		{
			brettGroesseLabel.setText(brettGroesse);
			Main.optionen.setOption("brettgroesse", Integer.parseInt(brettGroesse));
		}
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
		default:   brettGroesseBox.setValue(""); break;
		}
	
		Main.optionen.setOption("brettgroesse", Integer.parseInt(brettGroesse));

		//eingegebener String darf nicht verschwinden
		brettGroesseTextField.setText(brettGroesse);
	}

	//spielregeln
	@FXML private void handleSpielregeln(ActionEvent event)
	{
		Main.optionen.setOption("inEinerReihe", Integer.parseInt(anzahlReiheTextField.getText()));
		Main.optionen.setOption("nurAnlegen", anlegenCheckBox.isSelected());		
		Main.optionen.setOption("aiFaengtAn", aiCheckBox.isSelected());				
		Main.optionen.setOption("anfangInMitte", mitteBeginnCheckBox.isSelected());
	}
	
	//hintergrund bild
	@FXML private void handleBackground(ActionEvent event)
	{
		if (bild1Button.isSelected())
			backgroundImage.setImage((Image) Main.optionen.getOption("BackgroundImageAhornHolz"));
			
		if (bild2Button.isSelected())
			backgroundImage.setImage((Image) Main.optionen.getOption("BackgroundImageAhornMasern"));
	}
	
	//neustart, setzt alles zurueck
	public void neustart()
	{
		if(spielbrett!=null)
			gameAnchorPane.getChildren().removeAll(spielbrett.getGitter());
		if(zweiAiTimer!=null)
			zweiAiTimer.stop();
		
		if(spielbrett!=null)
			for (int i = 0; i < spielbrett.getSpielZuege().size(); i++)
				gameAnchorPane.getChildren().removeAll(spielbrett.getSpielZuege().get(i).iView);

		// move out of the way, not just forget it
		if(lastPlayed!=null)
			lastPlayed.setX(-1000);
		if(winningStone!=null)
			winningStone.forEach(v->{
				v.setX(-1000);
			});
		if(stoneImage!=null)
			stoneImage.setX(-1000);
		spielbrett=null;
	}
	
	//neustart
	@FXML private void handleSpielStartenButton()
	{
		//disable all settings options
		disable();
		
		startButton.setDisable(true);
		startButton.setVisible(false);
		
		neustart();
		bildeBrett();
		
		if ((int)Main.optionen.getOption("anzahlAi") == 2)
		{
			pauseGameButton.setDisable(false);
			pauseGameButton.setVisible(true);
			aiPaused=true;
			pauseGameButton.setText("Play");
			pauseGameButton.setSelected(true);
		}
		else
		{
			pauseGameButton.setDisable(true);
			pauseGameButton.setVisible(false);
		}
		
		//switch to game tab
		tabPaneSwitch.getSelectionModel().select(gameTab);
	}
	
	//einstellungen auf standard zurücksetzen
	@FXML private void handleZuruecksetzenButton(ActionEvent event)
	{
		standardEinstellungen();
	}
	
	//is only visible at beginning or when 2 ai is selected
	@FXML private void handleStartButton()
	{
		disable();
		
		if((int)Main.optionen.getOption("anzahlAi")!=2)
		{
			pauseGameButton.setDisable(true);
			pauseGameButton.setVisible(false);
		}
		else
		{
			pauseGameButton.setDisable(false);
			pauseGameButton.setVisible(true);
		}
		
		startButton.setDisable(true);
		startButton.setVisible(false);

		bildeBrett();
	}
	
	private void disable()
	{
		//disable all settings options
		zuruecksetzenButton.setDisable(true);
		spielStartenButton.setDisable(true);
		einSpielerButton.setDisable(true);
		zweiSpielerButton.setDisable(true);
		aiButton.setDisable(true);
		brettGroesseTextField.setDisable(true);
		brettGroesseBox.setDisable(true);
		anzahlReiheTextField.setDisable(true);
		anlegenCheckBox.setDisable(true);
		aiCheckBox.setDisable(true);
		mitteBeginnCheckBox.setDisable(true);
	}
	
	private void enable()
	{
		//enable all settings options
		zuruecksetzenButton.setDisable(false);
		spielStartenButton.setDisable(false);
		einSpielerButton.setDisable(false);
		zweiSpielerButton.setDisable(false);
		aiButton.setDisable(false);
		brettGroesseTextField.setDisable(false);
		brettGroesseBox.setDisable(false);
		anzahlReiheTextField.setDisable(false);
		anlegenCheckBox.setDisable(false);
		aiCheckBox.setDisable(false);
		mitteBeginnCheckBox.setDisable(false);
	}

	// restart with same settings
	@FXML private void handleNewGameButton()
	{
		if ((int) Main.optionen.getOption("anzahlAi") == 2)
		{
			aiPaused=true;
			pauseGameButton.setText("Play");
		}
		
		//warn user about new start
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Neustart");
		alert.setHeaderText("Wenn du neu startest, geht dein ganzer bisheriger Fortschritt verloren");
		alert.setContentText("Willst du wirklich neu starten?");
		
		ButtonType buttonTypeOne = new ButtonType("Ja");
		ButtonType buttonTypeTwo = new ButtonType("Nein");
		ButtonType buttonTypeThree = new ButtonType("Abbrechen");
		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree);
		Optional<ButtonType> result = alert.showAndWait();
		
		if (result.get() == buttonTypeOne)
		{	
			enable();
			neustart();
			
			newGameButton.setDisable(true);
			newGameButton.setVisible(false);
			
			startButton.setVisible(true);
			startButton.setDisable(false);
		}
		else
		{
		    alert.close();
		    if ((int) Main.optionen.getOption("anzahlAi") == 2)
		    {
		    	  aiPaused=false;
		    	  pauseGameButton.setText("Pause");
		    }
		}
	} //handleNewGameButton()

	@FXML private void handlePauseGameButton(ActionEvent event)
	{
		if((int)Main.optionen.getOption("anzahlAi")!=2 || zweiAiTimer==null)
			return;
		
		if(pauseGameButton.isSelected())
		{
			aiPaused=true;
			pauseGameButton.setText("Play");
		}
		else
		{
			aiPaused=false;
			pauseGameButton.setText("Pause");
		}
	}
	
	@FXML private void handleMouseMoved(MouseEvent event)
	{
		if(spielbrett==null||gameDone)
			return; // ohne begonenes spiel nichts zu tun
		
		// fix pos
		double coord[]= spielbrett.roundCoord(event.getX(), event.getY());

		// update position of next-stone
		stoneImage.setX(coord[0]-spielbrett.getGitterWeite()/2);
		stoneImage.setY(coord[1]-spielbrett.getGitterWeite()/2);
		stoneImage.setFitWidth( spielbrett.getGitterWeite());
		stoneImage.setFitHeight(spielbrett.getGitterWeite());
	}
	
	/** checks and potentially redraws the game-objects that are rendered
	 * @param forceIt = false
	 * 
	 * allows skip of any check wether a redraw is necessary
	 */
	@FXML private void handleSizeChanged()
	{	handleSizeChanged(false);	}
	
	/** checks and potentially redraws the game-objects that are rendered
	 * @param forceIt = false
	 * 
	 * allows skip of any check wether a redraw is necessary
	 */
	private void handleSizeChanged(boolean forceIt)
	{
		if(forceIt||currWidth!=wrapAnchorPane.getWidth()||currHeight!=wrapAnchorPane.getHeight())
		{
			currWidth=wrapAnchorPane.getWidth();
			currHeight=wrapAnchorPane.getHeight();
		}
		else // nothing to fix
			return;
		
		// change sizes for inner thingies
		tabPaneSwitch.setPrefWidth(currWidth);
		tabPaneSwitch.setPrefHeight(currHeight);
		currHeight-=33; // the tabs have a height
		gameAnchorPane.setPrefWidth(currWidth);
		gameAnchorPane.setPrefHeight(currHeight);
		
		newGameButton.setLayoutX(currWidth-newGameButton.getPrefWidth()-2);
		newGameButton.setLayoutY(2); // 2=(33-29)/2
		
		pauseGameButton.setLayoutX(newGameButton.getLayoutX()-pauseGameButton.getPrefWidth()-2);
		pauseGameButton.setLayoutY(2);
		
		aiSpeedSlider.setLayoutX(pauseGameButton.getLayoutX()-aiSpeedSlider.getPrefWidth()-2);
		aiSpeedSlider.setLayoutY(8.5); // =(33-16)/2
		
		startButton.setLayoutX(currWidth/2-startButton.getPrefWidth()/2);
		startButton.setLayoutY(currHeight/2-startButton.getPrefHeight()/2);
		
		// this only lets the background image expand, never shrink
		if(backgroundImage.getFitHeight()<currHeight)
			backgroundImage.setFitHeight(currHeight);
		if(backgroundImage.getFitWidth()<currWidth)
			backgroundImage.setFitWidth(currWidth);
		
		backgroundImage.setLayoutX(-(backgroundImage.getFitWidth ()-currWidth )/2);
		backgroundImage.setLayoutY(-(backgroundImage.getFitHeight()-currHeight)/2);
				
		if(spielbrett!=null)
		{	
			spielbrett.redrawGitter(currWidth==0?wrapAnchorPane.getPrefWidth():currWidth, currHeight==0?wrapAnchorPane.getPrefHeight():currHeight);
			stoneImage.setFitWidth(spielbrett.getGitterWeite());
		}
		
		// update lastMove image
		updatePlayMarkers();
		stoneImage.setX(-1001); // move out of view until next mouse movement
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
		checkIfGewinner(); // this implicitly removes old playMarkers and redraws them
	}
	
	@FXML void handleDragDetected(MouseEvent event)
	{
		// to make the move count where the mouse ended up at the end of the drag
		handleMouseMoved(event);
	}

	/**
	 * emulates a mouseclick at a given position on the games Pane
	 * @param x position
	 * @param y position
	 */
	private void handleMouseClicked(int x, int y)
	{
		// synthesize mouseClickEvent and fire it
		handleMouseClicked(new MouseEvent(null, // source - the source of the event. Can be null.
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
		));
	}
	
	@FXML private void handleMouseClicked(MouseEvent event)
	{
		if(spielbrett==null||gameDone)
			return; // ohne begonenes spiel nichts zu tun

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
			stoneImage.setX(100);
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
	
	private void letAImakeMove()
	{
		if(gegner!=null&&!gameDone)
		{
			Integer[][] zuege=gegner.getBestMoves();
			int zugNum=(int)(Math.random()*zuege.length); // of the generated best moves, take one at random
			if(zuege.length==0)
			{
				// handle Unentschieden
				handleGewinner(true);
				return;
			}
			else if (zuege.length==1)
				zugNum=0;
			
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
	
	/**	wird gerufen um gewinnerbehandlung zu starten	
	 * @param unentschieden = false
	 * 		sollte es ein volles brett geben, oder unentschieden festgestellt worden sein, 
	 * 		setze auf true und erzwinge beendigung des spieles
	 * 
	 * @return ob es einen gewinner gibt
	 */
	private boolean handleGewinner()
	{	return handleGewinner(false);	}
	
	/**	wird gerufen um gewinnerbehandlung zu starten	
	 * @param unentschieden = false
	 * 		sollte es ein volles brett geben, oder unentschieden festgestellt worden sein, 
	 * 		setze auf true und erzwinge beendigung des spieles
	 * 
	 * @return ob es einen gewinner gibt
	 */
	private boolean handleGewinner(boolean unentschieden)
	{
		boolean erg=checkIfGewinner();
		if(erg || unentschieden)
		{
			gameDone=true;
			if(zweiAiTimer!=null)
				aiPaused=true;
			pauseGameButton.setDisable(true);
			pauseGameButton.setVisible(false);
			aiSpeedSlider.setDisable(true);
			aiSpeedSlider.setVisible(false);
			
			try
			{
				// Lade XML-Datei mit WaehrungLayout
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("view/GewinnerLayout.fxml"));
				AnchorPane gewinnerPane = (AnchorPane) loader.load();

				// Erzeuge neue Buehne fuer das Dialogfenster
				Stage gewinnerStage = new Stage();

				// Das Hauptfenster wird blockiert
				gewinnerStage.initModality(Modality.WINDOW_MODAL);
				gewinnerStage.initOwner(Main.primaryStage);
				
				Scene gewinnerScene = new Scene(gewinnerPane);
				gewinnerStage.setScene(gewinnerScene);

				// GewinnerController anbinden und Kontrolle ueber das Dialogfenster uebergeben
				GewinnerController controller = loader.getController();
				controller.setDialogStage(gewinnerStage);
				controller.setDialogSpielController(this);
				
				// set approptiate texts
				if(unentschieden)
				{
					gewinnerStage.setTitle("Es ist Unentschieden!");
					controller.setGewinnerText("Unentschieden!");
				}
				else
				{
					gewinnerStage.setTitle("Es gibt einen Gewinner!");
					controller.setGewinnerText("Gewinner:");
					controller.setGewinnerImage((spielbrett.getSpielZuege().get(spielbrett.getSpielZuege().size()-1)).iView.getImage());
				}
				
				gewinnerStage.setResizable(false);
				gewinnerStage.show();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return false;
			} // catch()
		} // gibt gewinner
		return erg;
	} //handleGewinner()

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
	} // checkIfGewinner()

	// for debbugging purposes
	@FXML private void handleKeyPressed(KeyEvent event)
	{
		switch(event.getCode())
		{
		case S:
			System.out.println("gameAnchorPane  h:"+gameAnchorPane.getHeight()+" w:"+gameAnchorPane.getWidth());
			System.out.println("tabPaneSwitch   h:"+tabPaneSwitch.getHeight()+" w:"+tabPaneSwitch.getWidth());
			System.out.println("difference:"+(tabPaneSwitch.getHeight()-gameAnchorPane.getHeight()));
			break;
		case P:
			System.out.println("lastPlayed  x:"+lastPlayed.getX() + " y:"+lastPlayed.getY());
			break;
		case I:
			System.out.println("stoneImage  x:"+stoneImage.getX()+" y:"+stoneImage.getY());
			break;
		case U:
			System.out.println("1x:"+spielbrett.getSpielZuege().iterator().next().iView.getX()
							 +" 1y:"+spielbrett.getSpielZuege().iterator().next().iView.getY());
			System.out.println("zuege:"+spielbrett.getSpielZuege());
			break;
		case M:
			ImageView iV=spielbrett.getSpielZuege().iterator().next().iView;
			System.out.println("vX:"+iV.getX()+" vY:"+iV.getY());
			iV.setX(iV.getX()+1);
			//spielbrett.getSpielZuege().forEach(z->{z.iView.setX(z.iView.getX()+1);}); // move all right
			break;
			
		default:
			break;
		}
	}

	// for debbugging purposes
	@FXML private void handleKeyReleased(KeyEvent event)
	{
//		System.out.println("handleKeyReleased: "+event.getCode()+" "+event.toString());
	}
}

















