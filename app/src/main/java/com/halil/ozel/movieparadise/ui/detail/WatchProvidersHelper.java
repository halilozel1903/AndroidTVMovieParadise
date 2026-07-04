package com.halil.ozel.movieparadise.ui.detail;

import com.halil.ozel.movieparadise.data.models.CountryWatchProviders;
import com.halil.ozel.movieparadise.data.models.WatchProvider;
import com.halil.ozel.movieparadise.data.models.WatchProvidersResponse;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class WatchProvidersHelper {

    private static final int MAX_PROVIDERS = 8;
    private static final String[] REGION_FALLBACK = {"TR", "US"};

    private WatchProvidersHelper() {}

    public static List<WatchProvider> pickProviders(WatchProvidersResponse response) {
        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            return List.of();
        }
        CountryWatchProviders countryData = resolveCountry(response.getResults());
        if (countryData == null) {
            return List.of();
        }
        return mergeProviders(countryData);
    }

    private static CountryWatchProviders resolveCountry(Map<String, CountryWatchProviders> results) {
        for (String region : REGION_FALLBACK) {
            CountryWatchProviders data = results.get(region);
            if (hasProviders(data)) {
                return data;
            }
        }
        String deviceCountry = Locale.getDefault().getCountry();
        if (deviceCountry != null && !deviceCountry.isEmpty()) {
            CountryWatchProviders data = results.get(deviceCountry);
            if (hasProviders(data)) {
                return data;
            }
        }
        for (CountryWatchProviders data : results.values()) {
            if (hasProviders(data)) {
                return data;
            }
        }
        return null;
    }

    private static boolean hasProviders(CountryWatchProviders data) {
        return data != null && !mergeProviders(data).isEmpty();
    }

    private static List<WatchProvider> mergeProviders(CountryWatchProviders data) {
        Set<Integer> seen = new LinkedHashSet<>();
        List<WatchProvider> merged = new ArrayList<>();
        addProviders(merged, seen, data.getFlatrate());
        addProviders(merged, seen, data.getRent());
        addProviders(merged, seen, data.getBuy());
        return merged;
    }

    private static void addProviders(List<WatchProvider> target,
                                     Set<Integer> seen,
                                     List<WatchProvider> source) {
        if (source == null) {
            return;
        }
        for (WatchProvider provider : source) {
            if (provider == null || provider.getLogoPath() == null) {
                continue;
            }
            if (seen.add(provider.getProviderId()) && target.size() < MAX_PROVIDERS) {
                target.add(provider);
            }
        }
    }
}
