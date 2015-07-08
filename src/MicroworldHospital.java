import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Main Class of the Game
 * Created by Stanso on 2/28/2015.
 */
public class MicroworldHospital
{
    static PrintWriter fileOut;  //  PrintWriter for logging
    static MainFrame mainFrame;

    /**
     * MAIN
     * Entry of the program
     * @param args Unused
     */
    public static void main(String[] args)
    {
        try
        {
            //System.out.println("Test");
            mainFrame = new MainFrame();
        }
        // Catch and print exceptions
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Initialize log file
     * @param filename Log file name
     * @param isAgent Only write to file when this is "false"
     * @return Success or fail
     */
    public static boolean setUpLogFile(String filename, boolean isAgent)
    {
        if(MainFrame.isPracticeMode() || isAgent)
            return true;
        try
        {
            fileOut = new PrintWriter(filename+".csv");
            fileOut.println("InputType," +
                    "InputParameter," +
                    "TimeInput," +
                    "PlayerNurse," +
                    "PlayerDoctor," +
                    "PlayerSurgeon," +
                    "AgentNurses," +
                    "AgentDoctors," +
                    "AgentSurgeons," +
                    "PlayerQueueA," +
                    "PlayerQueueB," +
                    "AgentQueueA," +
                    "AgentQueueB," +
                    "PlayerScore," +
                    "AgentScore," +
                    "GroupScore");
        }
        catch (IOException e)
        {
            System.err.println("Fail to Write Log");
            return false;
        }
        return true;
    }

    /**
     * Log event to log file
     * @param event Event name
     * @param param Parameter follows with the event
     * @param isAgent Only write to file when this is "false"
     */
    public static void writeLogLine(String event, String param, boolean isAgent)
    {
        if(MainFrame.isPracticeMode() || isAgent)
            return;

            if(fileOut != null)
                fileOut.println(event + "," +
                    param + "," +
                    MainFrame.getTimeLastMillis() / 1000.0 + "," +
                    MainFrame.plyrGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) + "," +
                    MainFrame.plyrGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) + "," +
                    MainFrame.plyrGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) + "," +
                    MainFrame.plyrGameFrame.peerStatsPanel.getResourceNum(ResourceType.NURSE) + "," +
                    MainFrame.plyrGameFrame.peerStatsPanel.getResourceNum(ResourceType.DOCTOR) + "," +
                    MainFrame.plyrGameFrame.peerStatsPanel.getResourceNum(ResourceType.SURGEON) + "," +
                    MainFrame.plyrGameFrame.thisStatsPanel.getQueueLen(PatientType.A) + "," +
                    MainFrame.plyrGameFrame.thisStatsPanel.getQueueLen(PatientType.B) + "," +
                    MainFrame.plyrGameFrame.peerStatsPanel.getQueueLen(PatientType.A) + "," +
                    MainFrame.plyrGameFrame.peerStatsPanel.getQueueLen(PatientType.B) + "," +
                    MainFrame.plyrGameFrame.thisStatsPanel.getScore() + "," +
                    MainFrame.plyrGameFrame.peerStatsPanel.getScore() + "," +
                    (MainFrame.plyrGameFrame.thisStatsPanel.getScore() + MainFrame.plyrGameFrame.peerStatsPanel.getScore()));

        // TODO: might be issue
    }

    /**
     * Close log file
     */
    public static void endLog()
    {
        if(fileOut != null)
            fileOut.close();
    }
}
