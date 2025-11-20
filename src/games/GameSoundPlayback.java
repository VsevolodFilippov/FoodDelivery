package games;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import javax.sound.sampled.*;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameSoundPlayback implements Runnable{
	protected Random random;
	protected ArrayList<Path> directoryFilesList;
	static byte[] wavBytes;
	private AudioFormat af;	
	protected static File gameOverSound;	
	protected static File phoneSound;
	protected static File backgroundSound;
	protected static File ovenTimerSound;
	protected static File honkSound;
	protected static Clip gameOverSoundClip;
	protected static Clip phoneSoundClip;
	protected static Clip backgroundSoundClip;
	protected static Clip ovenTimerSoundClip;
	protected static Clip honkSoundClip;
	protected AudioInputStream gameOverAudioInputStream;	
	protected AudioInputStream phoneSoundAudioInputStream;
	protected AudioInputStream backgroundSoundAudioInputStream;
	protected AudioInputStream ovenTimerSoundAudioInputStream;
	protected AudioInputStream honkSoundAudioInputStream;
	protected static FloatControl gainControl;
	
	public GameSoundPlayback() {
		random = new Random();
		directoryFilesList = new ArrayList<Path>();
		gameOverSound = new File("sound\\playback\\gameOver.wav");		
		phoneSound = new File(filePathRandomizer(Path.of("sound\\ringtones")).toString());
		backgroundSound = new File("sound\\playback\\backgroundMusic.file");
		ovenTimerSound = new File(filePathRandomizer(Path.of("sound\\notifications")).toString());
		honkSound = new File(filePathRandomizer(Path.of("sound\\honk")).toString());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			gameOverAudioInputStream = AudioSystem.getAudioInputStream(gameOverSound);
			gameOverSoundClip = AudioSystem.getClip();			
			gameOverSoundClip.open(gameOverAudioInputStream);
			gainControl = (FloatControl) gameOverSoundClip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-10.0f);
						
			phoneSoundAudioInputStream = AudioSystem.getAudioInputStream(phoneSound);
			phoneSoundClip = AudioSystem.getClip();			
			phoneSoundClip.open(phoneSoundAudioInputStream);
			gainControl = (FloatControl) phoneSoundClip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-10.0f);
			
			
			try {
				wavBytes = convertWavToBytes(backgroundSound.toString());         
	        } catch (IOException e) {
	            System.err.println("Error converting file: " + e.getMessage());
	        }
			
	        af = new AudioFormat(44100, 16, 2, true, false);      
	        
			backgroundSoundClip = AudioSystem.getClip();
			backgroundSoundClip.open(af, wavBytes, 0, wavBytes.length);						        
			gainControl = (FloatControl) backgroundSoundClip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-15.0f); 
			backgroundSoundClip.loop(-1);
			
			ovenTimerSoundAudioInputStream = AudioSystem.getAudioInputStream(ovenTimerSound);
			ovenTimerSoundClip = AudioSystem.getClip();
			ovenTimerSoundClip.open(ovenTimerSoundAudioInputStream);
			
			honkSoundAudioInputStream = AudioSystem.getAudioInputStream(honkSound);
			honkSoundClip = AudioSystem.getClip();
			honkSoundClip.open(honkSoundAudioInputStream);	
			gainControl = (FloatControl) honkSoundClip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-10.0f);
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Path filePathRandomizer(Path p) {
		Path s;
		try(DirectoryStream<Path> dstr = Files.newDirectoryStream(p, "*.{wav,wave}")){
			for(Path entry: dstr) {
				directoryFilesList.add(entry.getName(p.getNameCount()));
			}
		}catch(InvalidPathException e) { 
		      System.out.println("Path Error " + e); 
	    } catch(NotDirectoryException e) { 
	      System.out.println(p + " is not a directory."); 
	    } catch (IOException e) { 
	      System.out.println("I/O Error: " + e); 
	    }
		
		int randomInt = random.nextInt(directoryFilesList.size());
		s = p.resolve(directoryFilesList.get(randomInt));
		directoryFilesList.clear();
		return s;
	}
	
	public static byte[] convertWavToBytes(String filePath) throws IOException {
        File wavFile = new File(filePath);
        Path path = wavFile.toPath();
        return Files.readAllBytes(path);
    }
}
