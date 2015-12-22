import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Stanso on 3/1/2015.
 */
public class RoomPanel extends JPanel
{
    final int NURSE_NEEDED = 1;
    final int DOCTOR_NEEDED = 1;
    final int SURGEON_NEEDED = 1;

    private JPanel contentPane;
    private JPanel dispPanel;
    private JPanel vacantPanel;
    private JPanel occupiedPanel;
    private JPanel collectPanel;
    private JLabel lblRoomNum;
    private JLabel lblPatientType;
    private JLabel lblHlprType;
    private JLabel lblHlprNum;
    private JLabel lblNrsNum;
    private JLabel lblTimeLeft;
    private JButton collectButton;

    private CardLayout dispPanelCard;

    private Timer timer;

    private int timeCount;
    private PatientType patientType;
    private int helperNum;
    private int nurseNum;
    private boolean roomOccupied;
    private JPanel currCard;

    private GameFrame linkedGameFrame;
    private StatisticsPanel linkedStatsPanel;

    public RoomPanel()
    {
        roomOccupied = false;
        timeCount = 0;
        helperNum = 0;
        nurseNum = 0;
        patientType = null;

        linkedGameFrame = null;
        linkedStatsPanel = null;

        dispPanelCard = new CardLayout();
        dispPanel.setLayout(dispPanelCard);

        dispPanel.add(vacantPanel, "vacantPanel");
        dispPanel.add(occupiedPanel, "occupiedPanel");
        dispPanel.add(collectPanel, "collectPanel");

        contentPane.setVisible(true);
        dispPanel.setVisible(true);
        dispPanelCard.show(dispPanel, "vacantPanel");
        currCard = vacantPanel;

        collectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                collectButtonClicked();
            }
        });

        vacantPanel.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mouseClicked(e);
                vacantPanelClicked();
            }
            @Override
            public  void mouseEntered(MouseEvent e)
            {
                if(linkedGameFrame.buttonClicked == Activity.ASSIGN_PATIENT_A_CLICKED ||
                        linkedGameFrame.buttonClicked == Activity.ASSIGN_PATIENT_B_CLICKED   )
                    vacantPanel.setBackground(new Color(0x84F0B3));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                vacantPanel.setBackground(Color.WHITE);
            }
        });

        occupiedPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mouseClicked(e);
                occupiedPanelClicked();
            }
            @Override
            public  void mouseEntered(MouseEvent e)
            {
                if(linkedGameFrame.buttonClicked == Activity.ASSIGN_DOCTOR_CLICKED &&
                        (patientType == PatientType.A && helperNum < DOCTOR_NEEDED))
                    occupiedPanel.setBackground(new Color(0x84F0B3));
                if(linkedGameFrame.buttonClicked == Activity.ASSIGN_SURGEON_CLICKED &&
                        (patientType == PatientType.B && helperNum < SURGEON_NEEDED))
                    occupiedPanel.setBackground(new Color(0x84F0B3));
                if(linkedGameFrame.buttonClicked == Activity.ASSIGN_NURSE_CLICKED &&
                        nurseNum < NURSE_NEEDED)
                    occupiedPanel.setBackground(new Color(0x84F0B3));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                occupiedPanel.setBackground(Color.WHITE);
            }
        });

        setVisible(true);
    }

    public void setRoomNum(int roomNum)
    {
        lblRoomNum.setText(String.valueOf(roomNum));
    }

    public boolean roomSetup(PatientType pType, int time)
    {
        if(!isEmpty())
            return false;

        linkedGameFrame.updateAssignmentTime();

        roomOccupied = true;
        patientType = pType;

        switch(patientType)
        {
            case A:
            {
                lblPatientType.setText("A");
                lblHlprType.setText("Doctor(s)");
                MicroworldHospital.writeLogLine("PatientType", "A", linkedGameFrame.isAgent());
                MicroworldHospital.writeLogLine("RoomAssign", lblRoomNum.getText(), linkedGameFrame.isAgent());
                break;
            }
            case B:
            {
                lblPatientType.setText("B");
                lblHlprType.setText("Surgeon(s)");
                MicroworldHospital.writeLogLine("PatientType", "A", linkedGameFrame.isAgent());
                MicroworldHospital.writeLogLine("RoomAssign", lblRoomNum.getText(), linkedGameFrame.isAgent());
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid Patient Type");
        }

        helperNum = 0;
        nurseNum = 0;
        lblHlprNum.setText(String.valueOf(helperNum));
        lblNrsNum.setText(String.valueOf(nurseNum));

        timeCount = time/1000;
        if(timeCount == 0)
            timer = new Timer(0, new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    linkedStatsPanel.setScore(linkedStatsPanel.getScore() +1);
                    MicroworldHospital.writeLogLine("TreatComplete", lblRoomNum.getText(), linkedGameFrame.isAgent());
                    dispPanelCard.show(dispPanel, "collectPanel");
                    currCard = collectPanel;
                    timer.stop();
                }
            });
        else
            timer = new Timer(1000, new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(--timeCount <= 0)
                    {
                        timer.stop();
                        linkedStatsPanel.setScore(linkedStatsPanel.getScore() +1);
                        MicroworldHospital.writeLogLine("TreatComplete", lblRoomNum.getText(), linkedGameFrame.isAgent());
                        lblTimeLeft.setText("00:00");
                        dispPanelCard.show(dispPanel, "collectPanel");
                        currCard = collectPanel;
                    }
                    else
                    {
                        String timeDisp = "";
                        if(timeCount / 60 < 10) timeDisp += "0";
                        timeDisp += timeCount / 60 + ":";
                        if(timeCount % 60 < 10) timeDisp += "0";
                        timeDisp += timeCount % 60 + "";
                        lblTimeLeft.setText(timeDisp);
                    }
                }
            });
        lblTimeLeft.setText("--:--");

        dispPanelCard.show(dispPanel, "occupiedPanel");
        currCard = occupiedPanel;

        return true;
    }

    public boolean addResource(ResourceType rType)
    {
        switch (rType)
        {
            case DOCTOR:
            {
                if(!needResource(rType))
                    return false;

                // critical section
                // linkedGameFrame.getResLock().lock();
                synchronized (linkedGameFrame.getResLock())
                {
                    linkedStatsPanel.decResourceNum(ResourceType.DOCTOR, 1);
                    lblHlprNum.setText(String.valueOf(++helperNum));
                }
                // linkedGameFrame.getResLock().unlock();

                    if (needResource(ResourceType.NURSE))
                        MicroworldHospital.writeLogLine("FirstResource", lblRoomNum.getText(), linkedGameFrame.isAgent());
                    else
                        MicroworldHospital.writeLogLine("SecondResource", lblRoomNum.getText(), linkedGameFrame.isAgent());

                    if (((patientType == PatientType.A && helperNum == DOCTOR_NEEDED) ||
                            (patientType == PatientType.B && helperNum == SURGEON_NEEDED)) && nurseNum > 0)
                        timer.start();

                    linkedGameFrame.updateAssignmentTime();

                return true;
            }
            case SURGEON:
            {
                /*if (!roomOccupied || (patientType == PatientType.A && helperNum >= DOCTOR_NEEDED) ||
                        (patientType == PatientType.B && helperNum >= SURGEON_NEEDED))
                    return false;*/

                if(!needResource(rType))
                    return false;

                // critical section
                // linkedGameFrame.getResLock().lock();
                synchronized (linkedGameFrame.getResLock())
                {
                    linkedStatsPanel.decResourceNum(ResourceType.SURGEON, 1);
                    lblHlprNum.setText(String.valueOf(++helperNum));
                }
                // linkedGameFrame.getResLock().unlock();

                    if (needResource(ResourceType.NURSE))
                        MicroworldHospital.writeLogLine("FirstResource", lblRoomNum.getText(), linkedGameFrame.isAgent());
                    else
                        MicroworldHospital.writeLogLine("SecondResource", lblRoomNum.getText(), linkedGameFrame.isAgent());

                    if (((patientType == PatientType.A && helperNum == DOCTOR_NEEDED) ||
                            (patientType == PatientType.B && helperNum == SURGEON_NEEDED)) && nurseNum > 0)
                        timer.start();

                    linkedGameFrame.updateAssignmentTime();

                return true;
            }
            case NURSE:
            {
                /*if (!roomOccupied || nurseNum >= NURSE_NEEDED)
                    return false;*/

                if(!needResource(rType))
                    return false;

                // critical section
                // linkedGameFrame.getResLock().lock();
                synchronized (linkedGameFrame.getResLock())
                {

                    linkedStatsPanel.decResourceNum(ResourceType.NURSE, 1);
                    lblNrsNum.setText(String.valueOf(++nurseNum));
                }
                // linkedGameFrame.getResLock().unlock();

                    if (needResource(ResourceType.DOCTOR) || needResource(ResourceType.SURGEON))
                        MicroworldHospital.writeLogLine("FirstResource", lblRoomNum.getText(), linkedGameFrame.isAgent());
                    else
                        MicroworldHospital.writeLogLine("SecondResource", lblRoomNum.getText(), linkedGameFrame.isAgent());

                    if (helperNum == NURSE_NEEDED && nurseNum > 0)
                        timer.start();

                    linkedGameFrame.updateAssignmentTime();

                return true;
            }
            default:
                throw new IllegalArgumentException("Invalid Resource Type");
        }
    }

    public boolean isEmpty()
    {
        return !roomOccupied;
    }

    public boolean needResource(ResourceType rType)
    {
        switch (rType)
        {
            case DOCTOR:
                return (roomOccupied && patientType == PatientType.A && helperNum < DOCTOR_NEEDED);
            case NURSE:
                return (roomOccupied && nurseNum < NURSE_NEEDED);
            case SURGEON:
                return (roomOccupied && patientType == PatientType.B && helperNum < SURGEON_NEEDED);
            default:
                throw new IllegalArgumentException("Invalid Resource Type");
        }
    }

    public boolean needCollect()
    {
        return (currCard == collectPanel);
    }

    public int getResourceNum(ResourceType rType)
    {
        switch (rType)
        {
            case DOCTOR:
            case SURGEON:
                return helperNum;
            case NURSE:
                return nurseNum;
            default:
                throw new IllegalArgumentException("Invalid Resource Type");
        }
    }

    public int getTimeCount()
    {
        return timeCount;
    }

    public PatientType getPatientType()
    {
        return patientType;
    }

    public void setLinkedGameFrame(GameFrame gameFrame)
    {
        linkedGameFrame = gameFrame;
        linkedStatsPanel = linkedGameFrame.thisStatsPanel;
    }

    private void vacantPanelClicked()
    {
        switch (linkedGameFrame.buttonClicked)
        {
            case ASSIGN_PATIENT_A_CLICKED:
                if(this.isEmpty())
                {
                    linkedStatsPanel.dequeue(PatientType.A);
                    this.roomSetup(PatientType.A, MainFrame.CONFIG.getCureTimeMillis(PatientType.A));
                }
                break;

            case ASSIGN_PATIENT_B_CLICKED:
                if(this.isEmpty())
                {
                    linkedStatsPanel.dequeue(PatientType.B);
                    this.roomSetup(PatientType.B, MainFrame.CONFIG.getCureTimeMillis(PatientType.B));
                }
                break;
            case NOTHING_CLICKED:
            default:
        }
        linkedGameFrame.buttonClicked = Activity.NOTHING_CLICKED;
    }

    private void occupiedPanelClicked()
    {
        switch (linkedGameFrame.buttonClicked)
        {
            case ASSIGN_DOCTOR_CLICKED:
                if(this.getPatientType() == PatientType.A && linkedStatsPanel.getResourceNum(ResourceType.DOCTOR) > 0)
                {
                    this.addResource(ResourceType.DOCTOR);
                }
                break;
            case ASSIGN_NURSE_CLICKED:
                if(linkedStatsPanel.getResourceNum(ResourceType.NURSE) > 0)
                {
                    this.addResource(ResourceType.NURSE);
                }
                break;
            case ASSIGN_SURGEON_CLICKED:
                if(this.getPatientType() == PatientType.B && linkedStatsPanel.getResourceNum(ResourceType.SURGEON) > 0)
                {
                    this.addResource(ResourceType.SURGEON);
                }
                break;
            case NOTHING_CLICKED:
            default:
        }
        linkedGameFrame.buttonClicked = Activity.NOTHING_CLICKED;
    }

    private void collectButtonClicked()
    {
        timer.setDelay(1000);

        // linkedGameFrame.getResLock().lock();
        synchronized (linkedGameFrame.getResLock())
        {

            linkedStatsPanel.incResourceNum(ResourceType.NURSE, nurseNum);
            nurseNum = 0;

            switch (patientType)
            {
                case A:
                    linkedStatsPanel.incResourceNum(ResourceType.DOCTOR, helperNum);
                    helperNum = 0;
                    break;
                case B:
                    linkedStatsPanel.incResourceNum(ResourceType.SURGEON, helperNum);
                    helperNum = 0;
                    break;
            }

        }
        // linkedGameFrame.getResLock().unlock();

        roomOccupied = false;
        timeCount = 0;
        patientType = null;
        MicroworldHospital.writeLogLine("PlayerCollect", lblRoomNum.getText(), linkedGameFrame.isAgent());
        dispPanelCard.show(dispPanel, "vacantPanel");
        currCard = vacantPanel;
    }

    public void timeStop()
    {
        if(timer != null)
            timer.stop();
    }

    public void clickPanel()
    {
        if(currCard == vacantPanel)
            vacantPanelClicked();
        else if(currCard == occupiedPanel)
            occupiedPanelClicked();
        else if(currCard == collectPanel)
            //collectButtonClicked();
            collectButton.doClick();
        linkedGameFrame.buttonClicked = Activity.ROOM_CLICKED;
    }
}
