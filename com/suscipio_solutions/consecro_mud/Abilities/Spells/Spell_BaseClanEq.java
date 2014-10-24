package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_BaseClanEq extends Spell
{
	@Override public String ID() { return "Spell_BaseClanEq"; }
	private final static String localizedName = CMLib.lang().L("Enchant Clan Equipment Base Model");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ENCHANTMENT;}
	@Override public long flags(){return super.flags()|Ability.FLAG_CLANMAGIC;}
	@Override protected int overrideMana(){return Ability.COST_ALL;}
	protected String type="";
	@Override protected boolean disregardsArmorCheck(MOB mob){return true;}

	@Override
	public boolean canBeLearnedBy(MOB teacher, MOB student)
	{
		if(student!=null)
		{
			for(final Enumeration<Ability> a=student.allAbilities();a.hasMoreElements();)
			{
				final Ability A=a.nextElement();
				if((A!=null)&&(A instanceof Spell_BaseClanEq))
				{
					teacher.tell(L("@x1 already knows '@x2', and may not learn another clan enchantment.",student.name(),A.name()));
					student.tell(L("You may only learn a single clan enchantment."));
					return false;
				}
			}
		}
		return super.canBeLearnedBy(teacher,student);
	}
	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(type.length()==0) return false;
		if(!mob.clans().iterator().hasNext())
		{
			mob.tell(L("You aren't even a member of a clan."));
			return false;
		}
		final Pair<Clan,Integer> clanPair=CMLib.clans().findPrivilegedClan(mob, Clan.Function.ENCHANT);
		if(clanPair==null)
		{
			mob.tell(L("You are not authorized to draw from the power of your clan."));
			return false;
		}
		final Clan C=clanPair.first;
		final String ClanName=C.clanID();
		final String ClanType=C.getGovernmentName();

		// Invoking will be like:
		//   CAST [CLANEQSPELL] ITEM QUANTITY
		//   -2   -1			0    1
		if(commands.size()<1)
		{
			mob.tell(L("Enchant which spell onto what?"));
			return false;
		}
		if(commands.size()<2)
		{
			mob.tell(L("Use how much clan enchantment power?"));
			return false;
		}
		final Physical target=mob.location().fetchFromMOBRoomFavorsItems(mob,null,(String)commands.elementAt(0),Wearable.FILTER_UNWORNONLY);
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",((String)commands.elementAt(0))));
			return false;
		}
		// Add clan power check start
		final int points=CMath.s_int((String)commands.elementAt(1));
		if(points<=0)
		{
			mob.tell(L("You need to use at least 1 enchantment point."));
			return false;
		}
		final long exp=points*CMProps.getIntVar(CMProps.Int.CLANENCHCOST);
		if((C.getExp()<exp)||(exp<0))
		{
			mob.tell(L("You need @x1 to do that, but your @x2 has only @x3 experience points.",""+exp,C.getGovernmentName(),""+C.getExp()));
			return false;
		}

		// Add clan power check end
		if(target.fetchEffect("Prop_ClanEquipment")!=null)
		{
			mob.tell(L("@x1 is already clan enchanted.",target.name(mob)));
			return false;
		}

		// lose all the mana!
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		C.setExp(C.getExp()-exp);
		C.update();

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("^S<S-NAME> move(s) <S-HIS-HER> fingers around <T-NAMESELF>, encanting intensely.^?"));
			if (mob.location().okMessage(mob, msg))
			{
				mob.location().send(mob, msg);
				final Ability A=CMClass.getAbility("Prop_ClanEquipment");
				final StringBuffer str=new StringBuffer("");
				str.append(type); // Type of Enchantment
				str.append(" ");
				str.append(""+points);     // Power of Enchantment
				str.append(" \"");
				str.append(ClanName);   					   // Clan Name
				str.append("\" \"");
				str.append(ClanType);   					   // Clan Type
				str.append("\"");
				A.setMiscText(str.toString());
				target.addEffect(A);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> move(s) <S-HIS-HER> fingers around <T-NAMESELF>, encanting intensely, and looking very frustrated."));
		return success;
	}
}
