package farm.bsg.wake.sources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import farm.bsg.QueryEngine;
import farm.bsg.models.Product;
import farm.bsg.models.WakeInputFile;
import farm.bsg.pages.YourCart;

public class EngineJoinSource extends Source {

    public static class InlineQuery {
        public static InlineQuery parse(final String body) {
            if (body == null) {
                return null;
            }
            final int start = body.indexOf(QUERY_BEGIN_PREFIX);
            if (start >= 0) {
                final int end = body.indexOf(QUERY_BEGIN_SUFFIX, start + QUERY_BEGIN_SUFFIX.length());
                final String query = body.substring(start + QUERY_BEGIN_PREFIX.length(), end);

                final int wrappedEnd = body.indexOf(QUERY_END, end);
                if (wrappedEnd < 0) {
                    return null;
                }

                final String middle = body.substring(end + QUERY_BEGIN_SUFFIX.length(), wrappedEnd);
                final String toReplace = body.substring(start, wrappedEnd + QUERY_END.length());
                return new InlineQuery(query, middle, toReplace);
            }
            return null;
        }

        public final String query;
        public final String interior;

        public final String replacement;

        public InlineQuery(final String query, final String interior, final String replacement) {
            this.query = query;
            this.interior = interior;
            this.replacement = replacement;
        }

    }

    private static final String QUERY_BEGIN_PREFIX = "!!query:";

    private static final String QUERY_BEGIN_SUFFIX = "!!";

    private static final String QUERY_END          = ":query!!";
    private final Source        base;
    private final QueryEngine   engine;

    public EngineJoinSource(final Source base, final QueryEngine engine) {
        this.base = base;
        this.engine = engine;
    }

    @Override
    public String get(final String key) {
        return this.base.get(key);
    }

    @Override
    public void populateDomain(final Set<String> domain) {
        this.base.populateDomain(domain);

    }

    @Override
    public void walkComplex(final BiConsumer<String, Object> injectComplex) {
        this.base.walkComplex(injectComplex);

        final ArrayList<String> categories = new ArrayList<>();
        final HashMap<String, ArrayList<HashMap<String, Object>>> productsByCategory = new HashMap<>();

        final ArrayList<HashMap<String, Object>> products = new ArrayList<>();
        for (final Product product : this.engine.select_product().done()) {
            final HashMap<String, Object> pMap = new HashMap<>();
            pMap.put("id", product.getId());
            pMap.put("name", product.get("name"));
            pMap.put("description", product.get("description"));
            pMap.put("price", "$" + product.get("price"));
            pMap.put("old_price", "$" + product.get("old_price"));

            final HashMap<String, String> image = new HashMap<>();
            final String contentType = product.get("image_content_type");
            image.put("type", contentType);
            if (contentType != null) {
                image.put("data", product.get("image"));

                pMap.put("image", image);
            }
            pMap.put("add_url", YourCart.CART_ADD.href("pid", product.getId()).value);

            String category = product.get("category");
            if (category != null) {
                category = category.toLowerCase().trim();
                if (category.length() > 0) {
                    category = "product_" + category;
                    ArrayList<HashMap<String, Object>> listByCategory = productsByCategory.get(category);
                    if (listByCategory == null) {
                        categories.add(category);
                        listByCategory = new ArrayList<>();
                        productsByCategory.put(category, listByCategory);
                    }
                    listByCategory.add(pMap);
                }
            }
            products.add(pMap);
        }
        injectComplex.accept("products", products);
        for (final Entry<String, ArrayList<HashMap<String, Object>>> entry : productsByCategory.entrySet()) {
            injectComplex.accept(entry.getKey(), entry.getValue());
        }

        // we should inject categories

        for (final WakeInputFile input : this.engine.select_wakeinputfile().done()) {
            if (input.isImage()) {
                final HashMap<String, String> iMap = new HashMap<>();
                iMap.put("base64", input.get("contents"));
                iMap.put("mime", input.get("content_type"));
                injectComplex.accept("file_" + input.get("filename").replaceAll(Pattern.quote("."), "_"), iMap);
            }
        }
    }

}
