package com.logpose.ph2.api.domain.growth;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DateTimeUtility;
import com.logpose.ph2.api.configration.DefaultFtageValues;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealGrowthFStageMapper;
import com.logpose.ph2.api.dto.ValueDateDTO;
import com.logpose.ph2.api.dto.growth.FValuesDTO;

@Component
public class FValueDomain
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

	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * Ph2RealGrowthFStageの推定値の設定
	 * @param deviceId
	 * @param year
	 * @throws ParseException 
	 */
	// ###############################################
	public void resetActualDate(Long deviceId, Short year) throws ParseException
		{
// * 生育推定のF値実績テーブルから該当デバイスと年度のレコードを取得する	
		List<Ph2RealGrowthFStageEntity> fstages = this.ph2RealGrowthFStageMapper.selectByDeviceAndYear(deviceId, year);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

// * モデルデータテーブルから該当デバイスと年度のレコードを取得する	
		List<ValueDateDTO> allData = this.ph2ModelDataMapper.selectFValueByYear(deviceId, year);
		int index = 0;
		for (Ph2RealGrowthFStageEntity entity : fstages)
			{
			// * 実測値または推定値が無い場合
			if ((null == entity.getEstimateDate()) || (null == entity.getActualDate()))
				{
				// * F値リストから該当日を探す
				for (; index < allData.size(); index++)
					{
					ValueDateDTO value = allData.get(index);
					// * F値が超えた場合
					if (value.getValue() >= entity.getAccumulatedF())
						{
						Date date = sdf.parse(value.getDate());
						// * 見積値を訂正する
						entity.setEstimateDate(date);
						this.ph2RealGrowthFStageMapper.updateByPrimaryKey(entity);
						index++;
						break;
						}
					}
				}
			}
		}

	// ###############################################
	/**
	 * 日付からF値の情報を得る
	 *
	 * @param id　F値ID
	 * @param date
	 * @throws ParseException 
	 */
	// ###############################################
	public FValuesDTO checkFValueByDate(Long id, String dateString) throws ParseException
		{
		Date date = DateTimeUtility.getDateFromString(dateString);
		FValuesDTO result = new FValuesDTO();

		Ph2RealGrowthFStageEntity entity = this.ph2RealGrowthFStageMapper.selectByPrimaryKey(id);
// * 指定デバイスの年度からF値のリストを得る
		ValueDateDTO value = this.ph2ModelDataMapper.selectFValueBySpecificDate(entity.getDeviceId(), entity.getYear(),
				date);
		if (null == value)
			{
			throw new RuntimeException("該当する日付にデータはありません。");
			}
		result.setAccumulated(value.getValue());
		double diff = value.getValue().doubleValue() - entity.getAccumulatedF().doubleValue();
		double interval = entity.getIntervalF().doubleValue() + diff;
		if (interval < 0)
			{
			throw new RuntimeException("適切なインターバルではありません。");
			}
		result.setInterval(interval);

		return result;
		}

	// ###############################################
	/**
	 * 対象デバイスの対象年度のFデータを取得する。
	 * 存在しない場合はデフォルト値を設定して返却
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return List<Ph2RealGrowthFStageEntity>
	 * @throws ParseException 
	 */
	// ###############################################
	public double getSproutFValue(Long deviceId, Short year)
		{
		double value = 0;
		// * 検索条件の設定
		Ph2RealGrowthFStageEntityExample exm = new Ph2RealGrowthFStageEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year).andStageEndEqualTo((short) 4);
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
				this.ph2RealGrowthFStageMapper.insert(entity);
				if(entity.getStageEnd() == 4) value = entity.getAccumulatedF();
				}
			}
		else value = records.get(0).getAccumulatedF();
		return value;
		}

	}
