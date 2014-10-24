package com.suscipio_solutions.consecro_mud.core.interfaces;



/**
*
* A place where people might be, can be either abstract (like an area),
* or concrete (like a Room)
*/
public interface Places extends PhysicalAgent
{
	/** a constant code for {@link Places#getAtmosphereCode()} that denotes that the atmo is inherited from a parent */
	public final static int ATMOSPHERE_INHERIT = -1;

	/**
	 * Returns the resource (or -1) that represents the atmosphere of this area.
	 * Since most rooms inherit their atmosphere from the area, this is important.
	 * Return -1 to have this area inherit its atmosphere from parents (which
	 * would ultimately go back to RESOURCE_AIR)
	 * @see com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial#MATERIAL_GAS
	 * @return the RawMaterial resource, or -1
	 */
	public int getAtmosphereCode();

	/**
	 * Returns the resource (or -1) that represents the atmosphere of this area.
	 * Since most rooms inherit their atmosphere from the area, this is important.
	 * Return -1 to have this area inherit its atmosphere from parents (which
	 * would ultimately go back to RESOURCE_AIR)
	 * @see com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial#MATERIAL_GAS
	 * @return the RawMaterial resource, or -1
	 */
	public int getAtmosphere();

	/**
	 * Sets the resource (or -1) that represents the atmosphere of this area.
	 * Since most rooms inherit their atmosphere from the area, this is important.
	 * Return -1 to have this area inherit its atmosphere from parents (which
	 * would ultimately go back to RESOURCE_AIR)
	 * @see com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial#MATERIAL_GAS
	 * @param resourceCode the RawMaterial resource to use
	 */
	public void setAtmosphere(int resourceCode);

	/**
	 * Returns a bitmap of climate flags for this area which will be used to influence
	 * the weather for the area in addition to season and other factors.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#CLIMASK_COLD
	 * @return a CLIMASK bitmap
	 */
	public int getClimateTypeCode();
	/**
	 * Returns a bitmap of climate flags for this area which will be used to influence
	 * the weather for the area in addition to season and other factors.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#CLIMASK_COLD
	 * @param newClimateType a CLIMASK bitmap
	 */
	public void setClimateType(int newClimateType);

	/**
	 * Returns a bitmap of the climate for this place.  If the climate is CLIMASK_INHERIT,
	 * then it will look to parent objects, such as areas, and parent areas, until it
	 * eventually finds a non-inherit, or returns CLIMASK_NORMAL;
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#CLIMASK_COLD
	 * @return a derived climate
	 */
	public int getClimateType();


	/**	Bitmap climate flag meaning that the area has inherited weather.  @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#climateType() */
	public final static int CLIMASK_INHERIT = -1;
	/**	Bitmap climate flag meaning that the area has normal weather.  @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#climateType() */
	public final static int CLIMASK_NORMAL=0;
	/**	Bitmap climate flag meaning that the area has wet weather.  @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#climateType() */
	public final static int CLIMASK_WET=1;
	/**	Bitmap climate flag meaning that the area has cold weather.  @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#climateType() */
	public final static int CLIMASK_COLD=2;
	/**	Bitmap climate flag meaning that the area has windy weather.  @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#climateType() */
	public final static int CLIMASK_WINDY=4;
	/**	Bitmap climate flag meaning that the area has hot weather.  @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#climateType() */
	public final static int CLIMASK_HOT=8;
	/**	Bitmap climate flag meaning that the area has dry weather.  @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#climateType() */
	public final static int CLIMASK_DRY=16;
	/**	Indexed description of the CLIMASK_ bitmap constants in all possible combinations.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#CLIMASK_NORMAL
	 */
	public final static String[] CLIMATE_DESCS={"NORMAL","WET","COLD","WINDY","HOT","DRY"};
	/**	Number of CLIMASK_ constants.  @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#climateType() */
	public final static int NUM_CLIMATES=6;
	/**	Bitmap climate flag meaning that the area has all weather modifiers.  @see com.suscipio_solutions.consecro_mud.core.interfaces.Places#climateType() */
	public final static int ALL_CLIMATE_MASK=31;
}
