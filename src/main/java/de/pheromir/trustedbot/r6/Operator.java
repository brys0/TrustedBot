package de.pheromir.trustedbot.r6;

/**
 * The Main Operator class containing the database/all important values
 *
 * @author MeFisto94
 */
public enum Operator {
    // Attackers
    SLEDGE(true, SpecialUnit.SAS, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG}, SpecialAbilities.FRAGS, SpecialAbilities.RUSH),
    THATCHER(true, SpecialUnit.SAS, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SHOTGUN}, SpecialAbilities.CLAYMORE, SpecialAbilities.DISABLER),
    ASH(false, SpecialUnit.SWAT, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE }, SpecialAbilities.RUSH),
    THERMITE(false, SpecialUnit.SWAT, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SHOTGUN}, SpecialAbilities.CLAYMORE, SpecialAbilities.HARD_BREACH),
    TWITCH(false, SpecialUnit.GIGN, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.DMR, WeaponType.SHOTGUN, WeaponType.REVOLVER }, SpecialAbilities.CLAYMORE, SpecialAbilities.RUSH),
    MONTAGNE(false, SpecialUnit.GIGN, Side.ATTACKERS, new WeaponType[] { WeaponType.REVOLVER }, SpecialAbilities.SHIELD),
    GLAZ(true, SpecialUnit.SPETZNAS, Side.ATTACKERS, new WeaponType[] { WeaponType.DMR }, SpecialAbilities.FRAGS, SpecialAbilities.MEME),
    FUZE(true, SpecialUnit.SPETZNAS, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.LMG }, SpecialAbilities.SHIELD),
    BLITZ(true, SpecialUnit.GSG9, Side.ATTACKERS, new WeaponType[0], SpecialAbilities.SHIELD, SpecialAbilities.RUSH),
    IQ(false, SpecialUnit.GSG9, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.LMG }, SpecialAbilities.CLAYMORE, SpecialAbilities.RUSH),
    BUCK(true, SpecialUnit.JTF2, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.DMR }, SpecialAbilities.FRAGS),
    BLACKBEARD(true, SpecialUnit.NAVY, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.DMR, WeaponType.REVOLVER }, SpecialAbilities.SHIELD),
    CAPITAO(true, SpecialUnit.BOPE, Side.ATTACKERS, new WeaponType[]{ WeaponType.LMG, WeaponType.RIFLE }, SpecialAbilities.CLAYMORE, SpecialAbilities.RUSH ),
    HIBANA(false, SpecialUnit.SAT, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG }, SpecialAbilities.HARD_BREACH),
    JACKAL(true, SpecialUnit.GEO, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.MEME, SpecialAbilities.CLAYMORE),
    YING(false, SpecialUnit.SDU, Side.ATTACKERS, new WeaponType[]{ WeaponType.LMG, WeaponType.SHOTGUN }, SpecialAbilities.TOXIC, SpecialAbilities.CLAYMORE),
    ZOFIA(false, SpecialUnit.GROM, Side.ATTACKERS, new WeaponType[]{ WeaponType.LMG, WeaponType.RIFLE }, SpecialAbilities.CLAYMORE),
    DOKKAEBI(false, SpecialUnit.SevenOSeven, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG }, SpecialAbilities.TOXIC),
    LION(true, SpecialUnit.GIGN /* French, actually CBRN */, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.DMR, WeaponType.SHOTGUN, WeaponType.REVOLVER }, SpecialAbilities.CLAYMORE, SpecialAbilities.TOXIC, SpecialAbilities.RUSH),
    FINKA(false, SpecialUnit.SPETZNAS /* See above */, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.LMG, WeaponType.SHOTGUN }, SpecialAbilities.FRAGS),
    MAVERICK(true, SpecialUnit.NAVY /* See above */, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.DMR }, SpecialAbilities.CLAYMORE, SpecialAbilities.FRAGS, SpecialAbilities.DISABLER),
    NOMAND(false, SpecialUnit.GIGR, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE }),
    GRIDLOCK(false, SpecialUnit.SASR, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.LMG, WeaponType.SHOTGUN }, SpecialAbilities.MEME),
    NOKK(false, SpecialUnit.JAGERKORPSET, Side.ATTACKERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.REVOLVER }, SpecialAbilities.FRAGS, SpecialAbilities.RUSH),
    AMARU(false, SpecialUnit.APCA, Side.ATTACKERS, new WeaponType[]{ WeaponType.LMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG}, SpecialAbilities.CLAYMORE, SpecialAbilities.RUSH),
    KALI(false, SpecialUnit.NIGHTHAVEN, Side.ATTACKERS, new WeaponType[]{ WeaponType.DMR, WeaponType.SECONDARY_SMG }, SpecialAbilities.CLAYMORE, SpecialAbilities.DISABLER),
    IANA(false, SpecialUnit.REU, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE }, SpecialAbilities.FRAGS, SpecialAbilities.RUSH),

    // DEFENDERS
    SMOKE(true, SpecialUnit.SAS, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG }),
    MUTE(true, SpecialUnit.SAS, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG }, SpecialAbilities.C4),
    CASTLE(true, SpecialUnit.SWAT, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }),
    PULSE(true, SpecialUnit.SWAT, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4),
    DOC(true, SpecialUnit.GIGN, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.REVOLVER, WeaponType.SHOTGUN }, SpecialAbilities.SPAWN_PEEK),
    ROOK(true, SpecialUnit.GIGN, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.REVOLVER, WeaponType.SHOTGUN }, SpecialAbilities.SPAWN_PEEK),
    KAPKAN(true, SpecialUnit.SPETZNAS, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN}, SpecialAbilities.C4, SpecialAbilities.TRAPS),
    TACHANKA(true, SpecialUnit.SPETZNAS, Side.DEFENDERS, new WeaponType[] { WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.MEME),
    JAEGER(true, SpecialUnit.GSG9, Side.DEFENDERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SHOTGUN }, SpecialAbilities.SPAWN_PEEK),
    BANDIT(true, SpecialUnit.GSG9, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4),
    FROST(false, SpecialUnit.JTF2, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.TRAPS),
    VALKYRIE(false, SpecialUnit.NAVY, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.REVOLVER }, SpecialAbilities.C4),
    CAVEIRA(false, SpecialUnit.BOPE, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }),
    ECHO(true, SpecialUnit.SAT, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG }, SpecialAbilities.SPAWN_PEEK),
    MIRA(false, SpecialUnit.GEO, Side.DEFENDERS, new WeaponType[] { WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4),
    LESION(true, SpecialUnit.SDU, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.TRAPS),
    ELA(false, SpecialUnit.GROM, Side.DEFENDERS, new WeaponType[] { WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.TRAPS),
    VIGIL(true, SpecialUnit.SevenOSeven, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG}),
    MAESTRO(true, SpecialUnit.GIS, Side.DEFENDERS, new WeaponType[]{ WeaponType.LMG, WeaponType.SHOTGUN, WeaponType.REVOLVER }),
    ALIBI(false, SpecialUnit.GIS, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.REVOLVER }),
    CLASH(false, SpecialUnit.SAS, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.REVOLVER }, SpecialAbilities.SHIELD, SpecialAbilities.TOXIC),
    KAID(true, SpecialUnit.GIGR, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4),
    MOZZIE(true, SpecialUnit.SASR, Side.DEFENDERS, new WeaponType[]{WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4),
    WARDEN(true, SpecialUnit.NAVY, Side.DEFENDERS, new WeaponType[]{WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG}, SpecialAbilities.C4),
    GOYO(true, SpecialUnit.APCA, Side.DEFENDERS, new WeaponType[]{WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4, SpecialAbilities.TRAPS),
    WAMAI(true, SpecialUnit.NIGHTHAVEN, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.RIFLE, WeaponType.REVOLVER}),
    ORYX(true, SpecialUnit.REU /* Unaffiliated */, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.REVOLVER });

    public boolean male;
    public SpecialUnit unit;
    public Side side;
    public WeaponType[] weaponCapabilities;
    public SpecialAbilities[] specialAbilities;

    Operator(boolean male, SpecialUnit unit, Side side, WeaponType[] weaponCapabilities, SpecialAbilities... specialAbilities) {
        this.male = male;
        this.unit = unit;
        this.side = side;
        this.weaponCapabilities = weaponCapabilities;
        this.specialAbilities = specialAbilities;
    }
}
