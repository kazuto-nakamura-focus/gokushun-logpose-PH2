package com.logpose.ph2.api.bulk.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DevicesMapper;
import com.logpose.ph2.api.dto.element.ObjectStatus;

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
	 * @return List<Ph2DevicesEntity>
	 */
	// --------------------------------------------------
	public List<Ph2DevicesEntity> selectAll()
		{
		return this.ph2DevicesMapper.selectAll(null);
		}

	// --------------------------------------------------
	/**
	 * オブジェクトステータスリストを取得する
	 * @return List<Ph2DevicesEntity>
	 */
	// --------------------------------------------------
	public List<ObjectStatus> getStatusList(List<Ph2DevicesEntity> devices)
		{
		List<Long> idList = new ArrayList<>();
		for (final Ph2DevicesEntity device : devices)
			{
			idList.add(device.getId());
			}
		return this.ph2DevicesMapper.selectDeviceStatus(idList);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスが全データロードかどうかチェックする
	 * @return boolean
	 */
	// --------------------------------------------------
	public boolean isAll(Ph2DevicesEntity device)
		{
		return (device.getDataStatus() & ALL_LOAD_NEEDED) > 0;
		}

	// --------------------------------------------------
	/**
	 *全デバイスに対して全データロードモードを設定する
	 * @return boolean
	 */
	// --------------------------------------------------
	public void setAllStatusToAllLoaded()
		{
		this.ph2DevicesMapper.setAllStatus(ALL_LOAD_NEEDED);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対してローディング中であることを設定する
	 * @param device
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public boolean setDataOnLoad(Ph2DevicesEntity device)
		{
// * 最新の情報を得る
		Ph2DevicesEntity entity = this.ph2DevicesMapper.selectByPrimaryKey(device.getId());
		int prevStatus = (null != entity.getDataStatus()) ? entity.getDataStatus().intValue() : 0;
		int newStatus = 0;
// * ロックされていない場合
		if ((prevStatus & ON_LOADING) == 0)
			{
			// ロック
			newStatus = ON_LOADING;
			// 全てのロード要求がある場合、そのステータスを保持
			if ((prevStatus & ALL_LOAD_NEEDED) > 0)
				{
				newStatus = newStatus | ALL_LOAD_NEEDED;
				}
			// 通常のロードの場合モデル情報を保持
			else if ((prevStatus & MODEL_DATA_CREATED) > 0)
				{
				newStatus = newStatus | MODEL_DATA_CREATED;
				}
			device.setDataStatus(newStatus);
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
	public void setDataNotLoading(Ph2DevicesEntity device)
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
	public void setDataInitialized(Ph2DevicesEntity device)
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
	public void setRawDataLoaded(Ph2DevicesEntity device)
		{
		this.setDataStatus(device, RAW_DATA_LOADED, true);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対してモデルデータが作成済を解除する
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void setUpdateNotModel(Ph2DevicesEntity device)
		{
		Integer status = (device.getDataStatus().intValue() | MODEL_DATA_CREATED) - MODEL_DATA_CREATED;
		device.setDataStatus(status);
		this.update(device, false);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対してモデルデータが作成済であることを通知する
	 * @param device
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void setModelDataCreated(Ph2DevicesEntity device)
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
	public List<Long> lockDevices(Ph2DevicesEntity device)
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
			Ph2DevicesEntity tmp = this.ph2DevicesMapper.selectByPrimaryKey(device.getPreviousDeviceId());
			if (null != tmp)
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
		Ph2DevicesEntityExample exm = new Ph2DevicesEntityExample();
		exm.createCriteria().andPreviousDeviceIdEqualTo(device.getId());
		List<Ph2DevicesEntity> devices = this.ph2DevicesMapper.selectByExample(exm);
		for (Ph2DevicesEntity ref : devices)
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
	 * デバイスリストに対してロックを試みる
	 * @param device
	 * @return ロックされたデバイスのIDリスト
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public List<Long> lockDevices(List<Ph2DevicesEntity> devices)
		{
		List<Long> rockList = new ArrayList<>();
// * デバイスに対してロックを試みる
		for (Ph2DevicesEntity device : devices)
			{
			if (!this.setDataOnLoad(device))
				{
				new RuntimeException(ONLOAD_ERROR);
				}
// * ロックリストに追加する
			rockList.add(device.getId());
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
	public void setUpdateNotAll(Ph2DevicesEntity device)
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

	// --------------------------------------------------------
	/**
	 * デバイスのロード情報を得る
	 * @param date 取得するステータスデータの日付
	 * @return List<ObjectStatus>
	 */
	// --------------------------------------------------------
	public List<ObjectStatus> getAllStatusList(Date date)
		{
		return this.ph2DevicesMapper.selectAllStatus(date);
		}

	// ===============================================
	// 保護関数群
	// ===============================================
	public void setDataStatus(Ph2DevicesEntity device, Integer status, boolean dataUpdated)
		{
		int orginal = device.getDataStatus();
		orginal = (orginal | status);
		device.setDataStatus(orginal);
		this.update(device, dataUpdated);
		}

	public void update(Ph2DevicesEntity device, boolean dataUpdated)
		{
		device.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		if (dataUpdated) device.setDataStatusDate(device.getUpdatedAt());
		this.ph2DevicesMapper.updateByPrimaryKeySelective(device);
		}
	}
