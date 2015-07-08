import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * The start up window
 * Main timers are managed here
 * Created by Stanso on 3/8/2015.
 */
public final class MainFrame extends JFrame
{
    protected final static Config CONFIG = new Config();
    private static Timer mainTimer;
    protected static int timeCount;
    private static long startSysTime;
    protected static GameFrame plyrGameFrame;
    protected static GameFrame agntGameFrame;
    private static boolean isPracticeMode;

    private static int plyrIntervalCount;
    private static int agntIntervalCount;
    //private Timer intervalTimer;
    private static int plyrPatientAdded;
    private static int agntPatientAdded;
    private static Timer currPlyrPatientAddTimer;
    private static Timer currAgntPatientAddTimer;
    private static ArrayList<Timer> plyrPatientAddTimers;
    private static ArrayList<Timer> agntPatientAddTimers;
    //private static Iterator<Timer> plyrPatientAddIterator;
    //private static Iterator<Timer> agntPatientAddIterator;

    private JTextField userIdTextField;
    private JTextField conditionTextField;
    private JSpinner trialSpinner;
    private JButton startGameButton;
    private JCheckBox visualizeAgntActivityCheckBox;
    private JButton configButton;
    private JPanel contentPane;
    private JCheckBox practiceModeCheckBox;
    private JFileChooser configFileChooser;

    /**
     * Constructor
     */
    public MainFrame()
    {
        /* Value Init */
        trialSpinner.setModel(new SpinnerNumberModel(1, 1, null, 1));

        // Config File Chooser Init
        configFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        configFileChooser.setDialogTitle("Choose Game Configuration File");
        FileFilter filter = new FileNameExtensionFilter("Configuration File (*.ini)","ini");
        configFileChooser.setFileFilter(filter);

        /* Action Listeners */
        configButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(configFileChooser.showOpenDialog(MicroworldHospital.mainFrame) == JFileChooser.APPROVE_OPTION)
                    CONFIG.loadSettings(configFileChooser.getSelectedFile());
            }
        });
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        practiceModeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPracticeMode = practiceModeCheckBox.isSelected();
            }
        });

        isPracticeMode = practiceModeCheckBox.isSelected();

        /* Window Setup */
        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);
        setTitle("Microworld - Hospital Management Game");
        setVisible(true);

        //this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

        /* Window Listeners */
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closed");
                e.getWindow().dispose();
                System.exit(0);
            }
        });
    }

    /**
     * Try to start the game
     */
    private void startGame()
    {
        plyrGameFrame = new GameFrame();
        agntGameFrame = new GameFrame();
        plyrGameFrame.initSetup(agntGameFrame, false);
        agntGameFrame.initSetup(plyrGameFrame, true);

        if(!CONFIG.isValid())
        {
            JOptionPane.showMessageDialog(this, "Have not load/Invalid configuration file", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(userIdTextField.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "The userID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        else
        {
            MicroworldHospital.setUpLogFile(userIdTextField.getText()+"_"+conditionTextField.getText()+"_"+ trialSpinner.getValue(), false);
        }

        /* Main Timer Setup */
        timeCount = MainFrame.CONFIG.getTotalTimeMillis()/1000;
        mainTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                timeCount--;
                if(timeCount <= 0)
                {
                    currPlyrPatientAddTimer.stop();
                    currAgntPatientAddTimer.stop();
                    MainFrame.mainTimer.stop();
                    MicroworldHospital.writeLogLine("GameOver", "", false);
                    agntGameFrame.mainTimerAction();
                    plyrGameFrame.mainTimerAction();
                    //
                    /*
                    plyrPatientAddTimer.stop();
                    agntPatientAddTimer.stop();
                    intervalTimer.stop();
                    */
                    MicroworldHospital.endLog();
                    trialSpinner.getModel().setValue((Integer)trialSpinner.getValue() + 1);
                    setVisible(true);
                }
                else
                {
                    plyrGameFrame.mainTimerAction();
                    agntGameFrame.mainTimerAction();
                }
            }
        });


        /*
        // First Interval
        intervalCount = 1;
        plyrPatientAdded = 0;
        plyrPatientAddTimer = new Timer(CONFIG.getTotalTimeMillis() / CONFIG.INTERVAL_NUM / CONFIG.getPlayerPatientInc(1), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                plyrGameFrame.addPatient();
                if(++plyrPatientAdded >= CONFIG.getPlayerPatientInc(1))
                    plyrPatientAddTimer.stop();
            }
        });
        agntPatientAdded = 0;
        agntPatientAddTimer = new Timer(CONFIG.getTotalTimeMillis() / CONFIG.INTERVAL_NUM / CONFIG.getAgentPatientInc(1), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agntGameFrame.addPatient();
                if(++agntPatientAdded >= CONFIG.getAgentPatientInc(1))
                    agntPatientAddTimer.stop();
            }
        });
        plyrPatientAddTimer.start();
        agntPatientAddTimer.start();
        ++intervalCount;

        // Rest Intervals
        intervalTimer = new Timer(CONFIG.getTotalTimeMillis()/ CONFIG.INTERVAL_NUM, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                plyrPatientAdded = 0;
                plyrPatientAddTimer = new Timer(CONFIG.getTotalTimeMillis() / CONFIG.INTERVAL_NUM / CONFIG.getPlayerPatientInc(intervalCount), new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        plyrGameFrame.addPatient();
                        if(++plyrPatientAdded >= CONFIG.getPlayerPatientInc(intervalCount))
                            plyrPatientAddTimer.stop();
                    }
                });
                agntPatientAdded = 0;
                agntPatientAddTimer = new Timer(CONFIG.getTotalTimeMillis() / CONFIG.INTERVAL_NUM / CONFIG.getAgentPatientInc(intervalCount), new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        agntGameFrame.addPatient();
                        if(++agntPatientAdded >= CONFIG.getAgentPatientInc(intervalCount))
                            agntPatientAddTimer.stop();
                    }
                });
                plyrPatientAddTimer.start();
                agntPatientAddTimer.start();

                if(++intervalCount >= CONFIG.INTERVAL_NUM)
                    intervalTimer.stop();
            }
        });
        intervalTimer.start();
        */

        /* Patient Adding Timer Setup */
        plyrIntervalCount = 0;
        agntIntervalCount = 0;
        plyrPatientAdded = 0;
        agntPatientAdded = 0;
        plyrPatientAddTimers = new ArrayList<Timer>();
        agntPatientAddTimers = new ArrayList<Timer>();
        for(int i = 0; i < CONFIG.INTERVAL_NUM; i++)
        {
            Timer player = new Timer(CONFIG.getTotalTimeMillis() / CONFIG.INTERVAL_NUM / CONFIG.getPlayerPatientInc(i), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    plyrGameFrame.addPatient();
                    if(plyrIntervalCount +1 >= CONFIG.INTERVAL_NUM)
                        currPlyrPatientAddTimer.stop();
                    else if(++plyrPatientAdded >= CONFIG.getPlayerPatientInc(plyrIntervalCount))
                    {
                        currPlyrPatientAddTimer.stop();
                        plyrPatientAdded = 0;
                        currPlyrPatientAddTimer = plyrPatientAddTimers.get(++plyrIntervalCount);
                        currPlyrPatientAddTimer.start();
                        //plyrPatientAddTimers.get(plyrIntervalCount - 1).stop();
                    }
                }
            });
            plyrPatientAddTimers.add(player);

            Timer agent = new Timer(CONFIG.getTotalTimeMillis() / CONFIG.INTERVAL_NUM / CONFIG.getAgentPatientInc(i), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    agntGameFrame.addPatient();
                    if(agntIntervalCount +1 >= CONFIG.INTERVAL_NUM)
                        currAgntPatientAddTimer.stop();
                    else if(++agntPatientAdded >= CONFIG.getPlayerPatientInc(agntIntervalCount))
                    {
                        currAgntPatientAddTimer.stop();
                        agntPatientAdded = 0;
                        currAgntPatientAddTimer = agntPatientAddTimers.get(++agntIntervalCount);
                        currAgntPatientAddTimer.start();
                        //agntPatientAddTimers.get(agntIntervalCount - 1).stop();
                    }
                }
            });
            agntPatientAddTimers.add(agent);
        }
        currPlyrPatientAddTimer = plyrPatientAddTimers.get(0);
        currAgntPatientAddTimer = agntPatientAddTimers.get(0);

        /* Init Game */
        mainTimer.start();
        currPlyrPatientAddTimer.start();
        currAgntPatientAddTimer.start();
        plyrGameFrame.start();
        if(visualizeAgntActivityCheckBox.isSelected())
            agntGameFrame.setVisible(true);
        agntGameFrame.start();
        MicroworldHospital.writeLogLine("GameStart", "", false);

        setVisible(false);
        startSysTime = System.currentTimeMillis();
    }

    /**
     * Force end game
     */
    protected void forceEndGame()
    {
        currPlyrPatientAddTimer.stop();
        currAgntPatientAddTimer.stop();
        mainTimer.stop();
        timeCount = 0;
        plyrGameFrame.dispose();
        agntGameFrame.dispose();
        MicroworldHospital.endLog();
        System.out.println("Force Closed");
        this.setVisible(true);
    }

    /**
     * Get the time last from game begin (in millisecond)
     * @return Time last in millisecond
     */
    public static long getTimeLastMillis()
    {
        long currSysTime = System.currentTimeMillis();
        return currSysTime - startSysTime;
    }

    /**
     * If the current game is for practice (No log)
     * @return "true" on practice mode
     */
    public static boolean isPracticeMode()
    {
        return isPracticeMode;
    }
}
