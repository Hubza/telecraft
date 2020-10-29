package uk.co.hubza.telecraft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class telecraft extends JavaPlugin implements Listener {
	public int lasthour = -1;
	public int currenthour;
	public BossBar bar;
	public ArrayList<String> teleplayers;
	public String verbose = "false";
	
	public void log(String logtext) {
		//Bukkit.broadcastMessage("[Telecraft] : " + logtext);
		if(verbose == "true") {
			getLogger().info(logtext);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
		log("user joined");
		Player player = e.getPlayer();
		if (!teleplayers.contains(player.getDisplayName())) {
		    log(player.getDisplayName() + " has joined the server. A teleport has happened whilst the player was offline. The player will now be teleported.");
		    teleplayer(player);
		}
    }
	
    public void CheckConfig() {
        
        if(getConfig().get("verbose") == null){ //if the setting has been deleted it will be null
            getConfig().set("verbose", "false"); //reset the setting
            saveConfig();
            reloadConfig();
 
        }
 
    }
	
	@Override
    public void onEnable() {
        File file = new File(getDataFolder() + File.separator + "config.yml"); //This will get the config file
        
        
        if (!file.exists()){ //This will check if the file exist
        	//Situation A, File doesn't exist

        	getConfig().addDefault("verbose", "false"); //adding default settings

        //Save the default settings
        	getConfig().options().copyDefaults(true);
        	saveConfig();
        } else {
        //situation B, Config does exist
        	CheckConfig(); //function to check the important settings
        	saveConfig(); //saves the config
        	reloadConfig();    //reloads the config

        }   
        
        //get String
        verbose = getConfig().getString("verbose");

		teleplayers = new ArrayList<String>();
		
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
		getLogger().info("onEnable has been invoked! The Telecraft plugin is now active!");
		
		try {
			File myObj = new File("teleplayers.txt");
			log("in the try loop");
			if (myObj.createNewFile()) {
			 	System.out.println("File created: " + myObj.getName());
			 	getLogger().info("teleplayers.txt file did not exist. Created for future use.");
			 } else {
				 getLogger().info("teleplayers.txt already exists. That means that there was most likely a previous shutdown. Reading...");
				 Scanner myReader = new Scanner(myObj);
			     while (myReader.hasNextLine()) {
			        teleplayers.add(myReader.nextLine());
			     }
			     getLogger().info("Teleplayers: " + teleplayers);
			     myReader.close();
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			getLogger().info("ERROR could not write to teleplayers");
		}
        
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
                    	teleplayers.clear();
                    	teleplayers = new ArrayList<String>();
                    	try {
                    		PrintWriter writer = new PrintWriter("teleplayers.txt");
                    		writer.close();
                    	} catch (IOException e) {
                			// TODO Auto-generated catch block
                			e.printStackTrace();
                		}
                    	FileWriter fw;
                    	try {
                			fw = new FileWriter("lasthour.txt");
                			BufferedWriter bw = new BufferedWriter(fw);
                			bw.write(Integer.toString(currenthour));
                			bw.close();
                		} catch (IOException e) {
                			// TODO Auto-generated catch block
                			e.printStackTrace();
                		}
                        lasthour = currenthour;
                        inittele();
                    }
                }
            }
        }.runTaskTimer(this, 0, 20);
    }
    
	public void inittele() {
		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
			teleplayer(player);
    	}
	}
	
	public void teleplayer(Player player) {
		teleplayers.add(player.getDisplayName());
		FileWriter fw;
		try {
			fw = new FileWriter("teleplayers.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(player.getDisplayName());
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Location spawn = Bukkit.getServer().getWorld("world").getSpawnLocation();
		int x = 0;
		int z = 0;
		int maxX= x + 2500;
		int minX= x - 2500;
		int maxZ= z + 2500;
		int minZ= z - 2500;
		Random rand = new Random();
		x = rand.nextInt(maxX - minX) + minX;
		z = rand.nextInt(maxZ - minZ) + minZ;
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