package com.suscipio_solutions.consecro_mud.Abilities.Paladin;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Paladin_Aura extends PaladinSkill
{
	@Override public String ID() { return "Paladin_Aura"; }
	private final static String localizedName = CMLib.lang().L("Paladin`s Aura");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_HOLYPROTECTION;}
	public Paladin_Aura()
	{
		super();
		paladinsGroup=new Vector();
	}
	protected boolean pass=false;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		pass=(invoker==null)||(invoker.fetchAbility(ID())==null)||proficiencyCheck(null,0,false);
		if(pass)
		for(int i=paladinsGroup.size()-1;i>=0;i--)
		{
			try
			{
				final MOB mob=(MOB)paladinsGroup.elementAt(i);
				if(CMLib.flags().isEvil(mob))
				{
					final int damage=(int)Math.round(CMath.div(mob.phyStats().level()+(2*getXLEVELLevel(invoker)),3.0));
					CMLib.combat().postDamage(invoker,mob,this,damage,CMMsg.MASK_MALICIOUS|CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,Weapon.TYPE_BURSTING,"^SThe aura around <S-NAME> <DAMAGES> <T-NAME>!^?");
				}
			}
			catch(final java.lang.ArrayIndexOutOfBoundsException e)
			{
			}
		}
		return true;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if((invoker==null)||(!(CMLib.flags().isGood(invoker))))
			return true;
		if(affected==null) return true;
		if(!(affected instanceof MOB)) return true;

		if((msg.target()!=null)
		   &&(paladinsGroup.contains(msg.target()))
		   &&(!paladinsGroup.contains(msg.source()))
		   &&(pass)
		   &&(msg.target() instanceof MOB)
		   &&(msg.source()!=invoker))
		{
			if((CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
			&&(msg.targetMinor()==CMMsg.TYP_CAST_SPELL)
			&&(msg.tool()!=null)
			&&(msg.tool() instanceof Ability)
			&&(!CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_HOLY))
			&&(CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_UNHOLY)))
			{
				msg.source().location().show((MOB)msg.target(),null,CMMsg.MSG_OK_VISUAL,L("The holy field around <S-NAME> protect(s) <S-HIM-HER> from the evil magic attack of @x1.",msg.source().name()));
				return false;
			}
			if(((msg.targetMinor()==CMMsg.TYP_POISON)||(msg.targetMinor()==CMMsg.TYP_DISEASE))
			&&(pass))
				return false;
		}
		return true;
	}
}
