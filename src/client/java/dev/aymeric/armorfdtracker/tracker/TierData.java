package dev.aymeric.armorfdtracker.tracker;

public class TierData {

    public static final long[] KILLS = {
        100, 200, 300, 500, 800, 1200, 1750, 2500, 3500, 5000,
        10000, 25000, 50000, 75000, 100000, 125000, 150000, 200000
    };

    public static final int[] DEFENSE = {
        20, 40, 60, 90, 120, 150, 180, 210, 240, 270,
        310, 335, 355, 370, 380, 390, 395, 400
    };

    /** Défense actuelle selon les kills. */
    public static int getCurrentDefense(long kills) {
        int def = 0;
        for (int i = 0; i < KILLS.length; i++) {
            if (kills >= KILLS[i]) def = DEFENSE[i];
            else break;
        }
        return def;
    }

    /** Kills requis pour le prochain palier, -1 si palier max atteint. */
    public static long getNextTierKills(long kills) {
        for (long t : KILLS) {
            if (kills < t) return t;
        }
        return -1;
    }

    /** Défense du prochain palier, -1 si palier max atteint. */
    public static int getNextTierDefense(long kills) {
        for (int i = 0; i < KILLS.length; i++) {
            if (kills < KILLS[i]) return DEFENSE[i];
        }
        return -1;
    }
}
