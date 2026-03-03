package com.novibe.common.data_sources;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class HostsBlockListsLoader extends ListLoader<String> {

    private static final String[] BLOCK_PREFIXES = { "0.0.0.0 ", "127.0.0.1 ", "::1 "};
    private static final String[] LOCALHOST_NAME = {"localhost", "ip6-localhost"};

    @Override
    protected String listType() {
        return "Block";
    }

    @Override
    protected Predicate<String> filterRelatedLines() {
        return line -> isBlock(line) && !isLocalhost(line);
    }

    @Override
    protected String toObject(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2) {
            return removeWWW(line.strip());
        }
        return removeWWW(parts[1].strip());
    }

    public static boolean isBlock(String line) {
        for (String blockPrefix : BLOCK_PREFIXES) {
            if (line.startsWith(blockPrefix)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLocalhost(String line) {
        for (String localhost : LOCALHOST_NAME) {
            if (line.endsWith(localhost))
                return true;
        }
        return false;
    }

}
