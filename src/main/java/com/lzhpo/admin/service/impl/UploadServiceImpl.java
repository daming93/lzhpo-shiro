package com.lzhpo.admin.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.admin.entity.Rescource;
import com.lzhpo.admin.mapper.RescourceMapper;
import com.lzhpo.admin.service.UploadService;
import com.lzhpo.common.util.FileUtil;

import cn.hutool.system.SystemUtil;

/**
 * <p> Author：lzhpo </p>
 * <p> Title：</p>
 * <p> Description：</p>
 */
@Service
public class UploadServiceImpl extends ServiceImpl<RescourceMapper, Rescource> implements  UploadService {

    @Override
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        byte[] data = file.getBytes();
        Rescource rescource = new Rescource();
        QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
        String hash = FileUtil.calcETag(file.getInputStream(),file.getSize());
        wrapper.eq("hash",hash);
        wrapper.eq("source","local");
        rescource = rescource.selectOne(wrapper);
        if( rescource!= null){
            return rescource.getWebUrl();
        }
        String extName = file.getOriginalFilename().substring(
                file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID() + extName;
        String contentType = file.getContentType();
        String url = "";
        String osName = SystemUtil.getOsInfo().getName();
        if(osName.contains("Window")){
        	url = "D:"+File.separator+"upload"+File.separator+"person"+File.separator+ new SimpleDateFormat("yyyyMMdd").format(new Date())+
  				File.separator;
        }else{
        	url = File.separator+"usr"+File.separator+"local"+File.separator+"upload"+File.separator+"person"+File.separator+ new SimpleDateFormat("yyyyMMdd").format(new Date())+
      				File.separator;
        }
        File targetFile = new File(url);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(url+fileName);
        out.write(data);
        out.flush();
        out.close();
        String webUrl = url+fileName;
        rescource = new Rescource();
        rescource.setFileName(fileName);
        rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.getSize()/1024)+"kb");
        rescource.setHash(hash);
        rescource.setFileType(contentType);
        rescource.setWebUrl(webUrl);
        rescource.setSource("local");
        rescource.insert();
        return webUrl;
    }
    
    @Override
    public String upload(MultipartFile file,String from) throws IOException, NoSuchAlgorithmException {
        byte[] data = file.getBytes();
        Rescource rescource = new Rescource();
        QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
        String hash = FileUtil.calcETag(file.getInputStream(),file.getSize());
        wrapper.eq("hash",hash);
        wrapper.eq("source","local");
        rescource = rescource.selectOne(wrapper);
        if( rescource!= null){
            return rescource.getId();
        }
        String extName = file.getOriginalFilename().substring(
                file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID() + extName;
        String contentType = file.getContentType();
        
        String url = "";
        String osName = SystemUtil.getOsInfo().getName();
        if(osName.contains("Window")){
        	url = "D:"+File.separator+"upload"+File.separator+from+File.separator+ new SimpleDateFormat("yyyyMMdd").format(new Date())+
  				File.separator;
        }else{
        	url = File.separator+"usr"+File.separator+"local"+File.separator+"upload"+File.separator+from+File.separator+ new SimpleDateFormat("yyyyMMdd").format(new Date())+
      				File.separator;
        }
      
        File targetFile = new File(url);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(url+fileName);
        out.write(data);
        out.flush();
        out.close();
        String webUrl = url+fileName;
        rescource = new Rescource();
        rescource.setFileName(fileName);
        rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.getSize()/1024)+"kb");
        rescource.setHash(hash);
        rescource.setFileType(contentType);
        rescource.setWebUrl(webUrl);
        rescource.setSource("local");
        rescource.insert();
        return rescource.getId();
    }

	@Override
	public Rescource getRescource(String rescourseId) {
		// TODO Auto-generated method stub
		return baseMapper.selectById(rescourseId);
	}
}
