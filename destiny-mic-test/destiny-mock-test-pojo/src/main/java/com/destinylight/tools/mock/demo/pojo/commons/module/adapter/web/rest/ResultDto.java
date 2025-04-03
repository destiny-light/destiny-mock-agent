package com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.rest;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 响应DTO
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/25
 */
public class ResultDto<T> {
    private String code = "0";
    private long total;
    private String message;
    private String level;
    private T data;
    private List<Object> i18nData;
    private Map<Object, Object> extData;

    @SuppressWarnings("unchecked")
    public <E> ResultDto<E> total(long total) {
        this.total = total;
        return (ResultDto<E>) this;
    }

    @SuppressWarnings("unchecked")
    public <E> ResultDto<E> message(String message) {
        this.message = message;
        return (ResultDto<E>) this;
    }

    @SuppressWarnings("unchecked")
    public <E> ResultDto<E> code(String code) {
        this.code = code;
        return (ResultDto<E>) this;
    }

    @SuppressWarnings("unchecked")
    public <E> ResultDto<E> code(int code) {
        this.code = String.valueOf(code);
        return (ResultDto<E>) this;
    }

    @SuppressWarnings("unchecked")
    public <E> ResultDto<E> extParams(Map<?, ?> params) {
        if (this.extData == null) {
            this.extData = new HashMap<>(8);
        }
        this.extData.putAll(params);
        return (ResultDto<E>) this;
    }

    @SuppressWarnings("unchecked")
    public <E> ResultDto<E> extParam(Object key, Object value) {
        if (this.extData == null) {
            this.extData = new HashMap<>(8);
        }
        this.extData.put(key, value);
        return (ResultDto<E>) this;
    }

    @SuppressWarnings("unchecked")
    public <E> ResultDto<E> level(String level) {
        this.level = level;
        return (ResultDto<E>) this;
    }

    @SuppressWarnings("unchecked")
    public <E> ResultDto<E> i18nData(List<Object> i18nData) {
        this.i18nData = i18nData;
        return (ResultDto<E>) this;
    }

    @SuppressWarnings("unchecked")
    public <E> ResultDto<E> data(T data) {
        this.data = data;
        return (ResultDto<E>) this;
    }

    public String getCode() {
        return code;
    }

    @JsonSetter
    public void setCode(String code) {
        this.code = code;
    }

    @Deprecated
    public void setCode(int code) {
        this.code = String.valueOf(code);
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<Object> getI18nData() {
        return i18nData;
    }

    public void setI18nData(List<Object> i18nData) {
        this.i18nData = i18nData;
    }

    public Map<Object, Object> getExtData() {
        return extData;
    }

    public void setExtData(Map<Object, Object> extData) {
        this.extData = extData;
    }

    @Override
    public String toString() {
        return "ResultDto [code=" + code + ", total=" + total + ", message=" + message + ", level=" + level + ", data="
                + data + ", i18nData=" + i18nData + ", extData=" + extData + "]";
    }
}
