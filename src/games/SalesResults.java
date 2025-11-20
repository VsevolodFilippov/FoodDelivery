package games;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class SalesResults extends JDialog{
	Random colorRandom;
	private int R;
	private int G;
	private int B;
	private JDialog salesResultsDialog;
	private ImagePanel salesResultImagePanel;
	private ImagePanel shadePanel;
	private JLabel gridTopLabel;
	private JLabel gridMiddleLabel;
	private JLabel gridBottomLabel;
	private JLabel startTimeLabel;
	private JLabel stopTimeLabel;
	private JLabel salesHeaderLabel;
	private JLabel onTimeLabel;
	private JLabel onTimeSalesLabel;
	private JLabel lateLabel;
	private JLabel lateSalesLabel;
	private JLabel totalSalesHeaderLabel;
	private JLabel totalSalesLabel;
	private JLabel costsHeaderLabel;
	private JLabel bakedLabel;
	private JLabel bakedCostsLabel;
	private JLabel milesLabel;
	private JLabel milesCostsLabel;
	private JLabel missedLabel;
	private JLabel missedCostsLabel;
	private JLabel totalCostsHeaderLabel;
	private JLabel totalCostsLabel;
	private JLabel totalProfitsLabel;
	private JLabel hourlyProfitsLabel;	
	private JButton returnButton;
	private int totalSales;
	private int totalCosts;
	private int bestScore;
	
	public SalesResults(int clockHour, int clockMinute, int pizzasOnTime, int netSoldPizza, 
			int	pizzasLate,	int netLatePizza, int totalPizzasBaked, int pizzaCost, int mileage, 
			double	mileageCost, int missedDeliveries, int costMissedPizza) {		
		colorRandom = new Random();
		R=colorRandom.nextInt(255);
		G=colorRandom.nextInt(255);
		B=colorRandom.nextInt(255);
		GridBagConstraints gridConstraints = new GridBagConstraints();	
		// frame constructor		
		salesResultsDialog = new JDialog();
		salesResultsDialog.setTitle("Sales results");
		salesResultsDialog.setResizable(false);
		salesResultsDialog.setModal(true);
		salesResultsDialog.setIconImage(Toolkit.getDefaultToolkit().createImage("image\\icon\\Emblem016.png"));
		salesResultsDialog.setLayout(new GridBagLayout());		
		salesResultsDialog.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent evt){
				exitForm(evt);
			}
		});
		
		salesResultImagePanel = new ImagePanel (new ImageIcon((FoodDelivery.parlorIcon).getImage().
				getScaledInstance(800, 750, Image.SCALE_SMOOTH)).getImage());
		salesResultImagePanel.setPreferredSize(new Dimension(800,700));
		salesResultImagePanel.setLayout(new GridBagLayout());
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		salesResultsDialog.add(salesResultImagePanel, gridConstraints);
		
		shadePanel = new ImagePanel(new ImageIcon("image\\misc\\shadeImage.png").getImage());
		shadePanel.setPreferredSize(new Dimension(790,690));
		shadePanel.setLayout(new GridBagLayout());
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		salesResultImagePanel.add(shadePanel, gridConstraints);
		
		gridTopLabel = new JLabel();
		gridTopLabel.setPreferredSize(new Dimension(700, 5));
		gridTopLabel.setOpaque(true);
		gridTopLabel.setBackground(new Color(R, G, B));
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 1;
		gridConstraints.
		gridwidth = 5;
		gridConstraints.insets = new Insets(5, 5, 0, 5);
		shadePanel.add(gridTopLabel, gridConstraints);
		
		gridMiddleLabel = new JLabel();
		gridMiddleLabel.setPreferredSize(new Dimension(5, 500));
		gridMiddleLabel.setOpaque(true);
		gridMiddleLabel.setBackground(new Color(R, G, B));
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 2;
		gridConstraints.gridy = 2;
		gridConstraints.gridheight = 5;
		gridConstraints.insets = new Insets(0, 5, 0, 5);
		shadePanel.add(gridMiddleLabel, gridConstraints);
		
		gridBottomLabel = new JLabel();
		gridBottomLabel.setPreferredSize(new Dimension(700, 5));
		gridBottomLabel.setOpaque(true);
		gridBottomLabel.setBackground(new Color(R, G, B));
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 7;
		gridConstraints.gridwidth = 5;
		gridConstraints.insets = new Insets(0, 5, 5, 5);
		shadePanel.add(gridBottomLabel, gridConstraints);
		
		startTimeLabel = new JLabel();
		startTimeLabel.setText("Start Time: 6:00");
		startTimeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		startTimeLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		gridConstraints.gridwidth = 2;
		gridConstraints.insets = new Insets(10, 5, 0, 0);
		shadePanel.add(startTimeLabel, gridConstraints);
		
		stopTimeLabel = new JLabel();
		stopTimeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		stopTimeLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 3;
		gridConstraints.gridy = 0;
		gridConstraints.gridwidth = 2;
		gridConstraints.insets = new Insets(10, 0, 0, 0);
		shadePanel.add(stopTimeLabel, gridConstraints);
		
		salesHeaderLabel = new JLabel();
		salesHeaderLabel.setText("Sales:");
		salesHeaderLabel.setFont(new Font("Arial", Font.BOLD, 18));
		salesHeaderLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 2;
		gridConstraints.anchor = GridBagConstraints.WEST;
		gridConstraints.insets = new Insets(10, 10, 0, 0);
		shadePanel.add(salesHeaderLabel, gridConstraints);
		
		onTimeLabel = new JLabel();
		onTimeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		onTimeLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 3;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 10, 0, 0);
		shadePanel.add(onTimeLabel, gridConstraints);
		
		onTimeSalesLabel = new JLabel();
		onTimeSalesLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		onTimeSalesLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 3;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 10, 0, 0);
		shadePanel.add(onTimeSalesLabel, gridConstraints);
		
		lateLabel = new JLabel();
		lateLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		lateLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 4;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 10, 0, 0);
		shadePanel.add(lateLabel, gridConstraints);
		
		lateSalesLabel = new JLabel();
		lateSalesLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		lateSalesLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 4;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 10, 0, 0);
		shadePanel.add(lateSalesLabel, gridConstraints);
		
		totalSalesHeaderLabel = new JLabel();
		totalSalesHeaderLabel.setText("Total Sales");
		totalSalesHeaderLabel.setFont(new Font("Arial", Font.BOLD, 18));
		totalSalesHeaderLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 6;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 10, 0, 0);
		shadePanel.add(totalSalesHeaderLabel, gridConstraints);
		
		totalSalesLabel = new JLabel();
		totalSalesLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		totalSalesLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 6;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 10, 0, 0);
		shadePanel.add(totalSalesLabel, gridConstraints);
		
		costsHeaderLabel = new JLabel();
		costsHeaderLabel.setText("Costs:");
		costsHeaderLabel.setFont(new Font("Arial", Font.BOLD, 18));
		costsHeaderLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 3;
		gridConstraints.gridy = 2;
		gridConstraints.anchor = GridBagConstraints.WEST;
		gridConstraints.insets = new Insets(10, 0, 0, 0);
		shadePanel.add(costsHeaderLabel, gridConstraints);
		
		bakedLabel = new JLabel();
		bakedLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		bakedLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 3;
		gridConstraints.gridy = 3;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 0, 0, 10);
		shadePanel.add(bakedLabel, gridConstraints);
		
		bakedCostsLabel = new JLabel();
		bakedCostsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		bakedCostsLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 4;
		gridConstraints.gridy = 3;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 0, 0, 10);
		shadePanel.add(bakedCostsLabel, gridConstraints);
		
		milesLabel = new JLabel();
		milesLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		milesLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 3;
		gridConstraints.gridy = 4;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 0, 0, 10);
		shadePanel.add(milesLabel, gridConstraints);
		
		milesCostsLabel = new JLabel();
		milesCostsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		milesCostsLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 4;
		gridConstraints.gridy = 4;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 0, 0, 10);
		shadePanel.add(milesCostsLabel, gridConstraints);
		
		missedLabel = new JLabel();
		missedLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		missedLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 3;
		gridConstraints.gridy = 5;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 0, 0, 10);
		shadePanel.add(missedLabel, gridConstraints);
				
		missedCostsLabel = new JLabel();
		missedCostsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		missedCostsLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 4;
		gridConstraints.gridy = 5;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 0, 0, 10);
		shadePanel.add(missedCostsLabel, gridConstraints);
		
		totalCostsHeaderLabel = new JLabel();
		totalCostsHeaderLabel.setText("Total Costs");
		totalCostsHeaderLabel.setFont(new Font("Arial", Font.BOLD, 18));
		totalCostsHeaderLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 3;
		gridConstraints.gridy = 6;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 0, 0, 10);
		shadePanel.add(totalCostsHeaderLabel, gridConstraints);
		
		totalCostsLabel = new JLabel();
		totalCostsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		totalCostsLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 4;
		gridConstraints.gridy = 6;
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.insets = new Insets(10, 0, 0, 10);
		shadePanel.add(totalCostsLabel, gridConstraints);
		
		totalProfitsLabel = new JLabel();
		totalProfitsLabel.setFont(new Font("Arial", Font.BOLD, 18));
		totalProfitsLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 8;
		gridConstraints.gridwidth = 2;
		gridConstraints.insets = new Insets(5, 10, 30, 0);
		shadePanel.add(totalProfitsLabel, gridConstraints);
		
		hourlyProfitsLabel = new JLabel();
		hourlyProfitsLabel.setFont(new Font("Arial", Font.BOLD, 18));
		hourlyProfitsLabel.setForeground(Color.WHITE);
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 3;
		gridConstraints.gridy = 8;
		gridConstraints.gridwidth = 2;
		gridConstraints.insets = new Insets(5, 10, 30, 0);
		shadePanel.add(hourlyProfitsLabel, gridConstraints);		
		
		returnButton = new JButton();
		returnButton.setText("Return");
		returnButton.setPreferredSize(new Dimension(80,30));
		returnButton.setBorder(BorderFactory.createMatteBorder(1, 0, 2, 0, new Color(0xA0001A)));
		returnButton.setOpaque(true);
		returnButton.setBackground(new Color(40,40,40));
		returnButton.setForeground(Color.WHITE);
		returnButton.setFont(new Font("Aerial", Font.BOLD,14));
		returnButton.setFocusable(false);		
		gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 9;
		gridConstraints.gridwidth = 5;
		gridConstraints.insets = new Insets(10, 0, 5, 0);
		shadePanel.add(returnButton, gridConstraints);
		returnButton.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent e){
				returnButtonActionPerformed(e);
			}
		});
		
		if (clockMinute < 10) {
			stopTimeLabel.setText("Stop Time: " + String.valueOf(clockHour) + ":0" +
			String.valueOf(clockMinute));
		} else {
			stopTimeLabel.setText("Stop Time: " + String.valueOf(clockHour) + ":" +
			String.valueOf(clockMinute));
		}
		
		onTimeLabel.setText(String.valueOf(pizzasOnTime) + " On-Time Deliveries");
		onTimeSalesLabel.setText("$" + String.valueOf(pizzasOnTime * netSoldPizza));
		lateLabel.setText(String.valueOf(pizzasLate) + " Late Deliveries");
		lateSalesLabel.setText("$" + String.valueOf(pizzasLate * netLatePizza));
		totalSales = pizzasOnTime * netSoldPizza + pizzasLate * netLatePizza;
		totalSalesLabel.setText("$" + String.valueOf(totalSales));
		bakedLabel.setText(String.valueOf(totalPizzasBaked) + " Food Items Made");
		bakedCostsLabel.setText("$" + String.valueOf(totalPizzasBaked * pizzaCost));
		milesLabel.setText(String.valueOf(mileage/4) + " Miles Driven");
		milesCostsLabel.setText("$" + String.valueOf((int) (mileage * mileageCost)));
		missedLabel.setText(String.valueOf(missedDeliveries) + " Missed Deliveries");
		missedCostsLabel.setText("$" + String.valueOf(missedDeliveries * costMissedPizza));
		totalCosts = (int) (totalPizzasBaked * pizzaCost + mileage * mileageCost +
		missedDeliveries * costMissedPizza);
		totalCostsLabel.setText("$" + String.valueOf(totalCosts));
		bestScoreAnalizer();		
		if (clockHour > 6) {
			double hours = clockHour - 6 + (double) clockMinute / 60;
			hourlyProfitsLabel.setText("Hourly Profits: $" + String.valueOf((int) ((totalSales -
			totalCosts) / hours)));
		}else {
			hourlyProfitsLabel.setText("Time played less then 1 hour");
		}		
		
		salesResultsDialog.pack();
		salesResultsDialog.setLocationRelativeTo(FoodDelivery.mainFrame);	
		salesResultsDialog.setVisible(true);	
		
		
	}
	
	private void exitForm(WindowEvent evt){
		this.dispose();
	}	
	
	public void bestScoreAnalizer() {
		String s = "";
		bestScore = 0;
		try (FileReader fr = new FileReader("files\\properties.file")){
			int c;		 
		      // Read and display the file. 
		      while((c = fr.read()) != -1) { 
		    	  s+=String.valueOf((char)c);
		      }		 		      
		    } catch(IOException e) { 
		      System.out.println("I/O Error: " + e); 
		    }
		for (int i = 0; i < s.length(); i++) {
			if(s.charAt(i)=='=') {
				bestScore=Integer.parseInt(s.substring(i+1, s.length()));
			}
		}		
		if(totalSales - totalCosts>bestScore) {
			bestScore=totalSales - totalCosts;
			totalProfitsLabel.setText("Total Profits: $" + String.valueOf(totalSales - totalCosts)+" RECORD!");
			s="best_score="+String.valueOf(bestScore);
			try ( FileWriter f0 = new FileWriter("files\\properties.file")) { 
			      for (int i=0; i < s.length(); i++) { 
			        f0.write(s.charAt(i)); 
			} 		       
			    } catch(IOException e) { 
			      System.out.println("An I/O Error Occured"); 
			    }
		} else totalProfitsLabel.setText("Total Profits: $" + String.valueOf(totalSales - totalCosts));		
	}
	
	public void returnButtonActionPerformed(ActionEvent e) {
		salesResultsDialog.dispose();
	}
}


