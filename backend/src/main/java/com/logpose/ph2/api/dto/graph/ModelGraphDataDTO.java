package com.logpose.ph2.api.dto.graph;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dto.EventDaysDTO;
import com.logpose.ph2.api.master.DeviceDayMaster;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 〇〇〇グラフデータ取得
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ModelGraphDataDTO extends GraphDataDTO
	{
	// * 去年のデータをベースとしたリスト
	private List<List<Double>> OtherValues = new ArrayList<>();
	// * 実測値のリスト
	private List<Double> meauredValues = new ArrayList<>();
	// * グラフのアノテーションデータ
	private List<EventDaysDTO> annotations;
	// * カテゴリー
	private List<String> category = new ArrayList<>();
	// * コメント
	private String comment;
	// * 本日の推定値
	private Double estimated;
	// * 実測値データの有無
	private boolean hasMeasured = false;

	public ModelGraphDataDTO()
		{
		super();
		for (int i = 0; i < 3; i++)
			{
			OtherValues.add(new ArrayList<>());
			}
		}

	public void add(Short type, Double value)
		{
		List<Double> target = null;
		if (null != type)
			{
			short typeValue = type.shortValue();
			if (typeValue == DeviceDayMaster.ORIGINAL)
				{
				this.getValues().add(value);
				this.meauredValues.add(null);
				}
			else if (typeValue == DeviceDayMaster.MEASURED)
				{
				this.getValues().add(null);
				this.meauredValues.add(value);
				this.hasMeasured = true;
				}
			else
				{
				this.getValues().add(null);
				this.meauredValues.add(null);

				if (typeValue == DeviceDayMaster.PREVIOUS_DEVICE)
					{
					target = this.OtherValues.get(0);
					}
				else if (typeValue == DeviceDayMaster.PREVIOUS_YEAR)
					{
					target = this.OtherValues.get(1);
					}
				else if (typeValue == DeviceDayMaster.WHEATHER)
					{
					target = this.OtherValues.get(2);
					}
				target.add(value);
				}
			}
		else
			{
			this.getValues().add(null);
			this.meauredValues.add(null);
			}
		for (final List<Double> listItem : this.OtherValues)
			{
			if (target != listItem)
				{
				listItem.add(null);
				}
			}
		}
	}
