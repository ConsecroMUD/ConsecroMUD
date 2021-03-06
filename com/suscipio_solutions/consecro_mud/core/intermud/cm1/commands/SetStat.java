package com.suscipio_solutions.consecro_mud.core.intermud.cm1.commands;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ExpertiseLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Modifiable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.intermud.cm1.RequestHandler;


public class SetStat extends GetStat
{
	@Override public String getCommandWord(){ return "SETSTAT";}

	public SetStat(RequestHandler req, String parameters)
	{
		super(req, parameters);
	}

	@Override
	public void run()
	{
		try
		{
			final PhysicalAgent P=req.getTarget();
			if(P==null)
			{
				req.sendMsg("[FAIL NO TARGET]");
				return;
			}
			if(!isAuthorized(req.getUser(),P))
			{
				req.sendMsg("[FAIL UNAUTHORIZED]");
				return;
			}
			String rest = "";
			String type = parameters.toUpperCase().trim();
			int x=parameters.indexOf(' ');
			if(x>0)
			{
				type=parameters.substring(0,x).toUpperCase().trim();
				rest=parameters.substring(x+1).toUpperCase().trim();
			}
			final Modifiable mod=getModifiable(type,P);
			if(mod==null)
			{
				req.sendMsg("[FAIL "+getHelp(req.getUser(), P, "")+"]");
				return;
			}
			String value="";
			String stat=rest;
			String firstValue="";
			String restValue="";
			char adjuster=' ';
			x=rest.indexOf(' ');
			if(x>0)
			{
				stat=rest.substring(0,x).toUpperCase().trim();
				value=rest.substring(x+1).toUpperCase().trim();
				firstValue=value;
				x=value.indexOf(' ');
				if(x>0)
				{
					firstValue=value.substring(0,x);
					restValue=value.substring(x+1).trim();
				}

				if((stat.length()>0)&&(!Character.isLetter(stat.charAt(0))))
				{
					adjuster=stat.charAt(0);
					stat=stat.substring(1);
				}
			}
			if((stat.length()==0)||(!isAStat(P,mod,stat)))
			{
				req.sendMsg("[FAIL USAGE: SETSTAT "+type+" "+CMParms.toStringList(getStatCodes(P,mod))+"]");
				return;
			}

			if(mod instanceof Physical)
				switch(CMParms.indexOf(PHYSSTATS, stat))
				{
					case -1: break;
					case 0:
					{
						if(adjuster=='-')
						{
							final Ability A=((Physical)mod).fetchEffect(firstValue);
							if(A!=null)
							{
								((Physical)mod).delEffect(A);
								req.sendMsg("[OK -"+A.ID()+"]");
							}
							else
								req.sendMsg("[FAIL "+firstValue+" NOT FOUND]");
						}
						else
						{
							final Ability A=CMClass.findAbility(firstValue);
							if(A==null)
								req.sendMsg("[FAIL "+firstValue+" NOT FOUND]");
							else
							if(adjuster=='+')
							{
								final MOB M=CMClass.sampleMOB();
								M.setLocation(CMLib.map().roomLocation((Physical)mod));
								try{A.invoke(M, CMParms.parse(restValue), (Physical)mod, true, 0);}catch(final Exception e){}
								M.destroy();
								req.sendMsg("[OK "+firstValue+"]");
							}
							else
							{
								A.setMiscText(restValue);
								((Physical)mod).addNonUninvokableEffect(A);
								req.sendMsg("[OK "+firstValue+"]");
							}
						}
						return;
					}
				}
			if(mod instanceof PhysicalAgent)
				switch(CMParms.indexOf(PHYASTATS, stat))
				{
					case -1: break;
					case 0:
					{
						if(adjuster=='-')
						{
							final Behavior A=((PhysicalAgent)mod).fetchBehavior(firstValue);
							if(A!=null)
							{
								((PhysicalAgent)mod).delBehavior(A);
								req.sendMsg("[OK -"+A.ID()+"]");
							}
							else
								req.sendMsg("[FAIL "+firstValue+" NOT FOUND]");
						}
						else
						{
							final Behavior A=CMClass.findBehavior(firstValue);
							if(A==null)
								req.sendMsg("[FAIL "+firstValue+" NOT FOUND]");
							else
							{
								A.setParms(restValue);
								((PhysicalAgent)mod).addBehavior(A);
							}
						}
						return;
					}
				}
			if(mod instanceof MOB)
				switch(CMParms.indexOf(MOBASTATS, stat))
				{
					case -1: break;
					case 0:
					{
						if(adjuster=='-')
						{
							final Ability A=((MOB)mod).fetchAbility(firstValue);
							if(A!=null)
							{
								((MOB)mod).delAbility(A);
								req.sendMsg("[OK -"+A.ID()+"]");
							}
							else
								req.sendMsg("[FAIL "+firstValue+" NOT FOUND]");
						}
						else
						{
							final Ability A=CMClass.findAbility(firstValue);
							if(A==null)
								req.sendMsg("[FAIL "+firstValue+" NOT FOUND]");
							else
							{
								A.setMiscText(restValue);
								((MOB)mod).addAbility(A);
								req.sendMsg("[OK "+firstValue+"]");
							}
						}
						return;
					}
					case 1:
					{
						final Faction F=CMLib.factions().getFaction(firstValue);
						if(F==null)
							req.sendMsg("[FAIL "+firstValue+" NOT EXIST]");
						else
						if(adjuster=='-')
						{
							final int f=((MOB)mod).fetchFaction(F.factionID());
							if(f<Integer.MAX_VALUE)
							{
								if(CMath.isInteger(restValue))
									((MOB)mod).addFaction(F.factionID(), f-CMath.s_int(restValue));
								else
									((MOB)mod).removeFaction(F.factionID());
								req.sendMsg("[OK -"+F.factionID()+" "+restValue+"]");
							}
							else
								req.sendMsg("[FAIL "+F.factionID()+" NOT FOUND]");
						}
						else
						{
							int def=0;
							if(CMath.isInteger(restValue))
								def+=CMath.s_int(restValue);
							((MOB)mod).addFaction(F.factionID(), def);
							req.sendMsg("[OK "+F.factionID()+" "+((MOB)mod).fetchFaction(F.factionID())+"]");
						}
						return;
					}
					case 2:
					{
						ExpertiseLibrary.ExpertiseDefinition def=CMLib.expertises().findDefinition(firstValue, true);
						if(def==null) def=CMLib.expertises().findDefinition(firstValue, false);
						if(def==null)
							req.sendMsg("[FAIL "+firstValue+" NOT EXIST]");
						else
						if(adjuster=='-')
						{
							((MOB)mod).delExpertise(def.ID);
							req.sendMsg("[OK -"+def.ID+"]");
						}
						else
						{
							((MOB)mod).addExpertise(def.ID);
							req.sendMsg("[OK "+def.ID+"]");
						}
						return;
					}
					case 3:
					{
						if(adjuster=='-')
						{
							final MOB M=((MOB)mod).fetchFollower(restValue);
							if(M==null)
								req.sendMsg("[FAIL "+restValue+" NOT FOUND]");
							else
							{
								M.setFollowing(null);
								req.sendMsg("[OK -"+M.Name()+"]");
							}
						}
						else
						{
							final Room R=CMLib.map().roomLocation((MOB)mod);
							MOB M=null;
							if(R!=null) M=R.fetchInhabitant(restValue);
							if(M!=null)
							{
								M.setFollowing((MOB)mod);
								req.sendMsg("[OK "+M.Name()+"]");
							}
							else
								req.sendMsg("[FAIL "+restValue+" NOT FOUND]");
						}
						return;
					}
				}
			if(mod instanceof ItemPossessor)
				switch(CMParms.indexOf(ITEMSTATS, stat))
				{
					case -1: break;
					case 0:
					{
						if(adjuster=='-')
						{
							final Item M=((ItemPossessor)mod).findItem(firstValue);
							if(M==null)
								req.sendMsg("[FAIL "+restValue+" NOT FOUND]");
							else
							{
								M.destroy();
								req.sendMsg("[OK -"+M.Name()+"]");
							}
						}
						else
						{
							final Room R=CMLib.map().roomLocation((Physical)mod);
							Item M=null;
							if((R!=null)&&(mod instanceof MOB)) M=R.findItem(firstValue);
							if((R!=null)&&(mod instanceof Room)) M=((MOB)mod).findItem(firstValue);
							if(M==null) M=CMClass.getItem(firstValue);
							if(M!=null)
							{
								((ItemPossessor)mod).addItem(M);
								req.sendMsg("[OK "+M.Name()+"]");
							}
							else
								req.sendMsg("[FAIL "+restValue+" NOT FOUND]");
						}
						return;
					}
				}
			if(mod instanceof Room)
				switch(CMParms.indexOf(ROOMSTATS, stat))
				{
					case -1: break;
					case 0:
					{
						if(adjuster=='-')
						{
							final MOB M=((Room)mod).fetchInhabitant(restValue);
							if(M==null)
								req.sendMsg("[FAIL "+restValue+" NOT FOUND]");
							else
							if(!M.isMonster())
								req.sendMsg("[FAIL "+M.Name()+" IS PLAYER]");
							else
							{
								M.destroy();
								req.sendMsg("[OK -"+M.Name()+"]");
							}
						}
						else
						{
							MOB M=CMLib.players().getPlayer(restValue);
							if(M==null)
								M=CMLib.map().findFirstInhabitant(((Room)mod).getArea().getMetroMap(), null, restValue, 10);
							if(M==null)
								M=CMClass.getMOB(restValue);
							if(M!=null)
							{
								((Room)mod).bringMobHere(M,true);
								req.sendMsg("[OK "+M.Name()+"]");
							}
							else
								req.sendMsg("[FAIL "+restValue+" NOT FOUND]");
						}
						return;
					}
				}

			if(!UseGenBuilder(P,mod))
				mod.setStat(stat, value);
			else
			{
				final String[] codes = this.getStatCodes(P, mod);
				for (final String code : codes)
					if(code.equalsIgnoreCase(stat))
						if(P instanceof MOB)
							CMLib.coffeeMaker().setGenMobStat((MOB)P, stat, value);
						else
						if(P instanceof Item)
							CMLib.coffeeMaker().setGenItemStat((Item)P, stat, value);

			}
			req.sendMsg("[OK]");
		}
		catch(final Exception ioe)
		{
			Log.errOut(className,ioe);
			req.close();
		}
	}
}
