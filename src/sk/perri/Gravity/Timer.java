package sk.perri.Gravity;

import org.bukkit.ChatColor;

public class Timer implements Runnable
{
    private int time;
    private GravityGame game;
    private int mode;

    public Timer(int time, GravityGame game, int mode)
    {
        this.time = time;
        this.game = game;
        this.mode = mode;
    }

    @Override
    public void run()
    {
        if(mode == 1)
            game.sidebar.replaceEntry(5, ChatColor.AQUA.toString()+ChatColor.BOLD+"Start in: "+time);
        else
            game.sidebar.replaceEntry(7, ChatColor.AQUA.toString()+ChatColor.BOLD+"End in: "+time);

        game.sidebar.update();
        time--;
        if(time <= 0)
        {
            if(mode == 1)
                game.gameStart();
            else
                game.gameEnd();
        }
    }
}
