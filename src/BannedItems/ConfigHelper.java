package BannedItems;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigHelper {
	public static void readConfig(File config, ArrayList<Integer> bannedItems) {
		if (bannedItems.size() > 0) {
			bannedItems.clear();
		}

		BufferedReader buff = null;
		try {
			buff = new BufferedReader(new FileReader(config));
			String line = buff.readLine();
			while ((line = buff.readLine()) != null) {
				try {
					Integer i = new Integer(line);
					bannedItems.add(i);
				} catch (NumberFormatException nfe) {
					// Do nothing, next line...
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (buff != null) {
				try {
					buff.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void writeConfig(File config, ArrayList<Integer> bannedItems) {
		BufferedWriter buff = null;
		try {
			// If the file exists, delete it
			//
			if (config.exists()) {
				config.delete();
			}

			// Create new file
			//
			config.createNewFile();
			buff = new BufferedWriter(new FileWriter(config));
			buff.write("-------------------\n");
			buff.write("-Banned Items List-\n");
			buff.write("-------------------\n");
			buff.write("\n");

			// Add items/blocks
			//
			if (bannedItems != null && bannedItems.size() > 0) {
				for (Integer id : bannedItems) {
					buff.write(String.valueOf(id) + "\n");
				}
			}
			buff.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buff != null) {
				try {
					buff.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}