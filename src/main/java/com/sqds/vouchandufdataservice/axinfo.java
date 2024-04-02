package com.sqds.vouchandufdataservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class axinfo {
   String title;
   String value;
   String column;
   String  width;
   public  axinfo(String title,String value)
   {
      this.title=title;
      this.value=value;
   }



}
