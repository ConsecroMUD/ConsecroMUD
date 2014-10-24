package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Beggar extends StdBehavior
{
	@Override public String ID(){return "Beggar";}
	Vector<MOB> mobsHitUp=new Vector<MOB>();
	int tickTock=0;

	@Override
	public String accountForYourself()
	{
		return "vagrant-like begging";
	}

	@Override
	public CMObject copyOf()
	{
		final Beggar obj=(Beggar)super.copyOf();
		obj.mobsHitUp=new Vector<MOB>();
		obj.mobsHitUp.addAll(mobsHitUp);
		return obj;
	}

	@Override
	public void executeMsg(Environmental oking, CMMsg msg)
	{
		super.executeMsg(oking,msg);
		if((oking==null)||(!(oking instanceof MOB)))
			return;
		final MOB mob=(MOB)oking;
		if((msg.amITarget(mob))&&(msg.targetMinor()==CMMsg.TYP_GIVE))
			msg.addTrailerMsg(CMClass.getMsg(mob,msg.source(),CMMsg.MSG_SPEAK,L("^T<S-NAME> say(s) 'Thank you gov'ner!' to <T-NAME> ^?")));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=Tickable.TICKID_MOB) return true;
		if(!canFreelyBehaveNormal(ticking)) return true;
		if(CMSecurity.isDisabled(CMSecurity.DisFlag.EMOTERS)) return true;
		tickTock++;
		if(tickTock<5) return true;
		tickTock=0;
		final MOB mob=(MOB)ticking;
		for(int i=0;i<mob.location().numInhabitants();i++)
		{
			final MOB mob2=mob.location().fetchInhabitant(i);
			if((mob2!=null)
			   &&(CMLib.flags().canBeSeenBy(mob2,mob))
			   &&(mob2!=mob)
			   &&(!mobsHitUp.contains(mob2))
			   &&(!mob2.isMonster()))
			{
				switch(CMLib.dice().roll(1,10,0))
				{
				case 1:
					CMLib.commands().postSay(mob,mob2,L("A little something for a vet please?"),false,false);
					break;
				case 2:
					CMLib.commands().postSay(mob,mob2,L("Spare a gold piece @x1",((mob2.charStats().getStat(CharStats.STAT_GENDER)=='M')?"mister?":"madam?")),false,false);
					break;
				case 3:
					CMLib.commands().postSay(mob,mob2,L("Spare some change?"),false,false);
					break;
				case 4:
					CMLib.commands().postSay(mob,mob2,L("Please @x1, a little something for an old @x2 down on @x3 luck?",((mob2.charStats().getStat(CharStats.STAT_GENDER)=='M')?"mister":"madam"),((mob.charStats().getStat(CharStats.STAT_GENDER)=='M')?"man":"woman"),mob.charStats().hisher()),false,false);
					break;
				case 5:
					CMLib.commands().postSay(mob,mob2,L("Hey, I lost my 'Will Work For Food' sign.  Can you spare me the money to buy one?"),false,false);
					break;
				case 6:
					CMLib.commands().postSay(mob,mob2,L("Spread a little joy to an old fogie?"),false,false);
					break;
				case 7:
					CMLib.commands().postSay(mob,mob2,L("Change?"),false,false);
					break;
				case 8:
					CMLib.commands().postSay(mob,mob2,L("Can you spare a little change?"),false,false);
					break;
				case 9:
					CMLib.commands().postSay(mob,mob2,L("Can you spare a little gold?"),false,false);
					break;
				case 10:
					CMLib.commands().postSay(mob,mob2,L("Gold piece for a poor fogie down on @x1 luck?",mob.charStats().hisher()),false,false);
					break;
				}
				mobsHitUp.addElement(mob2);
				break;
			}
		}
		if(mobsHitUp.size()>0)
			mobsHitUp.removeElementAt(0);
		return true;
	}
}
