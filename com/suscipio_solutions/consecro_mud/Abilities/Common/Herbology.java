package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Herbology extends CommonSkill
{
	@Override public String ID() { return "Herbology"; }
	private final static String localizedName = CMLib.lang().L("Herbology");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"HERBOLOGY"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode() {   return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_NATURELORE; }
	public String parametersFormat(){ return "HERB_NAME";}

	protected Item found=null;
	protected boolean messedUp=false;
	@Override protected boolean canBeDoneSittingDown() { return true; }

	public Herbology()
	{
		super();
		displayText=L("You are evaluating...");
		verb=L("evaluating");
	}

	@Override
	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if((affected!=null)&&(affected instanceof MOB)&&(!aborted)&&(!helping))
			{
				final MOB mob=(MOB)affected;
				if(messedUp)
					commonTell(mob,L("You lose your concentration on @x1.",found.name()));
				else
				{
					final List<String> herbList=Resources.getFileLineVector(Resources.getFileResource("skills/herbology.txt",true));
					String herb=null;
					while((herbList.size()>2)&&((herb==null)||(herb.trim().length()==0)))
						herb=herbList.get(CMLib.dice().roll(1,herbList.size(),-1)).trim().toLowerCase();

					if(found.rawSecretIdentity().length()>0)
					{
						herb=found.rawSecretIdentity();
						found.setSecretIdentity("");
					}

					commonTell(mob,L("@x1 appears to be @x2.",found.name(),herb));
					String name=found.Name();
					name=name.substring(0,name.length()-5).trim();
					if(name.length()>0)
						found.setName(name+" "+herb);
					else
						found.setName(L("some @x1",herb));
					found.setDisplayText(L("@x1 is here",found.Name()));
					found.setDescription("");
					found.text();
				}
			}
		}
		super.unInvoke();
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(super.checkStop(mob, commands))
			return true;
		if(commands.size()<1)
		{
			commonTell(mob,L("You must specify what herb you want to identify."));
			return false;
		}
		final Item target=mob.fetchItem(null,Wearable.FILTER_UNWORNONLY,CMParms.combine(commands,0));
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			commonTell(mob,L("You don't seem to have a '@x1'.",((String)commands.firstElement())));
			return false;
		}
		commands.remove(commands.firstElement());

		if((target.material()!=RawMaterial.RESOURCE_HERBS)
		||((!target.Name().toUpperCase().endsWith(" HERBS"))
		   &&(!target.Name().equalsIgnoreCase("herbs")))
		||(!(target instanceof RawMaterial))
		||(!target.isGeneric()))
		{
			commonTell(mob,L("You can only identify unknown herbs."));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		verb=L("studying @x1",target.name());
		displayText=L("You are @x1",verb);
		found=target;
		messedUp=false;
		if(!proficiencyCheck(mob,0,auto)) messedUp=true;
		final int duration=getDuration(15,mob,1,2);
		final CMMsg msg=CMClass.getMsg(mob,null,this,getActivityMessageType(),L("<S-NAME> stud(ys) @x1.",target.name()));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,mob,asLevel,duration);
		}
		return true;
	}
}
