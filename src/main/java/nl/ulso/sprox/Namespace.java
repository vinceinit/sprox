package nl.ulso.sprox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a namespace. Put a single annotation on a controller class to declare all XML elements used in the
 * controller to belong to that namespace.
 * <p>
 * If a controller handles elements from multiple namespaces, use {@link Namespaces} to list them all. If a single
 * controller handles XML elements from multiple namespaces, use this annotation repeatedly to declare them all, one at
 * a time, giving each namespace a different shorthand.
 * </p>
 * <p>
 * The first namespace in the list of namespaces is the default namespace. You can refer to elements in this
 * namespace without using its shorthand. Therefore it doesn't require one, unless of course you need it in your
 * code.
 * </p>
 * <p>
 * A namespace has a value - the namespace URI - and a shorthand. This is the name you use in {@link Node},
 * {@link Attribute} and {@link Source} annotations to refer to the namespace by prefixing the name of the element
 * with {@code &lt;shorthand&gt;:}. If a controller uses a single namespace the shorthand can be left empty; there's no
 * need to refer to it anywhere in the class.
 * </p>
 * <p>
 * Note that the namespace shorthand has absolutely nothing to do with namespace prefixes. Prefixes are used within
 * XML documents. Shorthands are used in controller methods. These are different concepts. To drive this point home
 * they are named differently on purpose.
 * </p>
 *
 * @see Namespaces
 * @see Node
 * @see Attribute
 * @see Source
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Namespaces.class)
public @interface Namespace {

    /**
     * @return URI of the namespace.
     */
    String value();

    /**
     * @return Shorthand to use when referring to the namespace in {@link Node}, {@link Attribute} or {@link Source}
     *         annotations. When using a single namespace, this is best left empty.
     */
    String shorthand() default "";
}
