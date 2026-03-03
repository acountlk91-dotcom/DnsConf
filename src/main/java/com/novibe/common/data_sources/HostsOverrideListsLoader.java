package com.novibe.common.data_sources;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class HostsOverrideListsLoader extends ListLoader<HostsOverrideListsLoader.BypassRoute> {

    public record BypassRoute(String ip, String website) {
    }

    @Override
    protected String listType() {
        return "Override";
    }

    @Override
    protected Predicate<String> filterRelatedLines() {
        return line -> !HostsBlockListsLoader.isBlock(line);
    }

    @Override
    protected BypassRoute toObject(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2) {
            return new BypassRoute("", removeWWW(line.strip()));
        }
        String ip = parts[0];
        String website = removeWWW(parts[1].strip());
        return new BypassRoute(ip, website);
    }

}
