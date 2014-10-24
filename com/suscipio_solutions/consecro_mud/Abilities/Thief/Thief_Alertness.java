package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Alertness extends ThiefSkill
{
	@Override public String ID() { return "Thief_Alertness"; }
	private final static String localizedName = CMLib.lang().L("Alertness");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Alertness)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"ALERTNESS"});
	@Override public int classificationCode(){	return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_ALERT;}
	@Override protected boolean disregardsArmorCheck(MOB mob){return true;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	Room room=null;


	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{

			final MOB mob=(MOB)affected;
			if(!CMLib.flags().aliveAwakeMobile(mob,true))
			{ unInvoke(); return false;}
			if(mob.location()!=room)
			{
				room=mob.location();
				Vector choices=null;
				for(int i=0;i<room.numItems();i++)
				{
					final Item I=room.getItem(i);
					if((I!=null)
					&&(CMLib.flags().canBeSeenBy(I,mob))
					&&(I.displayText().length()==0))
					{
						if(choices==null) choices=new Vector();
						choices.addElement(I);
					}
				}
				if(choices!=null)
				{
					int alert=getXLEVELLevel(mob);
					if(alert<=0)alert=1;
					while((alert>0)&&(choices.size()>0))
					{
						final Item I=(Item)choices.elementAt(CMLib.dice().roll(1,choices.size(),-1));
						choices.removeElement(I);
						mob.tell(I.name(mob)+": "+I.description());
						alert--;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void unInvoke()
	{
		final MOB M=(MOB)affected;
		super.unInvoke();
		if((M!=null)&&(!M.amDead()))
			M.tell(L("You don't feel quite so alert any more."));
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already alert."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		final boolean success=proficiencyCheck(mob,0,auto);

		final CMMsg msg=CMClass.getMsg(mob,target,this,auto?CMMsg.MSG_OK_ACTION:(CMMsg.MSG_DELICATE_HANDS_ACT|CMMsg.MASK_EYES),auto?L("<T-NAME> become(s) alert."):L("<S-NAME> become(s) suddenly alert."));
		if(!success)
			return beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to look alert, but become(s) distracted."));
		else
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,target,asLevel,0);
		}
		return success;
	}
}
