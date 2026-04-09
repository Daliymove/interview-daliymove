package com.daliymove.modules.export;

import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.resume.dto.ResumeAnalysisResponse;
import com.daliymove.modules.resume.entity.Resume;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.font.FontProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * PDF导出服务
 * - 使用 Thymeleaf 模板引擎生成 HTML
 * - 使用 iText html2pdf 将 HTML 转换为 PDF
 * - 支持中文字体
 */
@Slf4j
@Service
public class PdfExportService {

    private final TemplateEngine templateEngine;

    public PdfExportService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * 导出简历分析报告为PDF
     *
     * @param resume   简历实体
     * @param analysis 分析结果
     * @return PDF字节数组
     */
    public byte[] exportResumeAnalysis(Resume resume, ResumeAnalysisResponse analysis) {
        log.info("开始导出简历分析报告PDF: resumeId={}", resume.getId());

        try {
            Context context = new Context();
            context.setVariable("resume", resume);
            context.setVariable("analysis", analysis);
            context.setVariable("scoreDetail", analysis.scoreDetail());
            context.setVariable("strengths", analysis.strengths());
            context.setVariable("suggestions", analysis.suggestions());

            String htmlContent = templateEngine.process("resume-analysis-report", context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            FontProvider fontProvider = new FontProvider();
            
            ClassPathResource fontResource = new ClassPathResource("fonts/ZhuqueFangsong-Regular.ttf");
            if (fontResource.exists()) {
                try (InputStream is = fontResource.getInputStream()) {
                    byte[] fontBytes = is.readAllBytes();
                    fontProvider.addFont(fontBytes, PdfEncodings.IDENTITY_H);
                    log.info("成功加载中文字体: ZhuqueFangsong");
                }
            } else {
                log.warn("未找到中文字体文件");
            }
            
            com.itextpdf.html2pdf.ConverterProperties converterProperties = new com.itextpdf.html2pdf.ConverterProperties();
            converterProperties.setFontProvider(fontProvider);
            
            HtmlConverter.convertToPdf(htmlContent, outputStream, converterProperties);

            log.info("简历分析报告PDF导出成功: resumeId={}", resume.getId());
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("导出PDF失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXPORT_PDF_FAILED, "导出PDF失败: " + e.getMessage());
        }
    }
}