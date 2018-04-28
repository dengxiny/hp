package com.hp.service.impl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import com.hp.common.DateUtil;
import com.hp.common.JsonUtil;
import com.hp.common.LianJiaCostant;
import com.hp.dao.LianJiaDao;
import com.hp.dao.TestDao;
import com.hp.dao.model.LianJiaDO;
import com.hp.dao.model.TestDO;
import com.hp.redis.RedisCache;
import com.hp.redis.RedisLock;
import com.hp.service.TestService;

import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;







@Service
//@SpringBootApplication(scanBasePackages = {"com.hp"})  
public class TestServiceImpl implements TestService{
	public static Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);
	@Autowired
	TestDao testdao;
	
	@Autowired
	LianJiaDao lianJiaDao;
	
	@Autowired
    public RedisCache redisCache;
	
	public static int count=0;
	
	
	public static Set<String> IpPool=new HashSet<>(); 
	/*@Autowired
	public RedisLock redisLock;*/
	
	@Override
	public void f1() {
		TestDO testDO=new TestDO();
		//List<String> list=testdao.update();
		/*for (String string : list) {
			System.out.println(string);
		}*/
	/*	String url="http://www.xicidaili.com/";
		List<String> list=getIP(url);
		f1(list, "https://hz.fang.lianjia.com/loupan");*/
		Set<String> set=new HashSet<>();
		for (int i = 0; i < 10; i++) {
			Date date=new Date();
			set.add(date.getTime()+"");
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println(JsonUtil.toJSONString(set));
		redisCache.setString("IP", JsonUtil.toJSONString(set));
		logger.info("#####【】:Test");
		for (int i = 0; i < 200; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Lock lock=new ReentrantLock(); 
					lock.lock();
					String json=redisCache.getString("IP");
					Set<String> set1=(Set<String>) JsonUtil.toBean(json,set.getClass());
				//	set1.add("5555");
					//System.out.println("123");
					redisCache.setString("IP", JsonUtil.toJSONString(set1));
					System.out.println("第一个"+json);
					lock.unlock();
				}
			}).start();
			}
		for (int i = 0; i < 200; i++) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json=redisCache.getString("IP");
				/*Set<String> set2=(Set<String>) JsonUtil.toBean(json,set.getClass());
				System.out.println(Thread.currentThread().getName());
				for (String string : set2) {
					System.out.println(Thread.currentThread().getName()+"  "+string);
				}*/
				System.out.println(json);
			}
		}).start();
		}
		
		//System.out.println(redisCache.getString("IP"));
		
		/*Lock lock=new ReentrantLock(); 
		lock.lock();
		lock.unlock();*/
		System.out.println("Test");
	}
	public  List<String> getIP(String url) {
		DefaultHttpClient httpclient = new DefaultHttpClient(new PoolingClientConnectionManager());
		CloseableHttpResponse response = null; 
		List<String> list=new ArrayList<>();
		HttpGet httpget = new HttpGet(url);  
	    HttpEntity entity = null;
	     String s="";
	     try {
	           httpget.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
	           httpget.addHeader("Host","www.xicidaili.com");
	           response = httpclient.execute(httpget);
	           entity = response.getEntity(); 
	           s = EntityUtils.toString(entity, "UTF-8");
	           System.out.println(response.getStatusLine().getStatusCode());
	           httpclient.close();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally {
	        Document doc= Jsoup.parse(s);
	        Elements links = doc.getElementsByTag("td");
	        String webs=null;
	        String text="";
	       for (int i = 1; i < links.size(); i++) {
	        	Element link=links.get(i);
	        	String linkText = link.text();
				if(i%8==0&&i!=0) {
					text+="-"+linkText;
					if(!text.split("-")[5].substring(0, 1).equals("s")) {
						list.add(text);
						}
					text="";
				}else {
					text+="-"+linkText;
				}
	          
			}
	    }
		return list;
	}
	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {  
	    SSLContext sc = SSLContext.getInstance("SSLv3");  
	  
	    // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法  
	    X509TrustManager trustManager = new X509TrustManager() {  
	        @Override  
	        public void checkClientTrusted(  
	                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,  
	                String paramString) throws CertificateException {  
	        }  
	  
	        @Override  
	        public void checkServerTrusted(  
	                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,  
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
	public  void telnetIP(List<String> proxyIpMap, String url) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        CompletionService<String> cs = new ExecutorCompletionService<String>(threadPool);
        for (String proxyHost : proxyIpMap) {
            final String url1 = url;
            final String proxyHost1 = proxyHost;
            cs.submit(new Callable<String>() {
                public String call() throws Exception {
                    return f2(proxyHost1,url1);
                }
            });
        }
       for (String proxyHost : proxyIpMap) {
            try {
            	String a=cs.take().get();
            	count++;
            	if(a.split("-")[8].equals("ok")) {
            	//	i++;
                System.out.println("available"+a);
                IpPool.add(a.split("-")[1]+"-"+a.split("-")[2]+"-"+a.split("-")[5]);
               // redisCache.setString("IP", JsonUtil.toJSONString(IpPool));
                //redisCache.listPushHead("IP", a);
            	}
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
      }
       threadPool.shutdown();
     
    }
	public  String f2 (String proxyHost, String url) {
   	 CloseableHttpResponse response = null;  
        int statusCode = 0;
        try {
        	 SSLContext sslcontext = createIgnoreVerifySSL();  
 			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
 					.register("http", PlainConnectionSocketFactory.INSTANCE)  
 					.register("https", new SSLConnectionSocketFactory(sslcontext))  
 					.build();  
 			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
 			HttpClients.custom().setConnectionManager(connManager);  
        	//设置代理IP、端口、协议（请分别替换）
        	//System.out.println(proxyHost.split("-")[1]+" "+Integer.valueOf(proxyHost.split("-")[2])+" "+proxyHost.split("-")[5]);
            HttpHost proxy = new HttpHost(proxyHost.split("-")[1],Integer.valueOf(proxyHost.split("-")[2]),proxyHost.split("-")[5]);

            //把代理设置到请求配置
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setProxy(proxy).setSocketTimeout(3000).setConnectTimeout(3000).setConnectionRequestTimeout(3000)  
                    .build();

            //实例化CloseableHttpClient对象
            CloseableHttpClient httpclient = HttpClients.custom().
            		//setDefaultRequestConfig(defaultRequestConfig).
            		build();
        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(defaultRequestConfig);
        response = httpclient.execute(proxy,httpget);
        } catch (Exception e) {
       	 return proxyHost+"is null";
        }
        if(null!=response) {
       	 if(response.getStatusLine().getStatusCode() != 200) {
        	//System.out.println(proxyHost +"-"+response.getStatusLine().getStatusCode());
        	return proxyHost+"error"+response.getStatusLine().getStatusCode();
       	 }
       	 else{
         	//System.out.println(proxyHost +" ok");
         //	System.out.println();
         	return	proxyHost +"ok";
       	 }
        
        }
		return proxyHost+"is null";
   }
	public void testUrl(String url) {
		LianJiaDO lianJiaDO=new LianJiaDO();
		DefaultHttpClient httpclient = new DefaultHttpClient(new PoolingClientConnectionManager());
		CloseableHttpResponse response = null; 
		List<String> list=new ArrayList<>();
		HttpGet httpget = new HttpGet(url);  
	    HttpEntity entity = null;
	     String s="";
	     try {
	           httpget.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
	           response = httpclient.execute(httpget);
	           entity = response.getEntity(); 
	           s = EntityUtils.toString(entity, "UTF-8");
	           System.out.println(response.getStatusLine().getStatusCode());
	           httpclient.close();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally {
	    	  Document doc= Jsoup.parse(s);
	    	  Page page=new Page();
		       Html html=new Html(doc);
		       page.setHtml(html);
		       lianJiaDO.setUrl(url);
		       lianJiaDO.setName(get(page, LianJiaCostant.LIANJIA_NAME));
		       lianJiaDO.setAddress(get(page, LianJiaCostant.LIANJIA_ADDRESS));
		       lianJiaDO.setDirType(get(page, LianJiaCostant.LIANJIA_DIRTYPR));
		       lianJiaDO.setFloor(get(page, LianJiaCostant.LIANJIA_FLOOR));
		       String pagesize=get(page, LianJiaCostant.LIANJIA_SIZE);
				if(null!=pagesize&&!pagesize.equals("")&&pagesize.contains("平米")) {
					Float size=Float.valueOf(pagesize.replace("平米", ""));
					lianJiaDO.setSize(size);
				}else {
					Float size=(float) 0;
					lianJiaDO.setSize(size);
				}
				lianJiaDO.setBuildTime(get(page, LianJiaCostant.LIANJIA_BUILDTIME));
				
				String UNITPRICE=get(page, LianJiaCostant.LIANJIA_UNITPRICE);
				if(null!=UNITPRICE&&!UNITPRICE.equals("")&&UNITPRICE.contains("平米")) {
					Float unitPrice=Float.valueOf(UNITPRICE.replace("/平米", ""));
					lianJiaDO.setUnitPrice(unitPrice);
				}else {
					Float unitPrice=(float) 0;
				lianJiaDO.setUnitPrice(unitPrice);
				}
				if(null!=get(page, LianJiaCostant.LIANJIA_TOTALPRICE)&&!get(page, LianJiaCostant.LIANJIA_TOTALPRICE).equals("")) {
					Float unitPrice=Float.valueOf(get(page, LianJiaCostant.LIANJIA_TOTALPRICE));
					lianJiaDO.setTotalPrice(unitPrice);
				}
				else {
					Float unitPrice=(float) 0;
					lianJiaDO.setTotalPrice(unitPrice);
				}
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
		      int i= lianJiaDao.update(lianJiaDO);
		      System.out.println(i);
		      System.out.println(lianJiaDO.toString());
	}
	}
	public String get(Page page,String xpath) {
		if(null==xpath||xpath.equals("")) {
			return "";
		}
		String s=page.getHtml().xpath(xpath).all().toString().replace("[", "").replace("]", "").replace("\n", "").replace("\r", "");
		return s;
		
	}
	public void testUrl2(String url) {
		LianJiaDO lianJiaDO=new LianJiaDO();
		lianJiaDO.setUrl(url);
		lianJiaDO.setCreateTime("20180117");
		lianJiaDao.insert(lianJiaDO);
	}
	public void executeIpPool(int len){
		while(true) {
			if(count==len) {
			  redisCache.setString("IP", JsonUtil.toJSONString(IpPool));
			  break;
			  }else {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
		}
	/*	String json=redisCache.getString("IP");
		
		System.out.println(json);*/
	}
	@Override
	public void ipWork(String ipUrl) {
		String url="http://www.xicidaili.com/";
		List<String> list=getIP(url);
		//测试代理可用性
		telnetIP(list, ipUrl);
		int len=list.size();
		//生成代理池
		executeIpPool(len);
		
	}
}
