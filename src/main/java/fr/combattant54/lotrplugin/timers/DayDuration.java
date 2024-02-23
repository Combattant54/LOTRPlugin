package fr.combattant54.lotrplugin.timers;

import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.TimerBase;

@Timer(key = TimerBase.DAY_DURATION,
        defaultValue = 5 * 60,
        meetUpValue = 3 * 60)
public class DayDuration {
}