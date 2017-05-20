/**
 * @file Game.java
 * @author mjt, 2006-07
 * mixut@hotmail.com
 *
 * @created 31.10.2006
 * @edited 16.6.2007
 *
 */

package tstgame;

import java.io.*;
import murlen.util.fscript.*;
import java.awt.image.Raster;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;
import javax.imageio.ImageIO;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.io.File;
import javax.swing.*;

/**
 * peliluokka t‰nne ohjelmoidaan seikkailupeli.
 */
class Game
{
    /** hiiren koordinaatit */
    public static int mx = 0, my = 0;
    /** hiiren nappi 0=ei mit‰‰n, 1=vasen .. */
    public static int mbutton = 0;
    
    /**
     * hiirikursorit: [0]=k‰vely, [1]=silm‰, [2]=k‰si
     */
    public static BufferedImage[] cursors = new BufferedImage[4];
    
    /**
     * huone jossa ollaan
     */
    public static Room curRoom = null;
    /**
     * ohjattava tyyppi
     */
    public static Person dork = new Person();
    
    /**
     * mode: 0=k‰vely, 1=katso, 2=ota/k‰yt‰,  tavarat
     */
    public static int mode = 0;
    
    /**
     * jos hiirikursori on huonekuvan ulkopuolella, aseta t‰m‰ true ja ruutuun piirret‰‰n ruksi
     */
    public static boolean outOfArea=false;
    
    /**
     * valittu esine. 0=ei esinett‰
     */
    int selectedItem = 0;
    
    /**
     * tavarat jota tyyppi kantaa mukanaan. vektoriin esineiden nimet.
     */
    Vector<String> inventory = new Vector<String>();
    
    Game()
    {
	init();
    }
    
    /**
     * lataa huone, ukot ja hiirikursorit
     *
     */
    void init()
    {
	load("kuja", "aloitus");
	
	dork.load();
	
	cursors[0] = Room.loadImage("walk_cur.PNG");
	cursors[1] = Room.loadImage("eye_cur.PNG");
	cursors[2] = Room.loadImage("get_cur.PNG");
	cursors[3] = Room.loadImage("ruksi.PNG");
    }
    
    // max matka josta ota/k‰yt‰ toiminto toimii
    final int LEN=20;
    static boolean getUse=false; // jos klikataan k‰dell‰, tai esineell‰, aseta true:ksi
    int tmppoly=0;
    int destX, destY; // polygonin l‰himm‰n vertexin paikka ‰ij‰‰n, siihen k‰vell‰‰n
    int alkX, alkY; // paikka jossa hiirt‰ klikattiin
    
    /**
     * p‰‰looppi
     */
    public void run()
    {
	// toiminnon vaihto (hiiren oikea)
	if (mbutton == 3)
	{
	    mode++;
	    mode %= 3;
	    mbutton = 0;
	}
	
	// tsekkaa rajat
	if(mx<Main.transX || my<Main.transY || mx>Main.transX+curRoom.bgImage.getWidth() || my>Main.transY+curRoom.bgImage.getHeight())
	{
	    outOfArea=true;
	}
	else outOfArea=false;
	
	
	// ahhaa, nyt toimitaan (hiiren vasen nappi) kunhan klikattu huoneen alueella
	if (mbutton == 1)
	{
	    if(!outOfArea)
	    {
		mx-=Main.transX;
		my-=Main.transY;
		
		switch (mode)
		{
		    case 0: // k‰vely
			walkTo(mx, my);
			break;
			
		    case 1: // katso
			look(mx, my);
			break;
			
		    case 2: // ota/k‰yt‰
		    {
			// koordinaatit talteen
			destX=mx;
			destY=my;
			alkX=mx;
			alkY=my;
			
			// laske et‰isyys
			double len=Math.sqrt( (dork.x-destX)*(dork.x-destX) + (dork.y-destY)*(dork.y-destY) );
			
			// jos tarpeeksi l‰hell‰, tsekkaa heti mit‰ tehd‰
			if(len<LEN)
			{
			    getOrUse(destX, destY);
			    break;
			}
			else
			{
			    // jos liian kaukana, k‰vele ensin l‰hemm‰ksi, polyn l‰himp‰‰n vertexiin
			    int poly = getPoly(mx, my, 2);
			    if (poly!=-1)
			    {
				double minlen=9999;
				for(int q=0; q<curRoom.polys.get(poly).verts.size(); q++)
				{
				    double len2=Math.sqrt( (curRoom.polys.get(poly).verts.get(q).x - dork.x+10 )* (curRoom.polys.get(poly).verts.get(q).x - dork.x+10 ) +
					 (curRoom.polys.get(poly).verts.get(q).y - dork.y ) * (curRoom.polys.get(poly).verts.get(q).y - dork.y ) );
				    
				    if(len2<minlen)
				    {
					minlen=len2;
					destX=curRoom.polys.get(poly).verts.get(q).x;
					destY=curRoom.polys.get(poly).verts.get(q).y;
				    }
				}
			    }
			    
			    walkTo(destX, destY);
			    getUse=true;
			    
			}
			break;
		    }
		    
		    default: // joku muu eli tavaran k‰yttˆ
			useItem(mx, my, selectedItem);
			break;
			
		}
		mbutton = 0;
		mx+=Main.transX;
		my+=Main.transY;
		
	    }
	    
	}
	
	dork.update(); // p‰ivit‰ juntin paikka
	
	// ota tavara, k‰yt‰ jotain?
	if(getUse==true)
	{
	    // laske et‰isyys
	    double len=Math.sqrt( (dork.x-destX)*(dork.x-destX) + (dork.y-destY)*(dork.y-destY) );
	    
	    // jos tarpeeksi l‰hell‰, tsekkaa mit‰ tehd‰
	    if(len<LEN)
	    {
		getOrUse(alkX, alkY);
		getUse=false;
	    }
	    
	}
    }
    
    static void MessageBox(String msg)
    {
	if (msg.equals("")) return;
	JOptionPane.showMessageDialog(null, msg, "Dum de dum", JOptionPane.INFORMATION_MESSAGE);
    }
    static void ErrorMessage(String msg)
    {
	if (msg.equals("")) return;
	System.out.println(msg);
	JOptionPane.showMessageDialog(null, msg, "Virhe", JOptionPane.ERROR_MESSAGE);
    }
    
    void walkTo(int x, int y)
    {
	dork.walkTo(x, y);
    }
    void look(int x, int y)
    {
	// etsi poly
	int poly = getPoly(x, y, 1);
	if (poly == -1)
	{
	    MessageBox("Ei siin‰ olo mit‰‰n ihmeellist‰.");
	    return;
	}
	
	String desc = curRoom.polys.get(poly).descStr;
	// jos eka kirjain on $ merkki, pit‰‰ kutsua funktiota scriptist‰
	if(desc.charAt(0)=='$') curRoom.runScript(desc.substring(1));
	else
	    MessageBox(desc);
    }
    
    void getOrUse(int x, int y)
    {
	// etsi poly
	int poly = getPoly(x, y, 2);
	
	if (poly == -1)
	{
	    //MessageBox("Ei auttanut.");
	    return;
	}
	
	// jos vaatii tavaran
	if (curRoom.polys.get(poly).needsItem > 0)
	{
	    MessageBox("Ei auttanut.");
	    return;
	}
	
	String desc = curRoom.polys.get(poly).actionStr;
	if(desc.length()==0) return; // jos p‰‰llekk‰isi‰ polyja, toisen polyn desc voi olla ""
	
	// jos eka kirjain on $ merkki, pit‰‰ kutsua funktiota scriptist‰
	if(desc.charAt(0)=='$') curRoom.runScript(desc.substring(1));
	else
	    MessageBox(desc);
	
	// h‰vi‰‰kˆ poly?
	curRoom.polys.get(poly).visible = !curRoom.polys.get(poly).removePoly;
	
	// onko linkattu johonkin esineeseen
	if (curRoom.polys.get(poly).itemNum > 0)
	{
	    // siirtyykˆ omiin tavaroihin?
	    if (curRoom.polys.get(poly).toInventory)
	    {
		// lis‰‰ esineen nimi tavaroihin
		inventory.add(curRoom.objs.get(curRoom.polys.get(poly).itemNum).name);
		System.out.println("lis‰t‰‰n tavaroihin: "+curRoom.objs.get(curRoom.polys.get(poly).itemNum).name);
	    }
	    // h‰vitet‰‰nkˆ ruudulta?
	    curRoom.objs.get(curRoom.polys.get(poly).itemNum-1).visible=!curRoom.polys.get(poly).removeFromScreen;
	    
	}
	
    }
    
    void useItem(int x, int y, int itemNum)
    {
	// etsi poly
	int poly = getPoly(x, y, 2);
	if (poly == -1)
	{
	    MessageBox("Miit‰‰?");
	    return;
	}
	
	// jos eka kirjain on $ merkki, pit‰‰ kutsua funktiota scriptist‰
	if(curRoom.polys.get(poly).successUseStr.charAt(0)=='$')
	{
	    curRoom.runScript(curRoom.polys.get(poly).successUseStr.substring(1));
	    return;
	}
	
	// jos v‰‰r‰‰ esinett‰ v‰‰r‰‰n paikkaan
	if (curRoom.polys.get(poly).needsItem != itemNum)
	{
	    MessageBox("No ei.");
	}
	else // oikea esine oikeaan paikkaan, esim avain oveen
	{
	    // h‰vi‰‰kˆ poly?
	    curRoom.polys.get(poly).visible = !curRoom.polys.get(poly).removePoly;
	    
	    // onko linkattu johonkin esineeseen
	    if (curRoom.polys.get(poly).itemNum > 0)
	    {
		// siirtyykˆ omiin tavaroihin?
		if (curRoom.polys.get(poly).toInventory)
		{
		    // lis‰‰ esineen nimi tavaroihin
		    inventory.add(curRoom.objs.get(curRoom.polys.get(poly).itemNum).name);
		    
		}
		// h‰vitet‰‰nkˆ ruudulta?
		curRoom.polys.get(poly).visible = !curRoom.polys.get(poly).removeFromScreen;
		
		// h‰vitet‰‰nkˆ k‰ytetty esine tavaroista
		if (curRoom.polys.get(poly).removeFromInventory)
		    inventory.remove(itemNum);
		
		// jos palautetta pelaajalle
		if (!curRoom.polys.get(poly).successUseStr.equals(""))
		{
		    MessageBox(curRoom.polys.get(poly).successUseStr);
		}
		
	    }
	}
    }
    
    /**
     * etsii klikattu polygon, ja sen pit‰‰ olla visible
     * mode 1: jos polya katsotaan
     * mode 2: jos ota/k‰yt‰
     */
    int getPoly(int x, int y, int mode)
    {
	// kaikki polyt l‰pi
	for (int q = 0; q < curRoom.polys.size(); q++)
	{
	    if (curRoom.polys.get(q).visible)
	    {
		// tarkista lˆytyykˆ
		if (Polygon.pointInPolygon(x, y, q) == true)
		{
		    // jos polylla ei ole selityst‰, etsi jos jollain toisella polylla on
		    // (t‰m‰ siksi koska polyja voi olla p‰‰llekk‰in)
		    if(mode==1) // katsotaan joten etsi kuvaus
		    {
			if (Game.curRoom.polys.get(q).descStr.equals("")) // v‰‰r‰ poly?
			{
			    continue;
			}
		    }
		    else
			if(mode==2) // ota/k‰yt‰ joten actionstr palaute
			{
			if (Game.curRoom.polys.get(q).actionStr.equals("")) // v‰‰r‰ poly?
			{
			    continue;
			}
			}
		    
		    // ok, taisi olla oikea poly ja sen indeksi palautetaan
		    return q;
		}
	    }
	}
	
	return -1; // ei lˆytynyt
    }
    
    /**
     * lataa huone ja etsi poly (PAIKKA startPlace) ja aseta juntti siihen. jos
     * startPlace=="", aseta ensinm‰iseen PAIKKA paikkaan.. jos ei lˆydy
     * sit‰k‰‰n, n‰yt‰ virhe.
     *
     * @param roomName
     * @param startPlace
     */
    public static void load(String roomName, String startPlace)
    {
	curRoom=null;
	curRoom=new Room();
	
	// lataa tiedot
	curRoom.load(roomName);
	
	// jos ep‰onnistui, palaa
	if (curRoom.backGroundImage.equals(""))
	    return;
	
	// aseta ukkeli
	dork.reset(0, 0);
	for (int q = 0; q < curRoom.polys.size(); q++)
	{
	    // etsi PAIKKA
	    String[] strs = curRoom.polys.get(q).newRoom.split(" ");
	    if (strs[0].equals("PAIKKA"))
	    {
		int x = 0, y = 0;
		
		if (startPlace.equals("") || // jos ei m‰‰r‰tty niin otetaan eka PAIKKA
		     strs[1].equals(startPlace)) // tai jos haluttu paikka
		{
		    x = curRoom.polys.get(q).verts.get(0).x;
		    y = curRoom.polys.get(q).verts.get(0).y;
		    dork.reset(x, y);
		    
		    break;
		}
		
	    }
	}
	// ei lˆytynyt paikkaa
	if(dork.x==0 && dork.y==0)
	{
	    Game.MessageBox("PAIKKA puuttuu, huoneen tiedot vajaat.");
	}
	
	// lataa tausta
	if(curRoom.backGroundImage.length()>0)
	    curRoom.loadBackground(curRoom.backGroundImage);
	
	// lataa zbuffer
	if(curRoom.zBufImage.length()>0)
	    curRoom.loadZBuf(curRoom.zBufImage);
	
	// esineet
	for (int q = 0; q < curRoom.objs.size(); q++)
	{
	    // lataa esine
	    curRoom.objs.get(q).pic = Room.loadImage(curRoom.objs.get(q).fileName);
	}
	
	// lataa scripti jos lˆytyy
	curRoom.loadScript(roomName+".sc");
	
    }
    
}
