package uk.co.hubza.telecraft;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class telecraft extends JavaPlugin {
	public int lasthour = -1;
	public int currenthour;
	public BossBar bar;
	
	@Override
    public void onEnable() {
		getLogger().info("onEnable has been invoked! The Telecraft plugin is now active!");
        // TODO Insert logic to be performed when the plugin is enabled
		bar = Bukkit.createBossBar(
                ChatColor.DARK_PURPLE + "Initializing...",
                BarColor.PURPLE,
                BarStyle.SOLID);

    	bar.setVisible(true);
		
		new BukkitRunnable() {
            public void run() {
            	for (Player p: Bukkit.getServer().getOnlinePlayers()) {
            		bar.addPlayer(p);
            	}
                
            	Date date = new Date();   // given date
            	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            	calendar.setTime(date);   // assigns calendar to given date 
            	currenthour = calendar.get(Calendar.HOUR_OF_DAY);
            	
            	Date date2 = new Date();   // given date
            	Calendar calendar2 = GregorianCalendar.getInstance(); // creates a new calendar instance
            	calendar2.setTime(date2);   // assigns calendar to given date 
            	calendar2.set(Calendar.HOUR_OF_DAY,currenthour);
            	calendar2.set(Calendar.MINUTE,0);
            	calendar2.set(Calendar.SECOND,0);
            	calendar2.set(Calendar.MILLISECOND,0);
            	calendar2.add(Calendar.HOUR, 1);
            	

            	long currentsec = calendar.getTimeInMillis();
            	Bukkit.broadcastMessage("Currentsec is " + currentsec + ".");
            	long secinhour = calendar2.getTimeInMillis();
            	Bukkit.broadcastMessage("Secinhour is " + secinhour + ".");
            	long nexttel = (secinhour - currentsec) / 1000;
            	
            	if((nexttel / 60 + 1) > 1) {
            	bar.setTitle((nexttel / 60 + 1) + " Minutes until the next teleport.");
            	}else {
            		bar.setTitle(nexttel + " Seconds until the next teleport.");
            	}
            	
            	float barwidth = nexttel / 3600;
            	
                bar.setProgress(barwidth);
                
                Bukkit.broadcastMessage("Intended bar width: " + barwidth);
                
                Bukkit.broadcastMessage("The current hour should be " + currenthour + ".");
                Bukkit.broadcastMessage("Seconds to next teleport is " + nexttel + ".");
                
                Bukkit.broadcastMessage("Last hour is " + lasthour);
                bar.setVisible(true);
                
                if(lasthour == -1){
                    // load file where last hour was saved
                	 Bukkit.broadcastMessage("Last hour was not set. Loading...");
                	 
                	 File myObj = new File("lasthour.txt");
                	 try {
						if (myObj.createNewFile()) {
						 	System.out.println("File created: " + myObj.getName());
						 	Bukkit.broadcastMessage("lasthour.txt file did not exist. Created. Will now write currenthour to the file.");
						 	FileWriter myWriter = new FileWriter("lasthour.txt");
						 	myWriter.write(Integer.toString(currenthour));
						    myWriter.close();
						 } else {
							 Bukkit.broadcastMessage("lasthour.txt already exists. That means that there was most likely a previous shutdown. Loading.");
							 Scanner myReader = new Scanner(myObj);
						     while (myReader.hasNextLine()) {
						        lasthour = Integer.parseInt(myReader.nextLine());
						        Bukkit.broadcastMessage("Read the doc. " + lasthour);
						     }
						     myReader.close();
						 }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	 
                }
                else{
                    if(currenthour == lasthour){
                    	Bukkit.broadcastMessage("lasthour is the same as now. don't run anything");
                    }else{
                    	Bukkit.broadcastMessage("A new hour has began! Time to teleport.");
                        lasthour = currenthour;
                    }
                }
            }
        }.runTaskTimer(this, 0, 20);
    }
    
    @Override
    public void onDisable() {
    	getLogger().info("onDisable has been invoked! The Telecraft plugin is now shutting down.");
    	
    	for (Player p: Bukkit.getServer().getOnlinePlayers()) {
    		bar.removePlayer(p);
    	}
        // TODO Insert logic to be performed when the plugin is disabled
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("test")) {
    		sender.sendMessage("This is a test command. Enjoy the following");
    		sender.sendMessage("You, " + sender.getName() + ", have just ran the command /" + cmd.getName() + " : " + cmd);
    		return true;
    	} //If this has happened the function will return true. 
        
    	return false; 
    }
    
    
}

