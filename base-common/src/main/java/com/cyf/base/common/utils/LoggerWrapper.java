package com.cyf.base.common.utils;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志对象
 */
public class LoggerWrapper {
    private static final String DEFAULT_LOGGER = "application";//这个名称需要跟log4j2.xml配置的一样
    public static final String SEPARATE_LOGGER_NAME = "separate";//log4j2 中定义的可以输出独立日志文件的那个logger名称

    private Logger log = null;

    public static LoggerWrapper getLoggerWrapper(){
        LoggerWrapper logger = new LoggerWrapper();
        logger.log = LoggerFactory.getLogger(DEFAULT_LOGGER);
        return logger;
    }

    public static LoggerWrapper getSeparateWrapper(String logName){
        ThreadContext.put("logName", logName);
        return new LoggerWrapper(SEPARATE_LOGGER_NAME);
    }

    public static LoggerWrapper getLoggerWrapper(Class clazz){
        return new LoggerWrapper(clazz);
    }

    public static LoggerWrapper getLoggerWrapper(String logName){
        return new LoggerWrapper(logName);
    }

    private LoggerWrapper(){
        log = LoggerFactory.getLogger(DEFAULT_LOGGER);
    }

    private LoggerWrapper(String logName){
        log = LoggerFactory.getLogger(logName);
    }

    private LoggerWrapper(Class clazz){
        log = LoggerFactory.getLogger(clazz);
    }

    public final void debug(String message) {
        this.log.debug(message);
    }

    public final void debug(String message, Throwable t) {
        this.log.debug(message, t);
    }

    public final void debug(String message, Object object) {
        this.log.debug(message, object);
    }

    public final void debug(String message, Object... params) {
        this.log.debug(message, params);
    }

    public final void error(Throwable t) {
        this.log.error(t.getMessage(), t);
    }

    public final void error(String message) {
        this.log.error(message);
    }

    public final void error(String message, Throwable t) {
        this.log.error(message, t);
    }

    public final void error(String message, Object object) {
        this.log.error(message, object);
    }

    public final void error(String message, Object... params) {
        this.log.error(message, params);
    }

    public final void error(Exception e) {
        StringBuilder sb = new StringBuilder(e.getMessage() + " \r\n");
        StackTraceElement[] traceArr = e.getStackTrace();
        for (int i = 0; i < traceArr.length; i++) {
            sb.append("    " + traceArr[i].toString() + " \r\n");
        }
        this.log.error(sb.toString());
    }

    public final void info(String message) {
        this.log.info(message);
    }

    public final void info(String message, Throwable t) {
        this.log.info(message, t);
    }

    public final void info(String message, Object object) {
        this.log.info(message, object);
    }

    public final void info(String message, Object... params) {
        this.log.info(message, params);
    }

    public final void warn(String message) {
        this.log.warn(message);
    }

    public final void warn(String message, Throwable t) {
        this.log.warn(message, t);
    }

    public final void warn(String message, Object object) {
        this.log.warn(message, object);
    }

    public final void warn(String message, Object... params) {
        this.log.warn(message, params);
    }

    public final boolean isDebugEnable() {
        return this.log.isDebugEnabled();
    }

    public final boolean isErrorEnabled() {
        return this.log.isErrorEnabled();
    }

    public final boolean isInfoEnabled() {
        return this.log.isInfoEnabled();
    }

    public final boolean isWarnEnabled() {
        return this.log.isWarnEnabled();
    }
}
