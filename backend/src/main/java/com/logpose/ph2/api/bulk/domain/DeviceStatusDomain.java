package com.logpose.ph2.api.bulk.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DevicesMapper;

/**
 *  デバイスデータのステータスを設定する
 */
@Component
public class DeviceStatusDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static final String ONLOAD_ERROR = "関連デバイスが更新中です。しばらくしたら試してください。";
	private static final int ALL_LOAD_NEEDED = 128;
	private static final int ON_LOADING = 1;
	private static final int DATA_INITIALIZED = 2 | ON_LOADING;
	private static final int RAW_DATA_LOADED = 4 | DATA_INITIALIZED;
	private static final int MODEL_DATA_CREATED = 8 | RAW_DATA_LOADED;
	@Autowired
	private Ph2DevicesMapper ph2DevicesMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイスリストを取得する
	 * @return List<Ph2DevicesEnyity>
	 */
	// --------------------------------------------------
	public List<Ph2DevicesEnyity> selectAll()
		{
		return this.ph2DevicesMapper.selectAll(null);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスが全データロードかどうかチェックする
	 * @return boolean
	 */
	// --------------------------------------------------
	public boolean isAll(Ph2DevicesEnyity device)
		{
		return (device.getDataStatus() & ALL_LOAD_NEEDED) > 0;
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対してローディング中であることを設定する
	 * @param device
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public boolean setDataOnLoad(Ph2DevicesEnyity device)
		{
// * 最新の情報を得る
		Ph2DevicesEnyity entity = this.ph2DevicesMapper.selectByPrimaryKey(device.getId());
// * データをロードモードにする
		Integer status = entity.getDataStatus();
		if (null == status) status = 0;
		if ((status.intValue() & ON_LOADING) == 0)
			{
			// 最新の情報に更新
			if((status.intValue() & ALL_LOAD_NEEDED)>0)
				{
				device.setDataStatus((ALL_LOAD_NEEDED | ON_LOADING));
				}
			else device.setDataStatus(ON_LOADING);
			// DBへ更新
			this.update(device, false);
			return true;
			}
		else
			return false;
		}
	
	// --------------------------------------------------
	/**
	 * 指定されたデバイスのローディング中ステータスを解消する
	 * @param device
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void setDataNotLoading(Ph2DevicesEnyity device)
		{
		int status = device.getDataStatus();
		status = (status | ON_LOADING) - ON_LOADING;
		device.setDataStatus(status);
		this.update(device, false);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対して初期化済みであることを設定する
	 * @param device
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void setDataInitialized(Ph2DevicesEnyity device)
		{
		this.setDataStatus(device, DATA_INITIALIZED, true);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対して生データ済みであることを設定する
	 * @param device
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void setRawDataLoaded(Ph2DevicesEnyity device)
		{
		this.setDataStatus(device, RAW_DATA_LOADED, true);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対してモデルデータが作成済であることを通知する
	 * @param device
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void setModelDataCreated(Ph2DevicesEnyity device)
		{
		this.setDataStatus(device, MODEL_DATA_CREATED, true);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対して更新バッチ時全データを更新することを通知
	 * @param device
	 * @return ロックされたデバイスのIDリスト
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public List<Long> lockDevices(Ph2DevicesEnyity device)
		{
		List<Long> rockList = new ArrayList<>();
// * デバイスに対してロックを試みる
		if (!this.setDataOnLoad(device))
			{
			new RuntimeException(ONLOAD_ERROR);
			}
// * ロックリストに追加する
		rockList.add(device.getId());
// * 引継ぎ元のデバイスがあれば、ロックを掛ける
		if (null != device.getPreviousDeviceId())
			{
			Ph2DevicesEnyity tmp = this.ph2DevicesMapper.selectByPrimaryKey(device.getPreviousDeviceId());
			if( null != tmp)
				{
				tmp.setId(device.getPreviousDeviceId());
				if (!this.setDataOnLoad(tmp))
					{
					new RuntimeException(ONLOAD_ERROR);
					}
				rockList.add(device.getPreviousDeviceId());
				}
			else
				{
				device.setPreviousDeviceId(null);
				this.ph2DevicesMapper.updateByPrimaryKey(device);
				}
			}
// * このデバイスを参照しているデバイスがあれば、ロックをかける
		Ph2DevicesEnyityExample exm = new Ph2DevicesEnyityExample();
		exm.createCriteria().andPreviousDeviceIdEqualTo(device.getId());
		List<Ph2DevicesEnyity> devices = this.ph2DevicesMapper.selectByExample(exm);
		for (Ph2DevicesEnyity ref : devices)
			{
			if (!this.setDataOnLoad(ref))
				{
				new RuntimeException(ONLOAD_ERROR);
				}
			rockList.add(ref.getId());
			}
		return rockList;
		}
	// --------------------------------------------------
	/**
	 * 全データ更新要求を解除する
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void setUpdateNotAll(Ph2DevicesEnyity device)
		{
		Integer status = (device.getDataStatus().intValue() | ALL_LOAD_NEEDED) - ALL_LOAD_NEEDED;
		device.setDataStatus(status);
		this.update(device, false);
		}
	// --------------------------------------------------
	/**
	 * 指定されたデバイスリストに対してロックを解除する 
	 * @param devices
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void unLockDevices(List<Long> devices)
		{
		this.ph2DevicesMapper.unsetAllStatus(ON_LOADING, devices);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスリストに対してバッチ実行時に全更新をさせるモードを設定する
	 * @param devices
	 */
	// --------------------------------------------------
	public void prepareForAllUpdate(List<Long> devices)
		{
		this.ph2DevicesMapper.updateAllStatus(ALL_LOAD_NEEDED, devices);
		}
	// ===============================================
	// 保護関数群
	// ===============================================
	public void setDataStatus(Ph2DevicesEnyity device, Integer status, boolean dataUpdated)
		{
		int orginal = device.getDataStatus();
		orginal = (orginal | status);
		device.setDataStatus(orginal);
		this.update(device, dataUpdated);
		}
	public void update(Ph2DevicesEnyity device, boolean dataUpdated)
		{
		device.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		if(dataUpdated) device.setDataStatusDate(device.getUpdatedAt());
		this.ph2DevicesMapper.updateByPrimaryKeySelective(device);
		}	
	}
