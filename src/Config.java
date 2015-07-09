import org.ini4j.Ini;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.IllegalFormatException;

/**
 * The class holds all game configurations
 * Created by Stanso on 5/3/15.
 */
public class Config
{
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
    //Agent Behaviors
    private int agntBhvCoopMode;
    private int agntBhvFullQueAcptRate;
    private int agntBhvCrowdQueAcptRate;
    private int agntBhvSpareQueAcptRate;
    private boolean agntBhvSpareRequest;
    private boolean agntBhvCrowdRequest;
    private boolean agntBhvFullRequest;
    private int agntBhvTitForTatMem;
    private boolean agntBhvUseAvgDelay;
    private int agntBhvDecisionDelay;
    private int agntBhvAssignDelay;
    private int agntBhvResponseDelay;
    private int agntBhvRequestDelay;

    /**
     *  Config Dialog Constructor
     */
    public Config()
    {
        isValid = false;
    }


/**********************************************************************************************************************/
    public int getPlayerPatientNum(PatientType pType)
    {
        switch (pType)
        {
            case A:
                return plyrPatANum;
            case B:
                return plyrPatBNum;
            default:
                throw new IllegalArgumentException("Invalid Patient Type");
        }
    }

    public int getPlayerResourceNum(ResourceType rType)
    {
        switch (rType)
        {
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
     * @param interval The interval chosen to get the patient increase
     * @return the patient increase for the selected interval
     */
    public int getPlayerPatientInc(int interval)
    {
        // interval Start from 0
        switch(plyrItvl[interval])
        {
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

    public int getPlayerTotalPatientAdd()
    {
        int sum = getPlayerPatientNum(PatientType.A) + getPlayerPatientNum(PatientType.B);
        for(int i = 0; i < INTERVAL_NUM; i++)
            sum += getPlayerPatientInc(i);
        return sum;
    }

/**********************************************************************************************************************/
    public int getAgentPatientNum(PatientType pType)
    {
        switch (pType)
        {
            case A:
                return agntPatANum;
            case B:
                return agntPatBNum;
            default:
                throw new IllegalArgumentException("Invalid Patient Type");
        }
    }

    public int getAgentResourceNum(ResourceType rType)
    {
        switch (rType)
        {
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
     * @param interval The interval chosen to get the patient increase
     * @return the patient increase for the selected interval
     */
    public int getAgentPatientInc(int interval)
    {
        // interval Start from 0
        switch(agntItvl[interval])
        {
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

    public int getAgentTotalPatientAdd()
    {
        int sum = getAgentPatientNum(PatientType.A) + getAgentPatientNum(PatientType.B);
        for(int i = 0; i < INTERVAL_NUM; i++)
            sum += getAgentPatientInc(i);
        return sum;
    }

/**********************************************************************************************************************/
    public boolean isHighCoopAgent()
    {
        return (agntBhvCoopMode == 1);
    }

    public boolean isLowCoopAgent()
    {
        return (agntBhvCoopMode == 0);
    }

    public double getAgentFullAcceptRate()
    {
        return agntBhvFullQueAcptRate * 0.01;
    }

    public double getAgentCrowdedAcceptRate()
    {
        return agntBhvCrowdQueAcptRate * 0.01;
    }

    public double getAgentSpareAcceptRate()
    {
        return agntBhvSpareQueAcptRate * 0.01;
    }

    public int getAgentTitForTatMem()
    {
        return agntBhvTitForTatMem;
    }

    public int getAgentDecisionDelayMillis()
    {
        return agntBhvDecisionDelay;
    }

    public int getAgentAssignDelayMillis()
    {
        return agntBhvAssignDelay;
    }

    public int getAgentResponseDelayMillis()
    {
        return agntBhvResponseDelay;
    }

    public int getAgentRequestDelayMillis()
    {
        return agntBhvRequestDelay;
    }

    public boolean agentRequestWhenPeerFull()
    {
        return agntBhvFullRequest;
    }

    public boolean agentRequestWhenPeerCrowded()
    {
        return agntBhvCrowdRequest;
    }

    public boolean agentRequestWhenPeerSpare()
    {
        return agntBhvSpareRequest;
    }

    public boolean agentUsePlayerAvgDelay()
    {
        return agntBhvUseAvgDelay;
    }


/**********************************************************************************************************************/
    public int getTotalTimeMillis()
    {
        return 1000 * totTime;
    }

    public int getCureTimeMillis(PatientType pType)
    {
        switch (pType)
        {
            case A:
                return 1000*patACureTime;
            case B:
                return 1000*patBCureTime;
            default:
                throw new IllegalArgumentException("Invalid Patient Type");
        }
    }

    public boolean isGivingMode()
    {
        return (gameMode == 1);
    }

    public boolean isRequestMode()
    {
        return (gameMode == 0);
    }


/**********************************************************************************************************************/
    public boolean isValid()
    {
        return isValid;
    }

    private void validate()
    {
        if(gameMode < 0 || gameMode > 1)
            throw new IllegalArgumentException("Section: " + "overall" + ", " + "Argument: " + "gameMode");
        if(totTime < 0)
            throw new IllegalArgumentException("Section: " + "overall" + ", " + "Argument: " + "totalTime");
        if(patACureTime < 0)
            throw new IllegalArgumentException("Section: " + "overall" + ", " + "Argument: " + "patientACureTime");
        if(patBCureTime < 0)
            throw new IllegalArgumentException("Section: " + "overall" + ", " + "Argument: " + "patientBCureTime");

        if(plyrPatANum < 0)
            throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "patientANum");
        if(plyrPatBNum < 0)
            throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "patientBNum");
        if(plyrDocNum < 0)
            throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "doctorNum");
        if(plyrNrsNum < 0)
            throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "nurseNum");
        if(plyrSgnNum < 0)
            throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "surgeonNum");
        if(plyrLowTpoInc < 0)
            throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "lowTempoInc");
        if(plyrMedTpoInc < 0)
            throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "medTempoInc");
        if(plyrHighTpoInc < 0)
            throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "highTempoInc");
        for(int i = 0; i < plyrItvl.length; i++)
            if(plyrItvl[i] < -1 || plyrItvl[i] > 1)
                throw new IllegalArgumentException("Section: " + "player" + ", " + "Argument: " + "interval" + (i+1));

        if(agntPatANum < 0)
            throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "patientANum");
        if(agntPatBNum < 0)
            throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "patientBNum");
        if(agntDocNum < 0)
            throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "doctorNum");
        if(agntNrsNum < 0)
            throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "nurseNum");
        if(agntSgnNum < 0)
            throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "surgeonNum");
        if(agntLowTpoInc < 0)
            throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "lowTempoInc");
        if(agntMedTpoInc < 0)
            throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "medTempoInc");
        if(agntHighTpoInc < 0)
            throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "highTempoInc");
        for(int i = 0; i < agntItvl.length; i++)
            if(agntItvl[i] < -1 || agntItvl[i] > 1)
                throw new IllegalArgumentException("Section: " + "agent" + ", " + "Argument: " + "interval" + (i+1));

        if (agntBhvDecisionDelay < 0)
            throw new IllegalArgumentException("Section: " + "agentBehaviors" + ", " + "Argument: " + "decisionDelay");
        if (agntBhvAssignDelay < 0)
            throw new IllegalArgumentException("Section: " + "agentBehaviors" + ", " + "Argument: " + "assignmentDelay");

        if(gameMode == 0)
        {
            if(agntBhvCoopMode < 0 || agntBhvCoopMode > 1)
                throw new IllegalArgumentException("Section: " + "agentBehaviors" + ", " + "Argument: " + "cooperationMode");
            if(agntBhvFullQueAcptRate < 0 || agntBhvFullQueAcptRate > 100)
                throw new IllegalArgumentException("Section: " + "agentBehaviors" + ", " + "Argument: " + "fullQueueAcceptRate");
            if(agntBhvCrowdQueAcptRate < 0 || agntBhvCrowdQueAcptRate > 100)
                throw new IllegalArgumentException("Section: " + "agentBehaviors" + ", " + "Argument: " + "crowdQueueAcceptRate");
            if(agntBhvSpareQueAcptRate < 0 || agntBhvSpareQueAcptRate > 100)
                throw new IllegalArgumentException("Section: " + "agentBehaviors" + ", " + "Argument: " + "spareQueueAcceptRate");
            if(agntBhvTitForTatMem < 0)
                throw new IllegalArgumentException("Section: " + "agentBehaviors" + ", " + "Argument: " + "titForTatMemory");
            if(agntBhvResponseDelay < 0)
                throw new IllegalArgumentException("Section: " + "agentBehaviors" + ", " + "Argument: " + "responseDelay");
            if(agntBhvRequestDelay < 0)
                throw new IllegalArgumentException("Section: " + "agentBehaviors" + ", " + "Argument: " + "requestDelay");
        }

        isValid = true;
    }

    public void loadSettings(File configFile)
    {
        try
        {
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

            agntBhvUseAvgDelay = ini.get("agentBehaviors", "useAvgDelay", boolean.class);
            agntBhvDecisionDelay = ini.get("agentBehaviors", "decisionDelay", int.class);
            agntBhvAssignDelay = ini.get("agentBehaviors", "assignmentDelay", int.class);

            if(gameMode == 0)
            {
                agntBhvCoopMode = ini.get("agentBehaviors", "cooperationMode", int.class);
                agntBhvFullQueAcptRate = ini.get("agentBehaviors", "fullQueueAcceptRate", int.class);
                agntBhvCrowdQueAcptRate = ini.get("agentBehaviors", "crowdQueueAcceptRate", int.class);
                agntBhvSpareQueAcptRate = ini.get("agentBehaviors", "spareQueueAcceptRate", int.class);
                if(agntBhvCoopMode == 1)
                {
                    agntBhvSpareRequest = ini.get("agentBehaviors", "spareRequest", boolean.class);
                    agntBhvCrowdRequest = ini.get("agentBehaviors", "crowdRequest", boolean.class);
                    agntBhvFullRequest = ini.get("agentBehaviors", "fullRequest", boolean.class);
                }
                agntBhvTitForTatMem = ini.get("agentBehaviors", "titForTatMemory", int.class);
                agntBhvResponseDelay = ini.get("agentBehaviors", "responseDelay", int.class);
                agntBhvRequestDelay = ini.get("agentBehaviors", "requestDelay", int.class);
            }

            validate();
        }
        catch(IllegalFormatException e)
        {
            JOptionPane.showMessageDialog(MicroworldHospital.mainFrame, "Illegal INI file format", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (IllegalArgumentException e)
        {
            isValid = false;
            JOptionPane.showMessageDialog(MicroworldHospital.mainFrame, "Illegal value at " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(MicroworldHospital.mainFrame, "Error opening the file", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
}
