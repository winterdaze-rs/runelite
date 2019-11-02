/*
 * THIS SOFTWARE WRITTEN BY A KEYBOARD-WIELDING MONKEY BOI
 * No rights reserved. Use, redistribute, and modify at your own discretion,
 * and in accordance with Yagex and RuneLite guidelines.
 * However, aforementioned monkey would prefer if you don't sell this plugin for profit.
 * Good luck on your raids!
 */

package net.runelite.client.plugins.theatre;

import net.runelite.client.config.*;
import net.runelite.client.plugins.theatre.rooms.nylocas.NyloPredictor;
import net.runelite.client.config.Stub;

import java.awt.*;
import java.awt.event.KeyEvent;

@ConfigGroup("Theatre")

public interface TheatreConfig extends Config {

    enum NYLOCAS {
        NONE,
        MAGE,
        MELEE,
        RANGER
    }

    enum NYLOOPTION {
        NONE,
        TILE,
        TIMER
    }
    @ConfigItem(
            position = 0,
            keyName = "maidenStub",
            name = "Maiden",
            description = "" //stubs don't show descriptions when hovered over
    )
    default Stub maidenStub()
    {
        return new Stub();
    }
    @ConfigItem(
            position = 1,
            keyName = "showMaidenBloodToss",
            name = "Show Maiden Blood Toss",
            description = "Displays the tile location where tossed blood will land."

    )
    default boolean showMaidenBloodToss() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "showMaidenBloodSpawns",
            name = "Show Maiden Blood Spawns",
            description = "Show the tiles that blood spawns will travel to."
    )
    default boolean showMaidenBloodSpawns() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "bloatStub",
            name = "Bloat",
            description = "" //stubs don't show descriptions when hovered over
    )
    default Stub bloatStub()
    {
        return new Stub();
    }
    @ConfigItem(
            position = 4,
            keyName = "showBloatIndicator",
            name = "Show Bloat Status",
            description = "Displays Bloat's status (asleep, wake, and enrage) using color code."
    )
    default boolean showBloatIndicator() {
        return true;
    }

    @ConfigItem(
            position = 5,
            keyName = "showBloatHands",
            name = "Show Bloat Hands",
            description = "Highlights the falling hands inside Bloat."
    )
    default boolean showBloatHands() {
        return true;
    }

    @ConfigItem(
            position = 6,
            keyName = "bloatColor",
            name = "Hands Color",
            description = "Bloat Hands Color"
    )
    default Color bloatColor() {
        return Color.CYAN;
    }

    @ConfigItem(
            position = 7,
            keyName = "bloatFeet",
            name = "Bloat Hands Rave Edition",
            description = "Party. P A R T Y."
    )
    default boolean BloatFeetIndicatorRaveEdition() { return false; }

    @ConfigItem(
            position = 8,
            keyName = "showBloatTimer",
            name = "Show Bloat Timer",
            description = "Show the estimated time when Bloat will go down."
    )
    default boolean showBloatTimer(){ return false; }

    @ConfigItem(
            position = 9,
            keyName = "nylocasStub",
            name = "Nylocas",
            description = "" //stubs don't show descriptions when hovered over
    )
    default Stub nylocasStub()
    {
        return new Stub();
    }
    @ConfigItem(
            position = 10,
            keyName = "showNyloPillarHealth",
            name = "Show Nylocas Pillar Health",
            description = "Show the health bars of the Nylocas pillars."
    )
    default boolean showNyloPillarHealth() {
        return true;
    }

    @ConfigItem(
            position = 11,
            keyName = "showNylocasExplosions",
            name = "Highlight Old Nylocas",
            description = "Either a timer on the nylo counting down to explosion, or a tile underneath."
    )
    default NYLOOPTION showNylocasExplosions() { return NYLOOPTION.NONE; }

    @ConfigItem(
            position = 12,
            keyName = "showNylocasAmount",
            name = "Show Nylocas Amount",
            description = "An overlay will appear that counts the amount of Nylocas in the room."
    )
    default boolean showNylocasAmount() {
        return true;
    }

    /**
    @ConfigItem(
            position = 8,
            keyName = "showNylocasSpawns",
            name = "Show Nylocas Pre-spawns",
            description = "Know the contents of the next upcoming wave."
    )
    default boolean showNylocasSpawns() {
        return true;
    }

    @ConfigItem(
            position = 9,
            keyName = "highlightNyloRoles",
            name = "Highlight Nylo Prespawns",
            description = "Highlights the next upcoming wave based on role. FOR BEGINNERS"
    )
    default NYLOCAS highlightNyloRoles() {
        return NYLOCAS.NONE;
    }**/

    /**
    @ConfigItem(
            position = 10,
            keyName = "highlightNyloParents",
            name = "Show Nylo Parents (Un-used)",
            description = "Highlight the Nylocas that spawn outside the center."
    )
    default boolean highlightNyloParents() {
        return true;
    }**/

    @ConfigItem(
            position = 13,
            keyName = "highlightNyloAgros",
            name = "Show Nylocas Agros",
            description = "Highlight the Nylocas that are aggressive to the player."
    )
    default boolean highlightNyloAgros() {
        return true;
    }

    @ConfigItem(
            position = 14,
            keyName = "sotetsegStub",
            name = "Sotetseg",
            description = "" //stubs don't show descriptions when hovered over
    )
    default Stub sotetsegStub()
    {
        return new Stub();
    }

    @ConfigItem(
            position = 15,
            keyName = "showSotetsegAutoAttacks",
            name = "Show Sotetseg Orb Attacks",
            description = "Highlight the attacks which Sotetseg throws at you."
    )
    default boolean showSotetsegAutoAttacks() {
        return true;
    }

    @ConfigItem(
            position = 16,
            keyName = "showSotetsegAoEAttacks",
            name = "Show Sotetseg AoE",
            description = "Highlight the big AoE dragonball-z deathball."
    )
    default boolean showSotetsegAoE() {
        return true;
    }

    @ConfigItem(
            position = 17,
            keyName = "showSotetsegMaze",
            name = "Mark Sotetseg Maze",
            description = "Marks the tiles of Sotetseg's maze while in the overworld."
    )
    default boolean showSotetsegMaze() {
        return true;
    }
    /**
    @ConfigItem(
            position = 14,
            keyName = "showSotetsegMazeAuto",
            name = "Auto Mark Sotetseg Maze",
            description = "Marks the tiles of Sotetseg's maze instantly while in the overworld."
    )
    default boolean showSotetsegMazeAuto() {
        return true;
    }
    /**
    @ConfigItem(
            position = 14,
            keyName = "showSotetsegSolo",
            name = "Mark Sotetseg Maze (Solo)",
            description = "Marks the tiles of Sotetseg's maze while in the underworld."
    )
    default boolean showSotetsegSolo() {
        return true;
    }**/
    @ConfigItem(
            position = 18,
            keyName = "sotetsegMazeDiscord",
            name = "Sotetseg maze send discord",
            description = ""
    )
    default boolean sotetsegMazeDiscord(){ return false; }

    @ConfigItem(
            position = 19,
            keyName = "SotetsegAttacksSounds",
            name = "Sotetseg big AOE sound",
            description = ""
    )
    default boolean sotetsetAttacksSound() { return false; }

    @Range(max = 100)
    @ConfigItem(
            position = 20,
            keyName = "SotetsegAttacksSoundsVolume",
            name = "Sotetseg big AOE sound volume",
            description = ""
    )
    default int sotetsetAttacksSoundVolume() { return 80; }


    @ConfigItem(
            position = 21,
            keyName = "markerColor",
            name = "Sotey Color Party",
            description = "Configures the color of marked tile for discord party pings"
    )
    default Color mazeTileColourParty()
    {
        return Color.WHITE;
    }
    @ConfigItem(
            position = 22,
            keyName = "markerColor2",
            name = "Sotey Color",
            description = "Configures the second color of marked tiles"
    )
    default Color mazeTileColour2()
    {
        return Color.WHITE;
    }

    @ConfigItem(
            position = 23,
            keyName = "xarpusStub",
            name = "Xarpus",
            description = "" //stubs don't show descriptions when hovered over
    )
    default Stub xarpusStub()
    {
        return new Stub();
    }

    @ConfigItem(
            position = 24,
            keyName = "showXarpusHeals",
            name = "Show Xarpus Heals",
            description = "Highlights the tiles that Xarpus is healing with."
    )
    default boolean showXarpusHeals() {
        return true;
    }

    @ConfigItem(
            position = 25,
            keyName = "showXarpusTick",
            name = "Show Xarpus Turn Tick",
            description = "Count down the ticks until Xarpus turns their head."
    )
    default boolean showXarpusTick() {
        return true;
    }

    @ConfigItem(
            position = 26,
            keyName = "verzikStub",
            name = "Verzik",
            description = "" //stubs don't show descriptions when hovered over
    )
    default Stub verzikStub()
    {
        return new Stub();
    }

    @ConfigItem(
            position = 27,
            keyName = "showVerzikAttacks",
            name = "Show Verzik Attack Tick",
            description = "Count down the ticks until Verzik attacks."
    )
    default boolean showVerzikAttacks() {
        return true;
    }

    @ConfigItem(
            position = 28,
            keyName = "showVerzikYellows",
            name = "Show Yellows Tick",
            description = "Count down the ticks until Verzik yellow's damage tick."
    )
    default boolean showVerzikYellows() {
        return true;
    }

    @ConfigItem(
            position = 29,
            keyName = "showCrabTargets",
            name = "Show Crab Targets",
            description = "Shows the target of crabs at Verzik."
    )
    default boolean showCrabTargets() {
        return true;
    }

    @ConfigItem(
            position = 30,
            keyName = "VerzikTankTile",
            name = "Verzik P3 Tile Overlay",
            description = ""
    )
    default boolean VerzikTankTile(){ return false; }

    @ConfigItem(
            position = 31,
            keyName = "showVerzikRangeAttacks",
            name = "Show Verzik Range Atacks",
            description = "Shows the Highlight the tile in which a ranged attack on P2 will land."
    )
    default boolean showVerzikRangeAttack(){ return false; }

    @ConfigItem(
            position = 32,
            keyName = "showVerzikP1Timer",
            name = "Verzik Pillar Timer",
            description = "Overlays a tick timer during P1 verzik."
    )
    default boolean showVerzikP1Timer(){ return false; }

    @ConfigItem(
            position = 33,
            keyName = "shiftClickPurple",
            name = "Hide Purple",
            description = "Removes clickbox on purple crab spawn at Verzik."
    )
    default boolean shiftClickPurple(){ return false; }

    @ConfigItem(
            position = 34,
            keyName = "up",
            name = "Purple Key",
            description = "The key which will allow you to attack the purple crab."
    )
    default ModifierlessKeybind up()
    {
        return new ModifierlessKeybind(KeyEvent.VK_W, 0);
    }
    @ConfigItem(
            position = 35,
            keyName = "miscellaneousStub",
            name = "Miscellaneous",
            description = "" //stubs don't show descriptions when hovered over
    )
    default Stub miscStub()
    {
        return new Stub();
    }

    @ConfigItem(
            position = 36,
            keyName = "tickTimer",
            name = "Dumb Timer",
            description = "Dont tick this u dont need it."
    )
    default boolean showTickTimer(){ return false; }

    @ConfigItem(
            position = 37,
            keyName = "dudash",
            name = "Golden Spoon",
            description = "Turn this on to receive a message from the golden spoon himself when entering ToB loot chamber."
    )
    default boolean dudash() { return false; }
    @Range(max = 100)
    @ConfigItem(
            position = 38,
            keyName = "goldenSpoonSoundsVolume",
            name = "Golden Spoon Sound Volume",
            description = ""
    )
    default int dudashSoundVolume() { return 50; }
    /**
    @ConfigItem(
            position = 21,
            keyName = "showVerzikTickEat",
            name = "Show Verzik Tick Eats (Solo)",
            description = "Shows the timer on Verzik's electric (P2) and green ball (P3) attacks."
    )
    default boolean showVerzikTickEat(){ return false; }
    @ConfigItem(
            position = 30,
            keyName = "sotetsegMaze1",
            name = "Sotetseg maze",
            description = ""
    )
    default boolean sotetsegMaze(){ return false; }
     **/

}
