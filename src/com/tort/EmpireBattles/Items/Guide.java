package com.tort.EmpireBattles.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Guide {
    public static ItemStack Guide;

    public static void init(){
        createGuide();
    }

    private static void createGuide(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK,1);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();
        bookMeta.setTitle(ChatColor.LIGHT_PURPLE + "Guide");
        String n1 = "\n";
        ArrayList<String> pages = new ArrayList<>();
        bookMeta.setAuthor(ChatColor.WHITE + "Tort");
        pages.add(ChatColor.DARK_RED + "EMPIRE BATTLES"  + n1 + n1 + ChatColor.DARK_GRAY  + "Capture towns and other empires capitals to win the game!" + n1 + n1 + ChatColor.GREEN  + "This guide will help you get started!");
        pages.add(ChatColor.DARK_RED + "COMMANDS"  + n1 + n1 + ChatColor.DARK_GRAY + "/empirechat :" + ChatColor.GREEN + " Enables chatting to only your empire" + n1 + ChatColor.DARK_GRAY + "/horse :" + ChatColor.GREEN + " Spawn your horse"+ n1 + ChatColor.DARK_GRAY + "/stats :" + ChatColor.GREEN + " Show your stats"+ n1 + ChatColor.DARK_GRAY + "/empire join {empire} :" + ChatColor.GREEN + " Join an empire");
        pages.add(ChatColor.DARK_RED + "TOWNS"  + n1 + n1 + ChatColor.DARK_GRAY + "Each town has a specific resource that can be found within the mine." + n1  + n1 + ChatColor.GREEN + "If your empire owns the town you can mine the resources!");
        pages.add(ChatColor.DARK_RED + "CAPTURING TOWNS"  + n1 + n1 + ChatColor.DARK_GRAY + "To capture a town you must first place a cannon at the cannon location outside the town's gate." + n1  + n1 + ChatColor.GREEN + "Make sure to protect the cannon! If the cannon is destroyed it won't fire!");
        pages.add(ChatColor.DARK_RED + "CAPTURING TOWNS"  + n1 + n1 + ChatColor.DARK_GRAY + "Once the gate is destroyed rush in and start capturing at the capture zone." + n1  + n1 + ChatColor.GREEN + "Once the town is captured your empire will be able to teleport there and mine the resources!");
        pages.add(ChatColor.DARK_RED + "DEFENDING TOWNS"  + n1 + n1 + ChatColor.DARK_GRAY + "If a cannon is placed at one of your towns destroy the cannon before it fires." + n1  + n1 + ChatColor.DARK_GRAY + "To destroy the cannon simply hit the cannon until there is no health left.");
        pages.add(ChatColor.DARK_RED + "DEFENDING TOWNS"  + n1 + n1 + ChatColor.DARK_GRAY + "If the gate is already destroyed protect the town's capture zone while the enemy attempts to capture." + n1 + ChatColor.GREEN + "If the gate at a town isn't repaired you must go to the capture zone and clear the capture progress to restore the gate!");
        pages.add(ChatColor.DARK_RED + "MINES"  + n1 + n1 + ChatColor.DARK_GRAY + "Each town has a mine with the town's resources located in it." + n1 + ChatColor.GRAY + "There are 4 types of resources: " + ChatColor.GOLD + "GOLD" + ChatColor.GRAY + ", " +  "IRON"  + ChatColor.GRAY  + ", " + ChatColor.DARK_AQUA + "DIAMOND" + ChatColor.GRAY + ", and "+ ChatColor.DARK_GRAY + "NETHERITE" + n1 + ChatColor.GREEN + "Use resources to upgrade gear and buy items at shops!");
        pages.add(ChatColor.DARK_RED + "SHOPS"  + n1 + n1 + ChatColor.DARK_GRAY + "Shops are located within towns and empires. Items are bought with gold. You can mine gold in gold towns!");
        pages.add(ChatColor.DARK_RED + "EMPIRES"  + n1 + n1 + ChatColor.DARK_GRAY + "The mechanics of attacking and defending empires are the same as towns. If an empire is captured that empire is destroyed for the rest of the game. The game ends when one empire is left standing. ");
        pages.add(ChatColor.DARK_RED + "MAP"  + n1 + n1 + ChatColor.DARK_GRAY + "The game has a live map of the region control of the empires. Check out the map at our wesbite!" + n1  + n1 + ChatColor.GREEN + "www.mcempires.net");
        bookMeta.setPages(pages);
        item.setItemMeta(bookMeta);

        Guide = item;

    }
}
