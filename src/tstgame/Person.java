/**
 * @file Person.java
 * @author mjt, 2006-07
 * mixut@hotmail.com
 *
 * @created 31.10.2006
 * @edited 15.6.2007
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
 * henkilöluokka
 */
class Person
{
    // dorkan asennot
    BufferedImage[] pic = new BufferedImage[4*6];
    
    /** juntin koordinaatit */
    int x = 0, y = 0;
    /** juntin koordinaatit mihin se liikkuu */
    int tx = 0, ty = 0;
    float fx = 0, fy = 0;
    float addx = 0, addy = 0;
    int cur=0;
    
    void reset(int x, int y)
    {
	this.x = x;
	this.y = y;
	this.tx = x;
	this.ty = y;
	fx = x;
	fy = y;
    }
    
    final int SPD=6;
    int direction=6;
    public BufferedImage getPic()
    {
	return pic[direction+cur/SPD];
    }
    
    /**
     * liikuta junttia kohti päämääräänsä surkeesti toteutettu, ei reitinhakua
     */
    public void update()
    {
	if (tx == x && ty == y)
	    return;
	
	float lx = (float) (tx - x), ly = (float) (ty - y);
	
	float ux = Math.abs(lx);
	float uy = Math.abs(ly);
	
	if (ux == uy)
	    addx = addy = 1;
	else if (ux > uy)
	{
	    addx = 1;
	    addy = uy / ux;
	}
	else
	{
	    addy = 1;
	    addx = ux / uy;
	}
	
	if (lx < 0)
	    addx = -addx;
	if (ly < 0)
	    addy = -addy;
	
	// TODO tää systeemi paremmaksi
	int tmpx = 10; //pic.getWidth();
	
	if (!canMove((int) (fx + addx), (int) fy) ||
	     !canMove((int) (fx + addx + tmpx), (int) fy))
	    addx=0;
	if (!canMove((int) (fx), (int) (fy + addy)) ||
	     !canMove((int) (fx + tmpx), (int) (fy + addy)))
	    addy=0;
	
	if(addx==0 && addy==0) return;
	
	fx+=addx;
	fy+=addy;
	
	x = (int) fx;
	y = (int) fy;
	
	// laske kulma
	double ang=Math.toDegrees(Math.atan2(addy, addx));
	if(ang<0) ang=360+ang;
	if(ang>=270-45 && ang<360-45) direction=0;
	if(ang>=360-45 || ang<90-45) direction=6;
	if(ang>=90-45 && ang<180-45) direction=12;
	if(ang>=180-45 && ang<270-45) direction=18;
	
	cur++;
	if(cur==6*SPD) cur=0;
    }
    
    public void walkTo(int mx, int my)
    {
	tx = mx;
	ty = my;
    }
    
    /**
     * tarkistaa voiko x,y kohtaan liikkua (ei esteitä tiellä). palauttaa true
     * jos voi.
     */
    boolean canMove(int x, int y)
    {
	// tarkista ensin ruudun reunoihin
	if(x<0 || y<0 || x>Game.curRoom.bgImage.getWidth() || y>Game.curRoom.bgImage.getHeight()) return false;
	
	
	for (int q = 0; q < Game.curRoom.polys.size(); q++)
	{
	    // jos osuu polygoniin
	    if (Polygon.pointInPolygon(x, y, q))
	    {
		// jos poly on huoneenvaihto tai ukon paikan merkkaaja poly
		if (!Game.curRoom.polys.get(q).newRoom.equals(""))
		{
    		    // pitääkö kutsua scriptiä
		    if(Game.curRoom.polys.get(q).newRoom.charAt(0)=='$')
		    {
			Game.curRoom.runScript(Game.curRoom.polys.get(q).newRoom.substring(1));
			return false;
		    }
		    
		    String[] strs = Game.curRoom.polys.get(q).newRoom.split(" ");
		    
		    // tarkista onko paikan merkkaaja (PAIKKA)
		    if (strs[0].equals("PAIKKA"))
			return true; // paikkamerkki, eli ei välitetä siitä.
		    
		    if (strs.length == 1)
			Game.load(strs[0], ""); // lataa paikka, pistä ukko ekaan PAIKKA:aan mikä löytyy
		    else
			Game.load(strs[0], strs[1]); // huone ja paikka
		    return false;
		}
		
		// jos estepolygoni, palauta false
		if (Game.curRoom.polys.get(q).block)
		    return false;
	    }
	}
	return true; // ei ongelmia, voi liikkua
    }
    
    /**
     * lataa juntin asennot
     *
     * @param fileName
     */
    void load()
    {
	int q;
	
	for(q=0; q<6; q++)
	{
	    String fileName="up/pic"+q+".png";
	    pic[q] = Room.loadImage(fileName);
	    
	    fileName="right/pic"+q+".png";
	    pic[6+q] = Room.loadImage(fileName);
	    
	    fileName="down/pic"+q+".png";
	    pic[12+q] = Room.loadImage(fileName);
	    
	    fileName="left/pic"+q+".png";
	    pic[18+q] = Room.loadImage(fileName);
	}
	
	for(q=0; q<4*6; q++)
	{
	    for(int h=0; h<pic[q].getHeight(); h++)
	    {
		for(int w=0; w<pic[q].getWidth(); w++)
		{
		    if(pic[q].getRGB(w, h)==(255<<24)+(254<<16)+(254<<8)+254) pic[q].setRGB(w,h, 0); //(0<<24)+(255<<16)+(255+8)+255); 
		    
		}
	    }
	    
	}
	
    }
    
}
