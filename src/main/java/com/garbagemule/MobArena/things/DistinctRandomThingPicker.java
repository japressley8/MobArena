package com.garbagemule.MobArena.things;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DistinctRandomThingPicker implements ThingPicker {

    private final List<ThingPicker> pickers;
    private final Random random;
    private final int count;

    public DistinctRandomThingPicker(List<ThingPicker> pickers, Random random, int count) {
        if (pickers == null || pickers.isEmpty()) {
            throw new IllegalArgumentException("No pickers available for distinct random selection");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("Distinct random count must be greater than zero");
        }
        if (count > pickers.size()) {
            throw new IllegalArgumentException("Cannot select " + count + " distinct items from " + pickers.size() + " options");
        }

        this.pickers = new ArrayList<>(pickers);
        this.random = random;
        this.count = count;
    }

    @Override
    public Thing pick() {
        return pickMany(1).get(0);
    }

    @Override
    public List<Thing> pickMany() {
        return pickMany(count);
    }

    @Override
    public List<Thing> pickMany(int count) {
        if (count != this.count) {
            throw new IllegalArgumentException("Distinct random selector was configured for " + this.count + " items, but requested " + count);
        }
        List<ThingPicker> copy = new ArrayList<>(pickers);
        Collections.shuffle(copy, random);
        return copy.stream()
            .limit(this.count)
            .map(ThingPicker::pick)
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        String list = pickers.stream()
            .map(ThingPicker::toString)
            .collect(Collectors.joining(" or "));
        return "random(" + list + "):" + count;
    }

}
