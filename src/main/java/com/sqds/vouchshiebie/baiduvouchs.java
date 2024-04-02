package com.sqds.vouchshiebie;


import io.jsonwebtoken.io.IOException;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.util.UUID;

@Service
public class baiduvouchs {
    @Value("${baiduapi}")
    public    String API_key;
    @Value("${baidukey}")
    public    String SECRET_key;
    OkHttpClient client=new OkHttpClient().newBuilder().build();

    public String getAccessToken() throws IOException, java.io.IOException {
       String url="https://aip.baidubce.com/rest/2.0/ocr/v1/vat_invoice";
       MediaType mediatype=MediaType.parse("application/x-www-form-urlencoded");
       RequestBody requestBody=RequestBody.create(mediatype,"grant_type=client_credentials&client_id="+API_key
       +"&client_secret="+SECRET_key);
  
       Request reqest=new Request.Builder().url("https://aip.baidubce.com/oauth/2.0/token")
               .method("POST",requestBody)
               .addHeader("Content-Type","application/x-www-form-urlencoded")
               .build();
       Response response=client.newCall(reqest).execute();
       String responseData=response.body().string();
       return new JSONObject(responseData).getString("access_token");
    }

   public  JSONObject vouchinfo(MultipartFile files) throws Exception {
        String filename=files.getOriginalFilename();
        String  profix=filename.substring(filename.lastIndexOf("."));
       String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/vat_invoice";
       File imagefile= File.createTempFile(UUID.randomUUID().toString(),profix);
        files.transferTo(imagefile);
         byte[] images=FileUtil.readFileByBytes(imagefile.getPath());
         String imagestr=Base64Util.encode(images);
         String image="image="+imagestr;
         String token=getAccessToken();
         String result=HttpUtil.post(url,token,image);
         return  new JSONObject(result);


   }
   public  String vouchinfrombase64(String imagedata) throws  Exception
   {
       String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/vat_invoice";
       String encodedate= URLEncoder.encode(imagedata,"UTF-8");
       String image="image="+encodedate;
       String token=getAccessToken();
       String result=HttpUtil.post(url,token,image);
       return  result;
   }
}
