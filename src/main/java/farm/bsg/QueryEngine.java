package farm.bsg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import farm.bsg.data.BinaryOperators;
import farm.bsg.data.InMemoryStorage;
import farm.bsg.data.KeyIndex;
import farm.bsg.data.LexographicalOrder;
import farm.bsg.data.MultiPrefixLogger;
import farm.bsg.data.PutResult;
import farm.bsg.data.StorageEngine;
import farm.bsg.data.StringGroupBy;
import farm.bsg.data.UriBlobCache;
import farm.bsg.data.Value;
import farm.bsg.data.contracts.PersistenceLogger;
import farm.bsg.data.contracts.ProjectionProvider;
import farm.bsg.models.Baton;
import farm.bsg.models.Cart;
import farm.bsg.models.CartItem;
import farm.bsg.models.Check;
import farm.bsg.models.Customer;
import farm.bsg.models.PayrollEntry;
import farm.bsg.models.Person;
import farm.bsg.models.Product;
import farm.bsg.models.SiteProperties;
import farm.bsg.models.Subscriber;
import farm.bsg.models.Subscription;
import farm.bsg.models.Task;
import farm.bsg.models.TaskFactory;
import farm.bsg.models.WakeInputFile;

/****************************************************************
 * WARNING: Generated This class is a generated database query engine that provides an easy to take a simple Persistence mechanism into a real DB
 ****************************************************************/
public class QueryEngine {
    public class BatonListHolder {
        private final ArrayList<Baton> list;

        private BatonListHolder(final ArrayList<Baton> list) {
            this.list = list;
        }

        private BatonListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = batons_of(fetch_all("baton/" + scope));
            } else {
                this.list = batons_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<Baton> done() {
            return this.list;
        }

        public Baton first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public BatonListHolder fork() {
            return new BatonListHolder(this.list);
        }

        public HashMap<String, ArrayList<Baton>> groupBy(final Function<Baton, String> f) {
            final StringGroupBy<Baton> map = new StringGroupBy<>();
            final Iterator<Baton> it = this.list.iterator();
            while (it.hasNext()) {
                final Baton v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public BatonListHolder inline_apply(final Consumer<Baton> consumer) {
            final Iterator<Baton> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public BatonListHolder inline_filter(final Predicate<Baton> filter) {
            final Iterator<Baton> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public BatonListHolder inline_order_by(final Comparator<Baton> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public BatonListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Baton>(keys, true, true));
            return this;
        }

        public BatonListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Baton>(keys, asc, caseSensitive));
            return this;
        }

        public BatonListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Baton>(keys, false, true));
            return this;
        }

        public BatonListHolder limit(final int count) {
            final Iterator<Baton> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (baton/)
     **************************************************/

    public class BatonProjection_admin {
        private final HashMap<String, String> data;

        public BatonProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("code", farm.bsg.data.types.TypeString.project(pp, "code"));
        }

        public PutResult apply(final Baton baton) {
            return baton.validateAndApplyProjection(this.data);
        }
    }

    public class BatonSetQuery {
        private String                scope;
        private final HashSet<String> keys;

        private BatonSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<Baton> done() {
            return new BatonListHolder(this.keys, this.scope).done();
        }

        public BatonSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public BatonListHolder to_list() {
            return new BatonListHolder(this.keys, this.scope);
        }
    }

    public class CartItemListHolder {
        private final ArrayList<CartItem> list;

        private CartItemListHolder(final ArrayList<CartItem> list) {
            this.list = list;
        }

        private CartItemListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = cartitems_of(fetch_all("cart-item/" + scope));
            } else {
                this.list = cartitems_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<CartItem> done() {
            return this.list;
        }

        public CartItem first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public CartItemListHolder fork() {
            return new CartItemListHolder(this.list);
        }

        public HashMap<String, ArrayList<CartItem>> groupBy(final Function<CartItem, String> f) {
            final StringGroupBy<CartItem> map = new StringGroupBy<>();
            final Iterator<CartItem> it = this.list.iterator();
            while (it.hasNext()) {
                final CartItem v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public CartItemListHolder inline_apply(final Consumer<CartItem> consumer) {
            final Iterator<CartItem> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public CartItemListHolder inline_filter(final Predicate<CartItem> filter) {
            final Iterator<CartItem> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public CartItemListHolder inline_order_by(final Comparator<CartItem> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public CartItemListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<CartItem>(keys, true, true));
            return this;
        }

        public CartItemListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<CartItem>(keys, asc, caseSensitive));
            return this;
        }

        public CartItemListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<CartItem>(keys, false, true));
            return this;
        }

        public CartItemListHolder limit(final int count) {
            final Iterator<CartItem> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (cart-item/)
     **************************************************/

    public class CartItemProjection_admin {
        private final HashMap<String, String> data;

        public CartItemProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("cart", farm.bsg.data.types.TypeString.project(pp, "cart"));
            this.data.put("product", farm.bsg.data.types.TypeString.project(pp, "product"));
            this.data.put("quantity", farm.bsg.data.types.TypeNumber.project(pp, "quantity"));
            this.data.put("customizations", farm.bsg.data.types.TypeString.project(pp, "customizations"));
        }

        public PutResult apply(final CartItem cartitem) {
            return cartitem.validateAndApplyProjection(this.data);
        }
    }

    public class CartItemSetQuery {
        private String          scope;
        private HashSet<String> keys;

        private CartItemSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<CartItem> done() {
            return new CartItemListHolder(this.keys, this.scope).done();
        }

        private HashSet<String> lookup_cart(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.cartitem_cart.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_customizations(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.cartitem_customizations.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_product(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.cartitem_product.getKeys(value));
            }
            return keys;
        }

        public CartItemSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public CartItemListHolder to_list() {
            return new CartItemListHolder(this.keys, this.scope);
        }

        public CartItemSetQuery where_cart_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_cart(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_cart(values));
            }
            return this;
        }

        public CartItemSetQuery where_customizations_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_customizations(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_customizations(values));
            }
            return this;
        }

        public CartItemSetQuery where_product_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_product(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_product(values));
            }
            return this;
        }
    }

    public class CartListHolder {
        private final ArrayList<Cart> list;

        private CartListHolder(final ArrayList<Cart> list) {
            this.list = list;
        }

        private CartListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = carts_of(fetch_all("cart/" + scope));
            } else {
                this.list = carts_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<Cart> done() {
            return this.list;
        }

        public Cart first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public CartListHolder fork() {
            return new CartListHolder(this.list);
        }

        public HashMap<String, ArrayList<Cart>> groupBy(final Function<Cart, String> f) {
            final StringGroupBy<Cart> map = new StringGroupBy<>();
            final Iterator<Cart> it = this.list.iterator();
            while (it.hasNext()) {
                final Cart v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public CartListHolder inline_apply(final Consumer<Cart> consumer) {
            final Iterator<Cart> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public CartListHolder inline_filter(final Predicate<Cart> filter) {
            final Iterator<Cart> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public CartListHolder inline_order_by(final Comparator<Cart> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public CartListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Cart>(keys, true, true));
            return this;
        }

        public CartListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Cart>(keys, asc, caseSensitive));
            return this;
        }

        public CartListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Cart>(keys, false, true));
            return this;
        }

        public CartListHolder limit(final int count) {
            final Iterator<Cart> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (cart/)
     **************************************************/

    public class CartProjection_admin {
        private final HashMap<String, String> data;

        public CartProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("customer", farm.bsg.data.types.TypeString.project(pp, "customer"));
            this.data.put("task", farm.bsg.data.types.TypeString.project(pp, "task"));
            this.data.put("state", farm.bsg.data.types.TypeString.project(pp, "state"));
        }

        public PutResult apply(final Cart cart) {
            return cart.validateAndApplyProjection(this.data);
        }
    }

    public class CartSetQuery {
        private String          scope;
        private HashSet<String> keys;

        private CartSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<Cart> done() {
            return new CartListHolder(this.keys, this.scope).done();
        }

        private HashSet<String> lookup_customer(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.cart_customer.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_task(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.cart_task.getKeys(value));
            }
            return keys;
        }

        public CartSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public CartListHolder to_list() {
            return new CartListHolder(this.keys, this.scope);
        }

        public CartSetQuery where_customer_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_customer(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_customer(values));
            }
            return this;
        }

        public CartSetQuery where_task_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_task(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_task(values));
            }
            return this;
        }
    }

    public class CheckListHolder {
        private final ArrayList<Check> list;

        private CheckListHolder(final ArrayList<Check> list) {
            this.list = list;
        }

        private CheckListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = checks_of(fetch_all("checks/" + scope));
            } else {
                this.list = checks_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<Check> done() {
            return this.list;
        }

        public Check first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public CheckListHolder fork() {
            return new CheckListHolder(this.list);
        }

        public HashMap<String, ArrayList<Check>> groupBy(final Function<Check, String> f) {
            final StringGroupBy<Check> map = new StringGroupBy<>();
            final Iterator<Check> it = this.list.iterator();
            while (it.hasNext()) {
                final Check v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public CheckListHolder inline_apply(final Consumer<Check> consumer) {
            final Iterator<Check> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public CheckListHolder inline_filter(final Predicate<Check> filter) {
            final Iterator<Check> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public CheckListHolder inline_order_by(final Comparator<Check> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public CheckListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Check>(keys, true, true));
            return this;
        }

        public CheckListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Check>(keys, asc, caseSensitive));
            return this;
        }

        public CheckListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Check>(keys, false, true));
            return this;
        }

        public CheckListHolder limit(final int count) {
            final Iterator<Check> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (checks/)
     **************************************************/

    public class CheckProjection_admin {
        private final HashMap<String, String> data;

        public CheckProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("ref", farm.bsg.data.types.TypeString.project(pp, "ref"));
            this.data.put("person", farm.bsg.data.types.TypeString.project(pp, "person"));
            this.data.put("generated", farm.bsg.data.types.TypeDateTime.project(pp, "generated"));
            this.data.put("fiscal_day", farm.bsg.data.types.TypeString.project(pp, "fiscal_day"));
            this.data.put("payment", farm.bsg.data.types.TypeNumber.project(pp, "payment"));
            this.data.put("ready", farm.bsg.data.types.TypeString.project(pp, "ready"));
            this.data.put("checksum", farm.bsg.data.types.TypeNumber.project(pp, "checksum"));
            this.data.put("bonus_related", farm.bsg.data.types.TypeBoolean.project(pp, "bonus_related"));
            this.data.put("pto_change", farm.bsg.data.types.TypeNumber.project(pp, "pto_change"));
        }

        public PutResult apply(final Check check) {
            return check.validateAndApplyProjection(this.data);
        }
    }

    public class CheckSetQuery {
        private String          scope;
        private HashSet<String> keys;

        private CheckSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<Check> done() {
            return new CheckListHolder(this.keys, this.scope).done();
        }

        private HashSet<String> lookup_fiscal_day(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.check_fiscal_day.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_person(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.check_person.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_ready(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.check_ready.getKeys(value));
            }
            return keys;
        }

        public CheckSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public CheckListHolder to_list() {
            return new CheckListHolder(this.keys, this.scope);
        }

        public CheckSetQuery where_fiscal_day_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_fiscal_day(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_fiscal_day(values));
            }
            return this;
        }

        public CheckSetQuery where_person_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_person(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_person(values));
            }
            return this;
        }

        public CheckSetQuery where_ready_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_ready(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_ready(values));
            }
            return this;
        }
    }

    public class CustomerListHolder {
        private final ArrayList<Customer> list;

        private CustomerListHolder(final ArrayList<Customer> list) {
            this.list = list;
        }

        private CustomerListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = customers_of(fetch_all("customer/" + scope));
            } else {
                this.list = customers_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<Customer> done() {
            return this.list;
        }

        public Customer first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public CustomerListHolder fork() {
            return new CustomerListHolder(this.list);
        }

        public HashMap<String, ArrayList<Customer>> groupBy(final Function<Customer, String> f) {
            final StringGroupBy<Customer> map = new StringGroupBy<>();
            final Iterator<Customer> it = this.list.iterator();
            while (it.hasNext()) {
                final Customer v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public CustomerListHolder inline_apply(final Consumer<Customer> consumer) {
            final Iterator<Customer> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public CustomerListHolder inline_filter(final Predicate<Customer> filter) {
            final Iterator<Customer> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public CustomerListHolder inline_order_by(final Comparator<Customer> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public CustomerListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Customer>(keys, true, true));
            return this;
        }

        public CustomerListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Customer>(keys, asc, caseSensitive));
            return this;
        }

        public CustomerListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Customer>(keys, false, true));
            return this;
        }

        public CustomerListHolder limit(final int count) {
            final Iterator<Customer> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (customer/)
     **************************************************/

    public class CustomerProjection_admin {
        private final HashMap<String, String> data;

        public CustomerProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("email", farm.bsg.data.types.TypeString.project(pp, "email"));
            this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
            this.data.put("phone", farm.bsg.data.types.TypeString.project(pp, "phone"));
            this.data.put("salt", farm.bsg.data.types.TypeString.project(pp, "salt"));
            this.data.put("hash", farm.bsg.data.types.TypeString.project(pp, "hash"));
            this.data.put("cookie", farm.bsg.data.types.TypeString.project(pp, "cookie"));
            this.data.put("notification_token", farm.bsg.data.types.TypeString.project(pp, "notification_token"));
            this.data.put("notification_uri", farm.bsg.data.types.TypeString.project(pp, "notification_uri"));
        }

        public PutResult apply(final Customer customer) {
            return customer.validateAndApplyProjection(this.data);
        }
    }

    public class CustomerSetQuery {
        private String          scope;
        private HashSet<String> keys;

        private CustomerSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<Customer> done() {
            return new CustomerListHolder(this.keys, this.scope).done();
        }

        private HashSet<String> lookup_cookie(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.customer_cookie.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_email(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.customer_email.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_notification_token(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.customer_notification_token.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_phone(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.customer_phone.getKeys(value));
            }
            return keys;
        }

        public CustomerSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public CustomerListHolder to_list() {
            return new CustomerListHolder(this.keys, this.scope);
        }

        public CustomerSetQuery where_cookie_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_cookie(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_cookie(values));
            }
            return this;
        }

        public CustomerSetQuery where_email_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_email(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_email(values));
            }
            return this;
        }

        public CustomerSetQuery where_notification_token_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_notification_token(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_notification_token(values));
            }
            return this;
        }

        public CustomerSetQuery where_phone_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_phone(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_phone(values));
            }
            return this;
        }
    }

    public class PayrollEntryListHolder {
        private final ArrayList<PayrollEntry> list;

        private PayrollEntryListHolder(final ArrayList<PayrollEntry> list) {
            this.list = list;
        }

        private PayrollEntryListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = payrollentrys_of(fetch_all("payroll/" + scope));
            } else {
                this.list = payrollentrys_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<PayrollEntry> done() {
            return this.list;
        }

        public PayrollEntry first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public PayrollEntryListHolder fork() {
            return new PayrollEntryListHolder(this.list);
        }

        public HashMap<String, ArrayList<PayrollEntry>> groupBy(final Function<PayrollEntry, String> f) {
            final StringGroupBy<PayrollEntry> map = new StringGroupBy<>();
            final Iterator<PayrollEntry> it = this.list.iterator();
            while (it.hasNext()) {
                final PayrollEntry v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public PayrollEntryListHolder inline_apply(final Consumer<PayrollEntry> consumer) {
            final Iterator<PayrollEntry> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public PayrollEntryListHolder inline_filter(final Predicate<PayrollEntry> filter) {
            final Iterator<PayrollEntry> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public PayrollEntryListHolder inline_order_by(final Comparator<PayrollEntry> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public PayrollEntryListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<PayrollEntry>(keys, true, true));
            return this;
        }

        public PayrollEntryListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<PayrollEntry>(keys, asc, caseSensitive));
            return this;
        }

        public PayrollEntryListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<PayrollEntry>(keys, false, true));
            return this;
        }

        public PayrollEntryListHolder limit(final int count) {
            final Iterator<PayrollEntry> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (payroll/)
     **************************************************/

    public class PayrollEntryProjection_admin {
        private final HashMap<String, String> data;

        public PayrollEntryProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("person", farm.bsg.data.types.TypeString.project(pp, "person"));
            this.data.put("reported", farm.bsg.data.types.TypeDateTime.project(pp, "reported"));
            this.data.put("fiscal_day", farm.bsg.data.types.TypeString.project(pp, "fiscal_day"));
            this.data.put("mileage", farm.bsg.data.types.TypeNumber.project(pp, "mileage"));
            this.data.put("hours_worked", farm.bsg.data.types.TypeNumber.project(pp, "hours_worked"));
            this.data.put("pto_used", farm.bsg.data.types.TypeNumber.project(pp, "pto_used"));
            this.data.put("sick_leave_used", farm.bsg.data.types.TypeNumber.project(pp, "sick_leave_used"));
            this.data.put("hourly_wage_compesation", farm.bsg.data.types.TypeNumber.project(pp, "hourly_wage_compesation"));
            this.data.put("mileage_compensation", farm.bsg.data.types.TypeNumber.project(pp, "mileage_compensation"));
            this.data.put("pto_change", farm.bsg.data.types.TypeNumber.project(pp, "pto_change"));
            this.data.put("owed", farm.bsg.data.types.TypeNumber.project(pp, "owed"));
            this.data.put("tax_withholding", farm.bsg.data.types.TypeNumber.project(pp, "tax_withholding"));
            this.data.put("taxes", farm.bsg.data.types.TypeNumber.project(pp, "taxes"));
            this.data.put("benefits", farm.bsg.data.types.TypeNumber.project(pp, "benefits"));
            this.data.put("check", farm.bsg.data.types.TypeString.project(pp, "check"));
            this.data.put("unpaid", farm.bsg.data.types.TypeString.project(pp, "unpaid"));
        }

        public PutResult apply(final PayrollEntry payrollentry) {
            return payrollentry.validateAndApplyProjection(this.data);
        }
    }

    public class PayrollEntryProjection_edit {
        private final HashMap<String, String> data;

        public PayrollEntryProjection_edit(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("mileage", farm.bsg.data.types.TypeNumber.project(pp, "mileage"));
            this.data.put("hours_worked", farm.bsg.data.types.TypeNumber.project(pp, "hours_worked"));
            this.data.put("pto_used", farm.bsg.data.types.TypeNumber.project(pp, "pto_used"));
            this.data.put("sick_leave_used", farm.bsg.data.types.TypeNumber.project(pp, "sick_leave_used"));
        }

        public PutResult apply(final PayrollEntry payrollentry) {
            return payrollentry.validateAndApplyProjection(this.data);
        }
    }

    public class PayrollEntrySetQuery {
        private String          scope;
        private HashSet<String> keys;

        private PayrollEntrySetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<PayrollEntry> done() {
            return new PayrollEntryListHolder(this.keys, this.scope).done();
        }

        private HashSet<String> lookup_check(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.payrollentry_check.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_unpaid(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.payrollentry_unpaid.getKeys(value));
            }
            return keys;
        }

        public PayrollEntrySetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public PayrollEntryListHolder to_list() {
            return new PayrollEntryListHolder(this.keys, this.scope);
        }

        public PayrollEntrySetQuery where_check_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_check(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_check(values));
            }
            return this;
        }

        public PayrollEntrySetQuery where_unpaid_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_unpaid(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_unpaid(values));
            }
            return this;
        }
    }

    public class PersonListHolder {
        private final ArrayList<Person> list;

        private PersonListHolder(final ArrayList<Person> list) {
            this.list = list;
        }

        private PersonListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = persons_of(fetch_all("person/" + scope));
            } else {
                this.list = persons_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<Person> done() {
            return this.list;
        }

        public Person first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public PersonListHolder fork() {
            return new PersonListHolder(this.list);
        }

        public HashMap<String, ArrayList<Person>> groupBy(final Function<Person, String> f) {
            final StringGroupBy<Person> map = new StringGroupBy<>();
            final Iterator<Person> it = this.list.iterator();
            while (it.hasNext()) {
                final Person v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public PersonListHolder inline_apply(final Consumer<Person> consumer) {
            final Iterator<Person> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public PersonListHolder inline_filter(final Predicate<Person> filter) {
            final Iterator<Person> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public PersonListHolder inline_order_by(final Comparator<Person> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public PersonListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Person>(keys, true, true));
            return this;
        }

        public PersonListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Person>(keys, asc, caseSensitive));
            return this;
        }

        public PersonListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Person>(keys, false, true));
            return this;
        }

        public PersonListHolder limit(final int count) {
            final Iterator<Person> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (person/)
     **************************************************/

    public class PersonProjection_admin {
        private final HashMap<String, String> data;

        public PersonProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("login", farm.bsg.data.types.TypeString.project(pp, "login"));
            this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
            this.data.put("phone", farm.bsg.data.types.TypeString.project(pp, "phone"));
            this.data.put("email", farm.bsg.data.types.TypeString.project(pp, "email"));
            this.data.put("address_1", farm.bsg.data.types.TypeString.project(pp, "address_1"));
            this.data.put("address_2", farm.bsg.data.types.TypeString.project(pp, "address_2"));
            this.data.put("city", farm.bsg.data.types.TypeString.project(pp, "city"));
            this.data.put("state", farm.bsg.data.types.TypeString.project(pp, "state"));
            this.data.put("postal", farm.bsg.data.types.TypeString.project(pp, "postal"));
            this.data.put("country", farm.bsg.data.types.TypeString.project(pp, "country"));
            this.data.put("salt", farm.bsg.data.types.TypeString.project(pp, "salt"));
            this.data.put("hash", farm.bsg.data.types.TypeString.project(pp, "hash"));
            this.data.put("cookie", farm.bsg.data.types.TypeString.project(pp, "cookie"));
            this.data.put("super_cookie", farm.bsg.data.types.TypeString.project(pp, "super_cookie"));
            this.data.put("device_token", farm.bsg.data.types.TypeString.project(pp, "device_token"));
            this.data.put("notification_token", farm.bsg.data.types.TypeString.project(pp, "notification_token"));
            this.data.put("notification_uri", farm.bsg.data.types.TypeString.project(pp, "notification_uri"));
            this.data.put("fiscal_timezone", farm.bsg.data.types.TypeString.project(pp, "fiscal_timezone"));
            this.data.put("default_mileage", farm.bsg.data.types.TypeNumber.project(pp, "default_mileage"));
            this.data.put("hourly_wage_compesation", farm.bsg.data.types.TypeNumber.project(pp, "hourly_wage_compesation"));
            this.data.put("mileage_compensation", farm.bsg.data.types.TypeNumber.project(pp, "mileage_compensation"));
            this.data.put("ideal_weekly_hours", farm.bsg.data.types.TypeNumber.project(pp, "ideal_weekly_hours"));
            this.data.put("pto_earning_rate", farm.bsg.data.types.TypeNumber.project(pp, "pto_earning_rate"));
            this.data.put("bonus_target", farm.bsg.data.types.TypeNumber.project(pp, "bonus_target"));
            this.data.put("min_performance_multiplier", farm.bsg.data.types.TypeNumber.project(pp, "min_performance_multiplier"));
            this.data.put("max_performance_multiplier", farm.bsg.data.types.TypeNumber.project(pp, "max_performance_multiplier"));
            this.data.put("monthly_benefits", farm.bsg.data.types.TypeNumber.project(pp, "monthly_benefits"));
            this.data.put("tax_withholding", farm.bsg.data.types.TypeNumber.project(pp, "tax_withholding"));
            this.data.put("permissions_and_roles", farm.bsg.data.types.TypeStringTokenList.project(pp, "permissions_and_roles"));
        }

        public PutResult apply(final Person person) {
            return person.validateAndApplyProjection(this.data);
        }
    }

    public class PersonProjection_contact_info {
        private final HashMap<String, String> data;

        public PersonProjection_contact_info(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
            this.data.put("phone", farm.bsg.data.types.TypeString.project(pp, "phone"));
            this.data.put("email", farm.bsg.data.types.TypeString.project(pp, "email"));
            this.data.put("address_1", farm.bsg.data.types.TypeString.project(pp, "address_1"));
            this.data.put("address_2", farm.bsg.data.types.TypeString.project(pp, "address_2"));
            this.data.put("city", farm.bsg.data.types.TypeString.project(pp, "city"));
            this.data.put("state", farm.bsg.data.types.TypeString.project(pp, "state"));
            this.data.put("postal", farm.bsg.data.types.TypeString.project(pp, "postal"));
            this.data.put("country", farm.bsg.data.types.TypeString.project(pp, "country"));
        }

        public PutResult apply(final Person person) {
            return person.validateAndApplyProjection(this.data);
        }
    }

    public class PersonSetQuery {
        private String          scope;
        private HashSet<String> keys;

        private PersonSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<Person> done() {
            return new PersonListHolder(this.keys, this.scope).done();
        }

        private HashSet<String> lookup_cookie(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.person_cookie.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_device_token(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.person_device_token.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_login(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.person_login.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_notification_token(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.person_notification_token.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_phone(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.person_phone.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_super_cookie(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.person_super_cookie.getKeys(value));
            }
            return keys;
        }

        public PersonSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public PersonListHolder to_list() {
            return new PersonListHolder(this.keys, this.scope);
        }

        public PersonSetQuery where_cookie_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_cookie(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_cookie(values));
            }
            return this;
        }

        public PersonSetQuery where_device_token_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_device_token(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_device_token(values));
            }
            return this;
        }

        public PersonSetQuery where_login_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_login(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_login(values));
            }
            return this;
        }

        public PersonSetQuery where_notification_token_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_notification_token(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_notification_token(values));
            }
            return this;
        }

        public PersonSetQuery where_phone_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_phone(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_phone(values));
            }
            return this;
        }

        public PersonSetQuery where_super_cookie_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_super_cookie(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_super_cookie(values));
            }
            return this;
        }
    }

    public class ProductListHolder {
        private final ArrayList<Product> list;

        private ProductListHolder(final ArrayList<Product> list) {
            this.list = list;
        }

        private ProductListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = products_of(fetch_all("product/" + scope));
            } else {
                this.list = products_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<Product> done() {
            return this.list;
        }

        public Product first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public ProductListHolder fork() {
            return new ProductListHolder(this.list);
        }

        public HashMap<String, ArrayList<Product>> groupBy(final Function<Product, String> f) {
            final StringGroupBy<Product> map = new StringGroupBy<>();
            final Iterator<Product> it = this.list.iterator();
            while (it.hasNext()) {
                final Product v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public ProductListHolder inline_apply(final Consumer<Product> consumer) {
            final Iterator<Product> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public ProductListHolder inline_filter(final Predicate<Product> filter) {
            final Iterator<Product> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public ProductListHolder inline_order_by(final Comparator<Product> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public ProductListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Product>(keys, true, true));
            return this;
        }

        public ProductListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Product>(keys, asc, caseSensitive));
            return this;
        }

        public ProductListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Product>(keys, false, true));
            return this;
        }

        public ProductListHolder limit(final int count) {
            final Iterator<Product> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (product/)
     **************************************************/

    public class ProductProjection_admin {
        private final HashMap<String, String> data;

        public ProductProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
            this.data.put("description", farm.bsg.data.types.TypeString.project(pp, "description"));
            this.data.put("category", farm.bsg.data.types.TypeString.project(pp, "category"));
            this.data.put("customizations", farm.bsg.data.types.TypeString.project(pp, "customizations"));
            this.data.put("price", farm.bsg.data.types.TypeNumber.project(pp, "price"));
            this.data.put("old_price", farm.bsg.data.types.TypeNumber.project(pp, "old_price"));
            this.data.put("image", farm.bsg.data.types.TypeBytesInBase64.project(pp, "image"));
            this.data.put("image_content_type", farm.bsg.data.types.TypeString.project(pp, "image_content_type"));
            this.data.put("image_hash", farm.bsg.data.types.TypeString.project(pp, "image_hash"));
        }

        public PutResult apply(final Product product) {
            return product.validateAndApplyProjection(this.data);
        }
    }

    public class ProductSetQuery {
        private String                scope;
        private final HashSet<String> keys;

        private ProductSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<Product> done() {
            return new ProductListHolder(this.keys, this.scope).done();
        }

        public ProductSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public ProductListHolder to_list() {
            return new ProductListHolder(this.keys, this.scope);
        }
    }

    public class SitePropertiesListHolder {
        private final ArrayList<SiteProperties> list;

        private SitePropertiesListHolder(final ArrayList<SiteProperties> list) {
            this.list = list;
        }

        private SitePropertiesListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = sitepropertiess_of(fetch_all("site/" + scope));
            } else {
                this.list = sitepropertiess_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<SiteProperties> done() {
            return this.list;
        }

        public SiteProperties first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public SitePropertiesListHolder fork() {
            return new SitePropertiesListHolder(this.list);
        }

        public HashMap<String, ArrayList<SiteProperties>> groupBy(final Function<SiteProperties, String> f) {
            final StringGroupBy<SiteProperties> map = new StringGroupBy<>();
            final Iterator<SiteProperties> it = this.list.iterator();
            while (it.hasNext()) {
                final SiteProperties v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public SitePropertiesListHolder inline_apply(final Consumer<SiteProperties> consumer) {
            final Iterator<SiteProperties> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public SitePropertiesListHolder inline_filter(final Predicate<SiteProperties> filter) {
            final Iterator<SiteProperties> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public SitePropertiesListHolder inline_order_by(final Comparator<SiteProperties> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public SitePropertiesListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<SiteProperties>(keys, true, true));
            return this;
        }

        public SitePropertiesListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<SiteProperties>(keys, asc, caseSensitive));
            return this;
        }

        public SitePropertiesListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<SiteProperties>(keys, false, true));
            return this;
        }

        public SitePropertiesListHolder limit(final int count) {
            final Iterator<SiteProperties> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (site/)
     **************************************************/

    public class SitePropertiesProjection_admin {
        private final HashMap<String, String> data;

        public SitePropertiesProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("domain", farm.bsg.data.types.TypeString.project(pp, "domain"));
            this.data.put("product_name", farm.bsg.data.types.TypeString.project(pp, "product_name"));
            this.data.put("fb_page_token", farm.bsg.data.types.TypeString.project(pp, "fb_page_token"));
            this.data.put("twilio_phone_number", farm.bsg.data.types.TypeString.project(pp, "twilio_phone_number"));
            this.data.put("twilio_username", farm.bsg.data.types.TypeString.project(pp, "twilio_username"));
            this.data.put("twilio_password", farm.bsg.data.types.TypeString.project(pp, "twilio_password"));
            this.data.put("admin_phone", farm.bsg.data.types.TypeString.project(pp, "admin_phone"));
            this.data.put("product_imaging_thumbprint_size", farm.bsg.data.types.TypeNumber.project(pp, "product_imaging_thumbprint_size"));
            this.data.put("product_imaging_normal_size", farm.bsg.data.types.TypeNumber.project(pp, "product_imaging_normal_size"));
            this.data.put("description", farm.bsg.data.types.TypeString.project(pp, "description"));
            this.data.put("business_hours", farm.bsg.data.types.TypeString.project(pp, "business_hours"));
            this.data.put("business_phone", farm.bsg.data.types.TypeString.project(pp, "business_phone"));
            this.data.put("business_timezone", farm.bsg.data.types.TypeString.project(pp, "business_timezone"));
            this.data.put("fulfilment_strategy", farm.bsg.data.types.TypeString.project(pp, "fulfilment_strategy"));
            this.data.put("delivery_radius", farm.bsg.data.types.TypeNumber.project(pp, "delivery_radius"));
            this.data.put("pickup_rule", farm.bsg.data.types.TypeString.project(pp, "pickup_rule"));
            this.data.put("business_address1", farm.bsg.data.types.TypeString.project(pp, "business_address1"));
            this.data.put("business_address2", farm.bsg.data.types.TypeString.project(pp, "business_address2"));
            this.data.put("business_city", farm.bsg.data.types.TypeString.project(pp, "business_city"));
            this.data.put("business_state", farm.bsg.data.types.TypeString.project(pp, "business_state"));
            this.data.put("business_postal", farm.bsg.data.types.TypeString.project(pp, "business_postal"));
        }

        public PutResult apply(final SiteProperties siteproperties) {
            return siteproperties.validateAndApplyProjection(this.data);
        }
    }

    public class SitePropertiesSetQuery {
        private String                scope;
        private final HashSet<String> keys;

        private SitePropertiesSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<SiteProperties> done() {
            return new SitePropertiesListHolder(this.keys, this.scope).done();
        }

        public SitePropertiesSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public SitePropertiesListHolder to_list() {
            return new SitePropertiesListHolder(this.keys, this.scope);
        }
    }

    public class SubscriberListHolder {
        private final ArrayList<Subscriber> list;

        private SubscriberListHolder(final ArrayList<Subscriber> list) {
            this.list = list;
        }

        private SubscriberListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = subscribers_of(fetch_all("subscriber/" + scope));
            } else {
                this.list = subscribers_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<Subscriber> done() {
            return this.list;
        }

        public Subscriber first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public SubscriberListHolder fork() {
            return new SubscriberListHolder(this.list);
        }

        public HashMap<String, ArrayList<Subscriber>> groupBy(final Function<Subscriber, String> f) {
            final StringGroupBy<Subscriber> map = new StringGroupBy<>();
            final Iterator<Subscriber> it = this.list.iterator();
            while (it.hasNext()) {
                final Subscriber v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public SubscriberListHolder inline_apply(final Consumer<Subscriber> consumer) {
            final Iterator<Subscriber> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public SubscriberListHolder inline_filter(final Predicate<Subscriber> filter) {
            final Iterator<Subscriber> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public SubscriberListHolder inline_order_by(final Comparator<Subscriber> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public SubscriberListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Subscriber>(keys, true, true));
            return this;
        }

        public SubscriberListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Subscriber>(keys, asc, caseSensitive));
            return this;
        }

        public SubscriberListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Subscriber>(keys, false, true));
            return this;
        }

        public SubscriberListHolder limit(final int count) {
            final Iterator<Subscriber> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (subscriber/)
     **************************************************/

    public class SubscriberProjection_admin {
        private final HashMap<String, String> data;

        public SubscriberProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("source", farm.bsg.data.types.TypeString.project(pp, "source"));
            this.data.put("from", farm.bsg.data.types.TypeString.project(pp, "from"));
            this.data.put("destination", farm.bsg.data.types.TypeString.project(pp, "destination"));
            this.data.put("subscription", farm.bsg.data.types.TypeString.project(pp, "subscription"));
            this.data.put("debug", farm.bsg.data.types.TypeString.project(pp, "debug"));
        }

        public PutResult apply(final Subscriber subscriber) {
            return subscriber.validateAndApplyProjection(this.data);
        }
    }

    public class SubscriberSetQuery {
        private String          scope;
        private HashSet<String> keys;

        private SubscriberSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<Subscriber> done() {
            return new SubscriberListHolder(this.keys, this.scope).done();
        }

        private HashSet<String> lookup_subscription(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.subscriber_subscription.getKeys(value));
            }
            return keys;
        }

        public SubscriberSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public SubscriberListHolder to_list() {
            return new SubscriberListHolder(this.keys, this.scope);
        }

        public SubscriberSetQuery where_subscription_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_subscription(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_subscription(values));
            }
            return this;
        }
    }

    public class SubscriptionListHolder {
        private final ArrayList<Subscription> list;

        private SubscriptionListHolder(final ArrayList<Subscription> list) {
            this.list = list;
        }

        private SubscriptionListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = subscriptions_of(fetch_all("subscription/" + scope));
            } else {
                this.list = subscriptions_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<Subscription> done() {
            return this.list;
        }

        public Subscription first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public SubscriptionListHolder fork() {
            return new SubscriptionListHolder(this.list);
        }

        public HashMap<String, ArrayList<Subscription>> groupBy(final Function<Subscription, String> f) {
            final StringGroupBy<Subscription> map = new StringGroupBy<>();
            final Iterator<Subscription> it = this.list.iterator();
            while (it.hasNext()) {
                final Subscription v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public SubscriptionListHolder inline_apply(final Consumer<Subscription> consumer) {
            final Iterator<Subscription> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public SubscriptionListHolder inline_filter(final Predicate<Subscription> filter) {
            final Iterator<Subscription> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public SubscriptionListHolder inline_order_by(final Comparator<Subscription> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public SubscriptionListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Subscription>(keys, true, true));
            return this;
        }

        public SubscriptionListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Subscription>(keys, asc, caseSensitive));
            return this;
        }

        public SubscriptionListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Subscription>(keys, false, true));
            return this;
        }

        public SubscriptionListHolder limit(final int count) {
            final Iterator<Subscription> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (subscription/)
     **************************************************/

    public class SubscriptionProjection_admin {
        private final HashMap<String, String> data;

        public SubscriptionProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
            this.data.put("description", farm.bsg.data.types.TypeString.project(pp, "description"));
            this.data.put("subscribe_keyword", farm.bsg.data.types.TypeString.project(pp, "subscribe_keyword"));
            this.data.put("subscribe_message", farm.bsg.data.types.TypeString.project(pp, "subscribe_message"));
            this.data.put("unsubscribe_keyword", farm.bsg.data.types.TypeString.project(pp, "unsubscribe_keyword"));
            this.data.put("unsubscribe_message", farm.bsg.data.types.TypeString.project(pp, "unsubscribe_message"));
            this.data.put("event", farm.bsg.data.types.TypeString.project(pp, "event"));
        }

        public PutResult apply(final Subscription subscription) {
            return subscription.validateAndApplyProjection(this.data);
        }
    }

    public class SubscriptionSetQuery {
        private String          scope;
        private HashSet<String> keys;

        private SubscriptionSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<Subscription> done() {
            return new SubscriptionListHolder(this.keys, this.scope).done();
        }

        private HashSet<String> lookup_event(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.subscription_event.getKeys(value));
            }
            return keys;
        }

        public SubscriptionSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public SubscriptionListHolder to_list() {
            return new SubscriptionListHolder(this.keys, this.scope);
        }

        public SubscriptionSetQuery where_event_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_event(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_event(values));
            }
            return this;
        }
    }

    public class TaskFactoryListHolder {
        private final ArrayList<TaskFactory> list;

        private TaskFactoryListHolder(final ArrayList<TaskFactory> list) {
            this.list = list;
        }

        private TaskFactoryListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = taskfactorys_of(fetch_all("task_factory/" + scope));
            } else {
                this.list = taskfactorys_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<TaskFactory> done() {
            return this.list;
        }

        public TaskFactory first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public TaskFactoryListHolder fork() {
            return new TaskFactoryListHolder(this.list);
        }

        public HashMap<String, ArrayList<TaskFactory>> groupBy(final Function<TaskFactory, String> f) {
            final StringGroupBy<TaskFactory> map = new StringGroupBy<>();
            final Iterator<TaskFactory> it = this.list.iterator();
            while (it.hasNext()) {
                final TaskFactory v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public TaskFactoryListHolder inline_apply(final Consumer<TaskFactory> consumer) {
            final Iterator<TaskFactory> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public TaskFactoryListHolder inline_filter(final Predicate<TaskFactory> filter) {
            final Iterator<TaskFactory> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public TaskFactoryListHolder inline_order_by(final Comparator<TaskFactory> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public TaskFactoryListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<TaskFactory>(keys, true, true));
            return this;
        }

        public TaskFactoryListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<TaskFactory>(keys, asc, caseSensitive));
            return this;
        }

        public TaskFactoryListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<TaskFactory>(keys, false, true));
            return this;
        }

        public TaskFactoryListHolder limit(final int count) {
            final Iterator<TaskFactory> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (task_factory/)
     **************************************************/

    public class TaskFactoryProjection_admin {
        private final HashMap<String, String> data;

        public TaskFactoryProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
            this.data.put("description", farm.bsg.data.types.TypeString.project(pp, "description"));
            this.data.put("current_task", farm.bsg.data.types.TypeString.project(pp, "current_task"));
            this.data.put("priority", farm.bsg.data.types.TypeNumber.project(pp, "priority"));
            this.data.put("frequency", farm.bsg.data.types.TypeNumber.project(pp, "frequency"));
            this.data.put("snooze_time", farm.bsg.data.types.TypeNumber.project(pp, "snooze_time"));
            this.data.put("slack", farm.bsg.data.types.TypeNumber.project(pp, "slack"));
            this.data.put("month_filter", farm.bsg.data.types.TypeMonthFilter.project(pp, "month_filter"));
            this.data.put("day_filter", farm.bsg.data.types.TypeDayFilter.project(pp, "day_filter"));
        }

        public PutResult apply(final TaskFactory taskfactory) {
            return taskfactory.validateAndApplyProjection(this.data);
        }
    }

    public class TaskFactoryProjection_edit {
        private final HashMap<String, String> data;

        public TaskFactoryProjection_edit(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
            this.data.put("description", farm.bsg.data.types.TypeString.project(pp, "description"));
            this.data.put("priority", farm.bsg.data.types.TypeNumber.project(pp, "priority"));
            this.data.put("frequency", farm.bsg.data.types.TypeNumber.project(pp, "frequency"));
            this.data.put("snooze_time", farm.bsg.data.types.TypeNumber.project(pp, "snooze_time"));
            this.data.put("slack", farm.bsg.data.types.TypeNumber.project(pp, "slack"));
            this.data.put("month_filter", farm.bsg.data.types.TypeMonthFilter.project(pp, "month_filter"));
            this.data.put("day_filter", farm.bsg.data.types.TypeDayFilter.project(pp, "day_filter"));
        }

        public PutResult apply(final TaskFactory taskfactory) {
            return taskfactory.validateAndApplyProjection(this.data);
        }
    }

    public class TaskFactorySetQuery {
        private String                scope;
        private final HashSet<String> keys;

        private TaskFactorySetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<TaskFactory> done() {
            return new TaskFactoryListHolder(this.keys, this.scope).done();
        }

        public TaskFactorySetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public TaskFactoryListHolder to_list() {
            return new TaskFactoryListHolder(this.keys, this.scope);
        }
    }

    public class TaskListHolder {
        private final ArrayList<Task> list;

        private TaskListHolder(final ArrayList<Task> list) {
            this.list = list;
        }

        private TaskListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = tasks_of(fetch_all("task/" + scope));
            } else {
                this.list = tasks_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<Task> done() {
            return this.list;
        }

        public Task first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public TaskListHolder fork() {
            return new TaskListHolder(this.list);
        }

        public HashMap<String, ArrayList<Task>> groupBy(final Function<Task, String> f) {
            final StringGroupBy<Task> map = new StringGroupBy<>();
            final Iterator<Task> it = this.list.iterator();
            while (it.hasNext()) {
                final Task v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public TaskListHolder inline_apply(final Consumer<Task> consumer) {
            final Iterator<Task> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public TaskListHolder inline_filter(final Predicate<Task> filter) {
            final Iterator<Task> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public TaskListHolder inline_order_by(final Comparator<Task> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public TaskListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Task>(keys, true, true));
            return this;
        }

        public TaskListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Task>(keys, asc, caseSensitive));
            return this;
        }

        public TaskListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<Task>(keys, false, true));
            return this;
        }

        public TaskListHolder limit(final int count) {
            final Iterator<Task> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (task/)
     **************************************************/

    public class TaskProjection_admin {
        private final HashMap<String, String> data;

        public TaskProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("owner", farm.bsg.data.types.TypeString.project(pp, "owner"));
            this.data.put("blocked_by", farm.bsg.data.types.TypeString.project(pp, "blocked_by"));
            this.data.put("cart_id", farm.bsg.data.types.TypeString.project(pp, "cart_id"));
            this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
            this.data.put("description", farm.bsg.data.types.TypeString.project(pp, "description"));
            this.data.put("priority", farm.bsg.data.types.TypeNumber.project(pp, "priority"));
            this.data.put("due_date", farm.bsg.data.types.TypeDateTime.project(pp, "due_date"));
            this.data.put("snooze_time", farm.bsg.data.types.TypeNumber.project(pp, "snooze_time"));
            this.data.put("created", farm.bsg.data.types.TypeDateTime.project(pp, "created"));
            this.data.put("snoozed", farm.bsg.data.types.TypeDateTime.project(pp, "snoozed"));
            this.data.put("closed", farm.bsg.data.types.TypeDateTime.project(pp, "closed"));
            this.data.put("notification_token_for_closed", farm.bsg.data.types.TypeString.project(pp, "notification_token_for_closed"));
            this.data.put("notification_short_text_for_closed", farm.bsg.data.types.TypeString.project(pp, "notification_short_text_for_closed"));
            this.data.put("state", farm.bsg.data.types.TypeString.project(pp, "state"));
        }

        public PutResult apply(final Task task) {
            return task.validateAndApplyProjection(this.data);
        }
    }

    public class TaskSetQuery {
        private String          scope;
        private HashSet<String> keys;

        private TaskSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<Task> done() {
            return new TaskListHolder(this.keys, this.scope).done();
        }

        private HashSet<String> lookup_owner(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.task_owner.getKeys(value));
            }
            return keys;
        }

        private HashSet<String> lookup_state(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.task_state.getKeys(value));
            }
            return keys;
        }

        public TaskSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public TaskListHolder to_list() {
            return new TaskListHolder(this.keys, this.scope);
        }

        public TaskSetQuery where_owner_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_owner(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_owner(values));
            }
            return this;
        }

        public TaskSetQuery where_state_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_state(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_state(values));
            }
            return this;
        }
    }

    public class WakeInputFileListHolder {
        private final ArrayList<WakeInputFile> list;

        private WakeInputFileListHolder(final ArrayList<WakeInputFile> list) {
            this.list = list;
        }

        private WakeInputFileListHolder(final HashSet<String> keys, final String scope) {
            if (keys == null) {
                this.list = wakeinputfiles_of(fetch_all("wake_input/" + scope));
            } else {
                this.list = wakeinputfiles_of(fetch(keys));
            }
        }

        public int count() {
            return this.list.size();
        }

        public ArrayList<WakeInputFile> done() {
            return this.list;
        }

        public WakeInputFile first() {
            if (this.list.size() == 0) {
                return null;
            }
            return this.list.get(0);
        }

        public WakeInputFileListHolder fork() {
            return new WakeInputFileListHolder(this.list);
        }

        public HashMap<String, ArrayList<WakeInputFile>> groupBy(final Function<WakeInputFile, String> f) {
            final StringGroupBy<WakeInputFile> map = new StringGroupBy<>();
            final Iterator<WakeInputFile> it = this.list.iterator();
            while (it.hasNext()) {
                final WakeInputFile v = it.next();
                final String key = f.apply(v);
                map.add(key, v);
            }
            return map.index;
        }

        public WakeInputFileListHolder inline_apply(final Consumer<WakeInputFile> consumer) {
            final Iterator<WakeInputFile> it = this.list.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
            return this;
        }

        public WakeInputFileListHolder inline_filter(final Predicate<WakeInputFile> filter) {
            final Iterator<WakeInputFile> it = this.list.iterator();
            while (it.hasNext()) {
                if (filter.test(it.next())) {
                    it.remove();
                }
            }
            return this;
        }

        public WakeInputFileListHolder inline_order_by(final Comparator<WakeInputFile> comparator) {
            Collections.sort(this.list, comparator);
            return this;
        }

        public WakeInputFileListHolder inline_order_lexographically_asc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<WakeInputFile>(keys, true, true));
            return this;
        }

        public WakeInputFileListHolder inline_order_lexographically_by(final boolean asc, final boolean caseSensitive, final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<WakeInputFile>(keys, asc, caseSensitive));
            return this;
        }

        public WakeInputFileListHolder inline_order_lexographically_desc_by(final String... keys) {
            Collections.sort(this.list, new LexographicalOrder<WakeInputFile>(keys, false, true));
            return this;
        }

        public WakeInputFileListHolder limit(final int count) {
            final Iterator<WakeInputFile> it = this.list.iterator();
            int at = 0;
            while (it.hasNext()) {
                it.next();
                if (at >= count) {
                    it.remove();
                }
                at++;
            }
            return this;
        }
    }

    /**************************************************
     * Projects (wake_input/)
     **************************************************/

    public class WakeInputFileProjection_admin {
        private final HashMap<String, String> data;

        public WakeInputFileProjection_admin(final ProjectionProvider pp) {
            this.data = new HashMap<String, String>();
            this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
            this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
            this.data.put("filename", farm.bsg.data.types.TypeString.project(pp, "filename"));
            this.data.put("content_type", farm.bsg.data.types.TypeString.project(pp, "content_type"));
            this.data.put("description", farm.bsg.data.types.TypeString.project(pp, "description"));
            this.data.put("contents", farm.bsg.data.types.TypeBytesInBase64.project(pp, "contents"));
        }

        public PutResult apply(final WakeInputFile wakeinputfile) {
            return wakeinputfile.validateAndApplyProjection(this.data);
        }
    }

    public class WakeInputFileSetQuery {
        private String          scope;
        private HashSet<String> keys;

        private WakeInputFileSetQuery() {
            this.scope = "";
            this.keys = null;
        }

        public int count() {
            if (this.keys == null) {
                return to_list().count();
            } else {
                return this.keys.size();
            }
        }

        public ArrayList<WakeInputFile> done() {
            return new WakeInputFileListHolder(this.keys, this.scope).done();
        }

        private HashSet<String> lookup_filename(final String... values) {
            final HashSet<String> keys = new HashSet<>();
            for (final String value : values) {
                keys.addAll(QueryEngine.this.wakeinputfile_filename.getKeys(value));
            }
            return keys;
        }

        public WakeInputFileSetQuery scope(final String scope) {
            this.scope += scope + "/";
            return this;
        }

        public WakeInputFileListHolder to_list() {
            return new WakeInputFileListHolder(this.keys, this.scope);
        }

        public WakeInputFileSetQuery where_filename_eq(final String... values) {
            if (this.keys == null) {
                this.keys = lookup_filename(values);
            } else {
                this.keys = BinaryOperators.intersect(this.keys, lookup_filename(values));
            }
            return this;
        }
    }

    public final MultiPrefixLogger        indexing;

    // INDEX[Cart]
    public final KeyIndex                 cart_customer;               // BY[customer]

    public final KeyIndex                 cart_task;                   // BY[task]

    // INDEX[CartItem]
    public final KeyIndex                 cartitem_cart;               // BY[cart]

    public final KeyIndex                 cartitem_product;            // BY[product]

    public final KeyIndex                 cartitem_customizations;     // BY[customizations]

    // INDEX[Check]
    public final KeyIndex                 check_person;                // BY[person]

    public final KeyIndex                 check_fiscal_day;            // BY[fiscal_day]

    public final KeyIndex                 check_ready;                 // BY[ready]

    // INDEX[Customer]
    public final KeyIndex                 customer_email;              // BY[email]

    public final KeyIndex                 customer_phone;              // BY[phone]

    public final KeyIndex                 customer_cookie;             // BY[cookie]

    public final KeyIndex                 customer_notification_token; // BY[notification_token]

    // INDEX[PayrollEntry]
    public final KeyIndex                 payrollentry_check;          // BY[check]

    public final KeyIndex                 payrollentry_unpaid;         // BY[unpaid]

    // INDEX[Person]
    public final KeyIndex                 person_login;                // BY[login]

    public final KeyIndex                 person_phone;                // BY[phone]

    public final KeyIndex                 person_cookie;               // BY[cookie]

    public final KeyIndex                 person_super_cookie;         // BY[super_cookie]

    public final KeyIndex                 person_device_token;         // BY[device_token]

    public final KeyIndex                 person_notification_token;   // BY[notification_token]

    // INDEX[Subscriber]
    public final KeyIndex                 subscriber_subscription;     // BY[subscription]

    // INDEX[Subscription]
    public final KeyIndex                 subscription_event;          // BY[event]

    // INDEX[Task]
    public final KeyIndex                 task_owner;                  // BY[owner]

    public final KeyIndex                 task_state;                  // BY[state]

    // INDEX[WakeInputFile]
    public final KeyIndex                 wakeinputfile_filename;      // BY[filename]

    public final StorageEngine            storage;

    public final ExecutorService          executor;

    public final ScheduledExecutorService scheduler;

    public final UriBlobCache             publicBlobCache;

    public QueryEngine(final PersistenceLogger persistence) throws Exception {
        final InMemoryStorage memory = new InMemoryStorage();
        this.executor = Executors.newFixedThreadPool(2);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.publicBlobCache = new UriBlobCache();
        this.indexing = new MultiPrefixLogger();
        this.cart_customer = this.indexing.add("cart/", new KeyIndex("customer", false));
        this.cart_task = this.indexing.add("cart/", new KeyIndex("task", false));
        this.cartitem_cart = this.indexing.add("cart-item/", new KeyIndex("cart", false));
        this.cartitem_product = this.indexing.add("cart-item/", new KeyIndex("product", false));
        this.cartitem_customizations = this.indexing.add("cart-item/", new KeyIndex("customizations", false));
        this.check_person = this.indexing.add("checks/", new KeyIndex("person", false));
        this.check_fiscal_day = this.indexing.add("checks/", new KeyIndex("fiscal_day", false));
        this.check_ready = this.indexing.add("checks/", new KeyIndex("ready", false));
        this.customer_email = this.indexing.add("customer/", new KeyIndex("email", true));
        this.customer_phone = this.indexing.add("customer/", new KeyIndex("phone", false));
        this.customer_cookie = this.indexing.add("customer/", new KeyIndex("cookie", false));
        this.customer_notification_token = this.indexing.add("customer/", new KeyIndex("notification_token", false));
        this.payrollentry_check = this.indexing.add("payroll/", new KeyIndex("check", false));
        this.payrollentry_unpaid = this.indexing.add("payroll/", new KeyIndex("unpaid", false));
        this.person_login = this.indexing.add("person/", new KeyIndex("login", true));
        this.person_phone = this.indexing.add("person/", new KeyIndex("phone", false));
        this.person_cookie = this.indexing.add("person/", new KeyIndex("cookie", false));
        this.person_super_cookie = this.indexing.add("person/", new KeyIndex("super_cookie", false));
        this.person_device_token = this.indexing.add("person/", new KeyIndex("device_token", false));
        this.person_notification_token = this.indexing.add("person/", new KeyIndex("notification_token", false));
        this.indexing.add("product/", new farm.bsg.models.PublicSiteBuilder(this));
        this.subscriber_subscription = this.indexing.add("subscriber/", new KeyIndex("subscription", false));
        this.subscription_event = this.indexing.add("subscription/", new KeyIndex("event", false));
        this.task_owner = this.indexing.add("task/", new KeyIndex("owner", false));
        this.task_state = this.indexing.add("task/", new KeyIndex("state", false));
        this.wakeinputfile_filename = this.indexing.add("wake_input/", new KeyIndex("filename", true));
        this.indexing.add("wake_input/", new farm.bsg.models.PublicSiteBuilder(this));
        this.storage = new StorageEngine(memory, this.indexing, persistence);
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (baton/)
     **************************************************/

    public Baton baton_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("baton/" + id);
        if (v == null && !create) {
            return null;
        }
        final Baton result = baton_of(v);
        result.set("id", id);
        return result;
    }

    private Baton baton_of(final Value v) {
        final Baton item = new Baton();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<Baton> batons_of(final ArrayList<Value> values) {
        final ArrayList<Baton> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(baton_of(v));
        }
        return list;
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (cart/)
     **************************************************/

    public Cart cart_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("cart/" + id);
        if (v == null && !create) {
            return null;
        }
        final Cart result = cart_of(v);
        result.set("id", id);
        return result;
    }

    private Cart cart_of(final Value v) {
        final Cart item = new Cart();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (cart-item/)
     **************************************************/

    public CartItem cartitem_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("cart-item/" + id);
        if (v == null && !create) {
            return null;
        }
        final CartItem result = cartitem_of(v);
        result.set("id", id);
        return result;
    }

    private CartItem cartitem_of(final Value v) {
        final CartItem item = new CartItem();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<CartItem> cartitems_of(final ArrayList<Value> values) {
        final ArrayList<CartItem> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(cartitem_of(v));
        }
        return list;
    }

    private ArrayList<Cart> carts_of(final ArrayList<Value> values) {
        final ArrayList<Cart> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(cart_of(v));
        }
        return list;
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (checks/)
     **************************************************/

    public Check check_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("checks/" + id);
        if (v == null && !create) {
            return null;
        }
        final Check result = check_of(v);
        result.set("id", id);
        return result;
    }

    private Check check_of(final Value v) {
        final Check item = new Check();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<Check> checks_of(final ArrayList<Value> values) {
        final ArrayList<Check> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(check_of(v));
        }
        return list;
    }

    /**************************************************
     * Indexing (baton/)
     **************************************************/

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (customer/)
     **************************************************/

    public Customer customer_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("customer/" + id);
        if (v == null && !create) {
            return null;
        }
        final Customer result = customer_of(v);
        result.set("id", id);
        return result;
    }

    private Customer customer_of(final Value v) {
        final Customer item = new Customer();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<Customer> customers_of(final ArrayList<Value> values) {
        final ArrayList<Customer> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(customer_of(v));
        }
        return list;
    }

    public PutResult del(final Baton baton) {
        return this.storage.put(baton.getStorageKey(), null, false);
    }

    public PutResult del(final Cart cart) {
        return this.storage.put(cart.getStorageKey(), null, false);
    }

    public PutResult del(final CartItem cartitem) {
        return this.storage.put(cartitem.getStorageKey(), null, false);
    }

    public PutResult del(final Check check) {
        return this.storage.put(check.getStorageKey(), null, false);
    }

    public PutResult del(final Customer customer) {
        return this.storage.put(customer.getStorageKey(), null, false);
    }

    public PutResult del(final PayrollEntry payrollentry) {
        return this.storage.put(payrollentry.getStorageKey(), null, false);
    }

    public PutResult del(final Person person) {
        return this.storage.put(person.getStorageKey(), null, false);
    }

    public PutResult del(final Product product) {
        return this.storage.put(product.getStorageKey(), null, false);
    }

    public PutResult del(final SiteProperties siteproperties) {
        return this.storage.put(siteproperties.getStorageKey(), null, false);
    }

    public PutResult del(final Subscriber subscriber) {
        return this.storage.put(subscriber.getStorageKey(), null, false);
    }

    public PutResult del(final Subscription subscription) {
        return this.storage.put(subscription.getStorageKey(), null, false);
    }

    public PutResult del(final Task task) {
        return this.storage.put(task.getStorageKey(), null, false);
    }

    public PutResult del(final TaskFactory taskfactory) {
        return this.storage.put(taskfactory.getStorageKey(), null, false);
    }

    public PutResult del(final WakeInputFile wakeinputfile) {
        return this.storage.put(wakeinputfile.getStorageKey(), null, false);
    }

    private ArrayList<Value> fetch(final HashSet<String> keys) {
        final ArrayList<Value> values = new ArrayList<>();
        if (keys == null) {
            return values;
        }
        for (final String key : keys) {
            final Value value = this.storage.get(key);
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }

    private ArrayList<Value> fetch_all(final String prefix) {
        final ArrayList<Value> values = new ArrayList<>();
        values.addAll(this.storage.scan(prefix).values());
        return values;
    }

    /**************************************************
     * Indexing (cart/)
     **************************************************/

    public HashSet<String> get_cart_customer_index_keys() {
        return this.cart_customer.getIndexKeys();
    }

    /**************************************************
     * Indexing (product/)
     **************************************************/

    /**************************************************
     * Indexing (site/)
     **************************************************/

    public HashSet<String> get_cart_task_index_keys() {
        return this.cart_task.getIndexKeys();
    }

    /**************************************************
     * Indexing (cart-item/)
     **************************************************/

    public HashSet<String> get_cartitem_cart_index_keys() {
        return this.cartitem_cart.getIndexKeys();
    }

    public HashSet<String> get_cartitem_customizations_index_keys() {
        return this.cartitem_customizations.getIndexKeys();
    }

    public HashSet<String> get_cartitem_product_index_keys() {
        return this.cartitem_product.getIndexKeys();
    }

    /**************************************************
     * Indexing (task_factory/)
     **************************************************/

    public HashSet<String> get_check_fiscal_day_index_keys() {
        return this.check_fiscal_day.getIndexKeys();
    }

    /**************************************************
     * Indexing (checks/)
     **************************************************/

    public HashSet<String> get_check_person_index_keys() {
        return this.check_person.getIndexKeys();
    }

    public HashSet<String> get_check_ready_index_keys() {
        return this.check_ready.getIndexKeys();
    }

    public HashSet<String> get_customer_cookie_index_keys() {
        return this.customer_cookie.getIndexKeys();
    }

    /**************************************************
     * Indexing (customer/)
     **************************************************/

    public HashSet<String> get_customer_email_index_keys() {
        return this.customer_email.getIndexKeys();
    }

    public HashSet<String> get_customer_notification_token_index_keys() {
        return this.customer_notification_token.getIndexKeys();
    }

    public HashSet<String> get_customer_phone_index_keys() {
        return this.customer_phone.getIndexKeys();
    }

    /**************************************************
     * Indexing (payroll/)
     **************************************************/

    public HashSet<String> get_payrollentry_check_index_keys() {
        return this.payrollentry_check.getIndexKeys();
    }

    public HashSet<String> get_payrollentry_unpaid_index_keys() {
        return this.payrollentry_unpaid.getIndexKeys();
    }

    public HashSet<String> get_person_cookie_index_keys() {
        return this.person_cookie.getIndexKeys();
    }

    public HashSet<String> get_person_device_token_index_keys() {
        return this.person_device_token.getIndexKeys();
    }

    /**************************************************
     * Indexing (person/)
     **************************************************/

    public HashSet<String> get_person_login_index_keys() {
        return this.person_login.getIndexKeys();
    }

    public HashSet<String> get_person_notification_token_index_keys() {
        return this.person_notification_token.getIndexKeys();
    }

    public HashSet<String> get_person_phone_index_keys() {
        return this.person_phone.getIndexKeys();
    }

    public HashSet<String> get_person_super_cookie_index_keys() {
        return this.person_super_cookie.getIndexKeys();
    }

    /**************************************************
     * Indexing (subscriber/)
     **************************************************/

    public HashSet<String> get_subscriber_subscription_index_keys() {
        return this.subscriber_subscription.getIndexKeys();
    }

    /**************************************************
     * Indexing (subscription/)
     **************************************************/

    public HashSet<String> get_subscription_event_index_keys() {
        return this.subscription_event.getIndexKeys();
    }

    /**************************************************
     * Indexing (task/)
     **************************************************/

    public HashSet<String> get_task_owner_index_keys() {
        return this.task_owner.getIndexKeys();
    }

    public HashSet<String> get_task_state_index_keys() {
        return this.task_state.getIndexKeys();
    }

    /**************************************************
     * Indexing (wake_input/)
     **************************************************/

    public HashSet<String> get_wakeinputfile_filename_index_keys() {
        return this.wakeinputfile_filename.getIndexKeys();
    }

    public String make_key_baton(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("baton/");
        key.append(id);
        return key.toString();
    }

    public String make_key_cart(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("cart/");
        key.append(id);
        return key.toString();
    }

    public String make_key_cartitem(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("cart-item/");
        key.append(id);
        return key.toString();
    }

    public String make_key_check(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("checks/");
        key.append(id);
        return key.toString();
    }

    public String make_key_customer(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("customer/");
        key.append(id);
        return key.toString();
    }

    public String make_key_payrollentry(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("payroll/");
        key.append(id);
        return key.toString();
    }

    public String make_key_person(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("person/");
        key.append(id);
        return key.toString();
    }

    public String make_key_product(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("product/");
        key.append(id);
        return key.toString();
    }

    public String make_key_subscriber(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("subscriber/");
        key.append(id);
        return key.toString();
    }

    public String make_key_subscription(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("subscription/");
        key.append(id);
        return key.toString();
    }

    public String make_key_task(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("task/");
        key.append(id);
        return key.toString();
    }

    public String make_key_taskfactory(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("task_factory/");
        key.append(id);
        return key.toString();
    }

    public String make_key_wakeinputfile(final String id) {
        final StringBuilder key = new StringBuilder();
        key.append("wake_input/");
        key.append(id);
        return key.toString();
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (payroll/)
     **************************************************/

    public PayrollEntry payrollentry_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("payroll/" + id);
        if (v == null && !create) {
            return null;
        }
        final PayrollEntry result = payrollentry_of(v);
        result.set("id", id);
        return result;
    }

    private PayrollEntry payrollentry_of(final Value v) {
        final PayrollEntry item = new PayrollEntry();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<PayrollEntry> payrollentrys_of(final ArrayList<Value> values) {
        final ArrayList<PayrollEntry> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(payrollentry_of(v));
        }
        return list;
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (person/)
     **************************************************/

    public Person person_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("person/" + id);
        if (v == null && !create) {
            return null;
        }
        final Person result = person_of(v);
        result.set("id", id);
        return result;
    }

    private Person person_of(final Value v) {
        final Person item = new Person();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<Person> persons_of(final ArrayList<Value> values) {
        final ArrayList<Person> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(person_of(v));
        }
        return list;
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (product/)
     **************************************************/

    public Product product_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("product/" + id);
        if (v == null && !create) {
            return null;
        }
        final Product result = product_of(v);
        result.set("id", id);
        return result;
    }

    private Product product_of(final Value v) {
        final Product item = new Product();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<Product> products_of(final ArrayList<Value> values) {
        final ArrayList<Product> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(product_of(v));
        }
        return list;
    }

    public BatonProjection_admin projection_baton_admin_of(final ProjectionProvider pp) {
        return new BatonProjection_admin(pp);
    }

    public CartProjection_admin projection_cart_admin_of(final ProjectionProvider pp) {
        return new CartProjection_admin(pp);
    }

    public CartItemProjection_admin projection_cartitem_admin_of(final ProjectionProvider pp) {
        return new CartItemProjection_admin(pp);
    }

    public CheckProjection_admin projection_check_admin_of(final ProjectionProvider pp) {
        return new CheckProjection_admin(pp);
    }

    public CustomerProjection_admin projection_customer_admin_of(final ProjectionProvider pp) {
        return new CustomerProjection_admin(pp);
    }

    public PayrollEntryProjection_admin projection_payrollentry_admin_of(final ProjectionProvider pp) {
        return new PayrollEntryProjection_admin(pp);
    }

    public PayrollEntryProjection_edit projection_payrollentry_edit_of(final ProjectionProvider pp) {
        return new PayrollEntryProjection_edit(pp);
    }

    public PersonProjection_admin projection_person_admin_of(final ProjectionProvider pp) {
        return new PersonProjection_admin(pp);
    }

    public PersonProjection_contact_info projection_person_contact_info_of(final ProjectionProvider pp) {
        return new PersonProjection_contact_info(pp);
    }

    public ProductProjection_admin projection_product_admin_of(final ProjectionProvider pp) {
        return new ProductProjection_admin(pp);
    }

    public SitePropertiesProjection_admin projection_siteproperties_admin_of(final ProjectionProvider pp) {
        return new SitePropertiesProjection_admin(pp);
    }

    public SubscriberProjection_admin projection_subscriber_admin_of(final ProjectionProvider pp) {
        return new SubscriberProjection_admin(pp);
    }

    public SubscriptionProjection_admin projection_subscription_admin_of(final ProjectionProvider pp) {
        return new SubscriptionProjection_admin(pp);
    }

    public TaskProjection_admin projection_task_admin_of(final ProjectionProvider pp) {
        return new TaskProjection_admin(pp);
    }

    public TaskFactoryProjection_admin projection_taskfactory_admin_of(final ProjectionProvider pp) {
        return new TaskFactoryProjection_admin(pp);
    }

    public TaskFactoryProjection_edit projection_taskfactory_edit_of(final ProjectionProvider pp) {
        return new TaskFactoryProjection_edit(pp);
    }

    public WakeInputFileProjection_admin projection_wakeinputfile_admin_of(final ProjectionProvider pp) {
        return new WakeInputFileProjection_admin(pp);
    }

    /**************************************************
     * Writing Back to DB (baton/)
     **************************************************/

    public PutResult put(final Baton baton) {
        return this.storage.put(baton.getStorageKey(), new Value(baton.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (cart/)
     **************************************************/

    public PutResult put(final Cart cart) {
        return this.storage.put(cart.getStorageKey(), new Value(cart.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (cart-item/)
     **************************************************/

    public PutResult put(final CartItem cartitem) {
        return this.storage.put(cartitem.getStorageKey(), new Value(cartitem.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (checks/)
     **************************************************/

    public PutResult put(final Check check) {
        return this.storage.put(check.getStorageKey(), new Value(check.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (customer/)
     **************************************************/

    public PutResult put(final Customer customer) {
        return this.storage.put(customer.getStorageKey(), new Value(customer.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (payroll/)
     **************************************************/

    public PutResult put(final PayrollEntry payrollentry) {
        return this.storage.put(payrollentry.getStorageKey(), new Value(payrollentry.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (person/)
     **************************************************/

    public PutResult put(final Person person) {
        return this.storage.put(person.getStorageKey(), new Value(person.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (product/)
     **************************************************/

    public PutResult put(final Product product) {
        return this.storage.put(product.getStorageKey(), new Value(product.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (site/)
     **************************************************/

    public PutResult put(final SiteProperties siteproperties) {
        return this.storage.put(siteproperties.getStorageKey(), new Value(siteproperties.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (subscriber/)
     **************************************************/

    public PutResult put(final Subscriber subscriber) {
        return this.storage.put(subscriber.getStorageKey(), new Value(subscriber.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (subscription/)
     **************************************************/

    public PutResult put(final Subscription subscription) {
        return this.storage.put(subscription.getStorageKey(), new Value(subscription.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (task/)
     **************************************************/

    public PutResult put(final Task task) {
        return this.storage.put(task.getStorageKey(), new Value(task.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (task_factory/)
     **************************************************/

    public PutResult put(final TaskFactory taskfactory) {
        return this.storage.put(taskfactory.getStorageKey(), new Value(taskfactory.toJson()), false);
    }

    /**************************************************
     * Writing Back to DB (wake_input/)
     **************************************************/

    public PutResult put(final WakeInputFile wakeinputfile) {
        return this.storage.put(wakeinputfile.getStorageKey(), new Value(wakeinputfile.toJson()), false);
    }

    /**************************************************
     * Query Engine (baton/)
     **************************************************/

    public BatonSetQuery select_baton() {
        return new BatonSetQuery();
    }

    /**************************************************
     * Query Engine (cart/)
     **************************************************/

    public CartSetQuery select_cart() {
        return new CartSetQuery();
    }

    /**************************************************
     * Query Engine (cart-item/)
     **************************************************/

    public CartItemSetQuery select_cartitem() {
        return new CartItemSetQuery();
    }

    /**************************************************
     * Query Engine (checks/)
     **************************************************/

    public CheckSetQuery select_check() {
        return new CheckSetQuery();
    }

    /**************************************************
     * Query Engine (customer/)
     **************************************************/

    public CustomerSetQuery select_customer() {
        return new CustomerSetQuery();
    }

    /**************************************************
     * Query Engine (payroll/)
     **************************************************/

    public PayrollEntrySetQuery select_payrollentry() {
        return new PayrollEntrySetQuery();
    }

    /**************************************************
     * Query Engine (person/)
     **************************************************/

    public PersonSetQuery select_person() {
        return new PersonSetQuery();
    }

    /**************************************************
     * Query Engine (product/)
     **************************************************/

    public ProductSetQuery select_product() {
        return new ProductSetQuery();
    }

    /**************************************************
     * Query Engine (site/)
     **************************************************/

    public SitePropertiesSetQuery select_siteproperties() {
        return new SitePropertiesSetQuery();
    }

    /**************************************************
     * Query Engine (subscriber/)
     **************************************************/

    public SubscriberSetQuery select_subscriber() {
        return new SubscriberSetQuery();
    }

    /**************************************************
     * Query Engine (subscription/)
     **************************************************/

    public SubscriptionSetQuery select_subscription() {
        return new SubscriptionSetQuery();
    }

    /**************************************************
     * Query Engine (task/)
     **************************************************/

    public TaskSetQuery select_task() {
        return new TaskSetQuery();
    }

    /**************************************************
     * Query Engine (task_factory/)
     **************************************************/

    public TaskFactorySetQuery select_taskfactory() {
        return new TaskFactorySetQuery();
    }

    /**************************************************
     * Query Engine (wake_input/)
     **************************************************/

    public WakeInputFileSetQuery select_wakeinputfile() {
        return new WakeInputFileSetQuery();
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (site/)
     **************************************************/

    public SiteProperties siteproperties_get() {
        final Value v = this.storage.get("site/properties");
        final SiteProperties result = siteproperties_of(v);
        result.set("id", "properties");
        return result;
    }

    private SiteProperties siteproperties_of(final Value v) {
        final SiteProperties item = new SiteProperties();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<SiteProperties> sitepropertiess_of(final ArrayList<Value> values) {
        final ArrayList<SiteProperties> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(siteproperties_of(v));
        }
        return list;
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (subscriber/)
     **************************************************/

    public Subscriber subscriber_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("subscriber/" + id);
        if (v == null && !create) {
            return null;
        }
        final Subscriber result = subscriber_of(v);
        result.set("id", id);
        return result;
    }

    private Subscriber subscriber_of(final Value v) {
        final Subscriber item = new Subscriber();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<Subscriber> subscribers_of(final ArrayList<Value> values) {
        final ArrayList<Subscriber> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(subscriber_of(v));
        }
        return list;
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (subscription/)
     **************************************************/

    public Subscription subscription_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("subscription/" + id);
        if (v == null && !create) {
            return null;
        }
        final Subscription result = subscription_of(v);
        result.set("id", id);
        return result;
    }

    private Subscription subscription_of(final Value v) {
        final Subscription item = new Subscription();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<Subscription> subscriptions_of(final ArrayList<Value> values) {
        final ArrayList<Subscription> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(subscription_of(v));
        }
        return list;
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (task/)
     **************************************************/

    public Task task_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("task/" + id);
        if (v == null && !create) {
            return null;
        }
        final Task result = task_of(v);
        result.set("id", id);
        return result;
    }

    private Task task_of(final Value v) {
        final Task item = new Task();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (task_factory/)
     **************************************************/

    public TaskFactory taskfactory_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("task_factory/" + id);
        if (v == null && !create) {
            return null;
        }
        final TaskFactory result = taskfactory_of(v);
        result.set("id", id);
        return result;
    }

    private TaskFactory taskfactory_of(final Value v) {
        final TaskFactory item = new TaskFactory();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<TaskFactory> taskfactorys_of(final ArrayList<Value> values) {
        final ArrayList<TaskFactory> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(taskfactory_of(v));
        }
        return list;
    }

    private ArrayList<Task> tasks_of(final ArrayList<Value> values) {
        final ArrayList<Task> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(task_of(v));
        }
        return list;
    }

    /**************************************************
     * Basic Operations (look up, type transfer, keys, immutable copies) (wake_input/)
     **************************************************/

    public WakeInputFile wakeinputfile_by_id(final String id, final boolean create) {
        final Value v = this.storage.get("wake_input/" + id);
        if (v == null && !create) {
            return null;
        }
        final WakeInputFile result = wakeinputfile_of(v);
        result.set("id", id);
        return result;
    }

    private WakeInputFile wakeinputfile_of(final Value v) {
        final WakeInputFile item = new WakeInputFile();
        if (v != null) {
            item.injectValue(v);
        }
        return item;
    }

    private ArrayList<WakeInputFile> wakeinputfiles_of(final ArrayList<Value> values) {
        final ArrayList<WakeInputFile> list = new ArrayList<>(values.size());
        for (final Value v : values) {
            list.add(wakeinputfile_of(v));
        }
        return list;
    }
}
