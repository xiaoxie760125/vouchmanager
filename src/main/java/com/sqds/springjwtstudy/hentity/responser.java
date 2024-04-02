package com.sqds.springjwtstudy.hentity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class responser<T> {
private  int status;
private T result;
}
