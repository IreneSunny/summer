package ruc.summer.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtractUtils {
    static final Set<Character> sentenceEndCharacters = new HashSet<Character>();
    static {
        sentenceEndCharacters.add('。');
        sentenceEndCharacters.add('.');
        sentenceEndCharacters.add(':');
        sentenceEndCharacters.add('：');
        sentenceEndCharacters.add('…');
        sentenceEndCharacters.add('？');
        sentenceEndCharacters.add('?');
        sentenceEndCharacters.add('!');
        sentenceEndCharacters.add('！');
        sentenceEndCharacters.add((char)3853);        
    }
    /**
     * 是否属于免责声明文本
     * @param text
     * @return
     */
    public static boolean isDeclaireText(String text){
        if(text.length()<20) return false;
       
        return getDeclaireWordCount(text, true)>4;
    }
    
    static String[] DeclaireWords = new String[]{"免责","提示",  "声明", "本人", "观点","无关",  "转载", "承担", "全部", "责任","法律", "遵守", "有权", "管理员" }; 
    public static int getDeclaireWordCount(String text, boolean different){
        int count = 0;
        for(String word: DeclaireWords) {
            if(different) {
                if(text.contains(word)) count++;
            } else {
                int start = 0;
                while(start>=0) {
                    start = text.indexOf(word, start);
                    if(start>=0) {
                        start = start + word.length();
                        count++;
                    }
                }                
            }
        }
        
        return count;
    }
    
    /**
     * 获取text的第一段内容，前200个字符作为摘要
     * @param text
     * @return
     */
    public static String getAbstract(String text){
        int totalSize = 0;
        int currentSize = 0;
        if(text==null){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        
        String s = trim(text);
        String[] lines = s.split("\\r|\\n| ");
        for(String line:lines) {
            totalSize += line.length();
        }
        for(String line:lines) {
            if(line.length()>250) {
                //确保摘要截止的内容以合理的句子为单位
                int pos = 0;
                for(; pos<line.length(); pos++) {
                    char ch = line.charAt(pos);
                    if(sentenceEndCharacters.contains(ch)) {
                        if(pos>200) {
                            break;
                        }
                    }
                }
                sb.append(line.substring(0, pos));
                if(pos<line.length()) sb.append("…");
            } else {
                //如果句子比较短，不是以标点符号结尾，并且还有剩余文本内容，则不选用此句子作为摘要
                int restSize = totalSize - currentSize;
                if(restSize>250) {
                    if(line.length()<5) continue;
                    if(line.length()<50 && !sentenceEndCharacters.contains(line.charAt(line.length()-1))) continue;
                    sb.append(line);
                }else{
                    sb.append(line);
                }
            }
            if(sb.length()>200) break;
        }
        return sb.toString();
    }
    
    public static boolean isBlank(List<?> list) {
        return list==null || list.size()==0;
    }
    
    public static  boolean isBlank(String text) {
        if (text == null) {
            return true;
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            //if (ch == ' ' || ch == '　' || ch == '\n' || ch == '\r') {
            if(isBlankChar(ch)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
    
    public static String trim(String s) {
        if(s==null) return null;
        return trim(new StringBuffer(s), false);
    }
    
    public static String trim(String s, boolean removeBiaodian) {
        if(s==null) return null;
        return trim(new StringBuffer(s), removeBiaodian);
    }

    public static String getNonBlankChars(String s) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<s.length(); i++) {
            char ch = s.charAt(i);
            if(!isBlankChar(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    public static boolean isBlankChar(char ch) {
        return (ch == ' ' || ch == '\n' || ch == '\r' || ch == '　' || ch == '\t' || ch == 160);
    }
    
    /**
     * 计算字符串s中非空格的字符数量
     * @param s
     * @return
     */
    public static int countNonSpaceChars(String s){
        int count = 0;
         //删除行首的空字符或回车换行符号
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if(ch>='a'&&ch<='z' || ch>='A' && ch<='Z') continue;
            //注意：空格有两个：32和160都是
            if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '　' || ch == '\t' || ch == 160) {
                continue;
            }
            count++;
        }
        return count;
    }

    private static final Set<Character> BIAODIAN = new HashSet<Character>();
    static {
        String s = ":：.。,，";
        for(int i=0; i<s.length(); i++) {
            BIAODIAN.add(s.charAt(i));
        }
        
    }
    
    /**
     * 删除字符串两端的空格、回车和换行符号
     */
    public static String trim(StringBuffer sb, boolean removeBiaodian) {        
        //删除行首的空字符或回车换行符号
        int pos = 0;
        for (pos = 0; pos < sb.length(); pos++) {
            char ch = sb.charAt(pos);
            //注意：空格有两个：32和160都是
            if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '　' || ch == '\t' || ch == 160) {
                continue;
            } else if(removeBiaodian && BIAODIAN.contains(ch)) {
                continue;
            } else {
                break;
            }
        }
        
        sb.delete(0, pos);

        //删除末尾的空字符或回车换行符号
        for (int i = sb.length() - 1; i > 0; i--) {
            char ch = sb.charAt(i);
            if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '　') {
                sb.deleteCharAt(i);
            }else if(removeBiaodian && BIAODIAN.contains(ch)) {
                sb.deleteCharAt(i);
            } else {
                break;
            }
        }

        return sb.toString();
    }

    /**
     * 获取过滤后仅保留汉字的字符串
     * @param raw_string
     * @return
     */
    public static String getFilteredHanziString(String raw_string) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<raw_string.length(); i++) {
            char ch = raw_string.charAt(i);
            if(isHanzi(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    public static boolean isHanzi(char ch) {
        if(ch=='(' || ch==')' || ch=='［' || ch=='］' || ch=='['  || ch==']' ||  ch=='】'  || ch== '（' || ch=='）' ) return false;
        return ch >= 0x4e00 && ch <= 0x9fa5 || ch>=0x0f40 && ch<=0x0f69;
    }

    public static int getHanziCount(String text) {
        int count = 0;
        for(int i=0; i<text.length(); i++) {
            if(isHanzi(text.charAt(i))) {
                count++;
            }
        }
        return count;
    }
    
    public static int getEnCharCount(String text) {
        int count = 0;
        for(int i=0; i<text.length(); i++) {
            char ch = text.charAt(i);
            if(ch>='a' && ch<='z' || ch>='A' && ch<='Z') {
                count++;
            }
        }
        return count;
    }
    
    public static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    public static boolean isAlphabetic(char ch) {
        return ch >= 'a' && ch <= 'z' || ch>='A' && ch<='Z';
    }
    
    public static boolean isNumber(String s) {
        if(s.length()==0) return false;
        
        for(int i=0; i<s.length(); i++){
            if(!isNumber(s.charAt(i))) return false; 
        }
        return true;
    }
    
    /**
     * 是否为翻页链接文字, 必须包含数字，同时不能是小数, 如果是，返回页面的页码，否则返回0；
     * @return
     */
    public static int isPageLink(String anchor){        
        int numberCount = 0;
        int notNumberCount = 0;
        StringBuilder numberBuilder = new StringBuilder();
        int dotPos = -1;
        for(int i=0; i<anchor.length(); i++) {
            char ch = anchor.charAt(i);
            if(ch>='0' && ch<='9') {
                numberBuilder.append(ch);
                numberCount++;
            } else if(!ExtractUtils.isBlankChar(ch)) {
                if(ch=='.') dotPos = i;
                notNumberCount++;
            }
        }
        if(notNumberCount==1 && dotPos>0 && dotPos<anchor.length()-1) return 0;
        if(numberBuilder.length()==0 || notNumberCount>4) return 0;
        try{
            int pageNumber = Integer.parseInt(numberBuilder.toString());
            return (pageNumber>60)?0:pageNumber;
        }catch(NumberFormatException e) {
            return 0;
        }
    }
    
    public static String fixArticleTitle(String candidateTitle) {
      //去掉标题后面或前面的站点信息描述
        if(candidateTitle!=null) {
            String left = null;
            int pos = candidateTitle.indexOf("-");
            if(pos<8) {
                pos = candidateTitle.indexOf("－");
            }
            if(pos < 8) {
                pos = candidateTitle.indexOf("_");
            }
            if(pos<8) {
                pos = candidateTitle.indexOf("—");
            }
            if(pos>=10) {
                left =  trim(candidateTitle.substring(0, pos));
            } 
            
            String right = null;
            pos = candidateTitle.lastIndexOf("-");
            int len = candidateTitle.length();
            if((len-pos)<8) {
                pos = candidateTitle.lastIndexOf("－");
            }
            if((len-pos) < 8) {
                pos = candidateTitle.lastIndexOf("_");
            }
            if((len-pos)<8) {
                pos = candidateTitle.lastIndexOf("—");
            }
            if((len-pos)>=10) {
                right =  trim(candidateTitle.substring(pos+1));
            } 
            
            //取最大值
            if(left!=null || right!=null) {
                if(left==null) return right;
                if(right == null) return left;
                if(left.length()>right.length()) {
                    return left;
                } else {
                    return right;
                }
            }
        }
        
        return trim(candidateTitle);
    }

}
