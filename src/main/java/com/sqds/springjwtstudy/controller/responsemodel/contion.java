package com.sqds.springjwtstudy.controller.responsemodel;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class  contion<T>
{
    private  List<T> newsvouchs;
    private List<String> columns;
    
}
