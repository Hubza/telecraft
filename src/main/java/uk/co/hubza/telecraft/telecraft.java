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
import java.util.Random;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
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
	
	public void log(String logtext) {
		Bukkit.broadcastMessage("[Telecraft] : " + logtext);
	}
	
	@Override
    public void onEnable() {
		getLogger().info("onEnable has been invoked! The Telecraft plugin is now active!");
		
		try {
			File myObj = new File("teleplayers.txt");
			if (myObj.createNewFile()) {
			 	System.out.println("File created: " + myObj.getName());
			 	log("teleplayers.txt file did not exist. Created for future use.");
			 } else {
				 log("teleplayers.txt already exists. That means that there was most likely a previous shutdown. Reading...");
				 Scanner myReader = new Scanner(myObj);
			     while (myReader.hasNextLine()) {
			        lasthour = Integer.parseInt(myReader.nextLine());
			        log("Read the doc. " + lasthour);
			     }
			     myReader.close();
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
            	log("Currentsec is " + currentsec + ".");
            	long secinhour = calendar2.getTimeInMillis();
            	log("Secinhour is " + secinhour + ".");
            	long nexttel = (secinhour - currentsec) / 1000;
            	
            	if((nexttel / 60 + 1) > 1) {
            		bar.setTitle((nexttel / 60 + 1) + " Minutes until the next teleport.");
            	}else {
            		bar.setTitle(nexttel + " Seconds until the next teleport.");
            	}
            	
            	float barwidth = (float)nexttel / 3600;
            	
                bar.setProgress(barwidth);
                
                log("Intended bar width: " + barwidth);
                
                log("The current hour should be " + currenthour + ".");
                log("Seconds to next teleport is " + nexttel + ".");
                
                log("Last hour is " + lasthour);
                bar.setVisible(true);
                
                if(lasthour == -1){
                    // load file where last hour was saved
                	 log("Last hour was not set. Loading...");
                	 
                	 File myObj = new File("lasthour.txt");
                	 try {
						if (myObj.createNewFile()) {
						 	System.out.println("File created: " + myObj.getName());
						 	log("lasthour.txt file did not exist. Created. Will now write currenthour to the file.");
						 	FileWriter myWriter = new FileWriter("lasthour.txt");
						 	myWriter.write(Integer.toString(currenthour));
						    myWriter.close();
						 } else {
							 log("lasthour.txt already exists. That means that there was most likely a previous shutdown. Loading.");
							 Scanner myReader = new Scanner(myObj);
						     while (myReader.hasNextLine()) {
						        lasthour = Integer.parseInt(myReader.nextLine());
						        log("Read the doc. " + lasthour);
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
                    	log("lasthour is the same as now. don't run anything");
                    }else{
                    	log("A new hour has began! Time to teleport.");
                        lasthour = currenthour;
                        inittele();
                    }
                }
            }
        }.runTaskTimer(this, 0, 20);
    }
    
	@SuppressWarnings("deprecation")
	public void inittele() {
		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
			teleplayer(player);
    	}
	}
	
	public void teleplayer(Player player) {
		Location spawn = Bukkit.getServer().getWorld("world").getSpawnLocation();
		int x = 0;
		int z = 0;
		int maxX= x + 2500;
		int minX= x - 2500;
		int maxZ= z + 2500;
		int minZ= z - 2500;
		Random Xrand = new Random();
		x = Xrand.nextInt(maxX - minX) + minX;
		Random Zrand = new Random();
		z = Zrand.nextInt(maxZ - minZ) + minZ;
		int y = Bukkit.getServer().getWorld("world").getHighestBlockAt(x,z).getY();
		Location loc = new Location(player.getLocation().getWorld(), x, y, z);
		loc.getChunk().load();
		Chunk c = loc.getChunk();
		player.getLocation().getWorld().loadChunk(c);
		player.getLocation().getWorld().refreshChunk(c.getX(), c.getZ());
		loc.setY(loc.getWorld().getHighestBlockYAt(loc));
		player.teleport(loc);
		player.sendMessage(ChatColor.DARK_AQUA + "Teleported to: X: " + ChatColor.GOLD + x + ChatColor.DARK_AQUA + " Z: " + ChatColor.GOLD + z);
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
    	
    	if (cmd.getName().equalsIgnoreCase("tpplayer")) {
    		Player other = Bukkit.getServer().getPlayer(args[0]);
    		 
            if(other == null)
            {
                sender.sendMessage(ChatColor.RED+"Error: Player "+ChatColor.YELLOW+args[0]+ChatColor.RED+" is not online");
                return true;
            }
            else {
            	teleplayer(other);
            	return true;
            }
    	}
    	
        
    	return false; 
    }
    
    
}

