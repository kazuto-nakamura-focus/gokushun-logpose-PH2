package com.logpose.ph2.api.bulk.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.bulk.domain.BaseDataGenerator;
import com.logpose.ph2.api.bulk.domain.BaseDataGeneratorModules;
import com.logpose.ph2.api.bulk.domain.DataListModel;
import com.logpose.ph2.api.bulk.dto.SensorDataDTO;
import com.logpose.ph2.batch.dao.db.entity.MessagesEnyity;
import com.logpose.ph2.batch.dao.db.entity.Ph2DashBoardEntityExample;
import com.logpose.ph2.batch.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.batch.dao.db.entity.Ph2RelBaseDataEntityExample;
import com.logpose.ph2.batch.dao.db.mappers.MessagesMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2BaseDataMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2DashBoardMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2DevicesMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2InsolationDataMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2RelBaseDataMapper;
import com.logpose.ph2.batch.dao.db.mappers.joined.Ph2JoinedMapper;

@Service
public class S1DeviceDataLoaderService
	{
	@Autowired
	private Ph2DevicesMapper ph2DeviceMapper;
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	private MessagesMapper messagesMapper;
	@Autowired
	private BaseDataGeneratorModules modules;
	@Autowired
	private BaseDataGenerator baseDataGenerator;
	@Autowired
	Ph2RelBaseDataMapper ph2RelBaseDataMapper;
	@Autowired
	Ph2DashBoardMapper dashboardMapper;
	@Autowired
	Ph2BaseDataMapper ph2BaseDataMapper;
	@Autowired
	Ph2InsolationDataMapper ph2InsolationDataMapper;
	@Autowired
	Ph2JoinedMapper ph2JoinedMapper;
	
	public Ph2DevicesEnyity getDeviceInfo(Long deviceId)
		{
		return this.ph2DeviceMapper.selectByPrimaryKey(deviceId);
		}
	
	public Date getLatest(Long deviceId)
		{
		Date latest = this.ph2RelBaseDataMapper.selectLatest(deviceId);
		latest.setTime(latest.getTime()+1);
		return latest;
		}
	public List<Ph2DevicesEnyity> getDeviceAllInfo()
		{
		return this.ph2DeviceMapper.selectAll();
		}

	@Transactional(rollbackFor = Exception.class)
	public Date loadMessages(Ph2DevicesEnyity device, Date startDate) throws IOException
		{
		Date firstDate = null;
		Date lastDate = null;
// * メッセージからデータを取得する
		List<SensorDataDTO> records = null;
		try (Cursor<MessagesEnyity> messageCorsor = this.messagesMapper
				.selectByCastedAt(device.getId(), device.getTz(), startDate))
			{
			Iterator<MessagesEnyity> messages = messageCorsor.iterator();
			DataListModel messageData = new DataListModel();
			boolean isFirst = true;
			while (messages.hasNext())
				{
				MessagesEnyity message = messages.next();
				if(isFirst)
					{
					records = this.ph2JoinedMapper.getSensorData(device.getId());
					firstDate = message.getCastedAt();
					this.deleteTables(device.getId(), firstDate);
					isFirst = false;
					}
				else
					{
					lastDate = message.getCastedAt();
					}
				messageData = this.createTables(device, records, messageData, message);
				}
			}
		catch (Exception e)
			{
			throw e;
			}
		if((firstDate != null) &&(lastDate != null) )
			{
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(firstDate);
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(lastDate);
			if(startCal.get(Calendar.DATE) != endCal.get(Calendar.DATE))
				{
				return firstDate;
				}
			}
		return null;
		}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	private DataListModel createTables(
			Ph2DevicesEnyity device,
			List<SensorDataDTO> records,
			DataListModel dataListModel,
			MessagesEnyity message)
		{
// * レコード内のrawデータを３文字づつ抽出し、文字リストを作成する。
			List<String> data = splitByLength(message.getRaw(), 3);
// * データリストの設定を行う
			dataListModel = this.modules.setData(message.getCastedAt(), data, dataListModel);
// * データリストのカウントが16に達した時、BAT-BDC-02基礎データ作成処理#計算処理
// * とDBへの登録 を実行する。
			if (dataListModel.getCount() == 16)
				{
				this.baseDataGenerator.generate(message.getDeviceId(), records, dataListModel);
				}
	
		return dataListModel;
		}

	// ===============================================
	// プライベート関数群
	// ===============================================
	private List<String> splitByLength(String str, int length)
		{
		List<String> strs = new ArrayList<>();
		for (int i = 0; i < StringUtils.length(str); i += length)
			{
			strs.add(StringUtils.substring(str, i, i + length));
			}
		return strs;
		}
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	private void deleteTables(long deviceId, Date date)
		{
		Ph2RelBaseDataEntityExample relexm = new Ph2RelBaseDataEntityExample();
		relexm.createCriteria().andCastedAtGreaterThanOrEqualTo(date)
				.andDeviceIdEqualTo(deviceId);
		this.ph2RelBaseDataMapper.deleteByExample(relexm);
		// * 過去のダッシュボードの受信時刻移行のデータ削除
		Ph2DashBoardEntityExample exm = new Ph2DashBoardEntityExample();
		exm.createCriteria().andCastedAtGreaterThanOrEqualTo(date).andDeviceIdEqualTo(deviceId);
		this.dashboardMapper.deleteByExample(exm);
		
	/*	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		short year = (short) cal.get(Calendar.YEAR);
		Ph2DeviceDayEntityExample dexm = new Ph2DeviceDayEntityExample();
		dexm.createCriteria().andYearGreaterThanOrEqualTo(year);
		this.ph2DeviceDayMapper.deleteByExample(dexm);*/
		}
	}
