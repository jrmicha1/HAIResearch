package SettingsProgram;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainGUI {

	private JFrame frmHairesearchSettings;
	private PreferencesWindow preferenceswindow;
	private JTable table;
	private JLabel lblIniFile;
	private JRadioButtonMenuItem menuitemHighCoop, menuitemLowCoop;
	private JCheckBoxMenuItem menuitemCustom;
	private static String AllConfigsLocation;
	private static String GameConfigLocation;
	private static String CurrentConfig;
	private String[] iniSections, iniVariables, iniValues;
	private ArrayList<String> iniAllInOrder;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI window = new MainGUI();
					window.frmHairesearchSettings.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	/**
	 * Create the application.
	 */
	public MainGUI() {
		loadProperties();
		prepareIniFile();
		initializeGUIElements();
	}

	private void prepareIniFile() {
		Ini ini = null;
		try {
			ini = new Ini(new FileReader(MainGUI.GameConfigLocation+MainGUI.CurrentConfig));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane erroroptionPane = new JOptionPane("Failure to access GameConfigLocation file, please check"
					+ " if the file exists and your folder permissions before starting this program again. Otherwise, delete all entered property values in config.properties but not the variable names.", JOptionPane.ERROR_MESSAGE);    
			JDialog errordialog = erroroptionPane.createDialog("Failure");
			errordialog.setAlwaysOnTop(true);
			errordialog.setVisible(true);
			errordialog.dispose();
			System.exit(1);
		}
		
		iniSections = new String[ini.size()];
		int sectioncount = 0;
		int variablecount = 0;
		ArrayList<String> templist = new ArrayList<String>();
		ArrayList<String> templist2 = new ArrayList<String>();
		iniAllInOrder = new ArrayList<String>();
        System.out.println("Number of sections: "+ini.size()+"\n");
        
        for (String sectionName: ini.keySet()) {
        	iniSections[sectioncount] = sectionName;
        	iniAllInOrder.add(sectionName);
        	iniAllInOrder.add(null);
        	++sectioncount;
            System.out.println("["+sectionName+"]");
            Section section = ini.get(sectionName);
            for (String optionKey: section.keySet()) {
            	templist.add(optionKey);
            	iniAllInOrder.add(optionKey);
            	iniAllInOrder.add(ini.get(sectionName, optionKey, String.class));
                System.out.println("\t"+optionKey+"="+section.get(optionKey));
                templist2.add(ini.get(sectionName, optionKey, String.class));
                ++variablecount;
            }
        }
        
        iniVariables = new String[variablecount];
        iniValues = new String[variablecount];
        for (int i = 0; i < templist.size(); i++) {
        	iniVariables[i] = templist.get(i);
        	iniValues[i] = templist2.get(i);
        }
	}

	
	private void loadProperties() {
		
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);

			// get the property value BeenStartedBefore to get user input of folder locations
			if (prop.getProperty("AllConfigsLocation").equals("") || prop.getProperty("GameConfigLocation").equals("")) {
				
				String AllGameConfigLoc = null;
				String GameConfigLoc = null;
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				
				JOptionPane welcomePane = new JOptionPane("Hello. I see this is your first time using this program.", JOptionPane.PLAIN_MESSAGE);    
				JDialog welcomedialog = welcomePane.createDialog("Message");
				welcomedialog.setAlwaysOnTop(true);
				welcomedialog.setVisible(true);
				welcomedialog.dispose();
				
				JOptionPane optionPane = new JOptionPane("Please select the folder that contains all the .ini files (AllConfigs).", JOptionPane.PLAIN_MESSAGE);    
				JDialog dialog = optionPane.createDialog("Message");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
				dialog.dispose();
				
				int result = fileChooser.showOpenDialog(frmHairesearchSettings);
				
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = fileChooser.getSelectedFile();
				    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
				    AllGameConfigLoc = selectedFile.getAbsolutePath();
				}
				else if (result == JFileChooser.CANCEL_OPTION) {
					JOptionPane erroroptionPane = new JOptionPane("Failure to select folders, please enter them "
							+ "manually in config.properties or start this program again.", JOptionPane.ERROR_MESSAGE);    
					JDialog errordialog = erroroptionPane.createDialog("Failure");
					errordialog.setAlwaysOnTop(true);
					errordialog.setVisible(true);
					errordialog.dispose();
					System.exit(1);
				}
				
				JOptionPane optionPane2 = new JOptionPane("Please select the folder that contains the game .ini file (GameConfig).", JOptionPane.PLAIN_MESSAGE);    
				JDialog dialog2 = optionPane2.createDialog("Message");
				dialog2.setAlwaysOnTop(true);
				dialog2.setVisible(true);
				dialog2.dispose();
				
				int result2 = fileChooser.showOpenDialog(frmHairesearchSettings);
				
				if (result2 == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = fileChooser.getSelectedFile();
				    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
				    GameConfigLoc = selectedFile.getAbsolutePath();
				}
				else if (result2 == JFileChooser.CANCEL_OPTION) {
					JOptionPane erroroptionPane = new JOptionPane("Failure to select folders, please enter them "
							+ "manually in config.properties or start this program again.", JOptionPane.ERROR_MESSAGE);    
					JDialog errordialog = erroroptionPane.createDialog("Failure");
					errordialog.setAlwaysOnTop(true);
					errordialog.setVisible(true);
					errordialog.dispose();
					System.exit(1);
				}
				
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				changeProperty("AllConfigsLocation", AllGameConfigLoc+"\\");
				changeProperty("GameConfigLocation", GameConfigLoc+"\\");
			}
			
			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);
			
			MainGUI.AllConfigsLocation = prop.getProperty("AllConfigsLocation");
			MainGUI.GameConfigLocation = prop.getProperty("GameConfigLocation");
			
			if(prop.getProperty("CurrentConfig").equals("")) {
				File f = new File(MainGUI.GameConfigLocation);
				File[] matchingFiles = f.listFiles(new FilenameFilter() {
				    public boolean accept(File dir, String name) {
				        return name.endsWith("ini");
				    }
				});
				
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				changeProperty("CurrentConfig", matchingFiles[0].getName().toString());
			}
			
			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);
			
			MainGUI.CurrentConfig = prop.getProperty("CurrentConfig");
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void changeProperty(String propertyName, String propertyValue) {
		
		File file = new File("config.properties");
		
        PropertiesConfiguration config = new PropertiesConfiguration();
        PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout(config);
        
        try {
			layout.load(new InputStreamReader(new FileInputStream(file)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        config.setProperty(propertyName, propertyValue);
        
        try {
			layout.save(new FileWriter("config.properties", false));
		} catch (ConfigurationException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeGUIElements() {
		frmHairesearchSettings = new JFrame();
		frmHairesearchSettings.setTitle("HAIResearch Settings");
		frmHairesearchSettings.setResizable(false);
		frmHairesearchSettings.setBounds(100, 100, 600, 600);
		frmHairesearchSettings.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmHairesearchSettings.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnProfiles = new JMenu("Profiles");
		mnFile.add(mnProfiles);
		
		menuitemHighCoop = new JRadioButtonMenuItem("High Cooperation");
		menuitemHighCoop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switchProfiles("HIGH");
			}
		});
		mnProfiles.add(menuitemHighCoop);
		
		menuitemLowCoop = new JRadioButtonMenuItem("Low Cooperation");
		menuitemLowCoop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchProfiles("LOW");
			}
		});
		mnProfiles.add(menuitemLowCoop);
		
		menuitemCustom = new JCheckBoxMenuItem("Custom");
		menuitemCustom.setEnabled(false);
		mnProfiles.add(menuitemCustom);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mntmPreferences.setEnabled(false);
		mntmPreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				preferenceswindow = new PreferencesWindow();
				preferenceswindow.setVisible(true);
			}
		});
		mnFile.add(mntmPreferences);
		frmHairesearchSettings.getContentPane().setLayout(null);
		
		refreshTable();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 55, 574, 427);
		frmHairesearchSettings.getContentPane().add(scrollPane);
	
		scrollPane.setViewportView(table);
		
		JButton btnSaveChanges = new JButton("Save Changes");
		btnSaveChanges.setEnabled(false);
		btnSaveChanges.setBounds(145, 505, 125, 34);
		frmHairesearchSettings.getContentPane().add(btnSaveChanges);
		
		JButton btnRevertChanges = new JButton("Revert Changes");
		btnRevertChanges.setEnabled(false);
		btnRevertChanges.setBounds(306, 505, 125, 34);
		frmHairesearchSettings.getContentPane().add(btnRevertChanges);
		
		lblIniFile = new JLabel(CurrentConfig);
		lblIniFile.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblIniFile.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblIniFile.setBounds(248, 19, 183, 25);
		frmHairesearchSettings.getContentPane().add(lblIniFile);
		
		whichSelected();
	}
	
	
	private void refreshTable() {
		
		Object columnNames[] = { "Variable", "Value" };
		
		int variablecount = 0;
		Object rowData[][] = new Object[iniSections.length+iniVariables.length][2];
		
		//Nasty algorithm for compiling table from ini file
		for (int r=0; r<rowData.length; r++) {
		    for (int c=0; c<rowData[r].length; c++) {
		    		rowData[r][c] = iniAllInOrder.get(variablecount);
		    		++variablecount;
		    }
		}
		
		//instance table model
		DefaultTableModel tablemodel = new DefaultTableModel(rowData, columnNames) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //only second column
		       return column == 1;
		    }
		};
		if (table == null) {
			table = new JTable(rowData, columnNames);
		}
		table.setModel(tablemodel);
		tablemodel.fireTableDataChanged();
		table.setEnabled(false);
	}
	

	private void whichSelected() {
		if(CurrentConfig.equals("GLOC.ini")) {
			menuitemHighCoop.setSelected(false);
			menuitemLowCoop.setSelected(true);
			menuitemCustom.setSelected(false);
			lblIniFile.setText("GLOC.ini");
		}
		else if(CurrentConfig.equals("GHIC.ini")) {
			menuitemHighCoop.setSelected(true);
			menuitemLowCoop.setSelected(false);
			menuitemCustom.setSelected(false);
			lblIniFile.setText("GHIC.ini");
		}
		else if(CurrentConfig.equals("Custom")) {
			menuitemHighCoop.setSelected(false);
			menuitemLowCoop.setSelected(false);
			menuitemCustom.setSelected(true);
			lblIniFile.setText("Custom");
		}
		else {
			menuitemHighCoop.setSelected(false);
			menuitemLowCoop.setSelected(false);
			menuitemCustom.setSelected(false);
		}
	}
	
	private boolean switchProfiles(String type) {
		if (type.equals("HIGH")) {
			switchFiles("GLOC.ini", "GHIC.ini");
			changeProperty("CurrentConfig", "GHIC.ini");
			loadProperties();
			whichSelected();
			prepareIniFile();
			refreshTable();
			return true;
		}
		else if (type.equals("LOW")) {
			switchFiles("GHIC.ini", "GLOC.ini");
			changeProperty("CurrentConfig", "GLOC.ini");
			loadProperties();
			whichSelected();
			prepareIniFile();
			refreshTable();
			return true;
		}
		return false;
	}
	
	public static void switchFiles(String originalfile, String newfile) {
		File source = new File(AllConfigsLocation+newfile);
		File target = new File(GameConfigLocation+originalfile);
		try {
			target.delete();
			target = new File(GameConfigLocation+newfile);
			Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
