package fun.rega.RegaWheatFastGrow;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JavaPlugin {

    private static Main instance;
    private List<Cuboid> regionList;

    private Random random;
    private BukkitTask task;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.getCommand("regawheatfastgrow").setExecutor(new WFGCommand());

        this.random = new Random();
        this.regionList = new ArrayList();

        this.loadRegions();
    }

    public void loadRegions() {
        if(this.task != null && !this.task.isCancelled()) {
            this.task.cancel();
        }
        this.regionList.clear();

        World world = Bukkit.getWorld(this.getConfig().getString("world"));

        RegionManager rm = Main.getInstance().getWorldGuard().getRegionManager(world);
        for (String region : Main.getInstance().getConfig().getStringList("regions")) {
            ProtectedRegion rg = rm.getRegion(region);
            if(rg != null) {
                this.regionList.add(new Cuboid(world, rg.getMinimumPoint().getBlockX(), rg.getMinimumPoint().getBlockY(), rg.getMinimumPoint().getBlockZ(), rg.getMaximumPoint().getBlockX(), rg.getMaximumPoint().getBlockY(), rg.getMaximumPoint().getBlockZ()));
            }
        }
        this.startTask();
    }

    public void startTask() {
        task = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (Cuboid cuboid : regionList) {
                    cuboid.getBlocks().stream()
                            .filter(block -> block.getType() == Material.CROPS && block.getData() != 7 && random.nextInt(100) <= getConfig().getInt("grow-chance"))
                            .forEach(block -> block.setData((byte) (block.getData() + 1)));
                }

            }
        }, 20, this.getConfig().getInt("grow-cycle"));
    }

    @Override
    public void onDisable() {
        this.task.cancel();
    }

    public static Main getInstance() {
        return instance;
    }

    public WorldGuardPlugin getWorldGuard() {
        final Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

}
