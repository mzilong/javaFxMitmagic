package sample.controller.base;

import javafx.application.Platform;
import sample.controller.annotation.Receiver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Receiver,
 * For details, please refer to {@link Receiver}.
 *
 * Use example similar to Receiver：
 * <ul>
 * <li> First add a method with <font color="#1E90FF;"> {@code @Receiver}</font> in viewController:<br/>
 * <b>
 *     <font color="#1E90FF;"> {@code
 *     @Receiver(name = "ViewController",whenHidden = true)
 *     private void receiver(Object obj){}
 *     }</font>
 * </b>
 * <li>trigger listening event:<br/>
 * <b>
 *     <font color="#1E90FF;"> {@code sendMessageWithAsync("ViewController","Monitoring between different ViewControllers");}</font>
 * </b>
 * </ul>
 *
 * @author mzl
 * @date Create in  2019/6/21 0021 16:41
 * @description
 */
public class BaseReceiverController{

    public BaseReceiverController() {
        this.createReceiver();
    }

    /*----------------------------Receiver----------------------------------
     *
     * 所有 view 的父类。创建 view 对象需要用 {@link #createView(Class)} 方法创建，
     */

    private static  Map<String, List<ViewReceiverMethodInfo>> receiverMethods = new HashMap<>();
    /**
     * 异步发送消息给其他激活中的 ViewController ，其他 ViewController 类中被 @Receiver
     * 注解注释的方法且 name 为指定 receiverName 的方法，将被调用
     *
     * @param receiverName 接受者的名字
     * @param data         接收者方法参数
     */
    public void sendMessageWithAsync(String receiverName, Object... data) {
        CompletableFuture.runAsync(() -> {
            List<ViewReceiverMethodInfo> viewReceiverMethodInfos = receiverMethods.get(receiverName);
            for (ViewReceiverMethodInfo info : viewReceiverMethodInfos) {
                Method method = info.getMethod();
                BaseReceiverController view = info.getBaseController();
                if (!info.isWhenHidden()) {
                    continue;
                }
                method.setAccessible(true);
                Platform.runLater(() -> {
                    try {
                        method.invoke(view, data);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    /**
     * 检查的Receiver注解注册到新建BaseController中
     */
    public void createReceiver() {
        // 异步处理 Receiver 注解注释的方法
        CompletableFuture.runAsync(() -> {
            Method[] declaredMethods = this.getClass().getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                declaredMethod.setAccessible(true);
                Receiver annotation = declaredMethod.getAnnotation(Receiver.class);
                if (annotation != null) {
                    List<ViewReceiverMethodInfo> viewReceiverMethodInfos = receiverMethods.computeIfAbsent(annotation.name(), k -> new LinkedList<>());
                    ViewReceiverMethodInfo viewReceiverMethodInfo = new ViewReceiverMethodInfo();
                    viewReceiverMethodInfo.setBaseController(this);
                    viewReceiverMethodInfo.setMethod(declaredMethod);
                    viewReceiverMethodInfo.setReceiverName(annotation.name());
                    viewReceiverMethodInfo.setWhenHidden(annotation.whenHidden());
                    viewReceiverMethodInfos.add(viewReceiverMethodInfo);
                }
            }
        });
    }

    /**
     * 被 @Receiver 注解注释的方法信息，用于缓存被注释的方法
     */
    static class ViewReceiverMethodInfo {

        /**
         * Receiver 方法所属的视图对象
         */
        private BaseReceiverController baseController;
        /**
         * Receiver 方法
         */
        private Method method;
        /**
         * 对应 View 窗体关闭时，是否接受消息
         */
        private boolean whenHidden;
        /**
         * Receiver 名字
         */
        private String receiverName;

        public BaseReceiverController getBaseController() {
            return baseController;
        }

        public void setBaseController(BaseReceiverController baseController) {
            this.baseController = baseController;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public boolean isWhenHidden() {
            return whenHidden;
        }

        public void setWhenHidden(boolean whenHidden) {
            this.whenHidden = whenHidden;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }
    }
}
