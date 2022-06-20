package se.xtralarge.fisherfolk;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import org.bukkit.entity.Player;

public class WGManager {
    public static StateFlag FISHERFOLK_ENABLED;

    protected static Boolean RegisterFlags() {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            StateFlag fisherfolkFlag = new StateFlag("fisherfolk-enabled", false);
            registry.register(fisherfolkFlag);
            FISHERFOLK_ENABLED = fisherfolkFlag;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    protected static com.sk89q.worldguard.protection.flags.StateFlag.State getWorldGuardPermission(Player player,
            org.bukkit.Location entityLocation) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        return query.queryState(BukkitAdapter.adapt(entityLocation), localPlayer, WGManager.FISHERFOLK_ENABLED);
    }
}