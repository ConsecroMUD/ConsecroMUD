package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Language;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.DVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_LangTranslator extends Property implements Language
{
	@Override public String ID() { return "Prop_LangTranslator"; }
	@Override public String name(){return "Language Translator";}
	@Override public String writtenName(){return "Language Translator";}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS|CAN_ITEMS|CAN_ROOMS;}
	protected DVector langs=new DVector(2);

	@Override
	public String accountForYourself()
	{ return "Translates spoken language";	}

	@Override
	public void setMiscText(String text)
	{
		super.setMiscText(text);
		final Vector<String> V=CMParms.parse(text);
		langs.clear();
		int lastpct=100;
		for(int v=0;v<V.size();v++)
		{
			String s=V.elementAt(v);
			if(s.endsWith("%")) s=s.substring(0,s.length()-1);
			if(CMath.isNumber(s))
				lastpct=CMath.s_int(s);
			else
			{
				final Ability A=CMClass.getAbility(s);
				if(A!=null) langs.addElement(A.ID(),Integer.valueOf(lastpct));
			}
		}
	}

	@Override
	public List<String> languagesSupported()
	{
		return langs.getDimensionVector(1);
	}
	@Override
	public boolean translatesLanguage(String language)
	{
		return langs.containsIgnoreCase(language);
	}
	@Override
	public int getProficiency(String language)
	{
		for(int i=0;i<langs.size();i++)
			if(((String)langs.elementAt(i,1)).equalsIgnoreCase(language))
				return ((Integer)langs.elementAt(i,2)).intValue();
		return 0;
	}
	@Override public boolean beingSpoken(String language) { return true; }
	@Override public void setBeingSpoken(String language, boolean beingSpoken) {}
	@Override public Map<String, String> translationHash(String language) { return new Hashtable();}
	@Override public List<String[]> translationVector(String language) { return new Vector();}
	@Override public String translate(String language, String word) { return word;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(msg.tool() instanceof Ability)
		{
			if(text().length()>0)
			{
				final int t=langs.indexOf(msg.tool().ID());
				if(t<0) return;
				final Integer I=(Integer)langs.elementAt(t,2);
				if(CMLib.dice().rollPercentage()>I.intValue())
					return;
			}
			if((msg.tool().ID().equals("Fighter_SmokeSignals"))
			&&(msg.sourceMinor()==CMMsg.NO_EFFECT)
			&&(msg.targetMinor()==CMMsg.NO_EFFECT)
			&&(msg.othersMessage()!=null))
				CMLib.commands().postSay(msg.source(),null,L("The smoke signals seem to say '@x1'.",msg.othersMessage()),false,false);
			else
			if(((msg.sourceMinor()==CMMsg.TYP_SPEAK)
			   ||(msg.sourceMinor()==CMMsg.TYP_TELL)
			   ||(msg.sourceMinor()==CMMsg.TYP_ORDER)
			   ||(CMath.bset(msg.sourceMajor(),CMMsg.MASK_CHANNEL)))
			&&(msg.sourceMessage()!=null)
			&&((((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_LANGUAGE))
			{
				final String str=CMStrings.getSayFromMessage(msg.sourceMessage());
				if(str!=null)
				{
					Environmental target=null;
					final String sourceName = affected.name();
					if(msg.target() instanceof MOB)
						target=msg.target();
					if(CMath.bset(msg.sourceMajor(),CMMsg.MASK_CHANNEL))
						msg.addTrailerMsg(CMClass.getMsg(msg.source(),null,null,CMMsg.MSG_NOISE|CMMsg.MASK_ALWAYS,L("@x1 say(s) '@x2 said \"@x3\" in @x4'",sourceName,msg.source().name(),str,msg.tool().name())));
					else
					if((target==null)&&(msg.targetMessage()!=null))
						msg.addTrailerMsg(CMClass.getMsg(msg.source(),null,null,CMMsg.MSG_NOISE|CMMsg.MASK_ALWAYS,L("@x1 say(s) '@x2 said \"@x3\" in @x4'",sourceName,msg.source().name(),str,msg.tool().name())));
					else
					if(msg.othersMessage()!=null)
						msg.addTrailerMsg(CMClass.getMsg(msg.source(),target,null,CMMsg.MSG_NOISE|CMMsg.MASK_ALWAYS,L("@x1 say(s) '@x2 said \"@x3\" in @x4'",sourceName,msg.source().name(),str,msg.tool().name())));
				}
			}
		}
	}
}
