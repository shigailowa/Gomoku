package application.view;

import java.util.LinkedHashSet;
import java.util.Set;

import application.model.*;
import application.model.Brett.SpielZug;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SpielController {
	@FXML AnchorPane gameAnchorPane;
	@FXML ImageView backgroundImage;
	@FXML ImageView stoneImage;
	Brett spielbrett;
	
	SpielStein s;
	double currWidth, currHeight;
	
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

















