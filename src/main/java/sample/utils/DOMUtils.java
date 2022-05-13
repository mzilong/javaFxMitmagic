package sample.utils;

import org.dom4j.*;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/**
 * 基于dom4j的工具包
 * 
 * @author PengChan
 *
 */
public class DOMUtils {
 
	/**
	 * 通过文件的路径获取xml的document对象
	 * 
	 * @param path	文件的路径
	 * @return		返回文档对象
	 */
	public static Document getXMLByFilePath(String path) {
		if (null == path) {
			return null;
		}
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(new File(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}
	
	/**
	 * 通过xml字符获取document文档
	 * @param xmlstr	要序列化的xml字符
	 * @return			返回文档对象
	 * @throws DocumentException
	 */
	public static Document getXMLByString(String xmlstr) throws DocumentException{
		if(xmlstr==""||xmlstr==null){
			return null;
		}
		Document document = DocumentHelper.parseText(xmlstr);
		return document;
	}
 
	/**
	 * 获取某个元素的所有的子节点
	 * @param node	制定节点
	 * @return		返回所有的子节点
	 */
	public static List<Element> getChildElements(Element node) {
		if (null == node) {
			return null;
		}
		@SuppressWarnings("unchecked")
		List<Element> lists = node.elements();
		return lists;
	}
	
	/**
	 * 获取指定节点的子节点
	 * @param node			父节点
	 * @param childnode		指定名称的子节点
	 * @return				返回指定的子节点
	 */
	public static Element getChildElement(Element node,String childnode){
		if(null==node||null == childnode||"".equals(childnode)){
			return null;
		}
		return node.element(childnode);
	}
	
	/**
	 * 获取所有的属性值
	 * @param node
	 * @param arg
	 * @return
	 */
	public static Map<String, String> getAttributes(Element node,String...arg){
		if(node==null||arg.length==0){
			return null;
		}
		Map<String, String> attrMap = new HashMap<String,String>();
		for(String attr:arg){
			String attrValue = node.attributeValue(attr);
			attrMap.put(attr, attrValue);
		}
		return attrMap;
	}
	
	/**
	 * 获取element的单个属性
	 * @param node		需要获取属性的节点对象
	 * @param attr		需要获取的属性值
	 * @return			返回属性的值
	 */
	public static String getAttribute(Element node,String attr){
		if(null == node||attr==null||"".equals(attr)){
			return "";
		}
		return node.attributeValue(attr);
	}
 
	/**
	 * 添加孩子节点元素
	 * 
	 * @param parent
	 *            父节点
	 * @param childName
	 *            孩子节点名称
	 * @param childValue
	 *            孩子节点值
	 * @return 新增节点
	 */
	public static Element addChild(Element parent, String childName, String childValue) {
		Element child = parent.addElement(childName);// 添加节点元素
		child.setText(childValue == null ? "" : childValue); // 为元素设值
		return child;
	}
 
	/**
	 * DOM4j的Document对象转为XML报文串
	 * 
	 * @param document
	 * @param charset
	 * @return 经过解析后的xml字符串
	 */
	public static String documentToString(Document document, String charset) {
		StringWriter stringWriter = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();// 获得格式化输出流
		format.setEncoding(charset);// 设置字符集,默认为UTF-8
		XMLWriter xmlWriter = new XMLWriter(stringWriter, format);// 写文件流
		try {
			xmlWriter.write(document);
			xmlWriter.flush();
			xmlWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return stringWriter.toString();
	}
 
	/**
	 * 去掉声明头的(即<?xml...?>去掉)
	 * 
	 * @param document
	 * @param charset
	 * @return
	 */
	public static String documentToStringNoDeclaredHeader(Document document, String charset) {
		String xml = documentToString(document, charset);
		return xml.replaceFirst("\\s*<[^<>]+>\\s*", "");
	}
 
	/**
	 * 解析XML为Document对象
	 * 
	 * @param xml
	 *            被解析的XMl
	 * @return Document
	 */
	public final static Element parseXml(String xml) {
		StringReader sr = new StringReader(xml);
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document = saxReader.read(sr);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element rootElement = document != null ? document.getRootElement() : null;
		return rootElement;
	}
 
	/**
	 * 获取element对象的text的值
	 * 
	 * @param e
	 *            节点的对象
	 * @param tag
	 *            节点的tag
	 * @return
	 */
	public final static String getText(Element e, String tag) {
		Element _e = e.element(tag);
		if (_e != null) {
			return _e.getText();
		}

		return null;
	}
 
	/**
	 * 获取去除空格的字符串
	 * 
	 * @param e
	 * @param tag
	 * @return
	 */
	public final static String getTextTrim(Element e, String tag) {
		Element _e = e.element(tag);
		if (_e != null) {
			return _e.getTextTrim();
		}
		return null;
	}
 
	/**
	 * 获取节点值.节点必须不能为空，否则抛错
	 * 
	 * @param parent	父节点
	 * @param tag		想要获取的子节点
	 * @return			返回子节点
	 */
	public final static String getTextTrimNotNull(Element parent, String tag) {
		Element e = parent.element(tag);
		if (e == null) {
			throw new NullPointerException("节点为空");
		}

		return e.getTextTrim();
	}
 
	/**
	 * 节点必须不能为空，否则抛错
	 * 
	 * @param parent	父节点
	 * @param tag		想要获取的子节点
	 * @return			子节点
	 */
	public final static Element elementNotNull(Element parent, String tag) {
		Element e = parent.element(tag);
		if (e == null) {
			throw new NullPointerException("节点为空");
		}
		return e;

	}
	
	/**
	 * 将文档对象写入对应的文件中
	 * @param document		文档对象
	 * @param path			写入文档的路径
	 * @throws IOException	
	 */
	public final static void writeXMLToFile(Document document,String path) throws IOException{
		writeXMLToFile(document,new File(path));
	}
	public final static void writeXMLToFile(Document document,File path) throws IOException{
		if(document==null||path==null){
			return;
		}
		// 紧凑的格式
		// OutputFormat format = OutputFormat.createCompactFormat();
		// 排版缩进的格式
		OutputFormat format = OutputFormat.createPrettyPrint();
		// 设置编码
		format.setEncoding("UTF-8");
		// 创建XMLWriter对象,指定了写出文件及编码格式
		XMLWriter writer = new XMLWriter(
				new OutputStreamWriter(new FileOutputStream(path), "UTF-8"), format);
		// 写入
		writer.write(document);
		// 立即写入
		writer.flush();
		// 关闭操作
		writer.close();
	}

	public static Document createRootElement(String root){
		return DocumentHelper.createDocument(new DOMElement(root));
	}

	public static void main(String[] args) {
		//读取
		Document document = DOMUtils.getXMLByFilePath("C:\\Users\\mzl\\Desktop\\gameList.xml");
		System.out.println(document.asXML());
		Element root = document.getRootElement();
		System.out.println(root.getName());
		List<Element> elements = root.elements();
		for (Element list : elements) {
			System.out.println("  +-"+list.getName());
			List<Element> item = list.elements();
			for (Element e : item) {
				System.out.println("      +-"+e.getName()+":"+e.getText());
			}
		}

		//写入
		document = DocumentHelper.createDocument();
		root = document.getRootElement();
		root.setText("gameList");
		List<Node> rootContent= new ArrayList<>();
		for (int i = 0; i <2; i++) {
			Element game = DocumentHelper.createElement("game");
			DOMUtils.addChild(game,"name","游戏"+i);
			DOMUtils.addChild(game,"desc","描述"+i);
			DOMUtils.addChild(game,"path","路径"+i);
			DOMUtils.addChild(game,"image","");
			DOMUtils.addChild(game,"video","");
			DOMUtils.addChild(game,"marquee","");
			DOMUtils.addChild(game,"thumbnail","");
			DOMUtils.addChild(game,"releasedate","");
			DOMUtils.addChild(game,"developer","");
			DOMUtils.addChild(game,"publisher","");
			DOMUtils.addChild(game,"genre","");
			DOMUtils.addChild(game,"players","");
			DOMUtils.addChild(game,"lang","");
			DOMUtils.addChild(game,"region","");
			rootContent.add(game);
		}
		root.setContent(rootContent);

		try {
			DOMUtils.writeXMLToFile(document,"C:\\Users\\mzl\\Desktop\\gameList.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 
}