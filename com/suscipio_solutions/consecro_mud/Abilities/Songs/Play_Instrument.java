package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MusicalInstrument;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Play_Instrument extends Play
{
	@Override public String ID() { return "Play_Instrument"; }
	private final static String localizedName = CMLib.lang().L("Instruments");
	@Override public String name() { return localizedName; }
	@Override protected int requiredInstrumentType(){return MusicalInstrument.TYPE_WOODS;}
	public String mimicSpell(){return "";}

	@Override
	protected void inpersistantAffect(MOB mob)
	{
		Ability A=getSpell();
		if((A!=null)
		&&((mob!=invoker())||(getSpell().abstractQuality()!=Ability.QUALITY_MALICIOUS)))
		{
			final Vector chcommands=new Vector();
			chcommands.addElement(mob.name());
			A=(Ability)A.copyOf();
			A.invoke(invoker(),chcommands,mob,true,adjustedLevel(invoker(),0));
			if((A.abstractQuality()==Ability.QUALITY_MALICIOUS)
			&&(mob.isMonster())
			&&(!mob.isInCombat())
			&&(CMLib.flags().isMobile(mob))
			&&(!CMLib.flags().isATrackingMonster(mob))
			&&(mob.amFollowing()==null)
			&&(!mob.amDead())
			&&((!(mob instanceof Rideable))||(((Rideable)mob).numRiders()==0)))
			{
				A=CMClass.getAbility("Thief_Assassinate");
				if(A!=null) A.invoke(mob,invoker(),true,0);
			}
		}
	}


	@Override
	protected String songOf()
	{
		if(instrument!=null)
			return instrument.name();
		return name();
	}
	protected Ability getSpell()
	{
		return null;
	}
	@Override
	public int abstractQuality()
	{
		if(getSpell()!=null) return getSpell().abstractQuality();
		return Ability.QUALITY_BENEFICIAL_OTHERS;
	}
	@Override protected boolean persistantSong(){return false;}
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
}
