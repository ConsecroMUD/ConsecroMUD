package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;

public interface JournalsLibrary extends CMLibrary
{
	public HashSet<String> getImmortalJournalNames();
	public boolean isImmortalJournalName(String journal);

	public int loadCommandJournals(String list);
	public Enumeration<CommandJournal> commandJournals();
	public CommandJournal getCommandJournal(String named);
	public int getNumCommandJournals();
	public String getScriptValue(MOB mob, String journal, String oldValue);

	public boolean canReadMessage(JournalEntry entry, String srchMatch, MOB readerM, boolean ignorePrivileges);
	public int loadForumJournals(String list);
	public Enumeration<ForumJournal> forumJournals();
	public ForumJournal getForumJournal(String named);
	public ForumJournal getForumJournal(String named, Clan clan);
	public int getNumForumJournals();

	public void registerClanForum(Clan clan, String allClanForumMappings);
	public List<ForumJournal> getClanForums(Clan clan);

	public boolean subscribeToJournal(String journalName, String userName, boolean saveMailingList);
	public boolean unsubscribeFromJournal(String journalName, String userName, boolean saveMailingList);

	public JournalSummaryStats getJournalStats(ForumJournal journal);
	public void clearJournalSummaryStats(ForumJournal journal);


	public enum MsgMkrResolution { SAVEFILE, CANCELFILE }

	public MsgMkrResolution makeMessage(final MOB mob, final String messageTitle, final List<String> vbuf, boolean autoAdd) throws IOException;

	public static final String JOURNAL_BOUNDARY="%0D^w---------------------------------------------^N%0D";

	public static class JournalSummaryStats
	{
		public String name = "";
		public int threads =0;
		public int posts = 0;
		public String imagePath = "";
		public String shortIntro = "";
		public String longIntro = "";
		public String introKey = "";
		public String latestKey = "";
		public List<String> stuckyKeys=null;
	}

	public static class JournalEntry implements Comparable<JournalEntry>, Cloneable
	{
		public String key=null;
		public String from;
		public String to;
		public String subj;
		public String msg;
		public long date=0;
		public long update=0;
		public String parent="";
		public long attributes=0;
		public String data="";
		public int cardinal=0;
		public String msgIcon="";
		public int replies=0;
		public int views=0;
		public boolean isLastEntry=false;
		@Override
		public int compareTo(JournalEntry o)
		{
			if(date < o.date) return -1;
			if(date > o.date) return 1;
			return 0;
		}
		public StringBuffer derivedBuildMessage=null;
		public JournalEntry copyOf() { try{ return (JournalEntry)this.clone(); } catch(final Exception e){ return new JournalEntry();} }

		public final static long ATTRIBUTE_STUCKY=2;
		public final static long ATTRIBUTE_PROTECTED=1;

	}

	public static class CommandJournal
	{
		private String name="";
		private String mask="";
		private Hashtable<CommandJournalFlags,String> flags=new Hashtable<CommandJournalFlags,String>(1);

		public CommandJournal(String name, String mask, Hashtable<CommandJournalFlags,String> flags)
		{
			this.name=name;
			this.mask=mask;
			this.flags=flags;
		}
		public String NAME(){return name;}
		public String mask(){return mask;}
		public String JOURNAL_NAME(){ return "SYSTEM_"+NAME().toUpperCase().trim()+"S";}
		public String getFlag(CommandJournalFlags flag){return flags.get(flag);}
		public String getScriptFilename(){return flags.get(CommandJournalFlags.SCRIPT);}
	}

	public static enum CommandJournalFlags {
		CHANNEL,ADDROOM,EXPIRE,ADMINECHO,CONFIRM,SCRIPT;
	}

	public static class SMTPJournal
	{
		public final String  name;
		public final boolean forward;
		public final boolean subscribeOnly;
		public final boolean keepAll;
		public final String  criteriaStr;
		public final MaskingLibrary.CompiledZapperMask criteria;
		public SMTPJournal(String name, boolean forward, boolean subscribeOnly, boolean keepAll, String criteriaStr)
		{
			this.name=name; this.forward=forward; this.subscribeOnly=subscribeOnly; this.keepAll=keepAll; this.criteriaStr=criteriaStr;
			if(criteriaStr.trim().length()==0)
				this.criteria=null;
			else
				this.criteria=CMLib.masking().maskCompile(criteriaStr);
		}
	}

	public static class ForumJournal
	{
		private String name="";
		private String readMask="";
		private String postMask="";
		private String replyMask="";
		private String adminMask="";
		private Hashtable<ForumJournalFlags,String> flags=new Hashtable<ForumJournalFlags,String>(1);

		public ForumJournal(String name, Hashtable<ForumJournalFlags,String> flags)
		{
			this.name=name;
			String mask;

			mask=flags.remove(ForumJournalFlags.READ);
			this.readMask=(mask != null)?mask.trim():"";
			mask=flags.remove(ForumJournalFlags.POST);
			this.postMask=(mask != null)?mask.trim():"";
			mask=flags.remove(ForumJournalFlags.REPLY);
			this.replyMask=(mask != null)?mask.trim():"";
			mask=flags.remove(ForumJournalFlags.ADMIN);
			this.adminMask=(mask != null)?mask.trim():"";
			this.flags=flags;
		}
		public String NAME(){return name;}
		public String readMask(){return readMask;}
		public String postMask(){return postMask;}
		public String replyMask(){return replyMask;}
		public String adminMask(){return adminMask;}
		public String getFlag(CommandJournalFlags flag){return flags.get(flag);}
		public boolean maskCheck(MOB M, String mask)
		{
			if(mask.length()>0)
			{
				if(M==null) return false;
				return CMLib.masking().maskCheck(mask, M, true);
			}
			return true;
		}
		public boolean authorizationCheck(MOB M, ForumJournalFlags fl)
		{
			if(!maskCheck(M,readMask))
				return false;
			if(fl==ForumJournalFlags.READ)
				return true;
			if(fl==ForumJournalFlags.POST)
				return maskCheck(M,postMask);
			else
			if(fl==ForumJournalFlags.REPLY)
				return maskCheck(M,replyMask);
			else
			if(fl==ForumJournalFlags.ADMIN)
				return maskCheck(M,adminMask);
			return false;
		}
	}

	public static enum ForumJournalFlags {
		EXPIRE,READ,POST,REPLY,ADMIN;
	}

}
