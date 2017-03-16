package networkDrive;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.joda.time.LocalTime;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Calendar;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SpinnerDateModel;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import javax.swing.JSpinner;
import javax.swing.ButtonGroup;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.plaf.metal.*;

@SuppressWarnings("serial")
public class GUI extends JFrame {
	
	// Specify the look and feel to use by defining the LOOKANDFEEL constant
    // Valid values are: null (use the default), "Metal", "System", "Motif",
    // and "GTK"
    final static String LOOKANDFEEL = "System";
    
    // If you choose the Metal L&F, you can also choose a theme.
    // Specify the theme to use by defining the THEME constant
    // Valid values are: "DefaultMetal", "Ocean",  and "Test"
    final static String THEME = "Test";
    
	CheckConnectivity prog;
	JButton btnStart = new JButton("Start / Resume");
	JButton btnPause = new JButton("Pause");
	JButton btnStop = new JButton("Stop");
	boolean hasStarted = false;
	Date date = new Date();
	LocalTime morning1;
	LocalTime morning2;
	LocalTime afternoon1;
	LocalTime afternoon2;
	LocalTime afternoon3;
	LocalTime afternoon4;
	LocalTime evening1;
	LocalTime evening2;
	SpinnerDateModel sm = new SpinnerDateModel(date, null, null, Calendar.MINUTE);
	SpinnerDateModel sm1 = new SpinnerDateModel(date, null, null, Calendar.MINUTE);
	final JFileChooser fc = new JFileChooser();
	private final JTextField fieldTarget = new JTextField();
	private final JTextField fieldSource = new JTextField();
	private final JPanel panel = new JPanel();
	private final JTextPane txtBox = new JTextPane();
	private final JPanel panel_1 = new JPanel();
	private final JSpinner spinner = new JSpinner(sm);
	private final JSpinner spinner1 = new JSpinner(sm1);
	private final JLabel timeLabel = new JLabel("From:");
	private final JLabel timeLabel1 = new JLabel("To:");
	private final JRadioButton rdbtnTime = new JRadioButton("Morning");
	private final JRadioButton rdbtnTime_1 = new JRadioButton("Aft.1");
	private final JRadioButton rdbtnTime_3 = new JRadioButton("Evening");
	private final JButton submitBtn = new JButton("Submit");
	private final JButton transferNow = new JButton("Transfer Now / Test Connection");
	private final JButton btnBrowse = new JButton("Browse");
	private final JButton btnBrowse_1 = new JButton("Browse");
	private final JPanel panel_2 = new JPanel();
	private final JLabel lblNewLabel = new JLabel("");
	private final JLabel lblNewLabel_1 = new JLabel("Morning");
	private final JLabel lblNewLabel_2 = new JLabel("Aft.1");
	private final JLabel lblNewLabel_3 = new JLabel("Evening");
	private final JLabel morn2 = new JLabel();
	private final JLabel aft2 = new JLabel();
	private final JLabel lblNewLabel_6 = new JLabel("From");
	private final JLabel lblNewLabel_7 = new JLabel("To");
	private final JLabel eve1 = new JLabel();
	private final JLabel aft1 = new JLabel();
	private final JLabel eve2 = new JLabel();
	private final JLabel morn1 = new JLabel();
	private final JLabel numFiles = new JLabel("0 files ready to send");
	private Container thecontainer;
	private final JButton btnScanNow = new JButton("Scan Now");
	private final JLabel lblAft = new JLabel("Aft.2");
	private final JLabel aft3 = new JLabel("12:45");
	private final JLabel aft4 = new JLabel("13:00");
	private final JRadioButton rdbtnTime_2 = new JRadioButton("Aft.2");
	
	
	protected Container getTheContainer() {
		return thecontainer;
	}
	
	public GUI() {
		this.thecontainer = createComponents();
	}
	
	public static void main(String args[]) {
		//Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
	private static void createAndShowGUI() {
		
		//Set the look and feel.
        initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Thomas Sucks, Let's Run This");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GUI dagui = new GUI();
        
        if (dagui.getContainerListeners() == null)
        	frame.getContentPane().add(dagui.createComponents(), BorderLayout.CENTER);
        else
        	frame.getContentPane().add(dagui.getTheContainer(), BorderLayout.CENTER);
        
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(GUI.class.getResource("/resources/013 - Thomas - PIL Portraits (IMG_3304).jpg")));

        frame.pack();
        frame.setLocationRelativeTo(null);
        
        dagui.btnStart.doClick();
        dagui.btnScanNow.doClick();
        
        frame.setVisible(true);
	}
	
	private static void initLookAndFeel() {
		
        String lookAndFeel = null;
       
        if (LOOKANDFEEL != null) {
            if (LOOKANDFEEL.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
              //  an alternative way to set the Metal L&F is to replace the 
              // previous line with:
              // lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
            }
            else if (LOOKANDFEEL.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            } 
            else if (LOOKANDFEEL.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } 
            else if (LOOKANDFEEL.equals("GTK")) { 
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } 
            else {
                System.err.println("Unexpected value of LOOKANDFEEL specified: "
                                   + LOOKANDFEEL);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            try {
            	
                UIManager.setLookAndFeel(lookAndFeel);
                
                // If L&F = "Metal", set the theme
                if (LOOKANDFEEL.equals("Metal")) {
                  if (THEME.equals("DefaultMetal"))
                     MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                  else if (THEME.equals("Ocean"))
                     MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                     
                  UIManager.setLookAndFeel(new MetalLookAndFeel()); 
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Couldn't find class for specified look and feel:"
                                   + lookAndFeel);
                System.err.println("Did you include the L&F library in the class path?");
                System.err.println("Using the default look and feel.");
            } catch (UnsupportedLookAndFeelException e) {
                System.err.println("Can't use the specified look and feel ("
                                   + lookAndFeel
                                   + ") on this platform.");
                System.err.println("Using the default look and feel.");
            } catch (Exception e) {
                System.err.println("Couldn't get specified look and feel ("
                                   + lookAndFeel
                                   + "), for some reason.");
                System.err.println("Using the default look and feel.");
                e.printStackTrace();
            }
        }
    }

	public Container createComponents() {
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(GUI.class.getResource("/resources/013 - Thomas - PIL Portraits (IMG_3304).jpg")));
		
		setTitle("Thomas Sucks, Let's Run This");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(750, 500);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {239, 239, 239, 0};
		gridBagLayout.rowHeights = new int[] {50, 125, 100, 200, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		getContentPane().setBackground(Color.RED);
		panel.setOpaque(false);
		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 3;
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {670, 0, 0};
		gbl_panel.rowHeights = new int[] {35, 20, 30, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		GridBagConstraints gbc_fieldSource = new GridBagConstraints();
		gbc_fieldSource.fill = GridBagConstraints.BOTH;
		gbc_fieldSource.anchor = GridBagConstraints.NORTHWEST;
		gbc_fieldSource.insets = new Insets(0, 0, 5, 5);
		gbc_fieldSource.gridx = 0;
		gbc_fieldSource.gridy = 0;
		fieldSource.setForeground(Color.GRAY);
		fieldSource.setText("C:\\Users\\ASUprint\\Desktop\\Enfocus\\Switch\\ASU Print Online\\Output\\Ready for Print");
		fieldSource.setToolTipText("Source Folder");
		
		fieldSource.setColumns(20);
		GridBagConstraints gbc_fieldTarget = new GridBagConstraints();
		gbc_fieldTarget.fill = GridBagConstraints.BOTH;
		gbc_fieldTarget.insets = new Insets(0, 0, 5, 5);
		gbc_fieldTarget.anchor = GridBagConstraints.NORTHWEST;
		gbc_fieldTarget.gridx = 0;
		gbc_fieldTarget.gridy = 1;
		fieldTarget.setForeground(Color.GRAY);
		fieldTarget.setToolTipText("Target Folder");
		fieldTarget.setText("J:\\CMYK+Spot");
		fieldTarget.setColumns(20);
		
		if (!fieldTarget.getText().equals("Target Folder")) {
			fieldTarget.setForeground(Color.BLACK);
		}
		
		panel.add(fieldSource, gbc_fieldSource);
		
		GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
		gbc_btnBrowse.fill = GridBagConstraints.BOTH;
		gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
		gbc_btnBrowse.gridx = 1;
		gbc_btnBrowse.gridy = 0;
		panel.add(btnBrowse, gbc_btnBrowse);
		panel.add(fieldTarget, gbc_fieldTarget);
		
		GridBagConstraints gbc_btnBrowse_1 = new GridBagConstraints();
		gbc_btnBrowse_1.fill = GridBagConstraints.BOTH;
		gbc_btnBrowse_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnBrowse_1.gridx = 1;
		gbc_btnBrowse_1.gridy = 1;
		panel.add(btnBrowse_1, gbc_btnBrowse_1);
		
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		getContentPane().add(panel_1, gbc_panel_1);
		panel_1.setOpaque(false);
		SpinnerDateModel time1 = new SpinnerDateModel(); 
		time1.setCalendarField(Calendar.MINUTE);
		panel_1.setLayout(new GridLayout(0, 3, 10, 5));
		
		ButtonGroup group = new ButtonGroup();
		rdbtnTime.setForeground(Color.BLACK);
		
		panel_1.add(rdbtnTime);
		group.add(rdbtnTime);
		rdbtnTime.setSelected(true);
		rdbtnTime.setOpaque(false);
		rdbtnTime_1.setForeground(Color.BLACK);
		
		panel_1.add(rdbtnTime_1);
		group.add(rdbtnTime_1);
		rdbtnTime_1.setOpaque(false);
		rdbtnTime_2.setOpaque(false);
		rdbtnTime_2.setForeground(Color.BLACK);
		
		panel_1.add(rdbtnTime_2);
		group.add(rdbtnTime_2);
		timeLabel.setForeground(Color.BLACK);
		
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		panel_1.add(timeLabel);
		
		JSpinner.DateEditor de = new JSpinner.DateEditor(spinner, "HH:mm");
		de.getTextField().setEditable(false);
		spinner.setToolTipText("Double Click Hours or Minutes");
		spinner.setEditor(de);
		panel_1.add(spinner);
		rdbtnTime_3.setForeground(Color.BLACK);
		
		panel_1.add(rdbtnTime_3);
		group.add(rdbtnTime_3);
		rdbtnTime_3.setOpaque(false);
		timeLabel1.setForeground(Color.BLACK);
		timeLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		
		panel_1.add(timeLabel1);
		
		JSpinner.DateEditor de1 = new JSpinner.DateEditor(spinner1, "HH:mm");
		de1.getTextField().setEditable(false);
		spinner1.setToolTipText("Double Click Hours or Minutes");
		spinner1.setEditor(de1);
		panel_1.add(spinner1);
		
		submitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (rdbtnTime.isSelected()) {
					morning1 = LocalTime.parse(de.getFormat().format(spinner.getValue()));
					morning2 = LocalTime.parse(de1.getFormat().format(spinner1.getValue()));
					morn1.setText(morning1.toString().substring(0, 5));
					morn2.setText(morning2.toString().substring(0, 5));
				}
				
				else if (rdbtnTime_1.isSelected()) {
					afternoon1 = LocalTime.parse(de.getFormat().format(spinner.getValue()));
					afternoon2 = LocalTime.parse(de1.getFormat().format(spinner1.getValue()));
					aft1.setText(afternoon1.toString().substring(0, 5));
					aft2.setText(afternoon2.toString().substring(0, 5));
				}
				
				else if (rdbtnTime_2.isSelected()) {
					afternoon3 = LocalTime.parse(de.getFormat().format(spinner.getValue()));
					afternoon4 = LocalTime.parse(de1.getFormat().format(spinner1.getValue()));
					aft3.setText(afternoon1.toString().substring(0, 5));
					aft4.setText(afternoon2.toString().substring(0, 5));
				}
				
				else if (rdbtnTime_3.isSelected()) {
					evening1 = LocalTime.parse(de.getFormat().format(spinner.getValue()));
					evening2 = LocalTime.parse(de1.getFormat().format(spinner1.getValue()));
					eve1.setText(evening1.toString().substring(0, 5));
					eve2.setText(evening2.toString().substring(0, 5));
				}
			}
		});
		
		panel_1.add(submitBtn);
		
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 1;
		getContentPane().add(panel_2, gbc_panel_2);
		panel_2.setLayout(new GridLayout(3, 4, 0, 0));
		
		panel_2.add(lblNewLabel);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		panel_2.add(lblNewLabel_1);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		panel_2.add(lblNewLabel_2);
		lblAft.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblAft.setHorizontalAlignment(SwingConstants.CENTER);
		lblAft.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		panel_2.add(lblAft);
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		panel_2.add(lblNewLabel_3);
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_6.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblNewLabel_6.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		panel_2.add(lblNewLabel_6);
		morn1.setHorizontalAlignment(SwingConstants.CENTER);
		morn1.setAlignmentX(Component.CENTER_ALIGNMENT);
		morn1.setText("08:00");
		morn1.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		panel_2.add(morn1);
		aft1.setHorizontalAlignment(SwingConstants.CENTER);
		aft1.setText("10:00");
		aft1.setAlignmentX(Component.CENTER_ALIGNMENT);
		aft1.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		panel_2.add(aft1);
		aft3.setHorizontalAlignment(SwingConstants.CENTER);
		aft3.setBorder(new LineBorder(new Color(0, 0, 0)));
		aft3.setToolTipText("");
		
		panel_2.add(aft3);
		eve1.setHorizontalAlignment(SwingConstants.CENTER);
		eve1.setText("16:00");
		eve1.setAlignmentX(Component.CENTER_ALIGNMENT);
		eve1.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		panel_2.add(eve1);
		lblNewLabel_7.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_7.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblNewLabel_7.setRequestFocusEnabled(false);
		lblNewLabel_7.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		panel_2.add(lblNewLabel_7);
		morn2.setHorizontalAlignment(SwingConstants.CENTER);
		morn2.setText("09:00");
		morn2.setAlignmentX(Component.CENTER_ALIGNMENT);
		morn2.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		panel_2.add(morn2);
		aft2.setHorizontalAlignment(SwingConstants.CENTER);
		aft2.setText("11:30");
		aft2.setAlignmentX(Component.CENTER_ALIGNMENT);
		aft2.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		panel_2.add(aft2);
		aft4.setHorizontalAlignment(SwingConstants.CENTER);
		aft4.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		panel_2.add(aft4);
		eve2.setHorizontalAlignment(SwingConstants.CENTER);
		eve2.setText("16:15");
		eve2.setAlignmentX(Component.CENTER_ALIGNMENT);
		eve2.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		panel_2.add(eve2);
		
		GridBagConstraints gbc_btnTransferNow = new GridBagConstraints();
		gbc_btnTransferNow.fill = GridBagConstraints.BOTH;
		gbc_btnTransferNow.insets = new Insets(0, 0, 5, 0);
		gbc_btnTransferNow.gridx = 2;
		gbc_btnTransferNow.gridy = 1;
		getContentPane().add(transferNow, gbc_btnTransferNow);
		
		GridBagConstraints gbc_txtBox = new GridBagConstraints();
		gbc_txtBox.gridwidth = 3;
		gbc_txtBox.insets = new Insets(0, 0, 5, 0);
		gbc_txtBox.fill = GridBagConstraints.BOTH;
		gbc_txtBox.gridx = 0;
		gbc_txtBox.gridy = 2;
		txtBox.setEditable(false);
		txtBox.setText("After entering your source folder directory and your target folder directory pressing the start button will automatically submit "
				+ "both directories.\r\n\r\nTo change the directories, if the program has already started, press the stop button then enter your directories and then press the start button again.");
		getContentPane().add(txtBox, gbc_txtBox);
		
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.fill = GridBagConstraints.BOTH;
		gbc_btnStart.insets = new Insets(0, 0, 0, 5);
		gbc_btnStart.gridx = 0;
		gbc_btnStart.gridy = 3;
		getContentPane().add(btnStart, gbc_btnStart);
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		btnBrowse.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if (e.getSource() == btnBrowse) {
					
			        int returnVal = fc.showOpenDialog(GUI.this);

			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			           fieldSource.setText(fc.getSelectedFile().getAbsolutePath());
			        }
			            
				}
			}
		});
		
		btnBrowse_1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if (e.getSource() == btnBrowse_1) {
					
			        int returnVal = fc.showOpenDialog(GUI.this);
			        
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			           fieldTarget.setText(fc.getSelectedFile().getAbsolutePath());
			        }
				}
			}
		});
		
		transferNow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (!fieldSource.getText().equals("Source Folder") && !fieldTarget.getText().equals("Target Folder") && !fieldSource.getText().equals("") && !fieldTarget.getText().equals("")) {
						new CheckPresses().run(fieldSource.getText(), fieldTarget.getText(), false, false);
						JOptionPane.showMessageDialog(null, "Files transferred successfully!");
					}
					else {
						throw new InvalidPathException("", "Invalid File Paths.");
					}
				} catch (InvalidPathException e1) {
					JOptionPane.showMessageDialog(null, "Please check and enter valid paths.");
					System.out.println("\nThe source path \"" + fieldSource.getText() + "\" may be invalid.");
					System.out.println("\nThe target path \"" + fieldTarget.getText() + "\" may be invalid.");
					e1.printStackTrace();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Copying files failed.");
					System.out.println("Copying files failed.");
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		fieldTarget.addFocusListener(new FocusAdapter(){
			public void focusLost(FocusEvent e1) {
				if (fieldTarget.getText().equals("")){
					fieldTarget.setText("Target Folder");
					fieldTarget.setForeground(Color.GRAY);
				}
			}
			
			public void focusGained(FocusEvent evt1) {
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run() {
						if (fieldTarget.getText().equals("Target Folder")){
							fieldTarget.setText("");
							fieldTarget.setForeground(Color.BLACK);
						}
						else {
							fieldTarget.selectAll();
						}
					}
				});
			}
		});
		
		fieldSource.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent evt) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (fieldSource.getText().equals("Source Folder")) {
							fieldSource.setText("");
							fieldSource.setForeground(Color.BLACK);
						}
						else {
							fieldSource.selectAll();
						}
					}
				});
			}
			
			public void focusLost(FocusEvent e) {
				if (fieldSource.getText().equals("")) {
					fieldSource.setText("Source Folder");
					fieldSource.setForeground(Color.GRAY);
				}
			}
		});
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!hasStarted) {
					hasStarted = true;
					try {
						prog = new CheckConnectivity("Date Opened: " + LocalTime.now(), fieldSource.getText(), fieldTarget.getText(), morn1.getText(), morn2.getText(), aft1.getText(),
								aft2.getText(), aft3.getText(), aft4.getText(), eve1.getText(), eve2.getText());
					} catch (IllegalArgumentException e1) {
						prog = new CheckConnectivity("Date Opened: " + LocalTime.now(), fieldSource.getText(), fieldTarget.getText(), "09:30", "09:45","10:30", "11:00", "12:45",
								"13:00", "14:30", "14:45");
					} finally {
						prog.start();
					}
				}
				else {
					prog.resume();
				}
				
				
				btnStart.setEnabled(false);
				btnPause.setEnabled(true);
				btnStop.setEnabled(true);
				getContentPane().setBackground(Color.GREEN);
				
			}
		});
		btnPause.setEnabled(false);
		GridBagConstraints gbc_btnPause = new GridBagConstraints();
		gbc_btnPause.insets = new Insets(0, 0, 0, 5);
		gbc_btnPause.fill = GridBagConstraints.BOTH;
		gbc_btnPause.gridx = 1;
		gbc_btnPause.gridy = 3;
		getContentPane().add(btnPause, gbc_btnPause);
		
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prog.suspend();
				btnStart.setEnabled(true);
				btnPause.setEnabled(false);
				getContentPane().setBackground(Color.YELLOW);
			}
		});
		
		btnStop.setEnabled(false);
		GridBagConstraints gbc_btnStop = new GridBagConstraints();
		gbc_btnStop.fill = GridBagConstraints.BOTH;
		gbc_btnStop.gridx = 2;
		gbc_btnStop.gridy = 3;
		getContentPane().add(btnStop, gbc_btnStop);
		getContentPane().setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{panel, fieldSource, fieldTarget, btnStart, btnPause, btnStop, txtBox}));
		
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prog.stop();
				btnStart.setEnabled(true);
				btnPause.setEnabled(false);
				btnStop.setEnabled(false);
				hasStarted = false;
				getContentPane().setBackground(Color.RED);
			}
		});
		
		GridBagConstraints gbc_numFiles = new GridBagConstraints();
		gbc_numFiles.anchor = GridBagConstraints.EAST;
		gbc_numFiles.insets = new Insets(0, 0, 0, 5);
		gbc_numFiles.gridx = 0;
		gbc_numFiles.gridy = 2;
		numFiles.setHorizontalAlignment(SwingConstants.TRAILING);
		panel.add(numFiles, gbc_numFiles);
		
		GridBagConstraints gbc_btnScanNow = new GridBagConstraints();
		gbc_btnScanNow.gridx = 1;
		gbc_btnScanNow.gridy = 2;
		panel.add(btnScanNow, gbc_btnScanNow);
		
		btnScanNow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				File location;
				int files;
				
				location = new File(fieldSource.getText());
				
				if(location.isDirectory()) {
					files = location.listFiles().length;
				}
				else {
					files = 0;
				}
				
				numFiles.setText(files + " files ready to send");
				
			}
		});
		
		return getContentPane();
	}
}