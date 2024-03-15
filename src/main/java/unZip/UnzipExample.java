package unZip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipExample {

    public static void main(String[] args) {
    	
    	String section = "section06.";
    	String commonName = "Source-code-Control-Flow-";
        String destDirectory = "C:/Users/figur/Downloads/udemy/output/";
        String originDirectory = "C:/Users/figur/Downloads/udemy/";

        File originDirectoryFile = new File(originDirectory);
        File[] filesZip = originDirectoryFile.listFiles();
        Arrays.sort(filesZip, Comparator.comparingLong(File::lastModified));
        
        int count = 1;
        for (File file : filesZip) {
			String name = file.getName();
			String zipFilePath = originDirectory + name;
			extractZip(commonName, zipFilePath, destDirectory,count);
			count++;
		}        
        
        
        File desDirectoryFile = new File(destDirectory);
        File[] desFilesZip = desDirectoryFile.listFiles();
        for (File file : desFilesZip) {
        	String[] list = file.list();
        	replaceStringInFiles(file, list, file.getName(), section);
		}
        
    }


	private static void extractZip(String commonName, String zipFilePath, String destDirectory, int count) {
		String outputDirectoryName;
		String[] splitFilePath = zipFilePath.split("/");
        outputDirectoryName =   "$"+String.format("%02d", count)+splitFilePath[splitFilePath.length-1];
		outputDirectoryName = outputDirectoryName.replace(commonName, "").replace(".zip", "")
				.replace("-", "_").replace("(", "").replace(")", "")
				.replace("'", "").replace("&", "And")
				.replace(".", "_").replace(",", "_");
        unzip(zipFilePath, destDirectory, outputDirectoryName);
        
        
        File directory = new File(destDirectory+outputDirectoryName);
        String[] list = directory.list();
       
	}


    private static void unzip(String zipFilePath, String destDirectory, String outputDirectoryName) {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zis.getNextEntry();
                        
            while (entry != null) {
            	
            	
                String fileName = entry.getName();
                
                if(!fileName.contains("src")) {
                	entry = zis.getNextEntry();
                	continue;
                }
                
                fileName = fileName.substring(fileName.indexOf("src/")).replace("src/", "");
                System.out.println(fileName);
                String namePackage = fileName.replace("/", ".");
                
                File newFile = new File(destDirectory + File.separator + outputDirectoryName + File.separator + fileName);
                // Create all non-existing parent directories.
                newFile.getParentFile().mkdirs();
                if (!entry.isDirectory()) {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                } else {
                    // If it's a directory, create it.
                    newFile.mkdirs();
                }
                entry = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    private static void replaceStringInFiles(File directory, String[] list, String outputDirectoryName, String section) {

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Invalid directory path.");
            return;
        }

        File[] files = directory.listFiles();
        

        if (files == null) {
            System.err.println("Failed to list files in the directory.");
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                replaceStringInFiles(file, list, outputDirectoryName, section); // Recursively search in subdirectories
            } else if (file.getName().endsWith(".java")) {
                replaceStringInFile(file, list, outputDirectoryName, section);
            }
        }
    }

    private static void replaceStringInFile(File fileToModify, String[] listFile, String outputDirectoryName, String section) {
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fileToModify))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                // Replace occurrences of oldString with newString in each line
            	if(count == 0 && !line.contains("package")) {
            		String namePackage = "package "+section+outputDirectoryName+";";
            		contentBuilder.append(namePackage).append(System.lineSeparator());
            	}
            	count ++;
            	
            	List<String> filesList = Arrays.asList(listFile); 
            	for (String file : filesList) {
            		if(line.contains(file+".")) {
						line = line.replace(file+".", section+outputDirectoryName+"."+file+".");
						break;
					}
				}
                contentBuilder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (FileWriter writer = new FileWriter(fileToModify)) {
            writer.write(contentBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}