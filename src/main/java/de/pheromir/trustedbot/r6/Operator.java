package de.pheromir.trustedbot.r6;

/**
 * The Main Operator class containing the database/all important values
 *
 * @author MeFisto94
 */
public enum Operator {
    // Attackers
    SLEDGE("<:sledge:698351308779814941>", "Sledge", true, SpecialUnit.SAS, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG}, SpecialAbilities.FRAGS, SpecialAbilities.RUSH),
    THATCHER("<:thatcher:698351308305989644> ", "Thatcher", true, SpecialUnit.SAS, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SHOTGUN}, SpecialAbilities.CLAYMORE, SpecialAbilities.DISABLER),
    ASH("<:ash:698351308452659302>", "Ash", false, SpecialUnit.SWAT, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE }, SpecialAbilities.RUSH),
    THERMITE("<:thermite:698351308712706119>", "Thermite", true, SpecialUnit.SWAT, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SHOTGUN}, SpecialAbilities.CLAYMORE, SpecialAbilities.HARD_BREACH),
    TWITCH("<:twitch:698351308922683402>", "Twitch", false, SpecialUnit.GIGN, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.DMR, WeaponType.SHOTGUN, WeaponType.REVOLVER }, SpecialAbilities.CLAYMORE, SpecialAbilities.RUSH),
    MONTAGNE("<:montagne:698351310558462072>", "Montagne", true, SpecialUnit.GIGN, Side.ATTACKERS, new WeaponType[] { WeaponType.REVOLVER }, SpecialAbilities.SHIELD),
    GLAZ("<:glaz:698351308419235861>", "Glaz", true, SpecialUnit.SPETSNAZ, Side.ATTACKERS, new WeaponType[] { WeaponType.DMR }, SpecialAbilities.FRAGS, SpecialAbilities.MEME),
    FUZE("<:fuze:698351308746260571>","Fuze",  true, SpecialUnit.SPETSNAZ, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.LMG }, SpecialAbilities.SHIELD),
    BLITZ("<:blitz:698351308687540254>", "Blitz", true, SpecialUnit.GSG9, Side.ATTACKERS, new WeaponType[0], SpecialAbilities.SHIELD, SpecialAbilities.RUSH, SpecialAbilities.TOXIC),
    IQ("<:iq:698351308305989643>", "IQ", false, SpecialUnit.GSG9, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.LMG }, SpecialAbilities.CLAYMORE, SpecialAbilities.RUSH),
    BUCK("<:buck:698351308725551154>", "Buck", true, SpecialUnit.JTF2, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.DMR }, SpecialAbilities.FRAGS),
    BLACKBEARD("<:blackbeard:698351308788203592>", "Blackbeard", true, SpecialUnit.NAVY, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.DMR, WeaponType.REVOLVER }, SpecialAbilities.SHIELD, SpecialAbilities.TOXIC),
    CAPITAO("<:capitao:698351308729614357>", "Capitão", true, SpecialUnit.BOPE, Side.ATTACKERS, new WeaponType[]{ WeaponType.LMG, WeaponType.RIFLE }, SpecialAbilities.CLAYMORE, SpecialAbilities.RUSH ),
    HIBANA("<:hibana:698351308825952296>", "Hibana", false, SpecialUnit.SAT, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG }, SpecialAbilities.HARD_BREACH),
    JACKAL("<:jackal:698351308607848478>", "Jackal", true, SpecialUnit.GEO, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.MEME, SpecialAbilities.CLAYMORE, SpecialAbilities.TOXIC),
    YING("<:ying:698351457987985409>", "Ying", false, SpecialUnit.SDU, Side.ATTACKERS, new WeaponType[]{ WeaponType.LMG, WeaponType.SHOTGUN }, SpecialAbilities.TOXIC, SpecialAbilities.CLAYMORE),
    ZOFIA("<:zofia:698351457992442028>", "Zofia", false, SpecialUnit.GROM, Side.ATTACKERS, new WeaponType[]{ WeaponType.LMG, WeaponType.RIFLE }, SpecialAbilities.CLAYMORE),
    DOKKAEBI("<:dokkaebi:698351308523962450>", "Dokkaebi", false, SpecialUnit.SevenOSeven, Side.ATTACKERS, new WeaponType[]{ WeaponType.DMR, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG }, SpecialAbilities.TOXIC),
    LION("<:lion:698351308456984707>", "Lion", true, SpecialUnit.GIGN /* French, actually CBRN */, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.DMR, WeaponType.SHOTGUN, WeaponType.REVOLVER }, SpecialAbilities.CLAYMORE, SpecialAbilities.TOXIC, SpecialAbilities.RUSH),
    FINKA("<:finka:698351308310315129>", "Finka", false, SpecialUnit.SPETSNAZ /* See above */, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.LMG, WeaponType.SHOTGUN }, SpecialAbilities.FRAGS),
    MAVERICK("<:maverick:698351308876546128>", "Maverick", true, SpecialUnit.NAVY /* See above */, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.DMR }, SpecialAbilities.CLAYMORE, SpecialAbilities.FRAGS, SpecialAbilities.DISABLER, SpecialAbilities.HARD_BREACH),
    NOMAD("<:nomad:698351308645728256>", "Nomad", false, SpecialUnit.GIGR, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE }),
    GRIDLOCK("<:gridlock:698351308540870746>", "Gridlock", false, SpecialUnit.SASR, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.LMG, WeaponType.SHOTGUN }, SpecialAbilities.MEME),
    NOKK("<:nokk:698351308851380234>", "Nøkk", false, SpecialUnit.JAGERKORPSET, Side.ATTACKERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.REVOLVER }, SpecialAbilities.FRAGS, SpecialAbilities.RUSH),
    AMARU("<:amaru:698351308272566303>", "Amaru", false, SpecialUnit.APCA, Side.ATTACKERS, new WeaponType[]{ WeaponType.LMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG}, SpecialAbilities.CLAYMORE, SpecialAbilities.RUSH),
    KALI("<:kali:698351308989661204>", "Kali", false, SpecialUnit.NIGHTHAVEN, Side.ATTACKERS, new WeaponType[]{ WeaponType.DMR, WeaponType.SECONDARY_SMG }, SpecialAbilities.CLAYMORE, SpecialAbilities.DISABLER),
    IANA("<:iana:698351308276760637>", "Iana", false, SpecialUnit.REU, Side.ATTACKERS, new WeaponType[]{ WeaponType.RIFLE }, SpecialAbilities.FRAGS, SpecialAbilities.RUSH),
    RECRUIT_ATK("<:recruit:698553471531745320>", "Recruit", true, SpecialUnit.UNKNOWN, Side.ATTACKERS, new WeaponType[] { WeaponType.LMG, WeaponType.RIFLE, WeaponType.DMR, WeaponType.SHOTGUN }, SpecialAbilities.MEME, SpecialAbilities.CLAYMORE),

    // DEFENDERS
    SMOKE("<:smoke:698351308733939752>", "Smoke", true, SpecialUnit.SAS, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG }, SpecialAbilities.DEF_SHIELD),
    MUTE("<:mute:698351308729483416>", "Mute", true, SpecialUnit.SAS, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG }, SpecialAbilities.C4),
    CASTLE("<:castle:698351308515573840>", "Castle", true, SpecialUnit.SWAT, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }),
    PULSE("<:pulse:698351308972884099>", "Pulse", true, SpecialUnit.SWAT, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4),
    DOC("<:doc:698351308574294116>", "Doc", true, SpecialUnit.GIGN, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.REVOLVER, WeaponType.SHOTGUN }, SpecialAbilities.SPAWN_PEEK),
    ROOK("<:rook:698351308846923806>", "Rook", true, SpecialUnit.GIGN, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.REVOLVER, WeaponType.SHOTGUN }, SpecialAbilities.SPAWN_PEEK, SpecialAbilities.DEF_SHIELD),
    KAPKAN("<:kapkan:698351308226297918>", "Kapkan", true, SpecialUnit.SPETSNAZ, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN}, SpecialAbilities.C4, SpecialAbilities.TRAPS),
    TACHANKA("<:tachanka:698351308687802404>", "Tachanka", true, SpecialUnit.SPETSNAZ, Side.DEFENDERS, new WeaponType[] { WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.MEME, SpecialAbilities.DEF_SHIELD),
    JAGER("<:jager:698351308582682664>", "Jäger", true, SpecialUnit.GSG9, Side.DEFENDERS, new WeaponType[]{ WeaponType.RIFLE, WeaponType.SHOTGUN }, SpecialAbilities.SPAWN_PEEK),
    BANDIT("<:bandit:698351308544933948>", "Bandit", true, SpecialUnit.GSG9, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4),
    FROST("<:frost:698351308469436518>", "Frost", false, SpecialUnit.JTF2, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.TRAPS, SpecialAbilities.DEF_SHIELD),
    VALKYRIE("<:valkyrie:698351308889128960>", "Valkyrie", false, SpecialUnit.NAVY, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.REVOLVER }, SpecialAbilities.C4, SpecialAbilities.DEF_SHIELD),
    CAVEIRA("<:caveira:698351308184485909>", "Caveira", false, SpecialUnit.BOPE, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }),
    ECHO("<:echo:698351308616237127>", "Echo", true, SpecialUnit.SAT, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG }, SpecialAbilities.SPAWN_PEEK, SpecialAbilities.DEF_SHIELD),
    MIRA("<:mira:698351308654247976>", "Mira", false, SpecialUnit.GEO, Side.DEFENDERS, new WeaponType[] { WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4),
    LESION("<:lesion:698351308352258182>", "Lesion", true, SpecialUnit.SDU, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.TRAPS),
    ELA("<:ela:698351308595396626>", "Ela", false, SpecialUnit.GROM, Side.DEFENDERS, new WeaponType[] { WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.TRAPS, SpecialAbilities.DEF_SHIELD),
    VIGIL("<:vigil:698351308620431424>", "Vigil", true, SpecialUnit.SevenOSeven, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG}, SpecialAbilities.SPAWN_PEEK),
    MAESTRO("<:maestro:698351308645597215>", "Maestro", true, SpecialUnit.GIS, Side.DEFENDERS, new WeaponType[]{ WeaponType.LMG, WeaponType.SHOTGUN, WeaponType.REVOLVER }),
    ALIBI("<:alibi:698351308628951140>", "Alibi", false, SpecialUnit.GIS, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.REVOLVER }, SpecialAbilities.DEF_SHIELD),
    CLASH("<:clash:698351308377161759>", "Clash", false, SpecialUnit.SAS, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.REVOLVER }, SpecialAbilities.SHIELD, SpecialAbilities.TOXIC),
    KAID("<:kaid:698351308507447338>", "Kaid", true, SpecialUnit.GIGR, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4),
    MOZZIE("<:mozzie:698351308817563850>", "Mozzie", true, SpecialUnit.SASR, Side.DEFENDERS, new WeaponType[]{WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4),
    WARDEN("<:warden:698351457992179833>", "Warden", true, SpecialUnit.NAVY, Side.DEFENDERS, new WeaponType[]{WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG}, SpecialAbilities.C4, SpecialAbilities.DEF_SHIELD),
    GOYO("<:goyo:698351308230623274>", "Goyo", true, SpecialUnit.APCA, Side.DEFENDERS, new WeaponType[]{WeaponType.SMG, WeaponType.SHOTGUN }, SpecialAbilities.C4, SpecialAbilities.TRAPS, SpecialAbilities.DEF_SHIELD),
    WAMAI("<:wamai:698351457686257766>", "Wamai", true, SpecialUnit.NIGHTHAVEN, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.RIFLE, WeaponType.REVOLVER}, SpecialAbilities.DEF_SHIELD),
    ORYX("<:oryx:698351308230492252>", "Oryx", true, SpecialUnit.REU /* Unaffiliated */, Side.DEFENDERS, new WeaponType[]{ WeaponType.SMG, WeaponType.SHOTGUN }),
	RECRUIT_DEF("<:recruit:698553471531745320>", "Recruit", true, SpecialUnit.UNKNOWN, Side.DEFENDERS, new WeaponType[] { WeaponType.SMG, WeaponType.SHOTGUN, WeaponType.SECONDARY_SMG }, SpecialAbilities.MEME, SpecialAbilities.C4, SpecialAbilities.DEF_SHIELD);

    public boolean male;
    public String name;
    public SpecialUnit unit;
    public Side side;
    public WeaponType[] weaponCapabilities;
    public SpecialAbilities[] specialAbilities;
    public String emote;

    Operator(String emote, String name, boolean male, SpecialUnit unit, Side side, WeaponType[] weaponCapabilities, SpecialAbilities... specialAbilities) {
    	this.emote = emote;
    	this.name = name;
        this.male = male;
        this.unit = unit;
        this.side = side;
        this.weaponCapabilities = weaponCapabilities;
        this.specialAbilities = specialAbilities;
    }
}
