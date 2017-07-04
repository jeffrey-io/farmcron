/*
 * Copyright 2014 Jeffrey M. Barber; see LICENSE for more details
 */
package farm.bsg.wake.stages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Pattern;

import farm.bsg.wake.sources.ComplexMapInjectedSource;
import farm.bsg.wake.sources.Source;

/**
 * Defines a tree over the site TODO: document better
 */
public class InjectTopologyStage extends Stage {
    private class TopologyNode {
        private final HashMap<String, TopologyNode> children;
        private final String                        path;
        private Source                              source;

        public TopologyNode(final String path) {
            this.children = new HashMap<>();
            this.path = path;
        }

        public HashMap<String, Object> compile(final Source active) {
            final HashMap<String, Object> result = new HashMap<>();
            result.put("exists", this.source != null);
            final String activePath = active.get("path");
            if (activePath != null) {
                result.put("child_is_active", activePath.startsWith(this.path));
            }
            if (this.source != null) {
                result.put("title", this.source.get("title"));
                result.put("active", active == this.source);
                result.put("url", this.source.get("url"));
            }
            final ArrayList<Object> compiledChildren = new ArrayList<>();
            final ArrayList<TopologyNode> sortedChildren = new ArrayList<>(this.children.values());
            Collections.sort(sortedChildren, Comparator.comparingLong((item) -> item.order()));
            for (final TopologyNode child : sortedChildren) {
                compiledChildren.add(child.compile(active));
            }
            result.put("children", compiledChildren);
            for (final String childName : this.children.keySet()) {
                result.put(childName, this.children.get(childName).compile(active));
            }
            return result;
        }

        private long order() {
            if (this.source == null) {
                return Integer.MAX_VALUE;
            }
            return this.source.order();
        }
    }

    private class TopologyTree {
        TopologyNode root;

        private TopologyTree() {
            this.root = new TopologyNode("");
        }

        public void add(final String path, final Source source) {
            final TopologyNode node = node(path, source);
            if (node.source != null) {
                throw new RuntimeException("duplicate path");
            }
            node.source = source;
        }

        public HashMap<String, Object> compile(final Source active) {
            return this.root.compile(active);
        }

        TopologyNode node(final String path, final Source source) {
            TopologyNode head = this.root;
            for (String part : path.split(SLASH)) {
                if ("$".equals(part)) {
                    part = source.get("name");
                }
                TopologyNode next = head.children.get(part);
                if (next == null) {
                    String nextPath = head.path;
                    if (nextPath.length() > 0) {
                        nextPath += "/";
                    }
                    nextPath += part;
                    next = new TopologyNode(nextPath);
                    head.children.put(part, next);
                }
                head = next;
            }
            return head;
        }
    }

    private static final String SLASH = Pattern.quote("/");

    private final Stage         priorStage;

    public InjectTopologyStage(final Stage priorStage) {
        this.priorStage = priorStage;
    }

    @Override
    public Collection<Source> sources() {
        final ArrayList<Source> attachTo = new ArrayList<>();
        final TopologyTree topology = new TopologyTree();
        for (final Source source : this.priorStage.sources()) {
            final String path = source.get("path");
            if (path != null) {
                topology.add(path, source);
            }
            attachTo.add(source);
        }
        final ArrayList<Source> next = new ArrayList<>();
        attachTo.forEach((random) -> {
            next.add(new ComplexMapInjectedSource(random, "topology", topology.compile(random)));
        });
        return next;
    }
}
