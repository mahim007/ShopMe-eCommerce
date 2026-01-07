package com.mahim.shopme.filter;

import com.mahim.shopme.common.config.S3Config;
import com.mahim.shopme.common.entity.Setting;
import com.mahim.shopme.setting.SettingService;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Component
public class SettingFilter implements Filter {

    private final SettingService settingService;
    private final S3Config s3Config;

    public SettingFilter(SettingService settingService, S3Config s3Config) {
        this.settingService = settingService;
        this.s3Config = s3Config;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String url = httpServletRequest.getRequestURL().toString();

        if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        List<Setting> settings = settingService.getGeneralSetting();
        for (Setting setting : settings) {
            servletRequest.setAttribute(setting.getKey(), setting.getValue());
        }

        servletRequest.setAttribute("S3_BASE_URI", s3Config.getBaseS3Url());
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
