package com.sqds.springjwtstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.sqds.springjwtstudy.hentity.responser;
import com.sqds.ufdataManager.model.ufsystem.Ua_Account;
import com.sqds.ufdataManager.registory.ufsystem.Ua_AccountRepository;
import java.util.List;
@RestController
@RequestMapping("ufsystem")
public class ufsystemController {
    @Autowired
    private Ua_AccountRepository ua_AccountRepository;

    @GetMapping("uaccount")
    public responser<List<Ua_Account>> getall()
    {
        return new responser<>(200,this.ua_AccountRepository.findAllAccount());
    }

}
