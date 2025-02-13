package kryptonbutterfly.checkRelease;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kryptonbutterfly.checkRelease.data.ReleaseInfo;

public class Checker
{
	private static final long MAX_QUERY_FREQUENCY = 60_000L;
	
	private static final String githubApiRequestTemplate = "https://api.github.com/repos/%s/%s/releases";
	
	private final Gson gson = new GsonBuilder()
		.setDateFormat(ReleaseInfo.dateFormatPattern)
		.setPrettyPrinting()
		.create();
	
	private long lastQuery = Long.MIN_VALUE;
	
	private final String				githubRestUrl;
	private final ICheckReleaseState	state;
	
	public Checker(String owner, String repo, ICheckReleaseState state)
	{
		this.githubRestUrl	= githubApiRequestTemplate.formatted(owner, repo);
		this.state			= state;
	}
	
	public ReleaseInfo latestRelease(boolean force)
		throws IOException,
		InterruptedException
	{
		final var next = state.cadence().nextSchedule(state);
		if (!force && next == null)
			return state.latestVersion();
		final long currentTime = state.currentTimeMillis();
		if (!force && next >= currentTime)
			return state.latestVersion();
		
		final var latest = Arrays.stream(getAllReleases())
			.filter(r -> !(r.draft || r.prerelease))
			.sorted((l, r) -> r.published_at.compareTo(l.published_at))
			.findFirst();
		latest.ifPresent(r -> {
			state.latestVersion(r);
			state.lastQuery(currentTime);
		});
		return latest.orElse(state.latestVersion());
	}
	
	private ReleaseInfo[] getAllReleases() throws IOException, InterruptedException
	{
		if (lastQuery + MAX_QUERY_FREQUENCY > state.currentTimeMillis())
			return new ReleaseInfo[0];
		lastQuery = state.currentTimeMillis();
		
		final var	client		= HttpClient.newHttpClient();
		final var	request		= HttpRequest.newBuilder()
			.uri(URI.create(githubRestUrl))
			.GET()
			.build();
		final var	response	= client.send(request, HttpResponse.BodyHandlers.ofString());
		return gson.fromJson(response.body(), ReleaseInfo[].class);
	}
	
	public static SemVer getLatestFromPomProperties(String groupId, String artifactId)
		throws IOException,
		IllegalStateException,
		IllegalArgumentException,
		NumberFormatException
	{
		final String PROPERTY_VERSION_KEY = "version";
		
		final var resourcePath = "/META-INF/maven/%s/%s/pom.properties".formatted(groupId, artifactId);
		try (final var iStream = Checker.class.getResourceAsStream(resourcePath))
		{
			if (iStream == null)
				throw new IllegalStateException(
					"Unable to get version from resource '%s'. Resource not found.".formatted(resourcePath));
			final var pom = new Properties();
			pom.load(iStream);
			if (!pom.containsKey(PROPERTY_VERSION_KEY))
				throw new IllegalStateException("Missing property '%s'.".formatted(PROPERTY_VERSION_KEY));
			
			final var value = pom.getProperty(PROPERTY_VERSION_KEY);
			return SemVer.fromGitTag(value);
		}
	}
}
