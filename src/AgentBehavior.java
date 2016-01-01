import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Created by Stanso on 3/9/2015.
 */
public class AgentBehavior
{

    private Random rand;
    private long responseDelay;
    private long assignDelay;
    private long decisionDelay;
    private int titForTatCount;
    private boolean plyrLastResponse;
    private boolean shouldRequest;

    private GameFrame linkedGameFrame;

    protected AgentBehavior()
    {
        /* Value Init */
        rand = new Random(System.currentTimeMillis());
        responseDelay = MainFrame.CONFIG.getAgentResponseDelayMillis();
        assignDelay = MainFrame.CONFIG.getAgentAssignDelayMillis();
        decisionDelay = MainFrame.CONFIG.getAgentDecisionDelayMillis();
        shouldRequest = true;
        titForTatCount = 0;
    }

    public void setLinkedGameFrame(GameFrame frame)
    {
        linkedGameFrame = frame;
    }

    protected void receiveResponse(boolean accepted)
    {
        plyrLastResponse = accepted;
        if(!plyrLastResponse && titForTatCount == 0)
            titForTatCount = MainFrame.CONFIG.getAgentTitForTatMem();
    }

    protected synchronized boolean respondRequest(ResourceType resType)
    {
        updateDelay();
        delay(responseDelay);

        if(titForTatCount != 0)
        {
            titForTatCount--;
            return false;
        }

        if(linkedGameFrame.thisStatsPanel.isFullQueue(null) && rand.nextDouble() < MainFrame.CONFIG.getAgentFullAcceptRate())
            return true;
        if(linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && rand.nextDouble() < MainFrame.CONFIG.getAgentCrowdedAcceptRate())
            return true;
        if(linkedGameFrame.thisStatsPanel.isSpareQueue(null) && rand.nextDouble() < MainFrame.CONFIG.getAgentSpareAcceptRate())
            return true;

        return false;
    }

    private void delay(long milisTime)
    {
        try
        {
            Thread.sleep(milisTime);
        }
        catch(Exception e)
        {
            System.err.println("Thread Interrupted");
        }
    }

    private void startRequestDelay()
    {
        Timer timer = new Timer(MainFrame.CONFIG.getAgentRequestDelayMillis(), new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                shouldRequest = true;
            }
        });

        shouldRequest = false;
        timer.setRepeats(false);
        timer.start();
    }

    private void updateDelay()
    {
        if(MainFrame.CONFIG.agentUsePlayerAvgDelay())
        {
            if(linkedGameFrame.peerGameFrame.getResponseTimeAvgMillis() != 0)
                responseDelay = linkedGameFrame.peerGameFrame.getResponseTimeAvgMillis();
            if(linkedGameFrame.peerGameFrame.getDecisionTimeAvgMillis() != 0)
                decisionDelay = linkedGameFrame.peerGameFrame.getDecisionTimeAvgMillis();
            if(linkedGameFrame.peerGameFrame.getAssignTimeAvgMillis() != 0)
                assignDelay = linkedGameFrame.peerGameFrame.getAssignTimeAvgMillis();
        }

    }

    private RoomPanel findRoomCollect()
    {

        for (RoomPanel room : linkedGameFrame.roomPanelList)
            if (room.needCollect())
                return room;
        return null;
    }

    private RoomPanel findEmptyRoom()
    {
        for(RoomPanel room: linkedGameFrame.roomPanelList)
            if(room.isEmpty())
                return room;
        return null;
    }

    /**
     *  Find a room that need only the specified one resource
     * @param rType The resource that required
     * @return The room which need the resource
     */
    private RoomPanel findRoom1R(ResourceType rType)
    {
        // Find room which only need the one exact resource
        switch (rType)
        {
            case DOCTOR:
                for(RoomPanel room: linkedGameFrame.roomPanelList)
                    if(room.needResource(ResourceType.DOCTOR) && !room.needResource(ResourceType.NURSE))
                        return room;
                break;
            case SURGEON:
                for(RoomPanel room: linkedGameFrame.roomPanelList)
                    if(room.needResource(ResourceType.SURGEON) && !room.needResource(ResourceType.NURSE))
                        return room;
                break;
            case NURSE:
                for(RoomPanel room: linkedGameFrame.roomPanelList)
                    if(room.needResource(ResourceType.NURSE) && !room.needResource(ResourceType.DOCTOR) && !room.needResource(ResourceType.SURGEON))
                        return room;
                break;
            default:
                throw new IllegalArgumentException("Invalid Resource Type");
        }

        return null;
    }

    /**
     *  Find a room that need the specified two resources
     * @param rType1 The first resource that required
     * @param rType2 The second resource that required
     * @return The room which need the resources
     */
    private RoomPanel findRoom2R(ResourceType rType1, ResourceType rType2)
    {
        if( (rType1 != ResourceType.DOCTOR && rType1 != ResourceType.SURGEON && rType1 != ResourceType.NURSE) ||
                (rType2 != ResourceType.DOCTOR && rType2 != ResourceType.SURGEON && rType2 != ResourceType.NURSE) )
            throw new IllegalArgumentException("Invalid Resource Type");

        for(RoomPanel room: linkedGameFrame.roomPanelList)
            if(room.needResource(rType1) && room.needResource(rType2))
                return room;

        return null;
    }

    private boolean needResource(ResourceType rType)
    {
        if(linkedGameFrame.thisStatsPanel.getResourceNum(rType) <= 0)
            for(RoomPanel room: linkedGameFrame.roomPanelList)
                if(room.needResource(rType))
                    return true;
        return false;
    }

    /**
     * Is the time have been long enough to make another request to the player
     * @return Should or not request
     */
    private boolean shouldRequest()
    {
        if(!shouldRequest)
            return false;
        if(MainFrame.CONFIG.isHighCoopAgent())
        {
            if(linkedGameFrame.peerGameFrame.thisStatsPanel.isFullQueue(null) && MainFrame.CONFIG.agentRequestWhenPeerFull())
                return true;
            else if(linkedGameFrame.peerGameFrame.thisStatsPanel.isCrowdedQueue(null) && MainFrame.CONFIG.agentRequestWhenPeerCrowded())
                return true;
            else if(linkedGameFrame.peerGameFrame.thisStatsPanel.isSpareQueue(null) && MainFrame.CONFIG.agentRequestWhenPeerSpare())
                return true;
        }
        else
            return true;
        return false;
    }

    /**
     * Compare the queue length between the agent's and the player's
     * @return 0 for equal, -1 for agent is disadvantage, 1 for agent is advantage
     */
    private int compareQueueLen()
    {
        if(linkedGameFrame.thisStatsPanel.isSpareQueue(null) && linkedGameFrame.peerStatsPanel.isSpareQueue(null) ||
                linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && linkedGameFrame.peerStatsPanel.isCrowdedQueue(null) ||
                linkedGameFrame.thisStatsPanel.isFullQueue(null) && linkedGameFrame.peerStatsPanel.isFullQueue(null))
            return 0;
        if(linkedGameFrame.thisStatsPanel.isSpareQueue(null) && (linkedGameFrame.peerStatsPanel.isCrowdedQueue(null) || linkedGameFrame.peerStatsPanel.isFullQueue(null)) ||
                linkedGameFrame.thisStatsPanel.isCrowdedQueue(null) && linkedGameFrame.peerStatsPanel.isFullQueue(null) )
            return 1;
        return -1;
    }

    protected void start()
    {
        while (true)
        {
            if(MainFrame.timeCount <= 0)
                break;

            updateDelay();
            delay(decisionDelay);

            RoomPanel room;

            // Find room which can be collected
            if ((room = findRoomCollect()) != null)
            {
                room.clickPanel();
                continue;
            }
            else if(!MainFrame.CONFIG.isGivingMode() || compareQueueLen() <= 0)
            {
                // Find empty room to assign patient
                if (/*linkedGameFrame.assignPatientAButton.isEnabled()*/ linkedGameFrame.thisStatsPanel.getQueueLen(PatientType.A) > 0 &&
                        linkedGameFrame.thisStatsPanel.getQueueLen(PatientType.A) > linkedGameFrame.thisStatsPanel.getQueueLen(PatientType.B) &&
                        (room = findEmptyRoom()) != null)
                {
                    //linkedGameFrame.assignPatientAButton.doClick();
                    linkedGameFrame.clickAssignPatientButton(PatientType.A);
                    delay(assignDelay);
                    room.clickPanel();
                    continue;
                }
                else if (/*linkedGameFrame.assignPatientBButton.isEnabled()*/ linkedGameFrame.thisStatsPanel.getQueueLen(PatientType.B) > 0 && (room = findEmptyRoom()) != null)
                {
                    //linkedGameFrame.assignPatientBButton.doClick();
                    linkedGameFrame.clickAssignPatientButton(PatientType.B);
                    delay(assignDelay);
                    room.clickPanel();
                    continue;
                }
            }

            linkedGameFrame.getResLock().lock();

            // Find room that need only one resource
            if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 0 && (room = findRoom1R(ResourceType.DOCTOR)) != null)
            {
                //linkedGameFrame.assignDoctorButton.doClick();
                linkedGameFrame.clickAssignResourceButton(ResourceType.DOCTOR);
                delay(assignDelay);
                room.clickPanel();
            }
            else if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0 && (room = findRoom1R(ResourceType.NURSE)) != null)
            {
                //linkedGameFrame.assignNurseButton.doClick();
                linkedGameFrame.clickAssignResourceButton(ResourceType.NURSE);
                delay(assignDelay);
                room.clickPanel();
            }
            else if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) > 0 && (room = findRoom1R(ResourceType.SURGEON)) != null)
            {
                //linkedGameFrame.assignSurgeonButton.doClick();
                linkedGameFrame.clickAssignResourceButton(ResourceType.SURGEON);
                delay(assignDelay);
                room.clickPanel();
            }
            // Find room that need two resources and the agent has both of the resources
            else if(linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 0 && linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0 &&
                    (room = findRoom2R(ResourceType.DOCTOR, ResourceType.NURSE)) != null)
            {
                //linkedGameFrame.assignDoctorButton.doClick();
                linkedGameFrame.clickAssignResourceButton(ResourceType.DOCTOR);
                delay(assignDelay);
                room.clickPanel();

                delay(assignDelay);

                //linkedGameFrame.assignNurseButton.doClick();
                linkedGameFrame.clickAssignResourceButton(ResourceType.NURSE);
                delay(assignDelay);
                room.clickPanel();
            }
            else if(linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) > 0 && linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0 &&
                    (room = findRoom2R(ResourceType.SURGEON, ResourceType.NURSE)) != null)
            {
                //linkedGameFrame.assignSurgeonButton.doClick();
                linkedGameFrame.clickAssignResourceButton(ResourceType.SURGEON);
                delay(assignDelay);
                room.clickPanel();

                delay(assignDelay);

                //linkedGameFrame.assignNurseButton.doClick();
                linkedGameFrame.clickAssignResourceButton(ResourceType.NURSE);
                delay(assignDelay);
                room.clickPanel();
            }

            // Request Mode
            else if (MainFrame.CONFIG.isRequestMode())
            {
                linkedGameFrame.peerGameFrame.getResLock().lock();

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

                linkedGameFrame.peerGameFrame.getResLock().unlock();
            }

            // Giving Mode
            else if(MainFrame.CONFIG.isGivingMode())
            {
                    linkedGameFrame.peerGameFrame.getResLock().lock();

                    if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) > 0 &&
                            linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.DOCTOR) == 0 &&
                            !needResource(ResourceType.DOCTOR) &&
                            findRoom2R(ResourceType.DOCTOR, ResourceType.NURSE) == null)
                    {
                        delay(assignDelay);
                        //linkedGameFrame.giveDoctorButton.doClick();
                        System.out.println("Agent clicked give doctor.");
                        linkedGameFrame.clickGiveResourceButton(ResourceType.DOCTOR);
                    }

                    else if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) > 0 &&
                            linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.NURSE) == 0 &&
                            !needResource(ResourceType.NURSE) &&
                            findRoom2R(ResourceType.DOCTOR, ResourceType.NURSE) == null &&
                            findRoom2R(ResourceType.SURGEON, ResourceType.NURSE) == null)
                    {
                        delay(assignDelay);
                        //linkedGameFrame.giveNurseButton.doClick();
                        System.out.println("Agent clicked give nurse.");
                        linkedGameFrame.clickGiveResourceButton(ResourceType.NURSE);
                    }

                    else if (linkedGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) > 0 &&
                            linkedGameFrame.peerGameFrame.thisStatsPanel.getResourceNum(ResourceType.SURGEON) == 0 &&
                            !needResource(ResourceType.SURGEON) &&
                            findRoom2R(ResourceType.SURGEON, ResourceType.NURSE) == null)
                    {
                        delay(assignDelay);
                        //linkedGameFrame.giveSurgeonButton.doClick();
                        System.out.println("Agent clicked give surgeon.");
                        linkedGameFrame.clickGiveResourceButton(ResourceType.SURGEON);
                    }

                    linkedGameFrame.peerGameFrame.getResLock().unlock();

            }

            linkedGameFrame.getResLock().unlock();

        }
    }
}
