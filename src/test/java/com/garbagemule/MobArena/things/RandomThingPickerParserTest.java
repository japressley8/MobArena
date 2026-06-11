package com.garbagemule.MobArena.things;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasItems;

public class RandomThingPickerParserTest {

    private RandomThingPickerParser subject;
    private ThingPickerParser parser;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        parser = new ThingPickerParser() {
            @Override
            public ThingPicker parse(String s) {
                return null;
            }
        };
        subject = new RandomThingPickerParser(parser, new Random());
    }

    @Test
    public void returnsNullIfRandomIsMissing() {
        String input = "(a, b, c)";

        ThingPicker result = subject.parse(input);

        assertThat(result, nullValue());
    }

    @Test
    public void returnsNullIfParenthesesAreMissing() {
        String input = "random[a, b, c]";

        ThingPicker result = subject.parse(input);

        assertThat(result, nullValue());
    }

    @Test
    public void returnsNullIfNotRandom() {
        String input = "all(a, b, c)";

        ThingPicker result = subject.parse(input);

        assertThat(result, nullValue());
    }

    @Test
    public void invokesUnderlyingParserForEachItem() {
        String input = "random(a, b, c)";
        int[] count = new int[1];
        parser = new ThingPickerParser() {
            @Override
            public ThingPicker parse(String s) {
                if (s.equals("a") || s.equals("b") || s.equals("c")) {
                    count[0]++;
                }
                return new ThingPicker() {
                    @Override
                    public Thing pick() {
                        return null;
                    }
                };
            }
        };
        subject = new RandomThingPickerParser(parser, new Random());

        subject.parse(input);

        assertThat(count[0], is(3));
    }

    @Test
    public void returnsRandomThingPickerMultipleThings() {
        String input = "random(a, b)";

        parser = new ThingPickerParser() {
            @Override
            public ThingPicker parse(String s) {
                return new ThingPicker() {
                    @Override
                    public Thing pick() {
                        return null;
                    }
                };
            }
        };
        subject = new RandomThingPickerParser(parser, new Random());

        ThingPicker result = subject.parse(input);

        assertThat(result, instanceOf(RandomThingPicker.class));
    }

    @Test
    public void returnsOnlyPickerInsteadOfWrapping() {
        String input = "random(a)";
        ThingPicker picker = new ThingPicker() {
            @Override
            public Thing pick() {
                return null;
            }
        };
        parser = new ThingPickerParser() {
            @Override
            public ThingPicker parse(String s) {
                return s.equals("a") ? picker : null;
            }
        };
        subject = new RandomThingPickerParser(parser, new Random());

        ThingPicker result = subject.parse(input);

        assertThat(result, is(picker));
    }

    @Test
    public void returnsDistinctRandomPickerWhenCountIsSpecified() {
        String input = "random(a, b, c):2";
        Thing thingA = new Thing() {
            @Override
            public boolean giveTo(org.bukkit.entity.Player p) {
                return false;
            }

            @Override
            public boolean takeFrom(org.bukkit.entity.Player player) {
                return false;
            }

            @Override
            public boolean heldBy(org.bukkit.entity.Player player) {
                return false;
            }
        };
        Thing thingB = new Thing() {
            @Override
            public boolean giveTo(org.bukkit.entity.Player p) {
                return false;
            }

            @Override
            public boolean takeFrom(org.bukkit.entity.Player player) {
                return false;
            }

            @Override
            public boolean heldBy(org.bukkit.entity.Player player) {
                return false;
            }
        };
        Thing thingC = new Thing() {
            @Override
            public boolean giveTo(org.bukkit.entity.Player p) {
                return false;
            }

            @Override
            public boolean takeFrom(org.bukkit.entity.Player player) {
                return false;
            }

            @Override
            public boolean heldBy(org.bukkit.entity.Player player) {
                return false;
            }
        };
        ThingPicker pickerA = new ThingPicker() {
            @Override
            public Thing pick() {
                return thingA;
            }
        };
        ThingPicker pickerB = new ThingPicker() {
            @Override
            public Thing pick() {
                return thingB;
            }
        };
        ThingPicker pickerC = new ThingPicker() {
            @Override
            public Thing pick() {
                return thingC;
            }
        };

        parser = new ThingPickerParser() {
            @Override
            public ThingPicker parse(String s) {
                switch (s) {
                    case "a": return pickerA;
                    case "b": return pickerB;
                    case "c": return pickerC;
                    default: return null;
                }
            }
        };
        subject = new RandomThingPickerParser(parser, new Random(0L));

        ThingPicker result = subject.parse(input);

        assertThat(result, instanceOf(DistinctRandomThingPicker.class));

        List<Thing> picks = result.pickMany(2);
        assertThat(picks.size(), is(2));
        assertThat(picks.get(0), not(sameInstance(picks.get(1))));
        assertThat(picks, anyOf(hasItems(thingA, thingB), hasItems(thingA, thingC), hasItems(thingB, thingC)));
    }

    @Test
    public void distinctRandomPickerReturnsConfiguredCountForDefaultPickMany() {
        String input = "random(a, b, c):3";
        Thing thingA = new Thing() {
            @Override
            public boolean giveTo(org.bukkit.entity.Player p) {
                return false;
            }

            @Override
            public boolean takeFrom(org.bukkit.entity.Player player) {
                return false;
            }

            @Override
            public boolean heldBy(org.bukkit.entity.Player player) {
                return false;
            }
        };
        Thing thingB = new Thing() {
            @Override
            public boolean giveTo(org.bukkit.entity.Player p) {
                return false;
            }

            @Override
            public boolean takeFrom(org.bukkit.entity.Player player) {
                return false;
            }

            @Override
            public boolean heldBy(org.bukkit.entity.Player player) {
                return false;
            }
        };
        Thing thingC = new Thing() {
            @Override
            public boolean giveTo(org.bukkit.entity.Player p) {
                return false;
            }

            @Override
            public boolean takeFrom(org.bukkit.entity.Player player) {
                return false;
            }

            @Override
            public boolean heldBy(org.bukkit.entity.Player player) {
                return false;
            }
        };
        ThingPicker pickerA = new ThingPicker() {
            @Override
            public Thing pick() {
                return thingA;
            }
        };
        ThingPicker pickerB = new ThingPicker() {
            @Override
            public Thing pick() {
                return thingB;
            }
        };
        ThingPicker pickerC = new ThingPicker() {
            @Override
            public Thing pick() {
                return thingC;
            }
        };

        parser = new ThingPickerParser() {
            @Override
            public ThingPicker parse(String s) {
                switch (s) {
                    case "a": return pickerA;
                    case "b": return pickerB;
                    case "c": return pickerC;
                    default: return null;
                }
            }
        };
        subject = new RandomThingPickerParser(parser, new Random(0L));

        ThingPicker result = subject.parse(input);

        assertThat(result, instanceOf(DistinctRandomThingPicker.class));

        List<Thing> picks = result.pickMany();
        assertThat(picks.size(), is(3));
        assertThat(picks, hasItems(thingA, thingB, thingC));
    }

    @Test
    public void throwsIfCountExceedsOptions() {
        String input = "random(a, b):3";
        parser = new ThingPickerParser() {
            @Override
            public ThingPicker parse(String s) {
                return new ThingPicker() {
                    @Override
                    public Thing pick() {
                        return null;
                    }
                };
            }
        };
        subject = new RandomThingPickerParser(parser, new Random());

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Cannot pick 3 distinct items from 2 options");

        subject.parse(input);
    }

    @Test
    public void throwsIfZeroThings() {
        String input = "random()";
        exception.expect(IllegalArgumentException.class);

        subject.parse(input);
    }

}
