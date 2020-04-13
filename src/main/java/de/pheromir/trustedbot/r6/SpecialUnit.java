package de.pheromir.trustedbot.r6;

/**
 * The Unit the Operators belong to (useful for geographic or language challenges)
 *
 * @author MeFisto94
 */
public enum SpecialUnit {
    GIGN(Continent.EUROPE, Language.FRENCH),
    SAS(Continent.EUROPE, Language.ENGLISH),
    SWAT(Continent.NORTH_AMERICA, Language.ENGLISH),
    SPETSNAZ(Continent.ASIA, Language.RUSSIAN),
    GSG9(Continent.EUROPE, Language.GERMAN),
    JTF2(Continent.NORTH_AMERICA, Language.ENGLISH, Language.FRENCH),
    NAVY(Continent.NORTH_AMERICA, Language.ENGLISH),
    BOPE(Continent.SOUTH_AMERICA, Language.SPANISH /* We don't care */),
    SAT(Continent.ASIA, Language.JAPANESE),
    GEO(Continent.EUROPE, Language.SPANISH),
    SDU(Continent.ASIA, Language.CHINESE),
    GROM(Continent.EUROPE, Language.POLISH),
    SevenOSeven(Continent.ASIA, Language.CHINESE),
    GIGR(Continent.AFRICA, Language.ARABIC),
    SASR(Continent.AUSTRALIA, Language.ENGLISH),
    JAGERKORPSET(Continent.EUROPE, Language.DANISH),
    GIS(Continent.EUROPE, Language.ITALIAN),
    APCA(Continent.SOUTH_AMERICA, Language.SPANISH),
    REU(Continent.ASIA, Language.GREEK_AND_DUTCH),
    NIGHTHAVEN(Continent.AFRICA, Language.AFRICAN), /* Kali is Indian(?) */
	UNKNOWN(Continent.UNKNOWN, Language.UNKNOWN);

    public Continent continent;
    public Language[] languages;

    SpecialUnit(Continent continent, Language... languages) {
        this.continent = continent;
        this.languages = languages;
    }
}
