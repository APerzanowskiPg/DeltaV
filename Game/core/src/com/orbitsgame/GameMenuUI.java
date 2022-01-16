/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.orbitsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;


/**
 *
 * @author adrian
 */
public class GameMenuUI {
    private DeltaVGame game;
    private Stage stage;
    private Table root;
    
    // main UI
    private VisWindow mainMenuWnd;
    private Table mainMenuButtonsGroup;
    private Cell<VisTextButton> playButton;
    private Cell<VisTextButton> creditsButton;
    private Cell<VisTextButton> exitButton;
    
    
    // level choice UI
    private VisWindow levelsWnd;
    private Table levelsGroup;
    private Table levelsOuterGroup;
    private VisScrollPane levelsScrollPane;
    private VisTextButton goLevelBackButton;
    private LevelPlayerData[] levelsList;
    
    static void Init()
    {
        float x = 1;
    }
    
    GameMenuUI(DeltaVGame game)
    {
        this.game = game;
        
        
        
        stage = new Stage(new ScreenViewport());
        root = new Table();
        Gdx.input.setInputProcessor(stage);
        //InputMultiplexer inputMultiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
        //    if (!inputMultiplexer.getProcessors().contains(stage, true))
        //        inputMultiplexer.addProcessor(stage);
        
        root.setFillParent(true);
        stage.addActor(root);
        
        mainMenuWnd = new VisWindow("Menu gry");
        mainMenuWnd.setSize(400, 400);
        mainMenuWnd.setPosition(0.5f*Gdx.graphics.getWidth()- 200, 0.5f*Gdx.graphics.getHeight() - 200);
        mainMenuWnd.setMovable(false);
        stage.addActor(mainMenuWnd);
        
        mainMenuButtonsGroup = new Table();
        playButton =  mainMenuButtonsGroup.add(new VisTextButton("Rozpocznij grę"));
        playButton.padBottom(30).width(250).height(60);
        mainMenuButtonsGroup.row();
        creditsButton =  mainMenuButtonsGroup.add(new VisTextButton("Autor i podziękowania"));
        creditsButton.padBottom(30).width(250).height(60);
        mainMenuButtonsGroup.row();
        exitButton =  mainMenuButtonsGroup.add(new VisTextButton("Wyjdź"));
        exitButton.padBottom(30).width(250).height(60);
        mainMenuButtonsGroup.row();
        
        playButton.getActor().addListener(new MainMenuButtonEventListener());
        creditsButton.getActor().addListener(new MainMenuButtonEventListener());
        exitButton.getActor().addListener(new MainMenuButtonEventListener());
        //exitButton.getActor().getL
        mainMenuWnd.add(mainMenuButtonsGroup);
        
        
        
        
        /// levels window
        levelsWnd = new VisWindow("Wybór poziomu");
        levelsWnd.setSize(500, 600);
        levelsWnd.setPosition(0.5f*Gdx.graphics.getWidth()- 250, 0.5f*Gdx.graphics.getHeight() - 300);
        levelsWnd.setMovable(false);
        levelsWnd.setVisible(false);
        stage.addActor(levelsWnd);
        
        //levelsOuterGroup = new Table();
        levelsGroup = new Table();
        levelsScrollPane = new VisScrollPane(levelsGroup);
        levelsScrollPane.setSize(490, 550);
        
        goLevelBackButton = new VisTextButton("<< Powrót");
        levelsGroup.add(goLevelBackButton).padBottom(10);
        levelsGroup.row();
        goLevelBackButton.addListener(new LevelsMenuListener());
        
        //levelsScrollPane.set
        //levelsOuterGroup.add(levelsScrollPane).width(490).height(550).expandX().expandY();
        //levelsOuterGroup.row();
        levelsWnd.addActor(levelsScrollPane);
        
        //LevelSession.LevelDesc desc1 = new LevelSession.LevelDesc();
        //LevelPlayerData level1Data = new LevelPlayerData(levelsGroup, desc1);
        levelsList = new LevelPlayerData[]{
            LevelBuilder.BuildLevel(levelsGroup, 1, this), 
            LevelBuilder.BuildLevel(levelsGroup, 2, this), 
            LevelBuilder.BuildLevel(levelsGroup, 3, this)
        };
        levelsList[0].available = true;
    }
    
    void Render()
    {
        stage.draw();
        for(int i=0; i<levelsList.length; ++i)
        {
            levelsList[i].Act();
        }
        stage.act();
    }
    
    void GiveControl()
    {
        Gdx.input.setInputProcessor(stage);
    }
    
    class MainMenuButtonEventListener extends ClickListener
    {
        @Override
        public void clicked(InputEvent event, float x, float y) 
        {
            VisTextButton button = (VisTextButton)(event.getTarget().getParent());
            if(button == exitButton.getActor())
            {
                Gdx.app.exit();
                System.exit(0);
            }
            else if(button == playButton.getActor())
            {
                levelsWnd.setVisible(true);
                mainMenuWnd.setVisible(false);
            }
        }
    }
    
    
    
    
    
    
    class LevelPlayerData
    {
        LevelSession.LevelDesc desc;
        
        boolean playerCompleted = false;
        float playerClosesPos = 100000;
        float playerClosesVel = 100000;
        float playerShortestTime = 100000;
        float playerSmallestDeltaV = 100000;
        
        boolean available = false;
        //UI
        Table frame;
        VisLabel progressLabel;
        VisTextButton playButton;
        
        LevelPlayerData(Table levelsGroup, LevelSession.LevelDesc desc)
        {
            this.desc = desc;
            Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
            bgPixmap.setColor(Color.DARK_GRAY);
            bgPixmap.fill();
            TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
            
            frame = new Table();
            
            frame.setBackground(textureRegionDrawableBg);
            //frame.set
            Cell<Table> cell = levelsGroup.add(frame);
            cell.padBottom(20).width(450).height(100);
            levelsGroup.row();
            
            frame.add(new VisLabel(desc.name));
            frame.row();
            progressLabel = new VisLabel(GetProgressDesc());
            frame.add(progressLabel);
            frame.row();
            playButton = new VisTextButton("Zagraj");
            playButton.setDisabled(!available);
            playButton.addListener(new PlayLevelListener());
            frame.add(playButton);
            
        }
        
        /**
         * odblokowuje kolejny poziom
         */
        void UnlockNext()
        {
            int i = 0;
            for(i=0; i<levelsList.length; ++i)
            {
                if(levelsList[i] == this)
                {
                    if(i+1<levelsList.length)
                    {
                        levelsList[i+1].available = true;
                    }
                    return;
                }
            }
        }
        
        /**
         * działania wykonywane co klatkę
         */
        void Act()
        {
            progressLabel.setText(GetProgressDesc());
            playButton.setDisabled(!available);
            
        }
        
        String GetProgressDesc()
        {
            if(!available)
            {
                return "Poziom niedostępny";
            }
            else if(!playerCompleted)
            {
                return "Poziom nieukończony";
            }
            else
            {
                return "Najlepsze wyniki: błąd położenia=" + String.format("%.1f", playerClosesPos)
                        + "km;\n błąd prędkosci=" + String.format("%.2f", playerClosesVel);
                        //+ "km/s; czas=" + String.format("%.1f", playerShortestTime) +
                        //"s; deltaV=" + String.format("%.1f", playerSmallestDeltaV);
            }
        }
        
        class PlayLevelListener extends ClickListener
        {
            @Override
            public void clicked(InputEvent event, float x, float y) 
            {
                if(event.getTarget().getParent() == playButton)
                {
                    if(!playButton.isDisabled())
                    {
                        game.level = new LevelSession(desc, LevelPlayerData.this);
                        game.playingLevel = true;
                    }
                }
            }
        }
        
    }
    
    class LevelsMenuListener extends ClickListener
    {
        @Override
        public void clicked(InputEvent event, float x, float y) 
        {
            if(event.getTarget().getParent() == goLevelBackButton)
            {
                levelsWnd.setVisible(false);
                mainMenuWnd.setVisible(true);
            }
        }
    }
}
