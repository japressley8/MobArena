package com.garbagemule.MobArena.things;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomThingPickerParser implements ThingPickerParser {

    private final ThingPickerParser parser;
    private final Random random;

    public RandomThingPickerParser(
        ThingPickerParser parser,
        Random random
    ) {
        this.parser = parser;
        this.random = random;
    }

    @Override
    public ThingPicker parse(String s) {
        String trimmed = s.trim();
        if (!trimmed.startsWith("random(")) {
            return null;
        }

        int closing = findMatchingClosingParen(trimmed, 0);
        if (closing < 0) {
            return null;
        }

        String tail = trimmed.substring(closing + 1).trim();
        int count = 1;
        if (!tail.isEmpty()) {
            if (!tail.startsWith(":")) {
                throw new IllegalArgumentException("Invalid random count syntax: " + s);
            }
            String countText = tail.substring(1).trim();
            if (countText.isEmpty()) {
                throw new IllegalArgumentException("Missing count after random selector: " + s);
            }
            try {
                count = Integer.parseInt(countText);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid random count for " + s + ": " + countText);
            }
            if (count <= 0) {
                throw new IllegalArgumentException("Random count must be greater than zero: " + s);
            }
        }

        String randomExpression = trimmed.substring(0, closing + 1);
        String inner = ParserUtil.extractBetween(randomExpression, '(', ')');
        List<ThingPicker> pickers = ParserUtil.split(inner)
            .stream()
            .map(String::trim)
            .map(parser::parse)
            .collect(Collectors.toList());

        if (pickers.isEmpty()) {
            throw new IllegalArgumentException("Nothing to pick from: " + s);
        }
        if (pickers.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Invalid item in random selector: " + s);
        }
        if (count == 1) {
            if (pickers.size() == 1) {
                return pickers.get(0);
            }
            return new RandomThingPicker(pickers, random);
        }
        if (count > pickers.size()) {
            throw new IllegalArgumentException("Cannot pick " + count + " distinct items from " + pickers.size() + " options in " + s);
        }
        return new DistinctRandomThingPicker(pickers, random, count);
    }

    private int findMatchingClosingParen(String s, int start) {
        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

}
