package com.logpose.ph2.api.domain.photosynthesis;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DateTimeUtility;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealPsAmountMapper;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisValueDTO;

@Component
public class PhotoSynthesisDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2RealPsAmountMapper ph2RealPsAmountMapper;
	// ===============================================
	// 公開関数群(検索系)
	// ===============================================
	// ###############################################
	/**
	 * 光合成推定実績値取得
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 */
	// ###############################################
	public List<Ph2RealPsAmountEntity> getRealPsAmountEntity(Long devieId, Short year)
		{
// * 検索条件の設定
		Ph2RealPsAmountEntityExample exm = new Ph2RealPsAmountEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(devieId).andYearEqualTo(year);
		exm.setOrderByClause("date asc");

// * 問合せ実行
		return this.ph2RealPsAmountMapper.selectByExample(exm);
		}
	// ===============================================
	// 公開関数群(更新系)
	// ===============================================
	// ###############################################
	/**
	 * 光合成推定実績値更新
	 *
	 * @param records PhotosynthesisValueDTO 更新データリスト
	 * @return 更新の有無
	 * @throws ParseException 
	 */
	// ###############################################
	public boolean update(List<PhotosynthesisValueDTO> records) throws ParseException
		{
		Timestamp time = new Timestamp(System.currentTimeMillis());
		for (final PhotosynthesisValueDTO dto : records)
			{
			Date tmp = DateTimeUtility.getDateFromString(dto.getDate());
// * 該当するデバイスと日付を検索
			Ph2RealPsAmountEntity entity = this.ph2RealPsAmountMapper.selectByPrimaryKey(dto.getDeviceId(), tmp);
// * 存在しない場合、新たに作成
			if (null == entity)
				{
				entity = new Ph2RealPsAmountEntity();
				entity.setDeviceId(dto.getDeviceId());
				entity.setYear(dto.getYear());
				entity.setDate(tmp);
				}
// * データの作成
			entity.setUpdatedAt(time);
			entity.setValueF(dto.getF());
			entity.setValueG(dto.getG());
// * データの更新時
			if (null != entity.getCreatedAt())
				{
				this.ph2RealPsAmountMapper.updateByPrimaryKey(entity);
				}
			else
				{
				entity.setCreatedAt(time);
				this.ph2RealPsAmountMapper.insert(entity);
				}
			}
// * 古いデータの削除
		if (records.size() > 0)
			{
			Ph2RealPsAmountEntityExample exm = new Ph2RealPsAmountEntityExample();
			exm.createCriteria()
					.andDeviceIdEqualTo(records.get(0).getDeviceId())
					.andYearEqualTo(records.get(0).getYear())
					.andUpdatedAtLessThan(time);
			this.ph2RealPsAmountMapper.deleteByExample(exm);
			return true;
			}
		else
			{
			return false;
			}
		}
	}
