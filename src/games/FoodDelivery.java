package games;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.nio.file.*;
import java.util.*;

public class FoodDelivery {
	//GUI
	private File tmp;
	private Path imageDirectory;
	private Path carsDirectory;
	private Path frameIconPath;
	private Path foodDirectory;	
	private int incrementR;
	private int incrementG;
	private int incrementB;
	private ArrayList<Path> directoryFilesList;
	private Random random;
	protected static JFrame mainFrame;
	private Image mainFrameIcon;
	private JPanel gridPanel;
	private JLabel[][] deliveryGrid;
	private JPanel bufferPanel;
	private ImagePanel managementPanel;	
	private Border titledBorder;
	private JPanel timePanel;
	private JLabel timeLabel;
	private JPanel salesPanel;
	private JLabel salesLabel;
	private ImagePanel ordersPanel;
	private JScrollPane ordersScrollPane;
	private DefaultListModel ordersListModel;
	private JList ordersList;
	private JTextArea messageTextArea;
	private ImagePanel ovenPanel;
	private JLabel[] pizzaLabel;
	private JButton addPizzaButton;
	private JButton bakePizzaButton;
	private JPanel readyPanel;
	private JLabel readyLabel;
	private JPanel inCarPanel;
	private JLabel inCarLabel;
	private JButton loadCarButton;
	private JPanel buttonsPanel;
	private JCheckBox muteSoundCheckBox;
	private JButton startPauseButton;
	private JButton exitStopButton;	
	//Logic
	private final  int mSecPerMin;
	private Timer clockTimer;
	private Timer phoneTimer;
	private int clockHour, clockMinute;
	private int[][] pizzas;
	private int[][] orderTime;
	private int orderNumber;
	private Color onTimeDeliveryColor;
	private static Thread gameSoundThread;
	private int muteSoundCheckBoxClicksCounter;
	private final int pizzasReadyMax;
	private final int pizzasBakingMax;
	private final int bakingTime;
	private int pizzasBaking, pizzasReady;
	private int totalPizzasBaked;
	private int bakingMinutesLeft;
	private boolean ovenGoing;
	private ImageIcon pizzaIcon;
	private Timer ovenTimer;
	private int pizzasInCar;
	private boolean loadCarButtonEnabled;
	private final int pizzasInCarMax;
	private int pizzaC, pizzaR;
	private int deliveryC, deliveryR;
	private int carC, carR;
	private int deltaC, deltaR;
	private int mileage;
	private boolean carGoing;
	final int minPer20Squares;
	private Timer carTimer;
	private ImageIcon upCarIcon;
	private ImageIcon downCarIcon;
	private ImageIcon leftCarIcon;
	private ImageIcon rightCarIcon;
	protected static ImageIcon parlorIcon;
	private int carNumber;
	private final int orderMaxTime;
	private final int orderLateTime;
	private final int netSoldPizza;
	private final int netLatePizza;
	private final int costMissedPizza;
	private final int pizzaCost;
	private final double mileageCost;
	private int pizzasOnTime;
	private int pizzasLate;
	private int missedDeliveries;
	private int totalSales;
	private Color lateDeliveryColor;
	private Timer displayTimer;
			
	public FoodDelivery() {
		mSecPerMin = 3000;
		pizzasReadyMax = 12;
		pizzasBakingMax = 4;
		bakingTime = 8;
		pizzasInCarMax = 10;
		minPer20Squares = 3;
		orderMaxTime = 60;
		orderLateTime = 30;
		netSoldPizza = 10;
		netLatePizza = 5;
		costMissedPizza = 1;
		pizzaCost = 3;
		mileageCost = 0.1;
		if(!Files.isRegularFile(Path.of("files\\.tmp"))) {
			incrementR = 40;
			incrementG = 40;
			incrementB = 40;			
			muteSoundCheckBoxClicksCounter =0;				
			GridBagConstraints gbc;				
			//launch beacon file
			tmp = new File("files\\.tmp");
			try {
				tmp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}		
			
			imageDirectory = Path.of("image");
			carsDirectory = Path.of(imageDirectory+"\\cars");			
			frameIconPath = Path.of(imageDirectory+"\\icon"+"\\Emblem016.png");
			foodDirectory = Path.of(imageDirectory+"\\food");
			directoryFilesList = new ArrayList<Path>();
			random = new Random();
			
			try(DirectoryStream<Path> dstr = Files.newDirectoryStream(carsDirectory, "*.{gif}")){
				for(Path entry: dstr) {
					directoryFilesList.add(entry.getName(carsDirectory.getNameCount()));
				}
			}catch(InvalidPathException e) { 
			      System.out.println("Path Error " + e); 
		    } catch(NotDirectoryException e) { 
		      System.out.println(carsDirectory + " is not a directory."); 
		    } catch (IOException e) { 
		      System.out.println("I/O Error: " + e); 
		    }
			
			carNumber = random.nextInt(1+directoryFilesList.size()/4);
			directoryFilesList.clear();
			
			parlorIcon = new ImageIcon(Path.of("image\\parlor").
					resolve(filePathRandomizer(Path.of("image\\parlor"))).toString());
			
			upCarIcon = new ImageIcon(carsDirectory+"\\upcar"+String.valueOf(carNumber)+".gif");
			downCarIcon = new ImageIcon(carsDirectory+"\\downcar"+String.valueOf(carNumber)+".gif");
			leftCarIcon = new ImageIcon(carsDirectory+"\\leftcar"+String.valueOf(carNumber)+".gif");
			rightCarIcon = new ImageIcon(carsDirectory+"\\rightcar"+String.valueOf(carNumber)+".gif");
			
			mainFrame = new JFrame("Food Delivery");	
			mainFrame.setLayout(new GridBagLayout());
			mainFrame.setResizable(false);
			mainFrameIcon = Toolkit.getDefaultToolkit().createImage(frameIconPath.toString());
			mainFrame.setIconImage(mainFrameIcon);			
			mainFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					tmp.delete();
					System.exit(0);
				}
			});				
			
			gridPanel = new JPanel();
			gridPanel.setPreferredSize(new Dimension(810, 820));
			Color gridColor = new Color(60,60,60); 
			gridPanel.setBackground(new Color(40,40,40));
			gridPanel.setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridheight = 5;
			mainFrame.add(gridPanel, gbc);
			
			deliveryGrid = new JLabel[21][21];
			int w = (int) gridPanel.getPreferredSize().width / 21;			
			// j is row, i is column; build one row at a time
			for (int j = 0; j < 21; j++) {
			// start new row
				for (int i = 0; i < 21; i++) {
					deliveryGrid[i][j] = new JLabel();
					deliveryGrid[i][j].setPreferredSize(new Dimension(w, w));
					deliveryGrid[i][j].setFont(new Font("Arial", Font.BOLD, 14));
					deliveryGrid[i][j].setHorizontalAlignment(SwingConstants.CENTER);
					gbc = new GridBagConstraints();
					gbc.gridy = j;
					gbc.gridx = i;
					if (i == 0)	{
						if (j != 0)	{
						// row numbers
							deliveryGrid[i][j].setText(String.valueOf(j));
							deliveryGrid[i][j].setForeground(new Color(200,200,200));
						}
					} else if (j == 0) {
						if (i != 0)	{
						// column letters
							deliveryGrid[i][j].setText(String.valueOf((char) (i + 64)));
							deliveryGrid[i][j].setForeground(new Color(200,200,200));
						}
					} else {
							deliveryGrid[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
							deliveryGrid[i][j].setOpaque(true);
							deliveryGrid[i][j].setBackground(gridColor);
							deliveryGrid[i][j].setForeground(Color.YELLOW);
						}
					gridPanel.add(deliveryGrid[i][j], gbc);
					deliveryGrid[i][j].addMouseListener(new MouseAdapter() {
						public void mousePressed(MouseEvent e){
							deliveryGridMousePressed(e);
						}
					});
				}
			}
			
			bufferPanel = new JPanel();
			bufferPanel.setPreferredSize(new Dimension(25, 820));
			bufferPanel.setBorder(BorderFactory.createMatteBorder(0,0,0,2, new Color(incrementR, incrementG, incrementB)));
			bufferPanel.setBackground(new Color(40,40,40));
			gbc = new GridBagConstraints();
			gbc.gridx=1;
			gbc.gridy=0;
			mainFrame.add(bufferPanel, gbc);			
						
			managementPanel = new ImagePanel(new ImageIcon("image\\misc\\managementPanelImage.jpg").getImage());
			managementPanel.setPreferredSize(new Dimension(300, 820));
			managementPanel.setBackground(new Color(40,40,40));			
			managementPanel.setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.gridx=2;
			gbc.gridy=0;
			mainFrame.add(managementPanel, gbc);			
			
			timePanel = new JPanel();
			timePanel.setPreferredSize(new Dimension(140, 50));	
			timePanel.setOpaque(false);
			timePanel.setLayout(new GridBagLayout());
			//timePanel.setBackground(new Color(198, 198, 198));
			Border simpleBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.RED, Color.BLACK);
			titledBorder = BorderFactory.createTitledBorder(simpleBorder, "Time", 1, 2, 
					new Font("Arial", Font.BOLD, 16), Color.BLACK);
			timePanel.setBorder(titledBorder);			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;			
			gbc.insets = new Insets(5, 5, 5, 5);
			managementPanel.add(timePanel, gbc);
			
			timeLabel = new JLabel();
			timeLabel.setText("--:--");			
			timeLabel.setForeground(Color.BLACK);
			timeLabel.setFont(new Font("Arial", Font.BOLD, 18));
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(0,0,10,0);
			timePanel.add(timeLabel, gbc);
			
			salesPanel = new JPanel();
			salesPanel.setOpaque(false);
			salesPanel.setPreferredSize(new Dimension(140,50));
			salesPanel.setLayout(new GridBagLayout());
			titledBorder = BorderFactory.createTitledBorder(simpleBorder, "Sales", 1, 2, 
					new Font("Arial", Font.BOLD, 16), Color.BLACK);
			salesPanel.setBorder(titledBorder);		
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.insets = new Insets(5,5,5,5);			
			managementPanel.add(salesPanel, gbc);
			
			salesLabel = new JLabel();
			salesLabel.setText("$0");			
			salesLabel.setForeground(Color.BLACK);
			salesLabel.setFont(new Font("Aerial", Font.BOLD, 18));
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(0,0,10,0);
			salesPanel.add(salesLabel, gbc);
			
			ordersPanel = new ImagePanel(new ImageIcon("image\\misc\\orderDisplayImage.jpg").getImage());
			ordersPanel.setLayout(new GridBagLayout());
			ordersPanel.setPreferredSize(new Dimension(290, 200));
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(5,0,6,0);
			managementPanel.add(ordersPanel, gbc);
			
			ordersScrollPane = new JScrollPane();
			ordersList = new JList();
			ordersList.setBackground(new Color(80, 80, 80));
			ordersList.setForeground(Color.WHITE);
			ordersListModel = new DefaultListModel();
			messageTextArea = new JTextArea();			
			
			ordersScrollPane.setPreferredSize(new Dimension(265, 115));
			ordersScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			ordersScrollPane.setViewportView(ordersList);
			ordersList.setFont(new Font("Courier New", Font.PLAIN, 14));
			ordersList.setModel(ordersListModel);
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;		
			gbc.insets = new Insets(5,0,1,0);
			ordersPanel.add(ordersScrollPane, gbc);
			
			messageTextArea.setPreferredSize(new Dimension(210, 40));
			messageTextArea.setEditable(false);			
			messageTextArea.setBackground(new Color(80, 80, 80));
			messageTextArea.setForeground(Color.WHITE);			
			messageTextArea.setFont(new Font("Arial", Font.ITALIC, 14));
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.insets = new Insets(15,50,5,0);
			ordersPanel.add(messageTextArea, gbc);
			
			ovenPanel = new ImagePanel(new ImageIcon("image\\misc\\ovenOff.jpg").getImage());
			ovenPanel.setPreferredSize(new Dimension(290, 290));
			simpleBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.DARK_GRAY, Color.BLACK);
			titledBorder = BorderFactory.createTitledBorder(simpleBorder, "ovenOff", 1, 2, 
					new Font("Arial", Font.PLAIN, 14), Color.RED);
			ovenPanel.setBorder(titledBorder);			
			ovenPanel.setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(10,0,20,0);
			managementPanel.add(ovenPanel, gbc);
						
			pizzaLabel = new JLabel[4];
			for (int i = 0; i < pizzaLabel.length; i++) {
				pizzaLabel[i] = new JLabel();
				pizzaLabel[i].setPreferredSize(new Dimension(95, 95));
				gbc = new GridBagConstraints();
				gbc.gridx = i%2;
				gbc.gridy = i/2;
				if(i==1||i==pizzaLabel.length-1)gbc.insets = new Insets(5,10,10,40);
				else gbc.insets = new Insets(5,10,10,10);
				ovenPanel.add(pizzaLabel[i], gbc);				
			}
			
			addPizzaButton = new JButton();
			addPizzaButton.setOpaque(false);
			addPizzaButton.setFont(new Font("Aerial", Font.ITALIC, 14));
			addPizzaButton.setText("Add");
			addPizzaButton.setBackground(new Color(40,40,40));
			addPizzaButton.setForeground(new Color(0xD8B400));
			addPizzaButton.setEnabled(false);
			addPizzaButton.setFocusable(false);
			addPizzaButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(0x808080), Color.BLACK));
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.ipadx = 20;
			gbc.ipady = 5;
			gbc.insets = new Insets(10,15,0,10);
			ovenPanel.add(addPizzaButton, gbc);
			addPizzaButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					addPizzaButtonActionPerformed(e);
				}
			});
			
			bakePizzaButton = new JButton();
			bakePizzaButton.setOpaque(false);
			bakePizzaButton.setBackground(new Color(40,40,40));
			bakePizzaButton.setForeground(new Color(0xD8B400));
			bakePizzaButton.setFont(new Font("Aerial", Font.ITALIC,14));
			bakePizzaButton.setText("Bake");
			bakePizzaButton.setEnabled(false);
			bakePizzaButton.setFocusable(false);
			bakePizzaButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(0x808080), Color.BLACK));
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.ipadx = 10;
			gbc.ipady = 5;
			gbc.insets = new Insets(10,0,0,30);
			ovenPanel.add(bakePizzaButton, gbc);
			bakePizzaButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					bakePizzaButtonActionPerformed(e);
				}
			});
			
			readyPanel = new JPanel();
			readyPanel.setPreferredSize(new Dimension(140, 50));	
			readyPanel.setOpaque(true);
			readyPanel.setLayout(new GridBagLayout());
			readyPanel.setBackground(new Color(0x282828));
			simpleBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK, new Color(0xC6A500));
			titledBorder = BorderFactory.createTitledBorder(simpleBorder, "Baked", 1, 2, 
					new Font("Arial", Font.PLAIN, 14), Color.LIGHT_GRAY);
			readyPanel.setBorder(titledBorder);			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 3;			
			gbc.insets = new Insets(5, 5, 5, 5);
			managementPanel.add(readyPanel, gbc);
			
			readyLabel = new JLabel();
			readyLabel.setText("0");			
			readyLabel.setForeground(Color.LIGHT_GRAY);
			readyLabel.setFont(new Font("Arial", Font.BOLD, 18));
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(0,0,5,0);
			readyPanel.add(readyLabel, gbc);
			
			inCarPanel = new JPanel();
			inCarPanel.setPreferredSize(new Dimension(140, 50));
			inCarPanel.setOpaque(true);
			inCarPanel.setLayout(new GridBagLayout());
			inCarPanel.setBackground(new Color(0x282828));
			simpleBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK, new Color(0xC6A500));
			titledBorder = BorderFactory.createTitledBorder(simpleBorder, "Car trunk", 1, 2, 
					new Font("Arial", Font.PLAIN, 14), Color.LIGHT_GRAY);
			inCarPanel.setBorder(titledBorder);			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 3;			
			gbc.insets = new Insets(5, 5, 5, 5);
			managementPanel.add(inCarPanel, gbc);
			
			inCarLabel = new JLabel();
			inCarLabel.setText("0");			
			inCarLabel.setForeground(Color.LIGHT_GRAY);
			inCarLabel.setFont(new Font("Arial", Font.BOLD, 18));
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0.9;
			gbc.insets = new Insets(0,20,5,0);
			inCarPanel.add(inCarLabel, gbc);
			
			loadCarButton = new JButton();			
			loadCarButton.setOpaque(false);
			loadCarButton.setBackground(new Color(40,40,40));
			loadCarButton.setForeground(new Color(0xD8B400));
			loadCarButton.setFont(new Font("Aerial", Font.BOLD,14));
			loadCarButton.setText("Load");
			loadCarButton.setEnabled(false);
			loadCarButton.setFocusable(false);
			loadCarButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(0x808080), Color.BLACK));
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.ipadx = 5;
			gbc.weightx = 0.1;
			gbc.insets = new Insets(0,20,10,0);
			inCarPanel.add(loadCarButton, gbc);
			loadCarButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					loadCarButtonActionPerformed(e);
				}
			});
			
			muteSoundCheckBox = new JCheckBox("Mute", false);
			muteSoundCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
			muteSoundCheckBox.setFont(new Font("TAHOMA", Font.BOLD, 14));
			muteSoundCheckBox.setOpaque(false);
			muteSoundCheckBox.setForeground(new Color(0xFFFFFF));			
			muteSoundCheckBox.setFocusable(false);
			muteSoundCheckBox.setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 4;
			gbc.insets = new Insets(10, 10, 10, 10);
			gbc.anchor = GridBagConstraints.EAST;
			managementPanel.add(muteSoundCheckBox, gbc);
			muteSoundCheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					muteSoundCheckBoxItemStateChanged(e);				
				}			
			});
			
			buttonsPanel = new JPanel();
			buttonsPanel.setPreferredSize(new Dimension(295,100));
			buttonsPanel.setLayout(new GridBagLayout());
			buttonsPanel.setBackground(new Color(40,40,40,120));
			buttonsPanel.setOpaque(true);
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 5;
			gbc.insets = new Insets(10,0,10,0);
			gbc.gridwidth = 2;
			managementPanel.add(buttonsPanel, gbc);
			
			startPauseButton = new JButton();
			startPauseButton.setPreferredSize(new Dimension(50,25));
			startPauseButton.setOpaque(true);
			startPauseButton.setBackground(new Color(40,40,40));
			startPauseButton.setForeground(Color.WHITE);
			startPauseButton.setFont(new Font("Aerial", Font.BOLD,14));
			startPauseButton.setText("Start");			
			startPauseButton.setFocusable(false);
			startPauseButton.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED, new Color(0x930000), 
					Color.BLACK));
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.ipadx = 30;
			gbc.ipady = 5;
			gbc.weightx = 0.5;
			gbc.weighty = 0.5;
			gbc.insets = new Insets(0,0,0,1);
			buttonsPanel.add(startPauseButton, gbc);
			startPauseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					startPauseButtonActionPerformed(e);
				}
			});
			
			exitStopButton = new JButton();
			exitStopButton.setPreferredSize(new Dimension(50,25));
			exitStopButton.setOpaque(true);
			exitStopButton.setBackground(new Color(40,40,40));
			exitStopButton.setForeground(Color.WHITE);
			exitStopButton.setFont(new Font("Aerial", Font.BOLD,14));
			exitStopButton.setText("Exit");			
			exitStopButton.setFocusable(false);
			exitStopButton.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED, new Color(0x267400), 
					Color.BLACK));
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.ipadx = 30;
			gbc.ipady = 5;
			gbc.weightx = 0.5;
			gbc.weighty = 0.5;
			gbc.insets = new Insets(0,1,0,0);
			buttonsPanel.add(exitStopButton, gbc);
			exitStopButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					exitStopButtonActionPerformed(e);
				}
			});
			
			mainFrame.pack();
			mainFrame.setLocationRelativeTo(null);
			mainFrame.setVisible(true);			
			GUIBorderFlash();
			
			//main logic
			clockTimer = new Timer(mSecPerMin, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					clockTimerActionPerformed(e);
				}
			});			
			
			pizzas = new int[21][21];
			orderTime = new int[21][21];
			onTimeDeliveryColor = new Color(0, 100, 0);
			lateDeliveryColor = new Color(100, 0, 0);
			
			phoneTimer = new Timer(mSecPerMin, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					phoneTimerActionPerformed(e);
				}
			});			
			
			displayTimer = new Timer(2000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ordersPanel.setImage(new ImageIcon("image\\misc\\orderDisplayImage.jpg").getImage());
				}
			});
			
			ovenTimer = new Timer(mSecPerMin, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ovenTimerActionPerformed(e);
				}
			});
			
			carTimer = new Timer(mSecPerMin * minPer20Squares / 20, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					carTimerActionPerformed(e);			
				}
			});
			
		}else {System.exit(0);}
	}	
	
	public static void main(String[] args) {
		new FoodDelivery();		
		
		GameSoundPlayback gameSoundPlayback = new GameSoundPlayback();
		gameSoundThread = new Thread(gameSoundPlayback);
		gameSoundThread.setDaemon(true);
		gameSoundThread.start();
	}
	
	public Path filePathRandomizer(Path p) {
		Path s;
		try(DirectoryStream<Path> dstr = Files.newDirectoryStream(p, "*.{jpg,jpeg,gif}")){
			for(Path entry: dstr) {
				directoryFilesList.add(entry.getName(p.getNameCount()));
			}
		}catch(InvalidPathException e) { 
		      System.out.println("Path Error " + e); 
	    } catch(NotDirectoryException e) { 
	      System.out.println(p + " is not a directory."); 
	    } catch (IOException e) { 
	      System.out.println("I/O Error: " + e); 
	    }
		
		int randomInt = random.nextInt(directoryFilesList.size());
		s = directoryFilesList.get(randomInt);
		directoryFilesList.clear();
		return s;
	}
	
	public void deliveryGridMousePressed(MouseEvent e) {
		// determine which grid element was clicked
		Point p = e.getComponent().getLocation();
		// determine indicies based on p
		boolean matchFound = false;
		if (!carTimer.isRunning() && !loadCarButton.isEnabled() &&	startPauseButton.getText().equals("Pause")) {
			for (deliveryR = 1; deliveryR < 21; deliveryR++) {
				for (deliveryC = 1; deliveryC < 21; deliveryC++) {
					if (p.x == deliveryGrid[deliveryC][deliveryR].getX() && p.y == deliveryGrid[deliveryC][deliveryR].getY()){
						matchFound = true;
						break;
					}
				}
					if (matchFound)	break;
			}
			deltaC = deliveryC - carC;
			deltaR = deliveryR - carR;
			if (deltaC == 0 && deltaR == 0)	return;
			messageTextArea.setText("Car Going To: " + display(deliveryC, deliveryR));
			carTimer.start();
		}
	}
	
	public void addPizzaButtonActionPerformed(ActionEvent e) {
		pizzasBaking++;
		totalPizzasBaked++;		
		pizzaIcon = new ImageIcon(foodDirectory.resolve(filePathRandomizer(foodDirectory)).toString());
		pizzaLabel[pizzasBaking - 1].setIcon(new ImageIcon(pizzaIcon.getImage().getScaledInstance(95, 95,Image.SCALE_SMOOTH)));
		if (pizzasBaking == pizzasBakingMax)
		addPizzaButton.setEnabled(false);
	}
	
	public void bakePizzaButtonActionPerformed(ActionEvent e) {
		int hOut, mOut;
		if (pizzasBaking == 0)
			return;
		ovenPanel.setImage(new ImageIcon("image\\misc\\ovenOn.jpg").getImage());
		addPizzaButton.setEnabled(false);
		bakePizzaButton.setEnabled(false);
		hOut = clockHour;
		mOut = clockMinute + bakingTime;
		if (mOut > 59){
			mOut -= 60;
			hOut++;
		}
		String t;
		if (pizzasBaking == 1)t = "Pizza Out ~ ";
		else t = "Baked At ~ ";
		t += String.valueOf(hOut) + ":";
		if (mOut < 10) t += "0";
		t += String.valueOf(mOut);
		titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, 
				Color.DARK_GRAY, Color.BLACK), t, 1, 2, 
					new Font("Arial", Font.PLAIN, 14), Color.ORANGE);
		ovenPanel.setBorder(titledBorder);
		bakingMinutesLeft = bakingTime;
		ovenTimer.start();
	}
	
	public void loadCarButtonActionPerformed(ActionEvent e) {
		if (pizzasReady == 0) return;
		if (pizzasReady > pizzasInCarMax){
			pizzasInCar += pizzasInCarMax;
			pizzasReady -= pizzasInCarMax;
		} else	{
			pizzasInCar += pizzasReady;
			pizzasReady = 0;
		}
		readyLabel.setText(String.valueOf(pizzasReady));
		inCarLabel.setText(String.valueOf(pizzasInCar));
		loadCarButton.setEnabled(false);
	}
	
	public void muteSoundCheckBoxItemStateChanged(ItemEvent e) {		
		muteSoundCheckBoxClicksCounter +=1;
		muteSoundCheckBox.setText("Unmute");
		GameSoundPlayback.gainControl = (FloatControl) GameSoundPlayback.backgroundSoundClip.getControl(FloatControl.Type.MASTER_GAIN);
		GameSoundPlayback.gainControl.setValue(-80.0f);		
		GameSoundPlayback.backgroundSoundClip.loop(Clip.LOOP_CONTINUOUSLY);
		if(muteSoundCheckBoxClicksCounter%2==0) {
			muteSoundCheckBox.setText("Mute");
			muteSoundCheckBoxClicksCounter=0;
			GameSoundPlayback.gainControl.setValue(-15.0f);			
			GameSoundPlayback.backgroundSoundClip.loop(Clip.LOOP_CONTINUOUSLY);			
		}
	}
	
	public void startPauseButtonActionPerformed(ActionEvent e) {
		if(startPauseButton.getText().equals("Start")) {
			startPauseButton.setText("Pause");
			exitStopButton.setText("Stop");	
			managementPanel.setImage(new ImageIcon("image\\misc\\managementPanelImageBW.jpg").getImage());
			// clear grid
			for (int i = 1; i < 21; i++){
				for (int j = 1; j < 21; j++){
					deliveryGrid[i][j].setBackground(new Color(50,50,50));
					deliveryGrid[i][j].setText("");
					pizzas[i][j] = 0;
					orderTime[i][j] = 0;
				}
			}
			ordersListModel.removeAllElements();
			for (int i = 0; i < pizzaLabel.length; i++)	pizzaLabel[i].setIcon(null);
			pizzasBaking = 0;
			pizzasReady = pizzasBakingMax;
			totalPizzasBaked = pizzasReady;
			readyLabel.setText(String.valueOf(pizzasReady));
			addPizzaButton.setEnabled(true);
			bakePizzaButton.setEnabled(true);
			pizzasInCar = 0;
			inCarLabel.setText("0");
			loadCarButton.setEnabled(true);
			// initialize pizza parlor and car location
			deliveryGrid[carC][carR].setIcon(null);
			deliveryGrid[pizzaC][pizzaR].setIcon(null);
			pizzaC = 2 + random.nextInt(18);
			pizzaR = 2 + random.nextInt(18);
			deliveryGrid[pizzaC][pizzaR].setIcon(new ImageIcon(parlorIcon.getImage().
					getScaledInstance(deliveryGrid[pizzaC][pizzaR].getWidth(), deliveryGrid[pizzaC][pizzaR].getHeight(),
							Image.SCALE_SMOOTH)));
			carC = pizzaC;
			carR = pizzaR;
			mileage = 0;
			messageTextArea.setText(" Car at Pizza Parlor: " + display(carC, carR));
			pizzasOnTime = 0;
			pizzasLate = 0;
			missedDeliveries = 0;
			totalSales = 0;
			salesLabel.setText("$0");
			clockHour = 6;
			clockMinute = 0;
			timeLabel.setText("6:00");
			orderNumber = 1;
			clockTimer.start();
			phoneTimer.setDelay(mSecPerMin * (2 + random.nextInt(7)));
			phoneTimer.start();
		}else if(startPauseButton.getText().equals("Pause")) {
			startPauseButton.setText("Continue");
			exitStopButton.setEnabled(false);
			clockTimer.stop();
			phoneTimer.stop();
			ovenGoing = ovenTimer.isRunning();
			ovenTimer.stop();
			addPizzaButton.setEnabled(false);
			bakePizzaButton.setEnabled(false);
			loadCarButtonEnabled = loadCarButton.isEnabled();
			loadCarButton.setEnabled(false);
			carGoing = carTimer.isRunning();
			carTimer.stop();
		}else {
			//restarted
			startPauseButton.setText("Pause");
			exitStopButton.setEnabled(true);
			clockTimer.start();
			phoneTimer.start();
			if (ovenGoing) ovenTimer.start();
			addPizzaButton.setEnabled(!ovenGoing);
			bakePizzaButton.setEnabled(!ovenGoing);
			loadCarButton.setEnabled(loadCarButtonEnabled);
			if (carGoing) carTimer.start();
		}
	}
	
	public void exitStopButtonActionPerformed(ActionEvent e) {
		if(exitStopButton.getText().equals("Stop")) {
			GameSoundPlayback.gameOverSoundClip.setFramePosition(0);
			GameSoundPlayback.gameOverSoundClip.start();
			GameSoundPlayback.phoneSoundClip.stop();
			GameSoundPlayback.ovenTimerSoundClip.stop();
			GameSoundPlayback.honkSoundClip.stop();			
			
			exitStopButton.setText("Exit");
			startPauseButton.setText("Start");	
			managementPanel.setImage(new ImageIcon("image\\misc\\managementPanelImage.jpg").getImage());
			clockTimer.stop();
			phoneTimer.stop();
			ovenTimer.stop();
			titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, 
					Color.DARK_GRAY, Color.BLACK), "ovenOff", 1, 2, 
						new Font("Arial", Font.PLAIN, 14), Color.RED);
			ovenPanel.setBorder(titledBorder);
			ovenPanel.setImage(new ImageIcon("image\\misc\\ovenOff.jpg").getImage());
			for (int i = 0; i < pizzaLabel.length; i++) {
				pizzaLabel[i].setIcon(null);
			}
			addPizzaButton.setEnabled(false);
			bakePizzaButton.setEnabled(false);
			loadCarButton.setEnabled(false);
			carTimer.stop();
			SalesResults salesResults = new SalesResults(clockHour, clockMinute,
					pizzasOnTime, netSoldPizza, pizzasLate, netLatePizza, totalPizzasBaked, pizzaCost,
					mileage, mileageCost, missedDeliveries, costMissedPizza);
		} else {
			tmp.delete();
			System.exit(0);
		}	
	}
	
	public void clockTimerActionPerformed(ActionEvent e) {		
		int clockMinutes;
		int c, r;
		String st="";
		String s;
		String se;
		boolean expired = false;
		clockMinute++;
		if(clockMinute > 59) {
			clockMinute = 0;
			clockHour++;
			if(clockHour == 11) {
				timeLabel.setText("11:00");
				exitStopButton.doClick();
				return;
			}
		}
		String t = String.valueOf(clockHour)+":";
		if(clockMinute<10)t+="0";
		timeLabel.setText(t+String.valueOf(clockMinute));	
		// check for late orders - check to if first is expired
		clockMinutes = clockMinute + 60 * clockHour;
		if (ordersListModel.getSize() != 0) {
			for (int i = 0; i < ordersListModel.getSize(); i++) {
				st = String.valueOf(ordersListModel.getElementAt(i));
				c = ((int) st.charAt(10)) - 64;
				s = st.substring(12, 14);
				se="";
				for (int j = 0; j < s.length(); j++) {
					if(Character.isDigit(s.charAt(j))) se += s.charAt(j);
				}
				r = Integer.parseInt(se);								
				if (i == 0 && clockMinutes - orderTime[c][r] >= orderMaxTime) {
					expired = true;
					deliveryGrid[c][r].setBackground(new Color(50,50,50));
					deliveryGrid[c][r].setText("");
					missedDeliveries += pizzas[c][r];
					pizzas[c][r] = 0;
				} else if (clockMinutes - orderTime[c][r] >= orderLateTime) {
					deliveryGrid[c][r].setBackground(lateDeliveryColor);
				}
			}
			if (expired)ordersListModel.removeElementAt(0);
		}
	}
	
	public void phoneTimerActionPerformed(ActionEvent e) {
		int i, j, k;
		String order;		
		ordersPanel.setImage(new ImageIcon("image\\misc\\orderRecievedDisplayImageBW.jpg").getImage());
		displayTimer.stop();
		displayTimer.start();
		GameSoundPlayback.phoneSoundClip.setFramePosition(0);
		GameSoundPlayback.phoneSoundClip.start();		
		if (clockHour == 10 && clockMinute >= 30){
			phoneTimer.stop();
			return;
		}
		
		do{
			i = 1 + random.nextInt(20);			
			j = 1 + random.nextInt(20);
		}
		while (pizzas[i][j]!=0||deliveryGrid[i][j]==deliveryGrid[pizzaC][pizzaR]);
		k = random.nextInt(100);
		if (k <= 29)
			pizzas[i][j] = 1;
			else if (k <= 49)
			pizzas[i][j] = 2;
			else if (k <= 69)
			pizzas[i][j] = 3;
			else if (k <= 84)
			pizzas[i][j] = 4;
			else
			pizzas[i][j] = 5;
		orderTime[i][j] = clockMinute + 60 * clockHour;
		// build string listing order
		if(orderNumber<10)order ="\s" + String.valueOf(orderNumber) + ".\s\s" + timeLabel.getText() + "\s";
		else order = String.valueOf(orderNumber) + ".\s\s" + timeLabel.getText() + "\s";
		if(timeLabel.getText().length()==5)order = String.valueOf(orderNumber) + ".\s" + timeLabel.getText() + "\s";
		orderNumber++;
		//if (timeLabel.getText().length() == 4) order = " " + order;
		order += String.valueOf((char) (i + 64)) + "\s";
		if (j < 10)	order += "\s";
		order += String.valueOf(j) +"->"+String.valueOf(pizzas[i][j]);
		ordersListModel.addElement(order);
		deliveryGrid[i][j].setBackground(onTimeDeliveryColor);
		deliveryGrid[i][j].setText(String.valueOf(pizzas[i][j]));
		phoneTimer.setDelay(mSecPerMin*(2 + random.nextInt(6)));		
	}
	
	public void ovenTimerActionPerformed(ActionEvent e) {
		if (bakingMinutesLeft != 0) bakingMinutesLeft--;
		else {
			GameSoundPlayback.ovenTimerSoundClip.setFramePosition(0);
			GameSoundPlayback.ovenTimerSoundClip.start();
			ovenTimer.stop();
			pizzasReady += pizzasBaking;
			if (pizzasReady > pizzasReadyMax) pizzasReady = pizzasReadyMax;
			readyLabel.setText(String.valueOf(pizzasReady));
			titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, 
					Color.DARK_GRAY, Color.BLACK), "ovenOff", 1, 2, 
						new Font("Arial", Font.PLAIN, 14), Color.RED);
			ovenPanel.setBorder(titledBorder);
			ovenPanel.setImage(new ImageIcon("image\\misc\\ovenOff.jpg").getImage());
			for (int i = 0; i < pizzaLabel.length; i++) {
				pizzaLabel[i].setIcon(null);
			}
			pizzasBaking = 0;
			addPizzaButton.setEnabled(true);
			bakePizzaButton.setEnabled(true);
		}
	}
	
	public void carTimerActionPerformed(ActionEvent e) {
		int i, c, r;
		String st;
		String s;
		String se="";
		ImageIcon carIcon = null;
		if(deliveryGrid[carC][carR]!=deliveryGrid[pizzaC][pizzaR])deliveryGrid[carC][carR].setIcon(null);
		// move horizontally first
		if(deltaC!=0) {
			mileage++;
			if(deltaC>0) {
				carC++;
				carIcon = rightCarIcon;							
			} else {
				carC--;	
				carIcon = leftCarIcon;							
			}
			deltaC = deliveryC-carC;
		} else {
			if(deltaR!=0) {
				mileage++;
				if(deltaR>0) {
					carR++;	
					carIcon = downCarIcon;									
				} else {
					carR--;
					carIcon = upCarIcon;
				}
				deltaR = deliveryR-carR;
			}
		}
		
		deliveryGrid[carC][carR].setIcon(new ImageIcon(carIcon.getImage().getScaledInstance(deliveryGrid[carC][carR].
								getWidth(), deliveryGrid[carC][carR].getHeight(),Image.SCALE_AREA_AVERAGING)));
		if(deliveryGrid[carC][carR]==deliveryGrid[pizzaC][pizzaR]) {
			deliveryGrid[carC][carR].setIcon(new ImageIcon(parlorIcon.getImage().getScaledInstance(deliveryGrid[carC][carR].
					getWidth(), deliveryGrid[carC][carR].getHeight(),Image.SCALE_AREA_AVERAGING)));
		}
		if (carC == deliveryC && carR == deliveryR) {
			GameSoundPlayback.honkSoundClip.setFramePosition(0);
			GameSoundPlayback.honkSoundClip.start();
			messageTextArea.setText("Car at " + display(deliveryC, deliveryR));
			if (pizzas[deliveryC][deliveryR] == 0){
				messageTextArea.setText("Car at " + display(deliveryC, deliveryR)+"\nNo orders here");
			} else {
				if (pizzas[deliveryC][deliveryR] > pizzasInCar) {
					messageTextArea.append("\n Incomplete order");
				} else {
					messageTextArea.append("\n Delivered " + String.valueOf(pizzas[deliveryC][deliveryR]) + " Item");
					if(pizzas[deliveryC][deliveryR]>1)messageTextArea.append("s");
					// see if on-time
					if ((clockMinute + 60*clockHour) - orderTime[deliveryC][deliveryR] <= orderLateTime) {
						messageTextArea.append(": On-Time");
						totalSales += pizzas[deliveryC][deliveryR]*netSoldPizza;
						pizzasOnTime += pizzas[deliveryC][deliveryR];						
					} else {
						messageTextArea.append(": Delayed!");
						totalSales += pizzas[deliveryC][deliveryR]*netLatePizza;
						pizzasLate += pizzas[deliveryC][deliveryR];							
					}
					salesLabel.setText("$" + String.valueOf(totalSales));
					pizzasInCar -= pizzas[deliveryC][deliveryR];
					inCarLabel.setText(String.valueOf(pizzasInCar));
					pizzas[deliveryC][deliveryR] = 0;
					deliveryGrid[deliveryC][deliveryR].setBackground(new Color(50,50,50));
					deliveryGrid[deliveryC][deliveryR].setText("");
					// remove from list
					for (i = 0; i < ordersListModel.getSize(); i++) {
						st="";
						st = String.valueOf(ordersListModel.getElementAt(i));
						c = ((int) st.charAt(10)) - 64;						
						s = st.substring(12, 14);	
						se="";
						for (int j = 0; j < s.length(); j++) {
							if(Character.isDigit(s.charAt(j))) se += s.charAt(j);
						}
						r = Integer.parseInt(se);
						if (c == deliveryC && r == deliveryR)break;																
					}ordersListModel.removeElementAt(i);	
				}	
			}
			carTimer.stop();
			if (carC == pizzaC && carR == pizzaR) {
				messageTextArea.setText("Car at Food Parlor: " + display(pizzaC, pizzaR));
				deliveryGrid[carC][carR].setIcon(new ImageIcon(parlorIcon.getImage().getScaledInstance(deliveryGrid[pizzaC][pizzaR].
						getWidth(), deliveryGrid[pizzaC][pizzaR].getHeight(), Image.SCALE_SMOOTH)));
				pizzasInCar = 0;
				inCarLabel.setText("0");
				loadCarButton.setEnabled(true);
			}
		} else {
			messageTextArea.setText("Car at " + display(carC, carR));			
		}
	}
	
	private String display(int c, int r){
		return (String.valueOf((char)(c + 64)) + " " + String.valueOf(r));
	}
	
		
	public void GUIBorderFlash() {
		int delay = 10000; 	
		  ActionListener taskPerformer = new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		    	  incrementR = random.nextInt(255);
		    	  incrementG = random.nextInt(255);
		    	  incrementB = random.nextInt(255);
		    	  ordersScrollPane.setBorder(BorderFactory.createLineBorder(new Color((incrementR+150)%255, 
		    			  (incrementG+100)%255, (incrementB+50)%255)));
		    	  bufferPanel.setBorder(BorderFactory.createMatteBorder(0,0,0,2, 
		    			  new Color(incrementR, incrementG, incrementB)));
		      }
		  };new Timer(delay, taskPerformer).start();
	}
}
