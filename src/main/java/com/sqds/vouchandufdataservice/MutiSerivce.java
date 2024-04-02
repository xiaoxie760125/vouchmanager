package com.sqds.vouchandufdataservice;

import com.sqds.springjwtstudy.controller.responsemodel.vouchgl;
import com.sqds.vouchdatamanager.model.bankvouchs;
import com.sqds.vouchdatamanager.model.newsvouchs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sqds.vouchdatamanager.registroy.newsvouchsRepository;
import com.sqds.vouchdatamanager.registroy.bankvouchnoteRepository;
import com.sqds.vouchdatamanager.registroy.bankvouchsRepository;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MutiSerivce {
    @Autowired
    newsvouchsRepository newsvouchsRepository;
    @Autowired
    Insertufdatavouchs insertufdatavouchs;
    @Autowired
    bankvouchsRepository bankvouchsRepository;
    
    


    public String insertvouch(List<newsvouchs> vouchs,vouchtoglmodel vouchtogL,boolean isjesuan)
    {
        Thread upvouchthread=null;
        if(isjesuan)
        {
           List<newsvouchs> vouchall;
            vouchall = vouchs.stream().peek(newsvouchs -> newsvouchs.setShishou(newsvouchs.getTuiguangfei())).collect(Collectors.toList());
           upvouchthread=new Thread(new Runnable(){
               public  void  run(){
                   newsvouchsRepository.allocationend(vouchall,String.valueOf(vouchtogL.getYear()));
               }
           });
        }
        else {
            upvouchthread = new Thread(new Runnable() {
                @Override
                public void run() {
                    newsvouchsRepository.Insertorupdatevouchs(vouchs, String.valueOf(vouchtogL.getYear()));
                }
            });
        }

        String ufdatavouchnode=insertufdatavouchs.addvouchtofudata(vouchs,vouchtogL);
        String ufdatpathon="\\d{4}年\\d{1,2}月\\d{1,4}号";
        Pattern pattern=Pattern.compile(ufdatpathon);
        if(pattern.matcher(ufdatavouchnode).find())
        {
            Iterator<newsvouchs> vs=vouchs.stream().iterator();
            while (vs.hasNext())
            {
                newsvouchs s=vs.next();
                if(isjesuan)
                {
                  s.setUftvouchcode(ufdatavouchnode);
                }
                else
                {
                    s.setUfvouchcode(ufdatavouchnode);
                }

            }
            upvouchthread.start();

        }
        return ufdatavouchnode;
    }
    public  int cancevouchs(List<newsvouchs> newsvouchs,String year,boolean isupdate)
    {
        return  this.newsvouchsRepository.cancle(newsvouchs,year,isupdate);
    }



}
