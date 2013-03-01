/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

import java.util.ArrayList;

/**
 *
 * @author cbuntain
 */
public class GameList {
    private static ArrayList<Game> GAME_LIST = new ArrayList<Game>();
    
    public static void addGame(Game g) {
        GAME_LIST.add(g);
    }
    
    public static ArrayList<Game> getGames() {
        return GAME_LIST;
    }
}
