package com.sqds.springjwtstudy.controller.responsemodel;

import com.sqds.vouchdatamanager.model.bankvouchnote;
import  com.sqds.ufdataManager.model.ufdata.person;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class bankvouchmodel {

    private  bankvouchnote bankvouchnote;
    private  person nodeperson;

}
