package de.pheromir.trustedbot.r6;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * The available Challenges, currently hard coded
 *
 * @author MeFisto94
 */
public enum Challenges {

	// Languages
    GERMAN_FRENCH(op -> {
        List<Language> list = Arrays.asList(op.unit.languages);
        return op.unit.continent.equals(Continent.EUROPE) && (list.contains(Language.GERMAN) || list.contains(Language.FRENCH));
    }, "German & French", "Play german or french operators only"),
    
    ENGLISHMEN(op -> Arrays.asList(op.unit.languages).contains(Language.ENGLISH), "Englishmen", "Play english speaking operators only"),
    SPANISH(op -> Arrays.asList(op.unit.languages).contains(Language.SPANISH), "Spanish", "Play spanish operators only"),
    
    // Equipment
    TRAPS(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.TRAPS), "Traps", "Play trap operators"),
    C4(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.C4), "C4", "Play with a C4 (nitro cell) and use it"),
    SHIELD(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.SHIELD), "Shield", "Play with a shield"),
    FRAGS(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.FRAGS), "Frags", "Play with frag grenades and use them"),
    CLAYMORE(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.CLAYMORE), "Claymore", "Play with a claymore and use it"),
    NO_DEF_SHIELD(op -> op.side == Side.DEFENDERS && !Arrays.asList(op.specialAbilities).contains(SpecialAbilities.DEF_SHIELD), "No deployable Shields", "You're not allowed to play with deployable shields"),
    NO_HARDBREACH(op -> op.side == Side.ATTACKERS && !Arrays.asList(op.specialAbilities).contains(SpecialAbilities.HARD_BREACH), "No hard breach", "You're not allowed to play with hard breachers"),
    
    // Continents
    ASIA(op -> op.unit.continent == Continent.ASIA, "Asia", "Play operators from Asia"),
    EUROPE(op -> op.unit.continent == Continent.EUROPE, "Europe", "Play operators from Europe"),
    NA(op -> op.unit.continent == Continent.NORTH_AMERICA, "North America", "Play operators from North America"),
    SA(op -> op.unit.continent == Continent.SOUTH_AMERICA, "South America", "Play operators from South America"),

    // Weapons
    SHOTGUN(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.SHOTGUN), "Shotgun", "Play with a shotgun only (Gadgets still allowed)"),
    SMG(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.SMG), "SMG", "Play with a SMG only (Gadgets still allowed)"),
    RIFLE(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.RIFLE), "Rifle", "Play with a Rifle only (Gadgets still allowed)"),
    DMR(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.DMR), "DMR", "Play with a DMR only (Gadgets still allowed)"),
    LMG(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.LMG), "LMG", "Play with a LMG only (Gadgets still allowed)"),
    REVOLVER(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.REVOLVER), "Revolver & D-50", "Play with a Revolver or D-50 only (Gadgets still allowed)"),
    SECONDARY_SMG(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.SECONDARY_SMG), "Secondary SMG", "Play with a secondary SMG only (Gadgets still allowed)"),

    // Gender
    FEMALE(op -> !op.male, "Female", "Play with female operators only"),
    MALE(op -> op.male, "Male", "Play with male operators only"),
	
    // Playstyle
    RUSH(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.RUSH), "Rush", "Rush B сука блять\n(Dont stop walking!)"),
    SPAWN_PEEK(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.SPAWN_PEEK), "Spawn peek", "Peek the hell out of the attackers"),
    MEMES(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.MEME), "Meme", "Play \"Meme-operators\""),
    REVERSE(op -> op.side == Side.DEFENDERS, "Reverse Role", "Roamers are anchors, anchors are roamers", true),
    NO_REINFORCEMENTS(op -> op.side == Side.DEFENDERS, "No Reinforcements", "You're not allowed to use any reinforcement", true),
    ONE_REINFORCEMENT(op -> op.side == Side.DEFENDERS, "One Reinforcement", "You're only allowed to use one reinforcement each", true),
    NO_GADGETS("No Gadgets", "You're not allowed to use your primary-/secondary gadgets"),
    ONE_AT_A_TIME(op -> op.side == Side.ATTACKERS, "One at a time", "Only one person at the same time in the building. The last one in the scoreboard starts, then the one above (and so on..)", true),
    I_LIKE_DRONES(op -> op.side == Side.DEFENDERS, "I like drones!", "You're not allowed to destroy any drone (Hacking still allowed)"),
    
    // Misc
    TOXIC(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.TOXIC), "Toxic", "Play operators that are considered as toxic"),
	RANDOM_OP("Random Operator", "Play as a random operator (don't choose one)"),
	CARAMELLDANSEN("Caramelldansen", "Hear [Caramelldansen](https://www.youtube.com/watch?v=6-8E4Nirh9s) on 100% Volume while playing the round"),
    BRIGHTNESS_LOWEST("Lowest Brightness", "Turn your Display Brightness to the lowest value possible"),
    BRIGHTNESS_HIGHEST("Highest Brightness", "Turn your Display Brightness to the highest value possible"),
    INVERTED_CONTROLS("Inverted Controls", "Invert your Mouse Axis in the Controls");


    /**
     * The Predicate used to filter operators capable of fulfilling this challenge
     */
    public Predicate<Operator> filter;

    /**
     * The canonical name of this challenge
     */
    public String name;

    /**
     * The Description of this challenge
     */
    public String desc;

    /**
     * Whether this challenge is permitted on all operators (operator agnostic)
     */
    public boolean allOperators;

    /**
     * Constructs a challenge which is only completable with Operators passing the filter.
     *
     * @param filter The predicate used to filter capable operators
     * @param name The canonical name of this challenge
     * @param desc The description of this challenge.
     */
    Challenges(Predicate<Operator> filter, String name, String desc) {
        this(filter, name, desc, false);
    }

    /**
     * Constructs a challenge which is completable with all Operators
     * @param name The canonical name of this challenge
     * @param desc The description of this challenge
     */
    Challenges(String name, String desc) {
        this(((Operator op) -> true), name, desc, true);
    }

    /**
     * Constructs a challenge which is completable by "ALL" (allOperators) passing the filter.<br />
     * This is used for Challenges where a Predicate might filter for {@link Side} but still technically allow for all
     * Operators. Thus one can specify allOperators to indicate that the individual Operators aren't printed to the
     * message, even though some Operators might be filtered.
     *
     * @param filter The predicate used to filter capable operators
     * @param name The canonical name of this challenge
     * @param desc The description of this challenge
     * @param allOperators Whether this Challenge is completable on "ALL" Operators, regardless of the filter this is
     *                     just a Hint for the UI to NOT print out all individual operators.
     */
    Challenges(Predicate<Operator> filter, String name, String desc, boolean allOperators) {
        this.filter = filter;
        this.name = name;
        this.desc = desc;
        this.allOperators = allOperators;
    }
}
