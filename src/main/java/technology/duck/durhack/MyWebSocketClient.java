package technology.duck.durhack;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;

import java.net.URI;
import java.util.*;

public class MyWebSocketClient extends WebSocketClient {
    private final Durhack durhackPlugin;
    private Vector2i ScreenResolution = new Vector2i(1920, 1080);
    private float cameraFocalLength = 24;
    public double CameraPitchAngle = 62;

    public Vector3d ConvertScreenToDirection(Vector2d screenSpaceCoords) {
        Vector3d worldDirection = new Vector3d();

        double zoom = 2.5;

        double centre_x = ScreenResolution.x / 2.0;
        double centre_y = ScreenResolution.y / 2.0;

        worldDirection.x = screenSpaceCoords.x - centre_x;
        worldDirection.y = ScreenResolution.y - (screenSpaceCoords.y - centre_y);
        worldDirection.z = cameraFocalLength;


        worldDirection.x = worldDirection.x / (16 * zoom);
        worldDirection.y = worldDirection.y / (9 * zoom);

        worldDirection.y -= 80;

        /*durhackPlugin.LOGGER.info(String.format("CameraPitchAngle: %s", CameraPitchAngle));
        durhackPlugin.LOGGER.info(String.format("Vector before: %s", worldDirection));
        worldDirection.rotateX(Math.toRadians(CameraPitchAngle));
        durhackPlugin.LOGGER.info(String.format("Vector after: %s", worldDirection));*/

        return worldDirection.normalize();
    }

    public MyWebSocketClient(URI serverUri, Durhack javaPlugin) {
        super(serverUri);
        durhackPlugin = javaPlugin;
    }

    @Override
    public void onOpen(ServerHandshake handShakeData) {
        durhackPlugin.LOGGER.info("connection opened");

        if (isReconnecting) {
            durhackPlugin.ws.send("Ready for Stream");
        }

        isReconnecting = false;

    }



    @Override
    public void onMessage(String message) {
        String[] npcsStringData = message.split(";");

        List<Integer> idsSeen = new ArrayList<>();

        if (!Objects.equals(npcsStringData[0], "")) {
            for (String npcStringData : npcsStringData) {
                String[] splitString = npcStringData.split(",");
                int id = Integer.parseInt(splitString[0]);
                int x = Integer.parseInt(splitString[1]);
                int y = -39;
                int z = Integer.parseInt(splitString[2]);
                idsSeen.add(id);

                durhackPlugin.LOGGER.info(String.format("coordinates received for id %s: (%s, %s, %s)", id, x, y, z));

                double x1 = 16.0 - x*0.012;
                double z1 = 12.5 - z*0.012;

                Location loc = new Location(durhackPlugin.getServer().getWorld("world"), x1, y, z1);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (durhackPlugin.npcHashMap.containsKey(id)) {
                            // NPC already exists
                            NPC npc = durhackPlugin.npcHashMap.get(id);

                            npc.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);

                        } else {
                            // NPC does not exist yet
                            durhackPlugin.spawnNPC(id, loc);
                        }
                    }
                }.runTask(durhackPlugin);

            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                List<Integer> idsToDelete = new ArrayList<>();
                for (var entry : durhackPlugin.npcHashMap.entrySet()) {
                    // PROBLEM CHILD
                    if (!idsSeen.contains(entry.getKey())) {
                        idsToDelete.add(entry.getKey());
                    }
                }
                for (var id : idsToDelete) {
                    durhackPlugin.deleteNPC(id);
                }
            }
        }.runTask(durhackPlugin);
    }

    boolean isReconnecting = false;

    @Override
    public void onClose(int code, String reason, boolean remote) {
        isReconnecting = true;
        durhackPlugin.LOGGER.info("connection closed");
        durhackPlugin.LOGGER.info(String.valueOf(code));
        durhackPlugin.LOGGER.info(reason);
        durhackPlugin.LOGGER.info(String.valueOf(remote));
        durhackPlugin.ws.close();
        durhackPlugin.ws.connect();
    }

    @Override
    public void onError(Exception ex) {
        durhackPlugin.LOGGER.info("IT'S ALL FUCKED");
        //durhackPlugin.LOGGER.info(Arrays.toString(ex.getStackTrace()));
        ex.printStackTrace();
    }
}
