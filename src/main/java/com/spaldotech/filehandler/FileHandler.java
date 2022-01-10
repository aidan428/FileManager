package com.spaldotech.filehandler;

import java.io.File;
import java.io.IOException;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.io.FileUtils;


@Mod(modid = FileHandler.MODID, name = FileHandler.NAME, version = FileHandler.VERSION)

public class FileHandler {
	
    public static final String MODID = "spaldotech";
    public static final String NAME = "Spaldotech";
    public static final String VERSION = "1.0";
    
    //static Logger log = LogManager.getLogger(FileHandler.class);
    static Logger log = LogManager.getLogger("Spaldotech");
    		
	static File directory = new File ("config/fancymenu");
	static String filePresenceMessage = "A new modpack update has been detected. To complete installation, the config files must be refreshed and the modpack restarted.";
	static String errorCleaning = "An error occurred while deleting the files in the"  + directory +  "directory. Please consult the log files.";
	static String userContinueMessage = "The modpack may be able to run without refreshing the config files, however, this may lead to errors and modpack file corruption." + "\n" + "Would you like to continue?";
    static int filePresenceStatusInt = 0;
    static int choice = 0;

	
    public static void userConfirmMessage() {
		int userInput1 = JOptionPane.showConfirmDialog(null, userContinueMessage, "Spaldotech File Manager", JOptionPane.YES_NO_OPTION);
		System.out.println(userInput1);
		
		if (userInput1 == 0) {
			log.info("User opted to refresh config files");
			choice = 0;
		} else {
			log.info("User opted to not refresh config files");
			choice = 1;
		}
		
 
    }
    
    public static void cleanDirectory() {
    	try {
    		FileUtils.cleanDirectory(directory);
    		log.info("Directory Cleaned");
    	} catch (Exception e) {
    		log.error("Failed to clean directory");
    		JOptionPane.showMessageDialog(null, errorCleaning, "Spaldotech File Manager", JOptionPane.ERROR_MESSAGE);
    	}
    	
    }
    
    public static void checkFilePresence() {
    	
    	File obj1 = new File("config/fancymenu/InitialInstall.spald");
		if (obj1.isFile()) {
			log.info("FancyMenu initial install detected");
			JOptionPane.showMessageDialog(null, filePresenceMessage);
			filePresenceStatusInt = 1;

		} else {
			log.info("FancyMenu initial install not detected");
			filePresenceStatusInt = 0;
		}
		
    }
    
    public static void extractZip() {
    	String source = "cache/fancymenu-1.12.2-1.1.2.zip";
    	String target = "cache/extracted_files/";
    	
    	try {
    		ZipFile zipFile = new ZipFile(source);
    		log.info("Extracting ZIP. This may take a while.");
    		zipFile.extractAll(target);
    		
    	} catch (ZipException e) {
    		log.error("Failed to extract zip");
    		e.getStackTrace();	
    	}
    }
  
    
    public static void moveConfigFolder() {
    	
    	File srcDir = new File("cache/extracted_files/config/fancymenu");
    	File dstDir = new File("config/fancymenu");
    	
    	try {
    		FileUtils.copyDirectory(srcDir, dstDir);
    	} catch (IOException e) {
    		log.error("Failed to move config folder");
    		e.getStackTrace();
    	}
     	
    }
    
    public static void deleteExtractedFiles() {
    	try {
    		FileUtils.deleteDirectory(new File("cache/extracted_files"));
    	} catch (IOException e) {
    		log.error("Unable to delete extracted_file directory");
    	}
    }
    
    public static void deleteTempFile() {
    	
    	
    	try {
    		FileUtils.forceDelete(new File("config/fancymenu/InitialInstall.spald"));
    	} catch (IOException e) {
    		log.error("Unable to delete InitialInstall.spald");
    		e.getStackTrace();
    	}
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	System.out.println("Spaldotech logger preInit complete");
     
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {    	
    	log.info("Punch it, Chewie.");
    	checkFilePresence();
    	
    	if (filePresenceStatusInt == 1) {
    		userConfirmMessage();
    		
    		if (choice == 0) {
        		cleanDirectory();
    			extractZip();
    			moveConfigFolder();
    			deleteExtractedFiles();
    			deleteTempFile();
    			JOptionPane.showMessageDialog(null, "The config files have been refreshed and the modpack will now close. Please restart it from the Technic Launcher.");
    			log.info("Preparing to stop modpack");
    			System.exit(0);
        	}else {
        		log.debug("Not progressing");
        	}
    	} else {
    		log.debug("File does not exist - continuing");
    	}
    	
    	
    }
    
    
}


