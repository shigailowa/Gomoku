package application.view;


import application.model.Brett;
import application.model.Brett.SpielZug;
import application.model.Options;
import application.model.SpielAI;
import application.model.SpielStein;
import javafx.animation.AnimationTimer;
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
	
	@FXML
    private CheckBox mitteBeginnCheckBox;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private Tab ueberTab;

    @FXML
    private CheckBox anlegenCheckBox;

    @FXML
    private Label brettGroesseLabel;

    @FXML
    private AnchorPane einstellungenAnchorPane;

    @FXML
    private AnchorPane gameAnchorPane;

    @FXML
    private CheckBox aiCheckBox;

    @FXML
    private Tab helpTab;

    @FXML
    private Tab gameTab;

    @FXML
    private TextField brettGroesseTextField;

    @FXML
    private RadioButton zweiSpielerButton;

    @FXML
    private ImageView stoneImage;

    @FXML
    private ComboBox<String> brettGroesseBox;

    @FXML
    private ToggleButton bild2Button;

    @FXML
    private Tab einstellungenTab;

    @FXML
    private TextField anzahlReiheTextField;

    @FXML
    private ToggleButton bild1Button;

    @FXML
    private RadioButton einSpielerButton;

    @FXML
    private RadioButton aiButton;
    
    @FXML 
    private TextArea hilfeText;
    
    @FXML
    private TextArea uberText;
    
    @FXML
    private Button neuButton;
    
    @FXML
    private Button startenButton;
    
    //für Optionen
    private Options optionen = new Options();
    
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
		
	}
	
	//alle standardeinstellungen
	private void standardEinstellungen() {
		
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
    	
    	optionen.setOptions("debug", false);
		optionen.setOptions("BackgroundImage", new Image("resources/Ahorn_Holz.JPG"));
		optionen.setOptions("nextStoneOpacity", .7);
		optionen.setOptions("aiFaengtAn", true);
		optionen.setOptions("anzahlAi", 2); // \in \{0, 1, 2\}
		optionen.setOptions("inEinerReihe", 5);
		optionen.setOptions("brettgroesse", 19);
		optionen.setOptions("anfangInMitte", true);
		optionen.setOptions("anfangsFarbe", 0);
		optionen.setOptions("twoAiSpeed", 1);	
	}
	
	//baue Brett auf
	private void bildeBrett() {
		
		Integer brettgroesse = (Integer) optionen.getOptions("brettgroesse");
		
		spielbrett=new Brett(brettgroesse, gameAnchorPane.getPrefWidth(), gameAnchorPane.getPrefHeight());		
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

		handleMouseClicked(e);
		handleSizeChanged(true); // force the redraw

		gegner=new SpielAI(spielbrett);

		boolean aiFaengtAn=false;//TODO:aus einstellungen
		if(!aiFaengtAn)
		{
			gegner.generateNextMoves();
			//gegner.getMove();
		}
	}
	
	
	//spieler auswahl
	@FXML
	private void handleSpielerAnzahlButton(ActionEvent event) {
		
		if (einSpielerButton.isSelected()) {
			optionen.setOptions("anzahlAi",1);
		}
		
		if (zweiSpielerButton.isSelected()) {
			optionen.setOptions("anzahlAi",0);
		}
		
		if (aiButton.isSelected()) {
			optionen.setOptions("anzahlAi", 2);
		}
		
	}
	
	//brettgroesse box
	@FXML
	private void handleBrettGroesseBox(ActionEvent event) {
		
		String brettGroesseText = brettGroesseBox.getSelectionModel().getSelectedItem();
		
		if (brettGroesseText == "Tic Tac Toe") {
			brettGroesseTextField.setText("3");
		}
		
		if (brettGroesseText == "Gomoku 15" ) {
			brettGroesseTextField.setText("15");
		}
		
		if (brettGroesseText == "Gomoku 17" ) {
			brettGroesseTextField.setText("17");
		}
		
		if (brettGroesseText == "Gomoku 19 (Go)" ) {
			brettGroesseTextField.setText("19");
			standardEinstellungen();
		}
		
		if (brettGroesseText == " " ) {
			brettGroesseTextField.setText(" ");
		}
		
		String brettGroesse = brettGroesseTextField.getText();
		
		brettGroesseLabel.setText(brettGroesse);
		
		optionen.setOptions("brettgroesse", Integer.parseInt(brettGroesse));
	}
	
	//brettgroesse textfeld
	@FXML
	private void handleBrettGroesseFeld(ActionEvent event) {
		
		String brettGroesse = brettGroesseTextField.getText();
		
		brettGroesseLabel.setText(brettGroesse);
		
		if (brettGroesse == "3") {
			brettGroesseBox.setValue("Tic Tac Toe");
		}
		else if (brettGroesse == "15") {
			brettGroesseBox.setValue("Gomoku 15");
		}
		else if (brettGroesse == "17") {
			brettGroesseBox.setValue("Gomoku 17");
		}
		else if (brettGroesse == "19") {
			brettGroesseBox.setValue("Gomoku 19 (Go)");
		}
		else {
			brettGroesseBox.setValue(" ");
		}
		
		optionen.setOptions("brettgroesse", Integer.parseInt(brettGroesse));
		
	}
	
	
	//spielregeln
	@FXML
	private void handleSpielregeln(ActionEvent event) {
		
		String reiheText = anzahlReiheTextField.getText();
		
		optionen.setOptions("inEinerReihe", Integer.parseInt(reiheText));
		
		if (anlegenCheckBox.isSelected()) {
			optionen.setOptions("nurAnlegen", true);
		} else {
			optionen.setOptions("nurAnlegen", false);
		}
		
		if (aiCheckBox.isSelected()) {
			optionen.setOptions("aiFaengtAn", true);
		} else {
			optionen.setOptions("aiFaengtAn", false);
		}
		
		if (mitteBeginnCheckBox.isSelected()) {
			optionen.setOptions("anfangInMitte", true);
		} else {
			optionen.setOptions("anfangInMitte", false);
		}
	}
	
	
	//hintergrund bild 1
	@FXML 
	private void handleBackground(ActionEvent event) {
		
		if (bild1Button.isSelected()) {
			backgroundImage.setImage(new Image("resources/Ahorn_Holz.JPG"));
			optionen.setOptions("BackgroundImage", new Image("resources/Ahorn_Holz.JPG"));
		}
			
		if (bild2Button.isSelected()) {
			backgroundImage.setImage(new Image("resources/AhornMaser01.JPG"));
			optionen.setOptions("BackgroundImage", new Image("resources/AhornMaser01.JPG"));
		}
	}
	
	
	//neustart
	@FXML
	private void handleNeuButton() {
		bildeBrett();
	}
	
	//start button
	@FXML
	private void handleStartButton(ActionEvent event) {
		
		startenButton.setDisable(true);
		startenButton.setVisible(false);
		bildeBrett();
	}
	
	@FXML
	private void handleMouseMoved(MouseEvent event)
	{
//		handleSizeChanged(); // unschoen, aber geht erstmal nur so
		
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
				
		spielbrett.redrawGitter(currWidth,currHeight);
		stoneImage.setFitWidth(spielbrett.getGitterWeite());
	}
	
	@FXML
	void handleDragDetected(MouseEvent event)
	{
//		System.out.println("handleDragDetected");
		handleSizeChanged();
		handleMouseMoved(event); // to make the move count where the mouse ended up at the end of the drag
//		spielbrett.redrawGitter(currWidth,currHeight); // to move the pieces to the correct position
//		stoneImage.setFitWidth(spielbrett.getGitterWeite());
		handleSizeChanged();
	}
	
	@FXML
	private void handleMouseClicked(MouseEvent event)
	{
//		System.out.println("handleMouseClicked");
		
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
				gegner.generateNextMoves();

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

















