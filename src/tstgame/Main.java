/**
 * @file Main.java
 * @author mjt, 2006-07 
 * mixut@hotmail.com
 *
 * @created 31.10.2006
 * @edited 16.6.2007
 *
 * yksinkertaisen pelin runko.
 * lataa SPEditorilla tehdyt pelin tiedot.
 *
 * 14.6
 * scriptitiedosto ladataan samalla kun huone tiedot (jos semmone siis on). jos huone on bar1 niin scripti on bar1.sc ...
 * polygonin voi asettaa k‰ynnist‰m‰‰n scriptin funktion $funktio()  jolloin "funktio" suoritetaan scriptiss‰.
 *
 * 15.6
 * kaikki latautuu jar tiedostosta.
 *
 */
package tstgame;

import java.io.File;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class Main extends JPanel implements MouseListener, MouseMotionListener
{
    static final boolean DEBUG=false;
    
    public static boolean running=true;
    
    public static final String ROOMDIR = "/rooms/";
    public static final String PICSDIR = "/pics/";
    public static boolean initOK=false;
    
    static JFrame frame = null;
    
    static Main main=null;
    public static void main(String[] args)
    {
	main = new Main();
	main.run();
    }
    
    public Main()
    {
	frame = new JFrame("Aamukrapula");
	
	Color col = new Color(0, 0, 0);
	frame.setBackground(col);
	frame.setSize(640, 480);
	
	// frame.addKeyListener(this);
	frame.setContentPane(this);
	frame.setResizable(false);
	frame.addMouseListener(this);
	frame.addMouseMotionListener(this);
	frame.addWindowListener(new WindowAdapter()
	{
	    public void windowClosing(WindowEvent e)
	    {
		System.exit(0);
	    }
	});
	
	frame.setVisible(true);
	
	// piilota hiirikursori
	setCursor(getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB),
	     new Point(0, 0), "null"));
	
    }
    
    BufferedImage backgr=null;
    void run()
    {
	Game game = new Game();
	backgr=Room.loadImage("tausta.jpg");
	
	playMidi("/Dont_Fear_The_Reaper.mid");
	
	initOK=true;
	
	while (running)
	{
	    game.run();
	    
	    repaint();
	    
	    try
	    {
		Thread.sleep(10);
	    }
	    catch (InterruptedException e)
	    {
	    }
	    
	    Game.curRoom.runScript(""); // tarkista huoneeseen tulo (t‰m‰ siksi t‰‰ll‰ jotta graffat on jo piirretty ruudulle)
	    
	    
	    // jos midi loppuu niin alusta
	    if(sequencer!=null)
	    {
		if(!sequencer.isRunning())
		{
		    closeMidi();
		    sequencer=null;
		    
		    try
		    {
			Thread.sleep(300);
		    }
		    catch (InterruptedException e)
		    {
		    }
		    playMidi("/I_Will_Remember.mid");
		}
	    }
	    
	}
	
	closeMidi();
	System.exit(0);
	
    }
    
    // scriptist‰ k‰sin voidaan piirrell‰
    static BufferedImage drawImg;
    static int ix, iy;
    static public void drawImage(BufferedImage img, int x, int y)
    {
	drawImg=img;
	ix=x;
	iy=y;
    }
    /*static public void drawImage(BufferedImage img, int x, int y)
    {
	((Graphics2D)frame.getGraphics()).drawImage(img, x, y, null);
    }   */
    
    public static int transX=0, transY=0;
    
    public void paintComponent(Graphics g)
    {
	if(!initOK) return;
	
	//super.paintComponent(g); // tyhjenn‰ ruutu
	Graphics2D g2d = (Graphics2D) g;
	
	g2d.drawImage(backgr, 0, 0, null);
	
	transX=320-Game.curRoom.bgImage.getWidth()/2;
	transY=240-Game.curRoom.bgImage.getHeight()/2;
	g2d.translate(transX, transY);
	
	// piirr‰ taustakuva
	g2d.drawImage(Game.curRoom.bgImage, 0, 0, null);
	
	// piirr‰ esineet
	for (int q = 0; q < Game.curRoom.objs.size(); q++)
	{
	    Item2D item = Game.curRoom.objs.get(q);
	    if (item.visible)
		g2d.drawImage(item.pic, item.x, item.y, null);
	    
	}
	
	// piirr‰ henkilˆ
	if(Game.dork.getPic()!=null)
	    g2d.drawImage(Game.dork.getPic(), Game.dork.x, Game.dork.y - Game.dork.getPic().getHeight(), null);
	
	
	// script draw
	if(drawImg!=null) g2d.drawImage(drawImg, ix, iy, null);
	
	
	g2d.translate(-transX, -transY);
	
	// piirr‰ hiirikursori
	if(!Game.outOfArea) g2d.drawImage(Game.cursors[Game.mode], Game.mx, Game.my, null);
	else g2d.drawImage(Game.cursors[3], Game.mx, Game.my, null);
	
	if(DEBUG)
	{
	    // DEBUG: piirr‰ kaikki huoneen polyt ---
	    for (int q = 0; q < Game.curRoom.polys.size(); q++)
	    {
		if (Polygon.pointInPolygon(Game.mx, Game.my, q))
		    g.setColor(Color.red);
		else
		    g.setColor(Color.green);
		
		for (int w = 0; w < Game.curRoom.polys.get(q).verts.size() - 1; w++)
		{
		    if( Game.curRoom.polys.get(q).visible )
			g.drawLine(Game.curRoom.polys.get(q).verts.get(w).x, Game.curRoom.polys.get(q).verts.get(w).y,
			     Game.curRoom.polys.get(q).verts.get(w + 1).x, Game.curRoom.polys.get(q).verts.get(w + 1).y);
		}
	    } // DEBUG ---
	}
    }
    
    public void mouseClicked(MouseEvent me)
    {
    }
    public void mousePressed(MouseEvent me)
    {
	Game.getUse=false;
	
	Game.mx = me.getX();
	Game.my = me.getY();
	
	if (me.getButton() == MouseEvent.BUTTON1)
	    Game.mbutton = 1;
	if (me.getButton() == MouseEvent.BUTTON2)
	    Game.mbutton = 2;
	if (me.getButton() == MouseEvent.BUTTON3)
	    Game.mbutton = 3;
    }
    public void mouseDragged(MouseEvent me)
    {
	Game.mx = me.getX();
	Game.my = me.getY();
    }
    public void mouseMoved(MouseEvent me)
    {
	Game.mx = me.getX();
	Game.my = me.getY();
    }
    public void mouseReleased(MouseEvent me)
    {
	Game.mbutton = 0;
    }
    public void mouseEntered(MouseEvent me)
    {
    }
    public void mouseExited(MouseEvent me)
    {
    }
    
    
    /**
     *	lataa file miditiedosto. jos res==true, se ladataan jar-tiedostosta
     *
     */
    Sequencer sequencer=null;
    void playMidi(String file)
    {
	
	URL url=null;
	File midiFile=null;
	try
	{
	    sequencer = MidiSystem.getSequencer();
	    
	    url = this.getClass().getResource(file);
	    if(url==null)
	    {
		midiFile = new File(file);
		sequencer.setSequence(MidiSystem.getSequence(midiFile));
	    }
	    else
		sequencer.setSequence(MidiSystem.getSequence(url));
	    
	    sequencer.open();
	    sequencer.start();
	}
	catch(MidiUnavailableException mue)
	{
	    sequencer=null;
	    Game.ErrorMessage("Midi device unavailable!");
	}
	catch(InvalidMidiDataException imde)
	{
	    sequencer=null;
	    Game.ErrorMessage("Invalid Midi data!");
	}
	catch(IOException ioe)
	{
	    sequencer=null;
	    Game.ErrorMessage("Invalid Midi data!");
	}
	
    }
    void closeMidi()
    {
	if(sequencer!=null)
	{
	    sequencer.stop();
	    sequencer.close();
	}
    }
    
    
}



/**
 * esineiden luokka
 *
 */
class Item2D
{
    /** esineen nimi */
    String name = "";
    /** kuvatiedoston nimi */
    String fileName = "";
    
    /** esineen paikka huoneessa */
    int x = 0, y = 0;
    /** true niin piirret‰‰n */
    boolean visible = true;
    /** kuva */
    BufferedImage pic = null;
}

class Vector2i
{
    public int x = 0, y = 0;
}

class Polygon
{
    /** polygonin verteksit */
    Vector<Vector2i> verts = new Vector<Vector2i>();
    
    /** selitys jos sit‰ katsoo */
    String descStr = "";
    /** jos ota/k‰yt‰ niin pit‰‰kˆ kirjoittaa jotain */
    String actionStr = "";
    
    /** h‰vitet‰‰nkˆ poly jos ota/k‰yt‰ */
    boolean removePoly = false;
    /** jos true, poly toimii esteen‰ */
    boolean block = true;
    
    /** esine tiedot */
    /** jos vaikuttaa esineeseen, sen index */
    int itemNum = -1;
    /** jos ota/k‰yt‰, h‰vitet‰‰nkˆ esine ruudulta */
    boolean removeFromScreen = false;
    /** tuleeko esine omiin tavaroihin */
    boolean toInventory = false;
    
    /**
     * jos vaatii tavaran ennenkuin yll‰ olevat tapahtumat tapahtuu. esim ovi
     * vaatii avaimen. needsItem on vaikka 1 joka olisi avain. pelkk‰ ota/k‰yt‰
     * ei avaa ovea koska needsItem>=0 ja jos avaimella klikkaa polya, niin
     * sitten toteutetaan removeFromScreen jos true, removePoly jos true jne.
     */
    int needsItem = -1;
    
    /**
     * jos polygoni viel‰ vaikuttaa ruudulla, eli false niin polya ei huomioida
     */
    boolean visible = true;
    
    /**
     * tarkista onko xy kohta polygonin sis‰ll‰.
     *
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/insidepoly/
     */
    public static boolean pointInPolygon(int x, int y, int polynum)
    {
	int i, j;
	boolean c = false;
	
	Polygon poly = Game.curRoom.polys.get(polynum);
	if(poly.visible==false) return false;
	
	for (i = 0, j = poly.verts.size() - 1; i < poly.verts.size(); j = i++)
	{
	    Vector2i v1 = poly.verts.get(i);
	    Vector2i v2 = poly.verts.get(j);
	    
	    if ((((v1.y <= y) && (y < v2.y)) || ((v2.y <= y) && (y < v1.y)))
	    && (x < (v2.x - v1.x) * (y - v1.y) / (v2.y - v1.y) + v1.x))
		c = !c;
	    
	}
	return c;
    }
    
    /**
     * jos polygon on linkki uuteen huoneeseen, t‰ss‰ sen nimi.
     */
    String newRoom = "";
    
    /**
     * poistetaanko esine k‰ytˆn j‰lkeen tavaroista
     */
    boolean removeFromInventory = false;
    
    /**
     * onnistuneen esineen k‰ytˆn teksti
     */
    String successUseStr = "";
    
}

