package com.logpose.ph2.api.domain.growth;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.configration.DefaultFtageValues;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetGrowthEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.dao.db.entity.joined.ModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealGrowthFStageMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.GrowthDomainMapper;
import com.logpose.ph2.api.domain.DeviceDayDomain;
import com.logpose.ph2.api.domain.GraphDomain;
import com.logpose.ph2.api.domain.ModelAndDailyDataDomain;
import com.logpose.ph2.api.domain.common.MaxValue;
import com.logpose.ph2.api.dto.EventDaysDTO;
import com.logpose.ph2.api.dto.FDataListDTO;
import com.logpose.ph2.api.dto.ValueDateDTO;
import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;

@Component
public class GrowthDomain extends GraphDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DefaultFtageValues fstageValues;
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	@Autowired
	private Ph2RealGrowthFStageMapper ph2RealGrowthFStageMapper;
	@Autowired
	private GrowthParameterDomain growthParameterDomain;
	@Autowired
	private GrowthDomainMapper growthDomainMapper;
	@Autowired
	private DeviceDayDomain deviceDayDomain;
	@Autowired
	private ModelAndDailyDataDomain modelDataDomain;

	// ===============================================
	// 公開関数群
	// ===============================================

	// --------------------------------------------------
	/**
	 * 対象デバイスの対象年度のFデータを取得する。
	 * 存在しない場合はデフォルト値を設定して返却
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return List<Ph2RealGrowthFStageEntity>
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public List<Ph2RealGrowthFStageEntity> getFStageData(Long deviceId,
			Short year)
		{
		// * 検索条件の設定
		Ph2RealGrowthFStageEntityExample exm = new Ph2RealGrowthFStageEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year);
		exm.setOrderByClause("stage_start asc");
		// * 検索実行
		List<Ph2RealGrowthFStageEntity> records = this.ph2RealGrowthFStageMapper
				.selectByExample(exm);
		// * 該当が無い場合
		if (0 == records.size())
			{
			int sum = this.fstageValues.getStart().size();
			for (int i = 0; i < sum; i++)
				{
				// * デフォルトのFStageデータを作成し、DBに登録する。
				Ph2RealGrowthFStageEntity entity = new Ph2RealGrowthFStageEntity();
				entity.setDeviceId(deviceId);
				entity.setYear(year);
				entity.setAccumulatedF(this.fstageValues.getSig().get(i));
				entity.setIntervalF(this.fstageValues.getInterval().get(i));
				entity.setStageStart(this.fstageValues.getStart().get(i));
				entity.setStageEnd(this.fstageValues.getEnd().get(i));
				entity.setStageName(this.fstageValues.getName().get(i));
				entity.setColor(this.fstageValues.getColors().get(i));
				entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
				entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				long id = this.ph2RealGrowthFStageMapper.insert(entity);
				entity.setId(id);// TODO 必要か？
				records.add(entity);
				}
			return records;
			}
		else
			return records;
		}

	// --------------------------------------------------
	/**
	 * デバイスのモデルテーブルを更新する
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param startDate 統計対象開始日
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public void updateModelTable(Long deviceId, Short year)
			throws ParseException
		{
// * 統計対象開始日から存在しているDailyBaseDataの気温情報を取得
		List<ModelAndDailyDataEntity> realDayData = this.modelDataDomain.get(deviceId, year);

// * 日ごとデータがある場合
		if (0 != realDayData.size())
			{
// * デバイスID、年度からパラメータセットを取得
			Ph2ParamsetGrowthEntity param = this.growthParameterDomain.getParmaters(deviceId, year);

// * デバイスID、年度からFStage情報を取得
			List<Ph2RealGrowthFStageEntity> fstageInfo = this.getFStageData(deviceId, year);

// * モデルデータの更新
			new GrowthModelDataGenerator(realDayData, param, fstageInfo, this.fstageValues.getSprout());

// * DBへの更新を行う
			this.modelDataDomain.upate(realDayData);
			}
		}

	// --------------------------------------------------
	/**
	 * 生育推定モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return ModelGraphDataDTO
	 */
	// --------------------------------------------------
	public ModelGraphDataDTO getModelGraph(Long deviceId, Short year)
		{
		List<ModelDataEntity> entites = this.ph2ModelDataMapper
				.selectModelDataByType(deviceId, year);
// * データが存在しない場合 nullを返す
		if (0 == entites.size()) return null;
		if (null == entites.get(0).getfValue()) return null;

		ModelGraphDataDTO resultData = new ModelGraphDataDTO();

		List<Double> values = new ArrayList<>();
		List<Double> predictValues = new ArrayList<>();
// * 最大値
		MaxValue maxValue = new MaxValue();
// * 前日の日付
		Calendar cal = Calendar.getInstance();
		Date titlleDate = new DeviceDayAlgorithm().getPreviousDay();
// * 日付カテゴリ
		List<String> category = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		boolean prev = entites.get(0).getIsReal();
		for (ModelDataEntity entity : entites)
			{
			if (entity.getIsReal())
				{
				values.add(entity.getfValue());
				if (!prev)
					{
					predictValues.add(entity.getfValue());
					prev = true;
					}
				else
					predictValues.add(null);
				}
			else
				{
				predictValues.add(entity.getfValue());
				if (prev)
					{
					values.add(entity.getfValue());
					prev = false;
					}
				else
					values.add(null);
				}
			// * 取得日
			category.add(sdf.format(entity.getDate()));
			// * 最大値
			maxValue.setMax(entity.getfValue());
			// * タイトルに表示する値
			if (entity.getDate().getTime() == titlleDate.getTime())
				{
				resultData.setEstimated(entity.getfValue());
				}
			}
// * 値の設定
		resultData.setValues(values);
		resultData.setPredictValues(predictValues);
		resultData.setCategory(category);
// * アノテーションデータの生成
		List<EventDaysDTO> annotations = super.getEvent(deviceId, year);
		resultData.setAnnotations(annotations);
// * 最小値・最大値の設定
		String first = category.get(0);
		String last = category.get(category.size() - 1);
		resultData.setXStart(first);
		resultData.setXEnd(last);

		maxValue.setMax(annotations.get(annotations.size() - 1).getValue() + 10);
		resultData.setYStart((double) 0);
		resultData.setYEnd(maxValue.getMax());
// * コメント
		super.setComment(deviceId, year, resultData);
// * グラフデータの返却
		return resultData;
		}

	// --------------------------------------------------
	/**
	 * 生育推定F値データ取得
	 *
	 * @param deviceId デバイスID
	 * @param year  年度
	 * @return List<Ph2RealGrowthFStageEntity>
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public List<Ph2RealGrowthFStageEntity> getALlFValus(Long deviceId,
			Short year)
		{
		Ph2RealGrowthFStageEntityExample exm = new Ph2RealGrowthFStageEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year);
		exm.setOrderByClause("stage_start asc");
		List<Ph2RealGrowthFStageEntity> result = this.ph2RealGrowthFStageMapper
				.selectByExample(exm);
		for (final Ph2RealGrowthFStageEntity item : result)
			{
			if (null != item.getActualDate())
				{
				if (item.getActualDate().getTime() < 0)
					{
					item.setActualDate(null);
					}
				}
			}
		return result;
		}

	// --------------------------------------------------
	/**
	 * 生育推定実績値取得
	 *
	 * @param deviceId デバイスID
	 * @param date 日付
	 * @return 実績F値
	 */
	// --------------------------------------------------
	public ValueDateDTO getRealFData(Long deviceId, Date date)
		{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		new DeviceDayAlgorithm().setTimeZero(cal);
		// * モデルテーブルから該当する日付にもっとも近い過去の実績値を取得する
		ValueDateDTO value = this.ph2ModelDataMapper.selectFValueByDate(deviceId, cal.getTime());
		if (null == value)
			{
			throw new RuntimeException("実績データはまだありません。");
			}
		return value;
		}

	// --------------------------------------------------
	/**
	 * 生育推定実績値更新
	 *
	 * @param dto FDataListDTO
	 */
	// --------------------------------------------------
	public void updateFValues(FDataListDTO dto)
		{
		for (Ph2RealGrowthFStageEntity item : dto.getList())
			{
			// * IDがある場合
			if (null != item.getId())
				{
				// * 該当のFstageデータを取得する
				Ph2RealGrowthFStageEntity entity = this.ph2RealGrowthFStageMapper
						.selectByPrimaryKey(item.getId());
				entity.setAccumulatedF(item.getAccumulatedF());
				entity.setIntervalF(item.getIntervalF());
				entity.setStageStart(item.getStageStart());
				entity.setStageEnd(item.getStageEnd());
				entity.setStageName(item.getStageName());
				entity.setActualDate(item.getActualDate());
				entity.setEstimateDate(item.getEstimateDate());
				entity.setColor(item.getColor());
				entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				this.ph2RealGrowthFStageMapper.updateByPrimaryKey(entity);
				}
			else
				{
				item.setDeviceId(dto.getDeviceId());
				item.setYear(dto.getYear());
				item.setCreatedAt(new Timestamp(System.currentTimeMillis()));
				item.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				this.ph2RealGrowthFStageMapper.insert(item);
				}
			}
		}

	}
