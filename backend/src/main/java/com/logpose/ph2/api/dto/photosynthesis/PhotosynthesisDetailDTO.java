package com.logpose.ph2.api.dto.photosynthesis;

import java.util.List;

import lombok.Data;

/**
 * 光合成推定実績値詳細
 *
 */
@Data
public class PhotosynthesisDetailDTO
	{
	private String maxDate;
	private String minDate;
	private List<PhotosynthesisValueDTO> values;
	}
