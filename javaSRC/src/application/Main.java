package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {
	
	public static boolean DEBUG=false;
	
	
	@Override
	public void start(Stage primaryStage) {
		try {			
			FXMLLoader loader=new FXMLLoader();
		    loader.setLocation(Main.class.getResource("view/GameLayout.fxml"));
		    Scene spielScene = new Scene(loader.load());
		    
		    spielScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(spielScene);
			primaryStage.show();
	        spielScene.getRoot().requestFocus(); // damit der keyhandler anspringt
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {	
		launch(args);
	}
}










