package sample.tools;

import sample.controller.base.BaseController;

import java.util.HashMap;
import java.util.Map;

/**
 * 控制器管理
 * @author mzl
 * @Description ControllersManagement
 * @Data  2020/9/1 13:22
 */
public class ControllersManagement {
    private static final Map<String, BaseController> controllers = new HashMap<String, BaseController>();

    /**
     * 获取某个控制器Controller
     * @param cClass 控制器类Controller.clacc
     * @param <T> 继承BaseController的控制器
     * @return 某个控制器Controller
     */
    public static <T extends BaseController> T getController(Class<T> cClass){
        return (T)controllers.get(cClass.getSimpleName());
    }

    /**
     * 添加控制器Controller
     * @param baseController 控制器
     */
    public static void putController(BaseController baseController){
        controllers.put(baseController.getClass().getSimpleName(),baseController);
    }

    /**
     * 移除控制器Controller
     * @param cClass 控制器类Controller.clacc
     * @param <T> 继承BaseController的控制器
     */
    public static <T extends BaseController> void removeController(Class<T> cClass) {
        controllers.remove(cClass.getSimpleName());
    }

    /**
     * 移除所有控制器Controller
     */
    public static void removeAllController() {
        controllers.clear();
    }
    /**
     * 控制器Controller数目
     */
    public static int size() {
        return controllers.size();
    }
}
