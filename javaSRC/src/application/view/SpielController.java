package application.view;

import java.awt.Event;

import com.sun.javafx.geom.Point2D;

import application.model.Brett;
import application.model.SpielStein;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class SpielController {
	@FXML AnchorPane gameAnchorPane;
	@FXML ImageView backgroundImage;
	@FXML ImageView stoneImage;
	Brett spielbrett;
	
//	//test
//	Circle c;
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
		
//		c=new Circle(-1000, -1000, spielbrett.getGitterWeite()/2-1);
//		gameAnchorPane.getChildren().add(c);
		s=new SpielStein();
		stoneImage.setImage(s.getImage());
		stoneImage.setFitWidth(spielbrett.getGitterWeite());
		stoneImage.setX(-1000);// out of view
		stoneImage.toFront(); // pack den spielstein vor das gitter
		stoneImage.setSmooth(true);
		stoneImage.setOpacity(.85);
	}
	
	@FXML
	private void handleMouseMoved(MouseEvent event)
	{
		if(gameAnchorPane.getWidth()!=currWidth||gameAnchorPane.getHeight()!=currHeight)
			handleSizeChanged(); // unschoen, aber geht erstmal nur so
		
//		System.out.println(spielbrett.getRandX()+" "+spielbrett.getRandY());
//		Point2D a=new Point2D(1,2);
		double coord[]= spielbrett.roundCoord(event.getX(), event.getY());
//		c.setCenterX(event.getX()-event.getX()%spielbrett.getGitterWeite()+spielbrett.getGitterWeite()/2+spielbrett.getRandX()%spielbrett.getGitterWeite());
//		c.setCenterY(event.getY()-event.getY()%spielbrett.getGitterWeite()+spielbrett.getGitterWeite()/2+spielbrett.getRandY()%spielbrett.getGitterWeite());
		//c.setCenterX(coord[0]);
		//c.setCenterY(coord[1]);
		
//		System.out.println(currWidth+" "+currHeight+"  "+spielbrett.getGitter().get(20).getEndX());
		
		
		stoneImage.setX(coord[0]-spielbrett.getGitterWeite()/2);
		stoneImage.setY(coord[1]-spielbrett.getGitterWeite()/2);
		stoneImage.setFitWidth(spielbrett.getGitterWeite());
		stoneImage.setFitHeight(spielbrett.getGitterWeite());
//		System.out.println(stoneImage.getFitWidth()+" "+spielbrett.getGitterWeite());
//		stoneImage.setPreserveRatio(false);
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
//		c.setRadius(spielbrett.getGitterWeite()/2-1);
		stoneImage.setFitWidth(spielbrett.getGitterWeite());
	}
	
	@FXML
	private void handleMouseClicked(MouseEvent event)
	{
//		Circle w=new Circle(c.getCenterX(),c.getCenterY(),spielbrett.getGitterWeite()/2-1);
//		gameAnchorPane.getChildren().add(w);
	}
	
	@FXML //ka, springt nich an, lass ich hier aber erstmal liegen...
	//ich wollt damit nen debugmodus aktivieren...
	private void handleKeyTyped(KeyEvent event)
	{
		System.out.println("handleKeyTyped");
		System.out.println(event.toString());
	}
}

















