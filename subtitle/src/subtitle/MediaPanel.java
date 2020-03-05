
package subtitle;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 *
 * @author NEEL
 */
public class MediaPanel extends BorderPane {
    
private Media media;
	private MediaPlayer mediaPlayer;
	private MediaView mediaView;
	private final Button endButton;
	private final Button foreButton;
	private Button playButton;
	private final Button backButton;
	private final Button openButton;
	private Slider slVolume;
	private final HBox hBox;
	private Slider slProcess;
	private final VBox vBox;
	private FileChooser fileChooser;
	private File mediaFile,prevDirectory,target;
	private String MEDIA_URL;
	private boolean bCanPlay=false;
	private javafx.stage.Stage theStage;
	
	
	public MediaPanel(javafx.stage.Stage stage) {
		theStage=stage;
		setPrefSize(800, 600);
		setStyle("-fx-background-color:black");
		fileChooser=new FileChooser();
		fileChooser.setTitle("");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP4","*.mp4"));
		mediaView=new MediaView();
		
		endButton=new Button("[END]");
		backButton=new Button("<<");
		playButton=new Button("PLAY");
		foreButton=new Button(">>");
		openButton=new Button("[OPEN]");
		
                endButton.setOnAction(e->{
			if(bCanPlay){
				mediaPlayer.stop();
				playButton.setText(">");
			}
		});
		
		backButton.setOnAction(e->{
			if(bCanPlay){
				mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(new Duration(10000)));
			}
		});
		
		playButton.setOnAction(e->dealWithClick());
		
		foreButton.setOnAction(e->{
			if(bCanPlay){
				mediaPlayer.seek(mediaPlayer.getCurrentTime().add(new Duration(10000)));
			}
		});
		
		openButton.setOnAction((ActionEvent e) -> {
                    /**if(bCanPlay){
                        try {
                            fileChooser.setInitialDirectory(mediaFile.getParentFile());
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }**/
                    mediaFile=fileChooser.showOpenDialog(theStage);
                    if(mediaFile!=null){
                        MEDIA_URL=mediaFile.toURI().toString();
                        if(MEDIA_URL.endsWith(".mp4")){
                            try {
                                theStage.setTitle(MEDIA_URL);
                                if(bCanPlay){
                                    mediaPlayer.stop();
                                }
                                media=new Media(MEDIA_URL);
                                mediaPlayer=new MediaPlayer(media);
                                mediaPlayer.play();
                                playButton.setText("PAUSE");
                                mediaPlayer.volumeProperty().bind(slVolume.valueProperty().divide(100));
                                mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                                    @Override
                                    public void invalidated(Observable observable) {
                                        // TODO Auto-generated method stub
                                        slProcess.setValue(mediaPlayer.getCurrentTime().toMillis()/media.getDuration().toMillis()*2000);
                                    }
                                });
                                mediaView.setMediaPlayer(mediaPlayer);
                                bCanPlay=true;
                                //mediaPlayer.addTimedTextSource("");
                                convertToWav();
                                
                            } catch (Exception ex) {
                                Logger.getLogger(MediaPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });
		
		slVolume=new Slider();
		slVolume.setPrefWidth(150);
		slVolume.setMaxWidth(Region.USE_PREF_SIZE);
		slVolume.setMinWidth(30);
		slVolume.setValue(50);
		
		hBox=new HBox(10);
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().addAll(endButton,backButton,playButton,foreButton,openButton,new Label("Volume"),slVolume);
		
		slProcess=new Slider();
		slProcess.setValue(0);
		slProcess.setMax(2000);
		
		slProcess.setOnMouseDragged(e->{
			if(bCanPlay){
				mediaPlayer.seek(new Duration(slProcess.getValue()/2000*media.getDuration().toMillis()));
			}
		});
		vBox=new VBox();
		vBox.getChildren().addAll(slProcess,hBox);
		vBox.setAlignment(Pos.CENTER);
		
		setCenter(mediaView);
		mediaView.fitWidthProperty().bind(widthProperty());
		mediaView.setOnMouseClicked(e->dealWithClick());
		setBottom(vBox);
	}
	
	public void setVideo(String url) throws UnsupportedEncodingException{
		MEDIA_URL=url;
                 MEDIA_URL= URLEncoder.encode(MEDIA_URL, "UTF-8");
	}
	
	protected void dealWithClick(){
		if(bCanPlay){
			if(playButton.getText().equals("PLAY")){
				mediaPlayer.play();
				playButton.setText("PAUSE");
			}
			else{
				mediaPlayer.pause();
				playButton.setText("PLAY");
			}
		}
	}
        
        
        public void convertToWav() throws IOException, IllegalArgumentException, EncoderException{
            target = new File("C:\\Users\\NEEL\\Documents\\NetBeansProjects\\subtitle\\src\\subtitle\\AudioFile.wav");
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("pcm_s16le");
            audio.setBitRate(16);
            audio.setChannels(1);
            audio.setSamplingRate(16000);
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setFormat("wav");
            attrs.setAudioAttributes(audio);
            it.sauronsoftware.jave.Encoder encoder = new it.sauronsoftware.jave.Encoder();
            encoder.encode(mediaFile, target, attrs);
                System.out.println(".wav file generated");
                createSubtitleFile();
        }
        
           public void createSubtitleFile() throws IOException{
            Configuration configuration = new Configuration();
            configuration.setAcousticModelPath("file:C:/Users/NEEL/Desktop/sphinx4-master/sphinx4-data/src/main/resources/edu/cmu/sphinx/models/en-us/en-us");
            configuration.setDictionaryPath("file:C:/Users/NEEL/Desktop/sphinx4-master/sphinx4-data/src/main/resources/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
            configuration.setLanguageModelPath("file:C:/Users/NEEL/Desktop/sphinx4-master/sphinx4-data/src/main/resources/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
            StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
            recognizer.startRecognition(new FileInputStream("C:\\Users\\NEEL\\Documents\\NetBeansProjects\\subtitle\\src\\subtitle\\AudioFile.wav"));
            SpeechResult result = null;
            String mediaFile_new=mediaFile.toString();
            String MEDIA_URL_NEW;
            MEDIA_URL_NEW = mediaFile_new.replace(mediaFile_new.substring(mediaFile_new.length()-4), "");
            File file = new File(MEDIA_URL_NEW+".srt");
            //File file = new File("C:\\Users\\NEEL\\Documents\\NetBeansProjects\\OfflineSubtitle\\src\\offlinesubtitle\\SubtitleFile.srt");
            FileWriter fr = new FileWriter(file, true);
            long subtitle_no=1;
            
            loop:
            while ((result = recognizer.getResult()) != null) {
            
                String word_result="";  
                fr.write(String.valueOf(subtitle_no));
                subtitle_no++;
                System.out.print("\n");                
                fr.write("\n");
                for (WordResult r : result.getWords()) {
                    word_result = word_result + r;  
                    
                }
                System.out.println(word_result);
                String hypothesis=result.getHypothesis();
                System.out.format("Hypothesis: %s\n", result.getHypothesis());  
                fr.write(hypothesis+"\n");
                System.out.print("\n"); 
                fr.write("\n");
            }
            System.out.println("SUBTITLE GENERATED");
            fr.close();
            recognizer.stopRecognition();
            target.delete();
        }
}
