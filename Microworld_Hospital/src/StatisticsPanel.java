import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.Color;

/**
 * Created by Stanso on 3/2/2015.
 */
public class StatisticsPanel extends JPanel {

   private JPanel contentPane;
   private JPanel waitingRoomPanel;
   private JPanel queueBriefPanel;
   private JPanel queueDetailedPanel;
   private JPanel statisticsPanel;
   private JPanel resourcesPanel;
   private JPanel scorePanel;
   private JLabel lblDocNum;
   private JLabel lblNrsNum;
   private JLabel lblSgnNum;
   private JLabel lblScoreVal;
   protected QueuePanel detailedQueuePanelA;
   protected QueuePanel detailedQueuePanelB;
   protected QueuePanel briefQueuePanel;

   private CardLayout queuePanelCard;

   private GameFrame linkedGameFrame;

   // Have to get locked
   private int scoreVal;
   private int docNum;
   private int nrsNum;
   private int sgnNum;
   protected int missedPatientA;
   protected int missedPatientB;

   final private Object docNumLock = new Object();
   final private Object nrsNumLock = new Object();
   final private Object sgnNumLock = new Object();
   final private Object scoreLock = new Object();

   /**
    * Inner class for patient queues
    */
   public class QueuePanel extends JPanel {

      private PatientType patType;
      private int queueLen = 0;
      private int missedPatient = 0;
      private final int queueMaxLen = MainFrame.CONFIG.QUEUE_MAX_LEN;
      private final int spareThrshld = MainFrame.CONFIG.QUEUE_SPARE_LEN;
      private final int crowdThrshld = MainFrame.CONFIG.QUEUE_CROWDED_LEN;
      private final int fullThrshld = MainFrame.CONFIG.QUEUE_FULL_LEN;
      private final Color sparseColor = Color.GREEN;
      private final Color crowdColor = Color.YELLOW;
      private final Color fullColor = Color.RED;

      private final int circleStartX = 4;
      private final int circleStartY = 3;
      private final int circleDist = 30;
      private final int circleDiameter = 18;

      private final int squareStartX = 0;
      private final int squareStartY = 0;
      private final int squareEdge = 39;

      boolean briefMode;

      public QueuePanel(PatientType type, boolean isBrief) {
         patType = type;
         scoreVal = 0;
         setBriefMode(isBrief);
         setVisible(true);
         setOpaque(false);
         paintComponents(null);
      }

      public void setLen(int num) {
         queueLen = num;
         int otherQueueLen = 0;
         if (briefMode) {
            otherQueueLen = 0;
         } else if (patType == PatientType.A) {
            otherQueueLen = detailedQueuePanelB.getQueueLen();
         } else if (patType == PatientType.B) {
            otherQueueLen = detailedQueuePanelA.getQueueLen();
         }

         if (num + otherQueueLen > queueMaxLen) {
            queueLen = queueMaxLen - otherQueueLen;
            if (!briefMode) {
               missedPatient += (num - (queueMaxLen - otherQueueLen));
               for (int i = 0; i < num - (queueMaxLen - otherQueueLen); i++) {
                  switch (patType) {
                     case A:
                        if (!linkedGameFrame.isAgent()) {
                           MicroworldHospital.writeLogLine("PlayerPatientMissed", "A", linkedGameFrame.isAgent());
                        } else {
                           MicroworldHospital.writeLogLine("AgentPatientMissed", "A", linkedGameFrame.isAgent());
                        }
                        break;
                     case B:
                        if (!linkedGameFrame.isAgent()) {
                           MicroworldHospital.writeLogLine("PlayerPatientMissed", "B", linkedGameFrame.isAgent());
                        } else {
                           MicroworldHospital.writeLogLine("AgentPatientMissed", "B", linkedGameFrame.isAgent());
                        }
                        break;
                     default:
                        throw new IllegalArgumentException("Invalid Patient Type");
                  }
               }
            } else {
               missedPatient = detailedQueuePanelA.getMissedPatient() + detailedQueuePanelB.getMissedPatient();
            }
         } else if (num < 0) {
            queueLen = 0;
         }
         refreshPaint();
      }

      public void incLen() {
         setLen(queueLen + 1);
      }

      public void decLen() {
         setLen(queueLen - 1);
      }

      public int getQueueLen() {
         return queueLen;
      }

      public int getMissedPatient() {
         return missedPatient;
      }

      private void setBriefMode(boolean val) {
         briefMode = val;
         setBorder(null);
         repaint();
      }

      public void refreshPaint() {
         detailedQueuePanelA.repaint();
         detailedQueuePanelB.repaint();
         briefQueuePanel.repaint();
      }

      public boolean isSparse() {
         int lenSum = detailedQueuePanelA.getQueueLen() + detailedQueuePanelB.getQueueLen();
         return (lenSum >= spareThrshld && lenSum < crowdThrshld);
      }

      public boolean isCrowd() {
         int lenSum = detailedQueuePanelA.getQueueLen() + detailedQueuePanelB.getQueueLen();
         return (lenSum >= crowdThrshld && lenSum < fullThrshld);
      }

      public boolean isFull() {
         int lenSum = detailedQueuePanelA.getQueueLen() + detailedQueuePanelB.getQueueLen();
         return (lenSum >= fullThrshld && lenSum <= queueMaxLen);
      }

      @Override
      public void paintComponent(Graphics g) {
         //super.paintComponent(g);
         Graphics2D g2 = (Graphics2D) g;
         RenderingHints rh = new RenderingHints(
               RenderingHints.KEY_ANTIALIASING,
               RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setRenderingHints(rh);

         Color currColor = sparseColor;
         if (isSparse()) {
            currColor = sparseColor;
         } else if (isCrowd()) {
            currColor = crowdColor;
         } else if (isFull()) {
            currColor = fullColor;
         }

         if (!briefMode) {
            for (int i = 0; i < queueLen; i++) {
               g2.setColor(currColor);
               g2.fillOval(circleStartX + i * circleDist, circleStartY, circleDiameter, circleDiameter);
               g2.setColor(Color.BLACK);
               g2.drawOval(circleStartX + i * circleDist, circleStartY, circleDiameter, circleDiameter);
            }
         } else {
            g2.setColor(currColor);
            g2.fillRect(squareStartX, squareStartY, squareEdge, squareEdge);
            g2.setColor(Color.BLACK);
            g2.drawRect(squareStartX, squareStartY, squareEdge, squareEdge);
         }
      }
   }

   public StatisticsPanel() {
      /* Value Init */
      linkedGameFrame = null;

      missedPatientA = 0;
      missedPatientB = 0;

      scoreVal = 0;
      docNum = 0;
      nrsNum = 0;
      sgnNum = 0;

      /* Window Setup */
      queuePanelCard = new CardLayout();
      waitingRoomPanel.setLayout(queuePanelCard);

      waitingRoomPanel.add(queueBriefPanel, "queueBriefPanel");
      waitingRoomPanel.add(queueDetailedPanel, "queueDetailedPanel");

      setQueueDetailed();

      contentPane.setVisible(true);
      waitingRoomPanel.setVisible(true);
      statisticsPanel.setVisible(true);
   }

   public void setLinkedGameFrame(GameFrame gameFrame) {
      linkedGameFrame = gameFrame;
   }

   public void setTitle(String title) {
      TitledBorder border = (TitledBorder) contentPane.getBorder();
      border.setTitle(title);
      contentPane.repaint();
   }

   public void setQueueDetailed() {
      queuePanelCard.show(waitingRoomPanel, "queueDetailedPanel");
   }

   public void setQueueBrief() {
      queuePanelCard.show(waitingRoomPanel, "queueBriefPanel");
   }

   public void setScore(int num) {
      synchronized (scoreLock) {
         scoreVal = num;
         lblScoreVal.setText(String.valueOf(scoreVal));
         updateLinkedStatsPanel();
      }
   }

   public void setResourceNum(ResourceType rType, int num) {
      if (num < 0) {
         System.out.println("TRIED TO SET RESOURCE NUM LESS THAN 0 !!!");
      }
      switch (rType) {
         case DOCTOR:
            synchronized (docNumLock) {
               docNum = num;
               if (num < 0) {
                  docNum = 0;
               }
               lblDocNum.setText(String.valueOf(docNum));
               updateButtons();
               updateLinkedStatsPanel();
            }
            break;
         case NURSE:
            synchronized (nrsNumLock) {
               nrsNum = num;
               if (num < 0) {
                  nrsNum = 0;
               }
               lblNrsNum.setText(String.valueOf(nrsNum));
               updateButtons();
               updateLinkedStatsPanel();
            }
            break;
         case SURGEON:
            synchronized (sgnNumLock) {
               sgnNum = num;
               if (num < 0) {
                  sgnNum = 0;
               }
               lblSgnNum.setText(String.valueOf(sgnNum));
               updateButtons();
               updateLinkedStatsPanel();
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid Resource Type");
      }

   }

   public void incResourceNum(ResourceType rType, int num) {
      // setResourceNum(rType, getResourceNum(rType) + num);
      switch (rType) {
         case DOCTOR:
            synchronized (docNumLock) {
               docNum += num;
               if (num < 0) {
                  docNum = 0;
               }
               lblDocNum.setText(String.valueOf(docNum));
               updateButtons();
               updateLinkedStatsPanel();
            }
            break;
         case NURSE:
            synchronized (nrsNumLock) {
               nrsNum += num;
               if (num < 0) {
                  nrsNum = 0;
               }
               lblNrsNum.setText(String.valueOf(nrsNum));
               updateButtons();
               updateLinkedStatsPanel();
            }
            break;
         case SURGEON:
            synchronized (sgnNumLock) {
               sgnNum += num;
               if (num < 0) {
                  sgnNum = 0;
               }
               lblSgnNum.setText(String.valueOf(sgnNum));
               updateButtons();
               updateLinkedStatsPanel();
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid Resource Type");
      }

   }

   public void decResourceNum(ResourceType rType, int num) {
      // setResourceNum(rType, getResourceNum(rType) - num);
      switch (rType) {
         case DOCTOR:
            synchronized (docNumLock) {
               docNum -= num;
               if (num < 0) {
                  docNum = 0;
               }
               lblDocNum.setText(String.valueOf(docNum));
               updateButtons();
               updateLinkedStatsPanel();
            }
            break;
         case NURSE:
            synchronized (nrsNumLock) {
               nrsNum -= num;
               if (num < 0) {
                  nrsNum = 0;
               }
               lblNrsNum.setText(String.valueOf(nrsNum));
               updateButtons();
               updateLinkedStatsPanel();
            }
            break;
         case SURGEON:
            synchronized (sgnNumLock) {
               sgnNum -= num;
               if (num < 0) {
                  sgnNum = 0;
               }
               lblSgnNum.setText(String.valueOf(sgnNum));
               updateButtons();
               updateLinkedStatsPanel();
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid Resource Type");
      }

   }

   public synchronized void setQueueLen(PatientType pType, int len) {
      switch (pType) {
         case A:
            detailedQueuePanelA.setLen(len);
            briefQueuePanel.setLen(detailedQueuePanelA.getQueueLen() + detailedQueuePanelB.getQueueLen());
            break;
         case B:
            detailedQueuePanelB.setLen(len);
            briefQueuePanel.setLen(detailedQueuePanelA.getQueueLen() + detailedQueuePanelB.getQueueLen());
            break;
         default:
            throw new IllegalArgumentException("Invalid Patient Type");
      }

      updateButtons();
      updateLinkedStatsPanel();
   }

   public synchronized void dequeue(PatientType pType) {
      setQueueLen(pType, getQueueLen(pType) - 1);
      updateButtons();
      updateLinkedStatsPanel();
   }

   public synchronized void enqueue(PatientType pType) {
      setQueueLen(pType, getQueueLen(pType) + 1);
      updateButtons();
      updateLinkedStatsPanel();
   }

   public synchronized int getQueueLen(PatientType pType) {
      switch (pType) {
         case A:
            return detailedQueuePanelA.getQueueLen();
         case B:
            return detailedQueuePanelB.getQueueLen();
         default:
            throw new IllegalArgumentException("Invalid Patient Type");
      }
   }

   public int getScore() {
      synchronized (scoreLock) {
         return scoreVal;
      }
   }

   public int getResourceNum(ResourceType rType) {
      switch (rType) {
         case DOCTOR:
            synchronized (docNumLock) {
               return docNum;
            }
         case NURSE:
            synchronized (nrsNumLock) {
               return nrsNum;
            }
         case SURGEON:
            synchronized (sgnNumLock) {
               return sgnNum;
            }
         default:
            throw new IllegalArgumentException("Invalid Resource Type");
      }
   }

   private void createUIComponents() {
      detailedQueuePanelA = new QueuePanel(PatientType.A, false);
      detailedQueuePanelB = new QueuePanel(PatientType.B, false);
      briefQueuePanel = new QueuePanel(null, true);
   }

   private void updateButtons() {
      if (linkedGameFrame == null) {
         return;
      }

      if (getResourceNum(ResourceType.DOCTOR) == 0) {
         if (MainFrame.CONFIG.isRequestMode()) {
            linkedGameFrame.assignDoctorButton.setEnabled(false);
                //if ((!linkedGameFrame.peerGameFrame.isAgent() && !MainFrame.CONFIG.isAutomatic()) || MainFrame.CONFIG.isHighCoopAgent(linkedGameFrame.getBhvNum()))
            //    linkedGameFrame.peerGameFrame.requestDoctorButton.setEnabled(false);
         } else if (MainFrame.CONFIG.isGivingMode()) {
            linkedGameFrame.assignDoctorButton.setEnabled(false);
            linkedGameFrame.giveDoctorButton.setEnabled(false);
         }
      } else {
         if (MainFrame.CONFIG.isRequestMode()) {
            linkedGameFrame.assignDoctorButton.setEnabled(true);
            linkedGameFrame.peerGameFrame.requestDoctorButton.setEnabled(true);
         } else if (MainFrame.CONFIG.isGivingMode()) {
            linkedGameFrame.assignDoctorButton.setEnabled(true);
            linkedGameFrame.giveDoctorButton.setEnabled(true);
         }
      }

      if (getResourceNum(ResourceType.NURSE) == 0) {
         if (MainFrame.CONFIG.isRequestMode()) {
            linkedGameFrame.assignNurseButton.setEnabled(false);
                //if((!linkedGameFrame.peerGameFrame.isAgent() && !MainFrame.CONFIG.isAutomatic()) || MainFrame.CONFIG.isHighCoopAgent(linkedGameFrame.getBhvNum()))
            //    linkedGameFrame.peerGameFrame.requestNurseButton.setEnabled(false);
         } else if (MainFrame.CONFIG.isGivingMode()) {
            linkedGameFrame.assignNurseButton.setEnabled(false);
            linkedGameFrame.giveNurseButton.setEnabled(false);
         }
      } else {
         if (MainFrame.CONFIG.isRequestMode()) {
            linkedGameFrame.assignNurseButton.setEnabled(true);
            linkedGameFrame.peerGameFrame.requestNurseButton.setEnabled(true);
         } else if (MainFrame.CONFIG.isGivingMode()) {
            linkedGameFrame.assignNurseButton.setEnabled(true);
            linkedGameFrame.giveNurseButton.setEnabled(true);
         }
      }

      if (getResourceNum(ResourceType.SURGEON) == 0) {
         if (MainFrame.CONFIG.isRequestMode()) {
            linkedGameFrame.assignSurgeonButton.setEnabled(false);
                // if((!linkedGameFrame.peerGameFrame.isAgent() && !MainFrame.CONFIG.isAutomatic()) || MainFrame.CONFIG.isHighCoopAgent(linkedGameFrame.getBhvNum()))
            //     linkedGameFrame.peerGameFrame.requestSurgeonButton.setEnabled(false);
         } else if (MainFrame.CONFIG.isGivingMode()) {
            linkedGameFrame.assignSurgeonButton.setEnabled(false);
            linkedGameFrame.giveSurgeonButton.setEnabled(false);
         }
      } else {
         if (MainFrame.CONFIG.isRequestMode()) {
            linkedGameFrame.assignSurgeonButton.setEnabled(true);
            linkedGameFrame.peerGameFrame.requestSurgeonButton.setEnabled(true);
         } else if (MainFrame.CONFIG.isGivingMode()) {
            linkedGameFrame.assignSurgeonButton.setEnabled(true);
            linkedGameFrame.giveSurgeonButton.setEnabled(true);
         }
      }

      if (getQueueLen(PatientType.A) == 0) {
         linkedGameFrame.assignPatientAButton.setEnabled(false);
      } else {
         linkedGameFrame.assignPatientAButton.setEnabled(true);
      }

      if (getQueueLen(PatientType.B) == 0) {
         linkedGameFrame.assignPatientBButton.setEnabled(false);
      } else {
         linkedGameFrame.assignPatientBButton.setEnabled(true);
      }
   }

   private void updateLinkedStatsPanel() {
      if (linkedGameFrame == null) {
         return;
      }

      //linkedGameFrame.getResLock().lock();
      StatisticsPanel linkedStatsPanel = linkedGameFrame.peerGameFrame.peerStatsPanel;
      linkedStatsPanel.setResourceNum(ResourceType.DOCTOR, this.getResourceNum(ResourceType.DOCTOR));
      linkedStatsPanel.setResourceNum(ResourceType.NURSE, this.getResourceNum(ResourceType.NURSE));
      linkedStatsPanel.setResourceNum(ResourceType.SURGEON, this.getResourceNum(ResourceType.SURGEON));
      linkedStatsPanel.setQueueLen(PatientType.A, this.getQueueLen(PatientType.A));
      linkedStatsPanel.setQueueLen(PatientType.B, this.getQueueLen(PatientType.B));
      linkedStatsPanel.setScore(this.getScore());

        // linkedGameFrame.getResLock().unlock();
        //linkedStatsPanel.detailedQueuePanelA = this.detailedQueuePanelA;
      //linkedStatsPanel.detailedQueuePanelB = this.detailedQueuePanelB;
      //linkedStatsPanel.briefQueuePanel = this.briefQueuePanel;
      //linkedStatsPanel.briefQueuePanel.repaint();
   }

   public boolean isFullQueue(PatientType pType) {
      if (pType == null) {
         return briefQueuePanel.isFull();
      }

      switch (pType) {
         case A:
            return detailedQueuePanelA.isFull();
         case B:
            return detailedQueuePanelB.isFull();
         default:
            throw new IllegalArgumentException("Invalid Patient Type");
      }
   }

   public boolean isCrowdedQueue(PatientType pType) {
      if (pType == null) {
         return briefQueuePanel.isCrowd();
      }
      switch (pType) {
         case A:
            return detailedQueuePanelA.isCrowd();
         case B:
            return detailedQueuePanelB.isCrowd();
         default:
            throw new IllegalArgumentException("Invalid Patient Type");
      }
   }

   public boolean isSpareQueue(PatientType pType) {
      if (pType == null) {
         return briefQueuePanel.isSparse();
      }
      switch (pType) {
         case A:
            return detailedQueuePanelA.isSparse();
         case B:
            return detailedQueuePanelB.isSparse();
         default:
            throw new IllegalArgumentException("Invalid Patient Type");
      }
   }
}