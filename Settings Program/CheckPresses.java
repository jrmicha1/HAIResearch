package networkDrive;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.concurrent.TimeUnit;

public class CheckPresses {
	
	File source, target;
		
    public void run(String sourceFolder, String targetFolder, boolean retryConnection, boolean continueChecking) throws InvalidPathException, IOException, InterruptedException {
    	
    	try {
    		source = new File(sourceFolder);
        	target = new File(targetFolder);
    	} catch (InvalidPathException e) {
			throw e;
		}
    	
    	if(source.exists() && target.exists()) {
	    	try {
				Transfer.copy(source, target);
			} catch (IOException e) {
				throw e;
			}
    	}
    	else {
    		if (retryConnection) {
    			try {
        		//Wait 5 minutes before returning to retry the connection in CheckConnectivity.java
        		TimeUnit.MINUTES.sleep(5);
    			} catch (InterruptedException e) {
    				throw e;
    			}
    		} else {
        		return;
    		}
    	}
    	
    	if (continueChecking) {
    		try {
	        	//Wait 1 minute before returning to continue checking for files in CheckConnectivity.java
				TimeUnit.MINUTES.sleep(1);
    		} catch (InterruptedException e) {
    			throw e;
    		}
    	} 
    	else {
    		return;
    	}
    }
}