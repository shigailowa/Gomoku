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
		/*
		System.out.println("inf "+Double.POSITIVE_INFINITY); // inf
		System.out.println("inf*0 "+Double.POSITIVE_INFINITY*0); // NaN
		System.out.println("inf*2 "+Double.POSITIVE_INFINITY*2); //inf
		System.out.println("inf*inf "+Double.POSITIVE_INFINITY*Double.POSITIVE_INFINITY); // inf
		System.out.println("inf/0 "+Double.POSITIVE_INFINITY/0); // inf
		System.out.println("0/inf "+0/Double.POSITIVE_INFINITY); // 0
		System.out.println("inf/2 "+Double.POSITIVE_INFINITY/2); // inf
		System.out.println("2/inf "+2/Double.POSITIVE_INFINITY); // 0
		System.out.println("inf/inf "+Double.POSITIVE_INFINITY/Double.POSITIVE_INFINITY); // NaN
//		System.out.println("0/0"+0/0); // wirft div by 0 exception

		System.out.println();
		System.out.println("inf "+Double.NEGATIVE_INFINITY); // -inf
		System.out.println("inf*0 "+Double.NEGATIVE_INFINITY*0); // NaN
		System.out.println("inf*2 "+Double.NEGATIVE_INFINITY*2); // -inf
		System.out.println("inf*inf "+Double.NEGATIVE_INFINITY*Double.NEGATIVE_INFINITY); // +inf
		System.out.println("inf/0 "+Double.NEGATIVE_INFINITY/0); // -inf
		System.out.println("0/inf "+0/Double.NEGATIVE_INFINITY); // -0
		System.out.println("inf/2 "+Double.NEGATIVE_INFINITY/2); // -inf
		System.out.println("2/inf "+2/Double.NEGATIVE_INFINITY); // -0
		System.out.println("inf/inf "+Double.NEGATIVE_INFINITY/Double.NEGATIVE_INFINITY); // NaN
*/
		
		
		optionen.setOption("debug", false);
		optionen.setOption("BackgroundImageAhornHolz", new Image("resources/Ahorn_Holz.JPG"));
		optionen.setOption("BackgroundImageAhornMasern", new Image("resources/AhornMaser01.JPG"));
		optionen.setOption("nextStoneOpacity", .7);
		optionen.setOption("aiFaengtAn", true);
		optionen.setOption("anzahlAi", 0); // \in \{0, 1, 2\}
		optionen.setOption("inEinerReihe", 5);
		optionen.setOption("brettgroesse", 19);
		optionen.setOption("anfangInMitte", true);
		optionen.setOption("anfangsFarbe", 0);
		optionen.setOption("twoAiSpeed", (int)Math.floor(0.01*1000000000)/*makes it seconds*/);
		optionen.setOption("WinningStone", new Image("resources/WinningStone.png"));
		launch(args);
	}
}










