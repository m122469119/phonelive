/**
 * 
 */
package com.bolema.phonelive.utils;

import com.amap.api.location.AMapLocation;


public class AMapUtils {
	/**
	 *  开始定位
	 */
	public final static int MSG_LOCATION_START = 0;
	/**
	 * 定位完成
	 */
	public final static int MSG_LOCATION_FINISH = 1;
	/**
	 * 停止定位
	 */
	public final static int MSG_LOCATION_STOP= 2;
	
	public final static String KEY_URL = "URL";
	public final static String URL_H5LOCATION = "file:///android_asset/location.html";
	/**
	 * 根据定位结果返回定位信息的字符串
	 */
	public synchronized static String getLocationStr(AMapLocation location){
		if(null == location){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		//errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
		if(location.getErrorCode() == 0){
			sb.append("定位成功" + "\n");
			sb.append("定位类型: ").append(location.getLocationType()).append("\n");
			sb.append("经    度    : ").append(location.getLongitude()).append("\n");
			sb.append("纬    度    : ").append(location.getLatitude()).append("\n");
			sb.append("精    度    : ").append(location.getAccuracy()).append("米").append("\n");
			sb.append("提供者    : ").append(location.getProvider()).append("\n");
			
			if (location.getProvider().equalsIgnoreCase(
					android.location.LocationManager.GPS_PROVIDER)) {
				// 以下信息只有提供者是GPS时才会有
				sb.append("速    度    : ").append(location.getSpeed()).append("米/秒").append("\n");
				sb.append("角    度    : ").append(location.getBearing()).append("\n");
				// 获取当前提供定位服务的卫星个数
				sb.append("星    数    : ").append(location.getSatellites()).append("\n");
			} else {
				// 提供者是GPS时是没有以下信息的
				sb.append("国    家    : ").append(location.getCountry()).append("\n");
				sb.append("省            : ").append(location.getProvince()).append("\n");
				sb.append("市            : ").append(location.getCity()).append("\n");
				sb.append("城市编码 : ").append(location.getCityCode()).append("\n");
				sb.append("区            : ").append(location.getDistrict()).append("\n");
				sb.append("区域 码   : ").append(location.getAdCode()).append("\n");
				sb.append("地    址    : ").append(location.getAddress()).append("\n");
				sb.append("兴趣点    : ").append(location.getPoiName()).append("\n");
			}
		} else {
			//定位失败
			sb.append("定位失败" + "\n");
			sb.append("错误码:").append(location.getErrorCode()).append("\n");
			sb.append("错误信息:").append(location.getErrorInfo()).append("\n");
			sb.append("错误描述:").append(location.getLocationDetail()).append("\n");
		}
		return sb.toString();
	}
}
