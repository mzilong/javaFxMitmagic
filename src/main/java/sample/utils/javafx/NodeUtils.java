package sample.utils.javafx;

import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 实用工具类，提供的方法来简化节点处理。
 * 可能的使用情况正在寻找在特定位置的节点，添加/父母删除节点/（父接口不给孩子们写访问）
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 * @author Tom Eugelink &lt;tbee@tbee.org&gt;
 */
public class NodeUtils {

    // 不允许实例化
    private NodeUtils() {
        throw new AssertionError(); // not in this class either!
    }

   /**
    *
    * @param node 节点元素
    * @return 父级中节点的X坐标。
    */
   static public double xInParent(Node node, Node parent) {
	   double lX = 0;
	   
	   while (node != parent) {
		   double lXDelta = node.getBoundsInParent().getMinX();
		   lX += lXDelta;
		   //System.out.println("xInParent " + node + " -> " + lXDelta + " " + lX);
		   node = node.getParent();
	   }
       return lX;
   }

   /**
   *
   * @param node
   * @return 父级中节点的Y坐标。
   */
  static public double yInParent(Node node, Node parent) {
	   double lY = 0;
	   
	   while (node != parent) {
		   double lYDelta = node.getBoundsInParent().getMinY();
		   lY += lYDelta;
		   //System.out.println("yInParent " + node + " -> " + lYDelta + " " + lY);
		   node = node.getParent();
	   }
      return lY;
  }

    /**
     *
     * @param node
     * @return 节点的X屏幕坐标。
     */
    static public double screenX(Node node) {
        return sceneX(node) + node.getScene().getWindow().getX();
    }

    /**
     *
     * @param node
     * @return 节点的Y屏幕坐标。
     */
    static public double screenY(Node node) {
        return sceneY(node) + node.getScene().getWindow().getY();
    }

    /**
    *
    * @param node
    * @return 节点的X场景坐标。
    */
   static public double sceneX(Node node) {
       return node.localToScene(node.getBoundsInLocal()).getMinX() + node.getScene().getX();
   }

   /**
    *
    * @param node
    * @return 节点的Y场景坐标。
    */
   static public double sceneY(Node node) {
       return node.localToScene(node.getBoundsInLocal()).getMinY() + node.getScene().getY();
   }

    /**
     * 从其父级中删除指定的节点。
     *
     * @param n 要删除的节点
     *
     * @throws IllegalArgumentException if an unsupported parent class has been
     * specified or the parent is <code>null</code>
     */
    public static void removeFromParent(Node n) {
        if (n.getParent() instanceof Group) {
            ((Group) n.getParent()).getChildren().remove(n);
        } else if (n.getParent() instanceof Pane) {
            ((Pane) n.getParent()).getChildren().remove(n);
        } else {
            throw new IllegalArgumentException("Unsupported parent: " + n.getParent());
        }
    }

    /**
     * 将给定节点添加到指定的父级。
     *
     * @param p parent
     * @param n node
     *
     * @throws IllegalArgumentException if an unsupported parent class has been
     * specified or the parent is <code>null</code>
     */
    public static void addToParent(Parent p, Node n) {
        if (p instanceof Group) {
            ((Group) p).getChildren().add(n);
        } else if (p instanceof Pane) {
            ((Pane) p).getChildren().add(n);
        } else {
            throw new IllegalArgumentException("Unsupported parent: " + p);
        }
    }

    /**
     * 返回给定位置的第一个节点，该节点是指定类对象的实例。
     * 递归执行搜索，直到找到一个节点或到达一个叶节点为止。
     *
     * @param p parent node
     * @param sceneX x coordinate
     * @param sceneY y coordinate
     * @param nodeClass node class to search for
     * @return a node that contains the specified screen coordinates and is an
     * instance of the specified class or {@code null} if no such node
     * exist
     */
    public static Node getNode(Parent p, double sceneX, double sceneY, Class<?> nodeClass) {

        // dammit! javafx uses "wrong" children order.
        List<Node> rightOrder = new ArrayList<>();
        rightOrder.addAll(p.getChildrenUnmodifiable());
        Collections.reverse(rightOrder);

        for (Node n : rightOrder) {
            boolean contains = n.contains(n.sceneToLocal(sceneX, sceneY));

            if (contains) {

                if (nodeClass.isAssignableFrom(n.getClass())) {
                    return n;
                }

                if (n instanceof Parent) {
                    return getNode((Parent) n, sceneX, sceneY, nodeClass);
                }
            }
        }

        return null;
    }

    /**
     * 此方法可防止水平或垂直线条模糊，请使用snapXY（x）而不是x。
     * @param position (x or y)
     * @return
     */
	public static double snapXY(double position) {
		return ((int) position) + .5;
	}
	
	/**
	 * 这是在绑定中使用的snapXY方法，例如：
	 * p1.bind( snapXY( p2.multiply(0.1) ));
	 * 
     * @param position (x or y)
	 * @return
	 */
	public static DoubleBinding snapXY(final ObservableNumberValue position) {
		return new DoubleBinding() {
            {
                super.bind(position);
            }

            @Override
            public void dispose() {
                super.unbind(position);
            }

            @Override
            protected double computeValue() {
                return NodeUtils.snapXY(position.doubleValue());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(position);
            }
        };
    }


    /**
     * 此方法可防止水平或垂直线条模糊，请使用snapWH（x，w）代替w。
     * @param position (x or y)
     * @param offset (width or height)
     * @return
     */
	public static double snapWH(double position, double offset) {
		return snapXY(position + offset) - snapXY(position);
	}
	
	/**
	 * 这是在绑定中使用的snapXY方法，例如：
	 * p1.bind( snapXY( p2.multiply(0.1) ));
	 * 
     * @param position (x or y)
     * @param offset (width or height)
	 * @param dependencies
	 * @return
	 */
	public static DoubleBinding snapWH(final ObservableNumberValue position, final ObservableNumberValue offset, final Observable... dependencies) {
        return new DoubleBinding() {
            {
                super.bind(dependencies);
            }

            @Override
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override
            protected double computeValue() {
                return NodeUtils.snapWH(position.doubleValue(), offset.doubleValue());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return (dependencies.length == 1)? 
                        FXCollections.singletonObservableList(dependencies[0]) 
                        : FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(dependencies));
            }
        };
    }
	
	/**
	 * 此方法用于防止对样式类进行remove-add构造。
	 * This prevents CSS reprocessed unnecessary, because that is a time consuming process. 
	 * @param node
	 * @param styleclass
	 */
	static public void addStyleClass(Node node, String styleclass) {
		if (!node.getStyleClass().contains(styleclass)) {
			node.getStyleClass().add(styleclass);
		}
	}
	
	/**
	 * 删除操作已经“cheap”了，因此此方法仅用于镜像addStyleClass (这不是cheap)
	 * @param node
	 * @param styleclass
	 */
	static public void removeStyleClass(Node node, String styleclass) {
		node.getStyleClass().remove(styleclass);
	}
	
	/**
	 * 在与节点同名的包中派生CSS文件
	 * @param n
	 * @return
	 */
	static public String deriveCssFile(Node n) {
		Class<?> c = n.getClass();
		return c.getResource("/" + c.getPackage().getName().replaceAll("\\.", "/") + "/" + c.getSimpleName() + ".css").toExternalForm();
	}

	/**
	 *
     * 确定节点是否有效可见。
     * 这意味着，如果节点或其任何父节点不可见，则该节点实际上不可见。
     * 否则是可见的。
	 * 
	 * @param n
	 * @return
	 */
    static public boolean isEffectivelyVisible(Node n) {
    	while (n != null) {
    		if (!n.isVisible()) {
    			return false;
    		}
    		n = n.getParent();
    	}
    	return true;
    }


    /**
     * Find the X coordinate in ancestor's coordinate system that corresponds to the X=0 axis in
     * descendant's coordinate system.
     *
     * @param descendant a Node that is a descendant (direct or indirectly) of the ancestor
     * @param ancestor   a Node that is an ancestor of descendant
     */
    public static double getXShift( Node descendant, Node ancestor ) {
        double ret = 0.0;
        Node curr = descendant;
        while ( curr != ancestor ) {
            ret += curr.getLocalToParentTransform().getTx();
            curr = curr.getParent();
            if ( curr == null )
                throw new IllegalArgumentException( "'descendant' Node is not a descendant of 'ancestor" );
        }

        return ret;
    }

    /**
     * Find the Y coordinate in ancestor's coordinate system that corresponds to the Y=0 axis in
     * descendant's coordinate system.
     *
     * @param descendant a Node that is a descendant (direct or indirectly) of the ancestor
     * @param ancestor   a Node that is an ancestor of descendant
     */
    public static double getYShift( Node descendant, Node ancestor ) {
        double ret = 0.0;
        Node curr = descendant;
        while ( curr != ancestor ) {
            ret += curr.getLocalToParentTransform().getTy();
            curr = curr.getParent();
            if ( curr == null )
                throw new IllegalArgumentException( "'descendant' Node is not a descendant of 'ancestor" );
        }

        return ret;
    }

    /**
     * Make a best attempt to replace the original component with the replacement, and keep the same
     * position and layout constraints in the container.
     * <p>
     * Currently this method is probably not perfect. It uses three strategies:
     * <ol>
     *   <li>If the original has any properties, move all of them to the replacement</li>
     *   <li>If the parent of the original is a {@link BorderPane}, preserve the position</li>
     *   <li>Preserve the order of the children in the parent's list</li>
     * </ol>
     * <p>
     * This method does not transfer any handlers (mouse handlers for example).
     *
     * @param original    non-null Node whose parent is a {@link Pane}.
     * @param replacement non-null Replacement Node
     */
    public static void replaceComponent( Node original, Node replacement ) {
        Pane parent = (Pane) original.getParent();
        //transfer any properties (usually constraints)
        replacement.getProperties().putAll( original.getProperties() );
        original.getProperties().clear();

        ObservableList<Node> children = parent.getChildren();
        int originalIndex = children.indexOf( original );
        if ( parent instanceof BorderPane) {
            BorderPane borderPane = (BorderPane) parent;
            if ( borderPane.getTop() == original ) {
                children.remove( original );
                borderPane.setTop( replacement );

            } else if ( borderPane.getLeft() == original ) {
                children.remove( original );
                borderPane.setLeft( replacement );

            } else if ( borderPane.getCenter() == original ) {
                children.remove( original );
                borderPane.setCenter( replacement );

            } else if ( borderPane.getRight() == original ) {
                children.remove( original );
                borderPane.setRight( replacement );

            } else if ( borderPane.getBottom() == original ) {
                children.remove( original );
                borderPane.setBottom( replacement );
            }
        } else {
            //Hope that preserving the properties and position in the list is sufficient
            children.set( originalIndex, replacement );
        }
    }

    /**
     * Creates a "Scale Pane", which is a pane that scales as it resizes, instead of reflowing layout
     * like a normal pane. It can be used to create an effect like a presentation slide. There is no
     * attempt to preserve the aspect ratio.
     * <p>
     * If the region has an explicitly set preferred width and height, those are used unless
     * override is set true.
     * <p>
     * If the region already has a parent, the returned pane will replace it via the
     * {@link #replaceComponent(Node, Node)} method. The Region's parent must be a Pane in this case.
     *
     * @param region   non-null Region
     * @param w        default width, used if the region's width is calculated
     * @param h        default height, used if the region's height is calculated
     * @param override if true, w,h is the region's "100%" size even if the region has an explicit
     *                 preferred width and height set.
     *
     * @return the created StackPane, with preferred width and height set based on size determined by
     *         w, h, and override parameters.
     */
    public static StackPane createScalePane(Region region, double w, double h, boolean override ) {
        //如果包含GUI的区域尚未具有首选的宽度和高度，请进行设置。
        //但是，如果可以，我们可以将该设置用作“standard”分辨率。
        if ( override || region.getPrefWidth() == Region.USE_COMPUTED_SIZE )
            region.setPrefWidth( w );
        else
            w = region.getPrefWidth();

        if ( override || region.getPrefHeight() == Region.USE_COMPUTED_SIZE )
            region.setPrefHeight( h );
        else
            h = region.getPrefHeight();

        StackPane ret = new StackPane();
        ret.setPrefWidth( w );
        ret.setPrefHeight( h );
        if ( region.getParent() != null )
            replaceComponent( region, ret );

        //将可调整大小的内容包装在不可调整大小的容器（组）中
        Group group = new Group( region );
        //将组放在StackPane中，这将使其居中
        ret.getChildren().add( group );

        //将场景的宽度和高度绑定到组上的缩放参数
        group.scaleXProperty().bind( ret.widthProperty().divide( w ) );
        group.scaleYProperty().bind( ret.heightProperty().divide( h ) );

        return ret;
    }

}