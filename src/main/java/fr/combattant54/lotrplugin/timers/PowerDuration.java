package fr.combattant54.lotrplugin.timers;

import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.TimerBase;

@Timer(key = TimerBase.POWER_DURATION,
        defaultValue = 4 * 60,
        meetUpValue = 3 * 60)
public class PowerDuration {
}
