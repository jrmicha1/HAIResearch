import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The start up window Main timers are managed here Created by Stanso on
 * 3/8/2015.
 */
public final class MainFrame extends JFrame {

   protected final static Config CONFIG = new Config();
   private static Timer mainTimer;
   private static java.util.Timer taskTimer;
   protected static int timeCount;
   private static long startSysTime;
   protected static GameFrame plyrGameFrame;
   protected static GameFrame agntGameFrame;
   private static boolean isPracticeMode;
   private static boolean isAutomatic = false;//Currently there is no way to change this in-game; it just remains false.

   private static String userId;
   private static String condition;
   private static int trialNum;
   private static boolean visualizeAgntActivity = false; // Currently there is no way to change this in-game; it just
                                                         // remains false.

   private JButton startGameButton;
   private JPanel contentPane;
   private JLabel headerLabel;
   private JLabel descLabel;

   /**
    * Constructor
    */
   public MainFrame() {
      setUndecorated(true);

      /* Value Init */
      userId = "123"; //TODO: '123' is just a placeholder - Must set the actual User ID somehow!
      condition = "1"; //TODO:'1' is just a placeholder. What is 'condition'? Should either set it properly or remove it
      trialNum = 1; //TODO: 1 is just a placeholder. What is 'trial'? Should either set it properly or remove it
      isPracticeMode = true; // First run through the game is always practice

      // Load Initial Config File (i.e. the training config):
      File configFile = null;
      try{
         configFile = getIni("config\\PracticeConfig");
      }
      catch (IllegalArgumentException e) {
         System.err.println("ERROR: The training mode config file could not be loaded due to an invalid path!");
         e.printStackTrace();
         System.exit(1);
      }
      catch (FileNotFoundException e) {
         System.err.println("ERROR: The training mode config file could not be loaded; no .ini files in directory!");
         e.printStackTrace();
         System.exit(1);
      }
      CONFIG.loadSettings(configFile);

      /* Action Listeners */
      startGameButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            startGame();
         }
      });

      CONFIG.setAutomatic(isAutomatic);

      /* Window Setup */
      setContentPane(contentPane);
      pack();
      setLocationRelativeTo(null);
      setResizable(false);
      setTitle("Microworld Hospital");
      //setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      setVisible(true);

        //this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

      /* Window Listeners */
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            //System.out.println("Closed");
            //e.getWindow().dispose();
            //System.exit(0);
         }
//         @Override
//         public void windowIconified(WindowEvent e) {
//            setState(NORMAL);
//         }
      });
   }

   /**
    * Try to start the game
    */
   private void startGame() {
      plyrGameFrame = new GameFrame();
      agntGameFrame = new GameFrame();
      plyrGameFrame.initSetup(agntGameFrame, false);
      agntGameFrame.initSetup(plyrGameFrame, true);

      if (!CONFIG.isValid()) {
         JOptionPane.showMessageDialog(this, "Have not load/Invalid configuration file", "Error", JOptionPane.ERROR_MESSAGE);
         return;
      }

      if (userId.isEmpty() || userId == null) {
         JOptionPane.showMessageDialog(this, "Invalid userID!", "Error", JOptionPane.ERROR_MESSAGE);
         return;
      } else {
         MicroworldHospital.setUpLogFile(userId + "_" + condition + "_" + trialNum);
      }

      /* Main Timer Setup */
      // main (swing) timer for display
      timeCount = MainFrame.CONFIG.getTotalTimeMillis() / 1000;
      mainTimer = new javax.swing.Timer(1000, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            timeCount--;
            if (timeCount <= 0) {
               MainFrame.mainTimer.stop();
               MicroworldHospital.writeLogLine("GameOver", "", false);
               MicroworldHospital.writeLogLine("GameOver", "", true);
               agntGameFrame.mainTimerAction();
               plyrGameFrame.mainTimerAction();
               taskTimer.purge();
               MicroworldHospital.endLog();
               trialNum++;

               /* Current game has ended; transition to the next phase:
                *
                * If the previous game was practice mode, then set to play the full
                * 8-minute game next.
                *
                * If the previous game was the full 8-minute game, then end the program.
                */
               if(isPracticeMode()){
                  isPracticeMode = false;
                  headerLabel.setText("Full Game");
                  descLabel.setText("<html><div style=\"text-align:center;\">Play the full, 8-minute game!<br>(Data will be collected)</div></html>");

                  // Load Full Game Config File:
                  File configFile = null;
                  try{
                     configFile = getIni("config\\GameConfig");
                  }
                  catch (IllegalArgumentException ex) {
                     System.err.println("ERROR: The main game config file could not be loaded due to an invalid path!");
                     ex.printStackTrace();
                     System.exit(1);
                  }
                  catch (FileNotFoundException ex) {
                     System.err.println("ERROR: The main game config file could not be loaded; no .ini files in directory!");
                     ex.printStackTrace();
                     System.exit(1);
                  }
                  CONFIG.loadSettings(configFile);
               } else {
                  System.out.println("Training and 8-min full game completed, closing.");
                  System.exit(0);
               }

               setVisible(true);
            } else {
               plyrGameFrame.mainTimerAction();
               agntGameFrame.mainTimerAction();
            }
         }
      });

      // util timers for adding patients
      taskTimer = new java.util.Timer();
      for (int i = 0; i < CONFIG.INTERVAL_NUM; i++) {
         int plyrTaskTime = CONFIG.getTotalTimeMillis() / CONFIG.INTERVAL_NUM * i;
         for (int j = 0; j < CONFIG.getPlayerPatientInc(i); j++) {
            plyrTaskTime += CONFIG.getTotalTimeMillis() / CONFIG.INTERVAL_NUM / (CONFIG.getPlayerPatientInc(i) + 1);
            taskTimer.schedule(new java.util.TimerTask() {
               @Override
               public void run() {
                  plyrGameFrame.addPatient();
               }
            }, plyrTaskTime);
         }

         int agntTaskTime = CONFIG.getTotalTimeMillis() / CONFIG.INTERVAL_NUM * i;
         for (int j = 0; j < CONFIG.getAgentPatientInc(i); j++) {
            agntTaskTime += CONFIG.getTotalTimeMillis() / CONFIG.INTERVAL_NUM / (CONFIG.getAgentPatientInc(i) + 1);
            taskTimer.schedule(new java.util.TimerTask() {
               @Override
               public void run() {
                  agntGameFrame.addPatient();
               }
            }, agntTaskTime);
         }
      }


      /* Init Game */
      mainTimer.start();
      plyrGameFrame.start();
      if (visualizeAgntActivity) {
         agntGameFrame.setVisible(true);
      }
      agntGameFrame.start();

      startSysTime = System.currentTimeMillis();
      MicroworldHospital.writeLogLine("GameStart", "", false);
      MicroworldHospital.writeLogLine("GameStart", "", true);

      setVisible(false);
   }

   /**
    * Force end game
    */
   protected void forceEndGame() {
      mainTimer.stop();
      timeCount = 0;
      taskTimer.purge();
      plyrGameFrame.dispose();
      agntGameFrame.dispose();
      MicroworldHospital.endLog();
      System.out.println("Force Closed");
      this.setVisible(true);
   }

   /**
    * Get the time last from game begin (in millisecond)
    *
    * @return Time last in millisecond
    */
   public static long getTimeLastMillis() {
      long currSysTime = System.currentTimeMillis();
      return currSysTime - startSysTime;
   }

   /**
    * If the current game is for practice (No log)
    *
    * @return "true" on practice mode
    */
   public static boolean isPracticeMode() {
      return isPracticeMode;
   }

   /**
    * Gets the first .ini file found in the given directory
    * path and returns it as a File.
    * @param dir the directory to search
    * @return The first .ini file found in the dir
    * @throws IllegalArgumentException If dir is not a valid directory
    * @throws FileNotFoundException If no .ini files were found  in the directory
    */
   public static File getIni(String dir) throws IllegalArgumentException, FileNotFoundException{

      File configDir = new File(dir);

      if(!configDir.isDirectory()){
         throw new IllegalArgumentException("ERROR: Invalid directory path!");
      }

      File[] files = configDir.listFiles();
      File result = null;
      for(int i = 0; i < files.length; i++){
         if(files[i].getName().endsWith(".ini")){
            result = files[i];
            break;
         }
      }

      if(result == null){
         throw new FileNotFoundException("ERROR: Directory path did not contain any .ini files!");
      }

      return result;
   }
}