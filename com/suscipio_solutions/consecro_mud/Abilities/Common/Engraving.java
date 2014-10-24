package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.Set;
import java.util.TreeSet;
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
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Engraving extends CommonSkill
{
	@Override public String ID() { return "Engraving"; }
	private final static String localizedName = CMLib.lang().L("Engraving");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"ENGRAVE","ENGRAVING"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode() {   return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_CALLIGRAPHY; }

	protected Item found=null;
	protected String writing="";
	@Override protected boolean canBeDoneSittingDown() { return true; }

	public Engraving()
	{
		super();
		displayText=L("You are engraving...");
		verb=L("engraving");
	}

	@Override
	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if((affected!=null)&&(affected instanceof MOB)&&(!aborted)&&(!helping))
			{
				final MOB mob=(MOB)affected;
				if(writing.length()==0)
					commonTell(mob,L("You mess up your engraving."));
				else
				{
					String desc=found.description();
					final int x=desc.indexOf(" Engraved on it are the words `");
					final int y=desc.lastIndexOf('`');
					if((x>=0)&&(y>x))
						desc=desc.substring(0,x);
					found.setDescription(L("@x1 Engraved on it are the words `@x2`.",desc,writing));
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
		if(commands.size()<2)
		{
			commonTell(mob,L("You must specify what you want to engrave onto, and what words to engrave on it."));
			return false;
		}
		Item target=mob.fetchItem(null,Wearable.FILTER_UNWORNONLY,(String)commands.firstElement());
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			target=mob.location().findItem(null, (String)commands.firstElement());
			if((target!=null)&&(CMLib.flags().canBeSeenBy(target,mob)))
			{
				final Set<MOB> followers=mob.getGroupMembers(new TreeSet<MOB>());
				boolean ok=false;
				for(final MOB M : followers)
				{
					if(target.secretIdentity().indexOf(getBrand(M))>=0)
						ok=true;
				}
				if(!ok)
				{
					commonTell(mob,L("You aren't allowed to work on '@x1'.",((String)commands.firstElement())));
					return false;
				}
			}
		}
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			commonTell(mob,L("You don't seem to have a '@x1'.",((String)commands.firstElement())));
			return false;
		}
		commands.remove(commands.firstElement());

		final Ability write=mob.fetchAbility("Skill_Write");
		if(write==null)
		{
			commonTell(mob,L("You must know how to write to engrave."));
			return false;
		}

		if((((target.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_GLASS)
			&&((target.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_METAL)
			&&((target.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_ROCK)
			&&((target.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_PRECIOUS)
			&&((target.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_SYNTHETIC)
			&&((target.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_WOODEN)
			&&((target.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_MITHRIL))
		||(!target.isGeneric()))
		{
			commonTell(mob,L("You can't engrave onto that material."));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		writing=CMParms.combine(commands,0);
		verb=L("engraving on @x1",target.name());
		displayText=L("You are @x1",verb);
		found=target;
		if((!proficiencyCheck(mob,0,auto))||(!write.proficiencyCheck(mob,0,auto)))
			writing="";
		final int duration=getDuration(30,mob,1,3);
		final CMMsg msg=CMClass.getMsg(mob,target,this,getActivityMessageType(),L("<S-NAME> start(s) engraving on <T-NAME>."));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,mob,asLevel,duration);
		}
		return true;
	}
}
