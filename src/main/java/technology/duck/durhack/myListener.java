package technology.duck.durhack;

import net.citizensnpcs.api.event.CitizensEnableEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.logging.Level;

public class myListener implements Listener {
    public Durhack durhackPlugin;

    public myListener(Durhack plugin) {
        durhackPlugin = plugin;
    }

    @EventHandler
    public void onCitizensAPIEnabled(CitizensEnableEvent event) {
        durhackPlugin.LOGGER.info("Citizen's API Loaded");


        if (durhackPlugin.ws.isOpen()) {
            durhackPlugin.ws.send("Ready for Stream");
        } else {
            durhackPlugin.LOGGER.log(Level.SEVERE, "Server not reachable!");
        }

        /*int counter = 0;
        durhackPlugin.spawnNPC(1, new Location(durhackPlugin.getServer().getWorlds().get(0), 2 * ++counter, -39, 1));
        durhackPlugin.spawnNPC(2, new Location(durhackPlugin.getServer().getWorlds().get(0), 2 * ++counter, -39, 1));
        durhackPlugin.spawnNPC(3, new Location(durhackPlugin.getServer().getWorlds().get(0), 2 * ++counter, -39, 1));
        durhackPlugin.spawnNPC(4, new Location(durhackPlugin.getServer().getWorlds().get(0), 2 * ++counter, -39, 1));*/
    }



    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equals("/stop")) {
            durhackPlugin.deleteNPCs();
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if (event.getCommand().equals("stop") || event.getCommand().equals("/stop")) {
            durhackPlugin.deleteNPCs();
        }
    }


}
