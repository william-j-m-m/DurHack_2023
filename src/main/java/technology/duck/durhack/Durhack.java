package technology.duck.durhack;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.logging.Logger;

public final class Durhack extends JavaPlugin {
    public Logger LOGGER;
    public MyWebSocketClient ws;

    public HashMap<Integer, NPC> npcHashMap = new HashMap<>();

    public Location cameraPosition;
    public ArmorStand cameraAS;

    @Override
    public void onEnable() {
        cameraPosition = new Location(
                getServer().getWorld("world"),
                10,
                -35.5,
                1.1,
                0,
                0
        );

        // Plugin startup logic
        LOGGER = getLogger();

        URI wsUri = null;
        try {
            //wsUri = new URI("ws://localhost:8001/");
            wsUri = new URI("ws://192.168.8.115:8001/");
            //wsUri = new URI("ws://19.168.8.115:8001/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        ws = new MyWebSocketClient(wsUri, this);
        ws.connect();
        ws.setConnectionLostTimeout(0);

        getServer().getPluginManager().registerEvents(new myListener(this), this);

        getCommand("durhack").setExecutor(new CommandDurhack(this));

        /*cameraAS = (ArmorStand) getServer().getWorld("world").spawnEntity(cameraPosition, EntityType.ARMOR_STAND);
        cameraAS.setGravity(false);
        cameraAS.getEquipment().setHelmet(new ItemStack(Material.PIGLIN_HEAD, 1));*/


        LOGGER.info("Durcraft Virtual Attendees Plugin Loaded!");
    }

    public void spawnNPC(int id, Location location) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Durhacker");

        LOGGER.info(String.format("spawning npc %s", npc.getId()));
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent("DurHack_Citizen",
                "WitglcJeStFogO31cjqTL93DqC+f9ft1Qc/yg4rOnSYS69INsSkCjrzjphcObCB7v24WE8ljuyRPI4AXoiqVebu7SFUMtd9LumAtspfYydLQLFCa8OtTFZzne7l0fHAv+6+DU2gPxirxbtG4epOjTzRe1sshs0rhlhtldv6zvqGXTBbiNCyQ/4gw2/KtW2ebSUaZRH+PkDuJ5n0z5/gYkflMvem8CfThqpUCqoviMDBcxOIz7lrQUu96+YbfEqxqy2MnpiPLVfWh/8unO6JmZ84fSUX6ltzfnLoqVvO10hfGkltormBGen3fcy7oZD35lFGlmFIa4rg6RonsCNg5lLdefM7nXebOELD1oC/wHvXDjNdeyFYRlX0OenL0RoH5BaeSkHvBCtIVndJO/P+5d83YZpOi6h1n8/sdo/mgLqHmZWBk9pVXDtILBwYQWClOOxEySaR3W/rzP7WO0deYLjFrobxy0M7VqICpFumWE5iBdcrUih4YYSkR81je52dY3NLD8rSLUQO06dISKx9Ghw2gfWTcmirdHSSLO1dC8zp+iypA2NGzramYms46V3/TbRW09Yr4cp4oR1hm/WZyEXsc2ogF5qxzZVRWfIeyaFuednbnsDcYIUkw/YkZZhwso66RvPD5f3q3wA1Sz84M+D5BbJK5xYdw5m+4libSVb4=",
                "ewogICJ0aW1lc3RhbXAiIDogMTY5OTE1MTQzODAzNCwKICAicHJvZmlsZUlkIiA6ICI2ZjczZjZkYTRkZGI0MmY2YjNhOGM4ZmYwYjFmMzAyMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJCb3J5dGUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZiYjk2YjE3OTQxOGFkMDQwYTlhODNmODg3MmVkZGI5MThmZGI2YTkxY2RiOGU4YmVlNjY3ZjVhNDBiMDY1MyIKICAgIH0KICB9Cn0=");


        npc.spawn(location);
        npcHashMap.put(id, npc);
    }

    public void deleteNPC(int id) {
        npcHashMap.get(id).destroy();
        npcHashMap.remove(id);
        LOGGER.info(String.format("deleting npc %s", id));
    }

    public void deleteNPCs() {
        for (var entry : npcHashMap.entrySet()) {
            entry.getValue().destroy();
        }
        npcHashMap.clear();
    }

    /*@Override
    public void onDisable() {
        //getServer().getWorld("world").
        cameraAS.setHealth(0);
    }*/
}
