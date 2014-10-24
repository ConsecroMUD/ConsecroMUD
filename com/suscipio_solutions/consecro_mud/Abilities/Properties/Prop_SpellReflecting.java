package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_SpellReflecting extends Property implements TriggeredAffect
{
	@Override public String ID() { return "Prop_SpellReflecting"; }
	@Override public String name(){ return "Spell reflecting property";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS;}

	protected int minLevel=1;
	protected int maxLevel=30;
	protected int chance=100;
	protected int remaining=100;
	protected int fade=1;
	protected int uses=100;
	protected long lastFade=0;

	@Override public int abilityCode(){return uses;}
	@Override public void setAbilityCode(int newCode){uses=newCode;}

	@Override public long flags(){return Ability.FLAG_IMMUNER;}

	@Override
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_BEING_HIT;
	}

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		minLevel=CMParms.getParmInt(newText,"min",minLevel);
		maxLevel=CMParms.getParmInt(newText,"max",maxLevel);
		chance=CMParms.getParmInt(newText,"chance",chance);
		fade=CMParms.getParmInt(newText,"fade",fade);
		remaining=CMParms.getParmInt(newText,"remain",remaining);
		setAbilityCode(remaining);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(affected==null)	return true;
		if((fade<=0)&&(abilityCode()<remaining))
		{
			if(lastFade==0) lastFade=System.currentTimeMillis();
			final long time=System.currentTimeMillis()-lastFade;
			if(time>5*60000)
			{
				final double div=CMath.div(time,(long)5*60000);
				if(div>1.0)
				{
					setAbilityCode(abilityCode()+(int)Math.round(div));
					if(abilityCode()>remaining)
						setAbilityCode(remaining);
					lastFade=System.currentTimeMillis();
				}
			}
		}

		if((CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		&&(msg.targetMinor()==CMMsg.TYP_CAST_SPELL)
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Ability)
		&&(CMLib.dice().rollPercentage()<=chance)
		&&(abilityCode()>0)
		&&((((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SPELL))
		{
			MOB target=null;
			if(affected instanceof MOB)
				target=(MOB)affected;
			else
			if((affected instanceof Item)
			&&(!((Item)affected).amWearingAt(Wearable.IN_INVENTORY))
			&&(((Item)affected).owner()!=null)
			&&(((Item)affected).owner() instanceof MOB))
				target=(MOB)((Item)affected).owner();
			else
				return true;

			if(!msg.amITarget(target)) return true;
			if(msg.amISource(target)) return true;
			if(target.location()==null) return true;

			int lvl=CMLib.ableMapper().qualifyingLevel(msg.source(),((Ability)msg.tool()));
			if(lvl<=0) lvl=CMLib.ableMapper().lowestQualifyingLevel(((Ability)msg.tool()).ID());
			if(lvl<=0) lvl=1;
			if((lvl<minLevel)||(lvl>maxLevel)) return true;

			target.location().show(target,affected,CMMsg.MSG_OK_VISUAL,L("The field around <T-NAMESELF> reflects the spell!"));
			final Ability A=(Ability)msg.tool();
			A.invoke(target,msg.source(),true,msg.source().phyStats().level());
			setAbilityCode(abilityCode()-lvl);
			if(abilityCode()<=0)
			{
				if(affected instanceof MOB)
				{
					target.location().show(target,target,CMMsg.MSG_OK_VISUAL,L("The field around <T-NAMESELF> fades."));
					if(fade>0)
						target.delEffect(this);
				}
				else
				if(affected instanceof Item)
				{
					if(fade>0)
					{
						target.location().show(target,affected,CMMsg.MSG_OK_VISUAL,L("<T-NAMESELF> vanishes!"));
						((Item)affected).destroy();
						target.location().recoverRoomStats();
					}
					else
						target.location().show(target,affected,CMMsg.MSG_OK_VISUAL,L("The field around <T-NAMESELF> fades."));
				}
			}
			return false;
		}
		return super.okMessage(myHost,msg);
	}


}
