package com.yfshop.admin.task;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qiniu.http.Response;
import com.yfshop.admin.tool.poster.drawable.Image;
import com.yfshop.admin.tool.poster.drawable.Poster;
import com.yfshop.admin.tool.poster.drawable.Text;
import com.yfshop.admin.tool.poster.kernal.qiniu.QiniuConfig;
import com.yfshop.admin.tool.poster.kernal.qiniu.QiniuUploader;
import com.yfshop.code.manager.WebsiteCodeDetailManager;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.mapper.RegionMapper;
import com.yfshop.code.mapper.WebsiteCodeDetailMapper;
import com.yfshop.code.mapper.WebsiteCodeMapper;
import com.yfshop.code.model.Merchant;
import com.yfshop.code.model.Region;
import com.yfshop.code.model.WebsiteCode;
import com.yfshop.code.model.WebsiteCodeDetail;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipFile;

/**
 * 生成商户码任务
 */
@Component
@EnableAsync
public class WebsiteCodeTask {

    @Value("${websiteCode.dirs}")
    private String websiteCodeDirs;
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

    @Autowired
    QiniuUploader qiniuUploader;
    @Autowired
    QiniuConfig qiniuConfig;

    private static final Logger logger = LoggerFactory.getLogger(WebsiteCodeTask.class);

    @Async
    public void doWorkWebsiteCodeFile(Integer id) {
        WebsiteCode websiteCode = websiteCodeMapper.selectById(id);
        websiteCodeFile(websiteCode);
    }


    @SneakyThrows
    private void websiteCodeFile(WebsiteCode websiteCode) {
        if (websiteCode.getFileStatus() == null || "WAIT".equals(websiteCode.getFileStatus()) || "FAIL".equals(websiteCode.getFileStatus())) {
            websiteCode.setFileStatus("DOING");
            websiteCodeMapper.updateById(websiteCode);
            File dirs = new File(websiteCodeDirs + "/" + websiteCode.getMerchantId() + "/" + websiteCode.getBatchNo());
            File fileZip = new File(websiteCodeDirs + "/" + websiteCode.getMerchantId() + "/" + websiteCode.getBatchNo() + ".zip");
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
                if (failCount == 0) {
                    ZipUtil.zip(dirs, CharsetUtil.CHARSET_UTF_8);
                }
            }
            websiteCode.setFileStatus("FAIL");
            if (fileZip.exists()) {
                Response response = qiniuUploader.getUploadManager().put(fileZip, fileZip.getName(), qiniuUploader.getAuth().uploadToken(qiniuConfig.getBucket()));
                if (response.isOK()) {
                    websiteCode.setFileStatus("SUCCESS");
                    websiteCode.setFileUrl(websiteCode.getBatchNo() + ".zip");
                    if (StringUtils.isNotBlank(websiteCode.getEmail())) {
                        logger.info("发送邮件");
                    }
                }
            }
            websiteCodeMapper.updateById(websiteCode);
        }
    }

    //商户码 3位地区码+6位pid+6位年月日+5位序号
    @Async
    public void buildWebSiteCode(WebsiteCode websiteCode) {
        int count = websiteCodeDetailMapper.selectCount(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getPid, websiteCode.getMerchantId())
                .ge(WebsiteCodeDetail::getCreateTime, LocalDate.now()));
        if (count > 99999) {
            logger.info("{},超出当日限量了", websiteCode.getBatchNo());
            return;
        }
        Merchant merchant = merchantMapper.selectById(websiteCode.getMerchantId());
        Region region = regionMapper.selectById(merchant.getCityId());
        String code = String.format("%03d%06d%s", region == null ? 0 : region.getAreaCode(), websiteCode.getMerchantId(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")));
        List<WebsiteCodeDetail> list = new ArrayList<>();
        for (int i = 0; i < websiteCode.getQuantity(); i++) {
            WebsiteCodeDetail websiteCodeDetail = new WebsiteCodeDetail();
            websiteCodeDetail.setBatchId(websiteCode.getId());
            websiteCodeDetail.setAlias(String.format("%s%05d", code, i + count));
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
        qrCode.setY(330);
        qrCode.setHeight(540);
        qrCode.setWidth(540);
        qrCode.setQrCode(true);
        qrCode.setQrCodeMargin(0);
        qrCode.setUrl(websiteCodeUrl+websiteCode);
        qrCode.setIndex(2);
        images.add(qrCode);
        ArrayList<Text> texts = new ArrayList<>();
        poster.setTexts(texts);
        Text text = new Text();
        text.setFontSize(48);
        text.setColor("#333333");
        text.setX(254);
        text.setY(978);
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
