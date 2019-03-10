package ir.sadeghpro.splash.utils;

import ir.sadeghpro.splash.cnst.Flags;
import ir.sadeghpro.splash.model.ConfigSplash;

public class ValidationUtil {

    public static int hasPath(ConfigSplash cs) {
        if (cs.getPathSplash().isEmpty())
            return Flags.WITH_LOGO;
        else
            return Flags.WITH_PATH;
    }
}
