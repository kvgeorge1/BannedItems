package BannedItems;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import PluginReference.ChatColor;
import PluginReference.MC_Block;
import PluginReference.MC_Command;
import PluginReference.MC_DirectionNESWUD;
import PluginReference.MC_EventInfo;
import PluginReference.MC_ItemStack;
import PluginReference.MC_Location;
import PluginReference.MC_Player;
import PluginReference.MC_Server;
import PluginReference.PluginBase;
import PluginReference.PluginInfo;

public class MyPlugin extends PluginBase {
	private static final String CONFIG_DIR = "plugins_mod" + File.separatorChar + "BannedItems";
	private static ArrayList<Integer> bannedItems = new ArrayList<Integer>();
	private static File config = new File(CONFIG_DIR + File.separatorChar + "config.ini");

	public static MC_Server server;

	@Override
	public void onStartup(final MC_Server server) {
		MyPlugin.server = server;

		// Create directory if it doesn't exist
		//
		File dir = new File(CONFIG_DIR);
		if (!dir.exists())
			dir.mkdirs();

		// Create config.ini if it doesn't exist
		//
		if (!config.exists()) {
			ConfigHelper.writeConfig(config, bannedItems);
		}

		ConfigHelper.readConfig(config, bannedItems);
		for (Integer i : bannedItems) {
			System.out.println("Item: " + i);
		}

		server.registerCommand(new MC_Command() {

			@Override
			public List<String> getAliases() {
				return Arrays.asList("bi");
			}

			@Override
			public String getCommandName() {
				return "banneditem";
			}

			@Override
			public String getHelpLine(MC_Player plr) {
				return "/bannedItems - type '/bannedItems help' for info";
			}

			@Override
			public List<String> getTabCompletionList(MC_Player plr, String[] args) {
				return new ArrayList<>();
			}

			@Override
			public void handleCommand(MC_Player plr, String[] args) {
				if (args == null || args.length == 0) {
					if (plr == null) {
						server.log("[BannedItems] - Unrecognized Parameter!  User /banneditem help for valid parameters");
					} else {
						plr.sendMessage(ChatColor.GOLD
								+ "[BannedItems] - Unrecognized Parameter!  User /banneditem help for valid parameters");
					}
				} else {
					if (args[0].equals("help")) {
						if (plr == null) {
							server.log("/banneditem reload - re-reads banned item list from config.in");
							server.log("/banneditem add <id> - adds a new item/block to the list");
							server.log("/banneditem remove <id> - re-reads banned item list from config.in");
							server.log("/banneditem list - lists all items currently in the list");
						} else {
							plr.sendMessage(ChatColor.AQUA + "/banneditem reload" + ChatColor.WHITE
									+ " - re-reads banned item list from config.in");
							plr.sendMessage(ChatColor.AQUA + "/banneditem add <id>" + ChatColor.WHITE
									+ " - adds a new item/block to the list");
							plr.sendMessage(ChatColor.AQUA + "/banneditem remove <id>" + ChatColor.WHITE
									+ " - re-reads banned item list from config.in");
							plr.sendMessage(ChatColor.AQUA + "/banneditem list" + ChatColor.WHITE
									+ " - lists all items currently in the list");
						}
					} else if (args[0].equals("reload")) {
						ConfigHelper.readConfig(new File(CONFIG_DIR + File.separatorChar + "config.ini"), bannedItems);
						server.log("[BannedItems] - Configuration Reloaded");
						if (plr != null) {
							plr.sendMessage(ChatColor.GOLD + "[BannedItems] - Configuration Reloaded!");
							plr.sendMessage(ChatColor.GOLD + "/banneditems add <item_id/block_id>");
						}
					} else if (args[0].equals("add")) {
						// Check to see if there are enough arguments
						//
						if (args.length < 2 || args[1] == null || args[1].trim().equals("")) {
							if (plr != null) {
								plr.sendMessage(ChatColor.RED + "[BannedItems] - Invalid number of arguments!");
								plr.sendMessage(ChatColor.GOLD + "/banneditems add <item_id/block_id>");
							} else {
								server.log("[BannedItems] - Invalid Argument!");
								server.log("/banneditems add <item_id/block_id>");
							}
						} else {
							// Check for proper list of arguments and proper
							// format
							//
							Integer id = -1;
							try {
								id = new Integer(args[1].trim());
							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
								if (plr != null) {
									plr.sendMessage(ChatColor.RED + "[BannedItems] - Invalid number of arguments!");
									plr.sendMessage(ChatColor.GOLD + "/banneditems add <item_id/block_id>");
								} else {
									server.log("[BannedItems] - Invalid Argument!");
									server.log("/banneditems add <item_id/block_id>");
								}
							}

							// Check to see if item is already in the list
							//
							if (bannedItems.contains(id)) {
								if (plr != null) {
									plr.sendMessage(ChatColor.RED + "[BannedItems] - Item/Block already in list!");
								} else {
									server.log("[BannedItems] - Item/Block already in list!");
								}
							} else {
								if (id != -1) {
									bannedItems.add(id);
									ConfigHelper.writeConfig(config, bannedItems);
									if (plr != null) {
										plr.sendMessage(ChatColor.RED + "[BannedItems] - Item/Block added to list!");
									} else {
										server.log("[BannedItems] - Item/Block added to list!");
									}
								}
							}
						}
					} else if (args[0].equals("remove")) {
						// Check to see if there are enough arguments
						//
						if (args.length < 2 || args[1] == null || args[1].trim().equals("")) {
							if (plr != null) {
								plr.sendMessage(ChatColor.RED + "[BannedItems] - Invalid number of arguments!");
								plr.sendMessage(ChatColor.GOLD + "/banneditems remove <item_id/block_id>");
							} else {
								server.log("[BannedItems] - Invalid number of arguments!");
								server.log("/banneditems add <item_id/block_id>");
							}
						} else {
							// Check for proper list of arguments and proper
							// format
							//
							Integer id = -1;
							try {
								id = new Integer(args[1].trim());
							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
								if (plr != null) {
									plr.sendMessage(ChatColor.RED + "[BannedItems] - Invalid Argument!");
									plr.sendMessage(ChatColor.GOLD + "/banneditems remove <item_id/block_id>");
								} else {
									server.log("[BannedItems] - Invalid Argument!");
									server.log("/banneditems remove <item_id/block_id>");
								}
							}

							// Check to see if item is in the list
							//
							if (id != -1 && bannedItems.contains(id)) {
								bannedItems.remove(id);
								ConfigHelper.writeConfig(config, bannedItems);
								if (plr != null) {
									plr.sendMessage(ChatColor.GOLD + "[BannedItems] - Item/Block removed from list!");
								} else {
									server.log("[BannedItems] - Item/Block removed from list!");
								}
							} else {
								if (plr != null) {
									plr.sendMessage(ChatColor.RED + "[BannedItems] - Item/Block not found in list!");
								} else {
									server.log("[BannedItems] - Item/Block not found in list!");
								}
							}
						}
					} else if (args[0].equals("list")) {
						if (plr != null) {
							plr.sendMessage(ChatColor.GOLD + "---------------------------");
							plr.sendMessage(ChatColor.GOLD + "- " + ChatColor.AQUA + "Banned Items/Block List" + ChatColor.GOLD
									+ " -");
							plr.sendMessage(ChatColor.GOLD + "---------------------------");
							if (bannedItems.size() == 0) {
								plr.sendMessage(ChatColor.WHITE + "No items in list!");
							} else {
								for (Integer id : bannedItems) {
									plr.sendMessage(ChatColor.WHITE + String.valueOf(id));
								}
							}
						} else {
							server.log("---------------------------");
							server.log("- Banned Items/Block List -");
							server.log("---------------------------");
							if (bannedItems.size() == 0) {
								server.log("No items in list!");
							} else {
								for (Integer id : bannedItems) {
									server.log(String.valueOf(id));
								}
							}
						}
					} else {
						if (plr != null) {
							plr.sendMessage(ChatColor.RED + "[BannedItems] Unknown Command.");
						} else {
							server.log("[BannedItems] Unknown Command.");
						}
					}
				}
			}

			@Override
			public boolean hasPermissionToUse(MC_Player plr) {
				if (plr == null || plr.isOp()) {
					return true;
				}

				return false;
			}
		});

		System.out.println("[BannedItems] activated!");
	}

	public PluginInfo getPluginInfo() {
		PluginInfo info = new PluginInfo();
		info.name = "BannedItems";
		info.description = "A plugin to block specific items from user creation/placement/use";
		info.version = "v1.1";
		info.eventSortOrder = 1000f;
		return info;
	}

	@Override
	public void onItemCrafted(MC_Player plr, MC_ItemStack isCraftedItem) {
		if (bannedItems.contains(new Integer(isCraftedItem.getId()))) {
			plr.sendMessage(ChatColor.RED
					+ "NOTE: THIS ITEM HAS BEEN BANNED ON THE SERVER!!! Any attempt to place it will destroy it.");
			plr.getInventory().remove(isCraftedItem);
			plr.setItemInHand(null);
			plr.updateInventory();
		} else {
			super.onItemCrafted(plr, isCraftedItem);
		}
	}

	@Override
	public void onAttemptBlockPlace(MC_Player plr, MC_Location loc, MC_Block blk, MC_ItemStack isHandItem,
			MC_Location locPlacedAgainst, MC_DirectionNESWUD dir, MC_EventInfo ei) {
		if (bannedItems.contains(new Integer(isHandItem.getId()))) {
			plr.sendMessage(ChatColor.RED
					+ "BANNED ITEM!!! - This item has been banned on the server and has been removed from your inventory!");
			ei.isCancelled = true;
			plr.setItemInHand(null);
			plr.updateInventory();
		} else {
			super.onAttemptBlockPlace(plr, loc, blk, isHandItem, locPlacedAgainst, dir, ei);
		}
	}

	@Override
	public void onAttemptItemUse(MC_Player plr, MC_ItemStack is, MC_EventInfo ei) {
		if (bannedItems.contains(new Integer(is.getId()))) {
			plr.sendMessage(ChatColor.RED
					+ "BANNED ITEM!!! - This item has been banned on the server and has been removed from your inventory!");
			ei.isCancelled = true;
			plr.setItemInHand(null);
			plr.updateInventory();
		} else {
			super.onAttemptItemUse(plr, is, ei);
		}
	}

	public void onShutdown() {
		System.out.println("[BannedItems] de-activated!");
	}
}