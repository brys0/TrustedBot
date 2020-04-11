package de.pheromir.trustedbot.r6;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * The available Challenges, currently hard coded
 * @author MeFisto94
 */
public enum Challenges {
    GERMAN_FRENCH(op -> {
        List<Language> list = Arrays.asList(op.unit.languages);
        return list.contains(Language.GERMAN) || list.contains(Language.FRENCH);
    }),
    
    ENGLISHMEN(op -> Arrays.asList(op.unit.languages).contains(Language.ENGLISH)),
    SPANISH(op -> Arrays.asList(op.unit.languages).contains(Language.SPANISH)),
    RANDOM_OP(op -> true),
    
    TOXIC(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.TOXIC)),
    TRAPS(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.TRAPS)),
    C4(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.C4)),
    SHIELD(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.SHIELD)),
    SPAWN_PEEK(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.SPAWN_PEEK)),
    FRAGS(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.FRAGS)),
    MEMES(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.MEME)),
    RUSH(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.RUSH)),
    CLAYMORE(op -> Arrays.asList(op.specialAbilities).contains(SpecialAbilities.CLAYMORE)),
    
    // Continents
    ASIA(op -> op.unit.continent == Continent.ASIA),
    EUROPE(op -> op.unit.continent == Continent.EUROPE),
    NA(op -> op.unit.continent == Continent.NORTH_AMERICA),
    SA(op -> op.unit.continent == Continent.SOUTH_AMERICA),

    // Weapons
    SHOTGUN(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.SHOTGUN)),
    SMG(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.SMG)),
    RIFLE(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.RIFLE)),
    DMR(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.DMR)),
    LMG(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.LMG)),
    REVOLVER(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.REVOLVER)),
    SECONDARY_SMG(op -> Arrays.asList(op.weaponCapabilities).contains(WeaponType.SECONDARY_SMG)),

    FEMALE(op -> !op.male),
    MALE(op -> op.male);

    public Predicate<Operator> filter;
    Challenges(Predicate<Operator> filter) {
        this.filter = filter;
    }
}
