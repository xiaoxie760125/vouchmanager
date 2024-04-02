package com.sqds.ufdataManager.model.ufdata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name ="WA_GZtblset")
@Data
public class wa_gztblset {
    @Id
    private String iGzItem_id;
    private String cSetGZItemName;
    private  Integer iSetGZItemStyle;
    @Column(name ="iSetGZItemLenth")
    private  Integer cSetGZItemLenth;
    private  Integer iDecimal;
    private  Integer iSetGZItemProp;
    private  Integer iGZNum;

}
