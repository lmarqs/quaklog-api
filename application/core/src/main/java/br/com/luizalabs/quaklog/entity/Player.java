package br.com.luizalabs.quaklog.entity;

import br.com.luizalabs.quaklog.entity.vo.ConnectStatus;
import br.com.luizalabs.quaklog.entity.vo.GameTime;
import br.com.luizalabs.quaklog.entity.vo.Mod;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ToString
@EqualsAndHashCode
@Getter
public class Player {

    @FunctionalInterface
    interface PlayerKillListener {
        void kill(Player player);
    }

    private final Integer id;
    private final AtomicInteger kills;
    private final List<KillHistory> kdHistory;
    private final List<Item> items;
    private ConnectStatus connectStatus;
    private PlayerKillListener killListener;

    public Player(Integer id) {
        this.id = id;
        this.kills = new AtomicInteger();
        this.kdHistory = new ArrayList<>();
        this.items = new ArrayList<>();
        this.connectStatus = ConnectStatus.CONNECTED;
    }

    public void setKillListener(PlayerKillListener killListener) {
        this.killListener = killListener;
    }

    public void setConnectStatus(ConnectStatus connectStatus) {
        this.connectStatus = connectStatus;
    }

    public List<KillHistory> getKdHistory() {
        return Collections.unmodifiableList(kdHistory);
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void deadByWorld(GameTime gameTime, WorldPlayer world, Mod mod) {
        kdHistory.add(KillHistory.deadBy(gameTime, world, mod));
    }

    public void kill(GameTime gameTime, Player client, Mod mod) {
        if (killListener != null) killListener.kill(client);
        client.deadBy(gameTime, this, mod);
        kdHistory.add(KillHistory.killed(gameTime, client, mod));
        kills.incrementAndGet();
    }

    private void deadBy(GameTime gameTime, Player player, Mod mod) {
        kdHistory.add(KillHistory.deadBy(gameTime, player, mod));
        if (isWorld(player)) {
            kills.decrementAndGet();
        }
    }

    private boolean isWorld(Player player) {
        return player.id.equals(WorldPlayer.WORLD_ID);
    }

}