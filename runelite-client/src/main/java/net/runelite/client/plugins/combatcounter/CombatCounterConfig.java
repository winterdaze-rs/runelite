package net.runelite.client.plugins.combatcounter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("combatcounter")
public interface CombatCounterConfig extends Config {

    @ConfigItem(
            keyName = "Show Tick Counter",
            name = "Show Tick Counter",
            description = "Turn the tick counter on",
            position = 1
    )
    default boolean showTickCounter()
    {
        return false;
    }

    @ConfigItem(
            keyName = "Show Damage Counter",
            name = "Show Damage Counter",
            description = "Turn the damage counter on",
            position = 2
    )
    default boolean showDamageCounter()
    {
        return false;
    }


    @ConfigItem(
            keyName = "Reset on New Instance",
            name = "Reset On New Instance",
            description = "Resets counter when entering a new instance",
            position = 3
    )
    default boolean resetOnNewInstance()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Reset on World Hop",
            name = "Reset On World Hop",
            description = "Resets counter when world hopping",
            position = 4
    )
    default boolean resetOnWorldHop()
    {
        return false;
    }


}
