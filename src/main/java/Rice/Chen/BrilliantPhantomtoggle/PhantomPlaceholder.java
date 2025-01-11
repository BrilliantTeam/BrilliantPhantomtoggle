package Rice.Chen.BrilliantPhantomtoggle;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PhantomPlaceholder extends PlaceholderExpansion {
    private final BrilliantPhantomtoggle plugin;

    public PhantomPlaceholder(BrilliantPhantomtoggle plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "phantomtoggle";
    }

    @Override
    public @NotNull String getAuthor() {
        return "YourName";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";
        
        if (identifier.equals("state")) {
            return plugin.getPhantomSpawn(player) ? "是" : "否";
        }
        
        return null;
    }
}