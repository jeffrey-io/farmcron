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

    private final Source      base;
    private final QueryEngine engine;

    public EngineJoinSource(Source base, QueryEngine engine) {
        this.base = base;
        this.engine = engine;
    }

    private static final String QUERY_BEGIN_PREFIX = "!!query:";
    private static final String QUERY_BEGIN_SUFFIX = "!!";
    private static final String QUERY_END          = ":query!!";

    public static class InlineQuery {
        public final String query;
        public final String interior;
        public final String replacement;

        public InlineQuery(String query, String interior, String replacement) {
            this.query = query;
            this.interior = interior;
            this.replacement = replacement;
        }

        public static InlineQuery parse(String body) {
            if (body == null) {
                return null;
            }
            int start = body.indexOf(QUERY_BEGIN_PREFIX);
            if (start >= 0) {
                int end = body.indexOf(QUERY_BEGIN_SUFFIX, start + QUERY_BEGIN_SUFFIX.length());
                String query = body.substring(start + QUERY_BEGIN_PREFIX.length(), end);

                int wrappedEnd = body.indexOf(QUERY_END, end);
                if (wrappedEnd < 0) {
                    return null;
                }

                String middle = body.substring(end + QUERY_BEGIN_SUFFIX.length(), wrappedEnd);
                String toReplace = body.substring(start, wrappedEnd + QUERY_END.length());
                return new InlineQuery(query, middle, toReplace);
            }
            return null;
        }

    }

    @Override
    public String get(String key) {
        return base.get(key);
    }

    @Override
    public void populateDomain(Set<String> domain) {
        base.populateDomain(domain);

    }

    @Override
    public void walkComplex(BiConsumer<String, Object> injectComplex) {
        base.walkComplex(injectComplex);

        ArrayList<String> categories = new ArrayList<>();
        HashMap<String, ArrayList<HashMap<String, Object>>> productsByCategory = new HashMap<>();

        ArrayList<HashMap<String, Object>> products = new ArrayList<>();
        for (Product product : engine.select_product().done()) {
            HashMap<String, Object> pMap = new HashMap<>();
            pMap.put("id", product.getId());
            pMap.put("name", product.get("name"));
            pMap.put("description", product.get("description"));
            pMap.put("price", product.get("price"));
            
            HashMap<String, String> image = new HashMap<>();
            String contentType = product.get("image_content_type");
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
        for (Entry<String, ArrayList<HashMap<String, Object>>> entry : productsByCategory.entrySet()) {
            injectComplex.accept(entry.getKey(), entry.getValue());
        }

        // we should inject categories

        for (WakeInputFile input : engine.select_wakeinputfile().done()) {
            if (input.isImage()) {
                HashMap<String, String> iMap = new HashMap<>();
                iMap.put("base64", input.get("contents"));
                iMap.put("mime", input.get("content_type"));
                injectComplex.accept("file_" + input.get("filename").replaceAll(Pattern.quote("."), "_"), iMap);
            }
        }
    }

}
