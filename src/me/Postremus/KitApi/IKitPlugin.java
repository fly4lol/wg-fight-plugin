package me.Postremus.KitApi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IKitPlugin 
{
	boolean existsKit(String kitName);
	void giveKit(String kitName, Player p);
	ItemStack[] getKitItems(String kitName);
}
