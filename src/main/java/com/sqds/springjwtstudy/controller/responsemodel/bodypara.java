package com.sqds.springjwtstudy.controller.responsemodel;

import com.sqds.vouchdatamanager.model.newsvouchs;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
public class bodypara {
    private newsvouchs ncc;
    private Map<String,Integer> personinfo;
}
