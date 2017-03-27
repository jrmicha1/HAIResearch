
import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The window for the game Created by Stanso on 2/28/2015.
 */
public class GameFrame extends JFrame {

   protected JPanel contentPane;
   protected StatisticsPanel thisStatsPanel;
   protected StatisticsPanel peerStatsPanel;
   private RoomPanel room1Panel;
   private RoomPanel room2Panel;
   private RoomPanel room3Panel;
   private RoomPanel room4Panel;
   private RoomPanel room5Panel;
   private RoomPanel room6Panel;
   protected JButton assignDoctorButton;
   protected JButton assignNurseButton;
   protected JButton assignSurgeonButton;
   protected JButton requestNurseButton;
   protected JButton requestDoctorButton;
   protected JButton requestSurgeonButton;
   protected JButton assignPatientAButton;
   private JLabel lblMainTimer;
   protected JButton assignPatientBButton;
   protected JButton giveDoctorButton;
   protected JButton giveNurseButton;
   protected JButton giveSurgeonButton;
   private JPanel coopBtnPanel;
   private JPanel rqstBtnPanel;
   private JPanel giveBtnPanel;
   private JPanel preGiveBtnPanel;
   private JButton preGiveButton;

   private JOptionPane msgOptionPane;
   private JInternalFrame msgInternalFrame;

   private CardLayout coopBtnCard;

   protected Activity buttonClicked;

   protected ArrayList<RoomPanel> roomPanelList;

   private boolean isAgent;

   protected GameFrame peerGameFrame;
   private GameFrame thisGameFrame = this;

   private long timeRecord;
   private int acceptReqCount;
   private int denialReqCount;
   private long responseTimeAvg;
   private long decisionTimeAvg;
   private long assignTimeAvg;

   private int giveDocPendNum;
   private int giveNrsPendNum;
   private int giveSgnPendNum;
   final static private ReentrantLock resLock = new ReentrantLock();

   private int patientAAddCount;
   private int patientBAddCount;

   private Behavior behavior;
   private int bhvNum;

   private Random rand;

   public GameFrame() {
      /* Value Init */
      //rand = new Random(System.currentTimeMillis());
      setUndecorated(true);

      rand = new Random();
      behavior = null;
      bhvNum = -1;

      acceptReqCount = 0;
      denialReqCount = 0;

      giveDocPendNum = 0;
      giveNrsPendNum = 0;
      giveSgnPendNum = 0;

      roomPanelList = new ArrayList<RoomPanel>();
      roomPanelList.add(room1Panel);
      roomPanelList.add(room2Panel);
      roomPanelList.add(room3Panel);
      roomPanelList.add(room4Panel);
      roomPanelList.add(room5Panel);
      roomPanelList.add(room6Panel);
      for (int i = 0; i < roomPanelList.size(); i++) {
         roomPanelList.get(i).setRoomNum(i + 1);
      }

      coopBtnCard = new CardLayout();
      coopBtnPanel.setLayout(coopBtnCard);

      coopBtnPanel.add(rqstBtnPanel, "rqstBtnPanel");
      coopBtnPanel.add(giveBtnPanel, "giveBtnPanel");
      coopBtnPanel.add(preGiveBtnPanel, "preGiveBtnPanel");

      if (MainFrame.CONFIG.isRequestMode()) {
         requestDoctorButton.setEnabled(true);
         requestNurseButton.setEnabled(true);
         requestSurgeonButton.setEnabled(true);
         giveDoctorButton.setEnabled(false);
         giveNurseButton.setEnabled(false);
         giveSurgeonButton.setEnabled(false);
         coopBtnCard.show(coopBtnPanel, "rqstBtnPanel");
      } else if (MainFrame.CONFIG.isGivingMode()) {
         requestDoctorButton.setEnabled(false);
         requestNurseButton.setEnabled(false);
         requestSurgeonButton.setEnabled(false);
         giveDoctorButton.setEnabled(true);
         giveNurseButton.setEnabled(true);
         giveSurgeonButton.setEnabled(true);
         coopBtnCard.show(coopBtnPanel, "preGiveBtnPanel");
      }

      /* Window Listener */
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
                //peerGameFrame.dispose();
            //MicroworldHospital.endLog();

                //System.out.println("Closed");
            //MicroworldHospital.mainFrame.setVisible(true);
            //e.getWindow().dispose();

            //MicroworldHospital.mainFrame.forceEndGame();
         }
      });

      buttonClicked = Activity.NOTHING_CLICKED;

      /* Action Listeners */
      // Assign Patient Action Listeners
      assignPatientAButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            buttonClicked = Activity.ASSIGN_PATIENT_A_CLICKED;
            coopBtnCard.show(coopBtnPanel, "preGiveBtnPanel");
         }
      });
      assignPatientBButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            buttonClicked = Activity.ASSIGN_PATIENT_B_CLICKED;
            coopBtnCard.show(coopBtnPanel, "preGiveBtnPanel");
         }
      });

      // Assign Resources Action Listeners
      assignDoctorButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            buttonClicked = Activity.ASSIGN_DOCTOR_CLICKED;
            coopBtnCard.show(coopBtnPanel, "preGiveBtnPanel");
         }
      });
      assignNurseButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            buttonClicked = Activity.ASSIGN_NURSE_CLICKED;
            coopBtnCard.show(coopBtnPanel, "preGiveBtnPanel");
         }
      });
      assignSurgeonButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            buttonClicked = Activity.ASSIGN_SURGEON_CLICKED;
            coopBtnCard.show(coopBtnPanel, "preGiveBtnPanel");
         }
      });

      // Request Resources Action Listeners
      requestDoctorButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (!isAgent()) {
               MicroworldHospital.writeLogLine("PlayerRequest", "Doctor", isAgent());
            } else {
               MicroworldHospital.writeLogLine("AgentRequest", "Doctor", isAgent());
            }
            buttonClicked = Activity.REQUEST_DOCTOR_CLICKED;
            peerGameFrame.promptRequest(ResourceType.DOCTOR);
         }
      });
      requestNurseButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (!isAgent()) {
               MicroworldHospital.writeLogLine("PlayerRequest", "Nurse", isAgent());
            } else {
               MicroworldHospital.writeLogLine("AgentRequest", "Nurse", isAgent());
            }
            buttonClicked = Activity.REQUEST_NURSE_CLICKED;
            peerGameFrame.promptRequest(ResourceType.NURSE);
         }
      });
      requestSurgeonButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (!isAgent()) {
               MicroworldHospital.writeLogLine("PlayerRequest", "Surgeon", isAgent());
            } else {
               MicroworldHospital.writeLogLine("AgentRequest", "Surgeon", isAgent());
            }
            buttonClicked = Activity.REQUEST_SURGEON_CLICKED;
            peerGameFrame.promptRequest(ResourceType.SURGEON);
         }
      });

      // Give Out Resources Action Listeners
      giveDoctorButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            buttonClicked = Activity.GIVE_DOCTOR_CLICKED;
            promptGiveConfirm(ResourceType.DOCTOR);
            coopBtnCard.show(coopBtnPanel, "preGiveBtnPanel");
         }
      });
      giveNurseButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            buttonClicked = Activity.GIVE_NURSE_CLICKED;
            promptGiveConfirm(ResourceType.NURSE);
            coopBtnCard.show(coopBtnPanel, "preGiveBtnPanel");
         }
      });
      giveSurgeonButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            buttonClicked = Activity.GIVE_SURGEON_CLICKED;
            promptGiveConfirm(ResourceType.SURGEON);
            coopBtnCard.show(coopBtnPanel, "preGiveBtnPanel");
         }
      });

      preGiveButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            buttonClicked = Activity.PRE_GIVE_CLICKED;
            coopBtnCard.show(coopBtnPanel, "giveBtnPanel");
         }
      });

      /* Window Setup*/
      setContentPane(contentPane);
      pack();
      setLocationRelativeTo(null);
      //setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      setTitle("Microworld Hospital");
      setVisible(false);
   }

   protected void mainTimerAction() {
      if (MainFrame.timeCount <= 0) {
         lblMainTimer.setText("00:00");
         // Stop each room's timer
         for (int i = 0; i < roomPanelList.size(); i++) {
            roomPanelList.get(i).timeStop();
         }

         setVisible(false);
         // Display end game stats on user's GameFrame
         if (!isAgent) {
            JOptionPane.showMessageDialog(null, "Time's Up!\n" + "Total Score: "
                  + (thisStatsPanel.getScore() + peerStatsPanel.getScore())
                  + "\nYour Score: " + thisStatsPanel.getScore()
                  + "\nOneLife Medical's Score: " + peerStatsPanel.getScore());
         }
         dispose();
      } else {
         // Update Main Timer Display
         String timeDisp = "";
         if (MainFrame.timeCount / 60 < 10) {
            timeDisp += "0";
         }
         timeDisp += MainFrame.timeCount / 60 + ":";
         if (MainFrame.timeCount % 60 < 10) {
            timeDisp += "0";
         }
         timeDisp += MainFrame.timeCount % 60 + "";
         lblMainTimer.setText(timeDisp);
      }
   }

   /**
    *
    * @param pFrame Peer GameFrame Object
    * @param agentMode This (not the peer you set) frame is agent's GameFrame
    */
   public void initSetup(GameFrame pFrame, boolean agentMode) {
      this.isAgent = agentMode;
      peerGameFrame = pFrame;
      for (int i = 0; i < roomPanelList.size(); i++) {
         roomPanelList.get(i).setLinkedGameFrame(this);
      }

      thisStatsPanel.setLinkedGameFrame(this);
      thisStatsPanel.setQueueDetailed();
      if (!agentMode) {
         if (MainFrame.CONFIG.isAutomatic()) {
            bhvNum = 1;
            behavior = new Behavior(this);
         }
         setTitle("Hospital Management Game");
         thisStatsPanel.setTitle("<html><div style=\"text-align:center;\">Bayview Medical Clinic<br>(You)</div></html>");
         patientAAddCount = MainFrame.CONFIG.getPlayerPatientNum(PatientType.A);
         patientBAddCount = MainFrame.CONFIG.getPlayerPatientNum(PatientType.B);
         thisStatsPanel.setQueueLen(PatientType.A, MainFrame.CONFIG.getPlayerPatientNum(PatientType.A));
         thisStatsPanel.setQueueLen(PatientType.B, MainFrame.CONFIG.getPlayerPatientNum(PatientType.B));
         thisStatsPanel.setResourceNum(ResourceType.DOCTOR, MainFrame.CONFIG.getPlayerResourceNum(ResourceType.DOCTOR));
         thisStatsPanel.setResourceNum(ResourceType.NURSE, MainFrame.CONFIG.getPlayerResourceNum(ResourceType.NURSE));
         thisStatsPanel.setResourceNum(ResourceType.SURGEON, MainFrame.CONFIG.getPlayerResourceNum(ResourceType.SURGEON));
      } else {
         bhvNum = 2;
         behavior = new Behavior(this);
         setTitle("Hospital Management Game (Agent)");
         thisStatsPanel.setTitle("Agent's Panel");
         patientAAddCount = MainFrame.CONFIG.getAgentPatientNum(PatientType.A);
         patientBAddCount = MainFrame.CONFIG.getAgentPatientNum(PatientType.B);
         thisStatsPanel.setQueueLen(PatientType.A, MainFrame.CONFIG.getAgentPatientNum(PatientType.A));
         thisStatsPanel.setQueueLen(PatientType.B, MainFrame.CONFIG.getAgentPatientNum(PatientType.B));
         thisStatsPanel.setResourceNum(ResourceType.DOCTOR, MainFrame.CONFIG.getAgentResourceNum(ResourceType.DOCTOR));
         thisStatsPanel.setResourceNum(ResourceType.NURSE, MainFrame.CONFIG.getAgentResourceNum(ResourceType.NURSE));
         thisStatsPanel.setResourceNum(ResourceType.SURGEON, MainFrame.CONFIG.getAgentResourceNum(ResourceType.SURGEON));
      }

      if (!agentMode) {
         peerGameFrame.peerStatsPanel.setTitle("Player's Panel");
      } else {
         peerGameFrame.peerStatsPanel.setTitle("OneLife Medical Center");
      }
      peerGameFrame.peerStatsPanel.setQueueBrief();
   }

   public boolean isAgent() {
      return isAgent;
   }

   protected void clickAssignPatientButton(PatientType pType) {
      switch (pType) {
         case A:
            //assignPatientAButton.doClick();
            if (assignPatientAButton.isEnabled()) {
               buttonClicked = Activity.ASSIGN_PATIENT_A_CLICKED;
            }
            break;
         case B:
            //assignPatientBButton.doClick();
            if (assignPatientBButton.isEnabled()) {
               buttonClicked = Activity.ASSIGN_PATIENT_B_CLICKED;
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid Patient Type");
      }

      updateDecisionTime();
   }

   protected void clickAssignResourceButton(ResourceType rType) {
      switch (rType) {
         case DOCTOR:
            //assignDoctorButton.doClick();
            if (assignDoctorButton.isEnabled()) {
               buttonClicked = Activity.ASSIGN_DOCTOR_CLICKED;
            }
            break;
         case NURSE:
            //assignNurseButton.doClick();
            if (assignNurseButton.isEnabled()) {
               buttonClicked = Activity.ASSIGN_NURSE_CLICKED;
            }
            break;
         case SURGEON:
            //assignSurgeonButton.doClick();
            if (assignSurgeonButton.isEnabled()) {
               buttonClicked = Activity.ASSIGN_SURGEON_CLICKED;
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid Resource Type");
      }

      updateDecisionTime();
   }

   protected void clickRequestResourceButton(ResourceType rType) {
      switch (rType) {
         case DOCTOR:
            if (requestDoctorButton.isEnabled()) {
               if (!isAgent()) {
                  MicroworldHospital.writeLogLine("PlayerRequest", "Doctor", isAgent());
               } else {
                  MicroworldHospital.writeLogLine("AgentRequest", "Doctor", isAgent());
               }
               buttonClicked = Activity.REQUEST_DOCTOR_CLICKED;
               // cross call
               peerGameFrame.promptRequest(ResourceType.DOCTOR);
            }
            break;
         case NURSE:
            if (requestNurseButton.isEnabled()) {
               if (!isAgent()) {
                  MicroworldHospital.writeLogLine("PlayerRequest", "Nurse", isAgent());
               } else {
                  MicroworldHospital.writeLogLine("AgentRequest", "Nurse", isAgent());
               }
               buttonClicked = Activity.REQUEST_NURSE_CLICKED;
               peerGameFrame.promptRequest(ResourceType.NURSE);
            }
            break;
         case SURGEON:
            if (requestSurgeonButton.isEnabled()) {
               if (!isAgent()) {
                  MicroworldHospital.writeLogLine("PlayerRequest", "Surgeon", isAgent());
               } else {
                  MicroworldHospital.writeLogLine("AgentRequest", "Surgeon", isAgent());
               }
               buttonClicked = Activity.REQUEST_SURGEON_CLICKED;
               peerGameFrame.promptRequest(ResourceType.SURGEON);
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid Resource Type");
      }

      updateDecisionTime();
   }

   protected void clickGiveResourceButton(ResourceType rType) {
      switch (rType) {
         case DOCTOR:
            if (giveDoctorButton.isEnabled()) {
               buttonClicked = Activity.GIVE_DOCTOR_CLICKED;
               promptGiveConfirm(ResourceType.DOCTOR);
            }
            break;
         case NURSE:
            if (giveNurseButton.isEnabled()) {
               buttonClicked = Activity.GIVE_NURSE_CLICKED;
               promptGiveConfirm(ResourceType.NURSE);
            }
            break;
         case SURGEON:
            if (giveSurgeonButton.isEnabled()) {
               buttonClicked = Activity.GIVE_SURGEON_CLICKED;
               promptGiveConfirm(ResourceType.SURGEON);
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid Resource Type");
      }

      updateDecisionTime();
   }

   private synchronized void updateDecisionTime() {
      long measuredTime = System.currentTimeMillis() - timeRecord;
      if (measuredTime > MainFrame.CONFIG.VALID_TIME_MEASUREMENT) {
         if (decisionTimeAvg == 0) {
            decisionTimeAvg = measuredTime;
         } else {
            decisionTimeAvg = (decisionTimeAvg + measuredTime) / 2;
         }
      }
      timeRecord = System.currentTimeMillis();
   }

   protected synchronized void updateAssignmentTime() {
      long measuredTime = System.currentTimeMillis() - timeRecord;
      if (measuredTime > MainFrame.CONFIG.VALID_TIME_MEASUREMENT) {
         if (assignTimeAvg == 0) {
            assignTimeAvg = measuredTime;
         } else {
            assignTimeAvg = (assignTimeAvg + measuredTime) / 2;
         }
      }
      timeRecord = System.currentTimeMillis();
   }

   private synchronized void updateResponseTime() {
      long measuredTime = System.currentTimeMillis() - timeRecord;
      //if(measuredTime > MainFrame.CONFIG.VALID_TIME_MEASUREMENT)
      {
         if (responseTimeAvg == 0) {
            responseTimeAvg = measuredTime;
         } else {
            responseTimeAvg = (responseTimeAvg + measuredTime) / 2;
         }
      }
      timeRecord = System.currentTimeMillis();
   }

   public synchronized void addPatient() {
      if (rand.nextBoolean() && patientAAddCount <= MainFrame.CONFIG.getPlayerTotalPatientAdd()) {
         patientAAddCount++;
         thisStatsPanel.enqueue(PatientType.A);
         if (!isAgent()) {
            MicroworldHospital.writeLogLine("PlayerPatientAdded", "A", isAgent());
         } else {
            MicroworldHospital.writeLogLine("AgentPatientAdded", "A", isAgent());
         }
      } else if (patientBAddCount <= MainFrame.CONFIG.getAgentTotalPatientAdd()) {
         patientBAddCount++;
         thisStatsPanel.enqueue(PatientType.B);
         if (!isAgent()) {
            MicroworldHospital.writeLogLine("PlayerPatientAdded", "B", isAgent());
         } else {
            MicroworldHospital.writeLogLine("AgentPatientAdded", "B", isAgent());
         }
      }
   }

   /**
    * Prompt resource request on current GameFrame (Cross Call)
    *
    * @param rType Resource to request
    */
   protected void promptRequest(final ResourceType rType) {
        // New thread to make sure the request will not pause the game
      //new Thread(new Runnable() {
      //    @Override
      //    public void run()
      //    {
      boolean isAccepted;
      String res = "";

      switch (rType) {
         case DOCTOR:
            res = "DOCTOR";
            break;
         case NURSE:
            res = "NURSE";
            break;
         case SURGEON:
            res = "SURGEON";
            break;
         default:
            throw new IllegalArgumentException("Invalid Resource Type");
      }

      if (!isAgent() && !MainFrame.CONFIG.isAutomatic()) {
         int option;
         option = JOptionPane.showInternalConfirmDialog(contentPane, "The agent need a " + res
               + " from you, would you like to accept?", "Resource Request", JOptionPane.YES_NO_OPTION);
         timeRecord = System.currentTimeMillis();
         isAccepted = (option == JOptionPane.YES_OPTION);
         updateResponseTime();
      } else {
         timeRecord = System.currentTimeMillis();
         isAccepted = behavior.respondRequest(rType);
         updateResponseTime();
      }

      // not supposed to happen
      if (isAccepted && thisStatsPanel.getResourceNum(rType) <= 0) {
         isAccepted = false;
         if (!isAgent()) {
            JOptionPane.showInternalMessageDialog(contentPane, "You don't have " + res + "to give out!",
                  "Error", JOptionPane.ERROR_MESSAGE);
         }
      }

      if (isAccepted) {
         if (!isAgent()) {
            MicroworldHospital.writeLogLine("PlayerResponse", "Accept", isAgent());
         } else {
            MicroworldHospital.writeLogLine("AgentResponse", "Accept", isAgent());
         }

                    // critical section
         // System.err.println(Thread.currentThread().getId() + ": Self Lock: GameFrame: Prompt Request");
         thisGameFrame.getResLock().lock();

         acceptReqCount++;
         thisStatsPanel.decResourceNum(rType, 1);
         peerGameFrame.thisStatsPanel.incResourceNum(rType, 1);

         // System.err.println(Thread.currentThread().getId() + ": Self Unlock: GameFrame: Prompt Request");
         thisGameFrame.getResLock().unlock();

         if (!peerGameFrame.isAgent() && !MainFrame.CONFIG.isAutomatic()) {
            JOptionPane.showInternalMessageDialog(peerGameFrame.contentPane, "A " + res + " has given to you!");
         }
      } else {
         if (!isAgent()) {
            MicroworldHospital.writeLogLine("PlayerResponse", "Deny", isAgent());
         } else {
            MicroworldHospital.writeLogLine("AgentResponse", "Deny", isAgent());
         }

         denialReqCount++;
         if (!peerGameFrame.isAgent() && !MainFrame.CONFIG.isAutomatic()) {
            JOptionPane.showInternalMessageDialog(peerGameFrame.contentPane, "Your request of " + res + " has been denied.");
         }
      }

      if (peerGameFrame.isAgent()) {
         peerGameFrame.behavior.receiveResponse(isAccepted);
      }
            //}
      //}).start();
   }

   /**
    * Prompt confirmation on current GameFrame and notification on peer's
    * GameFrame (Self Call)
    *
    * @param rType Resource to give out
    */
   private void promptGiveConfirm(final ResourceType rType) {
      boolean isConfirmed = true;
      if (!isAgent() && !MainFrame.CONFIG.isAutomatic()) {
         String res = "";

         switch (rType) {
            case DOCTOR:
               res = "DOCTOR";
               break;
            case NURSE:
               res = "NURSE";
               break;
            case SURGEON:
               res = "SURGEON";
               break;
            default:
               throw new IllegalArgumentException("Invalid Resource Type");
         }

         int option;
         option = JOptionPane.showInternalConfirmDialog(contentPane, "Are you sure you want to send a " + res
               + " to OneLife Medical Center?", "Confirmation Required", JOptionPane.YES_NO_OPTION);
         timeRecord = System.currentTimeMillis();
         isConfirmed = (option == JOptionPane.YES_OPTION);
      }

      if (isConfirmed) {

         // System.err.println(Thread.currentThread().getId() + ": Self Lock: GameFrame: promptGiveConfirm");
         thisGameFrame.getResLock().lock();

         switch (rType) {
            case DOCTOR:
               // critical section

               thisStatsPanel.decResourceNum(rType, 1);
               peerGameFrame.thisStatsPanel.incResourceNum(rType, 1);
               giveDocPendNum++;
               if (!isAgent()) {
                  MicroworldHospital.writeLogLine("PlayerGive", "Doctor", isAgent());
               } else {
                  MicroworldHospital.writeLogLine("AgentGive", "Doctor", isAgent());
               }

               break;
            case NURSE:
               // critical section

               thisStatsPanel.decResourceNum(rType, 1);
               peerGameFrame.thisStatsPanel.incResourceNum(rType, 1);
               giveNrsPendNum++;
               if (!isAgent()) {
                  MicroworldHospital.writeLogLine("PlayerGive", "Nurse", isAgent());
               } else {
                  MicroworldHospital.writeLogLine("AgentGive", "Nurse", isAgent());
               }

               break;
            case SURGEON:
               // critical section

               thisStatsPanel.decResourceNum(rType, 1);
               peerGameFrame.thisStatsPanel.incResourceNum(rType, 1);
               giveSgnPendNum++;
               if (!isAgent()) {
                  MicroworldHospital.writeLogLine("PlayerGive", "Surgeon", isAgent());
               } else {
                  MicroworldHospital.writeLogLine("AgentGive", "Surgeon", isAgent());
               }

               break;
            default:
               throw new IllegalArgumentException("Invalid Resource Type");
         }

         // System.err.println(Thread.currentThread().getId() + ": Self Unlock: GameFrame: promptGiveConfirm");
         thisGameFrame.getResLock().unlock();

         if (peerGameFrame.isAgent()) {
            peerGameFrame.behavior.receiveResource();
         }

         if (!peerGameFrame.isAgent() && !MainFrame.CONFIG.isAutomatic()) {
            String msg = "";
            if (giveDocPendNum > 0) {
               msg += "DOCTOR";
               giveDocPendNum = 0;
            }
            if (giveNrsPendNum > 0) {
               msg += "NURSE";
               giveNrsPendNum = 0;
            }
            if (giveSgnPendNum > 0) {
               msg += "SURGEON";
               giveSgnPendNum = 0;
            }
            msgOptionPane = new JOptionPane("OneLife Medical Center gives you a " + msg + ".");
            msgInternalFrame = msgOptionPane.createInternalFrame(peerGameFrame.contentPane, "Notification");
            msgInternalFrame.pack();
            msgInternalFrame.show();
                            // Swap to the commented code for message aggregation
                            /*
             String msg = "";
             if (giveDocPendNum > 0) msg += "< " + giveDocPendNum + " Doctor(s) > ";
             if (giveNrsPendNum > 0) msg += "< " + giveNrsPendNum + " Nurse(s) > ";
             if (giveSgnPendNum > 0) msg += "< " + giveSgnPendNum + " Surgeon(s) > ";

             msgOptionPane = new JOptionPane("The neighbor hospital has sent you " + msg + ".");
             if (msgInternalFrame != null && msgInternalFrame.isShowing())
             {
             msgInternalFrame.dispose();
             }
             msgInternalFrame = msgOptionPane.createInternalFrame(peerGameFrame.contentPane, "Notification");
             msgInternalFrame.addInternalFrameListener(new InternalFrameListener()
             {
             @Override
             public void internalFrameOpened(InternalFrameEvent internalFrameEvent)
             {

             }

             @Override
             public synchronized void internalFrameClosing(InternalFrameEvent internalFrameEvent)
             {
             giveDocPendNum = giveNrsPendNum = giveSgnPendNum = 0;
             }

             @Override
             public void internalFrameClosed(InternalFrameEvent internalFrameEvent)
             {

             }

             @Override
             public void internalFrameIconified(InternalFrameEvent internalFrameEvent)
             {

             }

             @Override
             public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent)
             {

             }

             @Override
             public void internalFrameActivated(InternalFrameEvent internalFrameEvent)
             {

             }

             @Override
             public void internalFrameDeactivated(InternalFrameEvent internalFrameEvent)
             {

             }
             });
             msgInternalFrame.pack();
             msgInternalFrame.show();
             */
         }
      }
   }

   public void start() {
      timeRecord = System.currentTimeMillis();

      if (isAgent() || MainFrame.CONFIG.isAutomatic()) {
         if (!isAgent() && MainFrame.CONFIG.isAutomatic()) {
            setVisible(true);
         }
         new Thread(new Runnable() {
            @Override
            public void run() {
               behavior.start();
            }
         }).start();
      } else {
         setVisible(true);
      }
   }

   public int getAcceptRequestCount() {
      return acceptReqCount;
   }

   public int getDenialRequestCount() {
      return denialReqCount;
   }

   public long getResponseTimeAvgMillis() {
      return responseTimeAvg;
   }

   public long getDecisionTimeAvgMillis() {
      return decisionTimeAvg;
   }

   public long getAssignTimeAvgMillis() {
      return assignTimeAvg;
   }

   public int getBhvNum() {
      return bhvNum;
   }

   protected ReentrantLock getResLock() {
      return resLock;
   }
}
