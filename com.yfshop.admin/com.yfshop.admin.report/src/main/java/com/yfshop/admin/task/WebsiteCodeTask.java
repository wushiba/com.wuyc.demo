package com.yfshop.admin.task;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.dao.WebsiteCodeDao;
import com.yfshop.admin.tool.poster.drawable.Image;
import com.yfshop.admin.tool.poster.drawable.Poster;
import com.yfshop.admin.tool.poster.drawable.Text;
import com.yfshop.admin.tool.poster.kernal.UploadResult;
import com.yfshop.admin.tool.poster.kernal.oss.OssUploader;
import com.yfshop.code.manager.WebsiteCodeDetailManager;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.mapper.RegionMapper;
import com.yfshop.code.mapper.WebsiteCodeDetailMapper;
import com.yfshop.code.mapper.WebsiteCodeMapper;
import com.yfshop.code.model.Merchant;
import com.yfshop.code.model.Region;
import com.yfshop.code.model.WebsiteCode;
import com.yfshop.code.model.WebsiteCodeDetail;
import com.yfshop.common.util.DateUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 生成商户码任务
 */
@Component
public class WebsiteCodeTask {

    @Value("${websiteCode.dir}")
    private String websiteCodeDir;
    @Value("${websiteCode.url}")
    private String websiteCodeUrl;
    @Resource
    private WebsiteCodeMapper websiteCodeMapper;
    @Resource
    private WebsiteCodeDetailMapper websiteCodeDetailMapper;
    @Resource
    private WebsiteCodeDetailManager websiteCodeDetailManager;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private MerchantMapper merchantMapper;
    @Resource
    private WebsiteCodeDao websiteCodeDao;
    @Autowired
    EmailTask emailTask;
    @Autowired
    private OssUploader ossUploader;


    private static final Logger logger = LoggerFactory.getLogger(WebsiteCodeTask.class);


    @Async
    public void doWorkWebsiteCodeFile(String orderNo) {
        List<WebsiteCode> websiteCodeList = websiteCodeMapper.selectList(Wrappers.<WebsiteCode>lambdaQuery()
                .eq(WebsiteCode::getOrderNo, orderNo));
        for (WebsiteCode websiteCode : websiteCodeList) {
            buildWebSiteCode(websiteCode);
        }
    }

    @SneakyThrows
    private void websiteCodeFile(WebsiteCode websiteCode) {
        if (websiteCode.getFileStatus() == null || "WAIT".equals(websiteCode.getFileStatus()) || "FAIL".equals(websiteCode.getFileStatus())) {
            websiteCode.setFileStatus("DOING");
            websiteCodeMapper.updateById(websiteCode);
            File dirs = new File(websiteCodeDir + "/" + websiteCode.getMerchantId() + "/" + websiteCode.getBatchNo());
            File fileZip = new File(websiteCodeDir + "/" + websiteCode.getMerchantId() + "/" + websiteCode.getBatchNo() + ".zip");
            if (!dirs.isDirectory()) {
                dirs.mkdirs();
            }
            if (!fileZip.exists()) {
                List<WebsiteCodeDetail> websiteCodeDetails = websiteCodeDetailMapper.selectList(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                        .eq(WebsiteCodeDetail::getBatchId, websiteCode.getId()));
                int sum = websiteCodeDetails.size();
                int successCount = (int) websiteCodeDetails.stream().map(item -> buildWebsiteCodeFile(dirs, item.getAlias())).filter(Objects::nonNull).count();
                int failCount = sum - successCount;
                logger.info("{},成功合成{},失败合成{}", websiteCode.getBatchNo(), successCount, failCount);
                if (failCount == 0 && sum > 0) {
                    ZipUtil.zip(dirs, CharsetUtil.CHARSET_UTF_8);
                }
            }
            websiteCode.setFileStatus("FAIL");
            if (fileZip.exists()) {
                UploadResult response = ossUploader.upload(fileZip, fileZip.getName());
                if (response.isSuccessful()) {
                    logger.info("上传文件成功！");
                    websiteCode.setFileStatus("SUCCESS");
                    websiteCode.setFileUrl(response.getUrl());
                    String msg = "<p>您好!</p>\n" +
                            "<p>&nbsp;&nbsp;&nbsp;&nbsp;此邮件内含光明网点码，请妥善保管。雨帆</p>";
                    if (StringUtils.isNotBlank(websiteCode.getEmail())) {
                        try {
                            emailTask.sendAttachmentsMail(websiteCode.getEmail(), "光明网点码", msg, fileZip.getPath(), "xuwei@51jujibao.com");
                        } catch (Exception e) {
                            websiteCode.setFileStatus("FAIL-Mail");
                            e.printStackTrace();
                        }
                    }
                } else {
                    logger.info("上传文件失败！");
                }
            }
            websiteCodeMapper.updateById(websiteCode);
        }
    }

    //商户码 3位地区码+6位pid+6位年月日+5位序号
    @Async
    public void buildWebSiteCode(WebsiteCode websiteCode) {
        int count = websiteCodeDao.sumWebsiteCodeByBeforeId(websiteCode.getId(), websiteCode.getMerchantId());
        if (count > 9999) {
            logger.info("{},超出限量了", websiteCode.getBatchNo());
            websiteCode.setFileStatus("FAIL");
            websiteCodeMapper.updateById(websiteCode);
            return;
        }
        Merchant merchant = merchantMapper.selectById(websiteCode.getMerchantId());
        Region region = regionMapper.selectById(merchant.getCityId());
        String code = String.format("%s-%03d", merchant.getMobile().substring(merchant.getMobile().length() - 4), region == null ? 0 : region.getAreaCode());
        List<WebsiteCodeDetail> list = new ArrayList<>();
        for (int i = 0; i < websiteCode.getQuantity(); i++) {
            WebsiteCodeDetail websiteCodeDetail = new WebsiteCodeDetail();
            websiteCodeDetail.setBatchId(websiteCode.getId());
            websiteCodeDetail.setAlias(String.format("%s-%04d", code, ++count));
            websiteCodeDetail.setIsActivate("N");
            websiteCodeDetail.setPid(websiteCode.getMerchantId());
            websiteCodeDetail.setPidPath(websiteCode.getPidPath());
            list.add(websiteCodeDetail);
        }
        websiteCodeDetailManager.saveOrUpdateBatch(list, 200);
        websiteCodeFile(websiteCode);
    }


    private File buildWebsiteCodeFile(File dirs, String websiteCode) {
        Poster poster = new Poster();
        poster.setWidth(1087);
        poster.setHeight(1181);
        ArrayList<Image> images = new ArrayList<>();
        poster.setImages(images);
        Image background = new Image();
        background.setX(0);
        background.setY(0);
        background.setWidth(1087);
        background.setHeight(1181);
        background.setUrl("background.png");
        background.setIndex(1);
        images.add(background);
        Image qrCode = new Image();
        qrCode.setX(272);
        qrCode.setY(360);
        qrCode.setHeight(540);
        qrCode.setWidth(540);
        qrCode.setQrCode(true);
        qrCode.setQrCodeMargin(0);
        qrCode.setUrl(websiteCodeUrl + websiteCode);
        qrCode.setIndex(2);
        images.add(qrCode);
        ArrayList<Text> texts = new ArrayList<>();
        poster.setTexts(texts);
        Text text = new Text();
        text.setFontSize(48);
        text.setColor("#333333");
        text.setX(360);
        text.setY(1008);
        text.setText(websiteCode);
        texts.add(text);
        try {
            File file = new File(dirs, websiteCode + ".png");
            if (!file.exists()) {
                file.createNewFile();
                return poster.draw(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
