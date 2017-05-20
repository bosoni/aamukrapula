/**
 * @file FileIO.java
 * @author mjt, 2006-07
 * mixut@hotmail.com
 *
 * @created 31.10.2006
 * @edited 15.6.2007
 *
 * tiedostonkäsittely luokka.
 *
 */

package tstgame;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class FileIO
{
    File file;
    
    /**
     * avaa tiedosto
     *
     * @param fileName
     * @return
     */
    public boolean openFile(String fileName)
    {
	file = new File(fileName);
	return true;
    }
    
    /**
     * luo uusi tiedosto. jos jo olemassa, kysyy että kirjoitetaanko päälle
     *
     * @param fileName
     * @return
     */
    public boolean createFile(String fileName)
    {
	file = new File(fileName);
	
	// jos tiedosto olemassa
	if (file.exists())
	{
	    int response = JOptionPane.showConfirmDialog(null, "Korvataanko tiedosto?", "Samanniminen tiedosto löytyi",
		 JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	    if (response == JOptionPane.CANCEL_OPTION)
		return false;
	    
	    // poista vanha
	    file.delete();
	}
	
	return true;
    }
    
    /**
     * lue koko tiedosto Stringiin joka palautetaan
     *
     * @return true jos onnistui, muuten false
     */
    public String readFile()
    {
	StringBuffer fileBuffer;
	String fileString = null;
	String line;
	
	try
	{
	    FileReader in = new FileReader(file);
	    BufferedReader dis = new BufferedReader(in);
	    fileBuffer = new StringBuffer();
	    
	    while ((line = dis.readLine()) != null)
	    {
		fileBuffer.append(line + "\n");
	    }
	    
	    in.close();
	    fileString = fileBuffer.toString();
	    
	}
	catch (IOException e)
	{
	    Game.ErrorMessage("readFile(): " + e);
	    return null;
	}
	return fileString;
    }
    
    /**
     * kirjoita dataString tiedostoon
     *
     * @param dataString
     *            kirjoitettava teksti
     * @return true,jos onnistui. muuten false
     */
    public boolean writeFile(String dataString)
    {
	try
	{
	    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	    out.print(dataString);
	    out.flush();
	    out.close();
	}
	catch (IOException e)
	{
	    return false;
	}
	return true;
    }
    
    
    
    public String openAndReadFile(String file)
    {
	String fileString = "";
	String line;
	
	try
	{
	    
	    InputStream in = this.getClass().getResourceAsStream(file);
	    int c;
	    while((c = in.read()) != -1)
	    {
		if(c=='\r') continue;
		fileString+=(char)c;
	    }
	    
	}
	catch (IOException e)
	{
	    Game.ErrorMessage("openAndReadFile("+file+"): " + e);
	    return null;
	}
	return fileString;
    }
    
    
    
}
