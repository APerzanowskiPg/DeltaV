/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.orbitsgame;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.orbitsgame.GameMenuUI.LevelPlayerData;

/**
 *
 * @author adrian
 */
public class LevelBuilder {
    /**
     * Generuje dane poziomu na podstawie podanego identyfikatora
     * @param levelsGroup tabela, do której dodany zostanie element wyboru poziomu
     * @param levelId identyfikator poziomu
     * @return wygenerowane dane poziomu
     */
    static GameMenuUI.LevelPlayerData BuildLevel(Table levelsGroup, int levelId, GameMenuUI gameUI)
    {
        LevelSession.LevelDesc desc = new LevelSession.LevelDesc();
        LevelPlayerData ret;
        
        if(levelId == 1)
        {
            desc.name = "Manewr transferowy Hohmanna - część 1";
            desc.targetOrbit_e = 0.19150876998901367;
            desc.targetOrbit_inc = 0.9024035538022349;
            desc.targetOrbit_lan = 1.1003237837144855;
            desc.targetOrbit_aop = 2.36980806;
            desc.targetOrbit_a = 8400;//2.4038811440599437;
            /*desc.targetOrbit_e = 0.0004112;
            desc.targetOrbit_inc = 0.901357839;
            desc.targetOrbit_lan = 1.1015471;
            desc.targetOrbit_aop = 2.36980806;
            desc.targetOrbit_a = 10000;*/

            desc.startOrbit_e = 0.0004112;
            desc.startOrbit_inc = 0.901357839;
            desc.startOrbit_lan = 1.1015471;
            desc.startOrbit_aop = 2.36980806;
            desc.startOrbit_a = 6798;
            desc.start_timeSincePeriapsis = 0;

            // dry mass in spacecraft[tonnes]
            desc.spacecraft_dryMass = 2;
            // mass of fuel in spacecraft[tonnes]
            desc.spacecraft_fuelMass = 8;
            desc.spacecraft_startingFuelMass = 8;
            // engine specific impulse[s]
            desc.spacecraft_isp = 300;
            desc.spacecraft_maxMassFlowRatio = 2.9;

            desc.velocityErrorThreshold = 0.15;
            desc.posErrorThreshold = 300;
        }
        
        
        else if(levelId == 2)
        {
            desc.name = "Manewr transferowy Hohmanna - część 2";
            desc.targetOrbit_e = 0.0004112;
            desc.targetOrbit_inc = 0.901357839;
            desc.targetOrbit_lan = 1.1015471;
            desc.targetOrbit_aop = 2.36980806;
            desc.targetOrbit_a = 10000;

            
            desc.startOrbit_e = 0.19150876998901367;
            desc.startOrbit_inc = 0.9024035538022349;
            desc.startOrbit_lan = 1.1003237837144855;
            desc.startOrbit_aop = 2.36980806;
            desc.startOrbit_a = 8400;
            desc.start_timeSincePeriapsis = 0;

            // dry mass in spacecraft[tonnes]
            desc.spacecraft_dryMass = 2;
            // mass of fuel in spacecraft[tonnes]
            desc.spacecraft_fuelMass = 8;
            desc.spacecraft_startingFuelMass = 8;
            // engine specific impulse[s]
            desc.spacecraft_isp = 300;
            desc.spacecraft_maxMassFlowRatio = 2.9;

            desc.velocityErrorThreshold = 0.2;
            desc.posErrorThreshold = 300;
        }
        
        else if(levelId == 3)
        {
            desc.name = "Manewr transferowy Hohmanna";
            desc.targetOrbit_e = 0.0004112;
            desc.targetOrbit_inc = 0.901357839;
            desc.targetOrbit_lan = 1.1015471;
            desc.targetOrbit_aop = 2.36980806;
            desc.targetOrbit_a = 10000;

            desc.startOrbit_e = 0.0004112;
            desc.startOrbit_inc = 0.901357839;
            desc.startOrbit_lan = 1.1015471;
            desc.startOrbit_aop = 2.36980806;
            desc.startOrbit_a = 6798;
            desc.start_timeSincePeriapsis = 0;

            // dry mass in spacecraft[tonnes]
            desc.spacecraft_dryMass = 2;
            // mass of fuel in spacecraft[tonnes]
            desc.spacecraft_fuelMass = 8;
            desc.spacecraft_startingFuelMass = 8;
            // engine specific impulse[s]
            desc.spacecraft_isp = 300;
            desc.spacecraft_maxMassFlowRatio = 2.9;

            desc.velocityErrorThreshold = 0.15;
            desc.posErrorThreshold = 300;
        }
        
        else if(levelId == 4)
        {
            desc.name = "Manewr transferowy Hohmanna - odwrotnie";
            desc.startOrbit_e = 0.0004112;
            desc.startOrbit_inc = 0.901357839;
            desc.startOrbit_lan = 1.1015471;
            desc.startOrbit_aop = 2.36980806;
            desc.startOrbit_a = 10000;

            desc.targetOrbit_e = 0.0004112;
            desc.targetOrbit_inc = 0.901357839;
            desc.targetOrbit_lan = 1.1015471;
            desc.targetOrbit_aop = 2.36980806;
            desc.targetOrbit_a = 6798;
            desc.start_timeSincePeriapsis = 0;

            // dry mass in spacecraft[tonnes]
            desc.spacecraft_dryMass = 2;
            // mass of fuel in spacecraft[tonnes]
            desc.spacecraft_fuelMass = 8;
            desc.spacecraft_startingFuelMass = 8;
            // engine specific impulse[s]
            desc.spacecraft_isp = 300;
            desc.spacecraft_maxMassFlowRatio = 2.9;

            desc.velocityErrorThreshold = 0.15;
            desc.posErrorThreshold = 300;
        }
        
        else if(levelId == 5)
        {
            desc.name = "Zmiana inklinacji";
            desc.startOrbit_e = 0.0004112;
            desc.startOrbit_inc = 0.901357839;
            desc.startOrbit_lan = 1.1015471;
            desc.startOrbit_aop = 2.36980806;
            desc.startOrbit_a = 6798;

            desc.targetOrbit_e = 0.0004112;
            desc.targetOrbit_inc = 1.901357839;
            desc.targetOrbit_lan = 1.1015471;
            desc.targetOrbit_aop = 2.36980806;
            desc.targetOrbit_a = 6798;
            desc.start_timeSincePeriapsis = 0;

            // dry mass in spacecraft[tonnes]
            desc.spacecraft_dryMass = 2;
            // mass of fuel in spacecraft[tonnes]
            desc.spacecraft_fuelMass = 8;
            desc.spacecraft_startingFuelMass = 8;
            // engine specific impulse[s]
            desc.spacecraft_isp = 300;
            desc.spacecraft_maxMassFlowRatio = 2.9;

            desc.velocityErrorThreshold = 0.15;
            desc.posErrorThreshold = 300;
        }
         
        ret = gameUI.new LevelPlayerData(levelsGroup, desc);
        return ret;
    }
}
