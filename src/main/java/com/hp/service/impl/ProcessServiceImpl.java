package com.hp.service.impl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import com.hp.common.DateUtil;
import com.hp.common.JsonUtil;
import com.hp.common.LianJiaCostant;
import com.hp.common.StartModeEnum;
import com.hp.dao.FailInfoDao;
import com.hp.dao.LianJiaDao;
import com.hp.dao.TestDao;
import com.hp.dao.model.FailInfoDo;
import com.hp.dao.model.LianJiaDO;
import com.hp.dao.model.TestDO;
import com.hp.redis.RedisCache;
import com.hp.redis.RedisLock;
import com.hp.service.ProcessService;
import com.hp.service.TestService;

import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.selector.Html;

@Service
public class ProcessServiceImpl implements ProcessService {
	public static Logger logger = LoggerFactory.getLogger(ProcessServiceImpl.class);

	@Autowired
	LianJiaDao lianJiaDao;

	@Autowired
	FailInfoDao failInfoDao;
	
	@Autowired
	WorkProcesser workProcesser;

	@Autowired
	public RedisCache redisCache;

	private static int count = 0;

	private static int ipCount = 0;
	
	private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

	public static Set<String> set = new HashSet<>();

	public static Set<String> IpPool = new HashSet<>();
	
	//阻塞标志位
	public static int flag;
	/*
	 * @Autowired public RedisLock redisLock;
	 */
	@Autowired 
	public TestService testService;
	
	@Value("${spider-ip-url}")
	private String ipUrl;
	
	@Override
	public List<String> getUrlInfo(String homePage) {
		List<String> list = new ArrayList<>();
		getListPageUrl(homePage);
		logger.info("=======页码数："+set.size());
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

	public void getListPageUrl(String url) {
		DefaultHttpClient httpclient = new DefaultHttpClient(new PoolingClientConnectionManager());
		CloseableHttpResponse response = null;
		List<String> list = new ArrayList<>();
		HttpGet httpget = new HttpGet(url);
		HttpEntity entity = null;
		String s = "";
		try {
			httpget.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
			response = httpclient.execute(httpget);
			entity = response.getEntity();
			s = EntityUtils.toString(entity, "UTF-8");
			if (response.getStatusLine().getStatusCode() == 200) {
				Document doc = Jsoup.parse(s);
				Page page = new Page();
				Html html = new Html(doc);
				page.setHtml(html);
				System.out.println(
						page.getHtml().xpath("//div[@class='page-box fr']/div/@page-data").toString().split(":")[1]
								.split(",")[0]);
				int len = Integer.valueOf(
						page.getHtml().xpath("//div[@class='page-box fr']/div/@page-data").toString().split(":")[1]
								.split(",")[0]);
				for (int i = 1; i <= len; i++) {
					getDetailPageUrl(url + "/pg" + i);
					Thread.sleep(500);
				}
			} else {
				logger.info("获取主页异常");
				insertFailInfo(url);
			}

		} catch (Exception e) {
			logger.info("获取主页异常");
			insertFailInfo(url);
		} finally {
			httpclient.close();
		}
	}

	public void getDetailPageUrl(String url) {
		// String url="https://hz.lianjia.com/ershoufang/xihu/";
		DefaultHttpClient httpclient = new DefaultHttpClient(new PoolingClientConnectionManager());
		CloseableHttpResponse response = null;
		List<String> list = new ArrayList<>();
		HttpGet httpget = new HttpGet(url);
		HttpEntity entity = null;
		String s = "";
		try {
			httpget.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
			response = httpclient.execute(httpget);
			entity = response.getEntity();
			s = EntityUtils.toString(entity, "UTF-8");
			if (response.getStatusLine().getStatusCode() == 200) {
				Document doc = Jsoup.parse(s);
				Page page = new Page();
				Html html = new Html(doc);
				page.setHtml(html);
				list = page.getHtml().xpath("//ul[@class='sellListContent']/li/a/@href").all();
				//System.out.println(list.size());
				//System.out.println(list.toString());
				duplicateRemoval(list);
			} else {
				logger.info("获取列表页异常");
				insertFailInfo(url);
			}
		} catch (Exception e) {
			logger.info("获取列表页异常");
			insertFailInfo(url);
		} finally {
			httpclient.close();
		}
	}

	public void duplicateRemoval(List<String> list) {
		for (String string : list) {
			set.add(string);
		}
	}

	public void initPageUrl(int threadSize) {
		//ExecutorService threadPool = Executors.newCachedThreadPool();
		ExecutorService threadPool = Executors.newFixedThreadPool(threadSize);
		CompletionService<String> cs = new ExecutorCompletionService<String>(threadPool);
		for (String url : set) {
			final String url1 = url;
			cs.submit(new Callable<String>() {
				public String call() throws Exception {
					LianJiaDO lianJiaDO = new LianJiaDO();
					lianJiaDO.setUrl(url1);
					lianJiaDO.setCreateTime(DateUtil.formatTo_yyyyMMdd());
					return lianJiaDao.insert(lianJiaDO) + "--" + url1;
				}
			});
		}
		for (String url : set) {
			String url1 = "";
			try {
				String a = cs.take().get();
				url1 = a.split("--")[1];
				count++;
				if (a.split("--")[0].equals("0")) {
					logger.info("初始化详情页失败");
					insertFailInfo(url1, "insert PageUrl error ");
				}
			} catch (Exception e) {
				logger.info("初始化详情页失败");
				insertFailInfo(url1, "insert PageUrl error ");
			}
		}
		threadPool.shutdown();
		while(true) {
			if(count==set.size()) {
				logger.info("insert PageUrl over");
				//System.out.println("insert PageUrl over");
				//set.clear();
			  break;
			  }else {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			}
		}
		count=0;
		
	}

	public void updatePageUrl(int threadSize, long threadSleepTime) {
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadSize);
		CompletionService<String> cs = new ExecutorCompletionService<String>(fixedThreadPool);
		for (String url : set) {
			final String url1 = url;
			cs.submit(new Callable<String>() {
				public String call() throws Exception {
					try {
						Thread.sleep(threadSleepTime);
					} catch (InterruptedException e) {
						logger.info("线程异常");
					}
					return updatePageUrlDetailByLocal(url)+ "--" + url1;
				}
			});
		}
		for (String url : set) {
			String url1 = "";
			try {
				String a = cs.take().get();
				url1 = a.split("--")[1];
				count++;
				if (a.split("--")[0].equals("0")) {
					logger.info("update PageUrlDetail error ");
					insertFailInfo(url1, "update PageUrlDetail error ");
				}
			} catch (Exception e) {
				logger.info("update PageUrlDetail error ");
				insertFailInfo(url1, "update PageUrlDetail error ");
			}
		}
		fixedThreadPool.shutdown();
		while(true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(count==set.size()) {
				logger.info("update PageUrlDetail over");
				//System.out.println("update PageUrlDetail over");
				set.clear();
			  break;
			  }
		}
		count=0;
	}
	
	public void updatePageUrlAgent(int threadSize, long threadSleepTime) {
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadSize);
		CompletionService<String> cs = new ExecutorCompletionService<String>(fixedThreadPool);
		for (String url : set) {
			final String url1 = url;
			cs.submit(new Callable<String>() {
				public String call() throws Exception {
					try {
						Thread.sleep(threadSleepTime);
					} catch (InterruptedException e) {
						logger.info("线程异常");
					}
					return updatePageUrlDetail(url)+ "--" + url1;
				}
			});
		}
		for (String url : set) {
			String url1 = "";
			try {
				String a = cs.take().get();
				url1 = a.split("--")[1];
				System.out.println(a);
				count++;
				if (a.split("--")[0].equals("0")) {
					logger.info("update PageUrlDetail error ");
					insertFailInfo(url1, "update PageUrlDetail error ");
				}
			} catch (Exception e) {
				logger.info("update PageUrlDetail error ");
				insertFailInfo(url1, "update PageUrlDetail error ");
			}
		}
		fixedThreadPool.shutdown();
		while(true) {
			if(count==set.size()) {
				logger.info("update PageUrlDetail over");
				//System.out.println("update PageUrlDetail over");
				set.clear();
			  break;
			  }else {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			}
		}
		count=0;
	}
	public int updatePageUrlDetail(String url) {
		LianJiaDO lianJiaDO = new LianJiaDO();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		SSLContext sslcontext = null;
		int statusCode = 0;
		String s = "";
		String proxyHost = "";
		try {
			sslcontext = createIgnoreVerifySSL();
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", new SSLConnectionSocketFactory(sslcontext)).build();
			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			HttpClients.custom().setConnectionManager(connManager);
			// 设置代理IP、端口、协议（请分别替换）
			// System.out.println(proxyHost.split("-")[1]+"
			// "+Integer.valueOf(proxyHost.split("-")[2])+" "+proxyHost.split("-")[5]);
			String json = redisCache.getString("IP");
			Set<String> setIpPool = (Set<String>) JsonUtil.toBean(json, set.getClass());
			if(set.size()<1) {
				if(ipCount>4) {
					ipCount=0;
					System.out.println("本地");
					return updatePageUrlDetailByLocal(url);
				}
				else {
					Lock lock = new ReentrantLock();
					lock.lock();
					testService.ipWork(ipUrl);
					ipCount++;
					lock.unlock();
				}
			}
			Random r = new Random();
			int count = r.nextInt(setIpPool.size()) + 1;
			Iterator<String> it = setIpPool.iterator();
			
			for (int i = 1; i <= count; i++) {
				proxyHost = it.next();
			}
			HttpHost proxy = new HttpHost(proxyHost.split("-")[0], Integer.valueOf(proxyHost.split("-")[1]),
					proxyHost.split("-")[2]);
			// 把代理设置到请求配置
			RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).setSocketTimeout(30000)
					.setConnectTimeout(30000).setConnectionRequestTimeout(30000).build();
			// 实例化CloseableHttpClient对象
			httpclient = HttpClients.custom().build();
			HttpGet httpget = new HttpGet(url);
			httpget.setConfig(defaultRequestConfig);
			List<String> list = new ArrayList<>();
			httpget.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
			response = httpclient.execute(proxy, httpget);
			if (null == response) {
				try {
					removeIP(proxyHost);
					response.close();
					return updatePageUrlDetail(url);
				} catch (IOException e) {
					e.printStackTrace();
					return 0;
				}
			}
			if(200!=response.getStatusLine().getStatusCode()) {
				return 0;
			}
			entity = response.getEntity();
			s = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(removeIP(proxyHost)+":12");
			//removeIP(proxyHost);
			return 0;
		}
			System.out.println(s);
			Document doc = Jsoup.parse(s);
			Page page = new Page();
			Html html = new Html(doc);
			page.setHtml(html);
			lianJiaDO.setUrl(url);
			lianJiaDO.setName(get(page, LianJiaCostant.LIANJIA_NAME));
			lianJiaDO.setAddress(get(page, LianJiaCostant.LIANJIA_ADDRESS));
			lianJiaDO.setDirType(get(page, LianJiaCostant.LIANJIA_DIRTYPR));
			lianJiaDO.setFloor(get(page, LianJiaCostant.LIANJIA_FLOOR));
			lianJiaDO.setSize(get(page, LianJiaCostant.LIANJIA_SIZE));
			lianJiaDO.setBuildTime(get(page, LianJiaCostant.LIANJIA_BUILDTIME));
			lianJiaDO.setUnitPrice(get(page, LianJiaCostant.LIANJIA_UNITPRICE));
			lianJiaDO.setTotalPrice(get(page, LianJiaCostant.LIANJIA_TOTALPRICE));
			lianJiaDO.setRoom(get(page, LianJiaCostant.LIANJIA_ROOM));
			lianJiaDO.setSpan(get(page, LianJiaCostant.LIANJIA_SPAN));
			if (url.contains("ershoufang")) {
				lianJiaDO.setType("二手房");
			} else {
				lianJiaDO.setType(get(page, "新房"));
			}
			lianJiaDO.setBase(get(page, LianJiaCostant.LIANJIA_BASE));
			lianJiaDO.setTradeBase(get(page, LianJiaCostant.LIANJIA_TRADEBASE));
			lianJiaDO.setCreateTime(DateUtil.formatTo_yyyyMMdd());
			lianJiaDO.setFirstArea(get(page, LianJiaCostant.LIANJIA_FIRSTAREA).replace("二手房", ""));
			lianJiaDO.setSecondArea(get(page, LianJiaCostant.LIANJIA_SETSECONDAREA).replace("二手房", ""));
			lianJiaDO.setHouseType(get(page, LianJiaCostant.LIANJIA_HOUSETYPE));
			lianJiaDO.setUpdateTime(get(page, LianJiaCostant.LIANJIA_UPDATETIME));
			int i =1;
					//lianJiaDao.update(lianJiaDO);
			System.out.println(i);
			System.out.println(lianJiaDO);
			return i;
		}
	

	public int updatePageUrlDetailByLocal(String url) {
		LianJiaDO lianJiaDO = new LianJiaDO();
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);  
	     CloseableHttpResponse response = null;  
	     HttpEntity entity = null;
	     String s="";
	     try {
	        httpget.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
	        response = httpclient.execute(httpget);
	        entity = response.getEntity(); 
	        s = EntityUtils.toString(entity, "UTF-8");
	        if(null==response) {
	        	return 0;
	        }
	        if(200!=response.getStatusLine().getStatusCode()) {
				return 0;
			}else {
			System.out.println(response.getStatusLine().getStatusCode());
		//	System.out.println(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} 
			//System.out.println();
			Document doc = Jsoup.parse(s);
			Page page = new Page();
			Html html = new Html(doc);
			page.setHtml(html);
			lianJiaDO.setUrl(url);
			if(null==get(page, LianJiaCostant.LIANJIA_NAME)||get(page, LianJiaCostant.LIANJIA_NAME).equals("")) {
				return 0;
			}
			lianJiaDO.setName(get(page, LianJiaCostant.LIANJIA_NAME));
			lianJiaDO.setAddress(get(page, LianJiaCostant.LIANJIA_ADDRESS));
			lianJiaDO.setDirType(get(page, LianJiaCostant.LIANJIA_DIRTYPR));
			lianJiaDO.setFloor(get(page, LianJiaCostant.LIANJIA_FLOOR));
			lianJiaDO.setSize(get(page, LianJiaCostant.LIANJIA_SIZE));
			lianJiaDO.setBuildTime(get(page, LianJiaCostant.LIANJIA_BUILDTIME));
			lianJiaDO.setUnitPrice(get(page, LianJiaCostant.LIANJIA_UNITPRICE));
			lianJiaDO.setTotalPrice(get(page, LianJiaCostant.LIANJIA_TOTALPRICE));
			lianJiaDO.setRoom(get(page, LianJiaCostant.LIANJIA_ROOM));
			lianJiaDO.setSpan(get(page, LianJiaCostant.LIANJIA_SPAN));
			if (url.contains("ershoufang")) {
				lianJiaDO.setType("二手房");
			} else {
				lianJiaDO.setType(get(page, "新房"));
			}
			lianJiaDO.setBase(get(page, LianJiaCostant.LIANJIA_BASE));
			lianJiaDO.setTradeBase(get(page, LianJiaCostant.LIANJIA_TRADEBASE));
			lianJiaDO.setCreateTime(DateUtil.formatTo_yyyyMMdd());
			lianJiaDO.setFirstArea(get(page, LianJiaCostant.LIANJIA_FIRSTAREA).replace("二手房", ""));
			lianJiaDO.setSecondArea(get(page, LianJiaCostant.LIANJIA_SETSECONDAREA).replace("二手房", ""));
			lianJiaDO.setHouseType(get(page, LianJiaCostant.LIANJIA_HOUSETYPE));
			lianJiaDO.setUpdateTime(get(page, LianJiaCostant.LIANJIA_UPDATETIME));
			int i = lianJiaDao.update(lianJiaDO);
			System.out.println(i);
			return i;
		}
	

	public int removeIP(String ip) {
		try {
			Set<String> set = new HashSet<>();
			Lock lock = new ReentrantLock();
			lock.lock();
			String json = redisCache.getString("IP");
			Set<String> set1 = (Set<String>) JsonUtil.toBean(json, set.getClass());
			set1.remove(ip);
			redisCache.setString("IP", JsonUtil.toJSONString(set1));
			lock.unlock();
		} catch (Exception e) {
			cachedThreadPool.execute(new Runnable() {
				public void run() {
					FailInfoDo failInfoDo = new FailInfoDo();
					failInfoDo.setUrl(ip);
					failInfoDo.setCreateTime(DateUtil.formatTo_yyyyMMdd());
					failInfoDo.setFailInfo("error");
					failInfoDao.insert(failInfoDo);
				}
			});
			return 0;
		}
		return 1;
	}
    
	
	
	public String get(Page page, String xpath) {
		if (null == xpath || xpath.equals("")) {
			return "";
		}
		String s = page.getHtml().xpath(xpath).all().toString().replace("[", "").replace("]", "").replace("\n", "")
				.replace("\r", "");
		return s;

	}

	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[] { trustManager }, null);
		return sc;
	}

	public int insertFailInfo(String url) {
		FailInfoDo failInfoDo = new FailInfoDo();
		failInfoDo.setUrl(url);
		failInfoDo.setCreateTime(DateUtil.formatTo_yyyyMMdd());
		failInfoDo.setFailInfo("error");
		return failInfoDao.insert(failInfoDo);
	}
	
	
	
	public int insertFailInfo(String url, String failInfo) {
		FailInfoDo failInfoDo = new FailInfoDo();
		failInfoDo.setUrl(url);
		failInfoDo.setCreateTime(DateUtil.formatTo_yyyyMMdd());
		failInfoDo.setFailInfo(failInfo);
		return failInfoDao.insert(failInfoDo);
	}

	@Override
	public void work(String url,int threadSize,long threadSleepTime,String startMode) {
		logger.info("=========普通模式=========");
		if(startMode.equals(StartModeEnum.COMMON_AUTO.getDesc())) {
			logger.info("选择普通全自动模式");
			try {
				getListPageUrl(url);
				initPageUrl(threadSize);
				updatePageUrl(threadSize, threadSleepTime);
			} catch (Exception e) {
				logger.info("#####选择普通全自动模式	异常");
			}
		}else if (startMode.equals(StartModeEnum.COMMON_LIST.getDesc())) {
			logger.info("选择普通模式获取列表页");
			try {
				getListPageUrl(url);
				initPageUrl(threadSize);
				set.clear();
			} catch (Exception e) {
				logger.info("#####选择普通模式获取列表页	异常");
			}
		}else if (startMode.equals(StartModeEnum.COMMON_DETAIL.getDesc())) {
			logger.info("选择普通模式获取详情页");
			try {
				List<String> urllist = getUrlList();
				for (String string : urllist) {
					set.add(string);
				}
				updatePageUrl(threadSize, threadSleepTime);
			} catch (Exception e) {
				logger.info("#####选择普通模式获取详情页	异常");
			}
		}else if (startMode.equals(StartModeEnum.COMMON_ERROR.getDesc())) {
			logger.info("选择普通模式异常url重爬 ");
			try {
				List<String> errorUrlList = getErrorUrlList();
				for (String string : errorUrlList) {
					set.add(string);
				}
				updatePageUrl(threadSize, threadSleepTime);
			} catch (Exception e) {
				logger.info("#####选择普通模式异常url重爬	异常");
			}
		}else {
			logger.info("选择模式失败");
		}
		logger.info("=========选择模式不存在=========");
	}
	
	@Override
	public void workUseIp(String url,int threadSize,long threadSleepTime,String startMode) {
		/*getListPageUrl(url);
		//System.out.println("amount："+set.size());
		initPageUrl();
		List<String> urllist=getUrllist();
		for (String string : urllist) {
			set.add(string);
		}*/
		//IpPool.add("36.27.142.84-15217-http");
		// redisCache.setString("IP", JsonUtil.toJSONString(IpPool));
		//System.out.println("amount："+set.size());
		// set.add("https://hz.lianjia.com/ershoufang/103102047983.html");
	//	updatePageUrlAgent(threadSize,threadSleepTime);
		logger.info("=========代理模式=========");
		if(startMode.equals(StartModeEnum.COMMON_AUTO.getDesc())) {
			logger.info("选择代理全自动模式");
			try {
				getListPageUrl(url);
				initPageUrl(threadSize);
				updatePageUrlAgent(threadSize, threadSleepTime);
			} catch (Exception e) {
				logger.info("#####选择代理全自动模式	异常");
			}
		}else if (startMode.equals(StartModeEnum.COMMON_LIST.getDesc())) {
			logger.info("选择代理模式获取列表页");
			try {
				getListPageUrl(url);
				initPageUrl(threadSize);
				set.clear();
			} catch (Exception e) {
				logger.info("#####选择代理模式获取列表页	异常");
			}
		}else if (startMode.equals(StartModeEnum.COMMON_DETAIL.getDesc())) {
			logger.info("选择代理模式获取详情页");
			try {
				List<String> urllist = getUrlList();
				for (String string : urllist) {
					set.add(string);
				}
				updatePageUrlAgent(threadSize, threadSleepTime);
			} catch (Exception e) {
				logger.info("#####选择代理模式获取详情页	异常");
			}
		}else if (startMode.equals(StartModeEnum.COMMON_ERROR.getDesc())) {
			logger.info("选择代理模式异常url重爬");
			try {
				List<String> errorUrlList = getErrorUrlList();
				for (String string : errorUrlList) {
					set.add(string);
				}
				updatePageUrlAgent(threadSize, threadSleepTime);
			} catch (Exception e) {
				logger.info("#####选择代理模式异常url重爬	异常");
			}
		}else {
			logger.info("选择模式不存在");
		}
		logger.info("=========代理模式结束=========");
		
		
	}
	@Override
	public  void webMagicWork(String url,int threadSize,long threadSleepTime) {
		logger.info("=========webmagic模式=========");
		logger.info("=========webmagic模式开启=========");
		flag=0;
		Spider spider=Spider.create(workProcesser).addUrl(url).thread(threadSize);
		spider.start();
		//callable future 来 读取spider状态 阻塞防止直接返回对异常进行处理
		ExecutorService threadPool = Executors.newSingleThreadExecutor();
		Future<Integer> future = threadPool.submit(new Callable<Integer>() {
			public Integer call() throws Exception {
				while (true) {
					try {
						Thread.sleep(2000);
						if (spider.getStatus().toString().equals("Stopped")) {
							flag = 1;
							logger.info("over");
							break;
						} else if (spider.getStatus().toString().equals("running")) {
							logger.info("doing");
							spider.close();
						} else {
							logger.info("doing");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return flag;
			}
		});
		try {
			Thread.sleep(5000);
			if (future.get() == 1) {
				flag=0;
			}
		} catch (InterruptedException e) {
			logger.info("webmagic 线程监控异常");
		} catch (ExecutionException e) {
			logger.info("webmagic 线程监控异常");
		}
		logger.info("=========webmagic模式结束=========");
	}
	
	private List<String> getUrlList() {
		LianJiaDO lianJiaDO=new LianJiaDO();
		lianJiaDO.setCreateTime(DateUtil.formatTo_yyyyMMdd());
		return lianJiaDao.selectUrllist(lianJiaDO);
	}
	
	private List<String> getErrorUrlList() {
		LianJiaDO lianJiaDO=new LianJiaDO();
		lianJiaDO.setCreateTime(DateUtil.formatTo_yyyyMMdd());
		List<String> errorUrlList1=lianJiaDao.selectErrorUrlList(lianJiaDO);
		List<String> errorUrlList2=new ArrayList<>();
		for (String string : errorUrlList1) {
			if(string.contains("https")&&!string.contains("pg")) {
				errorUrlList2.add(string);
			}
		}
		return errorUrlList2;
	}
	
	
	
}
