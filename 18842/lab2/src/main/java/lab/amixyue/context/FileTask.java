package lab.amixyue.context;

import java.io.File;
import java.util.TimerTask;

import lombok.extern.log4j.Log4j;
/**
 * {@link FileTask}.setPath for load file from another path.
 * @author amy
 *
 */
@Log4j
public abstract class FileTask extends TimerTask {

	private File f;
	private long current;
	
	public FileTask(String path/*, Context context*/){
		f = new File(path);
		loadFileInfo();
	}
	
	@Override
	public abstract void run();
	
	private void loadFileInfo(){
		current = f.lastModified();
	}
	
	public boolean checkFileUpdate(){
		//log.debug("schedule!");
		if(current != f.lastModified()){
			//updated
			current = f.lastModified();
			return true;
		}
		return false;
	}

}
