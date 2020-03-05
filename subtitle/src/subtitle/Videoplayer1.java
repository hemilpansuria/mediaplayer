
package subtitle;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Videoplayer1 extends Application {

    @Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		MediaPanel pane=new MediaPanel(stage);
		Scene scene=new Scene(pane);
		stage.setScene(scene);
		stage.setTitle("VideoPlayer");
		stage.show();
                
	}
	
	public static void main(String[] args){
		Application.launch(args);
	}
        
}
