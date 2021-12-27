package com.aitd.library_common.utils;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterUtil {

    //过滤表情正则表达式
    public static final String EMOJI = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
    //匹配昵称
    public static final String MATCHER_NICKNAME = "[A-Za-z0-9_\\-\\u4e00-\\u9fa5]+";

    //匹配数字、中英文、标点符号
    public static final String MATCHER_DEFAULT = "[A-Za-z0-9_\\-\\u4e00-\\u9fa5\\p{P}]+";

    //过滤金额
    public static final String MATCHER_MONEY = "(^[1-9]\\d*(\\.\\d{1,2})?$)|(^[0]{1}(\\.\\d{1,2})?$)";

    //匹配中文字符
    public static final String MATCHER_CN = "[\\u4e00-\\u9fa5]";
    public static final String MATCHER_Emoji = "[^a-zA-Z0-9\\\\u4E00-\\\\u9FA5_,.?!:;…~_\\\\-\\\"\\\"/@*+'()<>{}/[/]()<>{}\\\\[\\\\]=%&$|\\\\/♀♂#¥£¢€\\\"^` ，。？！：；……～“”、“（）”、（——）‘’＠‘·’＆＊＃《》￥《〈〉》〈＄〉［］￡［］｛｝｛｝￠【】【】％〖〗〖〗／〔〕〔〕＼『』『』＾「」「」｜﹁﹂｀．]";


    /**
     * @param context
     * @return 返回EditText输入文本过滤器
     */
    public static InputFilter getInputFilter1(final Context context, final String toast, final String regex) {
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int start, int end, Spanned dest, int dstart, int dend) {
                if (charSequence.length() > 0) {
                    if (isMatcher(charSequence.toString(), regex)) {
                        return null;
                    } else {
                        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };
        return inputFilter;
    }


    /**
     * @param context
     * @return 返回EditText输入文本过滤器
     */
    public static InputFilter getInputFilter(final Context context, final String toast, final String regex) {
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int start, int end, Spanned dest, int dstart, int dend) {
                if (charSequence.length() > 0) {
                    if (isMatcher(charSequence.toString(), regex)) {
                        return null;
                    } else {
                        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };
        return inputFilter;
    }

    /**
     * @param context
     * @return 返回EditText输入文本过滤器
     */
    public static InputFilter getEmojiInputFilter(final Context context, final String toast) {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher emojiMatcher = emoji.matcher(charSequence);
                if (emojiMatcher.find()) {
                    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                    return "";
                }
                return null;
            }
        };
        return inputFilter;
    }

    public static InputFilter getInputFiltertwo(final Context context, final String toast, final String regex) {
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int start, int end, Spanned dest, int dstart, int dend) {
                if (charSequence.length() > 0) {
                    if (!isMatcher(charSequence.toString(), regex)) {
                        return null;
                    } else {
                        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };
        return inputFilter;
    }

    /**
     * 正则表达式匹配
     *
     * @param content 待匹配内容
     * @param regex   正则表达式
     * @return true 匹配成功
     */
    public static boolean isMatcher(String content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }

    public static InputFilter[] getInputFilterEmojo(final Context context, final String toast, final String regex) {
        InputFilter[] inputFilters = new InputFilter[]{
                new InputFilter() {
                    Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        Matcher emojiMatcher = emoji.matcher(source);
                        //LogUtil.e("inputFilters : "+source+" dest: "+dest);
                        if (emojiMatcher.find()) {
                            Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
                            //  MyToast.showText("不支持输入表情");
                            return "";
                        }
                        if (!TextUtils.isEmpty(source) && dest.length() == 15) {//超过15个字换行
                            // return "\n";
                            return source.toString().replace("\n", "");
                        }
                        return null;
                    }

                },
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            int type = Character.getType(source.charAt(i));
                            //LogUtil.e("inputFilters2 : "+source+" type: "+type);
                            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                                Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
                                return "";
                            }
                        }
                        CharSequence temp = "";
                        if (!TextUtils.isEmpty(source) && dest.length() == 15) {//超过15个字换行

                            //   return "\n";
                            return source.toString().replace("\n", "");
                        }
                        return null;
                    }
                },
                /**这里限制输入的长度为200*/
                new InputFilter.LengthFilter(25)
        };
        return inputFilters;
    }

}
