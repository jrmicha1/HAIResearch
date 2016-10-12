
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Created by Stanso on 3/9/2015.
 */
public class Behavior {

   private Random rand;
   private long responseDelay;
   private long assignDelay;
   private long decisionDelay;
   private int titForTatCount;
   private boolean plyrLastResponse;
   private boolean shouldRequest;
   private int incomingResource;
   private boolean polite;

   private GameFrame linkedGameFrame;

   protected Behavior(GameFrame frame) {
      /* Value Init */
      setLinkedGameFrame(frame);
      rand = new Random(System.currentTimeMillis());
      responseDelay = MainFrame.CONFIG.getAgentResponseDelayMillis(linkedGameFrame.getBhvNum());
      assignDelay = MainFrame.CONFIG.getAgentAssignDelayMillis(linkedGameFrame.getBhvNum());
      decisionDelay = MainFrame.CONFIG.getAgentDecisionDelayMillis(linkedGameFrame.getBhvNum());
      shouldRequest = true;
      titForTatCount = 0;
      incomingResource = 0;
      polite = false;
   }

   public void setLinkedGameFrame(GameFrame frame) {
      linkedGameFrame = frame;
   }

   protected void receiveResponse(boolean accepted) {
      plyrLastResponse = accepted;
      if (!plyrLastResponse && titForTatCount == 0) {
         titForTatCount = MainFrame.CONFIG.getAgentTitForTatMem(linkedGameFrame.getBhvNum());
      }
   }

   protected synchronized boolean respondRequest(ResourceType resType) {
      updateDelay();
      delay(responseDelay);

      if (titForTatCount != 0) {
         titForTatCount--;
         return false;
      }

      if (linkedGameFrame.thisStatsPanel.isFullQueue(null) && rand.nextDouble() < MainFrame.CONFIG.getAgentFullAcceptRate(linkedGameFrame.getBhvNum())) {
         return true;
      }
      if (linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && rand.nextDouble() < MainFrame.CONFIG.getAgentCrowdedAcceptRate(linkedGameFrame.getBhvNum())) {
         return true;
      }
      if (linkedGameFrame.thisStatsPanel.isSpareQueue(null) && rand.nextDouble() < MainFrame.CONFIG.getAgentSpareAcceptRate(linkedGameFrame.getBhvNum())) {
         return true;
      }

      return false;
   }

   public void receiveResource() {
      incomingResource++;
      polite = true;
   }

   private void delay(long milisTime) {
      try {
         Thread.sleep(milisTime);
      } catch (Exception e) {
         System.err.println("Thread Interrupted");
      }
   }

   private void startRequestDelay() {
      Timer timer = new Timer(MainFrame.CONFIG.getAgentRequestDelayMillis(linkedGameFrame.getBhvNum()), new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            shouldRequest = true;
         }
      });

      shouldRequest = false;
      timer.setRepeats(false);
      timer.start();
   }

   private void updateDelay() {
      if (MainFrame.CONFIG.agentUsePlayerAvgDelay(linkedGameFrame.getBhvNum())) {
         if (linkedGameFrame.peerGameFrame.getResponseTimeAvgMillis() != 0) {
            responseDelay = linkedGameFrame.peerGameFrame.getResponseTimeAvgMillis();
         }
         if (linkedGameFrame.peerGameFrame.getDecisionTimeAvgMillis() != 0) {
            decisionDelay = linkedGameFrame.peerGameFrame.getDecisionTimeAvgMillis();
         }
         if (linkedGameFrame.peerGameFrame.getAssignTimeAvgMillis() != 0) {
            assignDelay = linkedGameFrame.peerGameFrame.getAssignTimeAvgMillis();
         }
      }

   }

   private RoomPanel findRoomCollect() {

      for (RoomPanel room : linkedGameFrame.roomPanelList) {
         if (room.needCollect()) {
            return room;
         }
      }
      return null;
   }

   private RoomPanel findEmptyRoom() {
      for (RoomPanel room : linkedGameFrame.roomPanelList) {
         if (room.isEmpty()) {
            return room;
         }
      }
      return null;
   }

   /**
    * Find a room that need only the specified one resource
    *
    * @param rType The resource that required
    * @return The room which need the resource
    */
   private RoomPanel findRoom1R(ResourceType rType) {
      if (rType != ResourceType.DOCTOR && rType != ResourceType.SURGEON && rType != ResourceType.NURSE) {
         throw new IllegalArgumentException("Invalid Resource Type");
      }

      for (RoomPanel room : linkedGameFrame.roomPanelList) {
         if (room.needResource(rType)) {
            return room;
         }
      }

      return null;
   }

   /**
    * Find a room that need the specified two resources
    *
    * @param rType1 The first resource that required
    * @param rType2 The second resource that required
    * @return The room which need the resources
    */
   private RoomPanel findRoom2R(ResourceType rType1, ResourceType rType2) {
      if ((rType1 != ResourceType.DOCTOR && rType1 != ResourceType.SURGEON && rType1 != ResourceType.NURSE)
            || (rType2 != ResourceType.DOCTOR && rType2 != ResourceType.SURGEON && rType2 != ResourceType.NURSE)) {
         throw new IllegalArgumentException("Invalid Resource Type");
      }

      for (RoomPanel room : linkedGameFrame.roomPanelList) {
         if (room.needResource(rType1) && room.needResource(rType2)) {
            return room;
         }
      }

      return null;
   }

   private boolean needResource(ResourceType rType) {
      if (linkedGameFrame.thisStatsPanel.getResourceNum(rType) <= 0) {
         for (RoomPanel room : linkedGameFrame.roomPanelList) {
            if (room.needResource(rType)) {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Is the time have been long enough to make another request to the player
    *
    * @return Should or not request
    */
   private boolean shouldRequest() {
      if (!shouldRequest) {
         return false;
      }
      if (MainFrame.CONFIG.isHighCoopAgent(linkedGameFrame.getBhvNum())) {
         if (linkedGameFrame.peerGameFrame.thisStatsPanel.isFullQueue(null) && MainFrame.CONFIG.agentRequestWhenPeerFull(linkedGameFrame.getBhvNum())) {
            return true;
         } else if (linkedGameFrame.peerGameFrame.thisStatsPanel.isCrowdedQueue(null) && MainFrame.CONFIG.agentRequestWhenPeerCrowded(linkedGameFrame.getBhvNum())) {
            return true;
         } else if (linkedGameFrame.peerGameFrame.thisStatsPanel.isSpareQueue(null) && MainFrame.CONFIG.agentRequestWhenPeerSpare(linkedGameFrame.getBhvNum())) {
            return true;
         }
      } else {
         return true;
      }
      return false;
   }

   /**
    * Compare the queue length between the agent's and the player's
    *
    * @return 0 for equal, -1 for agent is disadvantage, 1 for agent is
    * advantage
    */
   private int compareQueueLen() {
      if (linkedGameFrame.thisStatsPanel.isSpareQueue(null) && linkedGameFrame.peerStatsPanel.isSpareQueue(null)
            || linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && linkedGameFrame.peerStatsPanel.isCrowdedQueue(null)
            || linkedGameFrame.thisStatsPanel.isFullQueue(null) && linkedGameFrame.peerStatsPanel.isFullQueue(null)) {
         return 0;
      }
      if (linkedGameFrame.thisStatsPanel.isSpareQueue(null) && (linkedGameFrame.peerStatsPanel.isCrowdedQueue(null) || linkedGameFrame.peerStatsPanel.isFullQueue(null))
            || linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && linkedGameFrame.peerStatsPanel.isFullQueue(null)) {
         return 1;
      }
      return -1;
   }

   protected void start() {
      System.out.println(Thread.currentThread().getId() + ": CoopMode: " + MainFrame.CONFIG.isHighCoopAgent(linkedGameFrame.getBhvNum())
            + "  FullQAcptRate: " + MainFrame.CONFIG.getAgentFullAcceptRate(linkedGameFrame.getBhvNum())
            + "  CrowdQAcptRate: " + MainFrame.CONFIG.getAgentCrowdedAcceptRate(linkedGameFrame.getBhvNum())
            + "  SpareQAcptRate: " + MainFrame.CONFIG.getAgentSpareAcceptRate(linkedGameFrame.getBhvNum()));

      System.out.println("Polite: " + polite);

      while (true) {
         if (MainFrame.timeCount <= 0) {
            break;
         }

         updateDelay();

         delay(responseDelay * incomingResource);
         incomingResource = 0;

         RoomPanel room;

         double curRand = rand.nextDouble();
         double possibility = 0.00, holdPossibility = 0.00;
         boolean giveDoc = false, giveNrs = false, giveSgn = false, giveAny = false, giveMood = false,
               needDoc = false, needNrs = false, needSgn = false;

         // Find room which can be collected
         if ((room = findRoomCollect()) != null) {
            // Here to change for the collecting delay change the following line to: delay(time_in_milliseconds);
            delay(1000);
            room.clickPanel();
            System.out.println("Agent collected resources.");
            continue;
         } // Should this behavior remains? NO
         else /*if(!MainFrame.CONFIG.isGivingMode() || compareQueueLen() <= 0)*/ {
            delay(decisionDelay);
            System.out.println("Decision Delay: " + decisionDelay);
            System.out.println("Polite: " + polite);

            //Polite behavior
            if (linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) == 0
                  && linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) == 0
                  && linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) == 0) {
               giveMood = true;
               System.out.println("Give Mood: " + giveMood);
            }

            if (polite) {
               if (giveMood) {
                  holdPossibility = 0.50;
               } else {
                  holdPossibility = 1.00;
               }

               System.out.println("Agent's possibility of holding now is: " + holdPossibility);
            }

            // Find room that need only one resource and be polite based on possibility
            if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 0
                  && (room = findRoom1R(ResourceType.DOCTOR)) != null
                  && polite) {
               while (curRand < holdPossibility) {
                  System.out.println("Agent needs this resource. " + curRand + " > " + holdPossibility);
                  holdPossibility = holdPossibility - 0.05;
                  delay(assignDelay);
                  System.out.println("Agent's possibility of holding now is: " + holdPossibility);
                  System.out.println("Current random number is: " + curRand);
               }
               needDoc = true;
            } else if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0
                  && (room = findRoom1R(ResourceType.NURSE)) != null
                  && polite) {
               while (curRand < holdPossibility) {
                  System.out.println("Agent needs this resource. " + curRand + " > " + holdPossibility);
                  holdPossibility = holdPossibility - 0.05;
                  delay(assignDelay);
                  System.out.println("Agent's possibility of holding now is: " + holdPossibility);
               }
               needNrs = true;
            } else if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) > 0
                  && (room = findRoom1R(ResourceType.SURGEON)) != null
                  && polite) {
               while (curRand < holdPossibility) {
                  System.out.println("Agent needs this resource. " + curRand + " > " + holdPossibility);
                  holdPossibility = holdPossibility - 0.05;
                  delay(assignDelay);
                  System.out.println("Agent's possibility of holding now is: " + holdPossibility);
               }
               needSgn = true;
            } else if (!needDoc && !needNrs && !needSgn && polite) {
               while (curRand < holdPossibility) {
                  System.out.println("Agent needs this resource. " + curRand + " > " + holdPossibility);
                  holdPossibility = holdPossibility - 0.10;
                  delay(assignDelay);
                  System.out.println("Agent's possibility of holding now is: " + holdPossibility);
               }
            }

            // Find empty room to assign patient only if resources available and assign both resources
            if (linkedGameFrame.thisStatsPanel.getQueueLen(PatientType.A) > 0
                  && linkedGameFrame.thisStatsPanel.getQueueLen(PatientType.A) > linkedGameFrame.thisStatsPanel.getQueueLen(PatientType.B)
                  && linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 0
                  && linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0
                  && (room = findEmptyRoom()) != null) {
               linkedGameFrame.clickAssignPatientButton(PatientType.A);
               delay(assignDelay);
               room.clickPanel();
               System.out.println("Agent assigned Type A patient.");

               if ((room = findRoom2R(ResourceType.DOCTOR, ResourceType.NURSE)) != null) {
                  delay(assignDelay);
                  linkedGameFrame.clickAssignResourceButton(ResourceType.DOCTOR);
                  room.clickPanel();

                  linkedGameFrame.clickAssignResourceButton(ResourceType.NURSE);
                  room.clickPanel();

                  System.out.println("Agent assigned Doctor and Nurse to a room.");
               }

               continue;
            }

            if (linkedGameFrame.thisStatsPanel.getQueueLen(PatientType.B) > 0
                  && (room = findEmptyRoom()) != null) {
               // On # of A and # of B equals, fairly randomly pick one of the patient
               if (linkedGameFrame.thisStatsPanel.getQueueLen(PatientType.A) == linkedGameFrame.thisStatsPanel.getQueueLen(PatientType.B)) {
                  if (rand.nextBoolean()
                        && linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 0
                        && linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0) {
                     linkedGameFrame.clickAssignPatientButton(PatientType.A);
                     delay(assignDelay);
                     room.clickPanel();
                     System.out.println("Agent assigned Type A patient.");

                     if ((room = findRoom2R(ResourceType.DOCTOR, ResourceType.NURSE)) != null) {
                        delay(assignDelay);
                        linkedGameFrame.clickAssignResourceButton(ResourceType.DOCTOR);
                        room.clickPanel();

                        linkedGameFrame.clickAssignResourceButton(ResourceType.NURSE);
                        room.clickPanel();

                        System.out.println("Agent assigned Doctor and Nurse to a room.");
                     }

                     continue;
                  }
               }

               if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) > 0
                     && linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0) {
                  linkedGameFrame.clickAssignPatientButton(PatientType.B);
                  delay(assignDelay);
                  room.clickPanel();
                  System.out.println("Agent assigned Type B patient.");

                  if ((room = findRoom2R(ResourceType.SURGEON, ResourceType.NURSE)) != null) {
                     delay(assignDelay);
                     linkedGameFrame.clickAssignResourceButton(ResourceType.SURGEON);
                     room.clickPanel();

                     linkedGameFrame.clickAssignResourceButton(ResourceType.NURSE);
                     room.clickPanel();

                     System.out.println("Agent assigned Surgeon and Nurse to a room.");
                  }

                  continue;
               }
            }
         }

         // System.err.println(Thread.currentThread().getId() + ": Self Lock: Behavior");
         linkedGameFrame.getResLock().lock();

                // Request Mode
            /* else if (MainFrame.CONFIG.isRequestMode())
          {

          if(shouldRequest())
          {

          if (needResource(ResourceType.DOCTOR) && linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 0)
          {
          //linkedGameFrame.requestDoctorButton.doClick();
          linkedGameFrame.clickRequestResourceButton(ResourceType.DOCTOR);
          System.out.println("Agent clicked request doctor.");
          startRequestDelay();
          } else if (needResource(ResourceType.NURSE) && linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0)
          {
          //linkedGameFrame.requestNurseButton.doClick();
          linkedGameFrame.clickRequestResourceButton(ResourceType.NURSE);
          System.out.println("Agent clicked request nurse.");
          startRequestDelay();
          } else if (needResource(ResourceType.SURGEON) && linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) > 0)
          {
          //linkedGameFrame.requestSurgeonButton.doClick();
          linkedGameFrame.clickRequestResourceButton(ResourceType.SURGEON);
          System.out.println("Agent clicked request surgeon.");
          startRequestDelay();
          }
          }

          } */
         // Giving Mode
         if (MainFrame.CONFIG.isGivingMode()) {
            System.out.println("Agent considering giving.");
                //boolean giveDoc = false, giveNrs = false, giveSgn = false, giveAny = false, giveMood = false,
            //        needDoc = false, needNrs = false, needSgn = false; --> defined earlier
            int give = -1;

            if (MainFrame.CONFIG.isHighCoopAgent(linkedGameFrame.getBhvNum())) {
               if ((linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) == 0
                     || linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) == 0
                     || linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) == 0)
                     && (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 1
                     || linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 1
                     || linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) > 1)) {
                  giveMood = true;
               }

               if (this.linkedGameFrame.thisStatsPanel.isSpareQueue(null) && giveMood && !polite) {
                  possibility = 1.00;
               } else if (this.linkedGameFrame.thisStatsPanel.isSpareQueue(null) && giveMood && polite) {
                  possibility = 0.25;
               } else if (this.linkedGameFrame.thisStatsPanel.isSpareQueue(null) && !giveMood && !polite) {
                  possibility = 0.50;
               } else if (this.linkedGameFrame.thisStatsPanel.isSpareQueue(null) && !giveMood && polite) {
                  possibility = 0.125;
               } else if (this.linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && giveMood && !polite) {
                  possibility = 0.75;
               } else if (this.linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && giveMood && polite) {
                  possibility = 0.1875;
               } else if (this.linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && !giveMood && !polite) {
                  possibility = 0.375;
               } else if (this.linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && !giveMood && polite) {
                  possibility = 0.09375;
               } else if (this.linkedGameFrame.thisStatsPanel.isFullQueue(null) && giveMood && !polite) {
                  possibility = 0.50;
               } else if (this.linkedGameFrame.thisStatsPanel.isFullQueue(null) && giveMood && polite) {
                  possibility = 0.125;
               } else if (this.linkedGameFrame.thisStatsPanel.isFullQueue(null) && !giveMood && !polite) {
                  possibility = 0.25;
               } else if (this.linkedGameFrame.thisStatsPanel.isFullQueue(null) && !giveMood && polite) {
                  possibility = 0.0625;
               }

                    //double curRand = rand.nextDouble();
               System.out.println("Agent's possibility of giving is: " + possibility);

               System.out.println("Current random number is: " + curRand);

               if (curRand < possibility) {
                  if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0
                        && linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) == 0) {
                     giveNrs = true;
                  } else if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 0
                        && linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) == 0) {
                     giveDoc = true;
                  } else if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) > 0
                        && linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) == 0) {
                     giveSgn = true;
                  } else if (!giveDoc && !giveNrs && !giveSgn) {
                     if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0
                           && linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) == 1) {
                        giveNrs = true;
                     } else if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) > 0
                           && linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) == 1) {
                        giveSgn = true;
                     } else if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 0
                           && linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) == 1) {
                        giveDoc = true;
                     }
                  }
               }

            } else {
               /*if (linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) == 0 ||
                linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) == 0 ||
                linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) == 0)
                {
                giveMood = true;
                }

                if (this.linkedGameFrame.thisStatsPanel.isSpareQueue(null) && giveMood) possibility = 0.50;
                else if (this.linkedGameFrame.thisStatsPanel.isSpareQueue(null) && !giveMood)
                possibility = 0.25;

                else if (this.linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && giveMood)
                possibility = 0.25;
                else if (this.linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && !giveMood)
                possibility = 0.125;

                else if (this.linkedGameFrame.thisStatsPanel.isFullQueue(null)) possibility = 0.00; */

               if ((linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) == 0
                     || linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) == 0
                     || linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) == 0)
                     && (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 1
                     || linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 1
                     || linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) > 1)) {
                  giveMood = true;
               }

               if (this.linkedGameFrame.thisStatsPanel.isSpareQueue(null) && giveMood && !polite) {
                  possibility = 0.50;
               } else if (this.linkedGameFrame.thisStatsPanel.isSpareQueue(null) && giveMood && polite) {
                  possibility = 1.00;
               } else if (this.linkedGameFrame.thisStatsPanel.isSpareQueue(null) && !giveMood && !polite) {
                  possibility = 0.50;
               } else if (this.linkedGameFrame.thisStatsPanel.isSpareQueue(null) && !giveMood && polite) {
                  possibility = 0.50;
               } else if (this.linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && giveMood && !polite) {
                  possibility = 0.25;
               } else if (this.linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && giveMood && polite) {
                  possibility = 0.50;
               } else if (this.linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && !giveMood && !polite) {
                  possibility = 0.25;
               } else if (this.linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && !giveMood && polite) {
                  possibility = 0.25;
               } else if (this.linkedGameFrame.thisStatsPanel.isFullQueue(null) && giveMood && !polite) {
                  possibility = 0.00;
               } else if (this.linkedGameFrame.thisStatsPanel.isFullQueue(null) && giveMood && polite) {
                  possibility = 0.25;
               } else if (this.linkedGameFrame.thisStatsPanel.isFullQueue(null) && !giveMood && !polite) {
                  possibility = 0.00;
               } else if (this.linkedGameFrame.thisStatsPanel.isFullQueue(null) && !giveMood && polite) {
                  possibility = 0.00;
               }

               /* Check to see if any resource is needed by the player and store in giveAny
                if (linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) < 2 ||
                linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) < 2 ||
                linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) < 2) {
                giveAny = true;
                } */
                    //double curRand = rand.nextDouble();
               System.out.println("Agent's possibility of giving is: " + possibility);

               System.out.println("Current random number is: " + curRand);

               if (curRand < possibility /* && giveAny */) {
                  if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 0) {
                     giveDoc = true;
                  }

                  if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0) {
                     giveNrs = true;
                  }

                  if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) > 0) {
                     giveSgn = true;
                  }
               }
            }

            polite = false;

            if (giveDoc && giveNrs && giveSgn) {
               double randNum = rand.nextDouble();
               if (randNum < 1.0 / 3.0) {
                  give = 0;
               } else if (randNum < 2.0 / 3.0) {
                  give = 1;
               } else {
                  give = 2;
               }
            } else if (giveDoc && giveNrs) {
               if (rand.nextBoolean()) {
                  give = 0;
               } else {
                  give = 1;
               }
            } else if (giveNrs && giveSgn) {
               if (rand.nextBoolean()) {
                  give = 1;
               } else {
                  give = 2;
               }
            } else if (giveDoc && giveSgn) {
               if (rand.nextBoolean()) {
                  give = 0;
               } else {
                  give = 2;
               }
            } else if (giveDoc) {
               give = 0;
            } else if (giveNrs) {
               give = 1;
            } else if (giveSgn) {
               give = 2;
            }

            switch (give) {
               case 0:
                  System.out.println("Agent clicked give doctor.");
                  //linkedGameFrame.giveDoctorButton.doClick();
                  linkedGameFrame.clickGiveResourceButton(ResourceType.DOCTOR);
                  break;
               case 1:
                  System.out.println("Agent clicked give nurse.");
                  //linkedGameFrame.giveNurseButton.doClick();
                  linkedGameFrame.clickGiveResourceButton(ResourceType.NURSE);
                  break;
               case 2:
                  System.out.println("Agent clicked give surgeon.");
                  //linkedGameFrame.giveSurgeonButton.doClick();
                  linkedGameFrame.clickGiveResourceButton(ResourceType.SURGEON);
                  break;
               default:
            }
         }

         // System.err.println(Thread.currentThread().getId() + ": Self Unlock: Behavior");
         linkedGameFrame.getResLock().unlock();

      }
   }
}
