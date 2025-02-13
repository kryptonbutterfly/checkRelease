package kryptonbutterfly.checkRelease;

import kryptonbutterfly.checkRelease.data.ReleaseInfo;

public interface ICheckReleaseState
{
	public long lastQuery();
	
	public void lastQuery(long last);
	
	public ICadence cadence();
	
	/**
	 * @return The latest previously queried version
	 */
	public ReleaseInfo latestVersion();
	
	public void latestVersion(ReleaseInfo release);
	
	public long currentTimeMillis();
}
