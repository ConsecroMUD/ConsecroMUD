package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_DisassembleTrap extends ThiefSkill
{
	@Override public String ID() { return "Thief_DisassembleTrap"; }
	private final static String localizedName = CMLib.lang().L("Disassemble Traps");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS|Ability.CAN_EXITS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"DISTRAP","DISASSEMBLETRAPS"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	public Environmental lastChecked=null;
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_DETRAP;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	public Vector lastDone=new Vector();

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Ability A=mob.fetchAbility("Thief_RemoveTraps");
		final Hashtable traps=new Hashtable();
		if(A==null)
		{
			mob.tell(L("You don't know how to remove traps."));
			return false;
		}

		final Vector cmds=new XVector(commands);
		cmds.addElement(new Boolean(true));
		final CharState oldState=(CharState)mob.curState().copyOf();
		final boolean worked=A.invoke(mob,cmds,givenTarget,auto,asLevel);
		oldState.copyInto(mob.curState());
		if(!worked) return false;
		for(int c=0;c<cmds.size();c++)
			if(cmds.elementAt(c) instanceof Trap)
			{
				final Trap T=(Trap)cmds.elementAt(c);
				if(!traps.containsKey(T.ID()))
					traps.put(T.ID(),T);
			}
		if(traps.size()==0)
		{
			mob.tell(L("Your attempt was unsuccessful."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		final Trap T=(Trap)traps.elements().nextElement();
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,T,this,auto?CMMsg.MSG_OK_ACTION:CMMsg.MSG_DELICATE_HANDS_ACT,
													 CMMsg.MSG_DELICATE_HANDS_ACT,
													 CMMsg.MSG_OK_ACTION,
												auto?L("@x1 begins to glow.",T.name()):
													L("<S-NAME> attempt(s) to safely dissassemble the @x1 trap.",T.name()));
			final Room R=mob.location();
			if(R.okMessage(mob,msg))
			{
				R.send(mob,msg);
				final List<Item> components=T.getTrapComponents();
				if(components.size()==0)
				{
					mob.tell(L("You don't end up with any usable components."));
				}
				else
				{
					for(int i=0;i<components.size();i++)
					{
						final Item I=components.get(i);
						I.text();
						I.recoverPhyStats();
						R.addItem(I,ItemPossessor.Expire.Resource);
					}
					R.recoverRoomStats();
					for(int i=0;i<components.size();i++)
					{
						final Item I=components.get(i);
						if(R.isContent(I))
							if(!CMLib.commands().postGet(mob,null,I,true))
								break;
					}
					R.recoverRoomStats();
				}
			}
		}
		else
			beneficialVisualFizzle(mob,T,L("<S-NAME> attempt(s) to disassemble the <T-NAME> trap, but fail(s)."));

		return success;
	}
}
