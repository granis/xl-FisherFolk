package se.xtralarge.fisherfolk;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public class Main extends JavaPlugin implements Listener {
  Boolean WGRegisteredOK = false;

  @Override
  public void onEnable() {
    final PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(this, this);
    // LootTable fishing = LootTables.FISHING_TREASURE.getLootTable();
    // net.minecraft.world.level.storage.loot.LootTable annan =
    // (net.minecraft.world.level.storage.loot.LootTable) fishing;

  }

  public void onLoad() {
    try {
      Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
      getLogger().info("Found WorldGuard 7+");
      WGRegisteredOK = WGManager.RegisterFlags();
      getLogger().info("WgRegistered:" + WGRegisteredOK.toString());
    } catch (Exception ex) {
      getLogger().info("Couldnt load WorldGuard");
    }
  }

  @Override
  public void onDisable() {
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerFish(final PlayerFishEvent e) {
    if (e.getPlayer() == null || e.isCancelled() || e.getState() != State.BITE) {
      return;
    }

    ItemStack inHand = e.getPlayer().getInventory().getItemInMainHand();
    if (inHand.getType() != Material.FISHING_ROD) {
      return;
    }

    boolean worldguardStateAllow = false;
    if (this.WGRegisteredOK) {
      com.sk89q.worldguard.protection.flags.StateFlag.State allowedRegion = WGManager.getWorldGuardPermission(
          e.getPlayer(),
          e.getHook().getLocation());
      if (allowedRegion == null || allowedRegion == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
        return;
      }
      worldguardStateAllow = true;
    }

    // should not override worldguards stateflag.DENY, so we check it afterwards
    boolean allowedPlayer = e.getPlayer().hasPermission("fisherfolk.auto");

    // if the player is not allowed, and the regionflag does not allow, theres no
    // permissions for this :(
    if (!allowedPlayer) {
      if (!worldguardStateAllow) {
        return;
      }
    }

    ServerLevel nmsWorld = ((CraftWorld) e.getPlayer().getWorld()).getHandle();
    net.minecraft.world.item.ItemStack inHandNMS = CraftItemStack.asNMSCopy(inHand);

    Random rand = new Random();
    long waitTicks = 10L + rand.nextInt(20);
    BukkitScheduler scheduler = getServer().getScheduler();
    JavaPlugin Main = this;
    ServerPlayer sPlayer = ((CraftPlayer) e.getPlayer()).getHandle();
    InteractionHand hand = ((CraftPlayer) e.getPlayer()).getHandle().getUsedItemHand();

    scheduler.scheduleSyncDelayedTask(this, new Runnable() {
      @Override
      public void run() {
        // catch
        inHandNMS.use(nmsWorld, sPlayer, hand);

        scheduler.scheduleSyncDelayedTask(Main, new Runnable() {
          @Override
          public void run() {
            // throw
            inHandNMS.use(nmsWorld, sPlayer, hand);
          }
        }, waitTicks);
      }
    }, waitTicks);
  }

}
