package com.aitd.module_login.utils;

import com.aitd.module_login.bean.CountryBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;


public class CountryData {
    public static String countryString =
            "[{\"short\":\"AD\",\"name\":\"安道爾共和國\",\"en\":\"Andorra\",\"tel\":\"376\"}," +
                    "{\"short\":\"AE\",\"name\":\"阿拉伯聯合酋長國\",\"en\":\"UnitedArabEmirates\",\"tel\":\"971\"}" +
                    ",{\"short\":\"AF\",\"name\":\"阿富汗\",\"en\":\"Afghanistan\",\"tel\":\"93\"},{\"short\":\"AG\",\"name\":\"安提瓜和巴布達\"" +
                    ",\"en\":\"AntiguaandBarbuda\",\"tel\":\"1268\"},{\"short\":\"AI\",\"name\":\"安圭拉島\",\"en\":\"Anguilla\",\"tel\":\"1" +
                    "264\"},{\"short\":\"AL\",\"name\":\"阿爾巴尼亞\",\"en\":\"Albania\",\"tel\":\"355\"},{\"short\":\"AM\",\"name\":\"亞美尼亞\"" +
                    ",\"en\":\"Armenia\",\"tel\":\"374\"},{\"short\":\"\",\"name\":\"阿森松\",\"en\":\"Ascension\",\"tel\":\"247\"},{\"short\":\"" +
                    "AO\",\"name\":\"安哥拉\",\"en\":\"Angola\",\"tel\":\"244\"},{\"short\":\"AR\",\"name\":\"阿根廷\",\"en\":\"Argentina\",\"te" +
                    "l\":\"54\"},{\"short\":\"AT\",\"name\":\"奧地利\",\"en\":\"Austria\",\"tel\":\"43\"},{\"short\":\"AU\",\"name\":\"澳大利亞\"," +
                    "\"en\":\"Australia\",\"tel\":\"61\"},{\"short\":\"AZ\",\"name\":\"阿塞拜疆\",\"en\":\"Azerbaijan\",\"tel\":\"994\"},{\"short" +
                    "\":\"BB\",\"name\":\"巴巴多斯\",\"en\":\"Barbados\",\"tel\":\"1246\"},{\"short\":\"BD\",\"name\":\"孟加拉國\",\"en\":" +
                    "\"Bangladesh\",\"tel\":\"880\"},{\"short\":\"BE\",\"name\":\"比利時\",\"en\":\"Belgium\",\"tel\":\"32\"},{\"short\":" +
                    "\"BF\",\"name\":\"布基納法索\",\"en\":\"Burkina-faso\",\"tel\":\"226\"}, {\"short\":\"BG\",\"name\":\"保加利亞\",\"en\":" +
                    "\"Bulgaria\",\"tel\":\"359\"},{\"short\":\"BH\",\"name\":\"巴林\",\"en\":\"Bahrain\",\"tel\":\"973\"},{\"short\":\"BI\"," +
                    "\"name\":\"布隆迪\",\"en\":\"Burundi\",\"tel\":\"257\"},{\"short\":\"BJ\",\"name\":\"貝寧\",\"en\":\"Benin\",\"tel\":" +
                    "\"229\"},{\"short\":\"BL\",\"name\":\"巴勒斯坦\",\"en\":\"Palestine\",\"tel\":\"970\"},{\"short\":\"BM\",\"name\":\"" +
                    "百慕大群島\",\"en\":\"BermudaIs.\",\"tel\":\"1441\"},{\"short\":\"BN\",\"name\":\"文萊\",\"en\":\"Brunei\",\"tel\":\"" +
                    "673\"},{\"short\":\"BO\",\"name\":\"玻利維亞\",\"en\":\"Bolivia\",\"tel\":\"591\"},{\"short\":\"BR\",\"name\":\"巴西" +
                    "\",\"en\":\"Brazil\",\"tel\":\"55\"},{\"short\":\"BS\",\"name\":\"巴哈馬\",\"en\":\"Bahamas\",\"tel\":\"1242\"}," +
                    "{\"short\":\"BW\",\"name\":\"博茨瓦納\",\"en\":\"Botswana\",\"tel\":\"267\"} ,{\"short\":\"BY\",\"name\":\"白俄羅斯\"" +
                    ",\"en\":\"Belarus\",\"tel\":\"375\"},{\"short\":\"BZ\",\"name\":\"伯利茲\",\"en\":\"Belize\",\"tel\":\"501\"}," +
                    "{\"short\":\"CA\",\"name\":\"加拿大\",\"en\":\"Canada\",\"tel\":\"1\"} ,{\"short\":\"\",\"name\":\"開曼群島\",\"en" +
                    "\":\"CaymanIs.\",\"tel\":\"1345\"},{\"short\":\"CF\",\"name\":\"中非共和國\",\"en\":\"CentralAfricanRepublic\",\"tel" +
                    "\":\"236\"},{\"short\":\"CG\",\"name\":\"剛果\",\"en\":\"Congo\",\"tel\":\"242\"},{\"short\":\"CH\",\"name\":\"瑞士" +
                    "\",\"en\":\"Switzerland\",\"tel\":\"41\"},{\"short\":\"CK\",\"name\":\"庫克群島\",\"en\":\"CookIs.\",\"tel\":\"682\"}" +
                    ",{\"short\":\"CL\",\"name\":\"智利\",\"en\":\"Chile\",\"tel\":\"56\"},{\"short\":\"CM\",\"name\":\"喀麥隆\",\"en\":" +
                    "\"Cameroon\",\"tel\":\"237\"},{\"short\":\"CN\",\"name\":\"中國\",\"en\":\"China\",\"tel\":\"86\"},{\"short\":\"CO\"," +
                    "\"name\":\"哥倫比亞\",\"en\":\"Colombia\",\"tel\":\"57\"},{\"short\":\"CR\",\"name\":\"哥斯達黎加\",\"en\":\"CostaRica\"," +
                    "\"tel\":\"506\"},{\"short\":\"CS\",\"name\":\"捷克\",\"en\":\"Czech\",\"tel\":\"420\"},{\"short\":\"CU\",\"name\":\"古巴\"," +
                    "\"en\":\"Cuba\",\"tel\":\"53\"},{\"short\":\"CY\",\"name\":\"塞浦路斯\",\"en\":\"Cyprus\",\"tel\":\"357\"},{\"short\":\"DE\",\"name\":\"德國\",\"en\":\"Germany\",\"tel\":" +
                    "\"49\"},{\"short\":\"DJ\",\"name\":\"吉布提\",\"en\":\"Djibouti\",\"tel\":\"253\"},{\"short\":\"DK\",\"name\":\"丹麥\",\"en" +
                    "\":\"Denmark\",\"tel\":\"45\"},{\"short\":\"DO\",\"name\":\"多米尼加共和國\",\"en\":\"DominicaRep.\",\"tel\":\"1890\"}," +
                    "{\"short\":\"DZ\",\"name\":\"阿爾及利亞\",\"en\":\"Algeria\",\"tel\":\"213\"} ,{\"short\":\"EC\",\"name\":\"厄瓜多爾\",\"en" +
                    "\":\"Ecuador\",\"tel\":\"593\"},{\"short\":\"EE\",\"name\":\"愛沙尼亞\",\"en\":\"Estonia\",\"tel\":\"372\"},{\"short\":" +
                    "\"EG\",\"name\":\"埃及\",\"en\":\"Egypt\",\"tel\":\"20\"},{\"short\":\"ES\",\"name\":\"西班牙\",\"en\":\"Spain\",\"tel\"" +
                    ":\"34\"},{\"short\":\"ET\",\"name\":\"埃塞俄比亞\",\"en\":\"Ethiopia\",\"tel\":\"251\"},{\"short\":\"FI\",\"name\":\"芬蘭\"" +
                    ",\"en\":\"Finland\",\"tel\":\"358\"},{\"short\":\"FJ\",\"name\":\"斐濟\",\"en\":\"Fiji\",\"tel\":\"679\"},{\"short\":\"FR\"" +
                    ",\"name\":\"法國\",\"en\":\"France\",\"tel\":\"33\"},{\"short\":\"GA\",\"name\":\"加蓬\",\"en\":\"Gabon\",\"tel\":\"241\"}," +
                    "{\"short\":\"GB\",\"name\":\"英國\",\"en\":\"UnitedKiongdom\",\"tel\":\"44\"} ,{\"short\":\"GD\",\"name\":\"格林納達\",\"en\":" +
                    "\"Grenada\",\"tel\":\"1809\"},{\"short\":\"GE\",\"name\":\"格魯吉亞\",\"en\":\"Georgia\",\"tel\":\"995\"},{\"short\":\"GF\"," +
                    "\"name\":\"法屬圭亞那\",\"en\":\"FrenchGuiana\",\"tel\":\"594\"},{\"short\":\"GH\",\"name\":\"加納\",\"en\":\"Ghana\",\"tel\"" +
                    ":\"233\"},{\"short\":\"GI\",\"name\":\"直布羅陀\",\"en\":\"Gibraltar\",\"tel\":\"350\"},{\"short\":\"GM\",\"name\":\"岡比亞\"" +
                    ",\"en\":\"Gambia\",\"tel\":\"220\"},{\"short\":\"GN\",\"name\":\"幾內亞\",\"en\":\"Guinea\",\"tel\":\"224\"},{\"short\":\"GR" +
                    "\",\"name\":\"希臘\",\"en\":\"Greece\",\"tel\":\"30\"},{\"short\":\"GT\",\"name\":\"危地馬拉\",\"en\":\"Guatemala\",\"tel\":" +
                    "\"502\"},{\"short\":\"GU\",\"name\":\"關島\",\"en\":\"Guam\",\"tel\":\"1671\"},{\"short\":\"GY\",\"name\":\"圭亞那\",\"en\":" +
                    "\"Guyana\",\"tel\":\"592\"},{\"short\":\"HK\",\"name\":\"中國香港\",\"en\":\"Hongkong\",\"tel\":\"852\"},{\"short\":\"" +
                    "HN\",\"name\":\"洪都拉斯\",\"en\":\"Honduras\",\"tel\":\"504\"},{\"short\":\"HT\",\"name\":\"海地\",\"en\":\"Haiti\",\"tel\"" +
                    ":\"509\"},{\"short\":\"HU\",\"name\":\"匈牙利\",\"en\":\"Hungary\",\"tel\":\"36\"},{\"short\":\"ID\",\"name\":\"印度尼西亞\"" +
                    ",\"en\":\"Indonesia\",\"tel\":\"62\"},{\"short\":\"IE\",\"name\":\"愛爾蘭\",\"en\":\"Ireland\",\"tel\":\"353\"},{\"short\":\"" +
                    "IL\",\"name\":\"以色列\",\"en\":\"Israel\",\"tel\":\"972\"},{\"short\":\"IN\",\"name\":\"印度\",\"en\":\"India\",\"tel\":\"9" +
                    "1\"},{\"short\":\"IQ\",\"name\":\"伊拉克\",\"en\":\"Iraq\",\"tel\":\"964\"},{\"short\":\"IR\",\"name\":\"伊朗\",\"en\":\"Ira" +
                    "n\",\"tel\":\"98\"},{\"short\":\"IS\",\"name\":\"冰島\",\"en\":\"Iceland\",\"tel\":\"354\"},{\"short\":\"IT\",\"name\":\"意大" +
                    "利\",\"en\":\"Italy\",\"tel\":\"39\"},{\"short\":\"\",\"name\":\"科特迪瓦\",\"en\":\"IvoryCoast\",\"tel\":\"225\"},{\"short\"" +
                    ":\"JM\",\"name\":\"牙買加\",\"en\":\"Jamaica\",\"tel\":\"1876\"},{\"short\":\"JO\",\"name\":\"約旦\",\"en\":\"Jordan\",\"tel" +
                    "\":\"962\"},{\"short\":\"JP\",\"name\":\"日本\",\"en\":\"Japan\",\"tel\":\"81\"},{\"short\":\"KE\",\"name\":\"肯尼亞\",\"en\"" +
                    ":\"Kenya\",\"tel\":\"254\"},{\"short\":\"KG\",\"name\":\"吉爾吉斯坦\",\"en\":\"Kyrgyzstan\",\"tel\":\"331\"},{\"short\":\"KH" +
                    "\",\"name\":\"柬埔寨\",\"en\":\"Kampuchea(Cambodia)\",\"tel\":\"855\"},{\"short\":\"KP\",\"name\":\"朝鮮\",\"en\":\"NorthKor" +
                    "ea\",\"tel\":\"850\"},{\"short\":\"KR\",\"name\":\"韓國\",\"en\":\"Korea\",\"tel\":\"82\"},{\"short\":\"KW\",\"name\":\"科威特\",\"en\":\"Kuwait\",\"tel\"" +
                    ":\"965\"},{\"short\":\"KZ\",\"name\":\"哈薩克斯坦\",\"en\":\"Kazakstan\",\"tel\":\"7\"},{\"short\":\"LA\",\"name\":\"老撾\"" +
                    ",\"en\":\"Laos\",\"tel\":\"856\"},{\"short\":\"LB\",\"name\":\"黎巴嫩\",\"en\":\"Lebanon\",\"tel\":\"961\"},{\"short\":\"LC\"" +
                    ",\"name\":\"聖盧西亞\",\"en\":\"St.Lucia\",\"tel\":\"1758\"},{\"short\":\"LI\",\"name\":\"列支敦士登\",\"en\":\"Liechtenstei" +
                    "n\",\"tel\":\"423\"},{\"short\":\"LK\",\"name\":\"斯里蘭卡\",\"en\":\"SriLanka\",\"tel\":\"94\"},{\"short\":\"LR\",\"name\":" +
                    "\"利比里亞\",\"en\":\"Liberia\",\"tel\":\"231\"},{\"short\":\"LS\",\"name\":\"萊索托\",\"en\":\"Lesotho\",\"tel\":\"266\"},{" +
                    "\"short\":\"LT\",\"name\":\"立陶宛\",\"en\":\"Lithuania\",\"tel\":\"370\"}, {\"short\":\"LU\",\"name\":\"盧森堡\",\"en\":\"Lu" +
                    "xembourg\",\"tel\":\"352\"},{\"short\":\"LV\",\"name\":\"拉脫維亞\",\"en\":\"Latvia\",\"tel\":\"371\"},{\"short\":\"LY\",\"n" +
                    "ame\":\"利比亞\",\"en\":\"Libya\",\"tel\":\"218\"},{\"short\":\"MA\",\"name\":\"摩洛哥\",\"en\":\"Morocco\",\"tel\":\"212\"}" +
                    ",{\"short\":\"MC\",\"name\":\"摩納哥\",\"en\":\"Monaco\",\"tel\":\"377\"},{\"short\":\"MD\",\"name\":\"摩爾多瓦\",\"en\":\"M" +
                    "oldova,Republicof\",\"tel\":\"373\"},{\"short\":\"MG\",\"name\":\"馬達加斯加\",\"en\":\"Madagascar\",\"tel\":\"261\"},{\"sho" +
                    "rt\":\"ML\",\"name\":\"馬里\",\"en\":\"Mali\",\"tel\":\"223\"},{\"short\":\"MM\",\"name\":\"緬甸\",\"en\":\"Burma\",\"tel\":" +
                    "\"95\"},{\"short\":\"MN\",\"name\":\"蒙古\",\"en\":\"Mongolia\",\"tel\":\"976\"},{\"short\":\"MO\",\"name\":\"中國澳門\",\"en\":" +
                    "\"Macao\",\"tel\":\"853\"},{\"short\":\"MS\",\"name\":\"蒙特塞拉特島\",\"en\":\"MontserratIs\",\"tel\":\"1664\"},{\"short\":" +
                    "\"MT\",\"name\":\"馬耳他\",\"en\":\"Malta\",\"tel\":\"356\"},{\"short\":\"\",\"name\":\"馬里亞那群島\",\"en\":\"MarianaIs\"," +
                    "\"tel\":\"1670\"},{\"short\":\"\",\"name\":\"馬提尼克\",\"en\":\"Martinique\",\"tel\":\"596\"},{\"short\":\"MU\",\"name\":\"" +
                    "毛里求斯\",\"en\":\"Mauritius\",\"tel\":\"230\"},{\"short\":\"MV\",\"name\":\"馬爾代夫\",\"en\":\"Maldives\",\"tel\":\"960\"" +
                    "},{\"short\":\"MW\",\"name\":\"馬拉維\",\"en\":\"Malawi\",\"tel\":\"265\"},{\"short\":\"MX\",\"name\":\"墨西哥\",\"en\":\"Me" +
                    "xico\",\"tel\":\"52\"},{\"short\":\"MY\",\"name\":\"馬來西亞\",\"en\":\"Malaysia\",\"tel\":\"60\"},{\"short\":\"MZ\",\"name\"" +
                    ":\"莫桑比克\",\"en\":\"Mozambique\",\"tel\":\"258\"},{\"short\":\"NA\",\"name\":\"納米比亞\",\"en\":\"Namibia\",\"tel\":\"26" +
                    "4\"},{\"short\":\"NE\",\"name\":\"尼日爾\",\"en\":\"Niger\",\"tel\":\"977\"},{\"short\":\"NG\",\"name\":\"尼日利亞\",\"en\":" +
                    "\"Nigeria\",\"tel\":\"234\"},{\"short\":\"NI\",\"name\":\"尼加拉瓜\",\"en\":\"Nicaragua\",\"tel\":\"505\"},{\"short\":\"NL\"" +
                    ",\"name\":\"荷蘭\",\"en\":\"Netherlands\",\"tel\":\"31\"},{\"short\":\"NO\",\"name\":\"挪威\",\"en\":\"Norway\",\"tel\":\"47" +
                    "\"},{\"short\":\"NP\",\"name\":\"尼泊爾\",\"en\":\"Nepal\",\"tel\":\"977\"},{\"short\":\"\",\"name\":\"荷屬安的列斯\",\"en\":" +
                    "\"NetheriandsAntilles\",\"tel\":\"599\"},{\"short\":\"NR\",\"name\":\"瑙魯\",\"en\":\"Nauru\",\"tel\":\"674\"},{\"short\":\"NZ\"" +
                    ",\"name\":\"新西蘭\",\"en\":\"NewZealand\",\"tel\":\"64\"},{\"short\":\"OM\",\"name\":\"阿曼\",\"en\":\"Oman\",\"tel\":\"968\"}," +
                    "{\"short\":\"PA\",\"name\":\"巴拿馬\",\"en\":\"Panama\",\"tel\":\"507\"} ,{\"short\":\"PE\",\"name\":\"秘魯\",\"en\":\"Peru\"," +
                    "\"tel\":\"51\"},{\"short\":\"PF\",\"name\":\"法屬玻利尼西亞\",\"en\":\"FrenchPolynesia\",\"tel\":\"689\"},{\"short\":\"PG\"," +
                    "\"name\":\"巴布亞新幾內亞\",\"en\":\"PapuaNewCuinea\",\"tel\":\"675\"},{\"short\":\"PH\",\"name\":\"菲律賓\",\"en\":\"Philippines\"" +
                    ",\"tel\":\"63\"},{\"short\":\"PK\",\"name\":\"巴基斯坦\",\"en\":\"Pakistan\",\"tel\":\"92\"},{\"short\":\"PL\",\"name\":\"波蘭\"," +
                    "\"en\":\"Poland\",\"tel\":\"48\"},{\"short\":\"PR\",\"name\":\"波多黎各\",\"en\":\"PuertoRico\",\"tel\":\"1787\"},{\"short\":\"PT\"" +
                    ",\"name\":\"葡萄牙\",\"en\":\"Portugal\",\"tel\":\"351\"},{\"short\":\"PY\",\"name\":\"巴拉圭\",\"en\":\"Paraguay\",\"tel\":\"595\"}" +
                    ",{\"short\":\"QA\",\"name\":\"卡塔爾\",\"en\":\"Qatar\",\"tel\":\"974\"},{\"short\":\"\",\"name\":\"留尼旺\",\"en\":\"Reunion\",\"tel" +
                    "\":\"262\"},{\"short\":\"RO\",\"name\":\"羅馬尼亞\",\"en\":\"Romania\",\"tel\":\"40\"},{\"short\":\"RU\",\"name\":\"俄羅斯\",\"en\":" +
                    "\"Russia\",\"tel\":\"7\"},{\"short\":\"SA\",\"name\":\"沙特阿拉伯\",\"en\":\"SaudiArabia\",\"tel\":\"966\"},{\"short\":\"SB\",\"name" +
                    "\":\"所羅門群島\",\"en\":\"SolomonIs\",\"tel\":\"677\"},{\"short\":\"SC\",\"name\":\"塞舌爾\",\"en\":\"Seychelles\",\"tel\":\"248\"}" +
                    ",{\"short\":\"SD\",\"name\":\"蘇丹\",\"en\":\"Sudan\",\"tel\":\"249\"},{\"short\":\"SE\",\"name\":\"瑞典\",\"en\":\"Sweden\",\"tel\":" +
                    "\"46\"},{\"short\":\"SG\",\"name\":\"新加坡\",\"en\":\"Singapore\",\"tel\":\"65\"},{\"short\":\"SI\",\"name\":\"斯洛文尼亞\",\"en\":" +
                    "\"Slovenia\",\"tel\":\"386\"},{\"short\":\"SK\",\"name\":\"斯洛伐克\",\"en\":\"Slovakia\",\"tel\":\"421\"},{\"short\":\"SL\",\"name\":" +
                    "\"塞拉利昂\",\"en\":\"SierraLeone\",\"tel\":\"232\"},{\"short\":\"SM\",\"name\":\"聖馬力諾\",\"en\":\"SanMarino\",\"tel\":\"378\"},{" +
                    "\"short\":\"\",\"name\":\"東薩摩亞(美)\",\"en\":\"SamoaEastern\",\"tel\":\"684\"},{\"short\":\"\",\"name\":\"西薩摩亞\",\"en\":\"SanMarino" +
                    "\",\"tel\":\"685\"},{\"short\":\"SN\",\"name\":\"塞內加爾\",\"en\":\"Senegal\",\"tel\":\"221\"},{\"short\":\"SO\",\"name\":\"索馬里\"," +
                    "\"en\":\"Somali\",\"tel\":\"252\"},{\"short\":\"SR\",\"name\":\"蘇里南\",\"en\":\"Suriname\",\"tel\":\"597\"},{\"short\":\"ST\",\"name\":" +
                    "\"聖多美和普林西比\",\"en\":\"SaoTomeandPrincipe\",\"tel\":\"239\"},{\"short\":\"SV\",\"name\":\"薩爾瓦多\",\"en\":\"EISalvador\",\"tel\":" +
                    "\"503\"},{\"short\":\"SY\",\"name\":\"敘利亞\",\"en\":\"Syria\",\"tel\":\"963\"},{\"short\":\"SZ\",\"name\":\"斯威士蘭\",\"en\":\"Swaziland" +
                    "\",\"tel\":\"268\"},{\"short\":\"TD\",\"name\":\"乍得\",\"en\":\"Chad\",\"tel\":\"235\"},{\"short\":\"TG\",\"name\":\"多哥\",\"en\":\"Togo" +
                    "\",\"tel\":\"228\"},{\"short\":\"TH\",\"name\":\"泰國\",\"en\":\"Thailand\",\"tel\":\"66\"},{\"short\":\"TJ\",\"name\":\"塔吉克斯坦\",\"en" +
                    "\":\"Tajikstan\",\"tel\":\"992\"},{\"short\":\"TM\",\"name\":\"土庫曼斯坦\",\"en\":\"Turkmenistan\",\"tel\":\"993\"},{\"short\":\"TN\"," +
                    "\"name\":\"突尼斯\",\"en\":\"Tunisia\",\"tel\":\"216\"},{\"short\":\"TO\",\"name\":\"湯加\",\"en\":\"Tonga\",\"tel\":\"676\"},{\"short\"" +
                    ":\"TR\",\"name\":\"土耳其\",\"en\":\"Turkey\",\"tel\":\"90\"},{\"short\":\"TT\",\"name\":\"特立尼達和多巴哥\",\"en\":\"TrinidadandTobago" +
                    "\",\"tel\":\"1809\"},{\"short\":\"TW\",\"name\":\"中國台灣\",\"en\":\"Taiwan\",\"tel\":\"886\"},{\"short\":\"TZ\",\"name\":\"坦桑尼亞\",\"" +
                    "en\":\"Tanzania\",\"tel\":\"255\"},{\"short\":\"UA\",\"name\":\"烏克蘭\",\"en\":\"Ukraine\",\"tel\":\"380\"},{\"short\":\"UG\",\"name\":" +
                    "\"烏干達\",\"en\":\"Uganda\",\"tel\":\"256\"},{\"short\":\"US\",\"name\":\"美國\",\"en\":\"UnitedStatesofAmerica\",\"tel\":\"1\"},{\"short" +
                    "\":\"UY\",\"name\":\"烏拉圭\",\"en\":\"Uruguay\",\"tel\":\"598\"},{\"short\":\"UZ\",\"name\":\"烏茲別克斯坦\",\"en\":\"Uzbekistan\",\"tel" +
                    "\":\"233\"},{\"short\":\"VC\",\"name\":\"聖文森特島\",\"en\":\"SaintVincent\",\"tel\":\"1784\"},{\"short\":\"VE\",\"name\":\"委內瑞拉\"," +
                    "\"en\":\"Venezuela\",\"tel\":\"58\"},{\"short\":\"VN\",\"name\":\"越南\",\"en\":\"Vietnam\",\"tel\":\"84\"},{\"short\":\"YE\",\"name\":" +
                    "\"也門\",\"en\":\"Yemen\",\"tel\":\"967\"},{\"short\":\"YU\",\"name\":\"南斯拉夫\",\"en\":\"Yugoslavia\",\"tel\":\"381\"},{\"short\":\"ZA" +
                    "\",\"name\":\"南非\",\"en\":\"SouthAfrica\",\"tel\":\"27\"},{\"short\":\"ZM\",\"name\":\"贊比亞\",\"en\":\"Zambia\",\"tel\":\"260\"}," +
                    "{\"short\":\"ZR\",\"name\":\"扎伊爾\",\"en\":\"Zaire\",\"tel\":\"243\"},{\"short\":\"ZW\",\"name\":\"津巴布韋\",\"en\":\"Zimbabwe\"," +
                    "\"tel\":\"263\"}]";


    public static List<CountryBean> getCountry() {
        return new Gson().fromJson(countryString, new TypeToken<List<CountryBean>>() {
        }.getType());
    }
}
