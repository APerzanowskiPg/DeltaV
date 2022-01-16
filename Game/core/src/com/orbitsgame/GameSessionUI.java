/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.orbitsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 *
 * @author adrian
 */
public class GameSessionUI {
    private LevelSession level;
    
    private Stage stage;
    private Table root;
    
    private VisWindow timeControlWnd;
    private Cell<Button>[] timeWarpButtons;
    // menu buttons
    private VisWindow menuWnd;
    private Cell<VisTextButton> completeButton;
    private Cell<VisTextButton> restartButton;
    private Cell<VisTextButton> goToMenuButton;
    
    private int currentWarpIndex;
    final private int[] timeWarpMultipliers = {1, 2, 3, 4, 5, 10, 50, 100, 1000, 10000, 100000};
    //UI elements
    private Table bottomUIPanel;
    private Table bottomUIPanelLeft;
    private Table bottomUIPanelRight;
    
    private Table thrusterBarColumn;
    private VisProgressBar thrustBar;
    
    private Table fuelBarColumn;
    private VisProgressBar fuelBar;
    
    // text indicators
    // left panel
    private Table spacecraftIndicatorsGroup;
    private VisLabel totalMassIndicator;
    private VisLabel fuelMassIndicator;
    private VisLabel accelerationIndicator;
    private VisLabel velocityIndicator;
    private VisLabel deltaVIndicator;
    
    // right panel
    private Table orbitElementsGroup;
    private VisLabel orbitVelocityErrorIndicator;
    private VisLabel orbitPositionErrorIndicator;
    
    //assets
    static TextureRegionDrawable timeWarpButtonOffDrawable;
    static TextureRegionDrawable timeWarpButtonOnDrawable;
    static TextureRegionDrawable bottomNavUIDrawable;
    
    DeltaVGame game;
    
    static void Init()
    {
        Texture timeWarpButtonOffTex = new Texture(Gdx.files.internal("time_warp_button_off.png"));
        Texture timeWarpButtonOnTex = new Texture(Gdx.files.internal("time_warp_button_on.png"));
        Texture bottomNavUITex = new Texture(Gdx.files.internal("bottomNavUI.png"));
        
        timeWarpButtonOffDrawable = new TextureRegionDrawable(timeWarpButtonOffTex);
        timeWarpButtonOnDrawable = new TextureRegionDrawable(timeWarpButtonOnTex);
        bottomNavUIDrawable = new TextureRegionDrawable(bottomNavUITex);
    }
    
    GameSessionUI(LevelSession launchingSession, DeltaVGame game)
    {
        this.game = game;
        level = launchingSession;
        
        stage = new Stage(new ScreenViewport());
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        timeControlWnd = new VisWindow("Przyspieszenie czasu");
        timeControlWnd.setSize(230, 60);
        timeControlWnd.setPosition(0, Gdx.graphics.getHeight());
        timeControlWnd.setMovable(false);
        
        
        currentWarpIndex = 0;
        //timeWarpButtons = new Button[11];
        timeWarpButtons = new Cell[11];
        for(int i=0; i<11; ++i)
        {
            timeWarpButtons[i] = timeControlWnd.add(new Button(timeWarpButtonOffDrawable));
            timeWarpButtons[i].getActor().addListener(new TimeWrapButtonEventListener());
            
        }
        UpdateWarpDisplay();
        
        stage.addActor(timeControlWnd);
        
        //menu
        menuWnd = new VisWindow("Opcje");
        menuWnd.setSize(180, 80);
        menuWnd.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        menuWnd.setMovable(false);
        
        completeButton = menuWnd.add(new VisTextButton("Zakończ")).height(60);
        completeButton.getActor().setDisabled(true);
        completeButton.getActor().addListener(new MenuButtonsListener());
        restartButton = menuWnd.add(new VisTextButton("Restart")).height(60);
        restartButton.getActor().addListener(new MenuButtonsListener());
        goToMenuButton = menuWnd.add(new VisTextButton("Wyjdź")).height(60);
        goToMenuButton.getActor().addListener(new MenuButtonsListener());
        
        stage.addActor(menuWnd);
        
        bottomUIPanel = new Table();
        bottomUIPanel.setBackground(bottomNavUIDrawable);
        bottomUIPanel.setPosition(0, 0);
        bottomUIPanel.setSize(1280, 212);
        bottomUIPanelLeft = new Table();
        bottomUIPanelLeft.setPosition(0, 0);
        bottomUIPanelLeft.setSize(530, 102);
        bottomUIPanelLeft.align(Align.right | Align.bottom);
        bottomUIPanel.addActor(bottomUIPanelLeft);
        bottomUIPanelRight = new Table();
        bottomUIPanelRight.setPosition(Gdx.graphics.getWidth()-530, 0);
        bottomUIPanelRight.setSize(530, 102);
        bottomUIPanel.addActor(bottomUIPanelRight);
        
        
        spacecraftIndicatorsGroup = new Table();
        totalMassIndicator = new VisLabel("Masa całkowita[t]: 0");
        totalMassIndicator.setAlignment(Align.left);
        fuelMassIndicator = new VisLabel("Masa paliwa[t]: 0");
        fuelMassIndicator.setAlignment(Align.left);
        accelerationIndicator = new VisLabel("Przyspieszenie[m/s^2]: 0");
        accelerationIndicator.setAlignment(Align.left);
        velocityIndicator = new VisLabel("Prędkość[km/s]: 0");
        velocityIndicator.setAlignment(Align.left);
        deltaVIndicator = new VisLabel("DeltaV[km/s]: 0");
        deltaVIndicator.setAlignment(Align.left);
        spacecraftIndicatorsGroup.add(totalMassIndicator).left();
        spacecraftIndicatorsGroup.row();
        spacecraftIndicatorsGroup.add(fuelMassIndicator).left();
        spacecraftIndicatorsGroup.row();
        spacecraftIndicatorsGroup.add(accelerationIndicator).left();
        spacecraftIndicatorsGroup.row();
        spacecraftIndicatorsGroup.add(velocityIndicator).left();
        spacecraftIndicatorsGroup.add(deltaVIndicator).left();
        bottomUIPanelLeft.add(spacecraftIndicatorsGroup).fillX().expandX();
        
        
        fuelBarColumn = new Table();
        fuelBarColumn.align(Align.top);
        fuelBar = new VisProgressBar(0,100, 0.1f, true);
        fuelBar.setValue(100);
        fuelBarColumn.add(fuelBar).height(Value.percentHeight(0.5f));
        fuelBarColumn.row();
        fuelBarColumn.add(new VisLabel("paliwo")).expand().fill().left();
        //thrustBar.
        bottomUIPanelLeft.add(fuelBarColumn).padLeft(10);
        
        thrusterBarColumn = new Table();
        thrusterBarColumn.align(Align.top);
        thrustBar = new VisProgressBar(0,100, 0.1f, true);
        thrusterBarColumn.add(thrustBar).height(Value.percentHeight(0.5f));
        thrusterBarColumn.row();
        thrusterBarColumn.add(new VisLabel("Ciąg")).expand().fill();
        //thrustBar.
        bottomUIPanelLeft.add(thrusterBarColumn).padLeft(10);
        
        orbitElementsGroup = new Table();
        orbitVelocityErrorIndicator = new VisLabel("Błąd prędkości[km/s]: 0");
        orbitVelocityErrorIndicator.setAlignment(Align.left);
        orbitElementsGroup.add(orbitVelocityErrorIndicator).left();
        orbitElementsGroup.row();
        orbitPositionErrorIndicator = new VisLabel("Błąd położenia[km]: 0");
        orbitPositionErrorIndicator.setAlignment(Align.left);
        orbitElementsGroup.add(orbitPositionErrorIndicator).left();
        orbitElementsGroup.row();
        bottomUIPanelRight.add(orbitElementsGroup).fillX().expandX();
        
        
        stage.addActor(bottomUIPanel);
        
        
        InputMultiplexer inputMultiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
            if (!inputMultiplexer.getProcessors().contains(stage, true))
                inputMultiplexer.addProcessor(stage);
    }
    
    void UpdateWarpDisplay()
    {
        for(int i=0; i<11; ++i)
        {
            if(i<=currentWarpIndex)
            {
                (timeWarpButtons[i].getActor()).getStyle().up = timeWarpButtonOnDrawable;//setBackground(timeWarpButtonOnDrawable);
            }
            else
            {
                timeWarpButtons[i].getActor().getStyle().up = timeWarpButtonOffDrawable;//.setBackground(timeWarpButtonOffDrawable);
            }
        }
        timeControlWnd.getTitleLabel().setText("Przyspieszenie czasu: x" + timeWarpMultipliers[currentWarpIndex]);
    }
    
    void Render()
    {
        String velVal;
        String posVal;
        float velError = level.GetVelocityError();
        float posError = level.GetPosError();
        
        boolean underThresh = false;
        if(velError <= level.desc.velocityErrorThreshold)
        {
            underThresh = true;
            velVal = String.format("%.2f", velError) + " < " + String.format("%.2f", level.desc.velocityErrorThreshold);
            orbitVelocityErrorIndicator.setColor(Color.GREEN);
        }
        else
        {
            underThresh = false;
            velVal = String.format("%.2f", velError) + " > " + String.format("%.2f", level.desc.velocityErrorThreshold);
            orbitVelocityErrorIndicator.setColor(Color.WHITE);
        }
        
        
        if(posError <= level.desc.posErrorThreshold)
        {
            posVal = String.format("%.2f", posError) + " < " + String.format("%.2f", level.desc.posErrorThreshold);
            orbitPositionErrorIndicator.setColor(Color.GREEN);
        }
        else
        {
            underThresh = false;
            posVal = String.format("%.2f", posError) + " > " + String.format("%.2f", level.desc.posErrorThreshold);
            orbitPositionErrorIndicator.setColor(Color.WHITE);
        }
        
        
        orbitVelocityErrorIndicator.setText("Błąd prędkości[km/s]: " + velVal);
        orbitPositionErrorIndicator.setText("Błąd położenia[km]: " + posVal);
        
        completeButton.getActor().setDisabled(!underThresh);
        
        stage.draw();
        stage.act();
    }
    
    void Dispose()
    {
        InputMultiplexer inputMultiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
        inputMultiplexer.removeProcessor(stage);
    }
    
    //event handlers
    class TimeWrapButtonEventListener extends ClickListener
    {
        @Override
        public void clicked(InputEvent event, float x, float y) 
        {
            for(int i=0; i<11; ++i)
            {
                if(event.getTarget() == timeWarpButtons[i].getActor())
                {
                    currentWarpIndex = i;
                    break;
                }
            }
            UpdateWarpDisplay();
        }
    }
    
    class MenuButtonsListener extends ClickListener
    {
        @Override
        public void clicked(InputEvent event, float x, float y) 
        {
            VisTextButton button = (VisTextButton)(event.getTarget().getParent());
            if(button == goToMenuButton.getActor())
            {
                game.playingLevel = false;
                game.level = null;
                game.gameMenu.GiveControl();
            }
            else if(button == restartButton.getActor())
            {
                game.level = new LevelSession(level.levelPlayerData.desc, level.levelPlayerData);
            }
            else if(button == completeButton.getActor())
            {
                if(!completeButton.getActor().isDisabled())
                {
                    level.levelPlayerData.playerCompleted = true;
                    level.levelPlayerData.playerClosesPos = Math.min(level.GetPosError(), level.levelPlayerData.playerClosesPos);
                    level.levelPlayerData.playerClosesVel = Math.min(level.GetVelocityError(), level.levelPlayerData.playerClosesVel);
                    level.levelPlayerData.UnlockNext();
                    game.gameMenu.GiveControl();
                    game.playingLevel = false;
                    game.level = null;
                }
            }
        }
    }
    
    
    
    //getters
    int GetTimeMultiplier()
    {
        return timeWarpMultipliers[currentWarpIndex];
    }
    
    //setters
    void SetThrustLevel(float thrust)
    {
        thrustBar.setValue(thrust*100.0f);
    }
    
    void SetTotalMass(float massInTonnes)
    {
        totalMassIndicator.setText("Masa całkowita[t]: " + String.format("%.2f", massInTonnes));
    }
    
    void SetFuelMass(float massInTonnes, float startingMass)
    {
        fuelMassIndicator.setText("Masa paliwa[t]: " + String.format("%.2f", massInTonnes));
        fuelBar.setValue(100.f*massInTonnes/startingMass);
    }
    
    // @acceleration - acceleration in m/s^2
    void SetAcceleration(float acceleration)
    {
        accelerationIndicator.setText("Przyspieszenie[m/s^2]: " + String.format("%.2f", acceleration));
    }
    
    // @velocity - velocity in km/s
    void SetVelocity(float velocity)
    {
        velocityIndicator.setText("Prędkość[km/s]: " + String.format("%.2f", velocity));
    }
    
    // @deltaV - deltaV in km/s
    void SetDeltaV(float deltaV)
    {
        deltaVIndicator.setText("DelatV[km/s]: " + String.format("%.2f", deltaV));
    }
}
