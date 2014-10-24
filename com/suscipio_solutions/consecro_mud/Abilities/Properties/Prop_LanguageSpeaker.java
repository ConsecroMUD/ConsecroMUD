package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Language;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_LanguageSpeaker extends Property
{
	@Override public String ID() { return "Prop_LanguageSpeaker"; }
	@Override public String name(){ return "Forces language speaking";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_MOBS|Ability.CAN_ITEMS;}
	protected boolean doPlayers=false;
	protected boolean noMobs=false;
	protected boolean homeOnly=false;
	protected MaskingLibrary.CompiledZapperMask mobMask = null;
	protected Language lang = null;
	protected String langStr = "";

	private final Room homeRoom = null;
	private final Area homeArea = null;
	private CMClass.CMObjectType affectedType = CMClass.CMObjectType.AREA;

	@Override
	public void setMiscText(String txt)
	{

		doPlayers=CMParms.getParmBool(txt,"PLAYERS",false);
		noMobs=CMParms.getParmBool(txt,"NOMOBS",false);
		homeOnly=CMParms.getParmBool(txt,"HOMEONLY",false);
		langStr=CMParms.getParmStr(txt,"LANGUAGE","").trim();
		final int x=txt.indexOf(';');
		mobMask=null;
		if((x>=0)&&(txt.substring(x+1).trim().length()>0))
			mobMask=CMLib.masking().getPreCompiledMask(txt.substring(x+1).trim());
		lang=null;
		super.setMiscText(txt);
	}

	@Override
	public void setAffectedOne(Physical P)
	{
		affectedType = CMClass.getType(P);
		super.setAffectedOne(P);
	}

	public Language getLanguage()
	{
		if((lang == null)&&(langStr.trim().length()>0))
		{
			lang=(Language)CMClass.getAbility(langStr.trim());
			langStr="";
		}
		return lang;
	}

	@Override
	public String accountForYourself()
	{
		return "Forces speaking the language: "+((lang!=null)?lang.name():"?");
	}

	public void startSpeaking(MOB mob)
	{
		final Room mobHomeRoom=mob.getStartRoom();
		final Area mobHomeArea=((mobHomeRoom==null)?null:mobHomeRoom.getArea());
		if(((lang!=null)||(langStr.length()>0))
		&&(doPlayers || mob.isMonster())
		&&((!noMobs) || (!mob.isMonster()))
		&&((!homeOnly) || (homeRoom==null) || (mobHomeRoom == homeRoom))
		&&((!homeOnly) || (homeArea==null) || (mobHomeArea == homeArea))
		&&(mob.fetchEffect(langStr)==null)
		&&((mobMask==null) || CMLib.masking().maskCheck(mobMask,mob,true)))
		{
			if(lang == null)
				lang = getLanguage();
			if(lang == null)
			{
				lang=(Language)CMClass.getAbility("Common");
				Log.errOut("Prop_LanguageSpeaker","Unknown language "+langStr);
			}
			if(lang != null)
			{
				switch(affectedType)
				{
				case AREA:
					lang=(Language)lang.copyOf();
					break;
				case LOCALE:
					lang=(Language)lang.copyOf();
					break;
				case MOB:
					break;
				case EXIT:
					lang=(Language)lang.copyOf();
					break;
				default: // item
					break;
				}
				mob.addNonUninvokableEffect(lang);
				lang.setSavable(false);
				lang.invoke(mob,mob,false,0);
			}
		}
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(affected!=null)
			switch(affectedType)
			{
			case AREA:
			{
				if(msg.targetMinor()==CMMsg.TYP_ENTER)
					startSpeaking(msg.source());
				else
				if(msg.sourceMinor()==CMMsg.TYP_LIFE)
					startSpeaking(msg.source());
				break;
			}
			case LOCALE:
			{
				if((msg.target() == affected)
				&&(msg.targetMinor()==CMMsg.TYP_ENTER))
					startSpeaking(msg.source());
				else
				if(msg.sourceMinor()==CMMsg.TYP_LIFE)
					startSpeaking(msg.source());
				break;
			}
			case MOB:
			{
				if(lang==null)
					startSpeaking((MOB)affected);
				break;
			}
			case EXIT:
			{
				if((msg.targetMinor()==CMMsg.TYP_ENTER)
				&&(msg.tool()==affected))
					startSpeaking(msg.source());
				break;
			}
			default: // item
			{
				if((msg.target() == affected)
				&&(msg.targetMinor()==CMMsg.TYP_GET)
				&&((lang==null)||(lang.affecting()!=msg.source())))
				{
					if((lang!=null)&&(lang.affecting()!=null))
						lang.affecting().delEffect(lang);
					startSpeaking(msg.source());
				}
				else
				if((msg.target() == affected)
				&&(msg.targetMinor()==CMMsg.TYP_DROP)
				&&(lang!=null)
				&&(lang.affecting()!=null))
				{
					lang.affecting().delEffect(lang);
					lang.setAffectedOne(null);
				}
				break;
			}
		}
		super.executeMsg(myHost,msg);
	}
}
