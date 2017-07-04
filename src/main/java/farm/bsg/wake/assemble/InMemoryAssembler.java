/*
 * Copyright 2014 Jeffrey M. Barber; see LICENSE for more details
 */
package farm.bsg.wake.assemble;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import farm.bsg.wake.sources.Source;
import farm.bsg.wake.stages.Stage;

/**
 * This will render the web site and describe a 'TODO' list of things to merge in
 */
public class InMemoryAssembler {

    private static class MergeStep {
        private final File   source;
        private final long   length;
        private final String key;
        private final String md5;
        private final String contentType;

        public MergeStep(final File source, final String key) {
            this.source = source;
            this.length = source.length();
            this.key = key;
            this.contentType = getContentType(source, key);
            try {
                final InputStream input = new FileInputStream(source);
                try {
                    final byte[] buffer = new byte[64 * 1024];
                    int rd;
                    final MessageDigest digest = MessageDigest.getInstance("MD5");
                    while ((rd = input.read(buffer)) > 0) {
                        digest.update(buffer, 0, rd);
                    }
                    this.md5 = new String(Hex.encodeHex(digest.digest()));
                } finally {
                    input.close();
                }
            } catch (final Exception notFound) {
                throw new RuntimeException(notFound);
            }
        }
    }

    private static String getContentType(final File source, final String key) {
        final String[] parts = key.split(Pattern.quote("."));
        if (parts.length > 0) {
            final String ext = parts[parts.length - 1].toLowerCase();
            if ("png".equals(ext)) {
                return "image/png";
            }
            if ("css".equals(ext)) {
                return "text/css";
            }
        }
        return new MimetypesFileTypeMap().getContentType(source);
    }

    private final ArrayList<MergeStep>    merge;
    private final HashMap<String, String> html;
    private final StringBuilder           audit;
    private final String                  sitemapPrefix;
    private final StringBuilder           sitemap;

    public InMemoryAssembler(final File mergePath, final Stage stage, final String sitemapPrefix) {
        this.merge = new ArrayList<>();
        this.html = new HashMap<>();
        this.audit = new StringBuilder();
        this.audit.append("<table>");
        this.sitemap = new StringBuilder();
        this.sitemap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        this.sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        this.sitemapPrefix = sitemapPrefix;
        registerStage(stage);
        registerMerge(mergePath, mergePath);
    }

    public void assemble(final PutTarget target) throws Exception {
        for (final MergeStep step : this.merge) {
            final FileInputStream input = new FileInputStream(step.source);
            try {
                target.upload(step.key, step.md5, step.contentType, input, step.length);
            } finally {
                input.close();
            }
        }
        for (final String url : this.html.keySet()) {
            final byte[] value = this.html.get(url).getBytes("UTF-8");
            target.upload(url, DigestUtils.md5Hex(value), "text/html", new ByteArrayInputStream(value), value.length);
        }
        final byte[] auditBytes = this.audit.toString().getBytes("UTF-8");
        target.upload("__audit.html", DigestUtils.md5Hex(auditBytes), "text/html", new ByteArrayInputStream(auditBytes), auditBytes.length);

        this.sitemap.append("</urlset>");
        final byte[] sitemapBytes = this.sitemap.toString().getBytes("UTF-8");
        target.upload("sitemap.xml", DigestUtils.md5Hex(sitemapBytes), "application/xml", new ByteArrayInputStream(sitemapBytes), sitemapBytes.length);

        final byte[] robotsBytes = "User-agent: *\nDisallow:\n".getBytes("UTF-8");
        target.upload("robots.txt", DigestUtils.md5Hex(robotsBytes), "text/text", new ByteArrayInputStream(robotsBytes), robotsBytes.length);
    }

    public void put(final String url, final String body) {
        this.html.put(url, body);
    }

    private void registerMerge(final File mergePath, final File root) {
        final String rootBase = root.getPath();
        for (final File toMerge : mergePath.listFiles()) {
            if (toMerge.isDirectory()) {
                registerMerge(toMerge, root);
            } else {
                final String key = toMerge.getPath().substring(rootBase.length() + 1);
                if (!this.html.containsKey(key)) {
                    this.merge.add(new MergeStep(toMerge, key));
                } else {
                    // TODO: here would be a good place to LOG
                }
            }
        }
    }

    private void registerStage(final Stage stage) {
        for (final Source source : stage.sources()) {
            final String url = source.get("url");
            final String body = source.get("body");
            this.html.put(url, body);
            if (!source.testBoolean("noindex")) {
                this.sitemap.append(" <url>\n");
                this.sitemap.append("  <loc>" + this.sitemapPrefix + url + "</loc>\n");
                this.sitemap.append("  <changefreq>daily</changefreq>\n");
                this.sitemap.append(" </url>\n");
            }
            this.audit.append("<tr><td>" + url + "</td><td>" + source.get("title") + "</td><td>" + source.get("audit") + "<br/>");
        }
    }

    public void validate(final BiConsumer<String, String> checker) {
        this.html.forEach(checker);
    }
}
