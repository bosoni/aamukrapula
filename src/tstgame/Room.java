/**
 * @file Room.java
 * @author mjt, 2006-07
 * mixut@hotmail.com
 *
 * @created 31.10.2006
 * @edited 15.6.2007
 *
 */
package tstgame;

import java.net.URL;
import java.util.HashMap;
import murlen.util.fscript.*;
import java.util.ArrayList;

import java.io.*;
import javax.swing.JOptionPane;
import java.awt.Graphics;
import java.util.Vector;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

/**
 * Room luokka tänne asetukset eli taustakuva, syvyysbufferi, huoneessa olevat
 * objektit ym..
 *
 */
class Room
{
    // scriptiin liittyvät muuttujat
    public static FScript fscript=null;
    boolean entered=true; // juuri tullut huoneeseen
    ArrayList params=new ArrayList();
    
    public Room()
    {
	fscript=null;
	entered=true;
    }
    
    /**
     * jos str=="", huoneeseen tulo ja scriptin tsekkaus
     * muuten suorita str-niminen funktio (samassa parametrit jos on)
     *
     */
    void runScript(String str)
    {
	if(fscript==null) return; // jos ei scriptiä ole, poistu
	
	try
	{
	    if(str.equals(""))
	    {
		if(entered==true) // jos juuri tullut huoneeseen
		{
		    entered=false;
		    fscript.callScriptFunction("entered", params);
		}
		
		return;
	    }
	    
	    // aseta parametrit
	    String[] parmsStr= str.split(" ");
	    for(int q=1; q<parmsStr.length; q++)
	    {
		params.add(parmsStr[q]);
	    }
	    
	    // suoritetaan parms[0] niminen funktio
	    fscript.callScriptFunction(parmsStr[0], params);
	    
	}
	catch(FSException e)
	{
	}
	catch(IOException e)
	{}
	
    }
    
    /**
     * lataa huoneen scriptitiedosto, jos sellainen löytyy.
     * siihen voi asettaa toimintoja joita speitorilla ei saa tehtyä.
     *
     */
    public void loadScript(String fileName)
    {
	ScriptFuncs.reset(); // luodaan ja tyhjennetään action taulukko (vain kerran)
	
	fileName=Main.ROOMDIR+fileName;
	
	// tsekkaa löytyykö tiedosto file
	URL url = Main.main.getClass().getResource(fileName);
	if(url==null) 
	{
	    File file = new File(fileName);
	    if(!file.exists()) return;
	}
	
	
	
	try
	{
	    fscript=new FScript();
	    fscript.registerExtension(new ScriptFuncs());
	    
	    FileReader f=null;
	    
	    if(url==null) 
	    {
		f=new FileReader(fileName);
		fscript.load(f);
	    }
	    else
	    {
		InputStream in = Main.main.getClass().getResourceAsStream(fileName);
		
		InputStreamReader inR = new InputStreamReader (  in  ) ; 
		
		fscript.load(inR);
	    }

	    fscript.run();
	}
	catch(IOException e)
	{}
	catch(FSException e)
	{}
    }
    
    /**
     * lataa huoneen kaikki tiedot, kuvat, polyt, tekstit
     */
    public void load(String fileName)
    {
	fileName = Main.ROOMDIR + fileName;
	
	// jos ollaan ladattu jo jotain, poista tiedot
	if (!backGroundImage.equals(""))
	{
	    objs.clear();
	    polys.clear();
	    zBufImage = "";
	    backGroundImage = "";
	    name = "";
	}
	
	FileIO file=new FileIO();
	
	/** tähän huoneen tiedot */
	String dataString = "";
	dataString = file.openAndReadFile(fileName); // koko tiedosto dataStringiin

	if (dataString == null)
	{
	    JOptionPane.showMessageDialog(null, "Lataaminen epäonnistui", "Virhe", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	String[] strs = dataString.split("\n"); // palasiksi
	
	// dataString+=name+"\n"+backGroundImage+"\n"+zBufImage+"\n"+polys.size();
	int pos = 0;
	name = strs[pos++];
	backGroundImage = strs[pos++];
	zBufImage = strs[pos++];
	
	int polys = Integer.parseInt(strs[pos++]);
	
	// aseta polygonin tiedot ja lisää huoneeseen
	for (int q = 0; q < polys; q++)
	{
	    Polygon tmppoly = new Polygon();
	    tmppoly.descStr = strs[pos++];
	    tmppoly.actionStr = strs[pos++];
	    tmppoly.removePoly = (strs[pos++].equals("false") ? false : true);
	    tmppoly.block = (strs[pos++].equals("false") ? false : true);
	    tmppoly.itemNum = Integer.parseInt(strs[pos++]);
	    tmppoly.removeFromScreen = (strs[pos++].equals("false") ? false : true);
	    tmppoly.toInventory = (strs[pos++].equals("false") ? false : true);
	    tmppoly.needsItem = Integer.parseInt(strs[pos++]);
	    int verts = Integer.parseInt(strs[pos++]);
	    
	    tmppoly.newRoom = strs[pos++];
	    tmppoly.removeFromInventory = (strs[pos++].equals("false") ? false : true);
	    tmppoly.successUseStr = strs[pos++];
	    
	    // aseta polyn verteksit
	    for (int w = 0; w < verts; w++)
	    {
		Vector2i v = new Vector2i();
		v.x = Integer.parseInt(strs[pos++]);
		v.y = Integer.parseInt(strs[pos++]);
		
		// lisää verteksit
		tmppoly.verts.add(v);
	    }
	    
	    // ja aseteltu poly huoneeseen
	    Game.curRoom.polys.add(tmppoly);
	}
	
	// aseta objektin tiedot ja lisää huoneeseen
	int size = Integer.parseInt(strs[pos++]);
	for (int q = 0; q < size; q++)
	{
	    Item2D item = new Item2D();
	    item.name = strs[pos++];
	    item.fileName = strs[pos++];
	    item.x = Integer.parseInt(strs[pos++]);
	    item.y = Integer.parseInt(strs[pos++]);
	    item.visible = (strs[pos++].equals("false") ? false : true);
	    
	    objs.add(item);
	}
	
    }
    /** huoneen nimi */
    String name = "";
    /** taustakuva */
    String backGroundImage = "";
    /** syvyyskartta */
    String zBufImage = "";
    
    /** esteet ja esineiden alueet */
    Vector<Polygon> polys = new Vector<Polygon>();
    /** esineiden tiedot */
    Vector<Item2D> objs = new Vector<Item2D>();
    
    BufferedImage bgImage = null;
    BufferedImage zImage = null;
    
    public void setName(String name)
    {
	this.name = name;
    }
    public void loadBackground(String name)
    {
	bgImage = loadImage(name);
	backGroundImage = name;
    }
    public void loadZBuf(String name)
    {
	zImage = loadImage(name);
	zBufImage = name;
    }
    
    /**
     * lataa kuva
     *
     * @param file
     *            tiedostonimi
     */
    static BufferedImage loadImage(String file)
    {
	if (file.equals("")) return null;
     
	file=Main.PICSDIR+file;
     
	BufferedImage bufimage = null;
	BufferedImage image = null;
     
	try
	{
	    URL url = Main.main.getClass().getResource(file);
	    
	    if (url != null)
	    {
		bufimage = ImageIO.read(url);
	    }
	    else
	    {
		bufimage = ImageIO.read(new File(file));
	    }
     
	    // luo kuva
	    image = new BufferedImage(bufimage.getWidth(), bufimage.getHeight(), BufferedImage.TYPE_INT_ARGB);
     
	    // piirrä sinne bufimage
	    Graphics g = image.createGraphics();
	    g.drawImage(bufimage, 0, 0, null);
     
	}
	catch (IOException err)
	{
	    Game.ErrorMessage("loadImage("+file+"): " + err);
	}
     
	return image;
    }
    
}

/**
 * tän luokan metodeita voidaan kutsua scriptitiedostosta.
 */
class ScriptFuncs  extends BasicExtension
{
    static final int MAX=1000;
    static HashMap action=null;
    
    static public void reset()
    {
	if(action==null)
	{
	    action=new HashMap();
	}
    }
    
    public Object callFunction(String name, ArrayList params) throws FSException
    {
	if(name.equals("MessageBox"))
	{
	    String str=(String)params.get(0);
	    Game.MessageBox(str);
	}
	else
	    if(name.equals("LoadAndDraw")) // LoadAndDraw(fileName, x, y)
	    {
	    // TODO latais scriptistä käsin kuvan ja näyttäis sen ruudulla
	    String str=(String)params.get(0);
	    BufferedImage image = Room.loadImage(str);
	    int x=((Integer)params.get(1)).intValue();
	    int y=((Integer)params.get(2)).intValue();
	    
	    Main.drawImage(image, x, y);
	    Main.main.repaint();
	    }
	    else
		if(name.equals("RemoveImage"))
		{
	    Main.drawImage(null, 0, 0);
		}
		else
		    if(name.equals("Delay"))
		    {
	    try
	    {
		Thread.sleep(((Integer)params.get(0)).intValue());
	    }
	    catch (InterruptedException e)
	    {
	    }
	    
		    }
		    else
			if(name.equals("EndGame"))
			{
	    Main.running=false;
			}
			else
			{
	    throw new FSUnsupportedException(name);
			}
	return null;
    }
    
    int getIndex(Object index)
    {
	Integer i=(Integer)index;
	return i.intValue();
    }
    
    // taulukot ---
    public Object getVar(String name,Object index)
    {
	if(name.equals("action"))
	{
	    if(action.get(index)==null) return new Integer(0);
	    return action.get(index);
	}
	
	return new Integer(0);
    }
    public void setVar(String name,Object index,Object value)
    {
	if(name.equals("action"))
	{
	    action.put(index, value);
	}
    }
    
}
