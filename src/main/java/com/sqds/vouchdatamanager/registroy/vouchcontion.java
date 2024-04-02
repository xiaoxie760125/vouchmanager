package com.sqds.vouchdatamanager.registroy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class vouchcontion {
    private String                                                                                                                                                                                                                                                                                     vouchcode;
    private String cpsn_num;
    private  String customername;
    private Date begindate;
    private  Date enddate;
    private  Integer count;
    private  String vouch_type;
    private  String ufzhangtaohao;
    private  boolean isend;

}
