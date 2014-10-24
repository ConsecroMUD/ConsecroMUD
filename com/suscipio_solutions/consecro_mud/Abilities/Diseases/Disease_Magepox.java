package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Disease_Magepox extends Disease
{
	@Override public String ID() { return "Disease_Magepox"; }
	private final static String localizedName = CMLib.lang().L("Magepox");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Magepox)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return CMProps.getIntVar( CMProps.Int.TICKSPERMUDDAY );}
	@Override protected int DISEASE_DELAY(){return 15;}
	@Override protected String DISEASE_DONE(){return "Your magepox clears up.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> come(s) down with the Magepox.^?";}
	@Override protected String DISEASE_AFFECT(){return "<S-NAME> watch(es) new mystical sores appear on <S-HIS-HER> body.";}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_PROXIMITY;}
	@Override public int difficultyLevel(){return 9;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if(affected==null) return false;
		if(!(affected instanceof MOB)) return true;

		final MOB mob=(MOB)affected;
		if((!mob.amDead())&&((--diseaseTick)<=0))
		{
			diseaseTick=DISEASE_DELAY();
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,DISEASE_AFFECT());
			catchIt(mob);
			return true;
		}
		return true;
	}

	@Override
	public void affectCharState(MOB affected, CharState affectableState)
	{
		if(affected==null) return;
		int hitsLost=affected.maxState().getHitPoints()-affected.curState().getHitPoints();
		if(hitsLost<0) hitsLost=0;
		int movesLost=(affected.maxState().getMovement()-affected.curState().getMovement());
		if(movesLost<0) movesLost=0;
		final int lostMana=hitsLost+movesLost;
		affectableState.setMana(affectableState.getMana()-lostMana);
		if(affectableState.getMana()<0)
			affectableState.setMana(0);
		if(affected.curState().getMana()>affectableState.getMana())
			affected.curState().setMana(affectableState.getMana());

	}
}
