package com.logpose.ph2.api.bulk.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.bulk.domain.DeviceLogDomain;
import com.logpose.ph2.api.configration.DefaultWeatherlAPIParameters;
import com.logpose.ph2.api.dao.api.FreeWeatherAPI;
import com.logpose.ph2.api.dao.api.entity.FreeWeatherRequest;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2HeadLinesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2HeadLinesEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RawDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherForecastEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherForecastEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2FieldsMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2HeadLinesMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RawDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2WeatherForecastMapper;
import com.logpose.ph2.api.exception.APIException;

@Service
public class S4HeadLineLoaderService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(S4HeadLineLoaderService.class);
	@Autowired
	private DeviceLogDomain deviceLogDomain;
	@Autowired
	private DefaultWeatherlAPIParameters param;
	@Autowired
	private Ph2RawDataMapper rawDataMapper;
	@Autowired
	private Ph2HeadLinesMapper ph2HeadLinesMapper;
	@Autowired
	private Ph2FieldsMapper ph2FieldsMapper;
	@Autowired
	private Ph2WeatherForecastMapper ph2WeatherForecastMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	@Transactional(rollbackFor = Exception.class)
	public void createHealines(Ph2DevicesEntity device, Date lastTime, boolean isAll) throws APIException
		{
		long deviceId = device.getId();
		final String logTime = this.deviceLogDomain.date(lastTime, "不明時刻");
		this.deviceLogDomain.log(LOG, device, getClass(), "最新の更新日時" + logTime + "からのデータを取得します。", isAll);
// * RawDataから最後の更新日付のデータを取得する
		List<Ph2RawDataEntity> entities = this.rawDataMapper.selectByDevice(deviceId, lastTime);

// * ヘッドラインデータを作成する
		Ph2HeadLinesEntityExample hdex = new Ph2HeadLinesEntityExample();
		hdex.createCriteria().andDeviceIdEqualTo(deviceId);
		this.ph2HeadLinesMapper.deleteByExample(hdex);

		for (Ph2RawDataEntity entity : entities)
			{
			Ph2HeadLinesEntity headline = new Ph2HeadLinesEntity();
			headline.setDeviceId(deviceId);
			headline.setRawDataId(entity.getId());
			this.ph2HeadLinesMapper.insert(headline);
			}

// * 本日の場合、天気データを更新する

		// *テーブルの削除
		Ph2WeatherForecastEntityExample exm = new Ph2WeatherForecastEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(device.getId());
		this.ph2WeatherForecastMapper.deleteByExample(exm);

		// * 天気情報の更新
		Calendar cal = Calendar.getInstance();
		long diff = cal.getTimeInMillis() - lastTime.getTime();
		if (4200000 > diff)
			{
			this.deviceLogDomain.log(LOG, device, getClass(), "天気情報の更新を実行します。", isAll);

			FreeWeatherAPI api = new FreeWeatherAPI();
			// リクエストの作成
			FreeWeatherRequest request = new FreeWeatherRequest();
			// フィールド情報の取得
			Ph2FieldsEntity field = this.ph2FieldsMapper.selectByPrimaryKey(device.getFieldId());
			request.setLatitude(field.getLatitude().toString());
			request.setLongitude(field.getLongitude().toString());
			request.setUrl(param.getCurrentUrl());
			request.setKey("355d4dae284c491da0825008240702");
			// 天気コードの取得
			try
				{
				List<Ph2WeatherForecastEntity> res = api.getForcastEntities(deviceId, request);
				for (final Ph2WeatherForecastEntity item : res)
					{
					this.ph2WeatherForecastMapper.insert(item);
					}
				this.deviceLogDomain.log(LOG, device, getClass(), "天気情報の更新が完了しました。", isAll);
				}
			catch(APIException e)
				{
				this.deviceLogDomain.log(LOG, device, getClass(), e.getCauseText(), isAll);
				this.deviceLogDomain.log(LOG, device, getClass(), "天気情報の更新に失敗しました。", isAll);
				}
			catch (Exception e)
				{
				this.deviceLogDomain.log(LOG, device, getClass(), e.getMessage(), isAll);
				this.deviceLogDomain.log(LOG, device, getClass(), "天気情報の更新に失敗しました。", isAll);
				throw e;
				}
			finally
				{
				List<String> urls = api.getUrlList();
				if (urls.size() > 0)
					{
					this.deviceLogDomain.log(LOG, device, getClass(), "以下のAPIが実行されています。", isAll);
					for (String url : urls)
						{
						this.deviceLogDomain.log(LOG, device, getClass(), url, isAll);
						}
					}
				}
			}
		else
			{
			this.deviceLogDomain.log(LOG, device, getClass(), "天気情報の更新はありませんでした。", isAll);
			}
		}
	}
