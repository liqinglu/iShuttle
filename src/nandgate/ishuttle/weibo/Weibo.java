package nandgate.ishuttle.weibo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.os.Message;
import android.widget.Toast;

public class Weibo {
		
	static weiboPoster ctx;
	Weibo(weiboPoster mContext){
		ctx=mContext;
	}
		public static String getMethodRequest(String url, Map<String, String> params, Map<String, String> header){
			System.out.println("get request is begin! url =" + url);
			HttpClient hc = new HttpClient();
			try {
				StringBuilder sb = new StringBuilder(url);
				if(params != null){
					for (String param_key : params.keySet()) {
						if(param_key == null || params.get(param_key) == null)
							continue;
						sb.append("&").append(param_key).append("=").append(params.get(param_key));
					}
				}
				GetMethod pm = new GetMethod(sb.toString());
				if(header != null){
					for (String head_key : header.keySet()) {
						if(head_key == null || header.get(head_key) == null)
							continue;
						pm.addRequestHeader(head_key, header.get(head_key));
					}
				}
				pm.getParams().setContentCharset("utf8");
				hc.executeMethod(pm);
				String ret = pm.getResponseBodyAsString();
				System.err.println(ret);
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				System.out.println("get request is end! url =" + url);
			}
			return "";
		}
		
		public static String postMethodRequestWithFile(String url, Map<String, String> params, Map<String, String> header, Map<String, byte[]> itemsMap){
			System.out.println("post request is begin! url =" + url);
			HttpClient hc = new HttpClient();
			try {
				PostMethod pm = new PostMethod(url);
				if(header != null){
					for (String head_key : header.keySet()) {
						if(head_key == null || header.get(head_key) == null)
							continue;
						pm.addRequestHeader(head_key, header.get(head_key));
					}
				}
				int part_size = 1;
				if(params != null)
					part_size = params.size();
				if(itemsMap != null)
					part_size = part_size + itemsMap.size();
				Part[] parts = new Part[part_size]; 
				int index = 0;
				if(itemsMap != null){
					for (String item_name : itemsMap.keySet()) {
						if(itemsMap.get(item_name) == null)
							continue;
						parts[index] = new FilePart(item_name, new ByteArrayPartSource(item_name, itemsMap.get(item_name)), "multipart/form-data;", "utf-8");
						index ++ ;
					}
				}
				
				if(params != null){
					for (String param_key : params.keySet()) {
						if(param_key == null|| params.get(param_key) == null)
							continue;
						parts[index] = new StringPart(param_key, params.get(param_key), "utf-8");
						index ++ ;
					}
				}
				pm.setRequestEntity(new MultipartRequestEntity(parts, pm.getParams())); 
				pm.getParams().setContentCharset("utf8");
				hc.executeMethod(pm);
				String ret = pm.getResponseBodyAsString();
				System.err.println(ret);
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				System.out.println("post request is end! url =" + url);
			}
			return "";
		}
		
		public static String postMethodRequestWithOutFile(String url, Map<String, String> params, Map<String, String> header){
			System.out.println("post request is begin! url =" + url);
			HttpClient hc = new HttpClient();
			try {
				PostMethod pm = new PostMethod(url);
				if(header != null){
					for (String head_key : header.keySet()) {
						if(head_key == null || header.get(head_key) == null)
							continue;
						pm.addRequestHeader(head_key, header.get(head_key));
					}
				}
				if(params != null){
					for (String param_key : params.keySet()) {
						if(param_key == null || params.get(param_key) == null)
							continue;
						pm.addParameter(param_key, params.get(param_key));
					}
				}
				pm.getParams().setContentCharset("utf8");
				
				final HttpClient hcTmp=hc;
				final PostMethod pmTmp=pm;
				final String rst="";
				new Thread(){

					@Override
					public void run() {
						Message message = new Message();
						try {
							hcTmp.executeMethod(pmTmp);
							message.what=pmTmp.getStatusLine().getStatusCode();
						} catch (HttpException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						weiboPoster.handler.sendMessage(message);
						super.run();
					}
					
				}.start();
				
				return rst;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				System.out.println("post request is end! url =" + url);
			}
			return "";
		}
		
		public static byte[] readFromURL(String url) {
			HttpClient httpClient = new HttpClient();
			GetMethod getMethod = new GetMethod(url);
			// 加入同步避免被防盗链机制屏蔽而取不到内容
			Header h = new Header("referer", "hupan.com");
			getMethod.setRequestHeader(h);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			try {
				// 执行getMethod
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode != HttpStatus.SC_OK) {
					return new byte[] {};
				}
				// 读取内容
				byte[] responseBody = getMethod.getResponseBody();
				// 处理内容
				return responseBody;
			} catch (Exception e) {
				System.out.println("读取url失败：url->" + url + ":" + e);
				return new byte[] {};
			} finally {
				// 释放连接
				getMethod.releaseConnection();
			}
		}
		
		/*public static void main(String[] args){
			Map<String, String> params = new HashMap<String, String>();
			String status = "测试微博!!!";
			params.put("access_token", AccessTokenKeeper.readAccessToken(ctx).getToken());
			System.out.println(AccessTokenKeeper.readAccessToken(ctx).getToken());
			params.put("status", status + Math.random());
			params.put("lat", "22.631163467669495");
			params.put("long", "114.04709815979004");
			Weibo.postMethodRequestWithOutFile(POST_WEIBO_URL_WITH_CONTENT, params, header);
			Map<String, byte[]> itemsMap = new HashMap<String, byte[]>();
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			params.put("status", status + Math.random());
			itemsMap.put("pic", readFromURL("http://ww1.sinaimg.cn/mw600/7973a285gw1dqgmdbzjbkj.jpg"));
			Weibo.postMethodRequestWithFile(POST_WEIBO_URL_WITH_IMAGE, params, header, itemsMap);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			params.put("status", status + Math.random());
			params.put("url", "http://ww1.sinaimg.cn/mw600/7973a285gw1dqgmdbzjbkj.jpg");
			//HttpUtil.postMethodRequestWithOutFile(POST_WEIBO_URL_WITH_CONTENT_URL, params, header);
		}*/
		
		/**
		 * 采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。<br>
		 */
		public static final String SINA_SOURCE = "2484956621";

		/**
		 * sina api 请求的参数<br>
		 */
		public static Map<String, String> header = new HashMap<String, String>();
		
		static{
			header.put("Accept-Language", "zh-CN,zh;q=0.8");
			header.put("User-Agent", "test sina api");
			header.put("Accept-Charset", "utf-8;q=0.7,*;q=0.3");
		}
		
		/**
		 * 
		 * 发布微博 请求方式 POST
		 * source		false	string	采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。<br>
		 * access_token	false	string	采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。<br>
		 * status		true	string	要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。<br>
		 * url			false	string	图片的URL地址，必须以http开头。<br>
		 * lat			false	float	纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。<br>
		 * long			false	float	经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。<br>
		 * annotations	false	string	元数据，主要是为了方便第三方应用记录一些适合于自己使用的信息，每条微博可以包含一个或者多个元数据，<br>
		 * 									必须以json字串的形式提交，字串长度不超过512个字符，具体内容可以自定。<br>
		 */
		//public static final String POST_WEIBO_URL_WITH_CONTENT_URL = "https://api.weibo.com/2/statuses/upload_url_text.json?";
		
		/**
		 * 
		 * 发布微博 请求方式 POST
		 * source		false	string	采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。<br>
		 * access_token	false	string	采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。<br>
		 * status		true	string	要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。<br>
		 * lat			false	float	纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。<br>
		 * long			false	float	经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。<br>
		 * annotations	false	string	元数据，主要是为了方便第三方应用记录一些适合于自己使用的信息，每条微博可以包含一个或者多个元数据，<br>
		 * 									必须以json字串的形式提交，字串长度不超过512个字符，具体内容可以自定。<br>
		 */
		public static final String POST_WEIBO_URL_WITH_CONTENT = "https://api.weibo.com/2/statuses/update.json?";
		
		/**
		 * 
		 * 发布微博 请求方式 POST
		 * source		false	string	采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。<br>
		 * access_token	false	string	采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。<br>
		 * status		true	string	要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。<br>
		 * pic			false	string	图片要上传的图片，仅支持JPEG、GIF、PNG格式，图片大小小于5M。。<br>
		 * lat			false	float	纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。<br>
		 * long			false	float	经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。<br>
		 * annotations	false	string	元数据，主要是为了方便第三方应用记录一些适合于自己使用的信息，每条微博可以包含一个或者多个元数据，<br>
		 * 									必须以json字串的形式提交，字串长度不超过512个字符，具体内容可以自定。<br>
		 */
		public static final String POST_WEIBO_URL_WITH_IMAGE = "https://api.weibo.com/2/statuses/upload.json?";

}