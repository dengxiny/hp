package com.hp.service;

import java.util.List;

public interface ProcessService {
List<String> getUrlInfo(String homePage);

void work(String string, int integer,long threadSleepTime, String startMode);
void workUseIp(String string, int integer,long threadSleepTime, String startMode);

void webMagicWork(String url, int threadSize, long threadSleepTime);

void webMagicWorkUseIp(String url, int threadSize, long threadSleepTime);
}
