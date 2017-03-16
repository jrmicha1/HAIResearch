package networkDrive;

import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;

public class Transfer {
	
	static JFrame message = new JFrame();
	
    public static void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    private static void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

	private static void copyFile(File source, File target) throws IOException {  
		message.setVisible(true);
		message.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	source.getAbsoluteFile().renameTo(new File(target.toString()));
    	message.setVisible(false);
    }
}