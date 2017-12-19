package application.view;

import application.model.Brett;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class SpielController {
	@FXML AnchorPane gameAnchorPane;
	Brett spielbrett;
	
	//test
	Circle c;
	
	@FXML
	private void initialize()
	{
		spielbrett=new Brett(10, gameAnchorPane.getPrefWidth(), gameAnchorPane.getPrefHeight());
		c=new Circle(-10, -10, spielbrett.getGitterWeite()/2-1);
		gameAnchorPane.getChildren().add(c);
		
		gameAnchorPane.getChildren().addAll(spielbrett.getGitter());
	}
	
	@FXML
	private void handleMouseMoved(MouseEvent event)
	{
//		System.out.println(event.getX()+" "+event.getY());
		c.setCenterX(event.getX()-event.getX()%spielbrett.getGitterWeite()+spielbrett.getGitterWeite()/2);
		c.setCenterY(event.getY()-event.getY()%spielbrett.getGitterWeite()+spielbrett.getGitterWeite()/2);
	}
}
