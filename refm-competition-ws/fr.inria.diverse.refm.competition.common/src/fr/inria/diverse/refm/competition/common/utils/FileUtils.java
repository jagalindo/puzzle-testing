package fr.inria.diverse.refm.competition.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

	public static String readFileContent(File file) throws IOException{
		String content = "";
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line = br.readLine();
		while(line != null){
			content += line + "\n";
			line = br.readLine();
		}
		
		br.close();
		return content;
	}
	
	public static void saveFile(String content, String filePath) throws IOException{
		File destination = new File(filePath);
		
		if(!destination.exists())
			destination.createNewFile();
		
		FileWriter fw = new FileWriter(destination);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
	}
}
