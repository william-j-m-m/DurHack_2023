package technology.duck.durhack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;

public class CommandDurhack implements CommandExecutor {
    private final Durhack durhackPlugin;



    public CommandDurhack(Durhack javaPlugin) {
        durhackPlugin = javaPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args[0]) {
            case "playerlook":
                return playerLook(sender, args);

            case "test":
                return armorStandLook(sender, args);

            case "connect":
                return connect(sender);

            case "camerapitch":
                return changeCameraPitch(sender, args);

            case "spawnplayer":
                return spawnPlayer(sender, args);
        }

        return true;
    }

    int counter = 0;

    private boolean spawnPlayer(CommandSender sender, String[] args) {
        Vector2d spawnAt = new Vector2d(Double.parseDouble(args[1]), Double.parseDouble(args[2]));
        Vector3d worldDirection = durhackPlugin.ws.ConvertScreenToDirection(spawnAt);

        Location loc = durhackPlugin.cameraPosition.clone();

        float yaw = (float) Math.toDegrees(Math.atan(worldDirection.x / worldDirection.z));
        loc.setYaw(yaw);

        float pitch = (float) Math.toDegrees(-Math.atan(worldDirection.y / worldDirection.z));
        loc.setPitch(pitch);

        //((Player) sender).teleport(loc);

        BlockIterator blockIterator = new BlockIterator(loc, 0, 30);

        Block hitblock = null;
        for (BlockIterator it = blockIterator; it.hasNext(); ) {
            Block block = it.next();
            sender.sendMessage(String.format("Looking at the block %s: %s, %s, %s", block.getType(), block.getX(), block.getY(), block.getZ()));
            if (block.getType() == Material.AIR) continue;

            hitblock = block;
            break;
        }

        if (hitblock == null) {
            sender.sendMessage("Could not find a block to spawn on");

            return false;
        }

        Location blockLoc = hitblock.getLocation();
        blockLoc.setY(blockLoc.getY() + 1);

        sender.sendMessage(String.format("Spawned an NPC at position: %s, %s, %s", blockLoc.getX(), blockLoc.getY(), blockLoc.getZ()));
        durhackPlugin.spawnNPC(counter++, blockLoc);

        return true;
    }

    private boolean changeCameraPitch(CommandSender sender, String[] args) {
        sender.sendMessage(String.format("changing camera pitch to %s", args[1]));
        durhackPlugin.ws.CameraPitchAngle = Double.parseDouble(args[1]);
        return true;
    }

    private boolean playerLook(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage("You need to add 2 double parameters");
            return false;
        }
        if (sender instanceof Player player) {
            Vector3d worldDirection = durhackPlugin.ws.ConvertScreenToDirection(new Vector2d(Double.parseDouble(args[1]), Double.parseDouble(args[2])));
            durhackPlugin.LOGGER.info(String.format("the direction should be: (%s, %s, %s)", worldDirection.x, worldDirection.y, worldDirection.z));

            //Location loc = player.getLocation();
            Location loc = durhackPlugin.cameraPosition.clone();

            float yaw = (float) Math.toDegrees(Math.atan(worldDirection.x / worldDirection.z));
            player.sendMessage(String.format("ur trying to set the yaw to %s", yaw));
            loc.setYaw(yaw);

            float pitch = (float) Math.toDegrees(-Math.atan(worldDirection.y / worldDirection.z));
            //float pitch = (float) Math.toDegrees(-Math.asin(worldDirection.y));
            player.sendMessage(String.format("ur trying to set the pitch to %s", pitch));
            loc.setPitch(pitch);

            player.teleport(loc);

            return true;

            /*BlockIterator blockIterator = new BlockIterator(player.getEyeLocation(), 0, 40);

            for (BlockIterator it = blockIterator; it.hasNext(); ) {
                Block block = it.next();

                if (block.getType() == Material.AIR) continue;

                block.setType(Material.GLOWSTONE);
                break;
            }*/

        }

        sender.sendMessage("You need to be a player to run this command");
        return false;
    }

    private boolean armorStandLook(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage("You need to add 2 double parameters");
            return false;
        }

        Vector3d worldDirection = durhackPlugin.ws.ConvertScreenToDirection(new Vector2d(Double.parseDouble(args[1]), Double.parseDouble(args[2])));
        durhackPlugin.LOGGER.info(String.format("the direction should be: (%s, %s, %s)", worldDirection.x, worldDirection.y, worldDirection.z));

        Location loc = durhackPlugin.cameraPosition.clone();

        float yaw = (float) Math.toDegrees(Math.atan(worldDirection.x / worldDirection.z));
        sender.sendMessage(String.format("ur trying to set the yaw to %s", yaw));
        loc.setYaw(yaw);

        float pitch = (float) Math.toDegrees(-Math.atan(worldDirection.y / worldDirection.z));
        //float pitch = (float) Math.toDegrees(-Math.asin(worldDirection.y));
        sender.sendMessage(String.format("ur trying to set the pitch to %s", pitch));
        loc.setPitch(pitch);

        durhackPlugin.cameraAS.teleport(loc);

        return true;
    }

    private boolean connect(CommandSender sender) {
        durhackPlugin.ws.reconnect();
        //durhackPlugin.ws.connect();

        sender.sendMessage("attempting reconnection");

        return true;
    }
}
