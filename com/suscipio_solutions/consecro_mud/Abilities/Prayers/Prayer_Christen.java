package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.CagedAnimal;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ChannelsLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_Christen extends Prayer
{
	@Override public String ID() { return "Prayer_Christen"; }
	private final static String localizedName = CMLib.lang().L("Christen");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public long flags(){return Ability.FLAG_HOLY;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
			if(mob.isMonster())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.isInCombat())
		{
			mob.tell(L("Not while you're fighting!"));
			return false;
		}
		if(commands.size()<2)
		{
			mob.tell(L("Christen whom what?"));
			return false;
		}
		String name=((String)commands.lastElement()).trim();
		commands.removeElementAt(commands.size()-1);
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;

		if((!(target instanceof CagedAnimal))||(target.phyStats().ability()<=0)||(!target.isGeneric()))
		{
			mob.tell(L("You may only christen a child."));
			return false;
		}
		if(name.length()==0)
		{
			mob.tell(L("Christen @x1 what?",target.name(mob)));
			return false;
		}
		if(name.indexOf(' ')>=0)
		{
			mob.tell(L("The name may not have a space in it."));
			return false;
		}

		name=CMStrings.capitalizeAndLower(name);

		if(CMLib.players().playerExists(name))
		{
			mob.tell(L("That name is already taken.  Please choose another."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> becomes @x1.",name):L("^S<S-NAME> christen(s) <T-NAMESELF> '@x1'.^?",name));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				String oldName=target.Name();
				target.setName(name);
				target.setDisplayText(L("@x1 is here.",name));
				String txt=((CagedAnimal)target).cageText();
				int startNameX=txt.indexOf("<NAME>");
				int endNameX=(startNameX>=0)?txt.indexOf("</NAME>",startNameX):-1;
				if(endNameX>startNameX)
					oldName=txt.substring(startNameX+6,endNameX);
				txt=CMStrings.replaceFirst(txt,"<NAME>"+oldName+"</NAME>","<NAME>"+name+"</NAME>");
				txt=CMStrings.replaceFirst(txt,"<DISP>"+oldName,"<DISP>"+name);
				((CagedAnimal)target).setCageText(txt);
				final List<String> channels=CMLib.channels().getFlaggedChannelNames(ChannelsLibrary.ChannelFlag.CHRISTENINGS);
				for(int i=0;i<channels.size();i++)
					CMLib.commands().postChannel(channels.get(i),mob.clans(),target.name()+" was just christened.",true);
				CMLib.leveler().postExperience(mob,null,null,5,false);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 over <T-NAME>, but lose(s) <S-HIS-HER> concentration.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
