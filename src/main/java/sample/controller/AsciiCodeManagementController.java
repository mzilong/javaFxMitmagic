package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.table.TableRowExpanderColumn;
import sample.controller.annotation.FxmlPath;
import sample.controller.base.BaseController;
import sample.locale.ControlResources;
import sample.model.AsciiItem;
import sample.model.AsciiProperty;
import sample.tools.helper.TableViewHelper;
import sample.utils.ClipboardUtils;
import sample.utils.DataUtils;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author: mzl
 * Description:ChildController
 * Data: 2020/8/18 16:29
 *
 * ASCII（American Standard Code for Information Interchange，
 * 美国信息互换标准代码，ASCⅡ）是基于拉丁字母的一套电脑编码系统。
 * 它主要用于显示现代英语和其他西欧语言。它是现今最通用的单字节编码系统，并等同于国际标准ISO/IEC 646。
 *
 * ASCII第一次以规范标准的型态发表是在1967年，最后一次更新则是在1986年，
 * 至今为止共定义了128个字符，其中33个字符无法显示（这是以现今操作系统为依归，
 * 但在DOS模式下可显示出一些诸如笑脸、扑克牌花式等8-bit符号），
 * 且这33个字符多数都已是陈废的控制字符，控制字符的用途主要是用来操控已经处理过的文字，
 * 在33个字符之外的是95个可显示的字符，包含用键盘敲下空白键所产生的空白字符也算1个可显示字符（显示为空白）。
 *
 * EASCII（Extended ASCII，延伸美国标准信息交换码,，EASCⅡ）是将ASCII码由7位扩充为8位而成。
 * EASCII的内码是由0到255共有256个字符组成。EASCII码比ASCII码扩充出来的符号包括表格符号、计算符号、希腊字母和特殊的拉丁符号。
 *
 *
 * ISO-8859-1
 * ISO-8859-1 是 HTML 4.01 中的默认字符。
 *
 * Windows-1252 是 Microsoft Windows 中的第一个默认字符集。
 * 从 1985 年到 1990 年，它是 Windows 中最流行的字符集。
 * 尽管 Windows-1252 与 ISO-8859-1 几乎相同，但它从未成为 ANSI 或 ISO 标准。
 * ISO-8859-1 与 Windows-1252 非常相似。
 * 在 ISO-8859-1 中，未定义 128 到 159 之间的字符。
 * 在 Windows-1252 中，从 128到 159 的字符用于某些有用的符号。
 *
 * 控制字符（水平制表符、回车符和换行符除外）与 HTML 文档无关。
 *
 * HTML三种表示方式
 * 1、实体 &nbsp;
 * 2、十进制 &#0000;
 * 3、十六进制 &#xFFFF;
 *
 */

@FxmlPath("fxml/ascii_code_management.fxml")
public class AsciiCodeManagementController extends BaseController {

    public VBox vbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //这里一定要有判断，不然会有空指针异常
    }

    @Override
    public String initTitle() {
        return ControlResources.getString("Title.AsciiCodeManagement");
    }

    @Override
    public String initIcon() {
        return "ic_launcher.png";
    }


    private void onCloseRequest(WindowEvent event) {
    }

    @Override
    public void onShowing(WindowEvent event) {
        super.onShowing(event);
        //这里一定要有判断，不然会有空指针异常
        if (getIntent() != null) {
            String data = getIntent().getData("data");
            logger.info(data);
        }

        Stage primaryStage = getIntent().getPrimaryStage();
        //监听最大化
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {});

        primaryStage.setOnCloseRequest(windowEvent -> onCloseRequest(windowEvent));

        addNode();
    }

    private void addNode(){
        vbox.getChildren().add(initTableView());
        vbox.setPrefWidth(585);
    }
    public Node initTableView() {
        TableView<AsciiItem> tableView = new TableView<>();
//        TableRowExpanderColumn<AsciiItem> expanderColumn = new TableRowExpanderColumn<>(this::createEditor);
//        tableView.getColumns().addAll(expanderColumn);
        TableViewHelper<AsciiItem> tableViewHelper= TableViewHelper.of(tableView);
        tableViewHelper.addStrColumn("二进制", AsciiItem::getBinary);
        tableViewHelper.addStrColumn("十进制", asciiItem -> ""+asciiItem.getDecimal()+"");
        tableViewHelper.addStrColumn("十六进制", asciiItem -> ""+asciiItem.getHex()+"");
        tableViewHelper.addStrColumn("Html标识", AsciiItem::getHtml);
        tableViewHelper.addStrColumn("缩写/字符", AsciiItem::getCharGraphic);
        tableViewHelper.addStrColumn("解析", AsciiItem::getParsing);
        tableViewHelper.setOnItemDoubleClick(asciiItem -> ClipboardUtils.setClipboardContent(DataFormat.PLAIN_TEXT,asciiItem.getHex()));
        tableView.getItems().addAll(getAsciiList());
        return tableView;
    }
    private GridPane createEditor(TableRowExpanderColumn.TableRowDataFeatures<AsciiItem> param) {
        GridPane editor = new GridPane();
        editor.setPadding(new Insets(10));
        editor.setHgap(10);
        editor.setVgap(5);

        AsciiItem asciiProperty = param.getValue();

        TextField htmlField = new TextField(asciiProperty.getHtml());
        TextField charGraphicField = new TextField(asciiProperty.getCharGraphic());

        editor.addRow(0, new Label("Html"), htmlField);
        editor.addRow(1, new Label("CharGraphic"), charGraphicField);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            asciiProperty.setHtml(htmlField.getText());
            asciiProperty.setCharGraphic(charGraphicField.getText());
            param.toggleExpanded();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> param.toggleExpanded());

        editor.addRow(2, saveButton, cancelButton);

        return editor;
    }
    private ObservableList<AsciiProperty> getCustomers() {
        List<AsciiProperty> list = new LinkedList<>();
        for (int i = 0; i < 256; i++) {
            AsciiProperty asciiItem=new AsciiProperty(i,DataUtils.integerToBinary(i),DataUtils.integerToHex(i),htmlStr[i],charStr[i],parsingStr[i]);
            if(i>=0&&i<=31||i==127){
                //ASCII控制字符
                asciiItem.setCharGraphic(charStr[i]);
            }else if(i>=32&&i<=126){
                //ASCII可显示字符
            }else if(i>=128&&i<=159){
                //EASCII:Windows-1252可显示字符
            }else if(i>=160&&i<=255){
                //EASCII:ISO-8859-1或者Windows-1252 可显示字符
            }
            list.add(asciiItem);
        }
        return FXCollections.observableArrayList(list);
    }
    String[] parsingStr = {
            /*--------0-31-------*/
            "空字符 (null)",
            "标题开始 (start of headline)",
            "正文开始 (start of text)",
            "正文结束 (end of text)",
            "传输结束 (end of transmission)",
            "请求 (enquiry)",
            "收到通知 (acknowledge)",
            "响铃 (bell)",
            "退格 (backspace)",
            "水平制表符 (horizontal tab)",
            "换行符 (NL line feed, new line)",
            "垂直制表符 (vertical tab)",
            "换页键 (NP form feed, new page)",
            "回车符 (carriage return)",
            "不用切换 (shift out)",
            "启用切换 (shift in)",
            "数据链路转义 (data link escape)",
            "设备控制1 (device control 1)",
            "设备控制2 (device control 2)",
            "设备控制3 (device control 3)",
            "设备控制4 (device control 4)",
            "拒绝接收 (negative acknowledge)",
            "同步空闲 (synchronous idle)",
            "传输块结束 (end of trans. block)",
            "取消 (cancel)",
            "介质中断 (end of medium)",
            "替换 (substitute)",
            "换码(溢出) (escape)",
            "文件分割符 (file separator)",
            "分组符 (group separator)",
            "记录分离符 (record separator)",
            "单元分隔符 (unit separator)",
            /*--------32-126-------*/
            "空格 (no-break space)",
            "感叹号 (exclamation mark)",
            "双引号 (quotation mark)",
            "数字符号 (number sign)",
            "美元符 (dollar sign)",
            "百分号 (percent sign)",
            "与号 (ampersand)",
            "省略号/单引号 (apostrophe)",
            "左圆括号 (left parenthesis)",
            "右圆括号 (right parenthesis)",
            "星号 (asterisk)",
            "加号 (plus sign)",
            "逗号 (comma)",
            "连字号或减号 (hyphen-minus)",
            "句点或小数点 (full stop)",
            "斜杠 (solidus)",
            "数字0 (digit zero)",
            "数字1 (digit one)",
            "数字2 (digit two)",
            "数字3 (digit three)",
            "数字4 (digit four)",
            "数字5 (digit five)",
            "数字6 (digit six)",
            "数字7 (digit seven)",
            "数字8 (digit eight)",
            "数字9 (digit nine)",
            "冒号 (colon)",
            "分号 (semicolon)",
            "小于 (less-than sign)",
            "等于 (equals sign)",
            "大于 (greater-than sign)",
            "问号 (question mark)",
            "商业at符号 (commercial at)",
            "大写字母A (Latin capital letter A)",
            "大写字母B (Latin capital letter B)",
            "大写字母C (Latin capital letter C)",
            "大写字母D (Latin capital letter D)",
            "大写字母E (Latin capital letter E)",
            "大写字母F (Latin capital letter F)",
            "大写字母G (Latin capital letter G)",
            "大写字母H (Latin capital letter H)",
            "大写字母I (Latin capital letter I)",
            "大写字母J (Latin capital letter J)",
            "大写字母K (Latin capital letter K)",
            "大写字母L (Latin capital letter L)",
            "大写字母N (Latin capital letter M)",
            "大写字母M (Latin capital letter N)",
            "大写字母O (Latin capital letter O)",
            "大写字母P (Latin capital letter P)",
            "大写字母Q (Latin capital letter Q)",
            "大写字母R (Latin capital letter R)",
            "大写字母S (Latin capital letter S)",
            "大写字母T (Latin capital letter T)",
            "大写字母U (Latin capital letter U)",
            "大写字母V (Latin capital letter V)",
            "大写字母W (Latin capital letter W)",
            "大写字母X (Latin capital letter X)",
            "大写字母Y (Latin capital letter Y)",
            "大写字母Z (Latin capital letter Z)",
            "左中括号 (left square bracket)",
            "反斜杠 (reverse solidus)",
            "右中括号 (right square bracket)",
            "音调符号 (circumflex accent)",
            "下划线 (low line)",
            "重音符 (grave accent)",
            "小写字母a (Latin small letter a)",
            "小写字母b (Latin small letter b)",
            "小写字母c (Latin small letter c)",
            "小写字母d (Latin small letter d)",
            "小写字母e (Latin small letter e)",
            "小写字母f (Latin small letter f)",
            "小写字母g (Latin small letter g)",
            "小写字母h (Latin small letter h)",
            "小写字母i (Latin small letter i)",
            "小写字母j (Latin small letter j)",
            "小写字母k (Latin small letter k)",
            "小写字母l (Latin small letter l)",
            "小写字母n (Latin small letter m)",
            "小写字母m (Latin small letter n)",
            "小写字母o (Latin small letter o)",
            "小写字母p (Latin small letter p)",
            "小写字母q (Latin small letter q)",
            "小写字母r (Latin small letter r)",
            "小写字母s (Latin small letter s)",
            "小写字母t (Latin small letter t)",
            "小写字母u (Latin small letter u)",
            "小写字母v (Latin small letter v)",
            "小写字母w (Latin small letter w)",
            "小写字母x (Latin small letter x)",
            "小写字母y (Latin small letter y)",
            "小写字母z (Latin small letter z)",
            "左大括号 (left curly bracket)",
            "垂直线 (vertical line)",
            "右大括号 (right curly bracket)",
            "代字号 (tilde)",
            /*--------127-------*/
            "删除 (delete)",
            /*--------128-159-------*/
            " (euro sign)",
            "保留 (not used)",
            " (single low-9 quotation mark)",
            " (Latin small letter f with hook)",
            " (double low-9 quotation mark)",
            " (horizontal ellipsis)",
            " (dagger)",
            " (double dagger)",
            " (modifier letter circumflex accent)",
            " (per mille sign)",
            " (Latin capital letter S with caron)",
            " (single left-pointing angle quotation mark)",
            " (Latin capital ligature OE)",
            "保留 (not used)",
            " (Latin capital letter Z with caron)",
            "保留 (not used)",
            "保留 (not used)",
            " (left single quotation mark)",
            " (right single quotation mark)",
            " (left double quotation mark)",
            " (right double quotation mark)",
            " (bullet)",
            " (en dash)",
            " (em dash)",
            " (small tilde)",
            " (trade mark sign)",
            " (Latin small letter s with caron)",
            " (single right-pointing angle quotation mark)",
            " (Latin small ligature oe)",
            "保留 (not used)",
            " (Latin small letter z with caron)",
            " (Latin capital letter Y with diaeresis)",
            /*--------160-255-------*/
            "空格 (no-break space)",
            "倒感叹号(inverted exclamation mark)",
            "英分 (cent sign)",
            "英镑 (pound sign)",
            "货币记号(currency sign)",
            "日元 (yen sign)",
            "断条/断直条 (broken bar)",
            "小节符 (section sign)",
            "分音符/元音变音 (diaeresis)",
            "版权符 (copyright sign)",
            "阴性序数记号 (feminine ordinal indicator)",
            "左指双尖引号 (left-pointing double angle quotation mark)",
            "非标记 (not sign)",
            "减号连字符 (minus hyphen)",
            "注册商标 (registered sign)",
            "上划线 (macron)",
            "度 (degree sign) ",
            "正负号 (plus-minus sign)",
            "上标数字二/平方 (superscript two)",
            "上标数字三/立方 (superscript three)",
            "重音符 (acute accent)",
            "微符 (micro sign)",
            "段落标记 (pilcrow sign)",
            "中心点 (middle dot)",
            "软音符 (cedilla)",
            "上标数字一 (superscript one)",
            "阳性序数记号 (masculine ordinal indicator)",
            "右指双尖引号 (right-pointing double angle quotation mark)",
            "分数四分之一 (vulgar fraction one quarter)",
            "分数二分之一 (vulgar fraction one half)",
            "分数四分之三 (vulgar fraction three quarters)",
            "竖翻问号 (inverted question mark)",
            "重音符A (Latin capital letter A with grave)",
            "锐音符A (Latin capital letter A with acute)",
            "扬抑符A (Latin capital letter A with circumflex)",
            "颚化符A (Latin capital letter A with tilde)",
            "分音符A (Latin capital letter A with diaeresis)",
            "带上圆圈的A (Latin capital letter A with ring above)",
            "连字AE (Latin capital letter AE)",
            "下加符C (Latin capital letter C with cedilla)",
            "重音符E (Latin capital letter E with grave)",
            "锐音符E (Latin capital letter E with acute)",
            "扬抑符E (Latin capital letter E with circumflex)",
            "分音符E (Latin capital letter E with diaeresis )",
            "重音符I (Latin capital letter I with grave)",
            "锐音符I (Latin capital letter I with acute)",
            "扬抑符I (Latin capital letter I with circumflex)",
            "分音符I (Latin capital letter I with diaeresis )",
            "ETH (Latin capital letter Eth)",
            "颚化符N (Latin capital letter N with tilde)",
            "重音符O (Latin capital letter O with grave)",
            "锐音符O (Latin capital letter O with acute)",
            "扬抑符O (Latin capital letter O with circumflex)",
            "颚化符O (Latin capital letter O with tilde)",
            "分音符O (Latin capital letter O with diaeresis)",
            "乘号 (multiplication sign)",
            "带斜线的O (Latin capital letter O with stroke)",
            "重音符U (Latin capital letter U with grave)",
            "锐音符U (Latin capital letter U with acute)",
            "扬抑符U (Latin capital letter U with circumflex)",
            "分音符U (Latin capital letter U with diaeresis)",
            "锐音符Y (Latin capital letter Y with acute)",
            "THORN (Latin capital letter Thorn)",
            "清音s (Latin small letter sharp s)",
            "重音符a (Latin small letter a with grave)",
            "锐音符a (Latin small letter a with acute)",
            "扬抑符a (Latin small letter a with circumflex)",
            "颚化符a (Latin small letter a with tilde)",
            "分音符a (Latin small letter a with diaeresis)",
            "上面带环a (Latin small letter a with ring above)",
            "连字ae (Latin small letter ae)",
            "下加符c (Latin small letter c with cedilla)",
            "重音符e (Latin small letter e with grave)",
            "锐音符e (Latin small letter e with acute)",
            "扬抑符e (Latin small letter e with circumflex)",
            "分音符e (Latin small letter e with diaeresis)",
            "重音符i (Latin small letter i with grave)",
            "锐音符i (Latin small letter i with acute)",
            "扬抑符i (Latin small letter i with circumflex)",
            "分音符i (Latin small letter i with diaeresis)",
            "eth (Latin small letter eth)",
            "颚化符n (Latin small letter n with tilde)",
            "重音符o (Latin small letter o with grave)",
            "锐音符o (Latin small letter o with acute)",
            "扬抑符o (Latin small letter o with circumflex)",
            "颚化符o (Latin small letter o with tilde)",
            "分音符o (Latin small letter o with diaeresis)",
            "除号 (division sign)",
            "带斜线的o (Latin small letter o with stroke)",
            "重音符u (Latin small letter u with grave)",
            "锐音符u (Latin small letter u with acute)",
            "扬抑符u (Latin small letter u with circumflex)",
            "分音符u (Latin small letter u with diaeresis)",
            "锐音符y (Latin small letter y with acute)",
            "thorn (Latin small letter thorn)",
            "分音符y (Latin small letter y with diaeresis)",
    };
    String[] charStr = {
                                /*--------0-31-------*/
            "NUL",      //00
            "SOH",      //01
            "STX",      //02
            "ETX",      //03
            "EOT",      //04
            "ENQ",      //05
            "ACK",      //06
            "BEL",      //07
            "BS",       //08
            "HT",       //09
            "LF",       //10
            "VT",       //11
            "FF",       //12
            "CR",       //13
            "SO",       //14
            "SI",       //15
            "DLE",      //16
            "DC1",      //17
            "DC2",      //18
            "DC3",      //19
            "DC4",      //20
            "NAK",      //21
            "SYN",      //22
            "ETB",      //23
            "CAN",      //24
            "EM",       //25
            "SUB",      //26
            "ESC",      //27
            "FS",       //28
            "GS",       //29
            "RS",       //30
            "US",       //31
                                /*--------32-126-------*/
            " ",     //32
            "!",        //33
            "\"",       //34
            "#",        //35
            "$",        //36
            "%",        //37
            "&",        //38
            "'",        //39
            "(",        //40
            ")",        //41
            "*",        //42
            "+",        //43
            ",",        //44
            "-",        //45
            ".",        //46
            "/",        //47
            "0",        //48
            "1",        //49
            "2",        //50
            "3",        //51
            "4",        //52
            "5",        //53
            "6",        //54
            "7",        //55
            "8",        //56
            "9",        //57
            ":",        //58
            ";",        //59
            "<",        //60
            "=",        //61
            ">",        //62
            "?",        //63
            "@",        //64
            "A",        //65
            "B",        //66
            "C",        //67
            "D",        //68
            "E",        //69
            "F",        //70
            "G",        //71
            "H",        //72
            "I",        //73
            "J",        //74
            "K",        //75
            "L",        //76
            "M",        //77
            "N",        //78
            "O",        //79
            "P",        //80
            "Q",        //81
            "R",        //82
            "S",        //83
            "T",        //84
            "U",        //85
            "V",        //86
            "W",        //87
            "X",        //88
            "Y",        //89
            "Z",        //90
            "[",        //91
            "\\",       //92
            "]",        //93
            "^",        //94
            "_",        //95
            "`",        //96
            "a",        //97
            "b",        //98
            "c",        //99
            "d",        //10
            "e",        //10
            "f",        //10
            "g",        //10
            "h",        //10
            "i",        //10
            "j",        //10
            "k",        //10
            "l",        //10
            "m",        //10
            "n",        //11
            "o",        //11
            "p",        //11
            "q",        //11
            "r",        //11
            "s",        //11
            "t",        //11
            "u",        //11
            "v",        //11
            "w",        //11
            "x",        //12
            "y",        //12
            "z",        //12
            "{",        //12
            "|",        //12
            "}",        //12
            "~",        //12
                                /*--------127--------*/
            "DEL",      //127
                                /*--------128-159-------*/
            "€",        //128
            "�",        //129
            "‚",        //130
            "ƒ",        //131
            "„",        //132
            "…",        //133
            "†",        //134
            "‡",        //135
            "ˆ",        //136
            "‰",        //137
            "Š",        //138
            "‹",        //139
            "Œ",        //140
            "�",        //141
            "Ž",        //142
            "�",        //143
            "�",        //144
            "‘",        //145
            "’",        //146
            "“",        //147
            "”",        //148
            "•",        //149
            "–",        //150
            "—",        //151
            "˜",        //152
            "™",        //153
            "š",        //154
            "›",        //155
            "œ",        //156
            "�",        //157
            "ž",        //158
            "Ÿ",        //159
                                /*--------160-255-------*/
            " ",     //160
            "¡",	    //161
            "¢",	    //162
            "£",	    //163
            "¤",	    //164
            "¥",	    //165
            "¦",	    //166
            "§",	    //167
            "¨",	    //168
            "©",	    //169
            "ª",	    //170
            "«",	    //171
            "¬",	    //172
            "-",	    //173
            "®",	    //174
            "¯",	    //175
            "°",	    //176
            "±",	    //177
            "²",	    //178
            "³",	    //179
            "´",	    //180
            "µ",	    //181
            "¶",	    //182
            "·",	    //183
            "¸",	    //184
            "¹",	    //185
            "º",	    //186
            "»",	    //187
            "¼",	    //188
            "½",	    //189
            "¾",	    //190
            "¿",	    //191
            "À",	    //192
            "Á",	    //193
            "Â",	    //194
            "Ã",	    //195
            "Ä",	    //196
            "Å",	    //197
            "Æ",	    //198
            "Ç",	    //199
            "È",	    //200
            "É",	    //201
            "Ê",	    //202
            "Ë",	    //203
            "Ì",	    //204
            "Í",	    //205
            "Î",	    //206
            "Ï",	    //207
            "Ð",	    //208
            "Ñ",	    //209
            "Ò",	    //210
            "Ó",	    //211
            "Ô",	    //212
            "Õ",	    //213
            "Ö",	    //214
            "×",	    //215
            "Ø",	    //216
            "Ù",	    //217
            "Ú",	    //218
            "Û",	    //219
            "Ü",	    //220
            "Ý",	    //221
            "Þ",	    //222
            "ß",	    //223
            "à",	    //224
            "á",	    //225
            "â",	    //226
            "ã",	    //227
            "ä",	    //228
            "å",	    //229
            "æ",	    //230
            "ç",	    //231
            "è",	    //232
            "é",	    //233
            "ê",	    //234
            "ë",	    //235
            "ì",	    //236
            "í",	    //237
            "î",	    //238
            "ï",	    //239
            "ð",	    //240
            "ñ",	    //241
            "ò",	    //242
            "ó",	    //243
            "ô",	    //244
            "õ",	    //245
            "ö",	    //246
            "÷",	    //247
            "ø",	    //248
            "ù",	    //249
            "ú",	    //250
            "û",	    //251
            "ü",	    //252
            "ý",	    //253
            "þ",	    //254
            "ÿ",	    //255
    };
    String[] htmlStr = {
                                /*--------0-31-------*/
            "NUL",      //00
            "SOH",      //01
            "STX",      //02
            "ETX",      //03
            "EOT",      //04
            "ENQ",      //05
            "ACK",      //06
            "BEL",      //07
            "BS",       //08
            "HT",       //09
            "LF",       //10
            "VT",       //11
            "FF",       //12
            "CR",       //13
            "SO",       //14
            "SI",       //15
            "DLE",      //16
            "DC1",      //17
            "DC2",      //18
            "DC3",      //19
            "DC4",      //20
            "NAK",      //21
            "SYN",      //22
            "ETB",      //23
            "CAN",      //24
            "EM",       //25
            "SUB",      //26
            "ESC",      //27
            "FS",       //28
            "GS",       //29
            "RS",       //30
            "US",       //31
                                /*--------32-126-------*/
            "",         //32
            "",         //33
            "&quot;",   //34
            "",         //35
            "",         //36
            "",         //37
            "&amp;",    //38
            "",         //39
            "",         //40
            "",         //41
            "",         //42
            "",         //43
            "",         //44
            "",         //45
            "",         //46
            "",         //47
            "",         //48
            "",         //49
            "",         //50
            "",         //51
            "",         //52
            "",         //53
            "",         //54
            "",         //55
            "",         //56
            "",         //57
            "",         //58
            "",         //59
            "&lt;",     //60
            "",         //61
            "&gt;",     //62
            "",         //63
            "",         //64
            "",         //65
            "",         //66
            "",         //67
            "",         //68
            "",         //69
            "",         //70
            "",         //71
            "",         //72
            "",         //73
            "",         //74
            "",         //75
            "",         //76
            "",         //77
            "",         //78
            "",         //79
            "",         //80
            "",         //81
            "",         //82
            "",         //83
            "",         //84
            "",         //85
            "",         //86
            "",         //87
            "",         //88
            "",         //89
            "",         //90
            "",         //91
            "",         //92
            "",         //93
            "",         //94
            "",         //95
            "",         //96
            "",         //97
            "",         //98
            "",         //99
            "",         //100
            "",         //101
            "",         //102
            "",         //103
            "",         //104
            "",         //105
            "",         //106
            "",         //107
            "",         //108
            "",         //109
            "",         //110
            "",         //111
            "",         //112
            "",         //113
            "",         //114
            "",         //115
            "",         //116
            "",         //117
            "",         //118
            "",         //119
            "",         //120
            "",         //121
            "",         //122
            "",         //123
            "",         //124
            "",         //125
            "",         //126
                                /*--------127--------*/
            "",         //127
                                /*--------128-159-------*/
            "&euro;	",  //128
            "",         //129
            "&sbquo;",  //130
            "&fnof;	",  //131
            "&bdquo;",  //132
            "&hellip;", //133
            "&dagger;", //134
            "&Dagger;", //135
            "&circ;",   //136
            "&permil;", //137
            "&Scaron;", //138
            "&lsaquo;", //139
            "&OElig;",  //140
            "",         //141
            "&Zcaron;", //142
            "",         //143
            "",         //144
            "&lsquo;",  //145
            "&rsquo;",  //146
            "&ldquo;",  //147
            "&rdquo;",  //148
            "&bull;	",  //149
            "&ndash;",  //150
            "&mdash;",  //151
            "&tilde;",  //152
            "&trade;",  //153
            "&scaron;", //154
            "&rsaquo;", //155
            "&oelig;",  //156
            "",         //157
            "&zcaron;", //158
            "&Yuml;",   //159
                                /*--------160-255-------*/
            "&nbsp;",
            "&iexcl;",  //161
            "&cent;",   //162
            "&pound;",  //163
            "&curren;", //164
            "&yen;",    //165
            "&brvbar;", //166
            "&sect;",   //167
            "&uml;",    //168
            "&copy;",   //169
            "&ordf;",   //170
            "&laquo;",  //171
            "&not;",    //172
            "&minus;",  //173
            "&reg;",    //174
            "&macr;",   //175
            "&deg;",    //176
            "&plusmn;", //177
            "&sup2;",   //178
            "&sup3;",   //179
            "&acute;",  //180
            "&micro;",  //181
            "&para;",   //182
            "&middot;", //183
            "&cedil;",  //184
            "&sup1;",   //185
            "&ordm;",   //186
            "&raquo;",  //187
            "&frac14;", //188
            "&frac12;", //189
            "&frac34;", //190
            "&iquest;", //191
            "&Agrave;", //192
            "&Aacute;", //193
            "&Acirc;",  //194
            "&Atilde;", //195
            "&Auml;",   //196
            "&Aring;",  //197
            "&AElig;",  //198
            "&Ccedil;", //199
            "&Egrave;", //200
            "&Eacute;", //201
            "&Ecirc;",  //202
            "&Euml;",   //203
            "&Igrave;", //204
            "&Iacute;", //205
            "&Icirc;",  //206
            "&Iuml;",   //207
            "&ETH;",    //208
            "&Ntilde;", //209
            "&Ograve;", //210
            "&Oacute;", //211
            "&Ocirc;",  //212
            "&Otilde;", //213
            "&Ouml;",   //214
            "&times;",  //215
            "&Oslash;", //216
            "&Ugrave;", //217
            "&Uacute;", //218
            "&Ucirc;",  //219
            "&Uuml;",   //220
            "&Yacute;", //221
            "&THORN;;", //222
            "&szlig;",  //223
            "&agrave;", //224
            "&aacute;", //225
            "&acirc;",  //226
            "&atilde;", //227
            "&auml;",   //228
            "&aring;",  //229
            "&aelig;",  //230
            "&ccedil;", //231
            "&egrave;", //232
            "&eacute;", //233
            "&ecirc;",  //234
            "&euml;",   //235
            "&igrave;", //236
            "&iacute;", //237
            "&icirc;",  //238
            "&iuml;",   //239
            "&eth;",    //240
            "&ntilde;", //241
            "&ograve;", //242
            "&oacute;", //243
            "&ocirc;",  //244
            "&otilde;", //245
            "&ouml;",   //246
            "&divide;", //247
            "&oslash;", //248
            "&ugrave;", //249
            "&uacute;", //250
            "&ucirc;",  //251
            "&uuml;",   //252
            "&yacute;", //253
            "&thorn;",  //254
            "&yuml;",   //255
    };
    private List<AsciiItem> getAsciiList(){
        List<AsciiItem> list = new LinkedList<>();
        for (int i = 0; i < 256; i++) {
            AsciiItem asciiItem=new AsciiItem(i,DataUtils.integerToBinary(i),DataUtils.integerToHex(i),htmlStr[i],charStr[i],parsingStr[i]);
            if(i>=0&&i<=31||i==127){
                //ASCII控制字符
                asciiItem.charGraphic = charStr[i];
            }else if(i>=32&&i<=126){
                //ASCII可显示字符
            }else if(i>=128&&i<=159){
                //EASCII:Windows-1252可显示字符
            }else if(i>=160&&i<=255){
                //EASCII:ISO-8859-1或者Windows-1252 可显示字符
            }
            list.add(asciiItem);
        }
        return list;
    }
}
