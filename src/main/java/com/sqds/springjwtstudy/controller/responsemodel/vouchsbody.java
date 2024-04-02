package com.sqds.springjwtstudy.controller.responsemodel;

import com.sqds.vouchandufdataservice.axinfo;
import com.sqds.vouchdatamanager.model.vouchs;
import lombok.Data;

import java.util.List;

/**
 * ЖѕЦ¤Ме
 */
@Data
public class vouchsbody {
    private List<vouchs> vs;
    private axinfo info;
    private String person;
}
