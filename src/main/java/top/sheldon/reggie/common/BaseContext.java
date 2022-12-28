package top.sheldon.reggie.common;

/**
 * 作用：在当前线程中存储sessionid
 * 原因：在MetaObjectHandler中做公共字段填充的时候，由于拿不到sessionid，所以需要在将sessionid保存在当前线程中，
 *      这样，在MetaObjectHandler中就可以从当前线程中取出sessionid字段
 */
public class BaseContext {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
