package com.cyf.shop.libs;

import com.cyf.base.common.bean.AjaxResult;
import com.cyf.base.common.exception.BizException;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.base.common.utils.LoggerWrapper;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/6/1
 */
public class ExceptionHandler extends SimpleMappingExceptionResolver{
    LoggerWrapper logger = LoggerWrapper.getLoggerWrapper(ExceptionHandler.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                              Object handler, Exception ex) {
        logger.error(ex);//记录异常信息到日志
        ModelAndView mv = null;
        String message = "系统发生异常！";
        if(ex instanceof BizException){//如果时业务异常，则可以取得业务错误信息用以提示用户
            message = ((BizException)ex).getMsg();
        }
        if (isNeedReturnJson(request)) {
            try {
                response.setCharacterEncoding("UTF-8");//设置编码，这个语句必须写在response.getWriter()前面
                PrintWriter writer = response.getWriter();
                writer.print(JsonUtil.toString(AjaxResult.fail(message)));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                logger.error(e);
            }
        } else {
            mv = super.doResolveException(request, response, handler, ex);
            mv.addObject("message", message);
        }
        return mv;
    }

    /**
     * 判断是否需要返回json格式的数据：如果是ajax请求，或者有指定accept为json的，都会判断为true
     * @param request
     * @return
     */
    private boolean isNeedReturnJson(HttpServletRequest request){
        //判断是否ajax请求
        String header = request.getHeader("X-Requested-With");
        boolean isAjax =  "XMLHttpRequest".equalsIgnoreCase(header) ? true : false;
        if(isAjax){
            return isAjax;
        }

        boolean isAcceptJson = false;
        String accept = request.getHeader("accept");
        if(accept != null && accept.indexOf("application/json") > -1){
            isAcceptJson = true;
        }
        return isAcceptJson;
    }
}
