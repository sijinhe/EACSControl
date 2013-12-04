/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.monitor.management;

import java.io.Serializable;

/**
 *
 * @author Sijin
 */
public class AppHttpInfo implements Serializable{

    private String name;
    private String id;
    private String serverid;
    private long hits;
    private long durationsSum;
    private long durationsSquareSum;
    private long maximum;
    private long cpuTimeSum;
    private long systemErrors;
    private long responseSizesSum;
    private long usedMemory;
    private long maxMemory;

    public AppHttpInfo() {
    }

    public AppHttpInfo(String containerid, String serverid) {
        this.id = containerid;
        this.serverid = serverid;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    public long getCpuTimeSum() {
        return cpuTimeSum;
    }

    public void setCpuTimeSum(long cpuTimeSum) {
        this.cpuTimeSum = cpuTimeSum;
    }

    public long getDurationsSquareSum() {
        return durationsSquareSum;
    }

    public void setDurationsSquareSum(long durationsSquareSum) {
        this.durationsSquareSum = durationsSquareSum;
    }

    public long getDurationsSum() {
        return durationsSum;
    }

    public void setDurationsSum(long durationsSum) {
        this.durationsSum = durationsSum;
    }

    public long getHits() {
        return hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getMaximum() {
        return maximum;
    }

    public void setMaximum(long maximum) {
        this.maximum = maximum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getResponseSizesSum() {
        return responseSizesSum;
    }

    public void setResponseSizesSum(long responseSizesSum) {
        this.responseSizesSum = responseSizesSum;
    }

    public long getSystemErrors() {
        return systemErrors;
    }

    public void setSystemErrors(long systemErrors) {
        this.systemErrors = systemErrors;
    }
    
}
