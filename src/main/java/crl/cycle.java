package crl;

import arc.util.Log;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class cycle extends Thread {
    private Thread MainT;
    private JSONObject data = new JSONObject();

    public cycle(Thread main) {
        MainT = main;
    }

    public void run() {
        Log.info("crl cycle started - Waiting 60 Seconds");
        Main.cycle = Thread.currentThread();
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (Exception e) {
        }
        Log.info("crl cycle running");
        while (MainT.isAlive()) {
            data = byteCode.get("crl");
            //sleep
            if (data.has("timeFrame") && data.has("commands")) {
                try {
                    TimeUnit.SECONDS.sleep((long) data.getInt("timeFrame") / data.getInt("commands"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (!data.has("timeFrame")) byteCode.putInt("crl", "timeFrame", 5);
                if (!data.has("commands")) byteCode.putInt("crl", "commands", 3);
                try {
                    TimeUnit.SECONDS.sleep(10/4);
                } catch (Exception e) {
                }
            }
            ///sleep
            Main.list.forEach((k,v) -> {
                if (v > 0) {
                    Main.list.put(k, v-1);
                }
            });
        }
        Log.warn(">>> crl cycle stopped!");
    }
}
