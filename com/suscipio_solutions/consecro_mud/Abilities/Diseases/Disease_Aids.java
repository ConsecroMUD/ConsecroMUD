package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Disease_Aids extends Disease
{
	@Override public String ID() { return "Disease_Aids"; }
	private final static String localizedName = CMLib.lang().L("Aids");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Aids)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}
	@Override public int difficultyLevel(){return 9;}

	@Override protected int DISEASE_TICKS(){return 999999;}
	@Override protected int DISEASE_DELAY(){return 50;}
	@Override protected String DISEASE_DONE(){return "Your aids goes into remission.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> look(s) like <S-HE-SHE> <S-IS-ARE> sick.^?";}
	@Override protected String DISEASE_AFFECT(){return "";}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_STD;}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		if(affected==null) return;
		affectableStats.setStat(CharStats.STAT_SAVE_DISEASE,affectableStats.getStat(CharStats.STAT_SAVE_DISEASE)-200);
	}

}
