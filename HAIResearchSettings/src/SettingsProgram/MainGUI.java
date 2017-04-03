package SettingsProgram;

import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Font;

public class MainGUI {

	private JFrame frmHairesearchSettings;
	private JTable table;
	private JLabel lblIniFile;
	private JRadioButtonMenuItem menuitemHighCoop, menuitemLowCoop;
	private JCheckBoxMenuItem menuitemCustom;
	private String AllConfigsLocation, GameConfigLocation, CurrentConfig;
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
			ini = new Ini(new FileReader(GameConfigLocation+CurrentConfig));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

			// get the property value and print it out
			this.AllConfigsLocation = prop.getProperty("AllConfigsLocation");
			this.GameConfigLocation = prop.getProperty("GameConfigLocation");
			this.CurrentConfig = prop.getProperty("CurrentConfig");

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
		mnProfiles.add(menuitemHighCoop);
		
		menuitemLowCoop = new JRadioButtonMenuItem("Low Cooperation");
		mnProfiles.add(menuitemLowCoop);
		
		menuitemCustom = new JCheckBoxMenuItem("Custom");
		mnProfiles.add(menuitemCustom);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mnFile.add(mntmPreferences);
		frmHairesearchSettings.getContentPane().setLayout(null);
		
		initializeTable();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 55, 574, 427);
		frmHairesearchSettings.getContentPane().add(scrollPane);
	
		scrollPane.setViewportView(table);
		
		JButton btnSaveChanges = new JButton("Save Changes");
		btnSaveChanges.setBounds(145, 505, 125, 34);
		frmHairesearchSettings.getContentPane().add(btnSaveChanges);
		
		JButton btnRevertChanges = new JButton("Revert Changes");
		btnRevertChanges.setBounds(306, 505, 125, 34);
		frmHairesearchSettings.getContentPane().add(btnRevertChanges);
		
		lblIniFile = new JLabel(CurrentConfig);
		lblIniFile.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblIniFile.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblIniFile.setBounds(248, 19, 183, 25);
		frmHairesearchSettings.getContentPane().add(lblIniFile);
		
		whichSelected();
	}
	
	
	private void initializeTable() {
		
		Ini ini = null;
		try {
			ini = new Ini(new FileReader(GameConfigLocation+CurrentConfig));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		 
		table = new JTable(rowData, columnNames);
		table.setModel(tablemodel);
	}
	

	private void whichSelected() {
		if(CurrentConfig.equals("GLOC.ini")) {
			menuitemHighCoop.setSelected(false);
			menuitemLowCoop.setSelected(true);
			menuitemCustom.setSelected(false);
		}
		else if(CurrentConfig.equals("GHIC.ini")) {
			menuitemHighCoop.setSelected(true);
			menuitemLowCoop.setSelected(false);
			menuitemCustom.setSelected(false);
		}
		else if(CurrentConfig.equals("Custom")) {
			menuitemHighCoop.setSelected(false);
			menuitemLowCoop.setSelected(false);
			menuitemCustom.setSelected(true);
		}
		else {
			menuitemHighCoop.setSelected(false);
			menuitemLowCoop.setSelected(false);
			menuitemCustom.setSelected(false);
		}
	}
}
