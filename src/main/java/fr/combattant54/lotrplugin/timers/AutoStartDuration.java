package fr.combattant54.lotrplugin.timers;

import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.TimerBase;

@Timer(key = TimerBase.AUTO_RESTART_DURATION,
        defaultValue = 60,
        meetUpValue = 60)
public class AutoStartDuration {
}
