package application.view;

import application.model.Brett;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class SpielController {
	@FXML AnchorPane gameAnchorPane;
	Brett spielbrett;
	
	@FXML
	private void initialize()
	{	spielbrett=new Brett(17);
	}
}
