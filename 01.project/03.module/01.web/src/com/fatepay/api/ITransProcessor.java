package com.fatepay.api;

/**
 * 交易处理器接口
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 14:15
 */
public interface ITransProcessor {
    /**
     * 处理入口
     * @return
     */
    public TransProcessorResult process();
}
