package com.yfshop.common.util;

//import cn.afterturn.easypoi.entity.vo.BigExcelConstants;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.google.common.collect.Lists;
import com.yfshop.common.exception.ApiException;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xulg
 */
public class ExcelUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName,
                                   boolean isCreateHeader, HttpServletResponse response) {
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName,
                                   HttpServletResponse response) {
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
    }

    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        defaultExport(list, fileName, response);
    }

    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response,
                                      ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }
    }

    private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("下载Excel文件失败", e);
            // throw new NormalException(e.getMessage());
        }
    }

    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }
    }

    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
            ExcelImportUtil.importExcel(new File(filePath), pojoClass, new ImportParams());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows,
                                          Class<T> pojoClass) {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (Exception e) {
            LOGGER.error("解析Excel文件失败", e);
            throw new ApiException(500, "解析Excel文件失败");
        }
        return list;
    }

    /**
     * 导出Excel大文件
     */
    public static void exportBigExcel(List<?> list, String title, String sheetName, Class<?> pojoClass,
                                      String fileName, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        ExportParams params = new ExportParams(title, sheetName);
        List<? extends List<?>> partitions = Lists.partition(list, 10000);
        Workbook workbook = null;
        for (List<?> partition : partitions) {
            workbook = ExcelExportUtil.exportBigExcel(params, pojoClass, partition);
        }
        ExcelExportUtil.closeExportBigExcel();
        if (workbook == null) {
            return;
        }
        System.out.println("EasyPOI导出" + list.size() + "条数据耗时：" + (System.currentTimeMillis() - start) + "ms");
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * 分页导出大文件
     */
    public static void pageExportBigExcel(PageExportExcelRequest request) {
        pageExportBigExcel(request.request, request.response, request.excelExportServer, request.pojoClass,
                request.totalPage, request.queryCondition, request.title, request.sheetName, request.fileName);
    }

    /**
     * 分页导出大文件
     */
    public static void pageExportBigExcel(HttpServletRequest request, HttpServletResponse response,
                                          IExcelExportServer excelExportServer, Class<?> pojoClass,
                                          int totalPage, Object queryCondition, String title,
                                          String sheetName, String fileName) {
        if (true) {
            throw new UnsupportedOperationException();
        }
        long start = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        ExportParams params = new ExportParams(title, sheetName, ExcelType.XSSF);
        //map.put(BigExcelConstants.CLASS, pojoClass);
        //map.put(BigExcelConstants.PARAMS, params);
        //就是我们的查询参数,会带到接口中,供接口查询使用
        //map.put(BigExcelConstants.DATA_PARAMS, queryCondition);
        //map.put(BigExcelConstants.DATA_INTER, excelExportServer);
        //map.put(BigExcelConstants.FILE_NAME, fileName);
        if (totalPage <= 0) {
            return;
        }
        render(totalPage, map, request, response);
        System.out.println("EasyPOI导出" + totalPage + "页数据耗时：" + (System.currentTimeMillis() - start) + "ms");
    }

    private static void render(int totalPage, Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
        try {
            /*
            String codedFileName = "临时文件";
            Workbook workbook = ExcelExportUtil.exportBigExcel(
                    (ExportParams) model.get(BigExcelConstants.PARAMS),
                    (Class<?>) model.get(BigExcelConstants.CLASS), Collections.EMPTY_LIST);
            IExcelExportServer server = (IExcelExportServer) model.get(BigExcelConstants.DATA_INTER);
            for (int page = 1; page <= totalPage; page++) {
                List<Object> list = server.selectListForExcelExport(model.get(BigExcelConstants.DATA_PARAMS), page);
                workbook = ExcelExportUtil.exportBigExcel((ExportParams) model.get(BigExcelConstants.PARAMS),
                        (Class<?>) model.get(BigExcelConstants.CLASS), list);
            }
            ExcelExportUtil.closeExportBigExcel();
            if (model.containsKey(BigExcelConstants.FILE_NAME)) {
                codedFileName = (String) model.get(BigExcelConstants.FILE_NAME);
            }
            if (workbook instanceof HSSFWorkbook) {
                codedFileName += ".xls";
            } else {
                codedFileName += ".xlsx";
            }
            if (isIE(request)) {
                codedFileName = URLEncoder.encode(codedFileName, "UTF8");
            } else {
                codedFileName = new String(codedFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            }
            response.setHeader("content-disposition", "attachment;filename=" + codedFileName);
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isIE(HttpServletRequest request) {
        return request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0
                || request.getHeader("USER-AGENT").toLowerCase().indexOf("rv:11.0") > 0
                || request.getHeader("USER-AGENT").toLowerCase().indexOf("edge") > 0;
    }

    @Data
    @Builder
    public static class PageExportExcelRequest {
        HttpServletRequest request;
        HttpServletResponse response;
        IExcelExportServer excelExportServer;
        Class<?> pojoClass;
        int totalPage;
        Object queryCondition;
        String title;
        String sheetName;
        String fileName;
    }
}