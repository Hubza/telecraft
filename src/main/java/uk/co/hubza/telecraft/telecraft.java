package uk.co.hubza.telecraft;

import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class telecraft extends JavaPlugin {
	@Override
    public void onEnable() {
		getLogger().info("onEnable has been invoked! The Telecraft plugin is now active!");
        // TODO Insert logic to be performed when the plugin is enabled
    }
    
    @Override
    public void onDisable() {
    	getLogger().info("onDisable has been invoked! The Telecraft plugin is now shutting down.");
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
