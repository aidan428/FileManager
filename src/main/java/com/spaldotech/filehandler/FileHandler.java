package com.spaldotech.filehandler;


import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.io.FileUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Mod(modid = FileHandler.MODID, name = FileHandler.NAME, version = FileHandler.VERSION)

public class FileHandler {
	
    public static final String MODID = "spaldotech";
    public static final String NAME = "Spaldotech";
    public static final String VERSION = "1.0";
    
    //static Logger log = LogManager.getLogger(FileHandler.class);
    static Logger log = LogManager.getLogger("Spaldotech");
    final static int BUFFER = 2048;	
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
    
    private static void unZipFile(String srcFilePath, String destFilePath) throws IOException
    {
        //Step 1 : Create destination path from the given destFilePath
         
        Path destination = Paths.get(destFilePath).normalize();
         
        //Step 2 : Create a directory destination if it doesn't exist.
         
        if( ! Files.exists(destination))
        {
            Files.createDirectory(destination);
        }
         
        //Step 3 : Create fis and zis from the given srcFilePath
         
        FileInputStream fis = new FileInputStream(srcFilePath);
        ZipInputStream zis = new ZipInputStream(fis);
         
        ZipEntry zipEntry = zis.getNextEntry();
         
        //For every zipEntry
         
        while (zipEntry != null)
        {
            //Step 4 : Convert zipEntry into path and resolve it against destination.
             
            Path path = destination.resolve(zipEntry.getName()).normalize();
             
            //Step 5 : If path doesn't start with destination, print "Invalid Zip Entry".
             
            if ( ! path.startsWith(destination)) 
            {
                System.out.println("Invalid Zip Entry");
            }
             
            //Step 6 : If zipEntry is a directory, create directory with path.
             
            if (zipEntry.isDirectory()) 
            {   
                Files.createDirectory(path);
            }
            else
            {
                //Step 7 : If zipEntry is not a directory, create bos with path,
                 
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(path));
                 
                byte[] bytes = new byte[1024];
                 
                //Read data byte by byte from zis into bytes and write same bytes into bos
                 
                while (zis.read(bytes) >= 0)
                {
                    bos.write(bytes, 0, bytes.length);
                }
                 
                //Close bos
             
                bos.close();
            }
             
            zis.closeEntry();
             
            zipEntry = zis.getNextEntry();
        }
         
        //Step 8 : Close the resources
         
        zis.close();
        fis.close();
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
    
    public static void extractZip() throws IOException {
    	String source = "cache/fancymenu-1.12.2-1.1.2.zip";
    	String target = "cache/extracted_files/";
    	
    	unZipFile(source, target);
    	

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
    public void init(FMLInitializationEvent event) throws IOException
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
    			FMLCommonHandler.instance().exitJava(BUFFER, false);
        	}else {
        		log.debug("Not progressing");
        	}
    	} else {
    		log.debug("File does not exist - continuing");
    	}
    	
    	
    }
    
    
}


