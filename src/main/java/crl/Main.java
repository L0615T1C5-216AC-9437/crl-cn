package crl;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.plugin.Plugin;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Main extends Plugin {
    //Var
    public static JSONObject data = new JSONObject();
    public static Thread cycle;
    public static HashMap<String, Integer> list = new HashMap<>();
    Boolean enabled = false;

    ///Var
    //on start
    public Main() {
        if (!byteCode.hasDir("mind_db")) {
            byteCode.mkdir("mind_db");
        }
        if (!byteCode.has("crl")) {
            data = new JSONObject();
            data.put("commands", 4);
            data.put("timeFrame", 10);
            byteCode.make("crl", data);
        }
        data = byteCode.get("async");
        if (data == null) {
            Log.err("Invalid file - " + System.getProperty("user.home") + "/mind_db/crl.cn");
            Log.info("Reset file using command `crl-clear`");
            return;
        }
        enabled = true;
        cycle a = new cycle(Thread.currentThread());
        a.setDaemon(false);
        a.start();

        Events.on(EventType.WorldLoadEvent.class, event -> {
            if (enabled) {
                if (!cycle.isAlive()) {
                    cycle b = new cycle(Thread.currentThread());
                    b.setDaemon(false);
                    b.start();
                    Log.err("crl cycle crashed - attempting restart");
                }
            }
        });
        Events.on(EventType.PlayerJoin.class, event -> {
            list.put(event.player.uuid, 0);
        });
        Events.on(EventType.PlayerLeave.class, event -> {
            list.remove(event.player.uuid);
        });
        Events.on(EventType.PlayerChatEvent.class, event -> {
            if (!byteCode.has("crl")) {
                Log.err("File not found - " + System.getProperty("user.home") + "/mind_db/crl.cn");
                return;
            }
            data = byteCode.get("crl");
            if (event.message.startsWith("/")) {
                if (!list.containsKey(event.player.uuid)) list.put(event.player.uuid, 1);
                list.put(event.player.uuid, list.get(event.player.uuid)+1);
                if (list.get(event.player.uuid) > data.getInt("commands")) {
                    if (list.get(event.player.uuid) > data.getInt("commands")+2) {
                        event.player.con.kick("Spamming commands");
                    } else if (list.get(event.player.uuid) > data.getInt("commands")+1) {
                        event.player.sendMessage("[scarlet]Command rate exceeded! Stop using commands so fast");
                    } else if (list.get(event.player.uuid) > data.getInt("commands")) {
                        event.player.sendMessage("[yellow]Command rate exceeded! Stop using commands so fast");
                    }
                }
            }
        });
    }

    public void registerServerCommands(CommandHandler handler) {
        handler.register("crl-clear", "generates the default async.cn file", arg -> {
            data = new JSONObject();
            data.put("commands", 4);
            data.put("timeFrame", 10);
            if (byteCode.save("crl", data) != null) Log.info("Successfully created " + System.getProperty("user.home") + "/mind_db/crl.cn");
        });
    }
}