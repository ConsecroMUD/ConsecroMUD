package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Listen extends ThiefSkill
{
	@Override public String ID() { return "Thief_Listen"; }
	private final static String localizedName = CMLib.lang().L("Listen");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return Ability.CAN_ROOMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_ALERT;}
	private static final String[] triggerStrings =I(new String[] {"LISTEN"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	protected Room sourceRoom=null;
	protected Room room=null;
	protected String lastSaid="";

	protected MOB getInvisibleMOB()
	{
		final MOB mrInvisible=CMClass.getFactoryMOB();
		mrInvisible.setName(L("Someone"));
		mrInvisible.basePhyStats().setDisposition(mrInvisible.basePhyStats().disposition()|PhyStats.IS_NOT_SEEN);
		mrInvisible.phyStats().setDisposition(mrInvisible.phyStats().disposition()|PhyStats.IS_NOT_SEEN);
		return mrInvisible;
	}

	protected Item getInvisibleItem()
	{
		final Item mrInvisible=CMClass.getItem("StdItem");
		mrInvisible.setName(L("Something"));
		mrInvisible.basePhyStats().setDisposition(mrInvisible.basePhyStats().disposition()|PhyStats.IS_NOT_SEEN);
		mrInvisible.phyStats().setDisposition(mrInvisible.phyStats().disposition()|PhyStats.IS_NOT_SEEN);
		return mrInvisible;
	}

	protected Environmental[] makeTalkers(MOB s, Environmental p, Environmental t)
	{
		final Environmental[] Ms=new Environmental[]{s,p,t};
		Ms[0]=getInvisibleMOB();
		if(p instanceof MOB)
		{
			if(p==s)
				Ms[1]=Ms[0];
			else
				Ms[1]=getInvisibleMOB();
		}
		else
		if(p!=null)
		{
			Ms[1]=getInvisibleItem();
		}
		if(t instanceof MOB)
		{
			if(p==s)
				Ms[2]=Ms[0];
			else
			if(p==s)
				Ms[2]=Ms[0];
			else
				Ms[2]=getInvisibleMOB();
		}
		else
		if(t!=null)
		{
			Ms[2]=getInvisibleItem();
		}
		return Ms;
	}

	public void cleanTalkers(Environmental[] Ps)
	{
		for(final Environmental P : Ps)
			if(P!=null)
				P.destroy();
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected instanceof Room)
		&&(invoker()!=null)
		&&(invoker().location()!=null)
		&&(sourceRoom!=null)
		&&(!invoker().isInCombat())
		&&(invoker().location()==sourceRoom))
		{
			if(invoker().location()==room)
			{
				if((msg.sourceMinor()==CMMsg.TYP_SPEAK)
				&&(msg.othersCode()==CMMsg.NO_EFFECT)
				&&(msg.othersMessage()==null)
				&&(msg.sourceMessage()!=null)
				&&(!msg.amISource(invoker()))
				&&(!msg.amITarget(invoker()))
				&&(!lastSaid.equals(msg.sourceMessage())))
				{
					lastSaid=msg.sourceMessage();
					if((invoker().phyStats().level()+(getXLEVELLevel(invoker())*10))>msg.source().phyStats().level())
						invoker().tell(msg.source(),msg.target(),msg.tool(),msg.sourceMessage());
					else
						invoker().tell(msg.source(),null,null,L("<S-NAME> said something, but you couldn't quite make it out."));
				}
			}
			else
			if((msg.sourceMinor()==CMMsg.TYP_SPEAK)
			&&(msg.othersMinor()==CMMsg.TYP_SPEAK)
			&&(msg.othersMessage()!=null)
			&&(msg.sourceMessage()!=null)
			&&(!lastSaid.equals(msg.sourceMessage())))
			{
				lastSaid=msg.sourceMessage();
				if((invoker().phyStats().level()+(getXLEVELLevel(invoker())*10))>msg.source().phyStats().level())
				{
					final Environmental[] Ps=makeTalkers(msg.source(),msg.target(),msg.tool());
					invoker().tell((MOB)Ps[0],Ps[1],Ps[2],msg.othersMessage());
					this.cleanTalkers(Ps);
				}
				else
					invoker().tell(msg.source(),null,null,L("<S-NAME> said something, but you couldn't quite make it out."));
			}

		}
		else
			unInvoke();
	}

	@Override
	public void unInvoke()
	{
		final MOB M=invoker();
		super.unInvoke();
		if((M!=null)&&(!M.amDead()))
			M.tell(L("You stop listening."));
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final String whom=CMParms.combine(commands,0);
		final int dirCode=Directions.getGoodDirectionCode(whom);
		if(!CMLib.flags().canHear(mob))
		{
			mob.tell(L("You don't hear anything."));
			return false;
		}

		if(room!=null)
		for(final Enumeration<Ability> a=room.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A.ID().equals(ID()))&&(invoker()==mob))
				A.unInvoke();
		}
		room=null;
		if(dirCode<0)
			room=mob.location();
		else
		{
			if((mob.location().getRoomInDir(dirCode)==null)||(mob.location().getExitInDir(dirCode)==null))
			{
				mob.tell(L("Listen which direction?"));
				return false;
			}
			room=mob.location().getRoomInDir(dirCode);
			if((room.domainType()&Room.INDOORS)==0)
			{
				mob.tell(L("You can only listen indoors."));
				return false;
			}
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=false;
		final CMMsg msg=CMClass.getMsg(mob,null,this,auto?CMMsg.MSG_OK_ACTION:(CMMsg.MSG_DELICATE_SMALL_HANDS_ACT),CMMsg.MSG_OK_VISUAL,CMMsg.MSG_OK_VISUAL,L("<S-NAME> listen(s)@x1.",((dirCode<0)?"":" "+Directions.getDirectionName(dirCode))));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			success=proficiencyCheck(mob,0,auto);
			int numberHeard=0;
			int levelsHeard=0;
			for(int i=0;i<room.numInhabitants();i++)
			{
				final MOB inhab=room.fetchInhabitant(i);
				if((inhab!=null)&&(!CMLib.flags().isSneaking(inhab))&&(!CMLib.flags().isHidden(inhab))&&(inhab!=mob))
				{
					numberHeard++;
					if(inhab.phyStats().level()>(mob.phyStats().level()+(2*super.getXLEVELLevel(mob))))
						levelsHeard+=(inhab.phyStats().level()-(mob.phyStats().level()+(2*super.getXLEVELLevel(mob))));
				}
			}
			if((success)&&(numberHeard>0))
			{
				if(((proficiency()+(getXLEVELLevel(mob)*10))>(50+levelsHeard))||(room==mob.location()))
				{
					mob.tell(L("You definitely hear @x1 creature(s).",""+numberHeard));
					if(proficiency()>((room==mob.location())?50:75))
					{
						sourceRoom=mob.location();
						beneficialAffect(mob,room,asLevel,0);
					}
				}
				else
					mob.tell(L("You definitely hear something."));
			}
			else
				mob.tell(L("You don't hear anything."));
		}
		return success;
	}

}
