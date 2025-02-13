package kryptonbutterfly.checkRelease;

public interface ICadence
{
	public Long nextSchedule(ICheckReleaseState settings);
}