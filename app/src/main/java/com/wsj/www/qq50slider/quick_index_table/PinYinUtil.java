package com.wsj.www.qq50slider.quick_index_table;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/8
 * 作用 : 汉语拼音工具类, 使用了Pinyin4j.jar 类库.
 */

public class PinYinUtil {
    public static String getPinYin(String chinese) {
        if (TextUtils.isEmpty(chinese)) {
            return null;
        }

        // 1. 配置汉语转拼音对象.
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        // 大写.
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        // 没有声调.
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);


        // 2. 注意 : 该库只能对单个字符进行转换, 因此需要将他转换成字符数组, 然后再进行转换.
        final char[] charArray = chinese.toCharArray();
        String pinyin = "";
        for (int i = 0; i < charArray.length; i++) {
            // 2.1 过滤掉空格
            if (Character.isWhitespace(charArray[i])) continue;

            // 2.2 汉字是两个字节, 所以大于 127
            if (charArray[i] > 127) {
                // 可能是汉字
                try {
                    String[] pyArray = PinyinHelper.toHanyuPinyinStringArray(charArray[i], format);
                    if (pyArray != null) {
                        // 对于多音字都是用第一个读音.
                        pinyin += pyArray[0];
                    }
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            } else {
                // 直接拼接, 不是汉字, 是键盘上可以直接输入的字符.
                pinyin += charArray[i];
            }
        }
        return pinyin;

    }
}
