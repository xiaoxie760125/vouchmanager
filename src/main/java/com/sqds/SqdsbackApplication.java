package com.sqds;

import com.sqds.ufdataManager.model.ufdata.GL_AccSum;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufdata.personRepository;
import com.sqds.ufdataManager.registory.ufdata.vouchcontioan;
import com.sqds.vouchandufdataservice.Insertufdatavouchs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication(scanBasePackages ={"com.sqds.springjwtstudy","com.sqds.Reflect","com.sqds.vouchdatamanager","com.sqds.anthdatamanange","com.sqds.filelutil.excelutil","com.sqds.fileutil.pdfutil","com.sqds.vouchandufdataservice","com.sqds.vouchdatamanager.Help","com.sqds.ufdataManager.*","com.sqds.comutil","com.sqds.ufdataManager.registory.ufdata","com.sqds.vouchshiebie"})
@EnableCaching
public class SqdsbackApplication {
	@Autowired
	private com.sqds.springjwtstudy.service.dataservice dataservice;
   @Autowired
   private  com.sqds.fileutil.pdfutil.PdfUtil pdfUtil;
	@Autowired
	Insertufdatavouchs insertvouchs;
	@Autowired
	personRepository person;
   @Autowired
   private  com.sqds.ufdataManager.registory.ufdata.Gl_AccVouchRepository glAccVouchRepository;
   @Autowired com.sqds.ufdataManager.registory.ufdata.Wa_GZDataRepository gzDataRepository;
	public static void main(String[] args) {
		SpringApplication.run(SqdsbackApplication.class, args);
	}



	public int insertuser() throws InvocationTargetException, IllegalAccessException {

		List<String> form=new LinkedList<>();
		form.add("养老金");
		form.add("医保扣款");
		form.add("养老金补扣");

		form.add("风险抵押金");
		form.add("风险抵押金代扣");
		form=sortstring(form);
		ufdatabasebasic info=new ufdatabasebasic();
		info.setYear(2024);
		info.setZhangtaohao("016");
		vouchcontioan vc=new vouchcontioan();
		vc.setCcode("1002");
		GL_AccSum glAccSum=this.glAccVouchRepository.GetYue(info,vc);
		vc.setBegindate(LocalDateTime.of(2024, 2, 1, 0, 0));
		vc.setEnddate(LocalDateTime.of(2024,2,29,0,0));
		List<String> fs= this.gzDataRepository.computerFormula(vc,info);
		fs.forEach(s->System.out.println(s));
		/*GzUfModel modeltest=this.insertvouchs.getgzufmode("gzufmodel029").get(0);
		vouchcontioan vc=new vouchcontioan();
		vc.setBegindate(LocalDateTime.of(2024, 2, 1, 0, 0));
		vc.setEnddate(LocalDateTime.of(2024,2,29,0,0));
		List<Gl_Accvouch> glAccvouches=this.gzDataRepository.GetGzUfDalModel(vc,modeltest,info);

         String[] columns=new String[]{"应发合计","扣款合计","实发合计","工龄工资","绩效工资"};
		File file=new File("gzdaoru.xlsx");*/
		/* List<axinfo> aresult=gzDataRepository.getcolums(columns,false,info);

		vc.setCperson_id("杨静");*/
		//StringBuilder result =gzDataRepository.insertgzdatafromExcelFile(file,info);
		return  1;

	}
	public List<String> sortstring(List<String> title)
	{
		for(int i=0;i<title.size();i++)
		{
			String needsord=title.get(i);
			for(int j=i+1;j<title.size();j++)
			{
				String needcompare=title.get(j);
                if(needcompare.contains(needsord))
                {
                    title.set(i, needcompare);
                    title.set(j, needsord);
                    needsord=title.get(i);
                }
			}
		}

		return title;
	}
	public String  getchinesrnum(Double needchangenum)
	{
		String[] chinese={"壹","贰","叁","肆","伍","陆","柒","捌","玖"};
		String[] unit={"元","拾","佰","仟","万","亿"};
		int numpart=needchangenum.intValue();
		DecimalFormat decimalFormat=new DecimalFormat("#.00");

		Double divm=Double.valueOf(decimalFormat.format(needchangenum-numpart))*100;
		int decpart=divm.intValue();
		System.out.println(divm);
		String chinesenum="";
		for(int i=0;i<unit.length-3 && numpart>0;i++)
		{
			System.out.println(numpart%10000);
			int part=numpart%10000;
			switch (i) {
				case 0:
					unit[0]="元";
					break;
				case 1:
					unit[0]="万";
					break;
				case 2:
					unit[0]="亿";
					break;

				default:
					break;
			}
			if(part>0)
			{
				int unitnum=0;
				while (part%10==0)
				{
					chinesenum=((part/10)%10!=0)?unit[0]+chinesenum:chinesenum;
					unitnum++;
					part/=10;
				}
				while (part%10>0) {
					chinesenum=chinese[part%10-1]+unit[unitnum]+chinesenum;
					part/=10;
					System.out.println(chinesenum);
					unitnum++;
				}
				//System.out.println(chinesenum);
			}
			numpart/=10000;
			System.out.println(numpart);

		}
		String[] divpartunit={"分","角"};
		int divnum=0;
		String xiaoshu="";
		while (decpart%10==0)
		{
			divnum++;
			decpart/=10;
		}
		while (decpart%10>0) {
			int divs=decpart%10;
			System.out.println(divs);
			xiaoshu=chinese[divs-1]+divpartunit[divnum]+xiaoshu;
			decpart/=10;
			divnum++;


		}
		chinesenum=chinesenum+xiaoshu;
		System.out.println(chinesenum);
		return  chinesenum;

	}

}
