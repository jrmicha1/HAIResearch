import javax.swing.*;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Random;
import java.lang.StringBuffer;

/**
 * Main Class of the Game Created by Stanso on 2/28/2015.
 */
public class MicroworldHospital {

   static File file1, file2; // Log files to save to
   static PrintWriter fileOut1, fileOut2;  //  PrintWriter for logging
   static MainFrame mainFrame;
   private static String UID;

   /**
    * MAIN Entry of the program
    *
    * @param args UID
    */
   public static void main(String[] args) {

      try {
         UID = args[0];//Throws exception if no args
         File f = new File("ResearchData\\" + UID);
         if(!f.exists()){//If the directory does not already exist, something is wrong.
            //Jump to the catch block and generate "error" UID:
            throw new ArrayIndexOutOfBoundsException();
         }
      }
      catch(ArrayIndexOutOfBoundsException e) {
         //If no UID provided, generate "error" UID:
         //"error" UID x2 length of UID (64vs32 chars)
         //"error" UID only cap. letters, no spc or -
         Random r = new Random();
         StringBuffer s = new StringBuffer(64);
         for(int i = 0; i < 64; i++){
            s.append((char)(r.nextInt(26) + 'A'));
         }
         UID = s.toString();

         //If "error" UID, no CSV directory exists yet.
         //Create directory for CSV files:
         File f = new File("ResearchData\\" + UID);
         f.mkdir();
      }

      try {
         //System.out.println("Test");
         // set look and feel
         for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
         mainFrame = new MainFrame();
      } // Catch and print exceptions
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Initialize log file
    *
    * @return Success or fail
    */
   public static boolean setUpLogFile() {
      if (MainFrame.isPracticeMode()) {
         return true;
      }
      try {

         //CSV files for both player and agent will be saved to ResearchData\UID
         file1 = new File("ResearchData\\" + UID + "\\" + UID + "_P" + ".csv");
         file2 = new File("ResearchData\\" + UID + "\\" + UID + "_A" + ".csv");

         fileOut1 = new PrintWriter(file1);
         fileOut1.println("InputType,"
               + "InputParameter,"
               + "TimeInput,"
               + "PlayerNurse,"
               + "PlayerDoctor,"
               + "PlayerSurgeon,"
               + "AgentNurses,"
               + "AgentDoctors,"
               + "AgentSurgeons,"
               + "PlayerQueueA,"
               + "PlayerQueueB,"
               + "AgentQueueA,"
               + "AgentQueueB,"
               + "PlayerScore,"
               + "AgentScore,"
               + "GroupScore");
         fileOut2 = new PrintWriter(file2);
         fileOut2.println("InputType,"
               + "InputParameter,"
               + "TimeInput,"
               + "PlayerNurse,"
               + "PlayerDoctor,"
               + "PlayerSurgeon,"
               + "AgentNurses,"
               + "AgentDoctors,"
               + "AgentSurgeons,"
               + "PlayerQueueA,"
               + "PlayerQueueB,"
               + "AgentQueueA,"
               + "AgentQueueB,"
               + "PlayerScore,"
               + "AgentScore,"
               + "GroupScore");
      } catch (IOException e) {
         System.err.println("Fail to Write Log");
         e.printStackTrace();
         return false;
      }
      return true;
   }

   /**
    * Log event to log file
    *
    * @param event Event name
    * @param param Parameter follows with the event
    * @param isAgent Only write to file when this is "false"
    */
   public static void writeLogLine(String event, String param, boolean isAgent) {
      if (MainFrame.isPracticeMode()) {
         return;
      }

      if (!isAgent && fileOut1 != null) {
         fileOut1.println(event + ","
               + param + ","
               + MainFrame.getTimeLastMillis() / 1000.0 + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getResourceNum(ResourceType.NURSE) + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getResourceNum(ResourceType.DOCTOR) + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getResourceNum(ResourceType.SURGEON) + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getQueueLen(PatientType.A) + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getQueueLen(PatientType.B) + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getQueueLen(PatientType.A) + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getQueueLen(PatientType.B) + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getScore() + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getScore() + ","
               + (MainFrame.plyrGameFrame.thisStatsPanel.getScore() + MainFrame.plyrGameFrame.peerStatsPanel.getScore()));
      } else if (fileOut2 != null) {
         fileOut2.println(event + ","
               + param + ","
               + MainFrame.getTimeLastMillis() / 1000.0 + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getResourceNum(ResourceType.NURSE) + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getResourceNum(ResourceType.DOCTOR) + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getResourceNum(ResourceType.SURGEON) + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getQueueLen(PatientType.A) + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getQueueLen(PatientType.B) + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getQueueLen(PatientType.A) + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getQueueLen(PatientType.B) + ","
               + MainFrame.plyrGameFrame.thisStatsPanel.getScore() + ","
               + MainFrame.plyrGameFrame.peerStatsPanel.getScore() + ","
               + (MainFrame.plyrGameFrame.thisStatsPanel.getScore() + MainFrame.plyrGameFrame.peerStatsPanel.getScore()));
      }
   }

   /**
    * Close log file
    */
   public static void endLog() {
      if (fileOut1 != null) {
         fileOut1.close();
      }
      if (fileOut2 != null) {
         fileOut2.close();
      }
   }

   public static String getUID(){
      return UID;
   }
}
