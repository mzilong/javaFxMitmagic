package sample.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用于View实现类之间的数据通信，通过调用 sendMessageWithAsync(String receiverName, Object... data)
 * 方法，将会在当前系统所有的 View 对象中，调用被此注解注释的方法
 * @author zhong
 * @date 2020-09-28 17:24:23 周一
 * @see sample.controller.base.BaseReceiverController#sendMessageWithAsync(String, Object...)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Receiver {

    /**
     * 接收者名字
     * @return 接收者名字
     */
    String name() default "";

    /**
     * 当此视图对应的 window 关闭时，是否接受消息
     * @return false 默认不接受，true 接受消息
     */
    boolean whenHidden() default false;

}
