package com.novibe.common.data_sources;

import com.novibe.common.util.Log;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Setter(onMethod_ = @Autowired)
public abstract class ListLoader<T> {

    private HttpClient client;

    protected abstract T toObject(String line);

    protected abstract String listType();

    protected abstract Predicate<String> filterRelatedLines();

    @SneakyThrows
    public List<T> fetchWebsites(List<String> urls) {
        List<java.util.concurrent.CompletableFuture<String>> futures = urls.stream()
                .map(url -> java.util.concurrent.CompletableFuture.supplyAsync(() -> fetchList(url)))
                .toList();

        return futures.stream()
                .map(java.util.concurrent.CompletableFuture::join)
                .map(String::stripIndent)
                .flatMap(s -> Pattern.compile("\\r?\\n").splitAsStream(s))
                .parallel()
                .filter(line -> !line.isBlank())
                .filter(line -> !line.startsWith("#"))
                .map(String::toLowerCase)
                .filter(filterRelatedLines())
                .distinct()
                .map(this::toObject)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @SneakyThrows
    private String fetchList(String url) {
        Log.io("Loading %s list from url: %s".formatted(listType(), url));
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
    }

    protected String removeWWW(String domain) {
        if (domain.startsWith("www.")) {
            return domain.substring("www.".length());
        }
        return domain;
    }

}
