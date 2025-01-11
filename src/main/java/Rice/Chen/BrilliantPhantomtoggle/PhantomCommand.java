package Rice.Chen.BrilliantPhantomtoggle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhantomCommand implements CommandExecutor, TabCompleter {
    private final BrilliantPhantomtoggle plugin;

    public PhantomCommand(BrilliantPhantomtoggle plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§7｜§6系統§7｜§f飯娘：§7此指令只能由玩家執行！");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage("§7｜§6系統§7｜§f飯娘：§7用法：/phantom toggle");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "toggle":
                boolean currentState = plugin.getPhantomSpawn(player);
                plugin.setPhantomSpawn(player, !currentState);
                String newState = !currentState ? "§a開啟" : "§c關閉";
                player.sendMessage("§7｜§6系統§7｜§f飯娘：§7個人夜魅生成已" + newState + "§f。");
                break;

            case "force":
                if (!player.hasPermission("phantomtoggle.force")) {
                    player.sendMessage("§7｜§6系統§7｜§f飯娘：§7您沒有權限使用此指令！");
                    return true;
                }
                plugin.forcePhantomSpawn(player);
                player.sendMessage("§7｜§6系統§7｜§f飯娘：§7已強制觸發夜魅生成條件！");
                break;

            case "info":
                if (!player.hasPermission("phantomtoggle.info")) {
                    player.sendMessage("§7｜§6系統§7｜§f飯娘：§7您沒有權限使用此指令！");
                    return true;
                }

                Player targetPlayer = player;
                if (args.length > 1) {
                    targetPlayer = plugin.getServer().getPlayer(args[1]);
                    if (targetPlayer == null) {
                        player.sendMessage("§7｜§6系統§7｜§f飯娘：§7找不到該玩家！");
                        return true;
                    }
                }

                boolean phantomState = plugin.getPhantomSpawn(targetPlayer);
                int restTime = targetPlayer.getStatistic(org.bukkit.Statistic.TIME_SINCE_REST);
                double days = restTime / 24000.0;

                player.sendMessage("§7｜§6系統§7｜§f飯娘：§7目標玩家資訊：");
                player.sendMessage("§7｜§6系統§7｜§f飯娘：§7玩家：§f" + targetPlayer.getName());
                player.sendMessage("§7｜§6系統§7｜§f飯娘：§7生成：" + (phantomState ? "§a開啟" : "§c關閉"));
                player.sendMessage(String.format("§7｜§6系統§7｜§f飯娘：§7睡眠：§f%.2f 天 §7（§f%d ticks§7）", days, restTime));
                break;

            default:
                sender.sendMessage("§7｜§6系統§7｜§f飯娘：§7用法：/phantom toggle");
                break;
        }
        
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("toggle");
            if (sender.hasPermission("phantomtoggle.force")) {
                completions.add("force");
            }
            if (sender.hasPermission("phantomtoggle.info")) {
                completions.add("info");
            }
            return completions;
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("info") && 
            sender.hasPermission("phantomtoggle.info")) {
            List<String> completions = new ArrayList<>();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return completions;
        }
        
        return Collections.emptyList();
    }
}