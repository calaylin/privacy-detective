import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.mallet.util.CollectionUtils;


public class Util {
	
	

	
	
	
	public static void writeFile(List<String> allLines,String fileName, boolean append)
	{
		File aFile = new File(fileName);
		FileWriter aFileWriter;
		try {
		 	    if(aFile.exists() == false)
		 	    		aFile.createNewFile();
				
				aFileWriter = new FileWriter(aFile, append); // Open in Append mode
				////aFileWriter.
				
				for(String aLine:allLines)
				{
					aFileWriter.write(aLine);
					//aFileWriter.w
				//	aFileWriter.write("\n");
					//System.out.println("write in sensor");
					
				}
				aFileWriter.close();
			 	   
			//	System.out.println("Writing done");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 	   
	}
	
	public static void writeFile(String allLines,String fileName, boolean append)
	{
		File aFile = new File(fileName);
		FileWriter aFileWriter;
		try {
		 	    if(aFile.exists() == false)
		 	    		aFile.createNewFile();
				
				aFileWriter = new FileWriter(aFile, append); // Open in Append mode
				
				//for(String aLine:allLines)
				{
					aFileWriter.write(allLines);
				//	aFileWriter.write("\n");
					//System.out.println("write in sensor");
					
				}
				aFileWriter.close();
			 	   
				//System.out.println("Writing done");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 	   
	}

	public static List<String> readFile(File file, boolean readAll)
    {
    	List<String> allWords = new ArrayList<String>();
    		
        FileReader aFileReader;
		
		BufferedReader reader = null;
	    
		//File file = new File(txtFile);
 	    if(file.exists() == true)
			try {
				
				
				aFileReader = new FileReader(file); 
				reader = new BufferedReader(aFileReader);
				
				String dataLine = reader.readLine();
				
				while(dataLine!=null)
				{
					if(readAll)
						allWords.add(dataLine);
					else{
						if(!allWords.contains(dataLine))
						{
							
							allWords.add(dataLine);
						}
					}
					
					dataLine = reader.readLine();
					//System.out.println(allFunctionWords.size());
				}
				
				
				aFileReader.close();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
			
			return allWords;
    	
    }

	public static void writeUTF8( String allLines, String filename,boolean append){
		 
		File file = new File(filename);
		
		try {
			if(!file.exists()){
				///System.out.println(file.getAbsolutePath());
				file.createNewFile();
			}
			
			 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF8"));
			 
			 if(append)
				 out.append(allLines);
			 else
				 out.write(allLines);
		     out.close();
		 //    System.out.println("writing done");
		 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 
		 
	}
	
	public static List<String> readFileUTF8(File file, boolean readAll)
    {
    	List<String> allWords = new ArrayList<String>();
    		
		BufferedReader reader = null;
	    
		//File file = new File(txtFile);
 	    if(file.exists() == true)
			try {
				
				
				//aFileReader = new FileReader(file); 
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF8"));
				
				String dataLine = reader.readLine();
				
				while(dataLine!=null)
				{
					if(readAll)
						allWords.add(dataLine);
					else{
						if(!allWords.contains(dataLine))
						{
							
							allWords.add(dataLine);
						}
					}
					
					dataLine = reader.readLine();
					//System.out.println(allFunctionWords.size());
				}
				
				reader.close();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
			
			return allWords;
    	
    }

	public static String read(File file, boolean readAll)
    {
		BufferedReader reader = null;
	    String output = "";
		//File file = new File(txtFile);
 	    if(file.exists() == true)
			try {
				
				
				//aFileReader = new FileReader(file); 
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF8"));
				
				String dataLine = reader.readLine();
				
				while(dataLine!=null)
					output+= dataLine+"\n";
					
				dataLine = reader.readLine();
				
				reader.close();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
			
			return output;
    	
    }

		  
}
