package com.borgdude.paintball.objects.guns;

import com.borgdude.paintball.Main;
import com.borgdude.paintball.objects.Gun;
import com.borgdude.paintball.utils.MathUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class Rocket extends Gun {

    private Main plugin;

    public Rocket(Main p) {
        this.plugin = p;
    }

    @Override
    public ItemStack getLobbyItem() {
        ItemStack rocket = new ItemStack(Material.GREEN_WOOL);
        ItemMeta rocketM = rocket.getItemMeta();
        rocketM.setDisplayName(ChatColor.DARK_GREEN + "Rocket Launcher");
        rocket.setItemMeta(rocketM);
        return rocket;
    }

    @Override
    public ItemStack getInGameItem() {
        ItemStack is = new ItemStack(Material.IRON_SHOVEL);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.DARK_GREEN + "PaintBall Rocket Launcher");
        is.setItemMeta(im);
        return is;
    }

    @Override
    public int getCooldown() {
        return 15;
    }

    @Override
    public void fire(Player player) {

        Snowball snowball = null;
        Vector velocity = null;

        snowball = player.launchProjectile(Snowball.class); // set the snowball variable
        velocity = player.getLocation().getDirection().multiply(1.2);// set the velocity variable
        snowball.setVelocity(velocity);

        player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 2,
                0.5f);
    }

    @Override
    public void onHit(Player player, Snowball ball, Block block, Entity entity) {

        if (ball.hasMetadata("fired")) {
            player.getLocation().getWorld().playSound(ball.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 0.25f);
            return;
        }

        player.getLocation().getWorld().playSound(ball.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.25f);

        for (Entity e : ball.getNearbyEntities(12, 12, 12)) {
            if (e instanceof Player) {
                Player n = (Player) e;
                Vector p = n.getLocation().toVector();
                p.add(new Vector(0, 0.5, 0));
                Vector b = ball.getLocation().toVector();
                Vector t = p.subtract(b).normalize();
                double dis = n.getLocation().distance(ball.getLocation());

                t = t.multiply(MathUtil.tanh(dis));
                if (player.getUniqueId().equals(n.getUniqueId())) {
                    t = t.multiply(new Vector(0.5, 0.5, 0.5));
                    n.setVelocity(t);
                    n.setFallDistance(MathUtil.getMaxHeight(t));
                } else {
                    t = t.multiply(new Vector(0.3, 0.4, 0.3));
                    n.setVelocity(t);
                }

            }
        }

        Location spawnLocation = ball.getLocation();

        int numberOfBalls = 8;
        int deg = 360 / numberOfBalls;

        for (int i = 0; i < numberOfBalls; i++) {
            Snowball snowball = player.getWorld().spawn(spawnLocation, Snowball.class);
            double vecX = Math.cos(Math.toRadians(i * deg));
            double vecY = 1;
            double vecZ = Math.sin(Math.toRadians(i * deg));
            snowball.setVelocity(new Vector(vecX, vecY, vecZ).multiply(0.35D).add(MathUtil.getRandomVector(0.2f)));
            snowball.setShooter(player);
            snowball.setMetadata("fired", new FixedMetadataValue(plugin, Boolean.TRUE));
        }
    }

    @Override
    public String getName() {
        return "RocketLauncher";
    }
}
