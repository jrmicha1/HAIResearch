import org.ini4j.Ini;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.IllegalFormatException;

/**
 * The class holds all game configurations Created by Stanso on 5/3/15.
 */
public class Config {
   /* CONSTANTS */

   public final int QUEUE_MAX_LEN = 6;
   public final int QUEUE_FULL_LEN = 5;
   public final int QUEUE_CROWDED_LEN = 3;
   public final int QUEUE_SPARE_LEN = 0;
   public final int INTERVAL_NUM = 5;
   public final long VALID_TIME_MEASUREMENT = 300; //ms

   /* Class Varibles */
   private boolean isValid;

   /* Config Varibles */
   // Overall
   private boolean automatic;
   private int gameMode;
   private int totTime;
   private int patACureTime;
   private int patBCureTime;
   // Player
   private int plyrPatANum;
   private int plyrPatBNum;
   private int plyrDocNum;
   private int plyrNrsNum;
   private int plyrSgnNum;
   private int plyrLowTpoInc;
   private int plyrMedTpoInc;
   private int plyrHighTpoInc;
   private int[] plyrItvl = new int[INTERVAL_NUM];
   // Agent
   private int agntPatANum;
   private int agntPatBNum;
   private int agntDocNum;
   private int agntNrsNum;
   private int agntSgnNum;
   private int agntLowTpoInc;
   private int agntMedTpoInc;
   private int agntHighTpoInc;
   private int[] agntItvl = new int[INTERVAL_NUM];
   //Agent Behaviors 1
   private int agntBhv1CoopMode;
   private int agntBhv1FullQueAcptRate;
   private int agntBhv1CrowdQueAcptRate;
   private int agntBhv1SpareQueAcptRate;
   private boolean agntBhv1SpareRequest;
   private boolean agntBhv1CrowdRequest;
   private boolean agntBhv1FullRequest;
   private int agntBhv1TitForTatMem;
   private boolean agntBhv1UseAvgDelay;
   private int agntBhv1DecisionDelay;
   private int agntBhv1AssignDelay;
   private int agntBhv1ResponseDelay;
   private int agntBhv1RequestDelay;
   //Agent Behaviors 2
   private int agntBhv2CoopMode;
   private int agntBhv2FullQueAcptRate;
   private int agntBhv2CrowdQueAcptRate;
   private int agntBhv2SpareQueAcptRate;
   private boolean agntBhv2SpareRequest;
   private boolean agntBhv2CrowdRequest;
   private boolean agntBhv2FullRequest;
   private int agntBhv2TitForTatMem;
   private boolean agntBhv2UseAvgDelay;
   private int agntBhv2DecisionDelay;
   private int agntBhv2AssignDelay;
   private int agntBhv2ResponseDelay;
   private int agntBhv2RequestDelay;

   /**
    * Config Dialog Constructor
    */
   public Config() {
      isValid = false;
   }

   public void setAutomatic(boolean auto) {
      automatic = auto;
   }

   public boolean isAutomatic() {
      return automatic;
   }

   /**
    * *******************************************************************************************************************
    */
   public int getPlayerPatientNum(PatientType pType) {
      switch (pType) {
         case A:
            return plyrPatANum;
         case B:
            return plyrPatBNum;
         default:
            throw new IllegalArgumentException("Invalid Patient Type");
      }
   }

   public int getPlayerResourceNum(ResourceType rType) {
      switch (rType) {
         case DOCTOR:
            return plyrDocNum;
         case NURSE:
            return plyrNrsNum;
         case SURGEON:
            return plyrSgnNum;
         default:
            throw new IllegalArgumentException("Invalid Resource Type");
      }
   }

   /**
    * Interval Start From 0 to (INTERVAL_NUM - 1)
    *
    * @param interval The interval chosen to get the patient increase
    * @return the patient increase for the selected interval
    */
   public int getPlayerPatientInc(int interval) {
      // interval Start from 0
      switch (plyrItvl[interval]) {
         case -1:
            return plyrLowTpoInc;
         case 0:
            return plyrMedTpoInc;
         case 1:
            return plyrHighTpoInc;
         default:
            throw new IllegalArgumentException("Invalid Tempo Type");
      }
   }

   public int getPlayerTotalPatientAdd() {
      int sum = getPlayerPatientNum(PatientType.A) + getPlayerPatientNum(PatientType.B);
      for (int i = 0; i < INTERVAL_NUM; i++) {
         sum += getPlayerPatientInc(i);
      }
      return sum;
   }

   /**
    * *******************************************************************************************************************
    */
   public int getAgentPatientNum(PatientType pType) {
      switch (pType) {
         case A:
            return agntPatANum;
         case B:
            return agntPatBNum;
         default:
            throw new IllegalArgumentException("Invalid Patient Type");
      }
   }

   public int getAgentResourceNum(ResourceType rType) {
      switch (rType) {
         case DOCTOR:
            return agntDocNum;
         case NURSE:
            return agntNrsNum;
         case SURGEON:
            return agntSgnNum;
         default:
            throw new IllegalArgumentException("Invalid Resource Type");
      }
   }

   /**
    * Interval Start From 0 to (INTERVAL_NUM - 1)
    *
    * @param interval The interval chosen to get the patient increase
    * @return the patient increase for the selected interval
    */
   public int getAgentPatientInc(int interval) {
      // interval Start from 0
      switch (agntItvl[interval]) {
         case -1:
            return agntLowTpoInc;
         case 0:
            return agntMedTpoInc;
         case 1:
            return agntHighTpoInc;
         default:
            throw new IllegalArgumentException("Invalid Tempo Type");
      }
   }

   public int getAgentTotalPatientAdd() {
      int sum = getAgentPatientNum(PatientType.A) + getAgentPatientNum(PatientType.B);
      for (int i = 0; i < INTERVAL_NUM; i++) {
         sum += getAgentPatientInc(i);
      }
      return sum;
   }

   /**
    * *******************************************************************************************************************
    */
   public boolean isHighCoopAgent(int bhvNum) {
      switch (bhvNum) {
         case 1: return (agntBhv1CoopMode == 1);
         case 2: return (agntBhv2CoopMode == 1);
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public boolean isLowCoopAgent(int bhvNum) {
      switch (bhvNum) {
         case 1: return (agntBhv1CoopMode == 0);
         case 2: return (agntBhv2CoopMode == 0);
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public double getAgentFullAcceptRate(int bhvNum) {
      switch (bhvNum) {
         case 1: return agntBhv1FullQueAcptRate * 0.01;
         case 2: return agntBhv2FullQueAcptRate * 0.01;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public double getAgentCrowdedAcceptRate(int bhvNum) {
      switch (bhvNum) {
         case 1: return agntBhv1CrowdQueAcptRate * 0.01;
         case 2: return agntBhv2CrowdQueAcptRate * 0.01;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public double getAgentSpareAcceptRate(int bhvNum) {
      switch (bhvNum) {
         case 1: return agntBhv1SpareQueAcptRate * 0.01;
         case 2: return agntBhv2SpareQueAcptRate * 0.01;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public int getAgentTitForTatMem(int bhvNum) {
      switch (bhvNum) {
         case 1: return agntBhv1TitForTatMem;
         case 2: return agntBhv2TitForTatMem;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public int getAgentDecisionDelayMillis(int bhvNum) {
      switch (bhvNum) {
         case 1: return agntBhv1DecisionDelay;
         case 2: return agntBhv2DecisionDelay;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public int getAgentAssignDelayMillis(int bhvNum) {
      switch (bhvNum) {
         case 1: return agntBhv1AssignDelay;
         case 2: return agntBhv2AssignDelay;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public int getAgentResponseDelayMillis(int bhvNum) {
      switch (bhvNum) {
         case 1: return agntBhv1ResponseDelay;
         case 2: return agntBhv2ResponseDelay;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public int getAgentRequestDelayMillis(int bhvNum) {
      switch (bhvNum) {
         case 1:
            return agntBhv1RequestDelay;
         case 2:
            return agntBhv2RequestDelay;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public boolean agentRequestWhenPeerFull(int bhvNum) {
      switch (bhvNum) {
         case 1:
            return agntBhv1FullRequest;
         case 2:
            return agntBhv2FullRequest;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public boolean agentRequestWhenPeerCrowded(int bhvNum) {
      switch (bhvNum) {
         case 1: return agntBhv1CrowdRequest;
         case 2: return agntBhv2CrowdRequest;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public boolean agentRequestWhenPeerSpare(int bhvNum) {
      switch (bhvNum) {
         case 1: return agntBhv1SpareRequest;
         case 2: return agntBhv2SpareRequest;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   public boolean agentUsePlayerAvgDelay(int bhvNum) {
      switch (bhvNum) {
         case 1: return agntBhv1UseAvgDelay;
         case 2: return agntBhv2UseAvgDelay;
         default:
            throw new IllegalArgumentException("Illegal Behavior Number");
      }
   }

   /**
    * *******************************************************************************************************************
    */
   public int getTotalTimeMillis() {
      return 1000 * totTime;
   }

   public int getCureTimeMillis(PatientType pType) {
      switch (pType) {
         case A:
            return 1000 * patACureTime;
         case B:
            return 1000 * patBCureTime;
         default:
            throw new IllegalArgumentException("Invalid Patient Type");
      }
   }

   public boolean isGivingMode() {
      return (gameMode == 1);
   }

   public boolean isRequestMode() {
      return (gameMode == 0);
   }

   /**
    * *******************************************************************************************************************
    */
   public boolean isValid() {
      return isValid;
   }

   private void validate() {
      if (gameMode < 0 || gameMode > 1) {
         throw new IllegalArgumentException("Section: " + "overall" + ", " + "Argument: " + "gameMode");
      }
      if (totTime < 0) {
         throw new IllegalArgumentException("Section: " + "overall" + ", " + "Argument: " + "totalTime");
      }
      if (patACureTime < 0) {
         throw new IllegalArgumentException("Section: " + "overall" + ", " + "Argument: " + "patientACureTime");
      }
      if (patBCureTime < 0) {
         throw new IllegalArgumentException("Section: " + "overall" + ", " + "Argument: " + "patientBCureTime");
      }

      if (plyrPatANum < 0) {
         throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "patientANum");
      }
      if (plyrPatBNum < 0) {
         throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "patientBNum");
      }
      if (plyrDocNum < 0) {
         throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "doctorNum");
      }
      if (plyrNrsNum < 0) {
         throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "nurseNum");
      }
      if (plyrSgnNum < 0) {
         throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "surgeonNum");
      }
      if (plyrLowTpoInc < 0) {
         throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "lowTempoInc");
      }
      if (plyrMedTpoInc < 0) {
         throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "medTempoInc");
      }
      if (plyrHighTpoInc < 0) {
         throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "highTempoInc");
      }
      for (int i = 0; i < plyrItvl.length; i++) {
         if (plyrItvl[i] < -1 || plyrItvl[i] > 1) {
            throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "interval" + (i + 1));
         }
      }

      if (agntPatANum < 0) {
         throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "patientANum");
      }
      if (agntPatBNum < 0) {
         throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "patientBNum");
      }
      if (agntDocNum < 0) {
         throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "doctorNum");
      }
      if (agntNrsNum < 0) {
         throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "nurseNum");
      }
      if (agntSgnNum < 0) {
         throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "surgeonNum");
      }
      if (agntLowTpoInc < 0) {
         throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "lowTempoInc");
      }
      if (agntMedTpoInc < 0) {
         throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "medTempoInc");
      }
      if (agntHighTpoInc < 0) {
         throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "highTempoInc");
      }
      for (int i = 0; i < agntItvl.length; i++) {
         if (agntItvl[i] < -1 || agntItvl[i] > 1) {
            throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "interval" + (i + 1));
         }
      }

      if (agntBhv1CoopMode < 0 || agntBhv1CoopMode > 1) {
         throw new IllegalArgumentException("Section: " + "agentBehaviors1" + ", " + "Argument: " + "cooperationMode");
      }
      if (agntBhv1DecisionDelay < 0) {
         throw new IllegalArgumentException("Section: " + "agentBehaviors1" + ", " + "Argument: " + "decisionDelay");
      }
      if (agntBhv1AssignDelay < 0) {
         throw new IllegalArgumentException("Section: " + "agentBehaviors1" + ", " + "Argument: " + "assignmentDelay");
      }
      if (agntBhv1ResponseDelay < 0) {
         throw new IllegalArgumentException("Section: " + "agentBehaviors1" + ", " + "Argument: " + "responseDelay");
      }

      if (gameMode == 0) {

         if (agntBhv1FullQueAcptRate < 0 || agntBhv1FullQueAcptRate > 100) {
            throw new IllegalArgumentException("Section: " + "agentBehaviors1" + ", " + "Argument: " + "fullQueueAcceptRate");
         }
         if (agntBhv1CrowdQueAcptRate < 0 || agntBhv1CrowdQueAcptRate > 100) {
            throw new IllegalArgumentException("Section: " + "agentBehaviors1" + ", " + "Argument: " + "crowdQueueAcceptRate");
         }
         if (agntBhv1SpareQueAcptRate < 0 || agntBhv1SpareQueAcptRate > 100) {
            throw new IllegalArgumentException("Section: " + "agentBehaviors1" + ", " + "Argument: " + "spareQueueAcceptRate");
         }
         if (agntBhv1TitForTatMem < 0) {
            throw new IllegalArgumentException("Section: " + "agentBehaviors1" + ", " + "Argument: " + "titForTatMemory");
         }
         if (agntBhv1RequestDelay < 0) {
            throw new IllegalArgumentException("Section: " + "agentBehaviors1" + ", " + "Argument: " + "requestDelay");
         }
      }

      if (isAutomatic()) {
         if (agntBhv2CoopMode < 0 || agntBhv2CoopMode > 1) {
            throw new IllegalArgumentException("Section: " + "agentBehaviors2" + ", " + "Argument: " + "cooperationMode");
         }
         if (agntBhv2DecisionDelay < 0) {
            throw new IllegalArgumentException("Section: " + "agentBehaviors2" + ", " + "Argument: " + "decisionDelay");
         }
         if (agntBhv2AssignDelay < 0) {
            throw new IllegalArgumentException("Section: " + "agentBehaviors2" + ", " + "Argument: " + "assignmentDelay");
         }
         if (agntBhv2ResponseDelay < 0) {
            throw new IllegalArgumentException("Section: " + "agentBehaviors2" + ", " + "Argument: " + "responseDelay");
         }

         if (gameMode == 0) {
            if (agntBhv2FullQueAcptRate < 0 || agntBhv2FullQueAcptRate > 100) {
               throw new IllegalArgumentException("Section: " + "agentBehaviors2" + ", " + "Argument: " + "fullQueueAcceptRate");
            }
            if (agntBhv2CrowdQueAcptRate < 0 || agntBhv2CrowdQueAcptRate > 100) {
               throw new IllegalArgumentException("Section: " + "agentBehaviors2" + ", " + "Argument: " + "crowdQueueAcceptRate");
            }
            if (agntBhv2SpareQueAcptRate < 0 || agntBhv2SpareQueAcptRate > 100) {
               throw new IllegalArgumentException("Section: " + "agentBehaviors2" + ", " + "Argument: " + "spareQueueAcceptRate");
            }
            if (agntBhv2TitForTatMem < 0) {
               throw new IllegalArgumentException("Section: " + "agentBehaviors2" + ", " + "Argument: " + "titForTatMemory");
            }
            if (agntBhv2RequestDelay < 0) {
               throw new IllegalArgumentException("Section: " + "agentBehaviors2" + ", " + "Argument: " + "requestDelay");
            }
         }
      }

      isValid = true;
   }

   public void loadSettings(File configFile) {
      try {
         Ini ini = new Ini(configFile);

         gameMode = ini.get("overall", "gameMode", int.class);
         totTime = ini.get("overall", "totalTime", int.class);
         patACureTime = ini.get("overall", "patientACureTime", int.class);
         patBCureTime = ini.get("overall", "patientBCureTime", int.class);

         plyrPatANum = ini.get("player", "patientANum", int.class);
         plyrPatBNum = ini.get("player", "patientBNum", int.class);
         plyrDocNum = ini.get("player", "doctorNum", int.class);
         plyrNrsNum = ini.get("player", "nurseNum", int.class);
         plyrSgnNum = ini.get("player", "surgeonNum", int.class);
         plyrLowTpoInc = ini.get("player", "lowTempoInc", int.class);
         plyrMedTpoInc = ini.get("player", "medTempoInc", int.class);
         plyrHighTpoInc = ini.get("player", "highTempoInc", int.class);
         plyrItvl[0] = ini.get("player", "interval1", int.class);
         plyrItvl[1] = ini.get("player", "interval2", int.class);
         plyrItvl[2] = ini.get("player", "interval3", int.class);
         plyrItvl[3] = ini.get("player", "interval4", int.class);
         plyrItvl[4] = ini.get("player", "interval5", int.class);

         agntPatANum = ini.get("agent", "patientANum", int.class);
         agntPatBNum = ini.get("agent", "patientBNum", int.class);
         agntDocNum = ini.get("agent", "doctorNum", int.class);
         agntNrsNum = ini.get("agent", "nurseNum", int.class);
         agntSgnNum = ini.get("agent", "surgeonNum", int.class);
         agntLowTpoInc = ini.get("agent", "lowTempoInc", int.class);
         agntMedTpoInc = ini.get("agent", "medTempoInc", int.class);
         agntHighTpoInc = ini.get("agent", "highTempoInc", int.class);
         agntItvl[0] = ini.get("agent", "interval1", int.class);
         agntItvl[1] = ini.get("agent", "interval2", int.class);
         agntItvl[2] = ini.get("agent", "interval3", int.class);
         agntItvl[3] = ini.get("agent", "interval4", int.class);
         agntItvl[4] = ini.get("agent", "interval5", int.class);

         agntBhv1CoopMode = ini.get("agentBehaviors1", "cooperationMode", int.class);
         agntBhv1UseAvgDelay = ini.get("agentBehaviors1", "useAvgDelay", boolean.class);
         agntBhv1DecisionDelay = ini.get("agentBehaviors1", "decisionDelay", int.class);
         agntBhv1AssignDelay = ini.get("agentBehaviors1", "assignmentDelay", int.class);
         agntBhv1ResponseDelay = ini.get("agentBehaviors1", "responseDelay", int.class);

         agntBhv2CoopMode = ini.get("agentBehaviors2", "cooperationMode", int.class);
         agntBhv2UseAvgDelay = ini.get("agentBehaviors2", "useAvgDelay", boolean.class);
         agntBhv2DecisionDelay = ini.get("agentBehaviors2", "decisionDelay", int.class);
         agntBhv2AssignDelay = ini.get("agentBehaviors2", "assignmentDelay", int.class);
         agntBhv2ResponseDelay = ini.get("agentBehaviors2", "responseDelay", int.class);

         if (gameMode == 0) {
            agntBhv1FullQueAcptRate = ini.get("agentBehaviors1", "fullQueueAcceptRate", int.class);
            agntBhv1CrowdQueAcptRate = ini.get("agentBehaviors1", "crowdQueueAcceptRate", int.class);
            agntBhv1SpareQueAcptRate = ini.get("agentBehaviors1", "spareQueueAcceptRate", int.class);
            if (agntBhv1CoopMode == 1) {
               agntBhv1SpareRequest = ini.get("agentBehaviors1", "spareRequest", boolean.class);
               agntBhv1CrowdRequest = ini.get("agentBehaviors1", "crowdRequest", boolean.class);
               agntBhv1FullRequest = ini.get("agentBehaviors1", "fullRequest", boolean.class);
            }
            agntBhv1TitForTatMem = ini.get("agentBehaviors1", "titForTatMemory", int.class);
            agntBhv1RequestDelay = ini.get("agentBehaviors1", "requestDelay", int.class);

            agntBhv2FullQueAcptRate = ini.get("agentBehaviors2", "fullQueueAcceptRate", int.class);
            agntBhv2CrowdQueAcptRate = ini.get("agentBehaviors2", "crowdQueueAcceptRate", int.class);
            agntBhv2SpareQueAcptRate = ini.get("agentBehaviors2", "spareQueueAcceptRate", int.class);
            if (agntBhv2CoopMode == 1) {
               agntBhv2SpareRequest = ini.get("agentBehaviors2", "spareRequest", boolean.class);
               agntBhv2CrowdRequest = ini.get("agentBehaviors2", "crowdRequest", boolean.class);
               agntBhv2FullRequest = ini.get("agentBehaviors2", "fullRequest", boolean.class);
            }
            agntBhv2TitForTatMem = ini.get("agentBehaviors2", "titForTatMemory", int.class);
            agntBhv2RequestDelay = ini.get("agentBehaviors2", "requestDelay", int.class);
         }

         validate();
      } catch (IllegalFormatException e) {
         JOptionPane.showMessageDialog(MicroworldHospital.mainFrame, "Illegal INI file format", "Error", JOptionPane.ERROR_MESSAGE);
      } catch (IllegalArgumentException e) {
         isValid = false;
         JOptionPane.showMessageDialog(MicroworldHospital.mainFrame, "Illegal value at " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      } catch (IOException e) {
         JOptionPane.showMessageDialog(MicroworldHospital.mainFrame, "Error opening the file", "Error", JOptionPane.ERROR_MESSAGE);
      }

   }
}
