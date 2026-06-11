package com.garbagemule.MobArena;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.WaveParser;
import com.garbagemule.MobArena.waves.enums.WaveBranch;
import com.garbagemule.MobArena.Msg;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WaveTitleTest {

    private Arena createArenaProxy(List<String> calls) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                String name = method.getName();
                if ("configName".equals(name)) {
                    return "testarena";
                }
                if ("announce".equals(name)) {
                    if (args != null && args.length == 1) {
                        calls.add("announce:" + args[0]);
                        return null;
                    }
                    if (args != null && args.length == 2) {
                        Object first = args[0];
                        Object second = args[1];
                        String message;
                        if (first instanceof Msg && second instanceof String) {
                            message = ((Msg) first).format((String) second);
                        } else {
                            message = String.valueOf(first) + ":" + String.valueOf(second);
                        }
                        calls.add("announce:" + message);
                        return null;
                    }
                }
                if (method.getReturnType() == boolean.class) {
                    return false;
                }
                if (method.getReturnType() == int.class) {
                    return 0;
                }
                if (method.getReturnType() == long.class) {
                    return 0L;
                }
                return null;
            }
        };

        return (Arena) Proxy.newProxyInstance(
            Arena.class.getClassLoader(),
            new Class[] { Arena.class },
            handler
        );
    }

    @Test
    public void parseWaveTitleFromConfig() {
        Arena arena = createArenaProxy(new ArrayList<>());

        YamlConfiguration config = new YamlConfiguration();
        config.set("type", "special");
        config.set("wave", 5);
        config.set("title", "&6Dawn of Death");
        config.createSection("monsters").set("zombie", 10);

        Wave wave = WaveParser.parseWave(arena, "special1", config, WaveBranch.SINGLE);

        assertNotNull(wave);
        assertEquals("§6Dawn of Death", wave.getTitle());
    }

    @Test
    public void announceWaveTitleAfterWaveStartMessage() {
        List<String> calls = new ArrayList<>();
        Arena arena = createArenaProxy(calls);

        YamlConfiguration config = new YamlConfiguration();
        config.set("type", "special");
        config.set("wave", 5);
        config.set("title", "&6Dawn of Death");
        config.createSection("monsters").set("zombie", 10);

        Wave wave = WaveParser.parseWave(arena, "special1", config, WaveBranch.SINGLE);
        wave.announce(arena, 5);

        assertEquals(2, calls.size());
        assertEquals("announce:" + Msg.WAVE_SPECIAL.format("5"), calls.get(0));
        assertEquals("announce:§6Dawn of Death", calls.get(1));
    }
}
