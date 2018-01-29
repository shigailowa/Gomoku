package application;

import application.model.Options;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application
{
	public static Options optionen=new Options();
	public static Stage primaryStage ;
	@Override
	public void start(Stage primaryStage)
	{
		Main.primaryStage=primaryStage;
		try
		{
			FXMLLoader loader=new FXMLLoader();
		    loader.setLocation(Main.class.getResource("view/GameLayout.fxml"));
		    Scene spielScene = new Scene(loader.load());
		    
		    spielScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(spielScene);
			primaryStage.show();
	        spielScene.getRoot().requestFocus(); // damit der keyhandler anspringt
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		optionen.setOption("debug", false);
		optionen.setOption("BackgroundImageAhornHolz", new Image("resources/Ahorn_Holz.JPG"));
		optionen.setOption("BackgroundImageAhornMasern", new Image("resources/AhornMaser01.JPG"));
		optionen.setOption("nextStoneOpacity", .7);
		optionen.setOption("aiFaengtAn", true);
		optionen.setOption("anzahlAi", 2); // \in \{0, 1, 2\}
		optionen.setOption("inEinerReihe", 5);
		optionen.setOption("brettgroesse", 19);
		optionen.setOption("anfangInMitte", true);
		optionen.setOption("anfangsFarbe", 0);
		optionen.setOption("twoAiSpeed", (long)Math.floor(1*1000000000)/*makes it seconds*/);
		optionen.setOption("WinningStone", new Image("resources/WinningStone.png"));
		launch(args);
	}
}










