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
import java.util.stream.Collectors;

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
            sender.sendMessage("§7｜§6系統§7｜§f飯娘：§7用法：/phantom toggle|info");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "toggle":
                boolean currentState = plugin.getPhantomSpawn(player);
                plugin.setPhantomSpawn(player, !currentState, currentState);
                String newState = !currentState ? "§a開啟" : "§c關閉";
                player.sendMessage("§7｜§6系統§7｜§f飯娘：§7個人夜魅生成已" + newState + "§7。");
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
                if (args.length > 1) {
                    if (!player.hasPermission("phantomtoggle.info")) {
                        player.sendMessage("§7｜§6系統§7｜§f飯娘：§7您沒有權限查看其他玩家的資訊！");
                        return true;
                    }
                    
                    Player targetPlayer = plugin.getServer().getPlayer(args[1]);
                    if (targetPlayer == null) {
                        player.sendMessage("§7｜§6系統§7｜§f飯娘：§7找不到該玩家！");
                        return true;
                    }
                    showPlayerInfo(player, targetPlayer);
                } else {
                    showPlayerInfo(player, player);
                }
                break;

            default:
                sender.sendMessage("§7｜§6系統§7｜§f飯娘：§7用法：/phantom toggle|info");
                break;
        }
        
        return true;
    }

    private void showPlayerInfo(Player viewer, Player target) {
        boolean phantomState = plugin.getPhantomSpawn(target);
        int restTime = target.getStatistic(org.bukkit.Statistic.TIME_SINCE_REST);
        double days = restTime / 24000.0;

        viewer.sendMessage("§7｜§6系統§7｜§f飯娘：§7夜魅生成控制資訊：");
        viewer.sendMessage("§7｜§6系統§7｜§f飯娘：§7玩家：§f" + target.getName());
        viewer.sendMessage("§7｜§6系統§7｜§f飯娘：§7生成：" + (phantomState ? "§a開啟" : "§c關閉"));
        viewer.sendMessage(String.format("§7｜§6系統§7｜§f飯娘：§7睡眠：§f%.2f 天 §7（§f%d ticks§7）", days, restTime));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("toggle");
            completions.add("info");
            if (sender.hasPermission("phantomtoggle.force")) {
                completions.add("force");
            }
            return filterCompletions(completions, args[0]);
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("info") && 
            sender.hasPermission("phantomtoggle.info")) {
            return filterCompletions(
                plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()),
                args[1]
            );
        }
        
        return Collections.emptyList();
    }

    private List<String> filterCompletions(List<String> completions, String partial) {
        String lowercasePartial = partial.toLowerCase();
        return completions.stream()
            .filter(str -> str.toLowerCase().startsWith(lowercasePartial))
            .collect(Collectors.toList());
    }
}