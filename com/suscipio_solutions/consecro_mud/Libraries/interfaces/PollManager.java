package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.Iterator;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Poll;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;

public interface PollManager extends CMLibrary
{
	public void addPoll(Poll P);
	public void removePoll(Poll P);
	public Poll getPoll(String named);
	public Poll getPoll(int x);
	public List<Poll>[] getMyPollTypes(MOB mob, boolean login);
	public Iterator<Poll> getPollList();
	public void processVote(Poll P, MOB mob);
	public void modifyVote(Poll P, MOB mob) throws java.io.IOException;
	public void processResults(Poll P, MOB mob);

	public void createPoll(Poll P);
	public void updatePollResults(Poll P);
	public void updatePoll(String oldName, Poll P);
	public void deletePoll(Poll P);
	public Poll loadPollByName(String name);
	public boolean loadPollIfNecessary(Poll P);
}
