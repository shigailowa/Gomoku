package application.view;

import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class GewinnerController
{
    @FXML private AnchorPane gewinnerPane;
    @FXML private Button abbrechenButton;
    @FXML private Button neuButton;
    @FXML private ImageView gewinneriView;
    @FXML private Text gewinnerText;
    
    private SpielController spielController;
    private Stage gewinnerStage;

	@FXML private void initialize()
	{

	}
    
    @FXML void handleAbbrechenButton(ActionEvent event)
    {
//		System.out.println("GewinnerController::handleAbbrechenButton");		
		gewinnerStage.close();
    }

    @FXML void handleNeuButton(ActionEvent event)
    {
//		System.out.println("GewinnerController::handleNeuButton");	
		gewinnerStage.close();
		spielController.neustart();
		spielController.bildeBrett();
    }
    
    public void setGewinnerImage(Image image)
    {
    	gewinneriView.setImage(image);
    }

    public void setDialogStage(Stage gewinnerStage)
	{
		this.gewinnerStage=gewinnerStage;
//		System.out.println("GewinnerController::setDialogStage");		
	}
    
    public void setGewinnerText(String s)
    {
    	gewinnerText.setText(s);
    }
    
    public void setDialogSpielController(SpielController spielController)
	{
		this.spielController = spielController;
//		System.out.println("GewinnerController::setDialogSpielController");		
	}
}














