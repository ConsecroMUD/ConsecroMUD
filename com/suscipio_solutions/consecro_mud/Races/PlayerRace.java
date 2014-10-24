package com.suscipio_solutions.consecro_mud.Races;


public class PlayerRace extends Human
{
	@Override public String ID(){ return "PlayerRace"; }
	@Override public String name(){ return "PlayerRace"; }
	@Override public String[] culturalAbilityNames(){return null;}
	@Override public int[] culturalAbilityProficiencies(){return null;}
	@Override public int availabilityCode(){return 0;}

	public PlayerRace()
	{
		super();
	}

}
