package com.aitd.module_login.bean;

import java.io.Serializable;

import me.yokeyword.indexablerv.IndexableEntity;

public class CountryCodeBean implements Serializable, IndexableEntity {

    private String country;            //--国籍名称
    private String mobCountryCode;     //--对应国籍的手机抬头
    private String countryCode;        // --国籍代码
    private String en;                 // --国籍代码

    @Override
    public String getFieldIndexBy() {
        return country;
    }

    @Override
    public void setFieldIndexBy(String indexField) {
        this.country = indexField;
    }

    @Override
    public void setFieldPinyinIndexBy(String pinyin) {

    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country){
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getMobCountryCode() {
        return mobCountryCode;
    }

    public void setMobCountryCode(String mobCountryCode) {
        this.mobCountryCode = mobCountryCode;
    }
}
