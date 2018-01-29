package application.view;

import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

//Controller für Pop-Up-Fenster, wenn es einen Gewinner gibt
public class GewinnerController
{
    @FXML private AnchorPane gewinnerPane;
    @FXML private Button abbrechenButton;
    @FXML private Button neuButton;
    @FXML private ImageView gewinneriView;
    @FXML private Text gewinnerText;
    
    private SpielController spielController;
    private Stage gewinnerStage;
    
    /*
     * Handler für Abbrechen Button
     * @param event  
     */
    @FXML void handleAbbrechenButton(ActionEvent event)
    {
		gewinnerStage.close();
    }

    /*
     * Handler für Neu Button
     * @param event
     */
    @FXML void handleNeuButton(ActionEvent event)
    {
		gewinnerStage.close();
		spielController.neustart();
		spielController.bildeBrett();
    }
    
    /*
     * 
     * @param image  Bild
     */
    public void setGewinnerImage(Image image)
    {
    	gewinneriView.setImage(image);
    }

    /*
     * 
     * @param gewinnerStage
     */
    public void setDialogStage(Stage gewinnerStage)
	{
		this.gewinnerStage=gewinnerStage;
	}
    
    /*
     * @param s  Text
     */
    public void setGewinnerText(String s)
    {
    	gewinnerText.setText(s);
    }
    
    /*
     * @ param spielController
     */
    public void setDialogSpielController(SpielController spielController)
	{
		this.spielController = spielController;
	}
}














