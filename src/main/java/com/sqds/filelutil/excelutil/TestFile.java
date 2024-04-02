package com.sqds.filelutil.excelutil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestFile {


    @ExceLTitle(title ="订单编号")
    private  String vouchcode;
    @ExceLTitle(title ="订单日期")
    private LocalDate vouchdate;
    @ExceLTitle(title ="订单金额")
    private BigDecimal sumvalue;
    @ExceLTitle(title="订单状态")
    private boolean  status;
    @ExceLTitle(title="价格")
    private  double price;



}
