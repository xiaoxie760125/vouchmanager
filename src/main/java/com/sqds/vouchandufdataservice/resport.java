package com.sqds.vouchandufdataservice;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class resport<T> {

    private String key;

    public boolean isIsgroup() {
        return isgroup;
    }

    public void setIsgroup(boolean isgroup) {
        this.isgroup = isgroup;
    }

    private boolean isgroup;
    private T value;
    //定义汇总项目
    @Getter
    @Setter
    private  String[] columns;
    //定义汇总项目对应的值



    public resport(String key, T value, boolean isgroup) {

        this.key = key;
        this.value = value;
        this.isgroup = isgroup;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
