package application.view;

import java.awt.Event;

import application.model.Brett;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class SpielController {
	@FXML AnchorPane gameAnchorPane;
	@FXML ImageView backgroundImage;
	Brett spielbrett;
	
	//test
	Circle c;
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
		
		spielbrett=new Brett(4, gameAnchorPane.getPrefWidth(), gameAnchorPane.getPrefHeight());		
		gameAnchorPane.getChildren().addAll(spielbrett.getGitter());
		
		c=new Circle(-10, -10, spielbrett.getGitterWeite()/2-1);
		gameAnchorPane.getChildren().add(c);
	}
	
	@FXML
	private void handleMouseMoved(MouseEvent event)
	{
		if(gameAnchorPane.getWidth()!=currWidth||gameAnchorPane.getHeight()!=currHeight)
			handleSizeChanged();
//		System.out.println(spielbrett.getRandX()+" "+spielbrett.getRandY());
		c.setCenterX(event.getX()-event.getX()%spielbrett.getGitterWeite()+spielbrett.getGitterWeite()/2+spielbrett.getRandX()%spielbrett.getGitterWeite());
		c.setCenterY(event.getY()-event.getY()%spielbrett.getGitterWeite()+spielbrett.getGitterWeite()/2+spielbrett.getRandY()%spielbrett.getGitterWeite());
	}
	
	@FXML
	private void handleSizeChanged()
	{
//		System.out.println("änderung!");
		currWidth=gameAnchorPane.getWidth();
		currHeight=gameAnchorPane.getHeight();
		
		if(backgroundImage.getFitHeight()<currHeight)
			backgroundImage.setFitHeight(currHeight);
		if(backgroundImage.getFitWidth()<currWidth)
			backgroundImage.setFitWidth(currWidth);
				
		spielbrett.redrawGitter(currWidth,currHeight);
		c.setRadius(spielbrett.getGitterWeite()/2-1);
	}
	
	@FXML
	private void handleMouseClicked(MouseEvent event)
	{
		Circle w=new Circle(c.getCenterX(),c.getCenterY(),spielbrett.getGitterWeite()/2-1);
		gameAnchorPane.getChildren().add(w);
	}
}
