// JFC
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;
import java.applet.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.engine.BaseIO.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*; 
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.sprite.AdvanceSprite; 
import com.golden.gamedev.util.*;
import netscape.javascript.*;

/**
 * Game in Windowed Mode Environment.
 *
 * Objective: show how to set up a windowed mode game
 */


public class Tentoumushi extends Game {


    Background      layer1BG;
    int[][]         layer1Map = new int[20][15];
    int[][]         gameMap = new int[20][15];
    int             gameStage=0; // 0 = main menu, 1 = ingame, 2 = level done
    SpriteGroup     BLOCKS_GROUP;
    BugSprite       bug;
    GameFont        font;
    BGSprite        bigBug;
    BGSprite        wellDone;
    BGSprite        levelSign;
    BGSprite        cross;
    GameFont        defaultFont;
    GameFont        menuFont;
    int             currentLevel=0;
    int             continueLevel=0;
    boolean         doneJS=false;
    JSObject        browserWindow;
    boolean         musicPlaying = true;

 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    { distribute=true; }

    public void initResources() {

        // Filling in random values for the tiles
        
        setMaskColor(Color.WHITE);

        BufferedImage[] layer1Images = getImages("images/bgtiles.png", 4, 5);

        for( int x=0; x<layer1Map.length; x++ )
            for( int y=0; y<layer1Map[0].length; y++ )
            {
                int rnd = getRandom(0, 3);

                layer1Map[x][y] = rnd;
                gameMap[x][y] = 0;
            }

        layer1BG = new TileBackground( layer1Images, layer1Map );

        BLOCKS_GROUP = new SpriteGroup("Blocks Group");
        BLOCKS_GROUP.setBackground(layer1BG);

        bug = new BugSprite(getImages("images/ladybug-moving.png",2,4));
        bug.setActive( false );
        bug.setAnimate( true );
        bug.setDirection( 0 );

        bigBug = new BGSprite( getImage("images/bigbug.png") );
        bigBug.setX( -80 );
        bigBug.setY( 220 );
        bigBug.setActive( true );

        wellDone = new BGSprite( getImage("images/welldone.png" ) );
        wellDone.setX( 150 );
        wellDone.setY( 125 );
        wellDone.setTarget( 150, 125 );

        levelSign = new BGSprite( getImage("images/levelsign.png" ) );
        levelSign.setX( 400 );
        levelSign.setY( 0 );
        levelSign.setTarget( 400, 0 );

        cross = new BGSprite( getImage("images/cross.png") );
        cross.setX( 10 );
        cross.setY( 10 );
        cross.setTarget( 10, 10 );

        defaultFont = fontManager.getFont(new java.awt.Font("Verdana", Font.BOLD, 14));
        menuFont = fontManager.getFont(new java.awt.Font("Verdana", Font.BOLD, 10));

        Applet applet = (Applet) bsGraphics;
        browserWindow = (JSObject) JSObject.getWindow(applet); 

        // Checking to see if there's a cookie storing the progress
        continueLevel = Integer.parseInt( getCookie( "progress" ) );

        // Playing some music
        bsMusic.play("haiku.mid");
    }

    public void toggleMusic() {
        if( musicPlaying == true ) {
            bsMusic.stop("haiku.mid");
            musicPlaying = false;
        } else {
            bsMusic.play("haiku.mid");
            musicPlaying = true;
        }
    }

    public void update(long elapsedTime) {
        if( keyDown(KeyEvent.VK_SPACE) ) {
            // System.out.println("-");
        }

        if( gameStage == 2 ) { // Finished level

            if( keyPressed( 'M' ) ) { // Music toggle
                toggleMusic();
            }

            if( bigBug.moving == false ) { // Loading new level

                //if( ( keyDown( KeyEvent.VK_SPACE )) || keyDown( (KeyEvent.VK_ENTER )) ) {
                    currentLevel = currentLevel + 1;

                    if( currentLevel > 152 ) {
                        gameStage = 4;
                    } else {
                        LoadLevel( currentLevel );
                        gameStage = 1;
                    }
                //}
            }

            bigBug.update( elapsedTime );
            wellDone.update( elapsedTime );
            levelSign.update( elapsedTime );
        }

        if( gameStage == 0 ) { // Main Menu
            if( keyDown( 'N' ) ) { // New Game
                gameStage = 1;
                currentLevel=1;
                LoadLevel(currentLevel);
            }

            if( keyDown( 'C' ) ) { // Continue
                if( continueLevel > 1 ) {
                    gameStage = 1;
                    currentLevel = continueLevel;
                    LoadLevel( currentLevel );
                }
            }

            if( keyDown( 'A' ) ) { // About Screen
                gameStage = 3;
            }

            if( keyPressed( 'M' ) ) { // Music toggle
                toggleMusic();
            }
        }

        if( gameStage == 3 ) { // About Screen
            if( keyDown( KeyEvent.VK_ENTER ) ) {
                gameStage = 0;
            }

            if( keyDown( KeyEvent.VK_SPACE ) ) {
                gameStage = 0;
            }


            if( keyPressed( 'M' ) ) { // Music toggle
                toggleMusic();
            }
            //System.out.println( Integer.toString(KeyEvent.KEY_DOWN) );
        }

        if( gameStage == 4 ) { // Finished the game

            if( keyPressed( 'M' ) ) { // Music toggle
                toggleMusic();
            }
            // Nothing to do, people will just be stuck here \o/
        }

        if( gameStage == 1 ) { // In Game


            if( keyPressed( 'M' ) ) { // Music toggle
                toggleMusic();
            }

            if( keyDown( 'R' ) ) { // Restart level
                if( bigBug.moving == false ) {
                    LoadLevel(currentLevel);
                }
            }

            if( keyDown( 'S' ) && distribute==false) { // Skip level
                if( bigBug.moving == false ) {
                    currentLevel = currentLevel + 1;
                    LoadLevel( currentLevel );
                }
            }

            if( keyDown( 'P' ) && distribute==false) { // Fast skip
                currentLevel = currentLevel + 1;
                LoadLevel( currentLevel );
            }

            if( keyDown( 'Q' ) && distribute==false && doneJS == false) {
                doneJS = true;

                String result = getCookie( "progress" );
                browserWindow.eval( "alert('"+ result +"');" );
            }

            if( keyDown( 'F' ) && distribute==false ) {
                gameStage = 4;
            }

            Sprite[] sprite = BLOCKS_GROUP.getSprites();

            // Moving the player

            int modX=0;
            int modY=0;
            int dir = bug.getDirection(); // Direction
            boolean mustMove = false;
            boolean levelDone = true;

            for( int i=0; i<BLOCKS_GROUP.getSize(); i++ ) { // Checking to see if we finished the level yet
                int blockX = ((Block) sprite[i]).cellX;
                int blockY = ((Block) sprite[i]).cellY;
                boolean blockMoving = ((Block) sprite[i]).moving;

                if( (layer1Map[blockX][blockY] != 18) || (blockMoving) ) {
                    levelDone = false;
                }
            }

            if( levelDone ) {
                gameStage = 2;
                bigBug.setTarget( -180, 220 );
                levelSign.setTarget( 400, 0 );
            }

            if( bug.moving == false ) { // Movement code, block checking

                if( keyDown( KeyEvent.VK_UP ) )    { modY = -1; dir=0; mustMove = true; }
                if( keyDown( KeyEvent.VK_DOWN ) )  { modY = 1;  dir=2; mustMove = true; }
                if( keyDown( KeyEvent.VK_LEFT ) )  { modX = -1; dir=3; mustMove = true; }
                if( keyDown( KeyEvent.VK_RIGHT ) ) { modX = 1;  dir=1; mustMove = true; }

                if( (modX != 0) && (modY != 0) ) {
                    modY = 0;
                }

                if( (gameMap[ bug.cellX + modX ][ bug.cellY + modY ] == 1) && (mustMove == true) ) {
                    boolean mustPush = false;
                    int blockToPush = 0;

                    for( int i=0; i<BLOCKS_GROUP.getSize(); i++ ) {
                        // Checking to see if there's a block where we want to go
                        
                        if( ( ((Block) sprite[i]).cellX==(bug.cellX+modX) ) &&
                            ( ((Block) sprite[i]).cellY==(bug.cellY+modY) ) ) {

                            blockToPush = i;
                            mustPush = true;
                        }
                    }

                    if( mustPush == true ) {
                        boolean canPush = true;

                        // Checking to see if we can push the block
                        //
                        for( int i=0; i<BLOCKS_GROUP.getSize(); i++ ) {
                        
                            if( ( ((Block) sprite[i]).cellX==(bug.cellX+(modX*2)) ) &&
                                ( ((Block) sprite[i]).cellY==(bug.cellY+(modY*2)) ) ) {

                                canPush = false;
                            }
                        }

                        if( gameMap[bug.cellX+(modX*2)][bug.cellY+(modY*2)] != 1) {
                            canPush = false;
                        }

                        if( canPush == true ) {
                            ((Block) sprite[blockToPush]).cellX = ((Block) sprite[blockToPush]).cellX + modX;
                            ((Block) sprite[blockToPush]).cellY = ((Block) sprite[blockToPush]).cellY + modY;
                            ((Block) sprite[blockToPush]).moving = true;

                            bug.setTarget( modX, modY, dir );
                        } else {
                            //System.out.println("Can't push");
                        }
                    } else {
                        bug.setTarget( modX, modY, dir );
                    }
                }
            } else {
                /* System.out.println("Can't move ["+Integer.toString(bug.cellX)+"]["+Integer.toString(bug.cellY)+"]"
                                  +" -modX,modY="+Integer.toString(modX)+","+Integer.toString(modY)
                                  +" gameMap="+Integer.toString(gameMap[bug.cellX+modX][bug.cellY+modY])
                                  ); */
            }

            // Checking to see if any blocks are standing on a pit
            
            for( int i=0; i<BLOCKS_GROUP.getSize(); i++ ) {

                int currentFrame = ((Block) sprite[i]).getFrame();
                int mapValue = layer1Map[ ((Block) sprite[i]).cellX ][ ((Block) sprite[i]).cellY ];

                if( (currentFrame == 0) && (mapValue == 18) && (((Block) sprite[i]).moving == false) ) { // 18 == pit
                    ((Block) sprite[i]).setFrame(1);
                }

                if( (currentFrame == 1) && (mapValue == 17) ) { // 17 == normal empty tile
                    ((Block) sprite[i]).setFrame(0);
                }

                //System.out.println( "["+currentFrame+","+gameMap[bug.cellX][bug.cellY]+"]");
            }

            // Updating the sprites
            BLOCKS_GROUP.update( elapsedTime );
            bug.update( elapsedTime );
            bigBug.update( elapsedTime );
            levelSign.update( elapsedTime );
        }
    }

    public void render(Graphics2D g) {
        layer1BG.render(g);

        int levelX = (int) levelSign.getX()+29;
        int levelY = (int) levelSign.getY()+29;

        if( currentLevel < 10 ) {
            levelX = levelX+5;
        }

        if( currentLevel > 99 ) {
            levelX = levelX-5;
        }

        g.setColor( Color.WHITE );

        if( gameStage == 0 ) { // Main Menu
            g.setColor( new Color(50, 125, 56, 150) );
            g.fillRect( 160, 110, 100, 115 );
            g.setColor( new Color(80, 155, 86) );
            g.fillRect( 150, 100, 100, 115 );
            g.setColor( new Color(50, 125, 56) );
            g.drawRect( 150, 100, 100, 115 );

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "N - New Game", 161, 111);
            g.setColor( Color.WHITE );
            menuFont.drawString(g, "N - New Game", 160, 110);

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "C - Continue", 161, 131);

            if( continueLevel > 1 ) {
                g.setColor( Color.WHITE );
                menuFont.drawString(g, "C - Continue", 160, 130);
            } else {
                g.setColor( new Color(190,190,190) );
                menuFont.drawString(g, "C - Continue", 160, 130);
            }

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "R - Restart", 161, 151);
            g.setColor( Color.WHITE );
            menuFont.drawString(g, "R - Restart", 160, 150);

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "M - Music", 161, 171);
            g.setColor( Color.WHITE );
            menuFont.drawString(g, "M - Music", 160, 170);

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "A - About", 161, 191);
            g.setColor( Color.WHITE );
            menuFont.drawString(g, "A - About", 160, 190);

            cross.render(g);
        }

        if( gameStage == 1 ) { // In Game 
            BLOCKS_GROUP.render(g);
            bug.render(g);
            bigBug.render(g);
            levelSign.render(g);

            g.setColor( Color.BLACK );
            defaultFont.drawString(g, Integer.toString(currentLevel), levelX+1, levelY+1);
            g.setColor( Color.WHITE );
            defaultFont.drawString(g, Integer.toString(currentLevel), levelX,  levelY);
        }

        if( gameStage == 2 ) { // In Between Levels
            BLOCKS_GROUP.render(g);
            bug.render(g);
            bigBug.render(g);
            wellDone.render(g);
            levelSign.render(g);

            g.setColor( Color.BLACK );
            defaultFont.drawString(g, Integer.toString(currentLevel), levelX+1, levelY+1);
            g.setColor( Color.WHITE );
            defaultFont.drawString(g, Integer.toString(currentLevel), levelX,  levelY);
        }

        if( gameStage == 3 ) { // About screen
            g.setColor( new Color(50, 125, 56, 150) );
            g.fillRect( 60, 110, 300, 100 );
            g.setColor( new Color(80, 155, 86) );
            g.fillRect( 50, 100, 300, 100 );
            g.setColor( new Color(50, 125, 56) );
            g.drawRect( 50, 100, 300, 100 );

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "Coding/Graphics:", 61, 111);
            g.setColor( Color.WHITE );
            menuFont.drawString(g, "Coding/Graphics:", 60, 110);

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "Tom Scheper (scheper@gmail.com)", 141, 131);
            g.setColor( Color.WHITE );
            menuFont.drawString(g, "Tom Scheper (scheper@gmail.com)", 140, 130);

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "Microban Levelset:", 61, 151);
            g.setColor( Color.WHITE );
            menuFont.drawString(g, "Microban Levelset:", 60, 150);

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "David Skinner (sasquatch@bentonrea.com)", 94, 171);
            g.setColor( Color.WHITE );
            menuFont.drawString(g, "David Skinner (sasquatch@bentonrea.com)", 93, 170);

            cross.render(g);
        }

        if( gameStage == 4 ) { // Finished the game
            g.setColor( new Color(50, 125, 56, 150) );
            g.fillRect( 60, 130, 300, 60 );
            g.setColor( new Color(80, 155, 86) );
            g.fillRect( 50, 120, 300, 60 );
            g.setColor( new Color(50, 125, 56) );
            g.drawRect( 50, 120, 300, 60 );

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "--------------------------------------------------", 78, 133);
            g.setColor( Color.WHITE );
            menuFont.drawString(g, "--------------------------------------------------", 77, 132);

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "Congratulations! You finished all 152 levels!", 78, 143);
            g.setColor( Color.WHITE );
            menuFont.drawString(g, "Congratulations! You finished all 152 levels!", 77, 142);

            g.setColor( Color.BLACK );
            menuFont.drawString(g, "--------------------------------------------------", 78, 153);
            g.setColor( Color.WHITE );
            menuFont.drawString(g, "--------------------------------------------------", 77, 152);
        }
    }

    public void LoadLevel(int levelNumber) {

        BLOCKS_GROUP.clear();
        bug.setActive( false );

        if( levelNumber == 0 ) { // Just add random background tiles
            for( int x=0; x<layer1Map.length; x++ )
                for( int y=0; y<layer1Map[0].length; y++ )
                {
                    int rnd = getRandom(0, 3);

                    layer1Map[x][y] = rnd;
                    gameMap[x][y] = 0;
                }
        }

        if( (levelNumber > 0) && (levelNumber < 153) ) {
            String jscmd = "SetCookie('progress',"+ Integer.toString(levelNumber) +",120);";
            browserWindow.eval( jscmd );

            String fileName;
            String[] levelTiles;

            bigBug.setX( -80 );
            bigBug.setY( 220 );
            bigBug.setTarget( 0, 220 );

            levelSign.setX( 400 );
            levelSign.setY( 0 );
            levelSign.setTarget( 320, 0 );

            bug.setActive( true );

            fileName = "levels/level" + Integer.toString(levelNumber) + ".txt";

            // Setting a cookie with the level number
            // JSObject.getWindow (this).eval ("document.cookie ='" +(String)arg +"';");

            // System.out.println( fileName );

            levelTiles = FileUtil.fileRead( bsIO.getStream( fileName ) );

            // Clearing the maps

            for( int y=0; y<15; y++ ) {
                for( int x=0; x<20; x++ ) {
                    gameMap[x][y]=0;
                }
            }

            // Filling the maps

            for( int y=0; y<15; y++ ) {
                for( int x=0; x<20; x++ ) {
                    int tileValue = Character.getNumericValue( levelTiles[y].charAt(x) );

                    if( tileValue != 0 ) {
                        // System.out.println( Integer.toString(tileValue) + " - " + levelTiles[y].charAt(x));
                    }

                    if( tileValue >= 20 ) {

                        if( tileValue == 20 ) {
                            layer1Map[x][y] = 18; // A pit here for the block
                            gameMap[x][y] = 1;

                            Block block = new Block(getImages("images/block.png",2,1));

                            block.setX( x*20 );
                            block.setY( y*20 );
                            block.cellX = x;
                            block.cellY = y;

                            BLOCKS_GROUP.add( block );
                        } else {
                            layer1Map[x][y] = 17; // Just an empty space
                            gameMap[x][y] = 1; 
                        }

                        if( tileValue == 21 ) { // BugSprite position
                            bug.setX( x*20 );
                            bug.setY( y*20 );
                            bug.cellX = x;
                            bug.cellY = y;

                            gameMap[x][y] = 1; // A block here
                        }

                        if( tileValue == 22 ) {
                            gameMap[x][y] = 1; // A block here

                            Block block = new Block(getImages("images/block.png",2,1));

                            block.setX( x*20 );
                            block.setY( y*20 );
                            block.cellX = x;
                            block.cellY = y;

                            BLOCKS_GROUP.add(block);
                        }

                    } else if( tileValue == 0 ) {

                        int rnd = getRandom(0,9);

                        if( rnd == 0 ) {
                            layer1Map[x][y] = getRandom(0,2)+1;
                        } else {
                            layer1Map[x][y] = 0;
                        }
                    } else if( (tileValue == 17) || (tileValue == 18) ) {
                        gameMap[x][y] = 1;
                        layer1Map[x][y] = tileValue;
                    } else {
                        layer1Map[x][y] = tileValue;
                    }
                }
            }
        }
    }

 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tentoumushi(), new Dimension(400,300), false);
        game.start();
    }

    public String getCookie() {
        /*
        ** get all cookies for a document
        */
        try {
            JSObject myDocument =  (JSObject) browserWindow.getMember("document");
            String myCookie = (String)myDocument.getMember("cookie");
            if (myCookie.length() > 0) 
               return myCookie;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "?";
    }

    public String getCookie(String name) {
        /*
        ** get a specific cookie by its name, parse the cookie.
        **    not used in this Applet but can be useful
        */
        String myCookie = getCookie();
        String search = name + "=";
        if (myCookie.length() > 0) {
            int offset = myCookie.indexOf(search);
            if (offset != -1) {
                offset += search.length();
                int end = myCookie.indexOf(";", offset);
                if (end == -1) end = myCookie.length();
                return myCookie.substring(offset,end);
            } else {
                return "0";
            }
        }
        return "0";
    }
}

/*    *    *    *    *    *    *    *    *    *    *    *    */

class BGSprite extends Sprite {
    int targetX;
    int targetY;

    boolean moving;

    public BGSprite( BufferedImage image ) {
        super(image,0,0);
    }

    public void update(long elapsedTime) {
        if( moveTo( elapsedTime, targetX, targetY, 0.1 ) ) {
            moving = false;
        } else {
            moving = true;
        }

        super.update( elapsedTime );
    }

    public void setTarget( int newX, int newY ) {
        targetX = newX;
        targetY = newY;
        moving = true;
    }
}

/*    *    *    *    *    *    *    *    *    *    *    *    */

class Block extends AnimatedSprite {
    int cellX;
    int cellY;

    boolean moving;

    public Block(BufferedImage[] image) {
	    super(image,0,0);
	}

    public void update(long elapsedTime) {
        if( moveTo( elapsedTime, cellX*20, cellY*20, 0.1 ) ) {
            moving = false;
        }

        super.update( elapsedTime );
    }
}

/*    *    *    *    *    *    *    *    *    *    *    *    */

class BugSprite extends AdvanceSprite {
    int cellX;
    int cellY;

    boolean moving;

    public static final int[][] animation = new int[][] { { 0, 1 }, // Up
                                                          { 2, 3 }, // Right
                                                          { 4, 5 }, // Down
                                                          { 6, 7 } }; // Left

    public static final int[][] standingstill = new int[][] { { 0, 0 },
                                                              { 2, 2 },
                                                              { 4, 4 },
                                                              { 6, 6 } };

    public BugSprite(BufferedImage[] image) {
        super(image,0,0);
    }

    public void update(long elapsedTime) {
        if( moveTo( elapsedTime, cellX*20, cellY*20, 0.1 ) ) {
            moving = false;
            setStatus( 0 ); // Moving stopped
        }

        super.update( elapsedTime );
    }

    //public void setTarget(int modX, int modY, int direction) {
    public void setTarget(int modX, int modY, int direction) {
        if( moving == false ) {
            moving = true;
            setStatus( 1 ); // Moving Started
            setAnimate( true );
            cellX = cellX + modX;
            cellY = cellY + modY;

            setDirection( direction );
        }
    }

	protected void animationChanged(int oldStat, int oldDir,
									int status, int direction) {

        if( status == 1 ) {
		    setAnimationFrame(animation[direction]);
        } else {
            // System.out.println("Standing still["+Integer.toString(direction)+"]");
		    setAnimationFrame(standingstill[direction]);
        }
	}
}
