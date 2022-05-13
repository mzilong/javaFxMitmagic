package sample.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsciiItem {
    /** 十进制 **/
    public int decimal;
    /** 二进制 **/
    public String binary;
    /** 十六进制 **/
    public String hex;
    /** html **/
    public String html;
    /** 字符图形 **/
    public String charGraphic;
    /** 解析 **/
    public String parsing;
}
