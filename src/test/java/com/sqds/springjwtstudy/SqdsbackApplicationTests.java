package com.sqds.springjwtstudy;

import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufdata.Gl_AccVouchRepository;
import com.sqds.ufdataManager.registory.ufdata.Gl_vouchClientRepository;
import com.sqds.ufdataManager.registory.ufdata.vouchcontioan;
import com.sqds.vouchandufdataservice.Insertufdatavouchs;
import com.sqds.vouchdatamanager.registroy.bankvouchsRepository;
import com.sqds.vouchshiebie.baiduvouchs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@SpringBootTest
class SqdsbackApplicationTests {
	@Autowired
	bankvouchsRepository bankvouchsRepository;
	@Autowired
	Insertufdatavouchs radisServce;
	@Autowired
	baiduvouchs vouchs;
	@Autowired
	Gl_AccVouchRepository glAccVouchRepository;
	@Autowired
	Gl_vouchClientRepository glclient;



	@Test
	void  glsumtest()
	{

		ufdatabasebasic info=new ufdatabasebasic();
		info.setYear(2023);
		info.setZhangtaohao("016");
		Map<Integer,Integer> codesin=glclient.getinum(LocalDateTime.of(2023,6,1,0,0),LocalDateTime.of(2023,12,31,0,0),info);
	List<Gl_Accvouch> getvouchs=glclient.findvouchs(2,2,info,1);
		vouchcontioan vc=new vouchcontioan();
		vc.setCcode("21810108");
		vc.setCperson_id("20301");
		vc.setBegindate(LocalDateTime.of(2023,6,1,0,0));
		vc.setEnddate(LocalDateTime.of(2023,12,31,0,0));
		List<Gl_Accvouch> glAccSum=this.glAccVouchRepository.dymamicvouchs(info,vc);
		Gl_Accvouch gl=glAccSum.get(glAccSum.size()-1);
		for (Gl_Accvouch gl_Accvouch : glAccSum)
		{
			System.out.println("月份 | 科目编码 | 摘要 | 借方发生额 | 贷方发生额 | 方向 | 余额");
			System.out.println(gl_Accvouch.getIperiod()+" | "+gl_Accvouch.getCcode()+" | "+gl_Accvouch.getCdigest()+" | "+gl_Accvouch.getMd()+" | "+gl_Accvouch.getMc()+" | "+gl_Accvouch.getCcend_c()+" | "+gl_Accvouch.getMe());
		}
	    System.out.println(glAccSum.size());
	}

	void contextLoads() {
		try {

 		 String token=vouchs.getAccessToken();
		  System.out.println(token);



			/*vouchcontion v=new vouchcontion();
			LocalDateTime data=LocalDateTime.of(2023,1,1,0,0,0);
			ZonedDateTime zdt=data.atZone(ZoneId.systemDefault());
			v.setBegindate(Date.from(zdt.toInstant()));
			v.setEnddate(new Date());
            v.setUfzhangtaohao("009");
			v.setCustomername("党宝宝");
			List<com.sqds.vouchdatamanager.model.bankvouchs> vouchs=bankvouchsRepository.getbankvouchs(v,"2025");for (bankvouchs b:vouchs)
			{
				System.out.println(b.getVouchcustname()+" "+b.getMc()+" "+b.getMd());
			}
			com.sqds.vouchandufdataservice.vouchtoglmodel vs= this.radisServce.getvouchmanager("shoukuan0092025");
			System.out.println(vs);
			List<Gl_Accvouch> model=this.bankvouchsRepository.getbankinfovouchs(vouchs,vs,"2025");
			//getchinesrnum(3300.00);*/

		}
		catch (Exception e)
		{
               System.out.println(e.getMessage());
		}
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

			int unitnum=0;
			if(part>0)
			{
				while (part/10>0)
				{
					String cin=part%10==0?"零":chinese[part%10-1];
					if(part%10>0)
					{
						chinesenum=chinese[part%10-1]+unit[unitnum]+chinesenum;
					}
					else
					{
						if((part/10)%10>0) {
							chinesenum = (unitnum == 0 || chinesenum.equals("元") || i==0? unit[0] : "零") + chinesenum;
						}
					}

					unitnum++;
					part/=10;
					System.out.println(unitnum);

				}
				chinesenum=chinese[part%10-1]+unit[unitnum]+chinesenum;
				System.out.println(chinesenum);

				if(numpart/10000!=0 && unitnum<3 )
				{
					chinesenum="零"+chinesenum;
				}
			}
			else
			{
				chinesenum=unit[0]+chinesenum;
			}
			System.out.println(unitnum);


			numpart/=10000;

			System.out.println(numpart);

		}
		String[] divpartunit={"分","角"};
		int divnum=0;
		String xiaoshu="";
		if(decpart>0) {
			/*if(decpart/10 == 0) {
				divnum++;
				xiaoshu = "零" + xiaoshu;

			}*/
			while (decpart % 10 > 0) {
				int divs = decpart % 10;
				System.out.println(divs);
				xiaoshu = chinese[divs - 1] + divpartunit[divnum] + xiaoshu;
				decpart /= 10;
				divnum++;


			}
		}
        if(decpart/10==0 && decpart>0)xiaoshu="零"+xiaoshu;
		chinesenum=chinesenum+xiaoshu;
		System.out.println(chinesenum);
		return  chinesenum;

	}
}
