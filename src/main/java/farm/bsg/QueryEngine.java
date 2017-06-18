package farm.bsg;

import farm.bsg.data.*;
import farm.bsg.data.KeyIndex;
import farm.bsg.data.MultiPrefixLogger;

import farm.bsg.data.contracts.*;

import farm.bsg.models.Cart;
import farm.bsg.models.CartItem;
import farm.bsg.models.Check;
import farm.bsg.models.Chore;
import farm.bsg.models.Event;
import farm.bsg.models.Habit;
import farm.bsg.models.PayrollEntry;
import farm.bsg.models.Person;
import farm.bsg.models.Product;
import farm.bsg.models.SiteProperties;
import farm.bsg.models.Subscriber;
import farm.bsg.models.Subscription;
import farm.bsg.models.WakeInputFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import java.util.function.Consumer;
import java.util.function.Predicate;

/****************************************************************
WARNING: Generated
This class is a generated database query engine that provides an
easy to take a simple Persistence mechanism into a real DB
****************************************************************/
public class QueryEngine {
  public final MultiPrefixLogger indexing;

  // INDEX[Cart]
  public final KeyIndex cart_user;  // BY[user]

  // INDEX[CartItem]
  public final KeyIndex cartitem_cart;  // BY[cart]
  public final KeyIndex cartitem_product;  // BY[product]
  public final KeyIndex cartitem_customizations;  // BY[customizations]

  // INDEX[Check]
  public final KeyIndex check_person;  // BY[person]
  public final KeyIndex check_fiscal_day;  // BY[fiscal_day]
  public final KeyIndex check_ready;  // BY[ready]

  // INDEX[PayrollEntry]
  public final KeyIndex payrollentry_check;  // BY[check]
  public final KeyIndex payrollentry_unpaid;  // BY[unpaid]

  // INDEX[Person]
  public final KeyIndex person_login;  // BY[login]
  public final KeyIndex person_phone;  // BY[phone]
  public final KeyIndex person_super_cookie;  // BY[super_cookie]
  public final KeyIndex person_notification_token;  // BY[notification_token]
  public final StorageEngine storage;

  public QueryEngine(PersistenceLogger persistence) throws Exception {
    InMemoryStorage memory = new InMemoryStorage();
    this.indexing = new MultiPrefixLogger();
    this.cart_user = indexing.add("cart/", new KeyIndex("user", false));
    this.cartitem_cart = indexing.add("cart-item/", new KeyIndex("cart", false));
    this.cartitem_product = indexing.add("cart-item/", new KeyIndex("product", false));
    this.cartitem_customizations = indexing.add("cart-item/", new KeyIndex("customizations", false));
    this.check_person = indexing.add("checks/", new KeyIndex("person", false));
    this.check_fiscal_day = indexing.add("checks/", new KeyIndex("fiscal_day", false));
    this.check_ready = indexing.add("checks/", new KeyIndex("ready", false));
    this.payrollentry_check = indexing.add("payroll/", new KeyIndex("check", false));
    this.payrollentry_unpaid = indexing.add("payroll/", new KeyIndex("unpaid", false));
    this.person_login = indexing.add("person/", new KeyIndex("login", true));
    this.person_phone = indexing.add("person/", new KeyIndex("phone", false));
    this.person_super_cookie = indexing.add("person/", new KeyIndex("super_cookie", false));
    this.person_notification_token = indexing.add("person/", new KeyIndex("notification_token", false));
    this.indexing.add("wake_input/", new farm.bsg.models.WakeInputFile.DirtyWakeInputFile());
    this.storage = new StorageEngine(memory, indexing, persistence);
  }

  private ArrayList<Value> fetch_all(String prefix) {
    ArrayList<Value> values = new ArrayList<>();
    values.addAll(storage.scan(prefix).values());
    return values;
  }

  private ArrayList<Value> fetch(HashSet<String> keys) {
    ArrayList<Value> values = new ArrayList<>();
    if (keys == null) {
      return values;
    }
    for (String key : keys) {
      Value value = storage.get(key);
        if (value != null) {
          values.add(value);
        }
      }
    return values;
  }

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (cart/)
  **************************************************/

  public Cart cart_by_id(String id, boolean create) {
    Value v = storage.get("cart/" + id);
    if (v == null && !create) {
      return null;
    }
    Cart result = cart_of(v);
    result.set("id", id);
    return result;
  }

  private Cart cart_of(Value v) {
    Cart item = new Cart();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<Cart> carts_of(ArrayList<Value> values) {
    ArrayList<Cart> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(cart_of(v));
    }
    return list;
  }

  public String make_key_cart(String id) {
    StringBuilder key = new StringBuilder();
    key.append("cart/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (cart-item/)
  **************************************************/

  public CartItem cartitem_by_id(String id, boolean create) {
    Value v = storage.get("cart-item/" + id);
    if (v == null && !create) {
      return null;
    }
    CartItem result = cartitem_of(v);
    result.set("id", id);
    return result;
  }

  private CartItem cartitem_of(Value v) {
    CartItem item = new CartItem();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<CartItem> cartitems_of(ArrayList<Value> values) {
    ArrayList<CartItem> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(cartitem_of(v));
    }
    return list;
  }

  public String make_key_cartitem(String id) {
    StringBuilder key = new StringBuilder();
    key.append("cart-item/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (checks/)
  **************************************************/

  public Check check_by_id(String id, boolean create) {
    Value v = storage.get("checks/" + id);
    if (v == null && !create) {
      return null;
    }
    Check result = check_of(v);
    result.set("id", id);
    return result;
  }

  private Check check_of(Value v) {
    Check item = new Check();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<Check> checks_of(ArrayList<Value> values) {
    ArrayList<Check> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(check_of(v));
    }
    return list;
  }

  public String make_key_check(String id) {
    StringBuilder key = new StringBuilder();
    key.append("checks/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (chore/)
  **************************************************/

  public Chore chore_by_id(String id, boolean create) {
    Value v = storage.get("chore/" + id);
    if (v == null && !create) {
      return null;
    }
    Chore result = chore_of(v);
    result.set("id", id);
    return result;
  }

  private Chore chore_of(Value v) {
    Chore item = new Chore();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<Chore> chores_of(ArrayList<Value> values) {
    ArrayList<Chore> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(chore_of(v));
    }
    return list;
  }

  public String make_key_chore(String id) {
    StringBuilder key = new StringBuilder();
    key.append("chore/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (event/)
  **************************************************/

  public Event event_by_id(String id, boolean create) {
    Value v = storage.get("event/" + id);
    if (v == null && !create) {
      return null;
    }
    Event result = event_of(v);
    result.set("id", id);
    return result;
  }

  private Event event_of(Value v) {
    Event item = new Event();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<Event> events_of(ArrayList<Value> values) {
    ArrayList<Event> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(event_of(v));
    }
    return list;
  }

  public String make_key_event(String id) {
    StringBuilder key = new StringBuilder();
    key.append("event/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (habits/)
  **************************************************/

  public Habit habit_by_id(String id, boolean create) {
    Value v = storage.get("habits/" + id);
    if (v == null && !create) {
      return null;
    }
    Habit result = habit_of(v);
    result.set("id", id);
    return result;
  }

  private Habit habit_of(Value v) {
    Habit item = new Habit();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<Habit> habits_of(ArrayList<Value> values) {
    ArrayList<Habit> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(habit_of(v));
    }
    return list;
  }

  public String make_key_habit(String who, String id) {
    StringBuilder key = new StringBuilder();
    key.append("habits/");
    key.append(who);
    key.append("/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (payroll/)
  **************************************************/

  public PayrollEntry payrollentry_by_id(String id, boolean create) {
    Value v = storage.get("payroll/" + id);
    if (v == null && !create) {
      return null;
    }
    PayrollEntry result = payrollentry_of(v);
    result.set("id", id);
    return result;
  }

  private PayrollEntry payrollentry_of(Value v) {
    PayrollEntry item = new PayrollEntry();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<PayrollEntry> payrollentrys_of(ArrayList<Value> values) {
    ArrayList<PayrollEntry> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(payrollentry_of(v));
    }
    return list;
  }

  public String make_key_payrollentry(String id) {
    StringBuilder key = new StringBuilder();
    key.append("payroll/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (person/)
  **************************************************/

  public Person person_by_id(String id, boolean create) {
    Value v = storage.get("person/" + id);
    if (v == null && !create) {
      return null;
    }
    Person result = person_of(v);
    result.set("id", id);
    return result;
  }

  private Person person_of(Value v) {
    Person item = new Person();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<Person> persons_of(ArrayList<Value> values) {
    ArrayList<Person> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(person_of(v));
    }
    return list;
  }

  public String make_key_person(String id) {
    StringBuilder key = new StringBuilder();
    key.append("person/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (product/)
  **************************************************/

  public Product product_by_id(String id, boolean create) {
    Value v = storage.get("product/" + id);
    if (v == null && !create) {
      return null;
    }
    Product result = product_of(v);
    result.set("id", id);
    return result;
  }

  private Product product_of(Value v) {
    Product item = new Product();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<Product> products_of(ArrayList<Value> values) {
    ArrayList<Product> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(product_of(v));
    }
    return list;
  }

  public String make_key_product(String id) {
    StringBuilder key = new StringBuilder();
    key.append("product/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (site/)
  **************************************************/

  public SiteProperties siteproperties_by_id(String id, boolean create) {
    Value v = storage.get("site/" + id);
    if (v == null && !create) {
      return null;
    }
    SiteProperties result = siteproperties_of(v);
    result.set("id", id);
    return result;
  }

  private SiteProperties siteproperties_of(Value v) {
    SiteProperties item = new SiteProperties();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<SiteProperties> sitepropertiess_of(ArrayList<Value> values) {
    ArrayList<SiteProperties> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(siteproperties_of(v));
    }
    return list;
  }

  public String make_key_siteproperties(String id) {
    StringBuilder key = new StringBuilder();
    key.append("site/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (subscriber/)
  **************************************************/

  public Subscriber subscriber_by_id(String id, boolean create) {
    Value v = storage.get("subscriber/" + id);
    if (v == null && !create) {
      return null;
    }
    Subscriber result = subscriber_of(v);
    result.set("id", id);
    return result;
  }

  private Subscriber subscriber_of(Value v) {
    Subscriber item = new Subscriber();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<Subscriber> subscribers_of(ArrayList<Value> values) {
    ArrayList<Subscriber> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(subscriber_of(v));
    }
    return list;
  }

  public String make_key_subscriber(String id) {
    StringBuilder key = new StringBuilder();
    key.append("subscriber/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (subscription/)
  **************************************************/

  public Subscription subscription_by_id(String id, boolean create) {
    Value v = storage.get("subscription/" + id);
    if (v == null && !create) {
      return null;
    }
    Subscription result = subscription_of(v);
    result.set("id", id);
    return result;
  }

  private Subscription subscription_of(Value v) {
    Subscription item = new Subscription();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<Subscription> subscriptions_of(ArrayList<Value> values) {
    ArrayList<Subscription> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(subscription_of(v));
    }
    return list;
  }

  public String make_key_subscription(String id) {
    StringBuilder key = new StringBuilder();
    key.append("subscription/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Basic Operations (look up, type transfer, keys, immutable copies) (wake_input/)
  **************************************************/

  public WakeInputFile wakeinputfile_by_id(String id, boolean create) {
    Value v = storage.get("wake_input/" + id);
    if (v == null && !create) {
      return null;
    }
    WakeInputFile result = wakeinputfile_of(v);
    result.set("id", id);
    return result;
  }

  private WakeInputFile wakeinputfile_of(Value v) {
    WakeInputFile item = new WakeInputFile();
    if (v != null) {
      item.injectValue(v);
    }
    return item;
  }

  private ArrayList<WakeInputFile> wakeinputfiles_of(ArrayList<Value> values) {
    ArrayList<WakeInputFile> list = new ArrayList<>(values.size());
    for (Value v : values) {
      list.add(wakeinputfile_of(v));
    }
    return list;
  }

  public String make_key_wakeinputfile(String id) {
    StringBuilder key = new StringBuilder();
    key.append("wake_input/");
    key.append(id);
    return key.toString();
}

  /**************************************************
  Indexing (cart/)
  **************************************************/

  public HashSet<String> get_cart_user_index_keys() {
    return cart_user.getIndexKeys();
  }

  /**************************************************
  Indexing (cart-item/)
  **************************************************/

  public HashSet<String> get_cartitem_cart_index_keys() {
    return cartitem_cart.getIndexKeys();
  }

  public HashSet<String> get_cartitem_product_index_keys() {
    return cartitem_product.getIndexKeys();
  }

  public HashSet<String> get_cartitem_customizations_index_keys() {
    return cartitem_customizations.getIndexKeys();
  }

  /**************************************************
  Indexing (checks/)
  **************************************************/

  public HashSet<String> get_check_person_index_keys() {
    return check_person.getIndexKeys();
  }

  public HashSet<String> get_check_fiscal_day_index_keys() {
    return check_fiscal_day.getIndexKeys();
  }

  public HashSet<String> get_check_ready_index_keys() {
    return check_ready.getIndexKeys();
  }

  /**************************************************
  Indexing (chore/)
  **************************************************/

  /**************************************************
  Indexing (event/)
  **************************************************/

  /**************************************************
  Indexing (habits/)
  **************************************************/

  /**************************************************
  Indexing (payroll/)
  **************************************************/

  public HashSet<String> get_payrollentry_check_index_keys() {
    return payrollentry_check.getIndexKeys();
  }

  public HashSet<String> get_payrollentry_unpaid_index_keys() {
    return payrollentry_unpaid.getIndexKeys();
  }

  /**************************************************
  Indexing (person/)
  **************************************************/

  public HashSet<String> get_person_login_index_keys() {
    return person_login.getIndexKeys();
  }

  public HashSet<String> get_person_phone_index_keys() {
    return person_phone.getIndexKeys();
  }

  public HashSet<String> get_person_super_cookie_index_keys() {
    return person_super_cookie.getIndexKeys();
  }

  public HashSet<String> get_person_notification_token_index_keys() {
    return person_notification_token.getIndexKeys();
  }

  /**************************************************
  Indexing (product/)
  **************************************************/

  /**************************************************
  Indexing (site/)
  **************************************************/

  /**************************************************
  Indexing (subscriber/)
  **************************************************/

  /**************************************************
  Indexing (subscription/)
  **************************************************/

  /**************************************************
  Indexing (wake_input/)
  **************************************************/

  /**************************************************
  Query Engine (cart/)
  **************************************************/

  public CartSetQuery select_cart() {
    return new CartSetQuery();
  }

  public class CartListHolder {
    private final ArrayList<Cart> list;

    private CartListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = carts_of(fetch_all("cart/" + scope));
      } else {
        this.list = carts_of(fetch(keys));
      }
    }

    private CartListHolder(ArrayList<Cart> list) {
      this.list = list;
    }

    public CartListHolder inline_filter(Predicate<Cart> filter) {
      Iterator<Cart> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public CartListHolder limit(int count) {
      Iterator<Cart> it = list.iterator();
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

    public CartListHolder inline_apply(Consumer<Cart> consumer) {
      Iterator<Cart> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public CartListHolder fork() {
      return new CartListHolder(this.list);
    }

    public CartListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Cart>(keys, true, true));
      return this;
    }

    public CartListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Cart>(keys, false, true));
      return this;
    }

    public CartListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Cart>(keys, asc, caseSensitive));
      return this;
    }

    public CartListHolder inline_order_by(Comparator<Cart> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public Cart first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<Cart> done() {
      return this.list;
    }
  }

  public class CartSetQuery {
    private String scope;
    private HashSet<String> keys;

    private CartSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public CartListHolder to_list() {
      return new CartListHolder(this.keys, this.scope);
    }

    public ArrayList<Cart> done() {
      return new CartListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public CartSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }

    private HashSet<String> lookup_user(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(cart_user.getKeys(value));
      }
      return keys;
    }

    public CartSetQuery where_user_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_user(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_user(values));
      }
      return this;
    }
  }

  /**************************************************
  Query Engine (cart-item/)
  **************************************************/

  public CartItemSetQuery select_cartitem() {
    return new CartItemSetQuery();
  }

  public class CartItemListHolder {
    private final ArrayList<CartItem> list;

    private CartItemListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = cartitems_of(fetch_all("cart-item/" + scope));
      } else {
        this.list = cartitems_of(fetch(keys));
      }
    }

    private CartItemListHolder(ArrayList<CartItem> list) {
      this.list = list;
    }

    public CartItemListHolder inline_filter(Predicate<CartItem> filter) {
      Iterator<CartItem> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public CartItemListHolder limit(int count) {
      Iterator<CartItem> it = list.iterator();
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

    public CartItemListHolder inline_apply(Consumer<CartItem> consumer) {
      Iterator<CartItem> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public CartItemListHolder fork() {
      return new CartItemListHolder(this.list);
    }

    public CartItemListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<CartItem>(keys, true, true));
      return this;
    }

    public CartItemListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<CartItem>(keys, false, true));
      return this;
    }

    public CartItemListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<CartItem>(keys, asc, caseSensitive));
      return this;
    }

    public CartItemListHolder inline_order_by(Comparator<CartItem> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public CartItem first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<CartItem> done() {
      return this.list;
    }
  }

  public class CartItemSetQuery {
    private String scope;
    private HashSet<String> keys;

    private CartItemSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public CartItemListHolder to_list() {
      return new CartItemListHolder(this.keys, this.scope);
    }

    public ArrayList<CartItem> done() {
      return new CartItemListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public CartItemSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }

    private HashSet<String> lookup_cart(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(cartitem_cart.getKeys(value));
      }
      return keys;
    }

    public CartItemSetQuery where_cart_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_cart(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_cart(values));
      }
      return this;
    }

    private HashSet<String> lookup_product(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(cartitem_product.getKeys(value));
      }
      return keys;
    }

    public CartItemSetQuery where_product_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_product(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_product(values));
      }
      return this;
    }

    private HashSet<String> lookup_customizations(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(cartitem_customizations.getKeys(value));
      }
      return keys;
    }

    public CartItemSetQuery where_customizations_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_customizations(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_customizations(values));
      }
      return this;
    }
  }

  /**************************************************
  Query Engine (checks/)
  **************************************************/

  public CheckSetQuery select_check() {
    return new CheckSetQuery();
  }

  public class CheckListHolder {
    private final ArrayList<Check> list;

    private CheckListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = checks_of(fetch_all("checks/" + scope));
      } else {
        this.list = checks_of(fetch(keys));
      }
    }

    private CheckListHolder(ArrayList<Check> list) {
      this.list = list;
    }

    public CheckListHolder inline_filter(Predicate<Check> filter) {
      Iterator<Check> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public CheckListHolder limit(int count) {
      Iterator<Check> it = list.iterator();
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

    public CheckListHolder inline_apply(Consumer<Check> consumer) {
      Iterator<Check> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public CheckListHolder fork() {
      return new CheckListHolder(this.list);
    }

    public CheckListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Check>(keys, true, true));
      return this;
    }

    public CheckListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Check>(keys, false, true));
      return this;
    }

    public CheckListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Check>(keys, asc, caseSensitive));
      return this;
    }

    public CheckListHolder inline_order_by(Comparator<Check> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public Check first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<Check> done() {
      return this.list;
    }
  }

  public class CheckSetQuery {
    private String scope;
    private HashSet<String> keys;

    private CheckSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public CheckListHolder to_list() {
      return new CheckListHolder(this.keys, this.scope);
    }

    public ArrayList<Check> done() {
      return new CheckListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public CheckSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }

    private HashSet<String> lookup_person(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(check_person.getKeys(value));
      }
      return keys;
    }

    public CheckSetQuery where_person_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_person(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_person(values));
      }
      return this;
    }

    private HashSet<String> lookup_fiscal_day(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(check_fiscal_day.getKeys(value));
      }
      return keys;
    }

    public CheckSetQuery where_fiscal_day_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_fiscal_day(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_fiscal_day(values));
      }
      return this;
    }

    private HashSet<String> lookup_ready(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(check_ready.getKeys(value));
      }
      return keys;
    }

    public CheckSetQuery where_ready_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_ready(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_ready(values));
      }
      return this;
    }
  }

  /**************************************************
  Query Engine (chore/)
  **************************************************/

  public ChoreSetQuery select_chore() {
    return new ChoreSetQuery();
  }

  public class ChoreListHolder {
    private final ArrayList<Chore> list;

    private ChoreListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = chores_of(fetch_all("chore/" + scope));
      } else {
        this.list = chores_of(fetch(keys));
      }
    }

    private ChoreListHolder(ArrayList<Chore> list) {
      this.list = list;
    }

    public ChoreListHolder inline_filter(Predicate<Chore> filter) {
      Iterator<Chore> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public ChoreListHolder limit(int count) {
      Iterator<Chore> it = list.iterator();
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

    public ChoreListHolder inline_apply(Consumer<Chore> consumer) {
      Iterator<Chore> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public ChoreListHolder fork() {
      return new ChoreListHolder(this.list);
    }

    public ChoreListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Chore>(keys, true, true));
      return this;
    }

    public ChoreListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Chore>(keys, false, true));
      return this;
    }

    public ChoreListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Chore>(keys, asc, caseSensitive));
      return this;
    }

    public ChoreListHolder inline_order_by(Comparator<Chore> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public Chore first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<Chore> done() {
      return this.list;
    }
  }

  public class ChoreSetQuery {
    private String scope;
    private HashSet<String> keys;

    private ChoreSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public ChoreListHolder to_list() {
      return new ChoreListHolder(this.keys, this.scope);
    }

    public ArrayList<Chore> done() {
      return new ChoreListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public ChoreSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }
  }

  /**************************************************
  Query Engine (event/)
  **************************************************/

  public EventSetQuery select_event() {
    return new EventSetQuery();
  }

  public class EventListHolder {
    private final ArrayList<Event> list;

    private EventListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = events_of(fetch_all("event/" + scope));
      } else {
        this.list = events_of(fetch(keys));
      }
    }

    private EventListHolder(ArrayList<Event> list) {
      this.list = list;
    }

    public EventListHolder inline_filter(Predicate<Event> filter) {
      Iterator<Event> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public EventListHolder limit(int count) {
      Iterator<Event> it = list.iterator();
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

    public EventListHolder inline_apply(Consumer<Event> consumer) {
      Iterator<Event> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public EventListHolder fork() {
      return new EventListHolder(this.list);
    }

    public EventListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Event>(keys, true, true));
      return this;
    }

    public EventListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Event>(keys, false, true));
      return this;
    }

    public EventListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Event>(keys, asc, caseSensitive));
      return this;
    }

    public EventListHolder inline_order_by(Comparator<Event> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public Event first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<Event> done() {
      return this.list;
    }
  }

  public class EventSetQuery {
    private String scope;
    private HashSet<String> keys;

    private EventSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public EventListHolder to_list() {
      return new EventListHolder(this.keys, this.scope);
    }

    public ArrayList<Event> done() {
      return new EventListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public EventSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }
  }

  /**************************************************
  Query Engine (habits/)
  **************************************************/

  public HabitSetQuery select_habit() {
    return new HabitSetQuery();
  }

  public class HabitListHolder {
    private final ArrayList<Habit> list;

    private HabitListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = habits_of(fetch_all("habits/" + scope));
      } else {
        this.list = habits_of(fetch(keys));
      }
    }

    private HabitListHolder(ArrayList<Habit> list) {
      this.list = list;
    }

    public HabitListHolder inline_filter(Predicate<Habit> filter) {
      Iterator<Habit> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public HabitListHolder limit(int count) {
      Iterator<Habit> it = list.iterator();
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

    public HabitListHolder inline_apply(Consumer<Habit> consumer) {
      Iterator<Habit> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public HabitListHolder fork() {
      return new HabitListHolder(this.list);
    }

    public HabitListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Habit>(keys, true, true));
      return this;
    }

    public HabitListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Habit>(keys, false, true));
      return this;
    }

    public HabitListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Habit>(keys, asc, caseSensitive));
      return this;
    }

    public HabitListHolder inline_order_by(Comparator<Habit> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public Habit first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<Habit> done() {
      return this.list;
    }
  }

  public class HabitSetQuery {
    private String scope;
    private HashSet<String> keys;

    private HabitSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public HabitListHolder to_list() {
      return new HabitListHolder(this.keys, this.scope);
    }

    public ArrayList<Habit> done() {
      return new HabitListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public HabitSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }
  }

  /**************************************************
  Query Engine (payroll/)
  **************************************************/

  public PayrollEntrySetQuery select_payrollentry() {
    return new PayrollEntrySetQuery();
  }

  public class PayrollEntryListHolder {
    private final ArrayList<PayrollEntry> list;

    private PayrollEntryListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = payrollentrys_of(fetch_all("payroll/" + scope));
      } else {
        this.list = payrollentrys_of(fetch(keys));
      }
    }

    private PayrollEntryListHolder(ArrayList<PayrollEntry> list) {
      this.list = list;
    }

    public PayrollEntryListHolder inline_filter(Predicate<PayrollEntry> filter) {
      Iterator<PayrollEntry> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public PayrollEntryListHolder limit(int count) {
      Iterator<PayrollEntry> it = list.iterator();
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

    public PayrollEntryListHolder inline_apply(Consumer<PayrollEntry> consumer) {
      Iterator<PayrollEntry> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public PayrollEntryListHolder fork() {
      return new PayrollEntryListHolder(this.list);
    }

    public PayrollEntryListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<PayrollEntry>(keys, true, true));
      return this;
    }

    public PayrollEntryListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<PayrollEntry>(keys, false, true));
      return this;
    }

    public PayrollEntryListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<PayrollEntry>(keys, asc, caseSensitive));
      return this;
    }

    public PayrollEntryListHolder inline_order_by(Comparator<PayrollEntry> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public PayrollEntry first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<PayrollEntry> done() {
      return this.list;
    }
  }

  public class PayrollEntrySetQuery {
    private String scope;
    private HashSet<String> keys;

    private PayrollEntrySetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public PayrollEntryListHolder to_list() {
      return new PayrollEntryListHolder(this.keys, this.scope);
    }

    public ArrayList<PayrollEntry> done() {
      return new PayrollEntryListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public PayrollEntrySetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }

    private HashSet<String> lookup_check(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(payrollentry_check.getKeys(value));
      }
      return keys;
    }

    public PayrollEntrySetQuery where_check_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_check(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_check(values));
      }
      return this;
    }

    private HashSet<String> lookup_unpaid(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(payrollentry_unpaid.getKeys(value));
      }
      return keys;
    }

    public PayrollEntrySetQuery where_unpaid_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_unpaid(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_unpaid(values));
      }
      return this;
    }
  }

  /**************************************************
  Query Engine (person/)
  **************************************************/

  public PersonSetQuery select_person() {
    return new PersonSetQuery();
  }

  public class PersonListHolder {
    private final ArrayList<Person> list;

    private PersonListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = persons_of(fetch_all("person/" + scope));
      } else {
        this.list = persons_of(fetch(keys));
      }
    }

    private PersonListHolder(ArrayList<Person> list) {
      this.list = list;
    }

    public PersonListHolder inline_filter(Predicate<Person> filter) {
      Iterator<Person> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public PersonListHolder limit(int count) {
      Iterator<Person> it = list.iterator();
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

    public PersonListHolder inline_apply(Consumer<Person> consumer) {
      Iterator<Person> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public PersonListHolder fork() {
      return new PersonListHolder(this.list);
    }

    public PersonListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Person>(keys, true, true));
      return this;
    }

    public PersonListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Person>(keys, false, true));
      return this;
    }

    public PersonListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Person>(keys, asc, caseSensitive));
      return this;
    }

    public PersonListHolder inline_order_by(Comparator<Person> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public Person first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<Person> done() {
      return this.list;
    }
  }

  public class PersonSetQuery {
    private String scope;
    private HashSet<String> keys;

    private PersonSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public PersonListHolder to_list() {
      return new PersonListHolder(this.keys, this.scope);
    }

    public ArrayList<Person> done() {
      return new PersonListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public PersonSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }

    private HashSet<String> lookup_login(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(person_login.getKeys(value));
      }
      return keys;
    }

    public PersonSetQuery where_login_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_login(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_login(values));
      }
      return this;
    }

    private HashSet<String> lookup_phone(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(person_phone.getKeys(value));
      }
      return keys;
    }

    public PersonSetQuery where_phone_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_phone(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_phone(values));
      }
      return this;
    }

    private HashSet<String> lookup_super_cookie(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(person_super_cookie.getKeys(value));
      }
      return keys;
    }

    public PersonSetQuery where_super_cookie_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_super_cookie(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_super_cookie(values));
      }
      return this;
    }

    private HashSet<String> lookup_notification_token(String... values) {
      HashSet<String> keys = new HashSet<>();
      for(String value : values) {
        keys.addAll(person_notification_token.getKeys(value));
      }
      return keys;
    }

    public PersonSetQuery where_notification_token_eq(String... values) {
      if (this.keys == null) {
        this.keys = lookup_notification_token(values);
      } else {
        this.keys = BinaryOperators.intersect(this.keys, lookup_notification_token(values));
      }
      return this;
    }
  }

  /**************************************************
  Query Engine (product/)
  **************************************************/

  public ProductSetQuery select_product() {
    return new ProductSetQuery();
  }

  public class ProductListHolder {
    private final ArrayList<Product> list;

    private ProductListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = products_of(fetch_all("product/" + scope));
      } else {
        this.list = products_of(fetch(keys));
      }
    }

    private ProductListHolder(ArrayList<Product> list) {
      this.list = list;
    }

    public ProductListHolder inline_filter(Predicate<Product> filter) {
      Iterator<Product> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public ProductListHolder limit(int count) {
      Iterator<Product> it = list.iterator();
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

    public ProductListHolder inline_apply(Consumer<Product> consumer) {
      Iterator<Product> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public ProductListHolder fork() {
      return new ProductListHolder(this.list);
    }

    public ProductListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Product>(keys, true, true));
      return this;
    }

    public ProductListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Product>(keys, false, true));
      return this;
    }

    public ProductListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Product>(keys, asc, caseSensitive));
      return this;
    }

    public ProductListHolder inline_order_by(Comparator<Product> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public Product first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<Product> done() {
      return this.list;
    }
  }

  public class ProductSetQuery {
    private String scope;
    private HashSet<String> keys;

    private ProductSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public ProductListHolder to_list() {
      return new ProductListHolder(this.keys, this.scope);
    }

    public ArrayList<Product> done() {
      return new ProductListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public ProductSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }
  }

  /**************************************************
  Query Engine (site/)
  **************************************************/

  public SitePropertiesSetQuery select_siteproperties() {
    return new SitePropertiesSetQuery();
  }

  public class SitePropertiesListHolder {
    private final ArrayList<SiteProperties> list;

    private SitePropertiesListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = sitepropertiess_of(fetch_all("site/" + scope));
      } else {
        this.list = sitepropertiess_of(fetch(keys));
      }
    }

    private SitePropertiesListHolder(ArrayList<SiteProperties> list) {
      this.list = list;
    }

    public SitePropertiesListHolder inline_filter(Predicate<SiteProperties> filter) {
      Iterator<SiteProperties> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public SitePropertiesListHolder limit(int count) {
      Iterator<SiteProperties> it = list.iterator();
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

    public SitePropertiesListHolder inline_apply(Consumer<SiteProperties> consumer) {
      Iterator<SiteProperties> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public SitePropertiesListHolder fork() {
      return new SitePropertiesListHolder(this.list);
    }

    public SitePropertiesListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<SiteProperties>(keys, true, true));
      return this;
    }

    public SitePropertiesListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<SiteProperties>(keys, false, true));
      return this;
    }

    public SitePropertiesListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<SiteProperties>(keys, asc, caseSensitive));
      return this;
    }

    public SitePropertiesListHolder inline_order_by(Comparator<SiteProperties> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public SiteProperties first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<SiteProperties> done() {
      return this.list;
    }
  }

  public class SitePropertiesSetQuery {
    private String scope;
    private HashSet<String> keys;

    private SitePropertiesSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public SitePropertiesListHolder to_list() {
      return new SitePropertiesListHolder(this.keys, this.scope);
    }

    public ArrayList<SiteProperties> done() {
      return new SitePropertiesListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public SitePropertiesSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }
  }

  /**************************************************
  Query Engine (subscriber/)
  **************************************************/

  public SubscriberSetQuery select_subscriber() {
    return new SubscriberSetQuery();
  }

  public class SubscriberListHolder {
    private final ArrayList<Subscriber> list;

    private SubscriberListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = subscribers_of(fetch_all("subscriber/" + scope));
      } else {
        this.list = subscribers_of(fetch(keys));
      }
    }

    private SubscriberListHolder(ArrayList<Subscriber> list) {
      this.list = list;
    }

    public SubscriberListHolder inline_filter(Predicate<Subscriber> filter) {
      Iterator<Subscriber> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public SubscriberListHolder limit(int count) {
      Iterator<Subscriber> it = list.iterator();
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

    public SubscriberListHolder inline_apply(Consumer<Subscriber> consumer) {
      Iterator<Subscriber> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public SubscriberListHolder fork() {
      return new SubscriberListHolder(this.list);
    }

    public SubscriberListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Subscriber>(keys, true, true));
      return this;
    }

    public SubscriberListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Subscriber>(keys, false, true));
      return this;
    }

    public SubscriberListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Subscriber>(keys, asc, caseSensitive));
      return this;
    }

    public SubscriberListHolder inline_order_by(Comparator<Subscriber> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public Subscriber first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<Subscriber> done() {
      return this.list;
    }
  }

  public class SubscriberSetQuery {
    private String scope;
    private HashSet<String> keys;

    private SubscriberSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public SubscriberListHolder to_list() {
      return new SubscriberListHolder(this.keys, this.scope);
    }

    public ArrayList<Subscriber> done() {
      return new SubscriberListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public SubscriberSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }
  }

  /**************************************************
  Query Engine (subscription/)
  **************************************************/

  public SubscriptionSetQuery select_subscription() {
    return new SubscriptionSetQuery();
  }

  public class SubscriptionListHolder {
    private final ArrayList<Subscription> list;

    private SubscriptionListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = subscriptions_of(fetch_all("subscription/" + scope));
      } else {
        this.list = subscriptions_of(fetch(keys));
      }
    }

    private SubscriptionListHolder(ArrayList<Subscription> list) {
      this.list = list;
    }

    public SubscriptionListHolder inline_filter(Predicate<Subscription> filter) {
      Iterator<Subscription> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public SubscriptionListHolder limit(int count) {
      Iterator<Subscription> it = list.iterator();
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

    public SubscriptionListHolder inline_apply(Consumer<Subscription> consumer) {
      Iterator<Subscription> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public SubscriptionListHolder fork() {
      return new SubscriptionListHolder(this.list);
    }

    public SubscriptionListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Subscription>(keys, true, true));
      return this;
    }

    public SubscriptionListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Subscription>(keys, false, true));
      return this;
    }

    public SubscriptionListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<Subscription>(keys, asc, caseSensitive));
      return this;
    }

    public SubscriptionListHolder inline_order_by(Comparator<Subscription> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public Subscription first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<Subscription> done() {
      return this.list;
    }
  }

  public class SubscriptionSetQuery {
    private String scope;
    private HashSet<String> keys;

    private SubscriptionSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public SubscriptionListHolder to_list() {
      return new SubscriptionListHolder(this.keys, this.scope);
    }

    public ArrayList<Subscription> done() {
      return new SubscriptionListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public SubscriptionSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }
  }

  /**************************************************
  Query Engine (wake_input/)
  **************************************************/

  public WakeInputFileSetQuery select_wakeinputfile() {
    return new WakeInputFileSetQuery();
  }

  public class WakeInputFileListHolder {
    private final ArrayList<WakeInputFile> list;

    private WakeInputFileListHolder(HashSet<String> keys, String scope) {
      if (keys == null) {
        this.list = wakeinputfiles_of(fetch_all("wake_input/" + scope));
      } else {
        this.list = wakeinputfiles_of(fetch(keys));
      }
    }

    private WakeInputFileListHolder(ArrayList<WakeInputFile> list) {
      this.list = list;
    }

    public WakeInputFileListHolder inline_filter(Predicate<WakeInputFile> filter) {
      Iterator<WakeInputFile> it = list.iterator();
      while (it.hasNext()) {
        if (filter.test(it.next())) {
          it.remove();
        }
      }
      return this;
    }

    public WakeInputFileListHolder limit(int count) {
      Iterator<WakeInputFile> it = list.iterator();
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

    public WakeInputFileListHolder inline_apply(Consumer<WakeInputFile> consumer) {
      Iterator<WakeInputFile> it = list.iterator();
      while (it.hasNext()) {
        consumer.accept(it.next());
      }
      return this;
    }

    public WakeInputFileListHolder fork() {
      return new WakeInputFileListHolder(this.list);
    }

    public WakeInputFileListHolder inline_order_lexographically_asc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<WakeInputFile>(keys, true, true));
      return this;
    }

    public WakeInputFileListHolder inline_order_lexographically_desc_by(String... keys) {
      Collections.sort(this.list, new LexographicalOrder<WakeInputFile>(keys, false, true));
      return this;
    }

    public WakeInputFileListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {
      Collections.sort(this.list, new LexographicalOrder<WakeInputFile>(keys, asc, caseSensitive));
      return this;
    }

    public WakeInputFileListHolder inline_order_by(Comparator<WakeInputFile> comparator) {
      Collections.sort(this.list, comparator);
      return this;
    }

    public int count() {
      return this.list.size();
    }
    public WakeInputFile first() {
      if (this.list.size() == 0) {
        return null;
      }
      return this.list.get(0);
    }
    public ArrayList<WakeInputFile> done() {
      return this.list;
    }
  }

  public class WakeInputFileSetQuery {
    private String scope;
    private HashSet<String> keys;

    private WakeInputFileSetQuery() {
      this.scope = "";
      this.keys = null;
    }

    public WakeInputFileListHolder to_list() {
      return new WakeInputFileListHolder(this.keys, this.scope);
    }

    public ArrayList<WakeInputFile> done() {
      return new WakeInputFileListHolder(this.keys, this.scope).done();
    }

    public int count() {
      if (this.keys == null) {
        return to_list().count();
      } else {
        return this.keys.size();
      }
    }

    public WakeInputFileSetQuery scope(String scope) {
      this.scope += scope + "/";
      return this;
    }
  }

  /**************************************************
  Projects (cart/)
  **************************************************/

  public class CartProjection_admin {
    private final HashMap<String, String> data;
    
    public CartProjection_admin(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
      this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
      this.data.put("user", farm.bsg.data.types.TypeString.project(pp, "user"));
    }

    public PutResult apply(Cart cart) {
      return cart.validateAndApplyProjection(this.data);
    }
  }

  public CartProjection_admin projection_cart_admin_of(ProjectionProvider pp) {
    return new CartProjection_admin(pp);
  }


  /**************************************************
  Projects (cart-item/)
  **************************************************/

  public class CartItemProjection_admin {
    private final HashMap<String, String> data;
    
    public CartItemProjection_admin(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
      this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
      this.data.put("cart", farm.bsg.data.types.TypeString.project(pp, "cart"));
      this.data.put("product", farm.bsg.data.types.TypeString.project(pp, "product"));
      this.data.put("quantiy", farm.bsg.data.types.TypeNumber.project(pp, "quantiy"));
      this.data.put("customizations", farm.bsg.data.types.TypeString.project(pp, "customizations"));
    }

    public PutResult apply(CartItem cartitem) {
      return cartitem.validateAndApplyProjection(this.data);
    }
  }

  public CartItemProjection_admin projection_cartitem_admin_of(ProjectionProvider pp) {
    return new CartItemProjection_admin(pp);
  }


  /**************************************************
  Projects (checks/)
  **************************************************/

  public class CheckProjection_admin {
    private final HashMap<String, String> data;
    
    public CheckProjection_admin(ProjectionProvider pp) {
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
    }

    public PutResult apply(Check check) {
      return check.validateAndApplyProjection(this.data);
    }
  }

  public CheckProjection_admin projection_check_admin_of(ProjectionProvider pp) {
    return new CheckProjection_admin(pp);
  }


  /**************************************************
  Projects (chore/)
  **************************************************/

  public class ChoreProjection_admin {
    private final HashMap<String, String> data;
    
    public ChoreProjection_admin(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
      this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
      this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
      this.data.put("last_performed", farm.bsg.data.types.TypeString.project(pp, "last_performed"));
      this.data.put("last_performed_by", farm.bsg.data.types.TypeString.project(pp, "last_performed_by"));
      this.data.put("frequency", farm.bsg.data.types.TypeString.project(pp, "frequency"));
      this.data.put("slack", farm.bsg.data.types.TypeString.project(pp, "slack"));
      this.data.put("month_filter", farm.bsg.data.types.TypeMonthFilter.project(pp, "month_filter"));
      this.data.put("day_filter", farm.bsg.data.types.TypeDayFilter.project(pp, "day_filter"));
      this.data.put("time_to_perform_hours", farm.bsg.data.types.TypeString.project(pp, "time_to_perform_hours"));
      this.data.put("equipment_skills_required", farm.bsg.data.types.TypeString.project(pp, "equipment_skills_required"));
      this.data.put("weather_requirements", farm.bsg.data.types.TypeString.project(pp, "weather_requirements"));
      this.data.put("hour_filter", farm.bsg.data.types.TypeString.project(pp, "hour_filter"));
      this.data.put("manual", farm.bsg.data.types.TypeString.project(pp, "manual"));
    }

    public PutResult apply(Chore chore) {
      return chore.validateAndApplyProjection(this.data);
    }
  }

  public ChoreProjection_admin projection_chore_admin_of(ProjectionProvider pp) {
    return new ChoreProjection_admin(pp);
  }


  public class ChoreProjection_edit {
    private final HashMap<String, String> data;
    
    public ChoreProjection_edit(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
      this.data.put("frequency", farm.bsg.data.types.TypeString.project(pp, "frequency"));
      this.data.put("slack", farm.bsg.data.types.TypeString.project(pp, "slack"));
      this.data.put("month_filter", farm.bsg.data.types.TypeMonthFilter.project(pp, "month_filter"));
      this.data.put("day_filter", farm.bsg.data.types.TypeDayFilter.project(pp, "day_filter"));
      this.data.put("time_to_perform_hours", farm.bsg.data.types.TypeString.project(pp, "time_to_perform_hours"));
      this.data.put("equipment_skills_required", farm.bsg.data.types.TypeString.project(pp, "equipment_skills_required"));
      this.data.put("weather_requirements", farm.bsg.data.types.TypeString.project(pp, "weather_requirements"));
      this.data.put("hour_filter", farm.bsg.data.types.TypeString.project(pp, "hour_filter"));
      this.data.put("manual", farm.bsg.data.types.TypeString.project(pp, "manual"));
    }

    public PutResult apply(Chore chore) {
      return chore.validateAndApplyProjection(this.data);
    }
  }

  public ChoreProjection_edit projection_chore_edit_of(ProjectionProvider pp) {
    return new ChoreProjection_edit(pp);
  }


  /**************************************************
  Projects (event/)
  **************************************************/

  public class EventProjection_admin {
    private final HashMap<String, String> data;
    
    public EventProjection_admin(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
      this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
      this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
      this.data.put("when", farm.bsg.data.types.TypeString.project(pp, "when"));
    }

    public PutResult apply(Event event) {
      return event.validateAndApplyProjection(this.data);
    }
  }

  public EventProjection_admin projection_event_admin_of(ProjectionProvider pp) {
    return new EventProjection_admin(pp);
  }


  /**************************************************
  Projects (habits/)
  **************************************************/

  public class HabitProjection_admin {
    private final HashMap<String, String> data;
    
    public HabitProjection_admin(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
      this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
      this.data.put("who", farm.bsg.data.types.TypeString.project(pp, "who"));
      this.data.put("last_done", farm.bsg.data.types.TypeString.project(pp, "last_done"));
      this.data.put("unlock_time", farm.bsg.data.types.TypeString.project(pp, "unlock_time"));
      this.data.put("warn_time", farm.bsg.data.types.TypeString.project(pp, "warn_time"));
      this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
      this.data.put("has_arg", farm.bsg.data.types.TypeString.project(pp, "has_arg"));
      this.data.put("last_arg_given", farm.bsg.data.types.TypeString.project(pp, "last_arg_given"));
      this.data.put("history", farm.bsg.data.types.TypeString.project(pp, "history"));
    }

    public PutResult apply(Habit habit) {
      return habit.validateAndApplyProjection(this.data);
    }
  }

  public HabitProjection_admin projection_habit_admin_of(ProjectionProvider pp) {
    return new HabitProjection_admin(pp);
  }


  public class HabitProjection_edit {
    private final HashMap<String, String> data;
    
    public HabitProjection_edit(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("unlock_time", farm.bsg.data.types.TypeString.project(pp, "unlock_time"));
      this.data.put("warn_time", farm.bsg.data.types.TypeString.project(pp, "warn_time"));
      this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
      this.data.put("has_arg", farm.bsg.data.types.TypeString.project(pp, "has_arg"));
    }

    public PutResult apply(Habit habit) {
      return habit.validateAndApplyProjection(this.data);
    }
  }

  public HabitProjection_edit projection_habit_edit_of(ProjectionProvider pp) {
    return new HabitProjection_edit(pp);
  }


  /**************************************************
  Projects (payroll/)
  **************************************************/

  public class PayrollEntryProjection_admin {
    private final HashMap<String, String> data;
    
    public PayrollEntryProjection_admin(ProjectionProvider pp) {
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
      this.data.put("owed", farm.bsg.data.types.TypeNumber.project(pp, "owed"));
      this.data.put("tax_withholding", farm.bsg.data.types.TypeNumber.project(pp, "tax_withholding"));
      this.data.put("taxes", farm.bsg.data.types.TypeNumber.project(pp, "taxes"));
      this.data.put("benefits", farm.bsg.data.types.TypeNumber.project(pp, "benefits"));
      this.data.put("is_performance_related_bonus", farm.bsg.data.types.TypeBoolean.project(pp, "is_performance_related_bonus"));
      this.data.put("check", farm.bsg.data.types.TypeString.project(pp, "check"));
      this.data.put("unpaid", farm.bsg.data.types.TypeString.project(pp, "unpaid"));
    }

    public PutResult apply(PayrollEntry payrollentry) {
      return payrollentry.validateAndApplyProjection(this.data);
    }
  }

  public PayrollEntryProjection_admin projection_payrollentry_admin_of(ProjectionProvider pp) {
    return new PayrollEntryProjection_admin(pp);
  }


  public class PayrollEntryProjection_edit {
    private final HashMap<String, String> data;
    
    public PayrollEntryProjection_edit(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("mileage", farm.bsg.data.types.TypeNumber.project(pp, "mileage"));
      this.data.put("hours_worked", farm.bsg.data.types.TypeNumber.project(pp, "hours_worked"));
      this.data.put("pto_used", farm.bsg.data.types.TypeNumber.project(pp, "pto_used"));
      this.data.put("sick_leave_used", farm.bsg.data.types.TypeNumber.project(pp, "sick_leave_used"));
    }

    public PutResult apply(PayrollEntry payrollentry) {
      return payrollentry.validateAndApplyProjection(this.data);
    }
  }

  public PayrollEntryProjection_edit projection_payrollentry_edit_of(ProjectionProvider pp) {
    return new PayrollEntryProjection_edit(pp);
  }


  /**************************************************
  Projects (person/)
  **************************************************/

  public class PersonProjection_admin {
    private final HashMap<String, String> data;
    
    public PersonProjection_admin(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
      this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
      this.data.put("login", farm.bsg.data.types.TypeString.project(pp, "login"));
      this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
      this.data.put("phone", farm.bsg.data.types.TypeString.project(pp, "phone"));
      this.data.put("email", farm.bsg.data.types.TypeString.project(pp, "email"));
      this.data.put("salt", farm.bsg.data.types.TypeString.project(pp, "salt"));
      this.data.put("hash", farm.bsg.data.types.TypeString.project(pp, "hash"));
      this.data.put("super_cookie", farm.bsg.data.types.TypeString.project(pp, "super_cookie"));
      this.data.put("notification_token", farm.bsg.data.types.TypeString.project(pp, "notification_token"));
      this.data.put("notification_uri", farm.bsg.data.types.TypeString.project(pp, "notification_uri"));
      this.data.put("country", farm.bsg.data.types.TypeString.project(pp, "country"));
      this.data.put("fiscal_timezone", farm.bsg.data.types.TypeString.project(pp, "fiscal_timezone"));
      this.data.put("default_mileage", farm.bsg.data.types.TypeNumber.project(pp, "default_mileage"));
      this.data.put("hourly_wage_compesation", farm.bsg.data.types.TypeNumber.project(pp, "hourly_wage_compesation"));
      this.data.put("mileage_compensation", farm.bsg.data.types.TypeNumber.project(pp, "mileage_compensation"));
      this.data.put("bonus_target", farm.bsg.data.types.TypeNumber.project(pp, "bonus_target"));
      this.data.put("min_performance_multiplier", farm.bsg.data.types.TypeNumber.project(pp, "min_performance_multiplier"));
      this.data.put("max_performance_multiplier", farm.bsg.data.types.TypeNumber.project(pp, "max_performance_multiplier"));
      this.data.put("monthly_benefits", farm.bsg.data.types.TypeNumber.project(pp, "monthly_benefits"));
      this.data.put("tax_withholding", farm.bsg.data.types.TypeNumber.project(pp, "tax_withholding"));
      this.data.put("equipment_skills", farm.bsg.data.types.TypeStringTokenList.project(pp, "equipment_skills"));
      this.data.put("permissions_and_roles", farm.bsg.data.types.TypeStringTokenList.project(pp, "permissions_and_roles"));
    }

    public PutResult apply(Person person) {
      return person.validateAndApplyProjection(this.data);
    }
  }

  public PersonProjection_admin projection_person_admin_of(ProjectionProvider pp) {
    return new PersonProjection_admin(pp);
  }


  /**************************************************
  Projects (product/)
  **************************************************/

  public class ProductProjection_admin {
    private final HashMap<String, String> data;
    
    public ProductProjection_admin(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
      this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
      this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
      this.data.put("description", farm.bsg.data.types.TypeString.project(pp, "description"));
      this.data.put("category", farm.bsg.data.types.TypeString.project(pp, "category"));
      this.data.put("customizations", farm.bsg.data.types.TypeString.project(pp, "customizations"));
      this.data.put("price", farm.bsg.data.types.TypeNumber.project(pp, "price"));
    }

    public PutResult apply(Product product) {
      return product.validateAndApplyProjection(this.data);
    }
  }

  public ProductProjection_admin projection_product_admin_of(ProjectionProvider pp) {
    return new ProductProjection_admin(pp);
  }


  /**************************************************
  Projects (site/)
  **************************************************/

  public class SitePropertiesProjection_admin {
    private final HashMap<String, String> data;
    
    public SitePropertiesProjection_admin(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
      this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
      this.data.put("domain", farm.bsg.data.types.TypeString.project(pp, "domain"));
      this.data.put("product_name", farm.bsg.data.types.TypeString.project(pp, "product_name"));
      this.data.put("fb_page_token", farm.bsg.data.types.TypeString.project(pp, "fb_page_token"));
      this.data.put("description", farm.bsg.data.types.TypeString.project(pp, "description"));
      this.data.put("equipment", farm.bsg.data.types.TypeString.project(pp, "equipment"));
    }

    public PutResult apply(SiteProperties siteproperties) {
      return siteproperties.validateAndApplyProjection(this.data);
    }
  }

  public SitePropertiesProjection_admin projection_siteproperties_admin_of(ProjectionProvider pp) {
    return new SitePropertiesProjection_admin(pp);
  }


  /**************************************************
  Projects (subscriber/)
  **************************************************/

  public class SubscriberProjection_admin {
    private final HashMap<String, String> data;
    
    public SubscriberProjection_admin(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
      this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
      this.data.put("source", farm.bsg.data.types.TypeString.project(pp, "source"));
      this.data.put("from", farm.bsg.data.types.TypeString.project(pp, "from"));
      this.data.put("destination", farm.bsg.data.types.TypeString.project(pp, "destination"));
      this.data.put("subscription", farm.bsg.data.types.TypeString.project(pp, "subscription"));
      this.data.put("debug", farm.bsg.data.types.TypeString.project(pp, "debug"));
    }

    public PutResult apply(Subscriber subscriber) {
      return subscriber.validateAndApplyProjection(this.data);
    }
  }

  public SubscriberProjection_admin projection_subscriber_admin_of(ProjectionProvider pp) {
    return new SubscriberProjection_admin(pp);
  }


  /**************************************************
  Projects (subscription/)
  **************************************************/

  public class SubscriptionProjection_admin {
    private final HashMap<String, String> data;
    
    public SubscriptionProjection_admin(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
      this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
      this.data.put("name", farm.bsg.data.types.TypeString.project(pp, "name"));
      this.data.put("subscribe_keyword", farm.bsg.data.types.TypeString.project(pp, "subscribe_keyword"));
      this.data.put("subscribe_message", farm.bsg.data.types.TypeString.project(pp, "subscribe_message"));
      this.data.put("unsubscribe_keyword", farm.bsg.data.types.TypeString.project(pp, "unsubscribe_keyword"));
      this.data.put("unsubscribe_message", farm.bsg.data.types.TypeString.project(pp, "unsubscribe_message"));
    }

    public PutResult apply(Subscription subscription) {
      return subscription.validateAndApplyProjection(this.data);
    }
  }

  public SubscriptionProjection_admin projection_subscription_admin_of(ProjectionProvider pp) {
    return new SubscriptionProjection_admin(pp);
  }


  /**************************************************
  Projects (wake_input/)
  **************************************************/

  public class WakeInputFileProjection_admin {
    private final HashMap<String, String> data;
    
    public WakeInputFileProjection_admin(ProjectionProvider pp) {
      this.data = new HashMap<String, String>();
      this.data.put("id", farm.bsg.data.types.TypeUUID.project(pp, "id"));
      this.data.put("__token", farm.bsg.data.types.TypeString.project(pp, "__token"));
      this.data.put("filename", farm.bsg.data.types.TypeString.project(pp, "filename"));
      this.data.put("body", farm.bsg.data.types.TypeBytesInBase64.project(pp, "body"));
    }

    public PutResult apply(WakeInputFile wakeinputfile) {
      return wakeinputfile.validateAndApplyProjection(this.data);
    }
  }

  public WakeInputFileProjection_admin projection_wakeinputfile_admin_of(ProjectionProvider pp) {
    return new WakeInputFileProjection_admin(pp);
  }

}
