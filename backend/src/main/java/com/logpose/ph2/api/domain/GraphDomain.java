package com.logpose.ph2.api.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.FDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealGrowthFStageMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.GrowthDomainMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2ParamSetJoinMapper;
import com.logpose.ph2.api.dto.EventDaysDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;

public class GraphDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2ParamSetJoinMapper ph2ParamSetJoinMapper;
	@Autowired
	private Ph2RealGrowthFStageMapper ph2RealGrowthFStageMapper;
	@Autowired
	private GrowthDomainMapper growthDomainMapper;
	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
		/**
		 * 生育推定イベントデータ取得
		 *
		 * @param deviceId-デバイスID
		 * @param year-年度
		 * @return イベントと日付のリスト
		 */
		// --------------------------------------------------
		public List<EventDaysDTO> getEvent(Long deviceId, Short year)
			{
			List<EventDaysDTO> resultData = new ArrayList<>();
// * Fステージデータの取得
// * 検索条件の設定
			Ph2RealGrowthFStageEntityExample exm = new Ph2RealGrowthFStageEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year);
			exm.setOrderByClause("stage_start asc");
// * Fステージデータの取得
			List<Ph2RealGrowthFStageEntity> fstages = this.ph2RealGrowthFStageMapper
					.selectByExample(exm);
// *モデルテーブルからFデータの取得
			List<FDataEntity> values = this.growthDomainMapper
					.selectValueAndDays(deviceId, year);
// * Fステージデータの累積値に達しているモデルデータの日付を戻り値のオブジェクトに追加する
			int index = 0;
			for (Ph2RealGrowthFStageEntity item : fstages)
				{
				// 累積F値
				Double sigF = item.getAccumulatedF();
				// F値に達する該当日があるかどうかのフラグ
				boolean hasDate = false;
				for (; index < values.size(); index++)
					{
					final FDataEntity value = values.get(index);
					// Fデータが無ければ終了
					if(null == value.getValue()) break;
					if (value.getValue().doubleValue() > sigF.doubleValue())
						{
						EventDaysDTO eventDay = new EventDaysDTO();
						eventDay.setName(item.getStageName());
						eventDay.setDate(value.getDate());
						eventDay.setValue(value.getValue());
						eventDay.setColor(item.getColor());
						resultData.add(eventDay);
						hasDate = true;
						break;
						}
					}
// * 該当日付が無い場合、空のデータを追加する
				if( !hasDate)
					{
					EventDaysDTO eventDay = new EventDaysDTO();
					eventDay.setName(item.getStageName());
					eventDay.setDate(null);
					eventDay.setValue(item.getAccumulatedF());
					eventDay.setColor(item.getColor());
					resultData.add(eventDay);
					}
				}
			return resultData;
			}
	// --------------------------------------------------
	/**
	 * グラフにコメントを付与する
	 * 
	 * @param parameterId
	 * @param dto
	 */
	// --------------------------------------------------
	public void setComment(Long device, Short year, RealModelGraphDataDTO dto)
		{
		List<String> comments = this.ph2ParamSetJoinMapper.selectLatestComment(device, year);
		if (comments.size() == 0)
			{
			dto.setComment("");
			}
		else
			{
			dto.setComment(comments.get(0));
			}
		}
	}
